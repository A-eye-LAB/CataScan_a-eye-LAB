package org.cataract.web.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cataract.web.application.service.AuthenticationService;
import org.cataract.web.application.service.SessionTokenService;
import org.cataract.web.domain.AppToken;
import org.cataract.web.domain.Role;
import org.cataract.web.domain.exception.InvalidTokenException;
import org.paseto4j.commons.PasetoException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class PasetoAuthenticationFilter extends OncePerRequestFilter {

    private final SessionTokenService sessionTokenService;
    private final AuthenticationService authenticationService;
    private final HandlerExceptionResolver handlerExceptionResolver;

    private static final long REFRESH_THRESHOLD = 5 * 60 * 1000; // 5 minutes in milliseconds

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) {

        Optional<String> optionalToken = extractToken(request);

        try {

            if (optionalToken.isPresent()) {
                String token = optionalToken.get();
                AppToken appToken = sessionTokenService.validateAccessToken(token);
                if (isTokenExpiringSoon(appToken)) {
                    sessionTokenService.invalidateToken(token);
                    String newToken = sessionTokenService.generateToken(appToken.getUsername(), Role.valueOf(appToken.getRole()));
                    response.setHeader("Authorization", "Bearer " + newToken);
                }
                UserDetails userDetails = authenticationService.loadUserByUsername(appToken.getUsername());
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                if (userDetails != null && authentication == null) {
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            if (e instanceof PasetoException) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                log.error("user used expired token", e);
            } else if (e instanceof InvalidTokenException) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                log.error("invalid token attempted", e);
            } else {
                log.error("error at paseto authentication filter", e);
            }
            handlerExceptionResolver.resolveException(request, response, null, e);
        } 
    }

    private Optional<String> extractToken(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            return Optional.of(token.substring(7));
        } else {
            return Optional.empty();
        }
    }

    private boolean isTokenExpiringSoon(AppToken appToken) {
        long currentTime = System.currentTimeMillis();
        long expiryTime = appToken.getExpiryDate().toEpochMilli();
        return expiryTime - currentTime <= REFRESH_THRESHOLD;
    }
}