package br.ead.home.vertx.controllers.quotes;

import io.vertx.ext.web.Router;
import io.vertx.sqlclient.Pool;

public class QuotesRestApi {

    public static void attach(Router parent, final Pool db) {
        parent.get("/quotes/:asset").handler(new GetQuoteHandler(db));
    }
}