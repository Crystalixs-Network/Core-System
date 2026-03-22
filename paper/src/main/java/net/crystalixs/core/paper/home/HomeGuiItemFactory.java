package net.crystalixs.core.paper.home;

import net.crystalixs.core.persistence.model.HomeModel;
import org.bukkit.Material;
import xyz.xenondevs.inventoryaccess.component.AdventureComponentWrapper;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.builder.ItemBuilder;

import static net.kyori.adventure.text.Component.empty;

public final class HomeGuiItemFactory {

    public ItemProvider homeItem(HomeModel model) {
        return new ItemBuilder(Material.GRASS_BLOCK)
                .setDisplayName(model.name());
    }

    public ItemProvider lockedItem() {
        return new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
                .setDisplayName(new AdventureComponentWrapper(empty()));
    }

}
