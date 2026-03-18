package net.crystalixs.core.paper.command;

import net.crystalixs.core.paper.CorePlugin;
import net.crystalixs.core.paper.command.cloud.PaperCommand;
import net.crystalixs.core.paper.command.cloud.PaperCommandSource;
import net.crystalixs.core.paper.command.cloud.PaperPlayerCommandSource;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.tag.resolver.Formatter;
import net.kyori.adventure.translation.GlobalTranslator;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.minecraft.extras.RichDescription;
import org.incendo.cloud.permission.Permission;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Locale;

import static net.kyori.adventure.text.Component.empty;
import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.minimessage.translation.Argument.component;
import static net.kyori.adventure.text.minimessage.translation.Argument.tagResolver;

public final class SignCommand extends PaperCommand {

    private final NamespacedKey key;

    public SignCommand(CorePlugin plugin) {
        super(plugin);
        this.key = new NamespacedKey(plugin, "item_signed");
    }

    @Override
    public void registerTo(@NotNull CommandManager<PaperCommandSource> commandManager) {
        commandManager.command(commandManager.commandBuilder("sign")
                .commandDescription(RichDescription.translatable("command.sign.description.main"))
                .senderType(PaperPlayerCommandSource.class)
                .permission(Permission.of("core.command.sign"))
                .handler(context -> {
                    Player player = context.sender().player();

                    ItemStack itemStack = player.getInventory().getItemInMainHand();
                    if (itemStack.getType().isAir()) {
                        player.sendMessage(translatable("command.sign.error.no-item"));
                        return;
                    }

                    ItemMeta meta = itemStack.getItemMeta();
                    if (meta == null) {
                        player.sendMessage(translatable("command.sign.error.invalid-item"));
                        return;
                    }

                    if (meta.getPersistentDataContainer().has(key, PersistentDataType.BOOLEAN)) {
                        player.sendMessage(translatable("command.sign.error.already-signed"));
                        return;
                    }

                    var lore = meta.lore();
                    var newLore = lore == null ? new ArrayList<Component>() : new ArrayList<>(lore);

                    Locale locale = plugin.resolveTranslationLocale(player.locale());
                    var signatureLine = translatable("command.sign.lore")
                            .arguments(
                                    component("player", player.name()),
                                    tagResolver(Formatter.date("date", LocalDate.now()))
                            )
                            .decoration(TextDecoration.ITALIC, false);

                    var renderedLine = GlobalTranslator.render(signatureLine, locale);

                    newLore.add(empty());
                    newLore.add(renderedLine);

                    meta.lore(newLore);
                    meta.getPersistentDataContainer().set(key, PersistentDataType.BOOLEAN, true);

                    itemStack.setItemMeta(meta);
                    player.sendMessage(translatable("command.sign.success"));
                }));
    }
}
