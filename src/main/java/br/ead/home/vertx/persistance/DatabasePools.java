package br.ead.home.vertx.persistance;

import br.ead.home.vertx.configuration.Configuration;
import io.vertx.core.Vertx;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.PoolOptions;
import lombok.experimental.UtilityClass;

@UtilityClass
public class DatabasePools {

    public static Pool createPostgresPool(Configuration configuration, Vertx vertx) {
        final var connectOptions = new PgConnectOptions()
                .setHost(configuration.getDatabaseConfiguration().getHost())
                .setPort(configuration.getDatabaseConfiguration().getPort())
                .setDatabase(configuration.getDatabaseConfiguration().getDatabase())
                .setUser(configuration.getDatabaseConfiguration().getUser())
                .setPassword(configuration.getDatabaseConfiguration().getPassword());

        var poolOptions = new PoolOptions().setMaxSize(4);
        return PgPool.pool(vertx, connectOptions, poolOptions);
    }
}
