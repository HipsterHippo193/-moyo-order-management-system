package com.fuchs.oms.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenProviderTest {

    private JwtTokenProvider tokenProvider;
    private static final String SECRET = "this-is-a-very-secure-256-bit-secret-key-for-testing";
    private static final Long EXPIRATION = 3600000L; // 1 hour

    @BeforeEach
    void setUp() {
        tokenProvider = new JwtTokenProvider(SECRET, EXPIRATION);
    }

    @Test
    void generateToken_containsSubjectClaim() {
        String token = tokenProvider.generateToken(1L, "vendor-a");
        assertNotNull(token);

        SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        assertEquals("vendor-a", claims.getSubject());
    }

    @Test
    void generateToken_containsVendorIdClaim() {
        String token = tokenProvider.generateToken(1L, "vendor-a");

        SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        assertEquals(1L, claims.get("vendorId", Long.class));
    }

    @Test
    void generateToken_hasIssuedAt() {
        String token = tokenProvider.generateToken(1L, "vendor-a");

        SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        assertNotNull(claims.getIssuedAt());
    }

    @Test
    void generateToken_hasExpiration() {
        String token = tokenProvider.generateToken(1L, "vendor-a");

        SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        assertNotNull(claims.getExpiration());
        // Expiration should be approximately 1 hour from now
        long expirationDiff = claims.getExpiration().getTime() - claims.getIssuedAt().getTime();
        assertEquals(3600000L, expirationDiff, 1000); // Allow 1 second tolerance
    }

    @Test
    void generateToken_differentVendors_differentTokens() {
        String token1 = tokenProvider.generateToken(1L, "vendor-a");
        String token2 = tokenProvider.generateToken(2L, "vendor-b");

        assertNotEquals(token1, token2);
    }

    @Test
    void validateToken_withValidToken_returnsTrue() {
        String token = tokenProvider.generateToken(1L, "vendor-a");
        assertTrue(tokenProvider.validateToken(token));
    }

    @Test
    void validateToken_withMalformedToken_returnsFalse() {
        assertFalse(tokenProvider.validateToken("not.a.valid.token"));
    }

    @Test
    void validateToken_withEmptyToken_returnsFalse() {
        assertFalse(tokenProvider.validateToken(""));
    }

    @Test
    void validateToken_withNullToken_returnsFalse() {
        assertFalse(tokenProvider.validateToken(null));
    }

    @Test
    void validateToken_withWrongSignature_returnsFalse() {
        // Create a token with a different secret
        JwtTokenProvider otherProvider = new JwtTokenProvider(
                "different-256-bit-secret-key-for-testing-purposes", EXPIRATION);
        String tokenFromOtherProvider = otherProvider.generateToken(1L, "vendor-a");

        assertFalse(tokenProvider.validateToken(tokenFromOtherProvider));
    }

    @Test
    void validateToken_withExpiredToken_returnsFalse() {
        // Create a provider with very short expiration (already expired)
        JwtTokenProvider expiredProvider = new JwtTokenProvider(SECRET, -1000L);
        String expiredToken = expiredProvider.generateToken(1L, "vendor-a");

        assertFalse(tokenProvider.validateToken(expiredToken));
    }

    @Test
    void getVendorIdFromToken_returnsCorrectVendorId() {
        String token = tokenProvider.generateToken(42L, "vendor-test");
        assertEquals(42L, tokenProvider.getVendorIdFromToken(token));
    }

    @Test
    void getUsernameFromToken_returnsCorrectUsername() {
        String token = tokenProvider.generateToken(1L, "vendor-test");
        assertEquals("vendor-test", tokenProvider.getUsernameFromToken(token));
    }
}
