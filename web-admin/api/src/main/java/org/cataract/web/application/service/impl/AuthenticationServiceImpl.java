package org.cataract.web.application.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cataract.web.application.service.AuthenticationService;
import org.cataract.web.application.service.SessionTokenService;
import org.cataract.web.domain.Role;
import org.cataract.web.domain.User;
import org.cataract.web.domain.exception.UserNotFoundException;
import org.cataract.web.infra.UserRepository;
import org.cataract.web.presentation.dto.UserLoginDto;
import org.cataract.web.presentation.dto.responses.TokenResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;

    private final SessionTokenService sessionTokenService;

    public TokenResponse login(UserLoginDto loginUserDto) {
        User user = userRepository.findByUsernameAndRoleNot(loginUserDto.getUsername(), Role.DELETED)
                .orElseThrow(() -> new BadCredentialsException("User not found or invalid password"));

        if (!BCrypt.checkpw(loginUserDto.getPassword(), user.getPassword())) {
            log.debug("[{}] user login failed with wrong password", loginUserDto.getUsername());
            throw new BadCredentialsException("User not found or invalid password");
        }
        String tokenStr = sessionTokenService.generateToken(user.getUsername(), user.getRole());
        sessionTokenService.saveTokenForUser(user, tokenStr);
        log.debug("[{}] login successful and token created", user.getUsername());
        return new TokenResponse(tokenStr);

    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsernameAndRoleNot(username,Role.DELETED)
                .orElseThrow(UserNotFoundException::new);
        log.debug("[{}] retrieved userdetails by username", user.getUsername());
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                Collections.singleton(new SimpleGrantedAuthority("ROLE_" +user.getRole().toString()))
        );
    }

}
