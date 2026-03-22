package net.crystalixs.core.paper.home;

import org.bukkit.entity.Player;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.window.Window;

public final class HomeGuiFactory {

    public void open(Player player) {
        Gui gui = Gui.normal()
                .setStructure(
                        "# # # # # # # # #",
                        "# # # # # # # # #",
                        "# # # # # # # # #"
                )
                .build();

        Window.single()
                .setViewer(player)
                .setTitle("Homes")
                .setGui(gui)
                .open(player);
    }
}
