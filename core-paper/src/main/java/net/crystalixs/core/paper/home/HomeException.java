package net.crystalixs.core.paper.home;

public class HomeException extends RuntimeException {

    private final HomeError error;

    public HomeException(HomeError error, String message) {
        super(message);
        this.error = error;
    }

    public HomeError error() {
        return error;
    }
}
