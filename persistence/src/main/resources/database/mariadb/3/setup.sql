CREATE TABLE IF NOT EXISTS player
(
    uuid     UUID            NOT NULL PRIMARY KEY,
    playtime BIGINT          NOT NULL DEFAULT 0,
    coins    BIGINT UNSIGNED NOT NULL DEFAULT 0,
    gems     BIGINT UNSIGNED NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS player_setting
(
    id          BIGINT  NOT NULL PRIMARY KEY AUTO_INCREMENT,
    player_uuid UUID    NOT NULL,
    is_vanished BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_player_setting_player FOREIGN KEY (player_uuid) REFERENCES player (uuid) ON DELETE CASCADE
);

CREATE TRIGGER IF NOT EXISTS create_player_settings
    AFTER INSERT
    ON player
    FOR EACH ROW
    INSERT INTO player_setting (player_uuid)
    VALUES (NEW.uuid);

CREATE TABLE IF NOT EXISTS player_ignore
(
    id           BIGINT    NOT NULL PRIMARY KEY AUTO_INCREMENT,
    player_uuid  UUID      NOT NULL,
    ignored_uuid UUID      NOT NULL,
    created_at   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_player_ignore_player FOREIGN KEY (player_uuid) REFERENCES player (uuid) ON DELETE CASCADE,
    CONSTRAINT fk_player_ignore_ignored FOREIGN KEY (ignored_uuid) REFERENCES player (uuid) ON DELETE CASCADE,
    CONSTRAINT uq_player_ignore_unique UNIQUE (player_uuid, ignored_uuid)
);

CREATE INDEX IF NOT EXISTS idx_player_ignore_ignored_uuid ON player_ignore (ignored_uuid);

CREATE TABLE IF NOT EXISTS homes
(
    id          BIGINT      NOT NULL PRIMARY KEY AUTO_INCREMENT,
    player_uuid UUID        NOT NULL,
    name        VARCHAR(64) NOT NULL,
    icon        VARCHAR(64) NOT NULL DEFAULT 'GRASS_BLOCK',
    world_name  VARCHAR(64) NOT NULL,
    x           DOUBLE      NOT NULL,
    y           DOUBLE      NOT NULL,
    z           DOUBLE      NOT NULL,
    yaw         FLOAT       NOT NULL,
    pitch       FLOAT       NOT NULL,
    created_at  TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_homes_player FOREIGN KEY (player_uuid) REFERENCES player (uuid) ON DELETE CASCADE,
    CONSTRAINT uq_homes_player_name UNIQUE (player_uuid, name)
);

CREATE INDEX IF NOT EXISTS idx_homes_player ON homes (player_uuid);

CREATE TABLE IF NOT EXISTS economy_transactions
(
    id                BIGINT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
    type              VARCHAR(32)     NOT NULL,
    currency          VARCHAR(16)     NOT NULL,
    amount            BIGINT UNSIGNED NOT NULL,
    from_player_uuid  UUID            NULL,
    to_player_uuid    UUID            NULL,
    actor_player_uuid UUID            NULL,
    reason            VARCHAR(255)    NULL,
    created_at        TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_economy_tx_from_player FOREIGN KEY (from_player_uuid) REFERENCES player (uuid) ON DELETE SET NULL,
    CONSTRAINT fk_economy_tx_to_player FOREIGN KEY (to_player_uuid) REFERENCES player (uuid) ON DELETE SET NULL,
    CONSTRAINT fk_economy_tx_actor_player FOREIGN KEY (actor_player_uuid) REFERENCES player (uuid) ON DELETE SET NULL
);

CREATE INDEX IF NOT EXISTS idx_eco_tx_from ON economy_transactions (from_player_uuid, created_at);
CREATE INDEX IF NOT EXISTS idx_eco_tx_to ON economy_transactions (to_player_uuid, created_at);
CREATE INDEX IF NOT EXISTS idx_eco_tx_actor ON economy_transactions (actor_player_uuid, created_at);
CREATE INDEX IF NOT EXISTS idx_eco_tx_created_at ON economy_transactions (created_at);

CREATE TABLE IF NOT EXISTS economy_audit
(
    id         BIGINT      NOT NULL PRIMARY KEY AUTO_INCREMENT,
    action     VARCHAR(64) NOT NULL,
    payload    TEXT        NOT NULL,
    status     VARCHAR(32) NOT NULL,
    created_at TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_eco_audit_action ON economy_audit (action, created_at);
CREATE INDEX IF NOT EXISTS idx_eco_audit_status ON economy_audit (status, created_at);