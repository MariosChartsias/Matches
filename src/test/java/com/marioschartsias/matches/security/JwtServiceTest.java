package com.marioschartsias.matches.security;

import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JwtServiceTest {

    private static final String SECRET = "bWF0Y2hlcy1hcGktbG9jYWwtand0LXNlY3JldC1rZXk=";

    @Test
    void createsATokenThatReturnsItsUsername() {
        JwtService jwtService = new JwtService(SECRET, 60);

        String token = jwtService.createToken("admin");

        assertEquals("admin", jwtService.usernameFrom(token));
    }

    @Test
    void rejectsATokenSignedWithAnotherKey() {
        JwtService issuingService = new JwtService(SECRET, 60);
        JwtService verifyingService = new JwtService("YW5vdGhlci0zMi1ieXRlcy1sb2NhbC1qd3Qtc2lnbmluZy1rZXk=", 60);

        assertThrows(JwtException.class, () -> verifyingService.usernameFrom(issuingService.createToken("admin")));
    }
}
