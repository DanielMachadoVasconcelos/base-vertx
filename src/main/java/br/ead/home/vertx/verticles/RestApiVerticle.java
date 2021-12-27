package br.ead.home.vertx.verticles;

import br.ead.home.vertx.configuration.Configuration;
import br.ead.home.vertx.configuration.ConfigurationLoader;
import br.ead.home.vertx.controllers.assets.AssetsRestApi;
import br.ead.home.vertx.controllers.quotes.QuotesRestApi;
import br.ead.home.vertx.controllers.watchlist.WatchListRestApi;
import br.ead.home.vertx.persistance.DatabasePools;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.sqlclient.Pool;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class RestApiVerticle extends AbstractVerticle {

    @Override
    public void start(final Promise<Void> startPromise) {
        ConfigurationLoader.load(vertx)
                .onFailure(startPromise::fail)
                .onSuccess(configuration -> {
                    log.info("Retrieved Configuration: {}", configuration);
                    startHttpServerAndAttachRoutes(startPromise, configuration);
                });
    }

    private void startHttpServerAndAttachRoutes(final Promise<Void> startPromise,
                                                final Configuration configuration) {
        final Pool database = DatabasePools.createPostgresPool(configuration, vertx);

        final Router restApi = Router.router(vertx);
        restApi.route()
                .handler(BodyHandler.create())
                .failureHandler(handleFailure());

        AssetsRestApi.attach(restApi, database);
        QuotesRestApi.attach(restApi, database);
        WatchListRestApi.attach(restApi, database);

        vertx.createHttpServer()
                .requestHandler(restApi)
                .exceptionHandler(error -> log.error("HTTP Server error: ", error))
                .listen(configuration.getServerPort(), http -> {
                    if (http.succeeded()) {
                        startPromise.complete();
                        log.info("HTTP server started on port {}", configuration.getServerPort());
                    } else {
                        startPromise.fail(http.cause());
                    }
                });
    }

    private Handler<RoutingContext> handleFailure() {
        return errorContext -> {

            if (errorContext.response().ended()) {
                return;
            }

            log.error("Route Error:", errorContext.failure());
            errorContext.response()
                    .setStatusCode(500)
                    .end(new JsonObject().put("message", "Something went wrong! :(").toBuffer());
        };
    }
}