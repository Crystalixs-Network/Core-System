package net.crystalixs.core.common.command;

public abstract class AbstractCommandSource<C> {

    protected final C plattformSender;

    public AbstractCommandSource(C plattformSender) {
        this.plattformSender = plattformSender;
    }

    public C plattformSender() {
        return plattformSender;
    }
}
