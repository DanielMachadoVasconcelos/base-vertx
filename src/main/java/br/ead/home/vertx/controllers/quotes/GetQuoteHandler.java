package br.ead.home.vertx.controllers.quotes;

import br.ead.home.vertx.persistance.DataBaseResponseHandler;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpHeaders;
import io.vertx.ext.web.RoutingContext;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.templates.SqlTemplate;
import lombok.extern.log4j.Log4j2;

import java.util.Collections;

@Log4j2
public class GetQuoteHandler implements Handler<RoutingContext> {

    private final Pool database;

    public GetQuoteHandler(final Pool database) {
        this.database = database;
    }

    @Override
    public void handle(final RoutingContext context) {
        final String assetParam = context.pathParam("asset");
        log.debug("Asset parameter: {}", assetParam);

        SqlTemplate.forQuery(database,
                        "SELECT q.asset, q.bid, q.ask, q.last_price, q.volume from broker.quotes q where asset=#{asset}")
                .mapTo(QuoteEntity.class)
                .execute(Collections.singletonMap("asset", assetParam))
                .onFailure(DataBaseResponseHandler.errorHandler(context, "Failed to get quote for asset " + assetParam + " from db!"))
                .onSuccess(transformSuccessDataBaseResponse(context, assetParam));
    }

    private Handler<RowSet<QuoteEntity>> transformSuccessDataBaseResponse(RoutingContext context, String assetParam) {
        return quotes -> {
            if (!quotes.iterator().hasNext()) {
                DataBaseResponseHandler.notFound(context, "quote for asset " + assetParam + " not available!");
                return;
            }

            var response = quotes.iterator().next().toJsonObject();
            log.info("Path {} responds with {}", context.normalizedPath(), response.encode());
            context.response()
                    .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
                    .end(response.toBuffer());
        };
    }
}