package com.example.end.service.interfaces;


import com.example.end.dto.*;
import com.example.end.models.User;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public interface UserService {

    @Transactional
    UserDto register(NewUserDto newUserDto);

    UserDto authenticate(String email, String password);

    UserDetailsDto getById(Long id);

    void sendConfirmationEmails(User masterUser);

    void validateEmail(String email);

    User createUser(NewUserDto newUserDto);


    @jakarta.transaction.Transactional
    UserDetailsDto updateUserDetails(Long userId, NewUserDetailsDto userDetailsDto);

    UserDto getMasterById(Long id);

    UserDto getClientById(Long id);

    @jakarta.transaction.Transactional
    void confirmMasterByEmail(String email);

    List<UserDetailsDto> findUsersByCategoryId(Long categoryId);

    Optional<User> findByEmail(String email);

    Optional<User> loadUserByEmail(String email);

    List<UserDetailsDto> getAllUsers();

    void deleteById(Long id);

    User findMasterUserByEmail(String email);

    UserDetailsDto addProfileImage(Long userId, ProfileImageDto profileImageDto);

    UserDetailsDto addPortfolioImages(Long userId, PortfolioImageDto portfolioImageDto);

    void activateMasterUser(User masterUser);

    List<UserDetailsDto> getAllMasters();

    void sendMessageToAdmin(String email, String phone, String firstName, String lastName, String message);

    // void sendMessageToAdmin(String subject, String message);

    UserDto getUserById(Long currentUserId);

}


