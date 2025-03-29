package com.example.end.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "AdminMessageRequest", description = "Message to Admin")
public class AdminMessageRequest {
    private String email;
    private String phone;
    private String firstName;
    private String lastName;
    private String message;

}
