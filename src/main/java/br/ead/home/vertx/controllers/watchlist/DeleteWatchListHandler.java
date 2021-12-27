package br.ead.home.vertx.controllers.watchlist;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.templates.SqlTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.util.Collections;

import static br.ead.home.vertx.persistance.DataBaseResponseHandler.errorHandler;

@Log4j2
@RequiredArgsConstructor
public class DeleteWatchListHandler implements Handler<RoutingContext> {

    private final Pool database;

    @Override
    public void handle(final RoutingContext context) {
        var accountId = WatchListRestApi.getAccountId(context);
        SqlTemplate.forUpdate(database, "DELETE FROM broker.watchlist where account_id=#{account_id}")
                .execute(Collections.singletonMap("account_id", accountId))
                .onFailure(errorHandler(context, "Failed to delete watchlist for accountId: " + accountId))
                .onSuccess(result -> log.debug("Deleted {} rows for accountId {}", result.rowCount(), accountId))
                .onSuccess(result -> context.response().setStatusCode(HttpResponseStatus.NO_CONTENT.code()).end());
    }
}