package com.example.end.service;

import com.example.end.dto.NewUserDetailsDto;
import com.example.end.dto.NewUserDto;
import com.example.end.dto.UserDetailsDto;
import com.example.end.dto.UserDto;
import com.example.end.infrastructure.exceptions.ResourceNotFoundException;
import com.example.end.infrastructure.exceptions.RestException;
import com.example.end.infrastructure.mail.ProjectMailSender;
import com.example.end.mapping.UserMapper;
import com.example.end.models.*;
import com.example.end.repository.CategoryRepository;
import com.example.end.repository.UserRepository;
import com.example.end.infrastructure.security.sec_servivce.TokenService;
import com.example.end.service.interfaces.CategoryService;
import com.example.end.service.interfaces.UserService;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
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

    @Value("${SPRING_MAIL_USERNAME}")
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
    @Transactional
    @Override
    public UserDto register(NewUserDto newUserDto) {
        validateEmail(newUserDto.getEmail());
        User user = newUserDto.createUser();
        user.setPassword(passwordEncoder.encode(newUserDto.getPassword()));

        if (user.getRole() == User.Role.MASTER) {
            senderService.sendMasterRegistrationConfirmation(user);
            user.setActive(false);
        } else {
            mailSender.sendRegistrationEmail(user.getEmail());
            user.setActive(true);
        }

        User savedUser = userRepository.save(user);

        UserDto userDto = userMapper.toDto(savedUser);

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
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RestException(HttpStatus.UNAUTHORIZED, "Invalid password");
        }
        return userMapper.toDto(user);
    }

    /**
     * Gets user information by ID.
     *
     * @param id user ID
     * @return detailed user information
     * @throws ResourceNotFoundException if user is not found
     */
    @Override
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public UserDetailsDto getById(Long id) {
        User user = userRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
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
     * @throws ResourceNotFoundException if the user is not found
     */
    @CacheEvict(value = {"allMasters", "usersByCategory"}, allEntries = true)
    @Override
    @Transactional
    public UserDetailsDto updateUserDetails(Long userId, NewUserDetailsDto userDetailsDto) {
        User user = userRepository.findByIdWithDetails(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

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
     * @throws  if the user is not found
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
     * @throws  if the user is not found
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
     * Gets a list of all masters.
     *
     * @return list of all users with MASTER role
     */
    @Cacheable(value = "allMasters", unless = "#result == null || #result.isEmpty()")
    @Override
    @Transactional(readOnly = true)
    public List<UserDetailsDto> getAllMasters(int page, int size) {
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size);
        List<User> masters = userRepository.findAllMastersWithDetails(pageable);
        return masters.stream()
                .map(userMapper::userDetailsToDto)
                .collect(Collectors.toList());
    }


    /**
     * Retrieves a user by their user ID.
     *
     * @param currentUserId the user ID
     * @return the user details
     * @throws  if the user is not found
     */
    @Override
    public UserDto getUserById(Long currentUserId) {
        User user = findUserByIdOrThrow(currentUserId);
        return userMapper.toDto(user);
    }

    /**
     * Gets a list of all users.
     *
     * @return list of all users
     */
    @Override
    @Transactional(readOnly = true)
    public List<UserDetailsDto> getAllUsers(int page, int size) {
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size);
        List<User> users = userRepository.findAllWithDetails(pageable);
        return users.stream()
                .map(userMapper::userDetailsToDto)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a master user by their email.
     *
     * @param email the email of the master user
     * @return the master user
     * @throws  if the user is not found or not a master
     */
    @Override
    public User findMasterUserByEmail(String email) {
        User masterUser = findUserByEmailOrThrow(email);
        if (masterUser.getRole() != User.Role.MASTER) {
            throw new ResourceNotFoundException("User is not a master: " + email);
        }
        if (masterUser.isActive()) {
            throw new ResourceNotFoundException ("Master is already active: " + email);
        }
        return masterUser;
    }

    /**
     * Finds users by category ID.
     *
     * @param categoryId category ID
     * @return list of users related to the given category
     */
    @Cacheable(value = "usersByCategory", key = "#categoryId", unless = "#result == null || #result.isEmpty()")
    @Override
    @Transactional(readOnly = true)
    public List<UserDetailsDto> findUsersByCategoryId(Long categoryId) {
        List<User> users = userRepository.findUsersByCategoryIdWithDetails(categoryId);
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
    @CacheEvict(value = {"allMasters", "usersByCategory"}, allEntries = true)
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

    User findUserByIdOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found for id: " + userId));
    }

    private User findUserByEmailOrThrow(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found for email: " + email));
    }

    private User findUserByIdAndRole(Long userId, User.Role role) {
        return userRepository.findByIdAndRole(userId, role)
                .orElseThrow(() -> new ResourceNotFoundException("User not found for id: " + userId + " with role: " + role));
    }
}