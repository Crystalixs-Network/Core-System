package net.crystalixs.core.common.command;

public abstract class CommandSource<C> {

    protected final C plattformSender;

    public CommandSource(C plattformSender) {
        this.plattformSender = plattformSender;
    }

    public C plattformSender() {
        return plattformSender;
    }
}
