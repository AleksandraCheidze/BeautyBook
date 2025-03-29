package com.example.end.service;

import com.example.end.dto.*;
import com.example.end.infrastructure.config.ImageUploadService;
import com.example.end.infrastructure.exceptions.*;
import com.example.end.infrastructure.mail.ProjectMailSender;
import com.example.end.mapping.UserMapper;
import com.example.end.models.*;
import com.example.end.repository.CategoryRepository;
import com.example.end.repository.PortfolioPhotoRepository;
import com.example.end.repository.UserRepository;
import com.example.end.infrastructure.security.sec_servivce.TokenService;
import com.example.end.service.interfaces.CategoryService;
import com.example.end.service.interfaces.UserService;
import com.example.end.utils.FileValidationUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * Implementation of {@link UserService}.
 * Provides methods for user registration, authentication, and management,
 * including updating user details, adding images, and fetching users by
 * category or role.
 */
@RequiredArgsConstructor
@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final UserMapper userMapper;
    private final CategoryService categoryService;
    private final PasswordEncoder passwordEncoder;
    private final ProjectMailSender mailSender;
    private final TokenService tokenService;
    private final SenderService senderService;
    private final PortfolioPhotoRepository portfolioPhotoRepository;
    private final ImageUploadService imageUploadService;

    @Value("${SPRING_MAIL_USERNAME}")
    private String adminEmail;

    /**
     * Registers a new user with the provided details.
     * If the user is a master, sends a confirmation email and sets the user as
     * inactive.
     * If the user is a client, sends a registration email and sets the user as
     * active.
     * Generates access and refresh tokens for the user after registration.
     *
     * @param newUserDto the new user details
     * @return the registered user with access and refresh tokens
     */
    @Override
    @Transactional
    @CacheEvict(value = { "allUsers", "mastersByCategory", "allMasters" }, allEntries = true)
    public UserDto register(NewUserDto newUserDto) {
        validateEmail(newUserDto.getEmail());

        User user = newUserDto.createUser();
        user.setHashPassword(passwordEncoder.encode(newUserDto.getHashPassword()));

        if (user.getRole() == User.Role.MASTER) {
            senderService.sendMasterRegistrationConfirmation(user);
            user.setActive(false);
        } else {
            mailSender.sendRegistrationEmail(user.getEmail());
            user.setActive(true);
        }

        User savedUser = userRepository.save(user);

        String accessToken = tokenService.generateAccessToken(savedUser);
        String refreshToken = tokenService.generateRefreshToken(savedUser);

        UserDto userDto = userMapper.toDto(savedUser);
        userDto.setAccessToken(accessToken);
        userDto.setRefreshToken(refreshToken);

        return userDto;
    }

    /**
     * Authenticates a user by validating their email and password.
     *
     * @param email the user's email
     * @param password the user's password
     * @return the authenticated user
     * @throws RestException if authentication fails
     */
    @Override
    public UserDto authenticate(String email, String password) {
        User user = findUserByEmailOrThrow(email);

        if (!passwordEncoder.matches(password, user.getHashPassword())) {
            throw new RestException(HttpStatus.UNAUTHORIZED, "Invalid password");
        }

        return userMapper.toDto(user);
    }

    /**
     * Retrieves user details by user ID.
     *
     * @param id the user ID
     * @return user details
     * @throws ResourceNotFoundException if the user is not found
     */
    @Override
    @Cacheable(value = "userDetails", key = "#id")
    public UserDetailsDto getById(Long id) {
        User user = findUserByIdOrThrow(id);
        return userMapper.userDetailsToDto(user);
    }

    /**
     * Validates if a user already exists with the given email.
     *
     * @param email the email to validate
     * @throws RestException if the email is already in use
     */
    @Override
    public void validateEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new RestException(HttpStatus.CONFLICT, "User with email <" + email + "> already exists");
        }
    }

    /**
     * Updates user details such as description, phone number, address, categories,
     * and procedures.
     *
     * @param userId the user ID
     * @param userDetailsDto the new user details
     * @return the updated user details
     * @throws ResourceNotFoundException if the user is not found
     */
    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "userDetails", key = "#userId"),
            @CacheEvict(value = { "allUsers", "mastersByCategory", "allMasters" }, allEntries = true)
    })
    public UserDetailsDto updateUserDetails(Long userId, NewUserDetailsDto userDetailsDto) {
        User user = findUserByIdOrThrow(userId);

        user.setDescription(userDetailsDto.getDescription());
        user.setPhoneNumber(userDetailsDto.getPhoneNumber());
        user.setAddress(userDetailsDto.getAddress());

        Set<Category> selectedCategories = new HashSet<>(
                categoryRepository.findAllById(userDetailsDto.getCategoryIds()));
        user.setCategories(selectedCategories);

        Set<Procedure> selectedProcedures = categoryService.getProceduresForCategories(
                selectedCategories, userDetailsDto.getProcedureIds());
        user.setProcedures(selectedProcedures);

        User updatedUser = userRepository.save(user);

        UserDetailsDto responseDto = userMapper.userDetailsToDto(updatedUser);
        responseDto.setCategoryIds(updatedUser.getCategories().stream()
                .map(Category::getId)
                .collect(Collectors.toList()));
        responseDto.setProcedureIds(updatedUser.getProcedures().stream()
                .map(Procedure::getId)
                .collect(Collectors.toList()));

        return responseDto;
    }

    /**
     * Retrieves a master by their user ID.
     *
     * @param id the user ID
     * @return the master user
     * @throws ResourceNotFoundException if the user is not found
     */
    @Override
    @Cacheable(value = "masterById", key = "#id")
    public UserDto getMasterById(Long id) {
        return getUserByIdAndRole(id, User.Role.MASTER);
    }

    /**
     * Retrieves a client by their user ID.
     *
     * @param id the user ID
     * @return the client user
     * @throws ResourceNotFoundException if the user is not found
     */
    @Override
    @Cacheable(value = "clientById", key = "#id")
    public UserDto getClientById(Long id) {
        return getUserByIdAndRole(id, User.Role.CLIENT);
    }

    /**
     * Confirms a master user's registration via email.
     *
     * @param email the email of the master user
     * @throws IllegalStateException if the master user is already active
     */
    @Override
    @CacheEvict(value = { "allUsers", "mastersByCategory", "allMasters" }, allEntries = true)
    public void confirmMasterByEmail(String email) {
        User masterUser = findMasterUserByEmail(email);
        masterUser.setActive(true);
        userRepository.save(masterUser);
        mailSender.sendRegistrationEmail(masterUser.getEmail());
    }

    @Override
    @Transactional
    @CacheEvict(value = { "userDetails", "masterById" }, key = "#userId")
    public String uploadProfilePhoto(Long userId, MultipartFile file, long maxSize) {
        String validationError = FileValidationUtils.validateImage(file);

        if (validationError != null) {
            throw new InvalidFileException(validationError);
        }

        if (file.getSize() > maxSize) {
            throw new InvalidFileException("File size exceeds maximum allowed (" +
                    maxSize / (1024 * 1024) + "MB)");
        }

        User user = findUserByIdOrThrow(userId);

        try {
            String imageUrl = imageUploadService.uploadImage(file);
            user.setProfilePhotoUrl(imageUrl);
            userRepository.save(user);
            return imageUrl;
        } catch (Exception e) {
            throw new ImageUploadException("Error during image upload.", e);
        }
    }

    @Override
    @Transactional
    @CacheEvict(value = { "userDetails", "masterById" }, key = "#userId")
    public List<PortfolioImageDto> uploadPortfolioPhotos(Long userId, List<MultipartFile> files, long maxSize)
            throws IOException, ExecutionException, InterruptedException {
        for (MultipartFile file : files) {
            String validationError = FileValidationUtils.validateImage(file);
            if (validationError != null) {
                throw new InvalidFileException(validationError);
            }

            if (file.getSize() > maxSize) {
                throw new InvalidFileException(String.format(
                        "File '%s' size exceeds maximum allowed (%d MB)",
                        file.getOriginalFilename(), maxSize / (1024 * 1024)));
            }
        }

        User user = findUserByIdOrThrow(userId);
        List<PortfolioImageDto> uploadedPhotos = new ArrayList<>();

        for (MultipartFile file : files) {
            try {
                String imageUrl = imageUploadService.uploadImage(file);
                PortfolioPhoto portfolioPhoto = new PortfolioPhoto();
                portfolioPhoto.setUrl(imageUrl);
                portfolioPhoto.setUser(user);

                PortfolioPhoto savedPhoto = portfolioPhotoRepository.save(portfolioPhoto);
                uploadedPhotos.add(PortfolioImageDto.builder()
                        .id(savedPhoto.getId())
                        .url(savedPhoto.getUrl())
                        .build());
            } catch (Exception e) {
                throw new ImageUploadException("Error uploading image for user " + userId, e);
            }
        }

        return uploadedPhotos;
    }

    @Override
    @Transactional
    @CacheEvict(value = { "userDetails", "masterById" }, key = "#userId")
    public void deleteProfilePhoto(Long userId) {
        User user = findUserByIdOrThrow(userId);
        String profilePhotoUrl = user.getProfilePhotoUrl();

        if (profilePhotoUrl != null) {
            try {
                String publicId = imageUploadService.extractPublicId(profilePhotoUrl);
                if (imageUploadService.exists(publicId)) {
                    imageUploadService.deleteImage(publicId);
                    user.setProfilePhotoUrl(null);
                    userRepository.save(user);
                } else {
                    throw new ImageNotFoundException("Profile photo not found for deletion.");
                }
            } catch (Exception e) {
                throw new ImageUploadException("Error during profile photo deletion.", e);
            }
        } else {
        }
    }

    @Override
    @Transactional
    @CacheEvict(value = { "userDetails", "masterById" }, key = "#userId")
    public void deletePortfolioPhoto(Long userId, Long photoId)  {
        User user = findUserByIdOrThrow(userId);

        PortfolioPhoto photo = portfolioPhotoRepository.findById(photoId)
                .orElseThrow(() -> new PhotoNotFoundException("Photo with ID " + photoId + " not found."));

        if (!photo.getUser().getId().equals(user.getId())) {
            throw new PhotoOwnershipException("Photo " + photoId + " does not belong to user " + userId);
        }

        try {
            String publicId = imageUploadService.extractPublicId(photo.getUrl());
            imageUploadService.deleteImage(publicId);
            portfolioPhotoRepository.delete(photo);
        } catch (Exception e) {
            throw new ImageDeleteException("Error deleting portfolio photo with ID " + photoId, e);
        }
    }

    @Override
    @Transactional
    public String getProfilePhoto(Long userId) {
        User user = findUserByIdOrThrow(userId);
        String profilePhotoUrl = user.getProfilePhotoUrl();

        if (profilePhotoUrl == null) {
            return "No profile photo found";
        }

        try {
            String publicId = imageUploadService.extractPublicId(profilePhotoUrl);
            if (imageUploadService.exists(publicId)) {
                return profilePhotoUrl;
            } else {
                throw new ImageNotFoundException("Profile photo not found");
            }
        } catch (Exception e) {
            throw new ImageUploadException("Error retrieving profile photo", e);
        }
    }

    @Override
    @Transactional
    public String getPortfolioPhoto(Long userId, Long photoId) {
        User user = findUserByIdOrThrow(userId);

        PortfolioPhoto photo = portfolioPhotoRepository.findById(photoId)
                .orElseThrow(() -> new PhotoNotFoundException("Photo with ID " + photoId + " not found."));

        if (!photo.getUser().getId().equals(user.getId())) {
            throw new PhotoOwnershipException("Photo " + photoId + " does not belong to user " + userId);
        }

        try {
            String publicId = imageUploadService.extractPublicId(photo.getUrl());
            if (imageUploadService.exists(publicId)) {
                return photo.getUrl();
            } else {
                throw new ImageNotFoundException("Portfolio photo not found");
            }
        } catch (Exception e) {
            throw new ImageUploadException("Error retrieving portfolio photo", e);
        }
    }

    @Override
    @Transactional
    public List<PortfolioImageDto> getAllPortfolioPhotos(Long userId) {
        User user = findUserByIdOrThrow(userId);
        List<PortfolioPhoto> photos = portfolioPhotoRepository.findByUserId(userId);

        if (photos.isEmpty()) {
            return Collections.emptyList();
        }

        List<PortfolioImageDto> portfolioPhotos = new ArrayList<>();

        for (PortfolioPhoto photo : photos) {
            try {
                String publicId = imageUploadService.extractPublicId(photo.getUrl());
                if (imageUploadService.exists(publicId)) {
                    portfolioPhotos.add(PortfolioImageDto.builder()
                            .id(photo.getId())
                            .url(photo.getUrl())
                            .build());
                } else {
                    log.warn("Portfolio photo with ID {} not found in storage", photo.getId());
                    portfolioPhotoRepository.delete(photo);
                }
            } catch (Exception e) {
                log.error("Error checking portfolio photo with ID {}: {}", photo.getId(), e.getMessage());
                throw new ImageUploadException("Error retrieving portfolio photos", e);
            }
        }

        return portfolioPhotos;
    }

    /**
     * Retrieves all users with the MASTER role.
     *
     * @return a list of all master users
     */
    @Override
    @Cacheable(value = "allMasters")
    public List<UserDetailsDto> getAllMasters() {
        List<User> masters = userRepository.findAllByRole(User.Role.MASTER);
        return masters.stream()
                .map(userMapper::userDetailsToDto)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a user by their user ID.
     *
     * @param currentUserId the user ID
     * @return the user details
     * @throws ResourceNotFoundException if the user is not found
     */
    @Override
    @Cacheable(value = "userById", key = "#currentUserId")
    public UserDto getUserById(Long currentUserId) {
        User user = findUserByIdOrThrow(currentUserId);
        return userMapper.toDto(user);
    }

    /**
     * Retrieves all users.
     *
     * @return a list of all users
     */
    @Override
    @Cacheable(value = "allUsers")
    public List<UserDetailsDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::userDetailsToDto)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a master user by their email.
     *
     * @param email the email of the master user
     * @return the master user
     * @throws UserNotFoundException if the user is not found or not a master
     */
    @Override
    public User findMasterUserByEmail(String email) {
        User masterUser = findUserByEmailOrThrow(email);

        if (masterUser.getRole() != User.Role.MASTER) {
            throw UserNotFoundException.notMaster(email);
        }

        if (masterUser.isActive()) {
            throw UserNotFoundException.alreadyActive(email);
        }

        return masterUser;
    }

    /**
     * Retrieves all users associated with a specific category.
     *
     * @param categoryId the category ID
     * @return a list of users associated with the category
     * @throws ResourceNotFoundException if the category is not found
     */
    @Override
    @Cacheable(value = "mastersByCategory", key = "#categoryId")
    public List<UserDetailsDto> findUsersByCategoryId(Long categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new ResourceNotFoundException("Category with ID " + categoryId + " not found");
        }

        List<User> users = userRepository.findUsersByCategoryId(categoryId);
        return users.stream()
                .map(userMapper::userDetailsToDto)
                .collect(Collectors.toList());
    }

    /**
     * Finds a user by their email.
     *
     * @param email the user's email
     * @return an optional containing the user if found
     * @throws IllegalArgumentException if email is null or empty
     */
    @Override
    public Optional<User> findByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email must not be null or empty");
        }

        return userRepository.findByEmail(email);
    }

    /**
     * Deletes a user by their ID.
     *
     * @param id the user ID
     * @throws ResourceNotFoundException if the user is not found
     * @throws ForbiddenException if the current user doesn't have permission to delete the user
     */
    @Override
    @Caching(evict = {
            @CacheEvict(value = { "userDetails", "userById", "masterById", "clientById" }, key = "#id"),
            @CacheEvict(value = { "allUsers", "mastersByCategory", "allMasters" }, allEntries = true)
    })
    @Transactional
    public void deleteById(Long id) {
        User user = findUserByIdOrThrow(id);

        if (!isCurrentUserOrAdmin(user)) {
            throw new ForbiddenException("You don't have permission to delete this user");
        }

        userRepository.delete(user);
        log.info("User with id {} has been deleted", id);
    }

    // Helper methods

    private UserDto getUserByIdAndRole(Long userId, User.Role role) {
        User user = findUserByIdAndRole(userId, role);
        return userMapper.toDto(user);
    }

    private User findUserByIdOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));
    }

    private User findUserByEmailOrThrow(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", email));
    }

    private User findUserByIdAndRole(Long userId, User.Role role) {
        return userRepository.findByIdAndRole(userId, role)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));
    }

    private boolean isCurrentUserOrAdmin(User user) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        String currentUserEmail = authentication.getName();
        return user.getEmail().equals(currentUserEmail) ||
                authentication.getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }
}