package net.crystalixs.core.paper.command;

import net.crystalixs.core.paper.CorePlugin;
import net.crystalixs.core.paper.command.cloud.PaperCommand;
import net.crystalixs.core.paper.command.cloud.PaperCommandSource;
import net.crystalixs.core.paper.command.cloud.PaperPlayerCommandSource;
import net.crystalixs.core.paper.command.util.SkullTextureService;
import net.kyori.adventure.translation.GlobalTranslator;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.minecraft.extras.RichDescription;
import org.incendo.cloud.permission.Permission;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.inventoryaccess.component.AdventureComponentWrapper;
import xyz.xenondevs.invui.item.builder.SkullBuilder;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.minimessage.translation.Argument.component;
import static org.incendo.cloud.parser.standard.StringParser.stringParser;

public class SkullCommand extends PaperCommand {

    private final SkullTextureService service;

    public SkullCommand(CorePlugin plugin) {
        super(plugin);
        this.service = new SkullTextureService();
    }

    @Override
    public void registerTo(@NotNull CommandManager<PaperCommandSource> commandManager) {
        commandManager.command(commandManager.commandBuilder("skull")
                .commandDescription(RichDescription.translatable("command.skull.description.main"))
                .senderType(PaperPlayerCommandSource.class)
                .permission(Permission.of("core.command.skull"))
                .required("player", stringParser(), RichDescription.translatable("command.skull.description.player"))
                .handler(context -> {
                    Player player = context.sender().player();
                    String name = context.get("player");

                    service.resolveTextureValue(name).whenComplete((texture, throwable) -> Bukkit.getScheduler().runTask(plugin, () -> {
                        if (throwable != null || texture == null) {
                            player.sendMessage(translatable("command.skull.error.player-not-found"));
                            return;
                        }

                        var nameArgument = component("player", text(name));
                        var itemName = translatable("command.skull.item-name").arguments(nameArgument);
                        var renderedName = GlobalTranslator.render(itemName, player.locale());

                        ItemStack skull = new SkullBuilder(new SkullBuilder.HeadTexture(texture))
                                .setDisplayName(new AdventureComponentWrapper(renderedName))
                                .get(null);

                        player.getInventory().addItem(skull);
                        player.sendMessage(translatable("command.skull.success").arguments(nameArgument));
                    }));
                }));
    }
}
