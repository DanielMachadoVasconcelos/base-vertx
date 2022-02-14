package br.ead.home.vertx.controllers.watchlist;

import br.ead.home.vertx.AbstractRestApiTest;
import br.ead.home.vertx.controllers.assets.Asset;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(VertxExtension.class)
class WatchListRestApiTest extends AbstractRestApiTest {

    private static final Logger LOG = LoggerFactory.getLogger(WatchListRestApiTest.class);

    @Test
    void shouldReturnWatchListWhenAssetsAreBeingTrackForSpecificAccount(Vertx vertx, VertxTestContext context) {
        var client = webClient(vertx);
        var accountId = UUID.randomUUID();
        client.put("/account/watchlist/" + accountId)
              .sendJsonObject(body())
              .onFailure(context::failNow)
              .onComplete(context.succeeding(response -> {
                    var json = response.bodyAsJsonObject();
                    LOG.info("Response PUT: {}", json);
                    assertEquals(204, response.statusCode());
              }))
              .compose(next -> {
                client.get("/account/watchlist/" + accountId)
                        .send()
                        .onFailure(context::failNow)
                        .onComplete(context.succeeding(response -> {
                            var json = response.bodyAsJsonArray();
                            LOG.info("Response GET: {}", json);
                            assertEquals("[{\"asset\":\"AMZN\"},{\"asset\":\"TSLA\"}]", json.encode());
                            assertEquals(200, response.statusCode());
                            context.completeNow();
                        }));
                return Future.succeededFuture();
            })
            .onFailure(context::failNow)
            .onSuccess(any -> context.completeNow());
    }

    private JsonObject body() {
        return new WatchList(Arrays.asList(
                new Asset("AMZN"),
                new Asset("TSLA"))
        ).toJsonObject();
    }

    private WebClient webClient(final Vertx vertx) {
        return WebClient.create(vertx, new WebClientOptions().setDefaultPort(TEST_SERVER_PORT));
    }
}