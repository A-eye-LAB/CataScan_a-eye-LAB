package org.cataract.web.infra;

import org.cataract.web.domain.SessionToken;
import org.cataract.web.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SessionTokenRepository extends JpaRepository<SessionToken, Long> {
    Optional<SessionToken> findByTokenValue(String tokenValue);

    List<SessionToken> findAllByUser(User user);

    int deleteByExpiresAtBefore(OffsetDateTime expiresAt);
}
