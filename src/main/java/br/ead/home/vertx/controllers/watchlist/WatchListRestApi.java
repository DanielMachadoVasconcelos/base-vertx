package br.ead.home.vertx.controllers.watchlist;

import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.sqlclient.Pool;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WatchListRestApi {

    public static void attach(final Router parent, final Pool db) {

        final String pgPath = "/account/watchlist/:accountId";
        parent.get(pgPath).handler(new GetWatchListHandler(db));
        parent.put(pgPath).handler(new PutWatchListHandler(db));
        parent.delete(pgPath).handler(new DeleteWatchListHandler(db));
    }

    static String getAccountId(final RoutingContext context) {
        var accountId = context.pathParam("accountId");
        log.debug("{} for account {}", context.normalizedPath(), accountId);
        return accountId;
    }
}