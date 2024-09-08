CREATE TABLE IF NOT EXISTS HighScore
(
    id
    BIGINT
    AUTO_INCREMENT
    PRIMARY
    KEY,
    player_name
    VARCHAR
(
    255
) NOT NULL,
    score INT NOT NULL
    );