package net.crystalixs.core.paper.home;

import net.crystalixs.core.persistence.model.HomeModel;
import net.kyori.adventure.text.Component;

import java.util.UUID;

public final class HomeRenameExecutor {

    private final HomeService service;
    private final HomeRenameInputValidator inputValidator;
    private final HomeRenameErrorMapper errorMapper;

    public HomeRenameExecutor(HomeService service) {
        this.service = service;
        this.inputValidator = new HomeRenameInputValidator();
        this.errorMapper = new HomeRenameErrorMapper();
    }

    public Outcome execute(UUID playerId, String oldNameInput, String newNameInput) {
        try {
            String oldName = inputValidator.normalizeOldName(oldNameInput);
            String newName = inputValidator.normalizeNewName(newNameInput);

            if (oldName.equalsIgnoreCase(newName)) {
                HomeException exception = new HomeException(HomeError.HOME_ALREADY_EXISTS, "Home with name " + newName + " already exists for player " + playerId);
                Component message = errorMapper.toMessage(exception, oldNameInput, newNameInput);
                return new Failure(exception, message);
            }

            HomeModel renamed = service.rename(playerId, oldName, newName);
            return new Success(oldName, renamed);

        } catch (HomeException exception) {
            Component message = errorMapper.toMessage(exception, oldNameInput, newNameInput);
            return new Failure(exception, message);
        }
    }

    public sealed interface Outcome permits Success, Failure {
    }

    public record Success(String oldName, HomeModel renamed) implements Outcome {
    }

    public record Failure(HomeException exception, Component message) implements Outcome {
        public boolean shouldLogWarn() {
            return exception.error() == HomeError.HOME_RENAME_FAILED || exception.error() == HomeError.PLAYER_CREATION_FAILED;
        }
    }
}
