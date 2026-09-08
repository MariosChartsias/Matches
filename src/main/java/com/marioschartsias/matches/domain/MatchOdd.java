package com.marioschartsias.matches.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.math.BigDecimal;
import java.util.Locale;

@Entity
@Table(
        name = "match_odds",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_match_odds_match_specifier",
                columnNames = {"match_id", "specifier"}
        )
)
public class MatchOdd {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "match_id", nullable = false)
    private Match match;

    @Column(nullable = false, length = 50)
    private String specifier;

    @Column(nullable = false, precision = 10, scale = 3)
    private BigDecimal odd;

    protected MatchOdd() {
    }

    public MatchOdd(Match match, String specifier, BigDecimal odd) {
        this.match = match;
        update(specifier, odd);
    }

    public void update(String specifier, BigDecimal odd) {
        this.specifier = normalize(specifier);
        this.odd = odd;
    }

    public static String normalize(String specifier) {
        return specifier.trim().toUpperCase(Locale.ROOT);
    }

    public Long getId() {
        return id;
    }

    public Match getMatch() {
        return match;
    }

    public String getSpecifier() {
        return specifier;
    }

    public BigDecimal getOdd() {
        return odd;
    }
}

