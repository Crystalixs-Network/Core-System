package net.crystalixs.core.velocity.config.jackson;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Nullable;
import tools.jackson.core.Version;
import tools.jackson.databind.JacksonModule;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.module.SimpleDeserializers;
import tools.jackson.databind.module.SimpleSerializers;

public class JacksonVelocity extends JacksonModule {

    private final @Nullable ValueSerializer<Component> componentJsonSerializer;
    private final @Nullable ValueDeserializer<Component> componentJsonDeserializer;

    public JacksonVelocity(@Nullable ValueSerializer<Component> componentJsonSerializer, @Nullable ValueDeserializer<Component> componentJsonDeserializer) {
        this.componentJsonSerializer = componentJsonSerializer;
        this.componentJsonDeserializer = componentJsonDeserializer;
    }

    public static JacksonVelocityBuilder builder() {
        return new JacksonVelocityBuilder();
    }

    @Override
    public String getModuleName() {
        return "JacksonVelocity";
    }

    @Override
    public Version version() {
        return new Version(1, 0, 0, "", "net.crystalixs.core", "velocity");
    }

    @Override
    public void setupModule(SetupContext context) {
        SimpleSerializers serializers = new SimpleSerializers();
        serializers.addSerializer(Component.class, componentJsonSerializer);

        SimpleDeserializers deserializers = new SimpleDeserializers();
        deserializers.addDeserializer(Component.class, componentJsonDeserializer);

        context.addSerializers(serializers);
        context.addDeserializers(deserializers);
    }
}
