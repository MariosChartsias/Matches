package com.marioschartsias.matches.service;

import com.marioschartsias.matches.api.dto.MatchOddRequest;
import com.marioschartsias.matches.api.dto.MatchOddResponse;
import com.marioschartsias.matches.domain.Match;
import com.marioschartsias.matches.domain.MatchOdd;
import com.marioschartsias.matches.domain.Sport;
import com.marioschartsias.matches.exception.DuplicateResourceException;
import com.marioschartsias.matches.exception.ResourceNotFoundException;
import com.marioschartsias.matches.repository.MatchOddRepository;
import com.marioschartsias.matches.repository.MatchRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MatchOddServiceTest {

    @Mock
    private MatchRepository matchRepository;

    @Mock
    private MatchOddRepository matchOddRepository;

    @InjectMocks
    private MatchOddService matchOddService;

    @Test
    void createsAndNormalizesAnOdd() {
        Match match = match(1L);
        when(matchRepository.findById(1L)).thenReturn(Optional.of(match));
        when(matchOddRepository.existsByMatchIdAndSpecifierIgnoreCase(1L, "X")).thenReturn(false);
        when(matchOddRepository.save(any(MatchOdd.class))).thenAnswer(invocation -> {
            MatchOdd saved = invocation.getArgument(0);
            ReflectionTestUtils.setField(saved, "id", 10L);
            return saved;
        });

        MatchOddResponse created = matchOddService.create(
                1L,
                new MatchOddRequest(" x ", new BigDecimal("1.500"))
        );

        assertThat(created.id()).isEqualTo(10L);
        assertThat(created.matchId()).isEqualTo(1L);
        assertThat(created.specifier()).isEqualTo("X");
        assertThat(created.odd()).isEqualByComparingTo("1.500");
    }

    @Test
    void rejectsADuplicateSpecifier() {
        Match match = match(1L);
        when(matchRepository.findById(1L)).thenReturn(Optional.of(match));
        when(matchOddRepository.existsByMatchIdAndSpecifierIgnoreCase(1L, "X")).thenReturn(true);

        assertThatThrownBy(() -> matchOddService.create(
                1L,
                new MatchOddRequest("X", new BigDecimal("1.80"))
        ))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("Specifier X already exists for match 1");
        verify(matchOddRepository, never()).save(any());
    }

    @Test
    void doesNotReturnAnOddFromAnotherMatch() {
        when(matchOddRepository.findByIdAndMatchId(7L, 2L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> matchOddService.findById(2L, 7L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Odd with id 7 was not found for match 2");
    }

    @Test
    void updatesAnExistingOdd() {
        MatchOdd existing = odd(7L, match(2L), "1", "1.700");
        when(matchOddRepository.findByIdAndMatchId(7L, 2L)).thenReturn(Optional.of(existing));
        when(matchOddRepository.existsByMatchIdAndSpecifierIgnoreCaseAndIdNot(2L, "2", 7L))
                .thenReturn(false);

        MatchOddResponse updated = matchOddService.update(
                2L,
                7L,
                new MatchOddRequest("2", new BigDecimal("2.150"))
        );

        assertThat(updated.specifier()).isEqualTo("2");
        assertThat(updated.odd()).isEqualByComparingTo("2.150");
    }

    private Match match(Long id) {
        Match match = new Match(
                "OSFP - PAO",
                LocalDate.of(2026, 9, 10),
                LocalTime.of(20, 30),
                "OSFP",
                "PAO",
                Sport.FOOTBALL
        );
        ReflectionTestUtils.setField(match, "id", id);
        return match;
    }

    private MatchOdd odd(Long id, Match match, String specifier, String price) {
        MatchOdd odd = new MatchOdd(match, specifier, new BigDecimal(price));
        ReflectionTestUtils.setField(odd, "id", id);
        return odd;
    }
}

