package org.cataract.web.config;

import lombok.extern.slf4j.Slf4j;
import org.cataract.web.application.service.SessionTokenService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SessionTokenCleanScheduler {

    private final SessionTokenService sessionTokenService;

    @Value("${app.token.cleanup.cron}")
    private String tokenCleanupCronExpression;

    public SessionTokenCleanScheduler(SessionTokenService sessionTokenService) {
        this.sessionTokenService = sessionTokenService;
    }

    @Scheduled(cron = "${app.token.cleanup.cron}")
    public void cleanupExpiredTokens() {
        int deletedCount = sessionTokenService.deleteExpiredTokens();
        log.info("Scheduler ran with cron '{}', deleted {} expired events.", tokenCleanupCronExpression, deletedCount);
    }
}
