package net.crystalixs.core.velocity.config.jackson;

import net.crystalixs.core.velocity.config.jackson.serializer.ComponentMiniMessageDeserializer;
import net.crystalixs.core.velocity.config.jackson.serializer.ComponentMiniMessageSerializer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jetbrains.annotations.Nullable;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.ValueSerializer;

public class JacksonVelocityBuilder {

    private @Nullable ValueSerializer<Component> componentJsonSerializer;
    private @Nullable ValueDeserializer<Component> componentJsonDeserializer;

    public JacksonVelocityBuilder withMiniMessage(MiniMessage miniMessage) {
        this.componentJsonSerializer = new ComponentMiniMessageSerializer(miniMessage);
        this.componentJsonDeserializer = new ComponentMiniMessageDeserializer(miniMessage);
        return this;
    }

    public JacksonVelocityBuilder withMiniMessage() {
        this.componentJsonSerializer = new ComponentMiniMessageSerializer();
        this.componentJsonDeserializer = new ComponentMiniMessageDeserializer();
        return this;
    }

    public JacksonVelocity build() {
        return new JacksonVelocity(componentJsonSerializer, componentJsonDeserializer);
    }
}
