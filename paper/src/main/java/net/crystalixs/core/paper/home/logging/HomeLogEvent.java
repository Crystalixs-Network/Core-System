package net.crystalixs.core.paper.home.logging;

public enum HomeLogEvent {
    CREATE_SUCCESS("home.create.success"),
    CREATE_FAILED("home.create.failed"),
    DELETE_SUCCESS("home.delete.success"),
    DELETE_FAILED("home.delete.failed"),
    RENAME_SUCCESS("home.rename.success"),
    RENAME_FAILED("home.rename.failed"),
    UPDATE_POSITION_SUCCESS("home.update_position.success"),
    UPDATE_POSITION_FAILED("home.update_position.failed"),
    UPDATE_ICON_SUCCESS("home.update_icon.success"),
    UPDATE_ICON_FAILED("home.update_icon.failed");

    private final String key;

    HomeLogEvent(String key) {
        this.key = key;
    }

    public String key() {
        return key;
    }
}
