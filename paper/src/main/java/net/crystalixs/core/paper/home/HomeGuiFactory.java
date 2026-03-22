package net.crystalixs.core.paper.home;

import org.bukkit.entity.Player;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.window.Window;

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

        Gui gui = Gui.normal()
                .setStructure(
                        "a b c d e f g h i",
                        "j k l m n o p q r",
                        "s t u v w x y z 1"
                )
                .build();

        Window.single()
                .setViewer(player)
                .setTitle("Homes")
                .setGui(gui)
                .open(player);
    }
}
