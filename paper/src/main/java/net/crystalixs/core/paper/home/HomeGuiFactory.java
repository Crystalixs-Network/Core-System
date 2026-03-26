package net.crystalixs.core.paper.home;

import net.crystalixs.core.common.logging.StructuredLogger;
import net.crystalixs.core.paper.CorePlugin;
import net.crystalixs.core.paper.home.gui.*;
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
            gui = new HomeEditGui(service, factory, this, model);

        } else if (guiClass == HomeIconSelectionGui.class) {
            if (model == null) {
                throw new IllegalArgumentException("HomeIconSelectionGui requires a home model");
            }
            gui = new HomeIconSelectionGui(factory, this, model);

        } else {
            throw new IllegalArgumentException("Unsupported GUI class: " + guiClass.getName());
        }

        gui.open(player);
    }

    public void openIconSelection(@NotNull Player player, @NotNull HomeModel model) {
        open(player, HomeIconSelectionGui.class, model);
    }

    public void openRename(@NotNull Player player, @NotNull HomeModel model) {
        HomeGuiItemFactory factory = new HomeGuiItemFactory(player);
        new RenameHomeGui(plugin, service, factory, model).open(player);
    }

    public void openList(@NotNull Player player) {
        open(player, HomeListGui.class, null);
    }

    public void openEdit(@NotNull Player player, @NotNull HomeModel model) {
        open(player, HomeEditGui.class, model);
    }

    public HomeService service() {
        return service;
    }

    public StructuredLogger logger(String... path) {
        return plugin.componentLogger(String.join(".", path));
    }
}
