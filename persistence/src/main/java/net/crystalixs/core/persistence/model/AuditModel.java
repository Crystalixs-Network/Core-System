package net.crystalixs.core.persistence.model;

import de.chojo.sadu.mapper.rowmapper.RowMapping;

import java.time.Instant;

public record AuditModel(
        long id,
        String action,
        String payload,
        String status,
        Instant createdAt
) {

    public static RowMapping<AuditModel> map() {
        return row -> new AuditModel(
                row.getLong("id"),
                row.getString("action"),
                row.getString("payload"),
                row.getString("status"),
                row.getTimestamp("created_at").toInstant());
    }

}
