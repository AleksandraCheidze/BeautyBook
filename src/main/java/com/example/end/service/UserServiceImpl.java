package com.example.end.service;

import com.example.end.dto.*;
import com.example.end.exceptions.*;
import com.example.end.infrastructure.config.ImageUploadService;
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
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of {@link UserService}.
 * Provides methods for user registration, authentication, and management,
 * including updating user details, adding images, and fetching users by category or role.
 */
@RequiredArgsConstructor
@Service
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

    @Value("${MAIL_ADMIN_EMAIL}")
    private String adminEmail;

    /**
     * Registers a new user with the provided details.
     * If the user is a master, sends a confirmation email and sets the user as inactive.
     * If the user is a client, sends a registration email and sets the user as active.
     * Generates access and refresh tokens for the user after registration.
     *
     * @param newUserDto the new user details
     * @return the registered user with access and refresh tokens
     */
    @Override
    @Transactional
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
     * @param email    the user's email
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
     * @throws UserNotFoundException if the user is not found
     */
    @Override
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
     * Updates user details such as description, phone number, address, categories, and procedures.
     *
     * @param userId         the user ID
     * @param userDetailsDto the new user details
     * @return the updated user details
     * @throws UserNotFoundException if the user is not found
     */
    @Override
    @Transactional
    public UserDetailsDto updateUserDetails(Long userId, NewUserDetailsDto userDetailsDto) {
        User user = findUserByIdOrThrow(userId);

        user.setDescription(userDetailsDto.getDescription());
        user.setPhoneNumber(userDetailsDto.getPhoneNumber());
        user.setAddress(userDetailsDto.getAddress());

        Set<Category> selectedCategories = new HashSet<>(categoryRepository.findAllById(userDetailsDto.getCategoryIds()));
        user.setCategories(selectedCategories);

        Set<Procedure> selectedProcedures = categoryService.getProceduresForCategories(selectedCategories, userDetailsDto.getProcedureIds());
        user.setProcedures(selectedProcedures);

        User updatedUser = userRepository.save(user);

        UserDetailsDto responseDto = userMapper.userDetailsToDto(updatedUser);
        responseDto.setCategoryIds(updatedUser.getCategories().stream().map(Category::getId).collect(Collectors.toList()));
        responseDto.setProcedureIds(updatedUser.getProcedures().stream().map(Procedure::getId).collect(Collectors.toList()));

        return responseDto;
    }

    /**
     * Retrieves a master by their user ID.
     *
     * @param id the user ID
     * @return the master user
     * @throws UserNotFoundException if the user is not found
     */
    @Override
    public UserDto getMasterById(Long id) {
        return getUserByIdAndRole(id, User.Role.MASTER);
    }

    /**
     * Retrieves a client by their user ID.
     *
     * @param id the user ID
     * @return the client user
     * @throws UserNotFoundException if the user is not found
     */
    @Override
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
    @Transactional
    public void confirmMasterByEmail(String email) {
        User masterUser = findMasterUserByEmail(email);

        if (masterUser.isActive()) {
            throw new IllegalStateException("Master is already active.");
        }
        masterUser.setActive(true);
        userRepository.save(masterUser);

        mailSender.sendRegistrationEmail(masterUser.getEmail());
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


    @Override
    public String uploadProfilePhoto(Long userId, MultipartFile file, long maxSize) throws IOException {
        if (!FileValidationUtils.isValidImage(file)) {
            throw new InvalidFileException("Only JPEG, PNG, or GIF images are allowed.");
        }
        if (file.getSize() > maxSize) {
            throw new InvalidFileException("File size exceeds the maximum allowed size of " + maxSize + " bytes.");
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
    public List<PortfolioImageDto> uploadPortfolioPhotos(Long userId, List<MultipartFile> files, long maxSize) throws IOException {
        for (MultipartFile file : files) {
            if (!FileValidationUtils.isValidImage(file)) {
                throw new InvalidFileException("Only JPEG, PNG, or GIF images are allowed.");
            }
            if (file.getSize() > maxSize) {
                throw new InvalidFileException("File size exceeds the maximum allowed size of " + maxSize + " bytes.");
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

                try {
                    PortfolioPhoto savedPhoto = portfolioPhotoRepository.save(portfolioPhoto); // Сохраняем фото и получаем объект с ID
                    uploadedPhotos.add(
                            PortfolioImageDto.builder()
                                    .id(savedPhoto.getId())
                                    .url(savedPhoto.getUrl())
                                    .build()
                    );
                } catch (Exception e) {
                    throw new PortfolioPhotoSaveException("Error saving portfolio photo for user " + userId, e);
                }
            } catch (ImageUploadException e) {
                throw new ImageUploadException("Error uploading image for user " + userId, e);
            }
        }

        return uploadedPhotos;
    }

    @Override
    public void deleteProfilePhoto(Long userId) throws IOException {
        User user = findUserByIdOrThrow(userId);
        String profilePhotoUrl = user.getProfilePhotoUrl();
        if (profilePhotoUrl != null) {
            try {
                String publicId = imageUploadService.extractPublicId(profilePhotoUrl);
                if (imageUploadService.exists(publicId)) {
                    imageUploadService.deleteImage(publicId);
                } else {
                    throw new ImageNotFoundException("Image not found for deletion.");
                }

                user.setProfilePhotoUrl(null);
                userRepository.save(user);
            } catch (Exception e) {
                throw new ImageUploadException("Error during image deletion.", e);
            }
        }
    }


    @Override
    public void deletePortfolioPhoto(Long userId, Long photoId) throws IOException {

        User user = findUserByIdOrThrow(userId);

        PortfolioPhoto photo = portfolioPhotoRepository.findById(photoId)
                .orElseThrow(() -> new PhotoNotFoundException("Photo with ID " + photoId + " not found."));

        if (!photo.getUser().getId().equals(user.getId())) {
            throw new PhotoOwnershipException("Photo " + photoId + " does not belong to user " + userId);
        }

        try {
            String publicId = imageUploadService.extractPublicId(photo.getUrl());
            imageUploadService.deleteImage(publicId);
        } catch (Exception e) {
            throw new ImageDeleteException("Error deleting image with ID " + photoId, e);
        }

        try {
            portfolioPhotoRepository.delete(photo);
        } catch (Exception e) {
            throw new PortfolioPhotoDeleteException("Error deleting portfolio photo with ID " + photoId, e);
        }
    }


    /**
     * Retrieves all users with the MASTER role.
     *
     * @return a list of all master users
     */
    @Override
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
     * @throws UserNotFoundException if the user is not found
     */
    @Override
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
    public List<UserDetailsDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::userDetailsToDto)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves all users associated with a specific category.
     *
     * @param categoryId the category ID
     * @return a list of users associated with the category
     */
    @Override
    public List<UserDetailsDto> findUsersByCategoryId(Long categoryId) {
        List<User> users = userRepository.findUsersByCategoryId(categoryId);
        if (users.isEmpty()) {
            throw UserNotFoundException.forCategoryId(categoryId);
        }
        return users.stream()
                .map(userMapper::userDetailsToDto)
                .collect(Collectors.toList());
    }

    /**
     * Finds a user by their email.
     *
     * @param email the user's email
     * @return an optional containing the user if found
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
     */
    @Override
    @Transactional
    public void deleteById(Long id) {
        User user = findUserByIdOrThrow(id);
        userRepository.delete(user);
    }


    private UserDto getUserByIdAndRole(Long userId, User.Role role) {
        User user = findUserByIdAndRole(userId, role);
        return userMapper.toDto(user);
    }

    private User findUserByIdOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found for id: " + userId));
    }

    private User findUserByEmailOrThrow(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RestException(HttpStatus.UNAUTHORIZED, "Invalid email <" + email + ">"));
    }

    private User findUserByIdAndRole(Long userId, User.Role role) {
        return userRepository.findByIdAndRole(userId, role)
                .orElseThrow(() -> new UserNotFoundException("User not found for id: " + userId + " with role: " + role));
    }

    public boolean isValidImage(MultipartFile file, long maxSize) {

        String contentType = file.getContentType();
        if (contentType == null ||
                !(contentType.equals("image/jpeg") ||
                        contentType.equals("image/png") ||
                        contentType.equals("image/gif"))) {
            return false;
        }

        if (file.getSize() > maxSize) {
            return false;
        }
        return true;
    }
}