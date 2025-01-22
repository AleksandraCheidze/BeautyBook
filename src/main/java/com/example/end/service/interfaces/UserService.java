package com.example.end.service.interfaces;


import com.example.end.dto.*;
import com.example.end.models.PortfolioPhoto;
import com.example.end.models.User;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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


    UserDto getUserById(Long currentUserId);

    List<UserDetailsDto> getAllUsers();

    void deleteById(Long id);

    User findMasterUserByEmail(String email);

    @jakarta.transaction.Transactional
    String uploadProfilePhoto(Long userId, MultipartFile file, long maxSize) throws IOException, ExecutionException, InterruptedException;

    @jakarta.transaction.Transactional
    List<String> uploadPortfolioPhotos(Long userId, List<MultipartFile> files, long maxSize) throws IOException, ExecutionException, InterruptedException;

    void deleteProfilePhoto(Long userId) throws IOException, ExecutionException, InterruptedException;


    void deletePortfolioPhoto(Long userId, Long photoId) throws IOException;

    List<UserDetailsDto> getAllMasters();


    List<PortfolioPhoto> getPortfolioPhotos(Long userId);
}


