CREATE TABLE matches (
    id BIGSERIAL PRIMARY KEY,
    description VARCHAR(200) NOT NULL,
    match_date DATE NOT NULL,
    match_time TIME NOT NULL,
    team_a VARCHAR(100) NOT NULL,
    team_b VARCHAR(100) NOT NULL,
    sport VARCHAR(20) NOT NULL,
    CONSTRAINT chk_matches_different_teams CHECK (LOWER(team_a) <> LOWER(team_b)),
    CONSTRAINT chk_matches_sport CHECK (sport IN ('FOOTBALL', 'BASKETBALL'))
);

CREATE TABLE match_odds (
    id BIGSERIAL PRIMARY KEY,
    match_id BIGINT NOT NULL,
    specifier VARCHAR(50) NOT NULL,
    odd NUMERIC(10, 3) NOT NULL,
    CONSTRAINT fk_match_odds_match
        FOREIGN KEY (match_id) REFERENCES matches (id) ON DELETE CASCADE,
    CONSTRAINT uk_match_odds_match_specifier UNIQUE (match_id, specifier),
    CONSTRAINT chk_match_odds_positive CHECK (odd > 0)
);

CREATE INDEX idx_matches_date_time ON matches (match_date, match_time);
CREATE INDEX idx_match_odds_match_id ON match_odds (match_id);

