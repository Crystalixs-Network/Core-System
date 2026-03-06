package net.crystalixs.core.velocity.command;

import net.crystalixs.core.velocity.CorePlugin;
import net.crystalixs.core.velocity.command.cloud.VelocityCommand;
import net.crystalixs.core.velocity.command.cloud.VelocityCommandSource;
import net.crystalixs.core.velocity.config.VelocityConfigLoader;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.minecraft.extras.RichDescription;
import org.incendo.cloud.permission.Permission;
import org.jspecify.annotations.NonNull;

import static net.kyori.adventure.text.Component.translatable;

public class CoreCommand extends VelocityCommand {

    private final VelocityConfigLoader loader;

    public CoreCommand(CorePlugin plugin, VelocityConfigLoader loader) {
        super(plugin);
        this.loader = loader;
    }

    @Override
    public void registerTo(@NonNull CommandManager<VelocityCommandSource> commandManager) {
        commandManager.command(commandManager.commandBuilder("core")
                .commandDescription(RichDescription.translatable("command.core.description.main"))
                .senderType(VelocityCommandSource.class)
                .permission(Permission.of("core.command.core"))
                .literal("reload", RichDescription.translatable("command.core.description.reload"))
                .handler(context -> {
                    loader.saveAndReload();
                    context.sender().plattformSender().sendMessage(translatable("command.core.reload"));
                }));
    }
}
