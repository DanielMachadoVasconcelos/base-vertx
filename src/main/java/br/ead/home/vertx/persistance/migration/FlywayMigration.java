package br.ead.home.vertx.persistance.migration;

import br.ead.home.vertx.configuration.DatabaseConfiguration;
import lombok.extern.log4j.Log4j2;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationInfo;

import io.vertx.core.Future;
import io.vertx.core.Vertx;

@Log4j2
public class FlywayMigration {

    public static Future<Void> migrate(final Vertx vertx, final DatabaseConfiguration dbConfig) {
        log.debug("DB Config: {}", dbConfig);
        return vertx.<Void>executeBlocking(promise -> {
            // Flyway migration is blocking => uses JDBC
            execute(dbConfig);
            promise.complete();
        }).onFailure(err -> log.error("Failed to migrate db schema with error: ", err));
    }

    private static void execute(final DatabaseConfiguration dbConfig) {
        var database = "postgresql";
        //var database = "mysql";
        final String jdbcUrl = String.format("jdbc:%s://%s:%d/%s",
                database,
                dbConfig.getHost(),
                dbConfig.getPort(),
                dbConfig.getDatabase()
        );

        log.debug("Migrating DB schema using jdbc url: {}", jdbcUrl);

        final Flyway flyway = Flyway.configure()
                .dataSource(jdbcUrl, dbConfig.getUser(), dbConfig.getPassword())
                .schemas("broker")
                .defaultSchema("broker")
                .load();

        var current = Optional.ofNullable(flyway.info().current());
        current.ifPresent(info -> log.info("db schema is at version: {}", info.getVersion()));

        var pendingMigrations = flyway.info().pending();
        log.debug("Pending migrations are: {}", printMigrations(pendingMigrations));

        flyway.migrate();
    }

    private static String printMigrations(final MigrationInfo[] pending) {
        if (Objects.isNull(pending)) {
            return "[]";
        }

        return Arrays.stream(pending)
                .map(each -> each.getVersion() + " - " + each.getDescription())
                .collect(Collectors.joining(",", "[", "]"));
    }

}
