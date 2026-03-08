package net.crystalixs.core.common.logging;

public interface LogFactory extends AutoCloseable {

    StructuredLogger logger(String name);

    @Override
    void close();
}
