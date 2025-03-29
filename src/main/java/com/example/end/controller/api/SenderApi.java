package com.example.end.controller.api;

import com.example.end.dto.AdminMessageRequest;
import com.example.end.dto.ErrorResponse;
import com.example.end.validation.dto.ValidationErrorsDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/messages")
@Tag(name = "Messages", description = "API endpoints for sending messages to administrators")
public interface SenderApi {

    @Operation(summary = "Send Message to Admin (Public)", description = "Send a message to administrators with user details. Access: All users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Message sent successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ValidationErrorsDto.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/message-admin")
    ResponseEntity<Void> sendMessageToAdmin(
            @Parameter(description = "Message details", required = true) @RequestBody @Valid AdminMessageRequest messageRequest);
}
