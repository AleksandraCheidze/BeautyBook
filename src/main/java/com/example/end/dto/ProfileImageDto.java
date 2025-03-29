package com.example.end.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProfileImageDto {

    @NotNull
    @Size(min = 1)
    @Schema(description = "Profile image of the user", example = "https://lh4.googleusercontent.com/proxy/wY0TD2dKCo-gTEC0RNKVGsg2bE6GhY0ZxRWO6STymhhBgCUK-FBq9lTO6nU3V0Sf-kjznCTU7ZrUSMyD0RjWJPHt-iN5HhRN3t4jO3VVliI3QdYuuZoTg17tPA")
    private String profileImageUrl;
}


