package net.crystalixs.core.velocity.command;

import com.velocitypowered.api.command.CommandSource;
import net.crystalixs.core.common.translation.TranslationProvider;
import net.crystalixs.core.velocity.CorePlugin;
import net.crystalixs.core.velocity.command.cloud.VelocityCommand;
import net.crystalixs.core.velocity.command.cloud.VelocityCommandSource;
import net.crystalixs.core.velocity.config.VelocityConfigUpdater;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.minecraft.extras.RichDescription;
import org.incendo.cloud.permission.Permission;
import org.jspecify.annotations.NonNull;

import java.io.IOException;
import java.util.EnumSet;

import static net.kyori.adventure.text.Component.translatable;

public class CoreCommand extends VelocityCommand {

    private final VelocityConfigUpdater updater;
    private final TranslationProvider provider;

    public CoreCommand(CorePlugin plugin, VelocityConfigUpdater updater, TranslationProvider provider) {
        super(plugin);
        this.updater = updater;
        this.provider = provider;
    }

    @Override
    public void registerTo(@NonNull CommandManager<VelocityCommandSource> commandManager) {
        commandManager.command(commandManager.commandBuilder("core")
                .commandDescription(RichDescription.translatable("command.core.description.main"))
                .senderType(VelocityCommandSource.class)
                .permission(Permission.of("core.command.core"))
                .literal("reload", RichDescription.translatable("command.core.description.reload"))
                .flag(commandManager.flagBuilder("all")
                        .withAliases("a")
                        .withDescription(RichDescription.translatable("command.core.description.flag.all")))
                .flag(commandManager.flagBuilder("config")
                        .withAliases("c")
                        .withDescription(RichDescription.translatable("command.core.description.flag.config")))
                .flag(commandManager.flagBuilder("messages")
                        .withAliases("m")
                        .withDescription(RichDescription.translatable("command.core.description.flag.messages")))
                .handler(this::reload));
    }

    private void reload(CommandContext<VelocityCommandSource> context) {
        final CommandSource source = context.sender().plattformSender();
        EnumSet<ReloadFlag> presentFlags = EnumSet.noneOf(ReloadFlag.class);

        for (ReloadFlag flag : ReloadFlag.values()) {
            if (context.flags().isPresent(flag.getName())) {
                presentFlags.add(flag);
            }
        }

        if (presentFlags.isEmpty()) {
            source.sendMessage(translatable("command.core.reload.error.no-flag-present"));
            return;
        }

        presentFlags.forEach(flag -> {
            boolean success = switch (flag) {
                case CONFIG -> reloadConfig();
                case MESSAGES -> reloadMessages();
                default -> reloadAll();
            };
            if (success) {
                source.sendMessage(translatable("command.core.reload." + flag.getName()));
            } else {
                source.sendMessage(translatable("command.core.reload.error.io-exception"));
            }
        });
    }


    private boolean reloadAll() {
        return reloadConfig() && reloadMessages();
    }

    private boolean reloadMessages() {
        provider.reload();
        return true;
    }

    private boolean reloadConfig() {
        try {
            updater.reload();
            return true;
        } catch (IOException exception) {
            return false;
        }
    }

    private enum ReloadFlag {
        ALL("all"),
        CONFIG("config"),
        MESSAGES("messages");

        private final String name;

        ReloadFlag(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}
