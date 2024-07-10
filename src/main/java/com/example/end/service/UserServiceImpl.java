package com.example.end.service;

import com.example.end.dto.*;
import com.example.end.exceptions.ProcedureNotFoundException;
import com.example.end.exceptions.RestException;
import com.example.end.exceptions.UserNotFoundException;
import com.example.end.mail.ProjectMailSender;
import com.example.end.mapping.UserMapper;
import com.example.end.models.*;
import com.example.end.repository.CategoryRepository;
import com.example.end.repository.UserRepository;
import com.example.end.security.sec_servivce.TokenService;
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
    private final PasswordEncoder passwordEncoder;
    private final ProjectMailSender mailSender;
    private final TokenService tokenService;
    @Value("${spring.mail.username}")
    private String adminEmail;

    @Override
    @Transactional
    public UserDto register(NewUserDto newUserDto) {
        validateEmail(newUserDto.getEmail());
        User user = createUser(newUserDto);
        if (user.getRole() == User.Role.MASTER) {
            sendConfirmationEmails(user);
            user.setActive(false);
        } else {
            user.setActive(true);
            sendRegistrationEmail(user);
        }
        userRepository.save(user);

        String accessToken = tokenService.generateAccessToken(user);
        String refreshToken = tokenService.generateRefreshToken(user);

        UserDto userDto = userMapper.toDto(user);
        userDto.setAccessToken(accessToken);
        userDto.setRefreshToken(refreshToken);

        return userDto;
    }

    @Override
    public UserDto authenticate(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RestException(HttpStatus.UNAUTHORIZED, "Неверный email или пароль"));

        if (!passwordEncoder.matches(password, user.getHashPassword())) {
            throw new RestException(HttpStatus.UNAUTHORIZED, "Неверный email или пароль");
        }

        return userMapper.toDto(user);
    }

    @Override
    public UserDetailsDto getById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found for id: " + id));
        return userMapper.userDetailsToDto(user);
    }


    @Override
    public void sendConfirmationEmails(User masterUser) {
        String subject = "Bestätigung der Registrierung des Meisters ausstehend";
        String messageToMaster = "Ihre Registrierung als Meister wurde erfasst und wartet auf die Bestätigung durch den Administrator. " +
                "Wir werden uns mit Ihnen in Verbindung setzen, sobald Ihr Konto bestätigt wurde. Vielen Dank für Ihre Registrierung!";
        mailSender.sendEmail(masterUser.getEmail(), subject, messageToMaster);
        String messageToAdmin = masterUser.getFirstName() + " " + masterUser.getLastName();
        mailSender.sendMasterConfirmationRequest(adminEmail, messageToAdmin);
    }


      @Override
      public void validateEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new RestException(HttpStatus.CONFLICT, "User with email <" + email + "> already exists");
        }
    }

  @Override
  public User createUser(NewUserDto newUserDto) {
        return User.builder()
                .firstName(newUserDto.getFirstName())
                .lastName(newUserDto.getLastName())
                .email(newUserDto.getEmail())
                .role(newUserDto.getRole() != null ? newUserDto.getRole() : User.Role.CLIENT)
                .hashPassword(passwordEncoder.encode(newUserDto.getHashPassword()))
                .isActive(newUserDto.getRole() != User.Role.MASTER)
                .build();
    }


    @Override
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

    private void sendRegistrationEmail(User user) {
        String subject = "Registrierung auf der Website";
        String message = "Herzlichen Glückwunsch zur erfolgreichen Registrierung auf unserer Website!";
        mailSender.sendEmail(user.getEmail(), subject, message);
    }


    @Override
    public UserDto getMasterById(Long id) {
        User master =  userRepository.findByIdAndRole(id, User.Role.MASTER)
                .orElseThrow(() -> new UserNotFoundException("Master not found for id: " + id));
        return userMapper.toDto(master);
    }
    @Override
    public UserDto getClientById(Long id) {
        User client = userRepository.findByIdAndRole(id, User.Role.CLIENT)
                .orElseThrow(() -> new UserNotFoundException("Client not found for id: " + id));
        return userMapper.toDto(client);
    }

    @Override
    @Transactional
    public void confirmMasterByEmail(String email) {
        User masterUser = findMasterUserByEmail(email);
        activateMasterUser(masterUser);
        sendRegistrationEmail(masterUser);
    }
    @Override
    public User findMasterUserByEmail(String email) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        User masterUser = optionalUser.orElseThrow(() -> new UserNotFoundException("Master user not found or already confirmed for email: " + email));
        if (masterUser.getRole() != User.Role.MASTER || masterUser.isActive()) {
            throw new UserNotFoundException("Master user not found or already confirmed for email: " + email);
        }
        return masterUser;
    }
    @Override
    @Transactional
    public UserDetailsDto addProfileImage(Long userId, ProfileImageDto profileImageDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found for id: " + userId));
        user.setProfileImageUrl(profileImageDto.getProfileImageUrl());
        User updatedUser = userRepository.save(user);
        return userMapper.userDetailsToDto(updatedUser);
    }

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

    @Override
    public void activateMasterUser(User masterUser) {
        masterUser.setActive(true);
        userRepository.save(masterUser);
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
        return null;
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
            throw new UserNotFoundException("User for category with ID " + categoryId + " not found");
        }
        return users.stream()
                .map(userMapper::userDetailsToDto)
                .collect(Collectors.toList());
    }
    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public Optional<User> loadUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found for id: " + id));
        userRepository.delete(user);
    }
    @Override
    public void sendMessageToAdmin(String email, String phone, String firstName, String lastName, String message) {
        String subject = "Neue Nachricht vom Benutzer: " + firstName + " " + lastName;
        String messageBody = "Email: " + email + "\n" +
                "Telefon: " + phone + "\n" +
                "Nachricht: " + message;
        mailSender.sendEmail(adminEmail, subject, messageBody);
    }

//    @Override
//    public void sendMessageToAdmin(String subject, String message) {
//        mailSender.sendEmail(adminEmail, subject, message);
//    }
}

