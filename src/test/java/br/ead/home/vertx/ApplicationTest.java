package br.ead.home.vertx;

import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(VertxExtension.class)
class ApplicationTest extends AbstractRestApiTest {

    @Test
    void shouldDeployApplicationMainVerticleWhenStarting(Vertx vertx, VertxTestContext testContext) {
        testContext.completeNow();
    }
}