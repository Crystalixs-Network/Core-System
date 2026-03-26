package net.crystalixs.core.paper.home.gui;

import org.bukkit.entity.Player;
import xyz.xenondevs.invui.gui.Gui;

public final class SelectIconGui implements HomeGui {

    @Override
    public String titleKey() {
        return "command.home.ui.edit.select-icon.title";
    }

    @Override
    public Gui buildGui(Player player) {
        return Gui.normal().build();
    }
}
