package net.crystalixs.core.paper.home.logging;

public enum HomeLogEvent {
    CREATE_SUCCESS("home.create.success", "home created"),
    CREATE_FAILED("home.create.failed", "home create failed"),
    DELETE_SUCCESS("home.delete.success", "home deleted"),
    DELETE_FAILED("home.delete.failed", "home delete failed"),
    RENAME_SUCCESS("home.rename.success", "home renamed"),
    RENAME_FAILED("home.rename.failed", "home rename failed"),
    UPDATE_POSITION_SUCCESS("home.update_position.success", "home position updated"),
    UPDATE_POSITION_FAILED("home.update_position.failed", "home position update failed"),
    UPDATE_ICON_SUCCESS("home.update_icon.success", "home icon updated"),
    UPDATE_ICON_FAILED("home.update_icon.failed", "home icon update failed");

    private final String key;
    private final String message;

    HomeLogEvent(String key, String message) {
        this.key = key;
        this.message = message;
    }

    public String key() {
        return key;
    }

    public String message() {
        return message;
    }
}
