package com.example.end.controller.api;

import com.example.end.dto.AdminMessageRequest;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/api/messages")
public interface SenderApi {
    @Operation(summary = "Send message to admin", description = "Send message to admin with user details.")
    @PostMapping("/message-admin")
    ResponseEntity<Void> sendMessageToAdmin(@RequestBody @Valid AdminMessageRequest messageRequest);
}


