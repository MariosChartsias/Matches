package com.marioschartsias.matches.api.dto;

import com.marioschartsias.matches.domain.MatchOdd;

import java.math.BigDecimal;

public record MatchOddResponse(
        Long id,
        Long matchId,
        String specifier,
        BigDecimal odd
) {
    public static MatchOddResponse from(MatchOdd matchOdd) {
        return new MatchOddResponse(
                matchOdd.getId(),
                matchOdd.getMatch().getId(),
                matchOdd.getSpecifier(),
                matchOdd.getOdd()
        );
    }
}

