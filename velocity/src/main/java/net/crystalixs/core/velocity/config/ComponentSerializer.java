package net.crystalixs.core.velocity.config;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jspecify.annotations.NonNull;
import org.spongepowered.configurate.serialize.ScalarSerializer;
import org.spongepowered.configurate.serialize.SerializationException;

import java.lang.reflect.Type;
import java.util.function.Predicate;

public final class ComponentSerializer extends ScalarSerializer<Component> {

    private final MiniMessage miniMessage;

    public ComponentSerializer(MiniMessage miniMessage) {
        super(Component.class);
        this.miniMessage = miniMessage;
    }

    @Override
    public Component deserialize(@NonNull Type type, @NonNull Object obj) throws SerializationException {
        if (!(obj instanceof String input)) {
            throw new SerializationException(type, "Expected a string for Adventure COmponent but got " + obj.getClass().getName() + " instead.");
        }
        return miniMessage.deserialize(input);
    }

    @Override
    protected @NonNull String serialize(Component item, @NonNull Predicate<Class<?>> typeSupported) {
        if (item == null) return "";
        return miniMessage.serialize(item);
    }
}
