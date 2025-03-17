package org.cataract.web.application.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.cataract.web.application.service.SessionTokenService;
import org.cataract.web.domain.AppToken;
import org.cataract.web.domain.Role;
import org.cataract.web.domain.SessionToken;
import org.cataract.web.domain.User;
import org.cataract.web.domain.exception.InvalidTokenException;
import org.cataract.web.infra.SessionTokenRepository;
import org.paseto4j.commons.PasetoException;
import org.paseto4j.commons.SecretKey;
import org.paseto4j.commons.Version;
import org.paseto4j.version4.Paseto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class SessionTokenServiceImpl implements SessionTokenService {

    @Value("${app.token.secret}")
    String secret;

    @Value("${app.token.footer}")
    String footer;

    @Value("${app.token.expire.hours}")
    int tokenExpireHours;

    public SessionTokenServiceImpl(SessionTokenRepository sessionTokenRepository) {
        this.sessionTokenRepository = sessionTokenRepository;
    }
    private final SessionTokenRepository sessionTokenRepository;

    @Transactional
    public SessionToken saveTokenForUser(User user, String token) {

        SessionToken sessionToken = SessionToken.builder()
                .tokenValue(token).createdAt(OffsetDateTime.now()).user(user)
                .expiresAt(OffsetDateTime.now().plusHours(tokenExpireHours)).build();
        log.info("token created and saved for [{}]", user.getUsername());
        sessionToken = sessionTokenRepository.save(sessionToken);
        return sessionToken;
    }

    public void invalidateToken(String tokenValue) {
        sessionTokenRepository.findByTokenValue(tokenValue).ifPresent(sessionTokenRepository::delete);
    }

    public Optional<String> encrypt(AppToken token) {
        String payload;
        try {
            payload = mapper().writeValueAsString(token);
            return Optional.of(Paseto.encrypt(key(), payload, footer));
        } catch (PasetoException | JsonProcessingException e) {
            log.error("Failed to encode token: {}", e.getMessage());
            e.printStackTrace();
            return Optional.empty();
        }
    }

    public Optional<AppToken> decrypt(String token) {
        try {
            String payload = Paseto.decrypt(key(), token, footer);
            AppToken appToken = mapper().readValue(payload, AppToken.class);
            if (Instant.now().isAfter(appToken.getExpiryDate())) {
                log.error("[{}] token is expired by: {}", appToken.getUsername(), appToken.getExpiryDate());
                return Optional.empty();
            }
            return Optional.of(appToken);
        } catch (PasetoException | JsonProcessingException e) {
            log.error("Failed to decode token: {}", e.getMessage());
            return Optional.empty();
        } catch (Exception e) {
            log.error("Decrypt failed: {}", e.getMessage());
            return Optional.empty();
        }
    }

    private SecretKey key() {
        return new SecretKey(this.secret.getBytes(StandardCharsets.UTF_8), Version.V4);
    }

    private JsonMapper mapper() {
        JsonMapper mapper = new JsonMapper();
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }

    public AppToken validateAccessToken(String token) throws InvalidTokenException, PasetoException {
        AppToken appToken = decrypt(token).orElseThrow(InvalidTokenException::new);
        String username = appToken.getUsername();
        String role = appToken.getRole();
        Instant instant = Instant.now().plus(tokenExpireHours, ChronoUnit.HOURS);
        return new AppToken(username, role, instant); // Placeholder
    }

    @Override
    public String generateToken(String username, Role role) {

        AppToken appToken = new AppToken(username, role);
        appToken.setExpiryDate(Instant.now().plus(tokenExpireHours, ChronoUnit.HOURS));
        String pasetoToken = encrypt(appToken).orElseThrow(() -> new PasetoException("failed to encrypt token"));
        return pasetoToken;
    }

    @Override
    public Optional<SessionToken> getValidToken(String refreshToken) {
        return sessionTokenRepository.findByTokenValue(refreshToken);
    }

    public String replaceToken(SessionToken sessionToken, String username) {
        User user = sessionToken.getUser();
        sessionTokenRepository.delete(sessionToken);
        log.debug("[{}] Replaced token", user.getUsername());
        return generateToken(username, user.getRole());
    }


    @Transactional
    public int deleteExpiredTokens() {
        OffsetDateTime now = OffsetDateTime.now();
        int deletedCount = sessionTokenRepository.deleteByExpiresAtBefore(now);
        log.info("Deleted {} expired events.", deletedCount);
        return deletedCount;
    }
}
