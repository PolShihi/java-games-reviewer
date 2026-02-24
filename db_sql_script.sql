CREATE TABLE company_types (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE production_companies (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    founded_year INT CHECK (founded_year >= 1900),
    website_url VARCHAR(255),
    ceo VARCHAR(100),
    company_type_id INT REFERENCES company_types(id) ON DELETE SET NULL
);

CREATE TABLE media_outlets (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    website_url VARCHAR(255),
    founded_year INT CHECK (founded_year >= 1900)
);

CREATE TABLE genres (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE games (
    id SERIAL PRIMARY KEY,
    title VARCHAR(150) NOT NULL,
    release_year INT CHECK (release_year >= 1950),
    description TEXT,
    developer_id INT REFERENCES production_companies(id) ON DELETE SET NULL,
    publisher_id INT REFERENCES production_companies(id) ON DELETE SET NULL,
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
    media_outlet_id INT NOT NULL REFERENCES media_outlets(id) ON DELETE CASCADE,
    score INT NOT NULL CHECK (score >= 0 AND score <= 100),
    summary TEXT,
    CONSTRAINT uq_review_game_outlet UNIQUE (game_id, media_outlet_id)
);

CREATE TABLE system_requirement_types (
    id SERIAL PRIMARY KEY,
    name VARCHAR(20) NOT NULL UNIQUE
);

CREATE TABLE system_requirements (
    game_id INT NOT NULL REFERENCES games(id) ON DELETE CASCADE,
    system_requirements_type_id INT NOT NULL REFERENCES system_requirement_types(id) ON DELETE CASCADE,
    storage_gb INT NOT NULL,
    ram_gb INT NOT NULL,
    cpu_ghz NUMERIC(3, 1),
    gpu_tflops NUMERIC(4, 2),
    vram_gb INT,
    PRIMARY KEY (game_id, system_requirements_type_id)
);

CREATE INDEX idx_games_developer_id ON games(developer_id);
CREATE INDEX idx_games_publisher_id ON games(publisher_id);
CREATE INDEX idx_games_title ON games(title);
CREATE INDEX idx_games_release_year ON games(release_year);

CREATE INDEX idx_reviews_game_id ON reviews(game_id);
CREATE INDEX idx_reviews_media_outlet_id ON reviews(media_outlet_id);
CREATE INDEX idx_reviews_score ON reviews(score);





INSERT INTO company_types (name) VALUES 
('Developer'), 
('Publisher'), 
('Hybrid');

INSERT INTO genres (name) VALUES 
('RPG'), ('Action'), ('Shooter'), ('Strategy'), ('Adventure');

INSERT INTO system_requirement_types (name) VALUES 
('low'), 
('medium'), 
('high');

INSERT INTO production_companies (name, ceo, founded_year, company_type_id) VALUES
('CD Projekt Red', 'Adam Kiciński', 2002, (SELECT id FROM company_types WHERE name = 'Hybrid' LIMIT 1)),
('Rockstar Games', 'Sam Houser', 1998, (SELECT id FROM company_types WHERE name = 'Hybrid' LIMIT 1)),
('Electronic Arts', 'Andrew Wilson', 1982, (SELECT id FROM company_types WHERE name = 'Publisher' LIMIT 1)),
('FromSoftware', 'Hidetaka Miyazaki', 1986, (SELECT id FROM company_types WHERE name = 'Developer' LIMIT 1)),
('Bandai Namco', 'Yasuo Miyakawa', 2005, (SELECT id FROM company_types WHERE name = 'Publisher' LIMIT 1));

INSERT INTO media_outlets (name, website_url, founded_year) VALUES
('IGN', 'https://www.ign.com', 1996),
('GameSpot', 'https://www.gamespot.com', 1996),
('PC Gamer', 'https://www.pcgamer.com', 1993);

INSERT INTO games (title, release_year, description, developer_id, publisher_id) VALUES
('The Witcher 3: Wild Hunt', 2015, 'Geralt of Rivia searches for Ciri.', (SELECT id FROM production_companies WHERE name = 'CD Projekt Red' LIMIT 1), (SELECT id FROM production_companies WHERE name = 'CD Projekt Red' LIMIT 1)),
('Grand Theft Auto V', 2013, 'Three criminals in Los Santos.', (SELECT id FROM production_companies WHERE name = 'Rockstar Games' LIMIT 1), (SELECT id FROM production_companies WHERE name = 'Rockstar Games' LIMIT 1)),
('Elden Ring', 2022, 'Tarnished in the Lands Between.', (SELECT id FROM production_companies WHERE name = 'FromSoftware' LIMIT 1), (SELECT id FROM production_companies WHERE name = 'Bandai Namco' LIMIT 1));

INSERT INTO games_genres (game_id, genre_id) VALUES
((SELECT id FROM games WHERE title = 'The Witcher 3: Wild Hunt' AND release_year = 2015),
(SELECT id FROM genres WHERE name = 'RPG')), 
((SELECT id FROM games WHERE title = 'The Witcher 3: Wild Hunt' AND release_year = 2015), (SELECT id FROM genres WHERE name = 'Adventure')),
((SELECT id FROM games WHERE title = 'Grand Theft Auto V' AND release_year = 2013),
(SELECT id FROM genres WHERE name = 'Action')), ((SELECT id FROM games WHERE title = 'Grand Theft Auto V' AND release_year = 2013),
(SELECT id FROM genres WHERE name = 'Shooter')),
((SELECT id FROM games WHERE title = 'Elden Ring'), (SELECT id FROM genres WHERE name = 'RPG')),
((SELECT id FROM games WHERE title = 'Elden Ring'), (SELECT id FROM genres WHERE name = 'Action'));

INSERT INTO system_requirements (game_id, system_requirements_type_id, storage_gb, ram_gb, cpu_ghz, gpu_tflops, vram_gb) VALUES
((SELECT id FROM games WHERE title = 'The Witcher 3: Wild Hunt'), (SELECT id FROM system_requirement_types WHERE name = 'medium'), 35, 8, 3.4, 4.0, 2),
((SELECT id FROM games WHERE title = 'Grand Theft Auto V'), (SELECT id FROM system_requirement_types WHERE name = 'medium'), 72, 8, 3.2, 3.0, 2),
((SELECT id FROM games WHERE title = 'Elden Ring'), (SELECT id FROM system_requirement_types WHERE name = 'medium'), 60, 12, 3.6, 9.0, 6);

INSERT INTO reviews (game_id, media_outlet_id, score, summary) VALUES
((SELECT id FROM games WHERE title = 'The Witcher 3: Wild Hunt'), (SELECT id FROM media_outlets WHERE name = 'IGN'), 93, 'An amazing RPG experience.'),
((SELECT id FROM games WHERE title = 'The Witcher 3: Wild Hunt'), (SELECT id FROM media_outlets WHERE name = 'PC Gamer'), 92, 'One of the best PC games ever.'),
((SELECT id FROM games WHERE title = 'Grand Theft Auto V'), (SELECT id FROM media_outlets WHERE name = 'IGN'), 100, 'A masterpiece.'),
((SELECT id FROM games WHERE title = 'Elden Ring'), (SELECT id FROM media_outlets WHERE name = 'GameSpot'), 100, 'FromSoftware best work.'),
((SELECT id FROM games WHERE title = 'Elden Ring'), (SELECT id FROM media_outlets WHERE name = 'IGN'), 100, 'Simply incredible.');