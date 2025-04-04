package com.example.end.controller;

import com.example.end.controller.api.SenderApi;
import com.example.end.dto.AdminMessageRequest;
import com.example.end.service.SenderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class SenderController implements SenderApi {
    private final SenderService senderService;

    @Override
    public ResponseEntity<Void> sendMessageToAdmin(AdminMessageRequest messageRequest) {
        try {
            senderService.sendMessageToAdmin(
                    messageRequest.getEmail(),
                    messageRequest.getPhone(),
                    messageRequest.getFirstName(),
                    messageRequest.getLastName(),
                    messageRequest.getMessage()
            );
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
}
