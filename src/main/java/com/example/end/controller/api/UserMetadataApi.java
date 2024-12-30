//package com.example.end.controller.api;
//
//import com.example.end.dto.UserDetailsDto;
//import com.example.end.validation.dto.ValidationErrorsDto;
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.Parameter;
//import io.swagger.v3.oas.annotations.media.Content;
//import io.swagger.v3.oas.annotations.media.Schema;
//import io.swagger.v3.oas.annotations.responses.ApiResponse;
//import io.swagger.v3.oas.annotations.responses.ApiResponses;
//import io.swagger.v3.oas.annotations.tags.Tag;
//import org.springframework.http.HttpStatus;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.util.List;
//
//@Tag(name = "User Metadata", description = "Operations for managing user metadata")
//@RequestMapping("/api/metadata/{userId}")
//public interface UserMetadataApi {
//
//    @Operation(
//            summary = "Upload a profile image",
//            description = "Allows a user to upload their profile image",
//            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
//                    content = @Content(
//                            mediaType = "multipart/form-data",
//                            schema = @Schema(
//                                    type = "object",
//                                    properties = {
//                                            @Schema(name = "file", type = "string", format = "binary", description = "Profile image file")
//                                    }
//                            )
//                    )
//            )
//    )
//    @PostMapping("/profileImage")
//    @ResponseStatus(HttpStatus.CREATED)
//    UserDetailsDto uploadProfilePhoto(
//            @Parameter(description = "ID of the user", required = true)
//            @PathVariable("userId") Long userId,
//
//            @RequestPart("file") MultipartFile file
//    );
//
//    @Operation(
//            summary = "Upload multiple portfolio images",
//            description = "Allows a user to upload multiple portfolio images",
//            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
//                    content = @Content(
//                            mediaType = "multipart/form-data",
//                            schema = @Schema(
//                                    type = "object",
//                                    properties = {
//                                            @Schema(
//                                                    name = "files",
//                                                    type = "array",
//                                                    description = "Portfolio image files",
//                                                    additionalPropertiesSchema = @Schema(
//                                                            type = "string",
//                                                            format = "binary"
//                                                    )
//                                            )
//                                    }
//                            )
//                    )
//            )
//    )
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "201", description = "Portfolio images uploaded successfully",
//                    content = @Content(mediaType = "application/json",
//                            schema = @Schema(implementation = UserDetailsDto.class))),
//            @ApiResponse(responseCode = "400", description = "Invalid files or validation error",
//                    content = @Content(mediaType = "application/json",
//                            schema = @Schema(implementation = ValidationErrorsDto.class)))
//    })
//    @PostMapping("/portfolioImages")
//    @ResponseStatus(HttpStatus.CREATED)
//    UserDetailsDto uploadPortfolioPhotos(
//            @Parameter(description = "ID of the user", required = true)
//            @PathVariable("userId") Long userId,
//
//            @RequestPart("files") List<MultipartFile> files
//    );
//
//    @Operation(summary = "Delete profile image")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "204", description = "Profile image deleted successfully"),
//            @ApiResponse(responseCode = "404", description = "Profile image not found")
//    })
//    @DeleteMapping("/profileImage")
//    @ResponseStatus(HttpStatus.NO_CONTENT)
//    void deleteProfilePhoto(
//            @Parameter(description = "ID of the user", required = true)
//            @PathVariable("userId") Long userId
//    );
//
//    @Operation(summary = "Delete a portfolio image")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "204", description = "Portfolio image deleted successfully"),
//            @ApiResponse(responseCode = "404", description = "Portfolio image not found")
//    })
//    @DeleteMapping("/portfolioImage/{photoId}")
//    @ResponseStatus(HttpStatus.NO_CONTENT)
//    void deletePortfolioPhoto(
//            @Parameter(description = "ID of the user", required = true)
//            @PathVariable("userId") Long userId,
//
//            @Parameter(description = "ID of the portfolio image to delete", required = true)
//            @PathVariable("photoId") Long photoId
//    );
//}
