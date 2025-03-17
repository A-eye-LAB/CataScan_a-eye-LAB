package org.cataract.web.domain;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AppToken {

    @Column(nullable = false)
    private String username;
    private String role;
    @Column(updatable = false, nullable = false)
    private Instant expiryDate;

    public AppToken(String username, Role role) {
        this.username = username;
        this.role = role.toString();
    }
}