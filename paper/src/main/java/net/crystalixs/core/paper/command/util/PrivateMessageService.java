package net.crystalixs.core.paper.command.util;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class PrivateMessageService {

    private final Map<UUID, UUID> lastMessagedBySender = new HashMap<>();

    public void rememberConversation(Player sender, Player receiver) {
        lastMessagedBySender.put(sender.getUniqueId(), receiver.getUniqueId());
        lastMessagedBySender.put(receiver.getUniqueId(), sender.getUniqueId());
    }

    public void shutdown() {
        lastMessagedBySender.clear();
    }
}
