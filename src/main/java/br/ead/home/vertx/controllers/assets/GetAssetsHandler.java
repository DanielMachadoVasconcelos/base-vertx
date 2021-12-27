package br.ead.home.vertx.controllers.assets;

import br.ead.home.vertx.persistance.DataBaseResponseHandler;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.web.RoutingContext;
import io.vertx.sqlclient.Pool;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class GetAssetsHandler implements Handler<RoutingContext> {

    private final Pool database;

    @Override
    public void handle(final RoutingContext context) {
        database.query("SELECT a.value FROM broker.assets a")
                .execute()
                .onFailure(DataBaseResponseHandler.errorHandler(context, "Failed to get assets from database!"))
                .onSuccess(result -> {
                    var response = new JsonArray();
                    result.forEach(row -> response.add(row.getValue("value")));
                    log.info("Path {} responds with {}", context.normalizedPath(), response.encode());
                    context.response()
                            .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
                            .end(response.toBuffer());
                });
    }
}