package br.ead.home.vertx;

import io.vertx.core.Vertx;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.BeforeEach;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static br.ead.home.vertx.configuration.EnvironmentConfiguration.*;

public abstract class AbstractRestApiTest {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractRestApiTest.class);
    protected static final int TEST_SERVER_PORT = 9000;

    @BeforeEach
    void deploy_verticle(Vertx vertx, VertxTestContext context) {
        System.setProperty(SERVER_PORT.getEnvironment(), String.valueOf(TEST_SERVER_PORT));
        System.setProperty(DB_HOST.getEnvironment(), "localhost");
        System.setProperty(DB_PORT.getEnvironment(), "5432");
        System.setProperty(DB_DATABASE.getEnvironment(), "broker");
        System.setProperty(DB_USER.getEnvironment(), "username");
        System.setProperty(DB_PASSWORD.getEnvironment(), "password");
        LOG.warn("!!! Tests are using local database !!!");
        vertx.deployVerticle(new Application(), context.succeeding(id -> context.completeNow()));
    }

}