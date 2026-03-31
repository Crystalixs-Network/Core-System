package net.crystalixs.core.paper.command.util;

import org.bukkit.Bukkit;
import org.bukkit.profile.PlayerTextures;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.concurrent.CompletableFuture;

public final class SkullTextureService {

    private final SkullTextureCodec coded = new SkullTextureCodec();

    public CompletableFuture<String> resolveTextureValue(String playerName) {
        return Bukkit.createProfileExact(null, playerName)
                .update().thenApply(updated -> {
                    PlayerTextures textures = updated.getTextures();
                    URL skinUrl = textures.getSkin();

                    return skinUrl == null ? null : coded.encodeFromUrl(skinUrl);
                });
    }

    private static final class SkullTextureCodec {
        String encodeFromUrl(URL skinUrl) {
            String payload = "{\"textures\":{\"SKIN\":{\"url\":\"%s\"}}}".formatted(skinUrl);
            return Base64.getEncoder().encodeToString(payload.getBytes(StandardCharsets.UTF_8));
        }
    }
}
