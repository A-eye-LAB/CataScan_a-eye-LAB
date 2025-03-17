package org.cataract.web.presentation.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cataract.web.application.service.UserService;
import org.cataract.web.presentation.dto.requests.UpdateUserRequestDto;
import org.cataract.web.presentation.dto.responses.ErrorResponseDto;
import org.cataract.web.presentation.dto.responses.UserResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Validated
@Slf4j
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<UserResponseDto> getUserData(Authentication authentication) {
        String username = authentication.getName();
        log.info("[{}] Received request to get user data", username);
        UserResponseDto userResponseDto = userService.getUserByUsername(username);
        return ResponseEntity.status(HttpStatus.OK).body(userResponseDto);
    }

    @PatchMapping
    public ResponseEntity<ErrorResponseDto> updateUserData(Authentication authentication,
                                                           @RequestBody @Valid UpdateUserRequestDto updateUserRequestDto) {
        String username = authentication.getName();
        log.info("[{}] Received request to update user data", username);
        ErrorResponseDto errorResponseDto = userService.updateSelfUser(username, updateUserRequestDto);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(errorResponseDto);
    }
}
