package com.lefkovitzj.sermonarchive.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class JwtUtilTest {
    private JwtUtil jwtUtil;

    private final String testSecret = "dummy_secret_key_must_be_long_enough_for_jwt_validation_step";
    private  final int testExpiration = 3600000;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();

        ReflectionTestUtils.setField(jwtUtil, "jwtSecret", testSecret);
        ReflectionTestUtils.setField(jwtUtil, "jwtExpirationMiliseconds", testExpiration);

        jwtUtil.init();
    }

    @Test
    void shouldGenerateValidTokenAndGetUser() {
        String originalUsername = "Test User";

        String token = jwtUtil.generateToken(originalUsername);
        assertThat(token).isNotNull();
        // Check structure as a header.payload.signature JWT
        assertThat(token).contains(".");

        boolean isValid = jwtUtil.validateJwtToken(token);
        assertThat(isValid).isTrue();

        String extractedUsername = jwtUtil.getUserFromToken(token);
        assertThat(extractedUsername).isEqualTo(originalUsername);
    }

    @Test
    void shouldReturnFalseForMalformedToken() {
        String malformedToken = "not.a.valid.jwt.token";

        boolean isValid = jwtUtil.validateJwtToken(malformedToken);
        assertThat(isValid).isFalse();
    }

    @Test
    void shouldReturnFalseForExpiredToken() {
        ReflectionTestUtils.setField(jwtUtil, "jwtExpirationMiliseconds", -1);
        String expiredToken = jwtUtil.generateToken("Test User");
        boolean isValid = jwtUtil.validateJwtToken(expiredToken);

        assertThat(isValid).isFalse();
    }
}
