package com.marioschartsias.matches.api.auth;

public record TokenResponse(String accessToken, String tokenType, long expiresInSeconds) {
}
