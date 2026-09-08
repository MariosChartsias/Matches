package com.marioschartsias.matches.repository;

import com.marioschartsias.matches.domain.MatchOdd;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MatchOddRepository extends JpaRepository<MatchOdd, Long> {

    List<MatchOdd> findByMatchIdOrderByIdAsc(Long matchId);

    Optional<MatchOdd> findByIdAndMatchId(Long id, Long matchId);

    boolean existsByMatchIdAndSpecifierIgnoreCase(Long matchId, String specifier);

    boolean existsByMatchIdAndSpecifierIgnoreCaseAndIdNot(Long matchId, String specifier, Long id);
}

