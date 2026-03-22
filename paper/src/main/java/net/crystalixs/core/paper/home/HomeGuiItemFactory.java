package net.crystalixs.core.paper.home;

import net.crystalixs.core.persistence.model.HomeModel;
import org.bukkit.Material;
import xyz.xenondevs.inventoryaccess.component.AdventureComponentWrapper;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.builder.ItemBuilder;

import static net.kyori.adventure.text.Component.empty;

public final class HomeGuiItemFactory {

    public ItemProvider icon(HomeModel model) {
        return new ItemBuilder(Material.GRASS_BLOCK)
                .setDisplayName(model.name());
    }

    public ItemProvider available() {
        return new ItemBuilder(Material.BARRIER)
                .setDisplayName(new AdventureComponentWrapper(empty()));
    }

    public ItemProvider locked() {
        return new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
                .setDisplayName(new AdventureComponentWrapper(empty()));
    }

}
