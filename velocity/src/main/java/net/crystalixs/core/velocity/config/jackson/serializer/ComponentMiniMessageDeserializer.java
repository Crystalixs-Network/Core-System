package net.crystalixs.core.velocity.config.jackson.serializer;

import com.google.gson.Gson;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.type.MapType;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.UnaryOperator;

public class ComponentMiniMessageDeserializer extends ValueDeserializer<Component> {

    private static final Gson GSON = new Gson();
    private final MiniMessage miniMessage;

    public ComponentMiniMessageDeserializer(MiniMessage miniMessage) {
        this.miniMessage = miniMessage;
    }

    public ComponentMiniMessageDeserializer() {
        this(MiniMessage.builder().postProcessor(UnaryOperator.identity()).build());
    }

    @Override
    public Component deserialize(JsonParser parser, DeserializationContext serializers) throws JacksonException {
        JsonNode tree = serializers.readTree(parser);
        if (tree.isObject()) return parseTree(tree, serializers);
        return miniMessage.deserialize(serializers.readValue(parser, String.class));
    }

    private Component parseTree(JsonNode tree, DeserializationContext serializers) throws JacksonException {
        MapType type = serializers.getTypeFactory().constructMapType(HashMap.class, String.class, Objects.class);
        Map<String, Object> map = serializers.readTreeAsValue(tree, type);

        return GsonComponentSerializer.gson().deserialize(GSON.toJson(map));
    }
}
