ALTER TABLE homes
    DROP FOREIGN KEY fk_homes_player;

ALTER TABLE homes
    ADD CONSTRAINT fk_homes_player
        FOREIGN KEY (player_id) REFERENCES player (uuid)
            ON DELETE CASCADE;

ALTER TABLE economy_transactions
    ADD CONSTRAINT fk_economy_tx_from_player
        FOREIGN KEY (from_player_id) REFERENCES player (uuid)
            ON DELETE SET NULL,
    ADD CONSTRAINT fk_economy_tx_to_player
        FOREIGN KEY (to_player_id) REFERENCES player (uuid)
            ON DELETE SET NULL,
    ADD CONSTRAINT fk_economy_tx_actor_player
        FOREIGN KEY (actor_player_id) REFERENCES player (uuid)
            ON DELETE SET NULL;