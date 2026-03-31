package net.crystalixs.core.paper.home;

public final class HomeRenameInputValidator {

    private static final int MAX_HOME_NAME_LENGTH = 64;

    public String normalizeOldName(String oldName) {
        return normalize(oldName, "Old home name must be valid");
    }

    public String normalizeNewName(String newName) {
        return normalize(newName, "New home name must be valid");
    }

    private String normalize(String name, String message) {
        if (name == null) {
            throw new HomeException(HomeError.INVALID_NAME, message);
        }

        String normalized = name.trim();
        if (normalized.isBlank() || normalized.length() > MAX_HOME_NAME_LENGTH) {
            throw new HomeException(HomeError.INVALID_NAME, message);
        }
        return normalized;
    }
}
