package net.crystalixs.core.paper.command;

import net.crystalixs.core.paper.CorePlugin;
import net.crystalixs.core.paper.command.cloud.PaperCommand;
import net.crystalixs.core.paper.command.cloud.PaperCommandSource;
import net.crystalixs.core.paper.command.cloud.PaperPlayerCommandSource;
import net.crystalixs.core.paper.economy.EconomyException;
import net.crystalixs.core.paper.economy.EconomyService;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.minecraft.extras.RichDescription;
import org.incendo.cloud.permission.Permission;
import org.jspecify.annotations.NonNull;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.minimessage.translation.Argument.component;

public final class CoinsCommand extends PaperCommand {

    private final EconomyService service;

    public CoinsCommand(CorePlugin plugin, EconomyService service) {
        super(plugin);
        this.service = service;
    }

    @Override
    public void registerTo(@NonNull CommandManager<PaperCommandSource> commandManager) {
        commandManager.command(commandManager.commandBuilder("coins")
                .commandDescription(RichDescription.translatable("command.coins.description.main"))
                .senderType(PaperPlayerCommandSource.class)
                .permission(Permission.of("core.command.coins"))
                .handler(context -> {
                    Player player = context.sender().player();
                    try {
                        long coins = service.getCoins(player.getUniqueId());
                        context.sender().sendMessage(translatable("command.coins.success").arguments(component("amount", text(coins))));

                    } catch (EconomyException exception) {
                        Component component = translatable(switch (exception.error()) {
                            case INVALID_AMOUNT -> "command.pay.error.invalid-amount";
                            case SELF_TRANSFER -> "command.pay.error.self-transfer";
                            case INSUFFICIENT_FUNDS -> "command.pay.error.insufficient-funds";
                            case PLAYER_NOT_FOUND -> "command.pay.error.player-not-found";
                            case PLAYER_CREATION_FAILED -> "command.pay.error.player-load";
                        });
                        context.sender().sendMessage(component);
                    }
                }));
    }
}
