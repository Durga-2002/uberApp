package com.codingshuttle.project.uber.uberApp.Security;

import com.codingshuttle.project.uber.uberApp.entities.enums.Role;
import com.codingshuttle.project.uber.uberApp.entities.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JWTServiceTest {

    private static final String SECRET = "unit-test-secret-key-that-is-long-enough-for-hs256-algorithm";

    private JWTService jwtService;
    private User user;

    @BeforeEach
    void setUp() {
        jwtService = new JWTService();
        ReflectionTestUtils.setField(jwtService, "jwtSecretKey", SECRET);

        user = new User();
        user.setId(42L);
        user.setEmail("rider@example.com");
        user.setRoles(Set.of(Role.RIDER));
    }

    @Test
    void accessTokenCarriesUserIdEmailAndRoles() {
        String token = jwtService.generateAccessToken(user);

        Claims claims = Jwts.parser()
                .verifyWith(jwtService.getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        assertEquals("42", claims.getSubject());
        assertEquals("rider@example.com", claims.get("email"));
        assertEquals(user.getRoles().toString(), claims.get("roles"));
        assertNotNull(claims.getExpiration());
    }

    @Test
    void userIdIsExtractedFromAccessToken() {
        assertEquals(42L, jwtService.getUserIdFromToken(jwtService.generateAccessToken(user)));
    }

    @Test
    void userIdIsExtractedFromRefreshToken() {
        assertEquals(42L, jwtService.getUserIdFromToken(jwtService.generateRefreshToken(user)));
    }

    @Test
    void tamperedTokenIsRejected() {
        String token = jwtService.generateAccessToken(user);
        String tampered = token.substring(0, token.length() - 1)
                + (token.endsWith("A") ? "B" : "A");

        assertThrows(JwtException.class, () -> jwtService.getUserIdFromToken(tampered));
    }
}
