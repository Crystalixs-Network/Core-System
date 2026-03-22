package net.crystalixs.core.paper.home;

import net.crystalixs.core.persistence.model.HomeModel;
import org.bukkit.Material;
import xyz.xenondevs.inventoryaccess.component.AdventureComponentWrapper;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.builder.ItemBuilder;

import static net.kyori.adventure.text.Component.empty;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.GRAY;
import static net.kyori.adventure.text.format.NamedTextColor.GREEN;

public final class HomeGuiItemFactory {

    public ItemProvider icon(HomeModel model) {
        return new ItemBuilder(Material.GRASS_BLOCK)
                .setDisplayName(new AdventureComponentWrapper(text(model.name(), GREEN)));
    }

    public ItemProvider available() {
        return new ItemBuilder(Material.BARRIER)
                .setDisplayName(new AdventureComponentWrapper(text("Freier Home-Slot", GRAY)));
    }

    public ItemProvider locked() {
        return new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
                .setDisplayName(new AdventureComponentWrapper(empty()));
    }

}
