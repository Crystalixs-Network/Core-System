package net.crystalixs.core.velocity.command;

import net.crystalixs.core.velocity.CorePlugin;
import net.crystalixs.core.velocity.command.cloud.VelocityCommand;
import net.crystalixs.core.velocity.command.cloud.VelocityCommandSource;
import net.crystalixs.core.velocity.command.cloud.VelocityPlayerCommandSource;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.component.DefaultValue;
import org.incendo.cloud.help.result.CommandEntry;
import org.incendo.cloud.minecraft.extras.MinecraftHelp;
import org.incendo.cloud.suggestion.Suggestion;
import org.incendo.cloud.suggestion.SuggestionProvider;
import org.jspecify.annotations.NonNull;

import java.util.stream.Collectors;

import static org.incendo.cloud.parser.standard.StringParser.greedyStringParser;

public class HelpCommand extends VelocityCommand {

    public HelpCommand(CorePlugin plugin) {
        super(plugin);
    }

    @Override
    public void registerTo(@NonNull CommandManager<VelocityCommandSource> commandManager) {
        MinecraftHelp<VelocityCommandSource> help = MinecraftHelp.<VelocityCommandSource>builder()
                .commandManager(commandManager)
                .audienceProvider(VelocityCommandSource::plattformSender)
                .commandPrefix("/help")
                .build();

        commandManager.command(commandManager.commandBuilder("help", "?")
                .senderType(VelocityPlayerCommandSource.class)
                .optional("query", greedyStringParser(), DefaultValue.constant(""),
                        SuggestionProvider.blocking(((context, input) -> commandManager.createHelpHandler()
                                .queryRootIndex(context.sender())
                                .entries()
                                .stream()
                                .map(CommandEntry::syntax)
                                .map(Suggestion::suggestion)
                                .collect(Collectors.toList()))))
                .handler(context -> help.queryCommands(context.get("query"), context.sender())));
    }
}
