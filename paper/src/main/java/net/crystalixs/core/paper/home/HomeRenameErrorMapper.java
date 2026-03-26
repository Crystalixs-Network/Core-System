package net.crystalixs.core.paper.home;

import net.kyori.adventure.text.Component;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.minimessage.translation.Argument.component;

public final class HomeRenameErrorMapper {

    public Component toMessage(HomeException exception, String oldName, String newName) {
        String key = switch (exception.error()) {
            case INVALID_NAME -> "command.home.create.error.invalid-name";
            case HOME_NOT_FOUND -> "command.home.rename.error.not-found";
            case HOME_ALREADY_EXISTS -> "command.home.rename.error.already-exists";
            case PLAYER_CREATION_FAILED -> "error.player-load";
            default -> "command.home.rename.error.persistence";
        };

        if (exception.error() == HomeError.HOME_NOT_FOUND) {
            String safeOldName = oldName == null ? "" : oldName.trim();
            return translatable(key).arguments(component("old_name", text(safeOldName)));
        }

        if (exception.error() == HomeError.HOME_ALREADY_EXISTS) {
            String safeNewName = newName == null ? "" : newName.trim();
            return translatable(key).arguments(component("new_name", text(safeNewName)));
        }

        return translatable(key);
    }
}
