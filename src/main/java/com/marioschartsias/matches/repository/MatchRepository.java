package com.marioschartsias.matches.repository;

import com.marioschartsias.matches.domain.Match;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MatchRepository extends JpaRepository<Match, Long> {

    @EntityGraph(attributePaths = "odds")
    @Query("select distinct m from SportMatch m order by m.matchDate, m.matchTime, m.id")
    List<Match> findAllWithOdds();

    @EntityGraph(attributePaths = "odds")
    @Query("select m from SportMatch m where m.id = :id")
    Optional<Match> findByIdWithOdds(@Param("id") Long id);
}

