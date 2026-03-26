CREATE TABLE IF NOT EXISTS economy_transactions
(
    id              BIGINT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
    type            VARCHAR(32)     NOT NULL,
    currency        VARCHAR(16)     NOT NULL,
    amount          BIGINT UNSIGNED NOT NULL,
    from_player_id  UUID            NULL,
    to_player_id    UUID            NULL,
    actor_player_id UUID            NULL,
    reason          VARCHAR(255)    NULL,
    created_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_economy_tx_from_player FOREIGN KEY (from_player_id) REFERENCES player (uuid),
    CONSTRAINT fk_economy_tx_to_player FOREIGN KEY (to_player_id) REFERENCES player (uuid),
    CONSTRAINT fk_economy_tx_actor_player FOREIGN KEY (actor_player_id) REFERENCES player (uuid)
);

CREATE INDEX IF NOT EXISTS idx_eco_tx_from ON economy_transactions (from_player_id, created_at);
CREATE INDEX IF NOT EXISTS idx_eco_tx_to ON economy_transactions (to_player_id, created_at);
CREATE INDEX IF NOT EXISTS idx_eco_tx_actor ON economy_transactions (actor_player_id, created_at);
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