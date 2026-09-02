package com.marioschartsias.matches.service;

import com.marioschartsias.matches.api.dto.MatchRequest;
import com.marioschartsias.matches.api.dto.MatchResponse;
import com.marioschartsias.matches.domain.Match;
import com.marioschartsias.matches.domain.Sport;
import com.marioschartsias.matches.exception.ResourceNotFoundException;
import com.marioschartsias.matches.repository.MatchRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MatchServiceTest {

    @Mock
    private MatchRepository matchRepository;

    @InjectMocks
    private MatchService matchService;

    @Test
    void returnsMatchesInRepositoryOrder() {
        Match first = match(1L, "OSFP - PAO");
        Match second = match(2L, "AEK - ARIS");
        when(matchRepository.findAllWithOdds()).thenReturn(List.of(first, second));

        List<MatchResponse> result = matchService.findAll();

        assertThat(result).extracting(MatchResponse::id).containsExactly(1L, 2L);
        assertThat(result).extracting(MatchResponse::description)
                .containsExactly("OSFP - PAO", "AEK - ARIS");
    }

    @Test
    void createsAndTrimsAMatch() {
        MatchRequest request = new MatchRequest(
                "  OSFP - PAO  ",
                LocalDate.of(2026, 9, 10),
                LocalTime.of(20, 30),
                "  OSFP ",
                " PAO  ",
                Sport.FOOTBALL
        );
        when(matchRepository.save(any(Match.class))).thenAnswer(invocation -> {
            Match saved = invocation.getArgument(0);
            ReflectionTestUtils.setField(saved, "id", 1L);
            return saved;
        });

        MatchResponse created = matchService.create(request);

        assertThat(created.id()).isEqualTo(1L);
        assertThat(created.description()).isEqualTo("OSFP - PAO");
        assertThat(created.teamA()).isEqualTo("OSFP");
        assertThat(created.teamB()).isEqualTo("PAO");
    }

    @Test
    void updatesAnExistingMatch() {
        Match existing = match(8L, "Old description");
        when(matchRepository.findByIdWithOdds(8L)).thenReturn(Optional.of(existing));
        MatchRequest request = new MatchRequest(
                "Updated description",
                LocalDate.of(2026, 11, 4),
                LocalTime.of(19, 0),
                "AEK",
                "ARIS",
                Sport.BASKETBALL
        );

        MatchResponse updated = matchService.update(8L, request);

        assertThat(updated.description()).isEqualTo("Updated description");
        assertThat(updated.sport()).isEqualTo(Sport.BASKETBALL);
        assertThat(updated.matchDate()).isEqualTo(LocalDate.of(2026, 11, 4));
    }

    @Test
    void rejectsAnUpdateForAMissingMatch() {
        when(matchRepository.findByIdWithOdds(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> matchService.update(404L, validRequest()))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Match with id 404 was not found");
        verify(matchRepository, never()).save(any());
    }

    private MatchRequest validRequest() {
        return new MatchRequest(
                "OSFP - PAO",
                LocalDate.of(2026, 9, 10),
                LocalTime.of(20, 30),
                "OSFP",
                "PAO",
                Sport.FOOTBALL
        );
    }

    private Match match(Long id, String description) {
        Match match = new Match(
                description,
                LocalDate.of(2026, 9, 10),
                LocalTime.of(20, 30),
                "OSFP",
                "PAO",
                Sport.FOOTBALL
        );
        ReflectionTestUtils.setField(match, "id", id);
        return match;
    }
}

