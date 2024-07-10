package com.example.end.controller;

import com.example.end.controller.api.UserMetadataApi;
import com.example.end.dto.PortfolioImageDto;
import com.example.end.dto.ProfileImageDto;
import com.example.end.dto.UserDetailsDto;
import com.example.end.service.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
@RestController
public class UserMetadataController implements UserMetadataApi {
    public final UserService userService;

    @Override
    public UserDetailsDto addProfileImages(Long userId, ProfileImageDto profileImageDto) {
        return userService.addProfileImage(userId, profileImageDto);
    }

    @Override
   public UserDetailsDto addPortfolioImages(Long userId, PortfolioImageDto portfolioImageDto){
       return userService.addPortfolioImages(userId, portfolioImageDto);
   }
}
