package com.example.end.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PortfolioImageDto {

    @NotNull
    @Size(min = 1)
    @Schema(description = "Portfolio image URLs of the user", example = "[\"https://example.com/image1.jpg\",\"https://example.com/image2.jpg\"]")
    private List<String> portfolioImageUrls;

    private Long id;

    private String url;

}
