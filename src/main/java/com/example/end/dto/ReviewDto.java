package com.example.end.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "ReviewDto", description = "Master's reviews")
public class ReviewDto {


    @Schema(accessMode = Schema.AccessMode.READ_ONLY,description = "Unique identifier of the review", example = "1")
    private Long id;

    @NotNull(message = "Client cannot be null")
    @Schema(description = "Client who left the review")
    private Long clientId;

    @NotNull(message = "Master cannot be null")
    @Schema(description = "Master who got the review")
    private Long masterId;

    @NotBlank(message = "Content cannot be blank")
    @Size(max = 1000, message = "Content length must be less than or equal to 1000 characters")
    @Schema(description = "Content of the review")
    private String content;

    @NotNull(message = "Rating cannot be null")
    @Min(value = 1, message = "Rating should be at least 1")
    @Max(value = 5, message = "Rating should be at most 5")
    @Schema(description = "Rating of the review", example = "5")
    private int rating;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Date and time of the review", example = "2024-03-16T10:00:00")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private String createdAt;


}
