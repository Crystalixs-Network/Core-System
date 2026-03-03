package net.crystalixs.core.velocity.config.jackson.serializer;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;

public class ComponentMiniMessageSerializer extends ValueSerializer<Component> {

    private final MiniMessage miniMessage;

    public ComponentMiniMessageSerializer() {
        this(MiniMessage.miniMessage());
    }

    public ComponentMiniMessageSerializer(MiniMessage miniMessage) {
        this.miniMessage = miniMessage;
    }

    @Override
    public void serialize(Component value, JsonGenerator gen, SerializationContext serializers) throws JacksonException {
        gen.writeString(miniMessage.serialize(value));
    }
}
