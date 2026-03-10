package net.crystalixs.core.paper.economy;

public class EconomyException extends RuntimeException {

    private final EconomyError error;

    public EconomyException(EconomyError error, String message) {
        super(message);
        this.error = error;
    }

    public EconomyError error() {
        return error;
    }
}
