package net.crystalixs.core.velocity.listener;

import io.lettuce.core.RedisClient;
import io.lettuce.core.codec.StringCodec;
import io.lettuce.core.pubsub.RedisPubSubAdapter;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import net.crystalixs.core.common.command.help.NetworkHelpCatalog;
import net.crystalixs.core.common.command.help.NetworkHelpCatalogCodec;
import net.crystalixs.core.common.command.help.NetworkHelpSyncProtocol;
import net.crystalixs.core.common.logging.LogMetadata;
import net.crystalixs.core.common.logging.StructuredLogger;
import net.crystalixs.core.velocity.help.BackendHelpCatalogCache;

import java.util.Base64;

public final class BackendHelpCatalogRedisSubscriber implements AutoCloseable {

    private static final String DEFAULT_REDIS_URI = "redis://localhost:6379";

    private final BackendHelpCatalogCache cache;
    private final StructuredLogger logger;
    private final String redisUri;

    private RedisClient client;
    private StatefulRedisPubSubConnection<String, String> connection;

    public BackendHelpCatalogRedisSubscriber(BackendHelpCatalogCache cache, StructuredLogger logger, String redisUri) {
        this.cache = cache;
        this.logger = logger;
        this.redisUri = (redisUri == null || redisUri.isBlank()) ? DEFAULT_REDIS_URI : redisUri;
    }

    public void start() {
        try {
            this.client = RedisClient.create(redisUri);
            this.connection = client.connectPubSub(StringCodec.UTF8);
            this.connection.addListener(new RedisPubSubAdapter<>() {
                @Override
                public void message(String channel, String message) {
                    if (!NetworkHelpSyncProtocol.REDIS_TOPIC.equals(channel)) {
                        return;
                    }
                    byte[] payload = Base64.getDecoder().decode(message);
                    NetworkHelpCatalog catalog = NetworkHelpCatalogCodec.decode(payload);
                    cache.upsert(catalog);
                }
            });
            this.connection.sync().subscribe(NetworkHelpSyncProtocol.REDIS_TOPIC);

            logger.info("help catalog redis subscriber started", LogMetadata
                    .event("help.sync.redis.subscriber_started")
                    .and(LogMetadata.Key.DESCRIPTION, redisUri));

        } catch (Exception exception) {
            logger.error("failed to start help catalog redis subscriber", LogMetadata
                    .event("help.sync.redis.subscriber_start_failed")
                    .and(LogMetadata.Key.DESCRIPTION, redisUri), exception);
        }
    }

    @Override
    public void close() {
        if (connection != null) {
            connection.close();
            connection = null;
        }
        if (client != null) {
            client.shutdown();
            client = null;
        }
    }
}
