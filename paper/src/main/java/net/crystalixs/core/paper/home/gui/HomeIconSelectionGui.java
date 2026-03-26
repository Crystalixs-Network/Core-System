package net.crystalixs.core.paper.home.gui;

import net.crystalixs.core.paper.home.HomeGuiFactory;
import net.crystalixs.core.paper.home.HomeGuiItemFactory;
import net.crystalixs.core.paper.home.gui.item.HomeEditGuiReturnToEditItem;
import net.crystalixs.core.paper.home.gui.item.HomeIconChoiceItem;
import net.crystalixs.core.persistence.model.HomeModel;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.item.builder.ItemBuilder;

public final class HomeIconSelectionGui implements HomeGui {

    private static final char[] ITEM_SLOTS = {
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i',
            'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r',
            's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '1'
    };

    private final HomeGuiItemFactory itemFactory;
    private final HomeGuiFactory guiFactory;
    private final HomeModel model;

    public HomeIconSelectionGui(HomeGuiItemFactory itemFactory, HomeGuiFactory guiFactory, HomeModel model) {
        this.itemFactory = itemFactory;
        this.guiFactory = guiFactory;
        this.model = model;
    }

    @Override
    public String titleKey() {
        return "command.home.ui.edit.select-icon.title";
    }

    @Override
    public Gui buildGui(Player player) {
        var logger = guiFactory.logger("home", "gui", "icon");
        var normal = Gui.normal().setStructure(
                "a b c d e f g h i",
                "j k l m n o p q r",
                "s t u v w x y z 1",
                "2 # # # # # # # #"
        );

        for (int slot = 0; slot < ITEM_SLOTS.length; slot++) {
            Material material = HomeIconCatalog.ICONS.get(slot);
            normal.addIngredient(ITEM_SLOTS[slot], new HomeIconChoiceItem(itemFactory, guiFactory, model, material, logger));
        }
        normal.addIngredient('2', new HomeEditGuiReturnToEditItem(itemFactory, guiFactory, model));
        normal.addIngredient('#', new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE));

        return normal.build();
    }
}
