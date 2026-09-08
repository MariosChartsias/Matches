package com.marioschartsias.matches.service;

import com.marioschartsias.matches.api.dto.MatchOddRequest;
import com.marioschartsias.matches.api.dto.MatchOddResponse;
import com.marioschartsias.matches.domain.Match;
import com.marioschartsias.matches.domain.MatchOdd;
import com.marioschartsias.matches.exception.DuplicateResourceException;
import com.marioschartsias.matches.exception.ResourceNotFoundException;
import com.marioschartsias.matches.repository.MatchOddRepository;
import com.marioschartsias.matches.repository.MatchRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class MatchOddService {

    private final MatchRepository matchRepository;
    private final MatchOddRepository matchOddRepository;

    public MatchOddService(MatchRepository matchRepository, MatchOddRepository matchOddRepository) {
        this.matchRepository = matchRepository;
        this.matchOddRepository = matchOddRepository;
    }

    public List<MatchOddResponse> findAll(Long matchId) {
        requireMatch(matchId);
        return matchOddRepository.findByMatchIdOrderByIdAsc(matchId).stream()
                .map(MatchOddResponse::from)
                .toList();
    }

    public MatchOddResponse findById(Long matchId, Long oddId) {
        return MatchOddResponse.from(requireOdd(matchId, oddId));
    }

    @Transactional
    public MatchOddResponse create(Long matchId, MatchOddRequest request) {
        Match match = requireMatch(matchId);
        String specifier = MatchOdd.normalize(request.specifier());
        if (matchOddRepository.existsByMatchIdAndSpecifierIgnoreCase(matchId, specifier)) {
            throw duplicateSpecifier(matchId, specifier);
        }

        MatchOdd matchOdd = new MatchOdd(match, specifier, request.odd());
        return MatchOddResponse.from(matchOddRepository.save(matchOdd));
    }

    @Transactional
    public MatchOddResponse update(Long matchId, Long oddId, MatchOddRequest request) {
        MatchOdd matchOdd = requireOdd(matchId, oddId);
        String specifier = MatchOdd.normalize(request.specifier());
        if (matchOddRepository.existsByMatchIdAndSpecifierIgnoreCaseAndIdNot(matchId, specifier, oddId)) {
            throw duplicateSpecifier(matchId, specifier);
        }

        matchOdd.update(specifier, request.odd());
        return MatchOddResponse.from(matchOdd);
    }

    @Transactional
    public void delete(Long matchId, Long oddId) {
        matchOddRepository.delete(requireOdd(matchId, oddId));
    }

    private Match requireMatch(Long matchId) {
        return matchRepository.findById(matchId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Match with id " + matchId + " was not found"
                ));
    }

    private MatchOdd requireOdd(Long matchId, Long oddId) {
        return matchOddRepository.findByIdAndMatchId(oddId, matchId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Odd with id " + oddId + " was not found for match " + matchId
                ));
    }

    private DuplicateResourceException duplicateSpecifier(Long matchId, String specifier) {
        return new DuplicateResourceException(
                "Specifier " + specifier + " already exists for match " + matchId
        );
    }
}

