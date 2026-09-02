package com.marioschartsias.matches.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.marioschartsias.matches.domain.Sport;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalTime;

public record MatchRequest(
        @NotBlank @Size(max = 200) String description,
        @NotNull LocalDate matchDate,
        @NotNull LocalTime matchTime,
        @NotBlank @Size(max = 100) String teamA,
        @NotBlank @Size(max = 100) String teamB,
        @NotNull Sport sport
) {
    @JsonIgnore
    @AssertTrue(message = "teamA and teamB must be different")
    public boolean isTeamPairValid() {
        return teamA == null || teamB == null || !teamA.trim().equalsIgnoreCase(teamB.trim());
    }
}

