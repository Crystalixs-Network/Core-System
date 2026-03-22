package net.crystalixs.core.paper.home;

import net.crystalixs.core.persistence.model.HomeModel;
import org.bukkit.entity.Player;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.item.impl.SimpleItem;
import xyz.xenondevs.invui.window.Window;

import java.util.ArrayList;

public final class HomeGuiFactory {

    private static final char[] HOME_SLOT_KEYS = {
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i',
            'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r',
            's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '1'
    };

    private final HomeService service;

    public HomeGuiFactory(HomeService service) {
        this.service = service;
    }

    public void open(Player player) {
        int limit = HomeLimitResolver.resolve(player);
        var homes = service.all(player.getUniqueId());
        var orderedHomes = new ArrayList<>(homes);

        Gui.Builder.Normal normal = Gui.normal()
                .setStructure(
                        "a b c d e f g h i",
                        "j k l m n o p q r",
                        "s t u v w x y z 1"
                );

        HomeGuiItemFactory factory = new HomeGuiItemFactory();
        for (int slot = 0; slot <= HOME_SLOT_KEYS.length; slot++) {
            char key = HOME_SLOT_KEYS[slot];

            if (slot < orderedHomes.size()) {
                HomeModel model = orderedHomes.get(slot);
                normal.addIngredient(key, new SimpleItem(factory.homeItem(model)));
                continue;
            }
            if (slot >= limit) {
                normal.addIngredient(key, new SimpleItem(factory.lockedItem()));
            }
        }

        Window.single()
                .setViewer(player)
                .setTitle("Homes")
                .setGui(normal.build())
                .open(player);
    }
}
