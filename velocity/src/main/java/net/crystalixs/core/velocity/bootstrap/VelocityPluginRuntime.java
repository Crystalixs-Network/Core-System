package net.crystalixs.core.velocity.bootstrap;

import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.proxy.ProxyServer;
import net.crystalixs.core.common.logging.LogFactory;
import net.crystalixs.core.common.logging.LogManager;
import net.crystalixs.core.common.bootstrap.AbstractPluginRuntime;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.Tag;
import org.slf4j.Logger;

import java.nio.file.Path;

import static net.kyori.adventure.text.Component.translatable;

public final class VelocityPluginRuntime extends AbstractPluginRuntime {

    private static final MiniMessage MINI_MESSAGE = MiniMessage.builder()
            .editTags(builder -> builder.tag("prefix", Tag.inserting(translatable("prefix"))))
            .build();

    private final PluginContainer pluginContainer;
    private final ProxyServer server;
    private final Path dataDirectory;

    private VelocityPluginRuntime(PluginContainer pluginContainer, ProxyServer server, Path dataDirectory, LogFactory logging) {
        super(logging);
        this.pluginContainer = pluginContainer;
        this.server = server;
        this.dataDirectory = dataDirectory;
    }

    public static VelocityPluginRuntime create(PluginContainer pluginContainer, ProxyServer server, Path dataDirectory, Logger platformLogger) {
        LogFactory logging = LogManager.createForSlf4j(platformLogger, dataDirectory.resolve("logs"));
        return new VelocityPluginRuntime(pluginContainer, server, dataDirectory, logging);
    }

    public PluginContainer pluginContainer() {
        return pluginContainer;
    }

    public ProxyServer server() {
        return server;
    }

    public Path dataDirectory() {
        return dataDirectory;
    }

    public MiniMessage miniMessage() {
        return MINI_MESSAGE;
    }
}
