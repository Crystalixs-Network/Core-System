package net.crystalixs.core.paper.home;

import net.crystalixs.core.paper.CorePlugin;
import net.crystalixs.core.paper.home.gui.HomeEditGui;
import net.crystalixs.core.paper.home.gui.HomeGui;
import net.crystalixs.core.paper.home.gui.HomeListGui;
import net.crystalixs.core.persistence.model.HomeModel;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class HomeGuiFactory {

    private final CorePlugin plugin;
    private final HomeService service;

    public HomeGuiFactory(CorePlugin plugin, HomeService service) {
        this.plugin = plugin;
        this.service = service;
    }

    public void open(@NotNull Player player, @NotNull Class<? extends HomeGui> guiClass, @Nullable HomeModel model) {
        HomeGuiItemFactory factory = new HomeGuiItemFactory(player);
        HomeGui gui;

        if (guiClass == HomeListGui.class) {
            gui = new HomeListGui(service, factory, this);

        } else if (guiClass == HomeEditGui.class) {
            if (model == null) {
                throw new IllegalArgumentException("HomeEditGui requires a home model");
            }
            gui = new HomeEditGui(plugin, service, factory, this, model);

        } else {
            throw new IllegalArgumentException("Unsupported GUI class: " + guiClass.getName());
        }

        gui.open(player);
    }

    public HomeService service() {
        return service;
    }
}
