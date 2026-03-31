package net.crystalixs.core.persistence.store;

import net.crystalixs.core.persistence.model.AuditModel;

import java.util.List;

public interface AuditStore {

    void create(AuditModel model);

    List<AuditModel> findByAction(String action, int limit);

}
