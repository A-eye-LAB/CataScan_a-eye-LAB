package org.cataract.web.presentation.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cataract.web.application.service.AuthenticationService;
import org.cataract.web.application.service.SessionTokenService;
import org.cataract.web.domain.AppToken;
import org.cataract.web.domain.SessionToken;
import org.cataract.web.domain.exception.InvalidTokenException;
import org.cataract.web.presentation.dto.ResponseDto;
import org.cataract.web.presentation.dto.UserLoginDto;
import org.cataract.web.presentation.dto.requests.RefreshTokenRequestDto;
import org.cataract.web.presentation.dto.responses.ErrorResponseDto;
import org.cataract.web.presentation.dto.responses.LogoutResponse;
import org.cataract.web.presentation.dto.responses.TokenResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RequestMapping("/auth")
@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    private final SessionTokenService sessionTokenService;

    @PostMapping("/login")
    public ResponseEntity<ResponseDto> authenticate(@Valid @RequestBody UserLoginDto loginUserDto) {
        log.info("User {} attempting login", loginUserDto.getUsername());
        try {
            TokenResponse tokenResponse = authenticationService.login(loginUserDto);
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    loginUserDto.getUsername(), loginUserDto.getPassword());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.info("User {} login successful", loginUserDto.getUsername());
            return ResponseEntity.ok(tokenResponse);
        } catch (BadCredentialsException e) {
            log.warn("User {} login failed due to bad credentials", loginUserDto.getUsername(), e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponseDto("Invalid username or password"));
        } catch (UsernameNotFoundException e) {
            log.warn("User {} login failed - user not found", loginUserDto.getUsername(), e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponseDto("User not found"));
        } catch (Exception e) {
            log.error("User {} login failed due to an unexpected error", loginUserDto.getUsername(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponseDto("Internal server error"));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<ResponseDto> logout(@RequestHeader("Authorization") String authHeader,
                                                 Authentication authentication) {
        UserDetails authenticationDetails = (UserDetails) authentication.getPrincipal();
        log.info("Received request to log out by user {}", authenticationDetails.getUsername());
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String tokenValue = authHeader.substring(7);
            sessionTokenService.invalidateToken(tokenValue);
            log.info("Session token for user {} deleted", authenticationDetails.getUsername());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponseDto("no valid token found in header"));
        }
        log.info("{} logged out, token invalidated", authenticationDetails.getUsername());
        return ResponseEntity.ok(new LogoutResponse("logged out"));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ResponseDto> refreshAccessToken(@RequestBody RefreshTokenRequestDto request) {
        String refreshToken = request.getRefreshToken();

        Optional<SessionToken> optionalToken = sessionTokenService.getValidToken(refreshToken);
        if (optionalToken.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponseDto("token not found"));
        }
        AppToken appToken = sessionTokenService.decrypt(optionalToken.get().getTokenValue()).orElseThrow(InvalidTokenException::new);
        String username = appToken.getUsername();
        log.info("[{}] Request to refresh Token", username);
        String replacedToken = sessionTokenService.replaceToken(optionalToken.get(), username);
        return ResponseEntity.ok(new TokenResponse(replacedToken));
    }
}
