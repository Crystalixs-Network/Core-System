package net.crystalixs.core.paper.command;

import net.crystalixs.core.paper.CorePlugin;
import net.crystalixs.core.paper.command.cloud.PaperCommand;
import net.crystalixs.core.paper.command.cloud.PaperCommandSource;
import net.crystalixs.core.paper.command.cloud.PaperPlayerCommandSource;
import net.crystalixs.core.paper.ignore.PlayerIgnoreService;
import org.bukkit.entity.Player;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.minecraft.extras.RichDescription;
import org.incendo.cloud.permission.Permission;
import org.incendo.cloud.suggestion.SuggestionProvider;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.minimessage.translation.Argument.component;
import static org.incendo.cloud.parser.standard.StringParser.stringParser;

public final class UnignoreCommand extends PaperCommand {

    private final PlayerIgnoreService service;

    public UnignoreCommand(CorePlugin plugin, PlayerIgnoreService service) {
        super(plugin);
        this.service = service;
    }

    @Override
    public void registerTo(@NotNull CommandManager<PaperCommandSource> commandManager) {
        commandManager.command(commandManager.commandBuilder("unignore")
                .commandDescription(RichDescription.translatable("command.unignore.description.main"))
                .senderType(PaperPlayerCommandSource.class)
                .permission(Permission.of("core.command.unignore"))
                .required("player", stringParser(),
                        RichDescription.translatable("command.unignore.description.player"),
                        ignoredPlayerNamesProvider()
                )
                .handler(context -> {
                    Player sender = context.sender().player();
                    Player target = context.get("player");

                    if (sender.getUniqueId().equals(target.getUniqueId())) {
                        sender.sendMessage(translatable("command.unignore.error.self"));
                        return;
                    }
                    if (!service.isIgnoring(sender, target)) {
                        sender.sendMessage(translatable("command.unignore.error.not-ignored"));
                        return;
                    }

                    service.unignorePlayer(sender, target);
                    sender.sendMessage(translatable("command.unignore.success").arguments(component("name", target.name())));
                }));
    }

    private SuggestionProvider<PaperPlayerCommandSource> ignoredPlayerNamesProvider() {
        return SuggestionProvider.blockingStrings((context, input) -> {
            Player sender = context.sender().player();
            String token = input.lastRemainingToken();

            return service.ignoredPlayerNames(sender).stream()
                    .filter(name -> token.isBlank() || name.regionMatches(true, 0, token, 0, token.length()))
                    .toList();
        });
    }
}
