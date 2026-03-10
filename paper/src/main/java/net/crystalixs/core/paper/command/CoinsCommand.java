package net.crystalixs.core.paper.command;

import net.crystalixs.core.paper.CorePlugin;
import net.crystalixs.core.paper.command.cloud.PaperCommand;
import net.crystalixs.core.paper.command.cloud.PaperCommandSource;
import net.crystalixs.core.paper.command.cloud.PaperPlayerCommandSource;
import net.crystalixs.core.paper.economy.EconomyErrorMessageMapper;
import net.crystalixs.core.paper.economy.EconomyException;
import net.crystalixs.core.paper.economy.EconomyService;
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
    private final EconomyErrorMessageMapper errorMapper;

    public CoinsCommand(CorePlugin plugin, EconomyService service, EconomyErrorMessageMapper errorMapper) {
        super(plugin);
        this.service = service;
        this.errorMapper = errorMapper;
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
                        context.sender().sendMessage(translatable(errorMapper.keyForCoins(exception.error())));
                    }
                }));
    }
}
