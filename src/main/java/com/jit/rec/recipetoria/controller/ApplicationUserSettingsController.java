package com.jit.rec.recipetoria.controller;

import com.jit.rec.recipetoria.controllerapi.ApplicationUserSettingsApi;
import com.jit.rec.recipetoria.dto.ApplicationUserDTO;
import com.jit.rec.recipetoria.entity.Response;
import com.jit.rec.recipetoria.service.ApplicationUserSettingsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/client/settings")
@RequiredArgsConstructor
public class ApplicationUserSettingsController implements ApplicationUserSettingsApi {

    private final ApplicationUserSettingsService applicationUserSettingsService;

    @GetMapping
    public ResponseEntity<Response> showSettings() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(Response.builder()
                        .timeStamp(LocalDateTime.now())
                        .statusCode(HttpStatus.OK.value())
                        .message("User personal information retrieved successfully")
                        .data(Map.of("applicationUserDTO", applicationUserSettingsService.getApplicationUser()))
                        .build());
    }

    @PatchMapping("/personal-info")
    public ResponseEntity<Response> updateApplicationUserInfo(
            @Valid @RequestBody ApplicationUserDTO applicationUserInfo) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(Response.builder()
                        .timeStamp(LocalDateTime.now())
                        .statusCode(HttpStatus.OK.value())
                        .message("User personal information updated successfully")
                        .data(Map.of("updatedApplicationUserDTO",
                                applicationUserSettingsService.updatePersonalInfo(applicationUserInfo)))
                        .build());
    }

    @PatchMapping("/photo")
    public ResponseEntity<Response> updateApplicationUserPhoto(@RequestBody MultipartFile file) throws IOException {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(Response.builder()
                        .timeStamp(LocalDateTime.now())
                        .statusCode(HttpStatus.OK.value())
                        .message("User profile photo updated successfully")
                        .data(Map.of("updatedApplicationUserDTO", applicationUserSettingsService.updatePhoto(file)))
                        .build());
    }

    @DeleteMapping("/photo-delete")
    public ResponseEntity<Response> deleteApplicationUserPhoto() throws IOException {
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body(Response.builder()
                        .timeStamp(LocalDateTime.now())
                        .statusCode(HttpStatus.NO_CONTENT.value())
                        .message("User profile photo deleted successfully")
                        .data(Map.of("updatedApplicationUserDTO", applicationUserSettingsService.deletePhoto()))
                        .build());
    }

    @PatchMapping("/password")
    public ResponseEntity<Response> updateApplicationUserPassword(
            @Valid @RequestBody ApplicationUserDTO applicationUserInfo) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(Response.builder()
                        .timeStamp(LocalDateTime.now())
                        .statusCode(HttpStatus.OK.value())
                        .message("User password updated successfully")
                        .data(Map.of("updatedApplicationUserDTO",
                                applicationUserSettingsService.updatePassword(applicationUserInfo)))
                        .build());
    }

    @DeleteMapping("/account-delete")
    public ResponseEntity<Response> deleteApplicationUser() throws IOException {
        applicationUserSettingsService.deleteApplicationUser();

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body(Response.builder()
                        .timeStamp(LocalDateTime.now())
                        .statusCode(HttpStatus.NO_CONTENT.value())
                        .message("User account deleted successfully")
                        .build());
    }
}