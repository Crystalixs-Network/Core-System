package net.crystalixs.core.paper.bootstrap;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.codec.StringCodec;
import net.crystalixs.core.common.command.help.NetworkHelpCatalog;
import net.crystalixs.core.common.command.help.NetworkHelpCatalogCodec;
import net.crystalixs.core.common.command.help.NetworkHelpSyncProtocol;
import net.crystalixs.core.common.logging.LogMetadata;
import net.crystalixs.core.common.logging.StructuredLogger;
import net.crystalixs.core.paper.command.cloud.PaperCommandSource;
import org.incendo.cloud.CommandManager;

import java.util.Base64;

public final class PaperHelpCatalogTransport implements AutoCloseable {

    private static final String DEFAULT_REDIS_URI = "redis://localhost:6379";

    private final StructuredLogger logger;
    private final PaperHelpCatalogPublisher publisher;
    private final String redisUri;

    private RedisClient client;
    private StatefulRedisConnection<String, String> connection;

    public PaperHelpCatalogTransport(StructuredLogger logger, PaperHelpCatalogPublisher publisher, String redisUri) {
        this.logger = logger;
        this.publisher = publisher;
        this.redisUri = (redisUri == null || redisUri.isBlank()) ? DEFAULT_REDIS_URI : redisUri;
    }

    public void connect() {
        try {
            this.client = RedisClient.create(redisUri);
            this.connection = client.connect(StringCodec.UTF8);

            logger.info("help catalog redis publisher connected", LogMetadata
                    .event("help.sync.redis.connected")
                    .and(LogMetadata.Key.DESCRIPTION, redisUri));

        } catch (Exception exception) {
            logger.error("failed to connect help catalog redis publisher", LogMetadata
                    .event("help.sync.redis.connect_failed")
                    .and(LogMetadata.Key.DESCRIPTION, redisUri), exception);
        }
    }

    public void publish(CommandManager<PaperCommandSource> commandManager) {
        if (connection == null) {
            logger.warn("help catalog publish skipped because redis is not connected", LogMetadata.event("help.sync.redis.not_connected"));
            return;
        }

        try {
            NetworkHelpCatalog catalog = publisher.snapshot(commandManager);
            byte[] payload = NetworkHelpCatalogCodec.encode(catalog);
            String encoded = Base64.getEncoder().encodeToString(payload);
            connection.sync().publish(NetworkHelpSyncProtocol.REDIS_TOPIC, encoded);

        } catch (Exception exception) {
            logger.warn("failed to publish backend help catalog to redis", LogMetadata.event("help.sync.redis.publish_failed"), exception);
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
