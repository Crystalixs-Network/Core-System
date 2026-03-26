package net.crystalixs.core.paper.home;

import net.crystalixs.core.common.logging.StructuredLogger;
import net.crystalixs.core.paper.CorePlugin;
import net.crystalixs.core.paper.home.gui.HomeEditGui;
import net.crystalixs.core.paper.home.gui.HomeIconSelectionGui;
import net.crystalixs.core.paper.home.gui.HomeListGui;
import net.crystalixs.core.paper.home.gui.RenameHomeGui;
import net.crystalixs.core.persistence.model.HomeModel;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class HomeGuiFactory {

    private final CorePlugin plugin;
    private final HomeService service;

    public HomeGuiFactory(CorePlugin plugin, HomeService service) {
        this.plugin = plugin;
        this.service = service;
    }

    public void openIconSelection(@NotNull Player player, @NotNull HomeModel model) {
        HomeGuiItemFactory factory = new HomeGuiItemFactory(player);
        new HomeIconSelectionGui(factory, this, model).open(player);
    }

    public void openRename(@NotNull Player player, @NotNull HomeModel model) {
        HomeGuiItemFactory factory = new HomeGuiItemFactory(player);
        new RenameHomeGui(plugin, service, factory, model).open(player);
    }

    public void openList(@NotNull Player player) {
        HomeGuiItemFactory factory = new HomeGuiItemFactory(player);
        new HomeListGui(service, factory, this).open(player);
    }

    public void openEdit(@NotNull Player player, @NotNull HomeModel model) {
        HomeGuiItemFactory factory = new HomeGuiItemFactory(player);
        new HomeEditGui(service, factory, this, model).open(player);
    }

    public HomeService service() {
        return service;
    }

    public StructuredLogger logger(String... path) {
        return plugin.componentLogger(String.join(".", path));
    }
}
