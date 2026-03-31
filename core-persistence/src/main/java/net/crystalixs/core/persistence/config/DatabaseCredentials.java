package net.crystalixs.core.persistence.config;

public record DatabaseCredentials(
        String host,
        int port,
        String database,
        String username,
        String password
) {
}
