package com.marioschartsias.matches.api.dto;

import com.marioschartsias.matches.domain.Match;
import com.marioschartsias.matches.domain.Sport;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record MatchResponse(
        Long id,
        String description,
        LocalDate matchDate,
        LocalTime matchTime,
        String teamA,
        String teamB,
        Sport sport,
        List<MatchOddResponse> odds
) {
    public static MatchResponse from(Match match) {
        return new MatchResponse(
                match.getId(),
                match.getDescription(),
                match.getMatchDate(),
                match.getMatchTime(),
                match.getTeamA(),
                match.getTeamB(),
                match.getSport(),
                match.getOdds().stream().map(MatchOddResponse::from).toList()
        );
    }
}

