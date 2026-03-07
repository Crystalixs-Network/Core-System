package net.crystalixs.core.common.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

final class ConfigFileVersion {

    private final String hash;

    private ConfigFileVersion(String hash) {
        this.hash = hash;
    }

    static ConfigFileVersion read(Path file) throws IOException {
        if (Files.notExists(file)) {
            return new ConfigFileVersion("missing");
        }

        try {
            byte[] content = Files.readAllBytes(file);
            byte[] digest = MessageDigest.getInstance("SHA-256").digest(content);
            return new ConfigFileVersion(HexFormat.of().formatHex(digest));

        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is not available.", exception);
        }
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof ConfigFileVersion version)) {
            return false;
        }
        return hash.equals(version.hash);
    }

    @Override
    public int hashCode() {
        return hash.hashCode();
    }

    @Override
    public String toString() {
        return hash;
    }
}
