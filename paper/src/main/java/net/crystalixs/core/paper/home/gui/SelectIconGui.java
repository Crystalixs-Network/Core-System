package net.crystalixs.core.paper.home.gui;

import net.crystalixs.core.paper.home.HomeGuiFactory;
import net.crystalixs.core.paper.home.HomeGuiItemFactory;
import net.crystalixs.core.paper.home.gui.item.HomeEditGuiReturnToListItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.item.builder.ItemBuilder;

public final class SelectIconGui implements HomeGui {

    private static final char[] ITEM_SLOTS = {
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i',
            'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r',
            's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '1'
    };

    private final HomeGuiItemFactory itemFactory;
    private final HomeGuiFactory guiFactory;

    public SelectIconGui(HomeGuiItemFactory itemFactory, HomeGuiFactory guiFactory) {
        this.itemFactory = itemFactory;
        this.guiFactory = guiFactory;
    }

    @Override
    public String titleKey() {
        return "command.home.ui.edit.select-icon.title";
    }

    @Override
    public Gui buildGui(Player player) {
        var normal = Gui.normal().setStructure(
                "a b c d e f g h i",
                "j k l m n o p q r",
                "s t u v w x y z 1",
                "2 # # # # # # # #"
        );

        for (int slot = 0; slot < ITEM_SLOTS.length; slot++) {
            Material material = IconCatalog.ICONS.get(slot);
            normal.addIngredient(ITEM_SLOTS[slot], new ItemBuilder(material));
        }
        normal.addIngredient('2', new HomeEditGuiReturnToListItem(itemFactory, guiFactory));
        normal.addIngredient('#', new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE));

        return normal.build();
    }
}
