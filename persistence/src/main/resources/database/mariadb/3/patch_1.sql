ALTER TABLE player_setting
    DROP COLUMN IF EXISTS is_ignored;

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