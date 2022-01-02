package br.ead.home.vertx.configuration;

import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Stream;

@Slf4j
public class ConfigurationLoader {

    private static final Logger LOG = LoggerFactory.getLogger(ConfigurationLoader.class);
    public static final String CONFIG_FILE = "application.yaml";

    public static final List<String> EXPOSED_ENVIRONMENT_VARIABLES = Stream.of(EnvironmentConfiguration.values())
                                                                           .map(EnvironmentConfiguration::getEnvironment)
                                                                           .toList();

    public static Future<Configuration> load(Vertx vertx) {
        final var exposedKeys = new JsonArray();
        EXPOSED_ENVIRONMENT_VARIABLES.forEach(exposedKeys::add);
        LOG.debug("Fetch configuration for {}", exposedKeys.encode());

        var envStore = new ConfigStoreOptions()
                .setType("env")
                .setConfig(new JsonObject().put("keys", exposedKeys));

        var propertyStore = new ConfigStoreOptions()
                .setType("sys")
                .setConfig(new JsonObject().put("cache", false));

        var yamlStore = new ConfigStoreOptions()
                .setType("file")
                .setFormat("yaml")
                .setConfig(new JsonObject().put("path", CONFIG_FILE));

        var retriever = ConfigRetriever.create(vertx,
                new ConfigRetrieverOptions()
                        // Order defines overload rules
                        .addStore(yamlStore)
                        .addStore(propertyStore)
                        .addStore(envStore)
        );

        return retriever.getConfig().map(Configuration::from);
    }
}
