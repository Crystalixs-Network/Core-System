CREATE TABLE IF NOT EXISTS player
(
    uuid     UUID            NOT NULL PRIMARY KEY,
    playtime BIGINT          NOT NULL DEFAULT 0,
    coins    BIGINT UNSIGNED NOT NULL DEFAULT 0,
    gems     BIGINT UNSIGNED NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS homes
(
    id         BIGINT      NOT NULL PRIMARY KEY AUTO_INCREMENT,
    player_id  UUID        NOT NULL,
    name       VARCHAR(64) NOT NULL,
    world_name VARCHAR(64) NOT NULL,
    x          DOUBLE      NOT NULL,
    y          DOUBLE      NOT NULL,
    z          DOUBLE      NOT NULL,
    yaw        FLOAT       NOT NULL,
    pitch      FLOAT       NOT NULL,
    created_at TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_homes_player FOREIGN KEY (player_id) REFERENCES player (uuid),
    CONSTRAINT uq_homes_player_name UNIQUE (player_id, name)
);

CREATE INDEX IF NOT EXISTS idx_homes_player ON homes (player_id);