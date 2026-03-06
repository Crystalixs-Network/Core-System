package net.crystalixs.core.velocity.command;

import net.crystalixs.core.common.translation.TranslationProvider;
import net.crystalixs.core.velocity.CorePlugin;
import net.crystalixs.core.velocity.command.cloud.VelocityCommand;
import net.crystalixs.core.velocity.command.cloud.VelocityCommandSource;
import net.crystalixs.core.velocity.config.VelocityConfigLoader;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.minecraft.extras.RichDescription;
import org.incendo.cloud.permission.Permission;
import org.incendo.cloud.suggestion.SuggestionProvider;
import org.jspecify.annotations.NonNull;

import java.util.List;

import static net.kyori.adventure.text.Component.translatable;
import static org.incendo.cloud.parser.standard.StringParser.stringParser;

public class CoreCommand extends VelocityCommand {

    private final List<String> reloadModes = List.of("ALL", "MESSAGES", "CONFIG");

    private final VelocityConfigLoader loader;
    private final TranslationProvider provider;

    public CoreCommand(CorePlugin plugin, VelocityConfigLoader loader, TranslationProvider provider) {
        super(plugin);
        this.loader = loader;
        this.provider = provider;
    }

    @Override
    public void registerTo(@NonNull CommandManager<VelocityCommandSource> commandManager) {
        commandManager.command(commandManager.commandBuilder("core")
                .commandDescription(RichDescription.translatable("command.core.description.main"))
                .senderType(VelocityCommandSource.class)
                .permission(Permission.of("core.command.core"))
                .literal("reload", RichDescription.translatable("command.core.description.reload.main"))
                .optional("mode", stringParser(),
                        RichDescription.translatable("command.core.description.reload.mode"),
                        SuggestionProvider.suggestingStrings(reloadModes))
                .handler(context -> {
                    String mode = context.getOrDefault("mode", "all");

                    switch (mode.toLowerCase()) {
                        case "config" -> reloadConfig(context);
                        case "messages" -> reloadMessages(context);
                        default -> reloadAll(context);
                    }
                }));
    }

    private void reloadAll(CommandContext<VelocityCommandSource> context) {
        reloadConfig(context);
        reloadMessages(context);
    }

    private void reloadMessages(CommandContext<VelocityCommandSource> context) {
        provider.reload();
        context.sender().plattformSender().sendMessage(translatable("command.core.reload.messages"));
    }

    private void reloadConfig(CommandContext<VelocityCommandSource> context) {
        loader.saveAndReload();
        context.sender().plattformSender().sendMessage(translatable("command.core.reload.config"));
    }
}
