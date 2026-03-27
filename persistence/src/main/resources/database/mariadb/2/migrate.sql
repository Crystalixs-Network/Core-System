CREATE TABLE IF NOT EXISTS player_setting
(
    id          BIGINT  NOT NULL PRIMARY KEY AUTO_INCREMENT,
    player_id   UUID    NOT NULL,
    is_ignored  BOOLEAN NOT NULL DEFAULT FALSE,
    is_vanished BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_player_setting_player FOREIGN KEY (player_id) REFERENCES player (uuid) ON DELETE CASCADE
);

CREATE TRIGGER IF NOT EXISTS create_player_settings
    AFTER INSERT
    ON player
    FOR EACH ROW
    INSERT INTO player_setting (player_id)
    VALUES (NEW.uuid);