package br.ead.home.vertx;

import br.ead.home.vertx.configuration.ConfigurationLoader;
import br.ead.home.vertx.persistance.migration.FlywayMigration;
import br.ead.home.vertx.verticles.RestApiVerticle;
import br.ead.home.vertx.verticles.VersionInfoVerticle;
import io.vertx.core.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Application extends AbstractVerticle {

    public static void main(String[] args) {
        var vertx = Vertx.vertx();
        vertx.exceptionHandler(error -> log.error("Unhandled error: {}", error.getMessage(), error));
        vertx.deployVerticle(new Application())
                .onFailure(error -> log.error("Failed to deploy:", error))
                .onSuccess(id -> log.info("Deployed {} with id {}", Application.class.getSimpleName(), id));
    }

    @Override
    public void start(Promise<Void> startPromise) {
        vertx.deployVerticle(VersionInfoVerticle.class.getName())
                .onFailure(startPromise::fail)
                .onSuccess(id -> log.info("Deployed {} with id {}", VersionInfoVerticle.class.getSimpleName(), id))
                .compose(next -> migrateDatabase())
                .onFailure(startPromise::fail)
                .onSuccess(id -> log.info("Migrated db schema to latest version!"))
                .compose(next -> deployRestApiVerticle(startPromise));
    }

    private Future<Void> migrateDatabase() {
        return ConfigurationLoader.load(vertx)
                .compose(config -> FlywayMigration.migrate(vertx, config.getDatabaseConfiguration()));
    }

    private Future<String> deployRestApiVerticle(final Promise<Void> startPromise) {
        DeploymentOptions deploymentOptions = new DeploymentOptions().setInstances(halfProcessors());
        return vertx.deployVerticle(RestApiVerticle.class.getName(), deploymentOptions)
                .onFailure(startPromise::fail)
                .onSuccess(id -> {
                    log.info("Deployed {} with id {}", RestApiVerticle.class.getSimpleName(), id);
                    startPromise.complete();
                });
    }

    private int halfProcessors() {
        return Math.max(1, Runtime.getRuntime().availableProcessors() / 2);
    }
}
