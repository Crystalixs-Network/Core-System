package net.crystalixs.core.paper.setting;

public class SettingException extends RuntimeException {

    private final SettingError error;

    public SettingException(SettingError error, String message) {
        super(message);
        this.error = error;
    }

    public SettingError error() {
        return error;
    }

}
