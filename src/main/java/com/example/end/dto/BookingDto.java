package com.example.end.dto;

import com.example.end.models.BookingStatus;
import com.example.end.models.Procedure;
import com.example.end.models.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO representing a booking")
public class BookingDto {

    @Schema(accessMode = Schema.AccessMode.READ_ONLY ,description = "Unique identifier of the booking", example = "1")
    private Long id;

    @NotNull(message = "Client ID cannot be null")
    @Schema(description = "Client ID associated with the booking", example = "1")
    private Long clientId;

    @NotNull(message = "Master ID cannot be null")
    @Schema(description = "Master ID associated with the booking", example = "1")
    private Long masterId;

    @NotNull(message = "Procedure ID cannot be null")
    @Schema(description = "Procedure ID associated with the booking", example = "Haircut")
    private Long procedureId;

    @NotNull(message = "Status cannot be null")
    @Schema(description = "Status of the booking")
    private BookingStatus status;

    @NotNull(message = "Date and time cannot be null")
    @Schema(description = "Date and time of the booking", example = "2024-03-16T10:00:00")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private String dateTime;

}
