package br.ead.home.vertx;

import br.ead.home.vertx.configuration.ConfigurationLoader;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.BeforeEach;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractRestApiTest {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractRestApiTest.class);
    protected static final int TEST_SERVER_PORT = 9000;

    @BeforeEach
    void deploy_verticle(Vertx vertx, VertxTestContext context) {
        System.setProperty(ConfigurationLoader.SERVER_PORT, String.valueOf(TEST_SERVER_PORT));
        System.setProperty(ConfigurationLoader.DB_HOST, "localhost");
        System.setProperty(ConfigurationLoader.DB_PORT, "5432");
        System.setProperty(ConfigurationLoader.DB_DATABASE, "broker");
        System.setProperty(ConfigurationLoader.DB_USER, "username");
        System.setProperty(ConfigurationLoader.DB_PASSWORD, "password");
        LOG.warn("!!! Tests are using local database !!!");
        vertx.deployVerticle(new Application(), context.succeeding(id -> context.completeNow()));
    }

}