package net.crystalixs.core.paper.home;

import net.kyori.adventure.translation.GlobalTranslator;
import org.bukkit.entity.Player;
import xyz.xenondevs.inventoryaccess.component.AdventureComponentWrapper;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.window.Window;

import static net.kyori.adventure.text.Component.translatable;

public sealed interface HomeGui permits HomeListGui, HomeEditGui {

    default void open(Player player) {
        Gui gui = buildGui(player);
        var renderedTitle = GlobalTranslator.render(translatable(this::titleKey), player.locale());

        Window.single()
                .setViewer(player)
                .setTitle(new AdventureComponentWrapper(renderedTitle))
                .setGui(gui)
                .open(player);
    }

    String titleKey();

    Gui buildGui(Player player);

}
