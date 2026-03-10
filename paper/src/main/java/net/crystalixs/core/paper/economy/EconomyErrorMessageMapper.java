package net.crystalixs.core.paper.economy;

public final class EconomyErrorMessageMapper {

    public String keyForPay(EconomyError error) {
        return switch (error) {
            case INVALID_AMOUNT -> "command.pay.error.invalid-amount";
            case SELF_TRANSFER -> "command.pay.error.self-transfer";
            case INSUFFICIENT_FUNDS -> "command.pay.error.insufficient-funds";
            case PLAYER_NOT_FOUND -> "command.pay.error.player-not-found";
            case PLAYER_CREATION_FAILED -> "command.pay.error.player-load";
        };
    }

    public String keyForEco(EconomyError error) {
        return switch (error) {
            case INVALID_AMOUNT -> "command.eco.error.invalid-amount";
            case SELF_TRANSFER -> "command.eco.error.invalid-amount";
            case INSUFFICIENT_FUNDS -> "command.eco.error.insufficient-funds";
            case PLAYER_NOT_FOUND -> "command.eco.error.player-not-found";
            case PLAYER_CREATION_FAILED -> "command.eco.error.player-load";
        };
    }

    public String keyForBalance(EconomyError error) {
        return switch (error) {
            case PLAYER_CREATION_FAILED, PLAYER_NOT_FOUND -> "command.balance.error.player-load";
            default -> "command.balance.error.player-load";
        };
    }

    public String keyForCoins(EconomyError error) {
        return switch (error) {
            case PLAYER_CREATION_FAILED, PLAYER_NOT_FOUND -> "command.coins.error.player-load";
            default -> "command.coins.error.player-load";
        };
    }
}
