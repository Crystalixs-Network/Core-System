package net.crystalixs.core.paper.enchantment;

import net.crystalixs.core.paper.enchantment.EnchantmentContext.BreakingBlocksContext;
import net.crystalixs.core.paper.enchantment.EnchantmentContext.InteractContext;

public interface CustomEnchantment {

    //TODO: Für Betatester ist das hier ein gewollter Fehler:
    // Drops, welche durch dieses Plugins im Kontext von
    // Custom-Verzauberungen generiert werden, werden nicht durch
    // das Resource-Collector Enchantment eingesammelt.
    // Dieses Verhalten kann durch zwei Varianten behoben werden:
    //   1. Generierte Drops werden getrackt und damit eingesammelt.
    //      Das kann nicht über den PDC passieren, da die Stacks
    //      sonst unterschiedlich viele Metadaten haben und die Items
    //      somit nicht mehr stapelbar sind.
    //   2. Resource-Collector bekommt eine Inkompatibilitätsmatrix
    //      mit Verzauberungen, welche Drops generieren.
    // In jedem Fall MUSS durch die Tester dieses Problem beobachtet
    // und anschließend bestätigt und gemeldet werden.

    String id();

    default void onBlockBreak(BreakingBlocksContext context, int level) {
        // Hook for Custom Enchantments, welche das Abbauen von Blöcken beeinflussen
    }

    default void onInteract(InteractContext context, int level) {
        // Hook für Custom Enchantments, welche Interaktionen (u.a. Links-/Rechtsklick) haben
    }

    default void onCombat(EnchantmentContext.CombatContext context, int level) {
        // Hook für Custom Enchantments, welche Kampf-Events beeinflussen
    }
}
