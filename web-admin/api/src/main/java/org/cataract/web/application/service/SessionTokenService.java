package org.cataract.web.application.service;

import org.cataract.web.domain.AppToken;
import org.cataract.web.domain.Role;
import org.cataract.web.domain.SessionToken;
import org.cataract.web.domain.User;
import org.cataract.web.domain.exception.InvalidTokenException;
import org.paseto4j.commons.PasetoException;
import java.util.Optional;

public interface SessionTokenService {

    SessionToken saveTokenForUser(User user, String token);

    void invalidateToken(String tokenValue);

    Optional<String> encrypt(AppToken token);

    Optional<AppToken> decrypt(String token);

    AppToken validateAccessToken(String token) throws InvalidTokenException, PasetoException;

    String generateToken(String username, Role role);

    Optional<SessionToken> getValidToken(String refreshToken);

    String replaceToken(SessionToken oldToken, String username);

    int deleteExpiredTokens();
}
