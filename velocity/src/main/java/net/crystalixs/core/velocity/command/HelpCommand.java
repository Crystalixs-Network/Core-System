package net.crystalixs.core.velocity.command;

import net.crystalixs.core.common.command.help.NetworkHelpCatalog;
import net.crystalixs.core.velocity.CorePlugin;
import net.crystalixs.core.velocity.command.cloud.VelocityCommand;
import net.crystalixs.core.velocity.command.cloud.VelocityCommandSource;
import net.crystalixs.core.velocity.command.cloud.VelocityPlayerCommandSource;
import net.crystalixs.core.velocity.help.StyledHelpRenderer;
import net.crystalixs.core.velocity.help.UnifiedHelpService;
import net.kyori.adventure.text.event.ClickEvent;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.help.HelpQuery;
import org.incendo.cloud.help.result.CommandEntry;
import org.incendo.cloud.help.result.IndexCommandResult;
import org.incendo.cloud.help.result.MultipleCommandResult;
import org.incendo.cloud.help.result.VerboseCommandResult;
import org.jspecify.annotations.NonNull;

import java.util.List;

import static org.incendo.cloud.minecraft.extras.RichDescription.translatable;
import static org.incendo.cloud.parser.standard.IntegerParser.integerParser;
import static org.incendo.cloud.parser.standard.StringParser.stringParser;

public class HelpCommand extends VelocityCommand {

    private final UnifiedHelpService service;

    public HelpCommand(CorePlugin plugin, UnifiedHelpService service) {
        super(plugin);
        this.service = service;
    }

    @Override
    public void registerTo(@NonNull CommandManager<VelocityCommandSource> commandManager) {
        commandManager.command(commandManager.commandBuilder("help", "?")
                .commandDescription(translatable("command.help.description.main"))
                .senderType(VelocityPlayerCommandSource.class)
                .flag(commandManager.flagBuilder("page")
                        .withAliases("p")
                        .withDescription(translatable("command.help.description.page"))
                        .withComponent(integerParser(1))
                        .build()
                )
                .optional("query", stringParser(), translatable("command.help.description.query"))
                .handler(context -> renderPage(commandManager, context.sender(), context.getOrDefault("query", ""), page(context))));
    }

    private void renderPage(CommandManager<VelocityCommandSource> commandManager, VelocityCommandSource sender, String query, int requestedPage) {
        var all = service.query(sender, query);

        int pageSize = 8;
        int pages = Math.max(1, (int) Math.ceil((double) all.size() / pageSize));
        int page = Math.min(Math.max(1, requestedPage), pages);

        int from = Math.min((page - 1) * pageSize, all.size());
        int to = Math.min(from + pageSize, all.size());
        var pageEntries = all.subList(from, to);

        createRenderer(sender, commandManager).render(query, page, pages, pageEntries).forEach(sender.plattformSender()::sendMessage);
    }

    private StyledHelpRenderer createRenderer(VelocityCommandSource sender, CommandManager<VelocityCommandSource> commandManager) {
        return new StyledHelpRenderer(entry -> ClickEvent.callback(audience -> {
            if (entry.isProxyCommand()) {
                renderProxyDetails(sender, entry.detailsQuery(), commandManager);
                return;
            }
            renderBackendDetails(commandManager, sender, entry.sourceLabel(), entry.detailsQuery());
        }));
    }

    private void renderProxyDetails(VelocityCommandSource sender, String detailsQuery, CommandManager<VelocityCommandSource> commandManager) {
        String query = detailsQuery.startsWith("/") ? detailsQuery.substring(1) : detailsQuery;
        var result = commandManager.createHelpHandler().query(HelpQuery.of(sender, query));
        var renderer = createRenderer(sender, commandManager);

        switch (result) {
            case VerboseCommandResult<VelocityCommandSource> verbose -> {
                renderer.renderProxyVerboseDetails(sender, commandManager, query, verbose).forEach(sender.plattformSender()::sendMessage);
                return;
            }
            case MultipleCommandResult<VelocityCommandSource> multiple -> {
                renderer.renderCommandSuggestions(query, multiple.childSuggestions(), suggestion -> ClickEvent.callback(audience -> renderProxyDetails(sender, suggestion, commandManager)))
                        .forEach(sender.plattformSender()::sendMessage);
                return;
            }
            case IndexCommandResult<VelocityCommandSource> index when !index.entries().isEmpty() -> {
                List<String> syntaxes = index.entries().stream().map(CommandEntry::syntax).toList();
                renderer.renderCommandSuggestions(query, syntaxes, suggestion -> ClickEvent.callback(audience -> renderProxyDetails(sender, suggestion, commandManager)))
                        .forEach(sender.plattformSender()::sendMessage);
                return;
            }
            default -> {
            }
        }

        renderer.renderNoResults(query, "Kein passender Proxy-Befehl gefunden.").forEach(sender.plattformSender()::sendMessage);
    }

    private void renderBackendDetails(CommandManager<VelocityCommandSource> commandManager, VelocityCommandSource sender, String sourceId, String detailsQuery) {
        StyledHelpRenderer renderer = createRenderer(sender, commandManager);
        NetworkHelpCatalog.Entry entry = service.findBackendEntry(sender, sourceId, detailsQuery);
        if (entry == null) {
            renderer.renderNoResults(detailsQuery, "Kein Backend-Help-Eintrag gefunden.").forEach(sender.plattformSender()::sendMessage);
            return;
        }
        renderer.renderBackendDetails(detailsQuery, entry).forEach(sender.plattformSender()::sendMessage);
    }

    private int page(CommandContext<VelocityPlayerCommandSource> context) {
        Integer rawPage = context.flags().getValue("page", 1);
        return rawPage != null ? rawPage : 1;
    }
}
