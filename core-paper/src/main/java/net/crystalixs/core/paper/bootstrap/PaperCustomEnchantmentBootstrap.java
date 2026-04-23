package net.crystalixs.core.paper.bootstrap;

import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.data.EnchantmentRegistryEntry;
import io.papermc.paper.registry.event.RegistryComposeEvent;
import io.papermc.paper.registry.event.RegistryEvents;
import io.papermc.paper.registry.keys.EnchantmentKeys;
import io.papermc.paper.registry.keys.ItemTypeKeys;
import io.papermc.paper.registry.keys.tags.ItemTypeTagKeys;
import io.papermc.paper.registry.set.RegistryKeySet;
import io.papermc.paper.registry.set.RegistrySet;
import net.crystalixs.core.paper.enchantment.CustomEnchantment;
import net.crystalixs.core.paper.enchantment.impl.*;
import net.kyori.adventure.text.Component;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemType;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

import static net.kyori.adventure.key.Key.key;
import static net.kyori.adventure.text.Component.text;

@SuppressWarnings("all")
public final class PaperCustomEnchantmentBootstrap {

    private static final Collection<Registration> REGISTRATIONS = List.of(
            new Registration("timber", text("Timber"), 3, event -> event.getOrCreateTag(ItemTypeTagKeys.AXES)),
            new Registration("wood_whisper", text("Wood Whisper"), 3, event -> event.getOrCreateTag(ItemTypeTagKeys.AXES)),
            new Registration("leaf_mining", text("Leaf Mining"), 3, event -> RegistrySet.keySet(RegistryKey.ITEM, ItemTypeKeys.SHEARS)),
            new Registration("vein_mining", text("Vein Mining"), 3, event -> event.getOrCreateTag(ItemTypeTagKeys.PICKAXES)),
            new Registration("stone_mining", text("Stone Mining"), 3, event -> event.getOrCreateTag(ItemTypeTagKeys.PICKAXES)),
            new Registration("drill", text("Drill"), 3, event -> event.getOrCreateTag(ItemTypeTagKeys.PICKAXES)),
            new Registration("earth_whisper", text("Earth Whisper"), 3, event -> event.getOrCreateTag(ItemTypeTagKeys.SHOVELS)),
            new Registration("plow", text("Pflug"), 3, event -> event.getOrCreateTag(ItemTypeTagKeys.HOES))
    );

    public Collection<CustomEnchantment> createEnchantments(String backendId) {
        return List.of(
                new TimberEnchantment(),
                new WoodWhisperEnchantment(),
                new LeafMiningEnchantment(),
                new VeinMiningEnchantment(),
                new StoneMiningEnchantment(),
                new DrillEnchantment(backendId),
                new EarthWhisperEnchantment(),
                new PlowEnchantment(),
                new StorageEnchantment(),
                new PurseEnchantment(),
                new ResourceCollectorEnchantment(),
                new GlassBreakerEnchantment(),
                new SmeltingTouchEnchantment()
        );
    }

    public void registerEnchantments(BootstrapContext context) {
        context.getLifecycleManager().registerEventHandler(LifecycleEvents.DATAPACK_DISCOVERY.newHandler(event -> {
            try {
                event.registrar().discoverPack(PaperCustomEnchantmentBootstrap.class.getResource("/core_datapack").toURI(), "core-tags");
            } catch (Exception exception) {
                throw new RuntimeException(exception);
            }
        }));
        context.getLifecycleManager().registerEventHandler(RegistryEvents.ENCHANTMENT.compose().newHandler(event -> {
            for (Registration registration : REGISTRATIONS) {
                event.registry().register(
                        EnchantmentKeys.create(key("core:" + registration.id())),
                        builder -> builder
                                .description(registration.displayName())
                                .supportedItems(registration.supportedItems().apply(event))
                                .weight(1024)
                                .anvilCost(1)
                                .maxLevel(registration.maxLevel())
                                .minimumCost(EnchantmentRegistryEntry.EnchantmentCost.of(1, 10))
                                .maximumCost(EnchantmentRegistryEntry.EnchantmentCost.of(30, 10))
                                .activeSlots(EquipmentSlotGroup.HAND)
                                .primaryItems(null)
                );
            }
        }));
    }

    private record Registration(String id, Component displayName, int maxLevel, Function<RegistryComposeEvent<Enchantment, EnchantmentRegistryEntry.Builder>, RegistryKeySet<ItemType>> supportedItems) {
    }
}
