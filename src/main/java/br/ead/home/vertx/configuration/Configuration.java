package br.ead.home.vertx.configuration;

import java.util.Objects;

import io.vertx.core.json.JsonObject;
import lombok.Builder;
import lombok.ToString;
import lombok.Value;

import static br.ead.home.vertx.configuration.ConfigurationLoader.*;

@Builder
@Value
@ToString
public class Configuration {
    
    int serverPort;
    String version;
    DatabaseConfiguration databaseConfiguration;

    public static Configuration from(final JsonObject config) {
        final Integer serverPort = config.getInteger(SERVER_PORT);
        if (Objects.isNull(serverPort)) {
            throw new RuntimeException(SERVER_PORT + " not configured!");
        }
        
        final String version = config.getString("version");
        if (Objects.isNull(version)) {
            throw new RuntimeException("version is not configured in config file!");
        }
        
        return Configuration.builder()
                .serverPort(serverPort)
                .version(version)
                .databaseConfiguration(parseDbConfig(config))
                .build();
    }

    private static DatabaseConfiguration parseDbConfig(final JsonObject config) {
        return DatabaseConfiguration.builder()
                .host(config.getString(DB_HOST))
                .port(config.getInteger(DB_PORT))
                .database(config.getString(DB_DATABASE))
                .user(config.getString(DB_USER))
                .password(config.getString(DB_PASSWORD))
                .build();
    }
}
