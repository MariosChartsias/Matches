package com.marioschartsias.matches.service;

import com.marioschartsias.matches.api.dto.MatchRequest;
import com.marioschartsias.matches.api.dto.MatchResponse;
import com.marioschartsias.matches.api.dto.PageResponse;
import com.marioschartsias.matches.domain.Match;
import com.marioschartsias.matches.exception.ResourceNotFoundException;
import com.marioschartsias.matches.repository.MatchRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class MatchService {

    private final MatchRepository matchRepository;

    public MatchService(MatchRepository matchRepository) {
        this.matchRepository = matchRepository;
    }

    public PageResponse<MatchResponse> findAll(Pageable pageable) {
        return PageResponse.from(matchRepository.findAll(pageable).map(MatchResponse::from));
    }

    public MatchResponse findById(Long id) {
        return MatchResponse.from(findMatchWithOdds(id));
    }

    @Transactional
    public MatchResponse create(MatchRequest request) {
        Match match = new Match(
                request.description(),
                request.matchDate(),
                request.matchTime(),
                request.teamA(),
                request.teamB(),
                request.sport()
        );
        return MatchResponse.from(matchRepository.save(match));
    }

    @Transactional
    public MatchResponse update(Long id, MatchRequest request) {
        Match match = findMatchWithOdds(id);
        match.update(
                request.description(),
                request.matchDate(),
                request.matchTime(),
                request.teamA(),
                request.teamB(),
                request.sport()
        );
        return MatchResponse.from(match);
    }

    @Transactional
    public void delete(Long id) {
        Match match = matchRepository.findById(id)
                .orElseThrow(() -> notFound(id));
        matchRepository.delete(match);
    }

    private Match findMatchWithOdds(Long id) {
        return matchRepository.findByIdWithOdds(id)
                .orElseThrow(() -> notFound(id));
    }

    private ResourceNotFoundException notFound(Long id) {
        return new ResourceNotFoundException("Match with id " + id + " was not found");
    }
}

