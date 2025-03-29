package com.example.end.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(name = "Message", description = "General message from the server")
public class StandardResponseDto {
    @Schema(description = "General message from the server", example = "Resource not found")
    private String message;
}