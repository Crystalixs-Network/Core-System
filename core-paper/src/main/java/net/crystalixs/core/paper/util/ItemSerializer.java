package net.crystalixs.core.paper.util;

import org.bukkit.inventory.ItemStack;

import java.io.*;

public final class ItemSerializer {

    private ItemSerializer() {
    }

    public static byte[] serialize(ItemStack[] content) {
        try (var stream = new ByteArrayOutputStream(); var data = new DataOutputStream(stream)) {
            data.writeInt(content.length);

            for (ItemStack itemStack : content) {
                if (itemStack == null || itemStack.getType().isAir()) {
                    data.writeInt(-1);
                    continue;
                }

                byte[] bytes = itemStack.serializeAsBytes();
                data.writeInt(bytes.length);
                data.write(bytes);
            }
            return stream.toByteArray();

        } catch (IOException exception) {
            throw new IllegalStateException("Could not serialize content", exception);
        }
    }

    public static ItemStack[] deserialize(byte[] payload) {
        try (var stream = new ByteArrayInputStream(payload); var data = new DataInputStream(stream)) {
            int length = data.readInt();
            if (length <= 0) return new ItemStack[0];

            ItemStack[] content = new ItemStack[length];
            for (int slot = 0; slot < length; slot++) {
                int itemLength = data.readInt();
                if (itemLength < 0) {
                    content[slot] = null;
                    continue;
                }

                byte[] bytes = data.readNBytes(itemLength);
                if (bytes.length != itemLength) {
                    throw new IllegalStateException("Unexpected end of storage payload");
                }
                content[slot] = ItemStack.deserializeBytes(bytes);
            }
            return content;

        } catch (IOException exception) {
            throw new IllegalStateException("Could not deserialize content", exception);
        }
    }
}
