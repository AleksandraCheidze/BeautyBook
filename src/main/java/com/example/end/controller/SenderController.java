package com.example.end.controller;

import com.example.end.controller.api.SenderApi;
import com.example.end.dto.AdminMessageRequest;
import com.example.end.service.SenderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/messages")
@Tag(name = "Messages", description = "API endpoints for sending messages to administrators")
public class SenderController implements SenderApi {
    private final SenderService senderService;

    @Override
    @PostMapping("/message-admin")
    @Operation(summary = "Send Message to Admin (Public)", description = "Send a message to administrators with user details. Access: All users")
    public ResponseEntity<Void> sendMessageToAdmin(AdminMessageRequest messageRequest) {
        try {
            senderService.sendMessageToAdmin(
                    messageRequest.getEmail(),
                    messageRequest.getPhone(),
                    messageRequest.getFirstName(),
                    messageRequest.getLastName(),
                    messageRequest.getMessage());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
}
