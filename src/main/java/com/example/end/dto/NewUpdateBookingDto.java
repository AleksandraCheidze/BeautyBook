package com.example.end.dto;

import com.example.end.models.BookingStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewUpdateBookingDto {

    @Schema(description = "Unique identifier of the booking", example = "1")
    private Long id;

    @NotNull(message = "Status cannot be null")
    @Schema(description = "Status of the booking")
    private BookingStatus status;

}
