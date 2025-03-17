package org.cataract.web.application.service;

import org.cataract.web.presentation.dto.UserLoginDto;
import org.cataract.web.presentation.dto.responses.TokenResponse;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface AuthenticationService extends UserDetailsService {

    TokenResponse login(UserLoginDto loginUserDto);

}
