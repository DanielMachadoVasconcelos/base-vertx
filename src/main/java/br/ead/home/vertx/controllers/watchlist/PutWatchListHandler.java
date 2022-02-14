package br.ead.home.vertx.controllers.watchlist;

import br.ead.home.vertx.persistance.DataBaseResponseHandler;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.SqlConnection;
import io.vertx.sqlclient.SqlResult;
import io.vertx.sqlclient.templates.SqlTemplate;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static br.ead.home.vertx.persistance.DataBaseResponseHandler.errorHandler;

@Slf4j
@RequiredArgsConstructor
public class PutWatchListHandler implements Handler<RoutingContext> {

    private final Pool database;

    @Override
    public void handle(final RoutingContext context) {

        var accountId = WatchListRestApi.getAccountId(context);

        var json = context.getBodyAsJson();
        var watchList = json.mapTo(WatchList.class);

        var parameterBatch = watchList.getAssets().stream()
                .map(asset -> {
                    final Map<String, Object> parameters = new HashMap<>();
                    parameters.put("account_id", accountId);
                    parameters.put("asset", asset.getName());
                    return parameters;
                }).toList();

        log.info("New watchList {} for the account {}", watchList, accountId);
        // Transaction
        database.withTransaction(client ->
             SqlTemplate.forUpdate(client,"DELETE FROM broker.watchlist w where w.account_id = #{account_id}")
                    .execute(Collections.singletonMap("account_id", accountId))
                    .onFailure(DataBaseResponseHandler.errorHandler(context, "Failed to clear watchlist for accountId: " + accountId))
                    .compose(deletionDone -> addAllForAccountId(client, context, parameterBatch))
                    .onFailure(DataBaseResponseHandler.errorHandler(context, "Failed to update watchlist for accountId: " + accountId))
                    .onSuccess(result -> context.response().setStatusCode(HttpResponseStatus.NO_CONTENT.code()).end(json.toBuffer()))
        ).onFailure(DataBaseResponseHandler.errorHandler(context, "Failed to update watchlist for accountId: " + accountId));
    }

    private Future<SqlResult<Void>> addAllForAccountId(final SqlConnection client,
                                                       final RoutingContext context,
                                                       final List<Map<String, Object>> parameterBatch) {
        log.info("Inserting the new watchList with parameters: {}", parameterBatch);
        return SqlTemplate.forUpdate(client,
                        "INSERT INTO broker.watchlist VALUES (#{account_id},#{asset})"
                                + " ON CONFLICT (account_id, asset) DO NOTHING")
                .executeBatch(parameterBatch)
                .onFailure(errorHandler(context, "Failed to insert into watchlist"));
    }
}