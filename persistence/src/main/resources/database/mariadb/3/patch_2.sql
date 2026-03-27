ALTER TABLE player_setting RENAME COLUMN player_id TO player_uuid;
ALTER TABLE player_setting
    DROP FOREIGN KEY fk_player_setting_player;
ALTER TABLE player_setting
    ADD CONSTRAINT fk_player_setting_player FOREIGN KEY (player_uuid) REFERENCES player (uuid) ON DELETE CASCADE;

DROP TRIGGER create_player_settings;
CREATE TRIGGER IF NOT EXISTS create_player_setting
    AFTER INSERT
    ON player
    FOR EACH ROW
    INSERT INTO player_setting (player_uuid)
    VALUES (NEW.uuid);

ALTER TABLE homes RENAME TO home;
ALTER TABLE home RENAME COLUMN player_id TO player_uuid;
ALTER TABLE home
    DROP FOREIGN KEY fk_homes_player;
ALTER TABLE home
    DROP CONSTRAINT uq_homes_player_name;
ALTER TABLE home
    DROP INDEX idx_homes_player;
ALTER TABLE home
    ADD CONSTRAINT fk_home_player FOREIGN KEY (player_uuid) REFERENCES player (uuid) ON DELETE CASCADE;
ALTER TABLE home
    ADD CONSTRAINT uq_home_player_name UNIQUE (player_uuid, name);
ALTER TABLE home
    ADD INDEX idx_homes_player (player_uuid);

ALTER TABLE economy_transactions RENAME COLUMN from_player_id TO from_player_uuid;
ALTER TABLE economy_transactions RENAME COLUMN to_player_id TO to_player_uuid;
ALTER TABLE economy_transactions RENAME COLUMN actor_player_id TO actor_player_uuid;
ALTER TABLE economy_transactions
    DROP FOREIGN KEY fk_economy_tx_from_player;
ALTER TABLE economy_transactions
    DROP FOREIGN KEY fk_economy_tx_to_player;
ALTER TABLE economy_transactions
    DROP FOREIGN KEY fk_economy_tx_actor_player;
ALTER TABLE economy_transactions
    DROP INDEX idx_eco_tx_from;
ALTER TABLE economy_transactions
    DROP INDEX idx_eco_tx_to;
ALTER TABLE economy_transactions
    DROP INDEX idx_eco_tx_actor;
ALTER TABLE economy_transactions
    ADD CONSTRAINT fk_economy_tx_from_player FOREIGN KEY (from_player_uuid) REFERENCES player (uuid) ON DELETE SET NULL;
ALTER TABLE economy_transactions
    ADD CONSTRAINT fk_economy_tx_to_player FOREIGN KEY (to_player_uuid) REFERENCES player (uuid) ON DELETE SET NULL;
ALTER TABLE economy_transactions
    ADD CONSTRAINT fk_economy_tx_actor_player FOREIGN KEY (actor_player_uuid) REFERENCES player (uuid) ON DELETE SET NULL;
ALTER TABLE economy_transactions
    ADD INDEX idx_eco_tx_from (from_player_uuid, created_at);
ALTER TABLE economy_transactions
    ADD INDEX idx_eco_tx_to (to_player_uuid, created_at);
ALTER TABLE economy_transactions
    ADD INDEX idx_eco_tx_actor (actor_player_uuid, created_at);

