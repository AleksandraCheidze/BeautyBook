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

    void validateEmail(String email);

    @jakarta.transaction.Transactional
    UserDetailsDto updateUserDetails(Long userId, NewUserDetailsDto userDetailsDto);

    UserDto getMasterById(Long id);

    UserDto getClientById(Long id);

    void confirmMasterByEmail(String email);

    List<UserDetailsDto> findUsersByCategoryId(Long categoryId);

    Optional<User> findByEmail(String email);

    List<UserDetailsDto> getAllUsers();

    void deleteById(Long id);

    User findMasterUserByEmail(String email);

    UserDetailsDto addProfileImage(Long userId, ProfileImageDto profileImageDto);

    UserDetailsDto addPortfolioImages(Long userId, PortfolioImageDto portfolioImageDto);


    List<UserDetailsDto> getAllMasters();

    UserDto getUserById(Long currentUserId);


}


