package com.example.end.service;

import com.example.end.dto.*;
import com.example.end.exceptions.RestException;
import com.example.end.exceptions.UserNotFoundException;
import com.example.end.infrastructure.mail.ProjectMailSender;
import com.example.end.mapping.UserMapper;
import com.example.end.models.*;
import com.example.end.repository.CategoryRepository;
import com.example.end.repository.UserRepository;
import com.example.end.infrastructure.security.sec_servivce.TokenService;
import com.example.end.service.interfaces.CategoryService;
import com.example.end.service.interfaces.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

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

    @Value("${spring.mail.admin-email}")
    private String adminEmail;

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

    @Override
    public UserDto authenticate(String email, String password) {
        User user = findUserByEmailOrThrow(email);
        if (!passwordEncoder.matches(password, user.getHashPassword())) {
            throw new RestException(HttpStatus.UNAUTHORIZED, "Invalid password");
        }
        return userMapper.toDto(user);
    }

    @Override
    public UserDetailsDto getById(Long id) {
        User user = findUserByIdOrThrow(id);
        return userMapper.userDetailsToDto(user);
    }

    @Override
    public void validateEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new RestException(HttpStatus.CONFLICT, "User with email <" + email + "> already exists");
        }
    }

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

    @Override
    public UserDto getMasterById(Long id) {
        User master = findUserByIdAndRole(id, User.Role.MASTER);
        return userMapper.toDto(master);
    }

    @Override
    public UserDto getClientById(Long id) {
        User client = findUserByIdAndRole(id, User.Role.CLIENT);
        return userMapper.toDto(client);
    }

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
    @Transactional
    public UserDetailsDto addProfileImage(Long userId, ProfileImageDto profileImageDto) {
        if (profileImageDto == null || profileImageDto.getProfileImageUrl() == null || profileImageDto.getProfileImageUrl().isEmpty()) {
            throw new IllegalArgumentException("Profile image URL must not be null or empty");
        }
        User user = findUserByIdOrThrow(userId);
        user.setProfileImageUrl(profileImageDto.getProfileImageUrl());
        User updatedUser = userRepository.save(user);
        return userMapper.userDetailsToDto(updatedUser);
    }

    @Override
    @Transactional
    public UserDetailsDto addPortfolioImages(Long userId, PortfolioImageDto portfolioImageDto) {
        User user = findUserByIdOrThrow(userId);

        Set<String> portfolioImageUrls = new HashSet<>(portfolioImageDto.getPortfolioImageUrls());
        user.setPortfolioImageUrls(portfolioImageUrls);

        User updatedUser = userRepository.save(user);
        return userMapper.userDetailsToDto(updatedUser);
    }

    @Override
    public List<UserDetailsDto> getAllMasters() {
        List<User> masters = userRepository.findAllByRole(User.Role.MASTER);
        return masters.stream()
                .map(userMapper::userDetailsToDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getUserById(Long currentUserId) {
        User user = findUserByIdOrThrow(currentUserId);
        return userMapper.toDto(user);
    }

    @Override
    public List<UserDetailsDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::userDetailsToDto)
                .collect(Collectors.toList());
    }

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

    @Override
    public Optional<User> findByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email must not be null or empty");
        }
        return userRepository.findByEmail(email);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        User user = findUserByIdOrThrow(id);
        userRepository.delete(user);
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
}
