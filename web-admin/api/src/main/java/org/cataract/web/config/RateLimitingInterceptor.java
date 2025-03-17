package org.cataract.web.config;

import io.github.bucket4j.*;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitingInterceptor implements HandlerInterceptor {
    private final Map<String, Bucket> ipBuckets = new ConcurrentHashMap<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        String clientIp = request.getHeader("X-Forwarded-For");
        if (clientIp == null || clientIp.isBlank()) {
            clientIp = request.getRemoteAddr();
        }
        Bucket bucket = ipBuckets.computeIfAbsent(clientIp, k -> createNewBucket());

        if (bucket.tryConsume(1)) {
            return true; // Allow request to proceed
        }
        response.sendError(429, "Too Many Requests - Rate limit exceeded");
        return false;
    }

    private Bucket createNewBucket() {
        Bandwidth limit = Bandwidth.builder().capacity(10).refillGreedy(10, Duration.ofSeconds(60)).build();
        return Bucket.builder().addLimit(limit).build();
    }
}