package net.crystalixs.core.paper;

import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.data.EnchantmentRegistryEntry;
import io.papermc.paper.registry.event.RegistryEvents;
import io.papermc.paper.registry.keys.EnchantmentKeys;
import io.papermc.paper.registry.keys.tags.ItemTypeTagKeys;
import io.papermc.paper.registry.set.RegistrySet;
import net.kyori.adventure.key.Key;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.Component.text;

public final class CorePluginBootstrap implements PluginBootstrap {

    @Override
    public void bootstrap(@NotNull BootstrapContext context) {
        context.getLifecycleManager().registerEventHandler(RegistryEvents.ENCHANTMENT.compose().newHandler(event -> {
            event.registry().register(
                    EnchantmentKeys.create(Key.key("core:timber")),
                    builder -> builder.description(text("Timber"))
                            .supportedItems(event.getOrCreateTag(ItemTypeTagKeys.AXES))
                            .maxLevel(3)
                            .weight(1024)
                            .anvilCost(1)
                            .minimumCost(EnchantmentRegistryEntry.EnchantmentCost.of(1, 10))
                            .maximumCost(EnchantmentRegistryEntry.EnchantmentCost.of(30, 10))
                            .primaryItems(RegistrySet.keySet(RegistryKey.ITEM))
                            .activeSlots(EquipmentSlotGroup.HAND)
            );
        }));
    }
}
