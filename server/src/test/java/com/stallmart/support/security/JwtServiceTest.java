package com.stallmart.support.security;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class JwtServiceTest {

    @Autowired
    private JwtService jwtService;

    @Test
    void shouldParseUserId_whenAccessTokenIsGenerated() {
        String token = jwtService.generateAccessToken(12L, "VENDOR", true);

        assertThat(jwtService.isExpired(token)).isFalse();
        assertThat(jwtService.getUserId(token)).isEqualTo(12L);
        assertThat(jwtService.parseToken(token).get("role", String.class)).isEqualTo("VENDOR");
    }
}
