package net.crystalixs.core.paper.command.util;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class InventorySeeService {

    private final JavaPlugin plugin;

    public InventorySeeService(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void open(Player viewer, Player target, boolean canModify) {
    }
}
