package net.crystalixs.core.velocity.command;

import net.crystalixs.core.velocity.CorePlugin;
import net.crystalixs.core.velocity.command.cloud.VelocityCommand;
import net.crystalixs.core.velocity.command.cloud.VelocityCommandSource;
import net.crystalixs.core.velocity.command.cloud.VelocityPlayerCommandSource;
import net.crystalixs.core.velocity.help.StyledHelpRenderer;
import net.crystalixs.core.velocity.help.UnifiedHelpService;
import org.incendo.cloud.CommandManager;
import org.jspecify.annotations.NonNull;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.incendo.cloud.minecraft.extras.RichDescription.translatable;
import static org.incendo.cloud.parser.standard.IntegerParser.integerParser;
import static org.incendo.cloud.parser.standard.StringParser.greedyStringParser;
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
                .optional("query", greedyStringParser(), translatable("command.help.description.query"))
                .handler(context -> renderPage(context.sender(), context.getOrDefault("query", ""), 1)));

        commandManager.command(commandManager.commandBuilder("help-page")
                .commandDescription(translatable("command.help.description.main"))
                .senderType(VelocityPlayerCommandSource.class)
                .optional("query", stringParser())
                .optional("page", integerParser(1), translatable("command.help.description.page"))
                .handler(context -> {
                    int page = context.getOrDefault("page", 1);
                    String encoded = context.getOrDefault("query", "");
                    String query = decodeQuery(encoded);
                    renderPage(context.sender(), query, page);
                }));
    }

    private void renderPage(VelocityCommandSource sender, String query, int requestedPage) {
        var all = service.query(sender, query);

        int pageSize = 8;
        int pages = Math.max(1, (int) Math.ceil((double) all.size() / pageSize));
        int page = Math.min(Math.max(1, requestedPage), pages); // hard clamp

        int from = Math.min((page - 1) * pageSize, all.size());
        int to = Math.min(from + pageSize, all.size());
        var pageEntries = all.subList(from, to);

        StyledHelpRenderer renderer = new StyledHelpRenderer(this::encodeQuery);
        renderer.render(query, page, pages, pageEntries).forEach(sender.plattformSender()::sendMessage);
    }

    private String encodeQuery(String query) {
        if (query == null || query.isBlank()) return "";
        return Base64.getUrlEncoder().withoutPadding().encodeToString(query.getBytes(StandardCharsets.UTF_8));
    }

    private String decodeQuery(String encoded) {
        if (encoded == null || encoded.isBlank()) return "";
        try {
            return new String(Base64.getUrlDecoder().decode(encoded), StandardCharsets.UTF_8);
        } catch (IllegalArgumentException exception) {
            return "";
        }
    }
}
