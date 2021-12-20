package br.ead.home.vertx.verticles;

import br.ead.home.vertx.configuration.ConfigurationLoader;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class VersionInfoVerticle extends AbstractVerticle {

    @Override
    public void start(final Promise<Void> startPromise) throws Exception {
        ConfigurationLoader.load(vertx)
                .onFailure(startPromise::fail)
                .onSuccess(configuration -> log.info("Current Application Version is: {}", configuration.getVersion()))
                .onSuccess(configuration -> startPromise.complete());
    }
}