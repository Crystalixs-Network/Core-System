package net.crystalixs.core.paper.home;

import net.crystalixs.core.persistence.model.HomeModel;
import net.kyori.adventure.translation.GlobalTranslator;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import xyz.xenondevs.inventoryaccess.component.AdventureComponentWrapper;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.builder.ItemBuilder;

import static net.kyori.adventure.text.Component.*;
import static net.kyori.adventure.text.minimessage.translation.Argument.component;

public final class HomeGuiItemFactory {

    private final Player player;

    public HomeGuiItemFactory(Player player) {
        this.player = player;
    }

    public ItemProvider icon(HomeModel model) {
        var name = translatable("command.home.ui.item.home").arguments(component("name", text(model.name())));
        var renderedName = GlobalTranslator.render(name, player.locale());

        return new ItemBuilder(Material.GRASS_BLOCK).setDisplayName(new AdventureComponentWrapper(renderedName));
    }

    public ItemProvider available() {
        var name = translatable("command.home.ui.item.available");
        var renderedName = GlobalTranslator.render(name, player.locale());

        return new ItemBuilder(Material.BARRIER).setDisplayName(new AdventureComponentWrapper(renderedName));
    }

    public ItemProvider locked() {
        return new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setDisplayName(new AdventureComponentWrapper(empty()));
    }

    public ItemProvider back() {
        var name = translatable("command.home.ui.edit.item.back");
        var renderedName = GlobalTranslator.render(name, player.locale());

        return new ItemBuilder(Material.ARROW).setDisplayName(new AdventureComponentWrapper(renderedName));
    }

    public ItemProvider delete() {
        var name = translatable("command.home.ui.edit.item.delete");
        var renderedName = GlobalTranslator.render(name, player.locale());

        return new ItemBuilder(Material.BARRIER).setDisplayName(new AdventureComponentWrapper(renderedName));
    }

    public ItemProvider rename() {
        var name = translatable("command.home.ui.edit.item.rename");
        var renderedName = GlobalTranslator.render(name, player.locale());

        return new ItemBuilder(Material.NAME_TAG).setDisplayName(new AdventureComponentWrapper(renderedName));
    }

}
