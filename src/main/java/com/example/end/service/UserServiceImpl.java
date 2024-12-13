package com.example.end.service;

import com.example.end.dto.*;
import com.example.end.exceptions.ProcedureNotFoundException;
import com.example.end.exceptions.RestException;
import com.example.end.exceptions.UserNotFoundException;
import com.example.end.infrastructure.mail.ProjectMailSender;
import com.example.end.mapping.UserMapper;
import com.example.end.models.*;
import com.example.end.repository.CategoryRepository;
import com.example.end.repository.UserRepository;
import com.example.end.infrastructure.security.sec_servivce.TokenService;
import com.example.end.service.interfaces.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.util.*;
import java.util.stream.Collectors;

/**
 * The UserService class provides methods for managing users.
 */
@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final ProjectMailSender mailSender;
    private final TokenService tokenService;

    @Value("${spring.mail.admin-email}")
    private String adminEmail;

    /**
     * Description: This method saves the new user to the DB or throws an exception if the user's email address already exists.
     *
     * <p>This method validates the user's email and creates a new user with the specified
     * information. If the user's role is {@link User.Role#MASTER}, the method sends
     * confirmation emails and sets the user's active status to false. Otherwise, it sets the
     * user's active status to true and sends a registration email.
     *
     * <p>The method then saves the user in the database, generates access and refresh tokens,
     * and maps the user data to a {@link UserDto} object with the generated tokens included.
     *
     * @param newUserDto the data transfer object containing the new user's registration details
     * @return a {@link UserDto} object with the registered user's data and generated tokens
     * @throws RestException if the provided email is already in use
     */
    @Override
    @Transactional
    public UserDto register(NewUserDto newUserDto) {
        validateEmail(newUserDto.getEmail());
        User user = newUserDto.createUser();
        user.setHashPassword(passwordEncoder.encode(newUserDto.getHashPassword()));

        if (user.getRole() == User.Role.MASTER) {
            mailSender.sendConfirmationEmails(user, adminEmail);
            user.setActive(false);
        } else {
            user.setActive(true);
            mailSender.sendRegistrationEmail(user);
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
     * Description: This method authenticates the user using email and password.
     *
     * <p>This method searches for a user by their email in the database.
     *  If a user with the specified email is not found, or if the password is incorrect,
     *  a {@link RestException} with an HTTP status 401 (Unauthorized) is thrown.
     *
     *  @param email    the email of the user attempting to authenticate
     *  @param password the password of the user attempting to authenticate
     *  @return a {@link UserDto} object containing the authenticated user's data
     *  @throws RestException if the email or password is invalid
     */
    @Override
    public UserDto authenticate(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RestException(HttpStatus.UNAUTHORIZED, "Invalid email <" + email + ">"));

        if (!passwordEncoder.matches(password, user.getHashPassword())) {
            throw new RestException(HttpStatus.UNAUTHORIZED, "Invalid email <" + password + ">");
        }

        return userMapper.toDto(user);
    }

    /**
     * Fetches user details by the provided unique user ID.
     *
     * @param id the unique identifier of the user to retrieve
     * @return a {@link UserDetailsDto} containing the detailed information of the user
     * @throws UserNotFoundException if no user exists with the specified ID
     */
    @Override
    public UserDetailsDto getById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found for id: " + id));
        return userMapper.userDetailsToDto(user);
    }

    /**
     * Validates whether the provided email address is already in use.
     * <p>
     * This method checks the user repository to see if the given email is associated
     * with an existing user. If the email exists, a {@link RestException} is thrown
     * with a conflict HTTP status and an appropriate error message.
     * </p>
     *
     * @param email the email address to validate.
     * @throws RestException if the email is already associated with an existing user.
     *                       The exception will have an HTTP status of {@link HttpStatus#CONFLICT}
     *                       and a message indicating the conflict.
     */
    //TODO void method, we need to check if something needs to be returned for processing on FE
      @Override
      public void validateEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new RestException(HttpStatus.CONFLICT, "User with email <" + email + "> already exists");
        }
    }

    /**
     * Updates the details of a user, including their description, phone number, address, categories, and procedures.
     *
     * <p>
     * This method retrieves a user by their unique ID, updates their information based on the provided
     * details, and saves the changes to the database. It also validates the provided category and procedure IDs
     * to ensure they exist and belong to the selected categories.
     * </p>
     *
     * @param userId the ID of the user whose details are to be updated. Must be a valid user ID.
     * @param userDetailsDto a {@code NewUserDetailsDto} object containing the new details for the user.
     *                        Includes description, phone number, address, category IDs, and procedure IDs.
     * @return a {@code UserDetailsDto} containing the updated user details, including category and procedure IDs.
     *
     * @throws UserNotFoundException if no user is found for the provided ID.
     * @throws ProcedureNotFoundException if a procedure ID in {@code userDetailsDto} is invalid or doesn't belong to the selected categories.
     */
    //TODO UserDetailsDto updateUserDetails loop within loop
    //TODO split this method
    @Override
    @Transactional
    public UserDetailsDto updateUserDetails(Long userId, NewUserDetailsDto userDetailsDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found for id: " + userId));

        user.setDescription(userDetailsDto.getDescription());
        user.setPhoneNumber(userDetailsDto.getPhoneNumber());
        user.setAddress(userDetailsDto.getAddress());

        Set<Category> selectedCategories = new HashSet<>(categoryRepository.findAllById(userDetailsDto.getCategoryIds()));
        user.setCategories(selectedCategories);
        Set<Procedure> selectedProcedures = new HashSet<>();

        for (Category category : selectedCategories) {
            for (Long procedureId : userDetailsDto.getProcedureIds()) {
                Procedure procedure = category.getProcedures().stream()
                        .filter(p -> p.getId().equals(procedureId))
                        .findFirst()
                        .orElseThrow(() -> new ProcedureNotFoundException("Procedure not found for id: " + procedureId));
                selectedProcedures.add(procedure);
            }
        }
        user.setProcedures(selectedProcedures);
        User updatedUser = userRepository.save(user);
        UserDetailsDto responseDto = userMapper.userDetailsToDto(updatedUser);
        responseDto.setCategoryIds(updatedUser.getCategories().stream().map(Category::getId).collect(Collectors.toList()));
        responseDto.setProcedureIds(updatedUser.getProcedures().stream().map(Procedure::getId).collect(Collectors.toList()));
        return responseDto;
    }

    /**
     * Retrieves a master user by their unique identifier.
     *
     * <p>
     * This method fetches a user from the repository based on their ID and verifies
     * that they have the role of a master. If the user exists and meets the role requirement,
     * their details are mapped to a {@code UserDto} object and returned.
     * </p>
     *
     * @param id the unique identifier of the master user to retrieve. Must be a valid ID.
     * @return a {@code UserDto} object containing the details of the master user.
     *
     * @throws UserNotFoundException if no user with the specified ID and role of MASTER is found.
     */
    @Override
    public UserDto getMasterById(Long id) {
        User master =  userRepository.findByIdAndRole(id, User.Role.MASTER)
                .orElseThrow(() -> new UserNotFoundException("Master not found for id: " + id));
        return userMapper.toDto(master);
    }

    /**
     * Retrieves a client user by their unique identifier.
     *
     * <p>
     * This method fetches a user from the repository based on their ID and verifies
     * that they have the role of a client. If the user exists and meets the role requirement,
     * their details are mapped to a {@code UserDto} object and returned.
     * </p>
     *
     * @param id the unique identifier of the client user to retrieve. Must be a valid ID.
     * @return a {@code UserDto} object containing the details of the client user.
     *
     * @throws UserNotFoundException if no user with the specified ID and role of CLIENT is found.
     */
    @Override
    public UserDto getClientById(Long id) {
        User client = userRepository.findByIdAndRole(id, User.Role.CLIENT)
                .orElseThrow(() -> new UserNotFoundException("Client not found for id: " + id));
        return userMapper.toDto(client);
    }

    /**
     * Confirms the registration of a master user by their email.
     *
     * <p>
     * This method performs the following actions:
     * <ul>
     *   <li>Finds the master user in the system by their email address.</li>
     *   <li>Activates the user's account if they are found.</li>
     *   <li>Sends a registration confirmation email to the master user.</li>
     * </ul>
     * </p>
     *
     * <p><b>Transactional Behavior:</b> This method is transactional, ensuring that all operations
     * (finding, activating, and sending the email) are completed as a single unit of work.
     * If any step fails, the transaction will roll back.</p>
     *
     * @param email the email address of the master user to confirm. Must not be {@code null} or empty.
     * @throws UserNotFoundException if no user with the specified email is found.
    // * @throws MailSendingException if an error occurs while sending the confirmation email.
     */
    //TODO void method, we need to check if something needs to be returned for processing on FE
    @Override
    @Transactional
    public void confirmMasterByEmail(String email) {
        User masterUser = findMasterUserByEmail(email);

        if (masterUser.isActive()) {
            throw new IllegalStateException("Master is already active.");
        }
        masterUser.setActive(true);
        userRepository.save(masterUser);

        mailSender.sendRegistrationEmail(masterUser);
    }

    /**
     * Finds and retrieves a master user by their email address.
     *
     * <p>
     * This method queries the user repository for a user with the given email address, ensuring that the user:
     * - Has the role of MASTER.
     * - Is not yet active (indicating their account has not been confirmed).
     * If the user does not meet these criteria, an exception is thrown.
     * </p>
     *
     * @param email the email address of the master user to retrieve.
     * @return a {@code User} object representing the master user.
     *
     * @throws UserNotFoundException if:
     *         - No user with the specified email exists.
     *         - The user does not have the MASTER role.
     *         - The user is already active (confirmed).
     */
    //TODO differ message?
    @Override
    public User findMasterUserByEmail(String email) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        User masterUser = optionalUser.orElseThrow(() -> new UserNotFoundException("Master user not found or already confirmed for email: " + email));
        if (masterUser.getRole() != User.Role.MASTER || masterUser.isActive()) {
            throw new UserNotFoundException("Master user not found or already confirmed for email: " + email);
        }
        return masterUser;
    }


    /**
     * Adds or updates the profile image URL for a user.
     *
     * <p>
     * This method retrieves a user by their unique ID and updates their profile image URL using the provided
     * {@link ProfileImageDto}. The updated user information is then saved to the database and returned as a DTO.
     * </p>
     *
     * @param userId the ID of the user whose profile image URL is to be added or updated.
     * @param profileImageDto a {@code ProfileImageDto} containing the URL of the new profile image.
     * @return a {@code UserDetailsDto} containing the updated user details.
     *
     * @throws UserNotFoundException if no user is found for the provided ID.
     */
    //TODO check null or empty, blank ImageString
    @Override
    @Transactional
    public UserDetailsDto addProfileImage(Long userId, ProfileImageDto profileImageDto) {
        if (profileImageDto == null || profileImageDto.getProfileImageUrl() == null || profileImageDto.getProfileImageUrl().isEmpty()) {
            throw new IllegalArgumentException("Profile image URL must not be null or empty");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found for id: " + userId));
        user.setProfileImageUrl(profileImageDto.getProfileImageUrl());
        User updatedUser = userRepository.save(user);
        return userMapper.userDetailsToDto(updatedUser);
    }

    /**
     * Updates the portfolio images of a user.
     *
     * <p>
     * This method retrieves a user by their ID, updates their portfolio image URLs with the provided set,
     * saves the changes to the database, and returns the updated user details.
     * </p>
     *
     * @param userId the unique identifier of the user whose portfolio images are to be updated.
     * @param portfolioImageDto an object containing the new portfolio image URLs for the user.
     * @return a {@code UserDetailsDto} object containing the updated user details, including the new portfolio images.
     *
     * @throws UserNotFoundException if the user with the specified ID does not exist.
     * @throws IllegalArgumentException if the provided set of portfolio image URLs is null or empty.
     */
    @Override
    @Transactional
    public UserDetailsDto addPortfolioImages(Long userId, PortfolioImageDto portfolioImageDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found for id: " + userId));

        Set<String> portfolioImageUrls = new HashSet<>(portfolioImageDto.getPortfolioImageUrls());
        user.setPortfolioImageUrls(portfolioImageUrls);

        User updatedUser = userRepository.save(user);
        return userMapper.userDetailsToDto(updatedUser);
    }

    /**
     * Activates a master user account.
     *
     * <p>
     * This method sets the active status of a given master user to {@code true} and saves the updated user entity
     * to the database.
     * </p>
     *
     * @param masterUser the {@code User} object representing the master user whose account is to be activated.
     *                   The user object must not be {@code null}.
     *
     * @throws IllegalArgumentException if the provided {@code masterUser} is {@code null}.
     */
    //TODO void method, we need to check if something needs to be returned for processing on FE
    @Override
    public void activateMasterUser(User masterUser) {
        if (masterUser == null) {
            throw new IllegalArgumentException("Master user cannot be null");
        }
        if (!masterUser.isActive()) {
            masterUser.setActive(true);
            userRepository.save(masterUser);
        }
    }

    /**
     * Retrieves a list of all users with the "MASTER" role and converts them to DTOs.
     *
     * <p>
     * This method queries the database for all users with the role {@code User.Role.MASTER},
     * converts each user entity into a {@code UserDetailsDto}, and returns the list of DTOs.
     * </p>
     *
     * @return a {@code List<UserDetailsDto>} containing details of all users with the "MASTER" role.
     *         If no such users exist, an empty list is returned.
     */
    @Override
    public List<UserDetailsDto> getAllMasters() {
        List<User> masters = userRepository.findAllByRole(User.Role.MASTER);
        return masters.stream()
                .map(userMapper::userDetailsToDto)
                .collect(Collectors.toList());
    }

    //TODO????
    @Override
    public UserDto getUserById(Long currentUserId) {
        return null;
    }

    /**
     * Retrieves a list of all users in the system and converts them to DTOs.
     *
     * <p>
     * This method fetches all user entities from the database, transforms each
     * entity into a {@code UserDetailsDto} using a mapper, and returns a list of DTOs.
     * </p>
     *
     * @return a {@code List<UserDetailsDto>} containing details of all users in the system.
     *         If no users exist, an empty list is returned.
     */
    @Override
    public List<UserDetailsDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::userDetailsToDto)
                .collect(Collectors.toList());
    }

    /**
     * Finds and retrieves a list of users associated with a specific category ID.
     *
     * <p>
     * This method queries the database for users linked to a particular category and
     * converts the retrieved entities into DTOs. If no users are found for the specified
     * category ID, a {@code UserNotFoundException} is thrown.
     * </p>
     *
     * @param categoryId the ID of the category to search for users.
     * @return a {@code List<UserDetailsDto>} containing details of the users associated with the category.
     *         Returns an empty list if no users are linked to the specified category ID.
     * @throws UserNotFoundException if no users are associated with the provided category ID.
     */
    @Override
    public List<UserDetailsDto> findUsersByCategoryId(Long categoryId) {
        List<User> users = userRepository.findUsersByCategoryId(categoryId);
        if (users.isEmpty()) {
            throw new UserNotFoundException("User for category with ID " + categoryId + " not found");
        }
        return users.stream()
                .map(userMapper::userDetailsToDto)
                .collect(Collectors.toList());
    }



    /**
     * Finds a user by their email address.
     *
     * <p>
     * This method queries the database to find a user with the provided email. If a user exists,
     * it returns an {@code Optional} containing the user entity. If no user is found, it returns an empty {@code Optional}.
     * </p>
     *
     * @param email the email address to search for. Must be a valid email string.
     * @return an {@code Optional<User>} containing the user entity if found, or empty if no user exists with the given email.
     */
    //TODO two methods findByEmail and loadUserByEmail do the same - ???
    @Override
    public Optional<User> findByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email must not be null or empty");
        }
        return userRepository.findByEmail(email);
    }

    //TODO two methods findByEmail and loadUserByEmail do the same - ???
    @Override
    public Optional<User> loadUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    //TODO void method, we need to check if something needs to be returned for processing on FE
    @Override
    @Transactional
    public void deleteById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found for id: " + id));
        userRepository.delete(user);
    }

}

