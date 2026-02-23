CREATE TABLE developers (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE publishers (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE genres (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE publications (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE games (
    id SERIAL PRIMARY KEY,
    title VARCHAR(150) NOT NULL,
    release_year INT CHECK (release_year >= 1950 AND release_year <= 2100),
    developer_id INT REFERENCES developers(id) ON DELETE SET NULL,
    publisher_id INT REFERENCES publishers(id) ON DELETE SET NULL,
    CONSTRAINT uq_game_title_year UNIQUE (title, release_year)
);

CREATE TABLE games_genres (
    game_id INT NOT NULL REFERENCES games(id) ON DELETE CASCADE,
    genre_id INT NOT NULL REFERENCES genres(id) ON DELETE CASCADE,
    PRIMARY KEY (game_id, genre_id)
);

CREATE TABLE reviews (
    id SERIAL PRIMARY KEY,
    game_id INT NOT NULL REFERENCES games(id) ON DELETE CASCADE,
    publication_id INT NOT NULL REFERENCES publications(id) ON DELETE CASCADE,
    score INT NOT NULL CHECK (score >= 0 AND score <= 100),
    summary TEXT,
    CONSTRAINT uq_review_game_pub UNIQUE (game_id, publication_id)
);

CREATE INDEX idx_games_developer_id ON games(developer_id);
CREATE INDEX idx_games_publisher_id ON games(publisher_id);
CREATE INDEX idx_games_title ON games(title);
CREATE INDEX idx_games_release_year ON games(release_year);

CREATE INDEX idx_reviews_game_id ON reviews(game_id);
CREATE INDEX idx_reviews_publication_id ON reviews(publication_id);
CREATE INDEX idx_reviews_score ON reviews(score);

