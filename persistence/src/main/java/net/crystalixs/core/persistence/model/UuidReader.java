package net.crystalixs.core.persistence.model;

import de.chojo.sadu.mapper.reader.ValueReader;
import de.chojo.sadu.mapper.wrapper.Row;

import java.util.UUID;

public interface UuidReader {

    static ValueReader<UUID, String> uuidReader() {
        return ValueReader.create(UUID::fromString, Row::getString, Row::getString);
    }
}
