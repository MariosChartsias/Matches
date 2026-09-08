package com.marioschartsias.matches.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import org.hibernate.annotations.BatchSize;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity(name = "SportMatch")
@Table(name = "matches")
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String description;

    @Column(name = "match_date", nullable = false)
    private LocalDate matchDate;

    @Column(name = "match_time", nullable = false)
    private LocalTime matchTime;

    @Column(name = "team_a", nullable = false, length = 100)
    private String teamA;

    @Column(name = "team_b", nullable = false, length = 100)
    private String teamB;

    @jakarta.persistence.Enumerated(jakarta.persistence.EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Sport sport;

    @OneToMany(mappedBy = "match", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("id ASC")
    @BatchSize(size = 100)
    private List<MatchOdd> odds = new ArrayList<>();

    protected Match() {
    }

    public Match(String description, LocalDate matchDate, LocalTime matchTime,
                 String teamA, String teamB, Sport sport) {
        update(description, matchDate, matchTime, teamA, teamB, sport);
    }

    public void update(String description, LocalDate matchDate, LocalTime matchTime,
                       String teamA, String teamB, Sport sport) {
        this.description = description.trim();
        this.matchDate = matchDate;
        this.matchTime = matchTime;
        this.teamA = teamA.trim();
        this.teamB = teamB.trim();
        this.sport = sport;
    }

    public Long getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public LocalDate getMatchDate() {
        return matchDate;
    }

    public LocalTime getMatchTime() {
        return matchTime;
    }

    public String getTeamA() {
        return teamA;
    }

    public String getTeamB() {
        return teamB;
    }

    public Sport getSport() {
        return sport;
    }

    public List<MatchOdd> getOdds() {
        return Collections.unmodifiableList(odds);
    }
}
