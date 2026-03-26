package net.crystalixs.core.paper.home.gui;

import net.crystalixs.core.common.logging.StructuredLogger;
import net.crystalixs.core.paper.CorePlugin;
import net.crystalixs.core.paper.home.HomeGuiItemFactory;
import net.crystalixs.core.paper.home.HomeRenameExecutor;
import net.crystalixs.core.paper.home.HomeRenameFailureHandler;
import net.crystalixs.core.paper.home.HomeService;
import net.crystalixs.core.paper.home.gui.item.RenameHomeGuiConfirmationItem;
import net.crystalixs.core.persistence.model.HomeModel;
import net.kyori.adventure.translation.GlobalTranslator;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import xyz.xenondevs.inventoryaccess.component.AdventureComponentWrapper;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.window.AnvilWindow;

import java.util.concurrent.atomic.AtomicReference;

import static net.kyori.adventure.text.Component.translatable;

public final class RenameHomeGui {

    public static final String RENAME_ANVIL_PDC_KEY = "home_rename_anvil";

    private final CorePlugin plugin;
    private final HomeGuiItemFactory factory;
    private final HomeModel model;

    private final StructuredLogger logger;
    private final HomeRenameExecutor renameExecutor;
    private final HomeRenameFailureHandler renameFailureHandler;

    public RenameHomeGui(CorePlugin plugin, HomeService service, HomeGuiItemFactory factory, HomeModel model) {
        this.plugin = plugin;
        this.factory = factory;
        this.model = model;

        this.logger = plugin.componentLogger("home", "gui", "rename");

        this.renameExecutor = new HomeRenameExecutor(service);
        this.renameFailureHandler = new HomeRenameFailureHandler();
    }

    public void open(Player player) {
        NamespacedKey key = new NamespacedKey(plugin, RENAME_ANVIL_PDC_KEY);
        player.getPersistentDataContainer().set(key, PersistentDataType.BOOLEAN, true);

        var title = translatable("command.home.ui.edit.rename.title");
        var renderedTitle = GlobalTranslator.render(title, player.locale());
        var originalName = model.name();
        var pendingNewName = new AtomicReference<>(originalName);

        Gui gui = Gui.normal()
                .setStructure("i x r")
                .addIngredient('x', new ItemBuilder(Material.AIR))
                .addIngredient('i', factory.icon(model))
                .addIngredient('r', new RenameHomeGuiConfirmationItem(player, originalName, pendingNewName::get, renameExecutor, renameFailureHandler, logger))
                .build();

        AnvilWindow.single()
                .setViewer(player)
                .setTitle(new AdventureComponentWrapper(renderedTitle))
                .setGui(gui)
                .addRenameHandler(pendingNewName::set)
                .open(player);
    }
}
