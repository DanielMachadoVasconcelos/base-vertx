package br.ead.home.vertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import lombok.extern.slf4j.Slf4j;
import br.ead.home.vertx.services.TemperatureSensor;
import br.ead.home.vertx.utils.EventBusAddresses;

@Slf4j
public class Application extends AbstractVerticle {

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        vertx.createHttpServer()
                .requestHandler(req -> req.response()
                        .putHeader("content-type", "text/plain")
                        .end("Hello World!"))
                .listen(8888)
                .onSuccess(http -> {
                    startPromise.complete();
                    log.info("HTTP server started on port 8888");
                }).onFailure(error -> {
                    startPromise.fail(error.getCause());
                    log.error("Failed to start HTTP server!", error);
                });

        vertx.deployVerticle(new TemperatureSensor());
        vertx.eventBus().consumer(EventBusAddresses.TEMPERATURES,
                message -> log.info("Current Temperature: " + message.body()));
    }
}
