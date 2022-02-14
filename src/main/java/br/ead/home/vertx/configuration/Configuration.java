package br.ead.home.vertx.configuration;

import com.google.common.base.Preconditions;
import io.vertx.core.json.JsonObject;
import lombok.Builder;
import lombok.ToString;
import lombok.Value;

import static br.ead.home.vertx.configuration.EnvironmentConfiguration.*;

@Builder
@Value
@ToString
public class Configuration {
    
    int serverPort;
    String version;
    DatabaseConfiguration databaseConfiguration;

    public static Configuration from(final JsonObject config) {
        var serverPort = SERVER_PORT.extract(config);
        var version = VERSION.extract(config);

        Preconditions.checkNotNull(serverPort, "server port is not configured in configuration file!");
        Preconditions.checkNotNull(version, "version is not configured in config file!");

        return Configuration.builder()
                .serverPort(Integer.valueOf(serverPort))
                .version(version)
                .databaseConfiguration(parseDbConfig(config))
                .build();
    }

    private static DatabaseConfiguration parseDbConfig(final JsonObject config) {
        var dbConfiguration = config.getJsonObject("db");

        var port = DB_PORT.extract(config);
        var host = DB_HOST.extract(config);
        var database = DB_DATABASE.extract(config);
        var user = DB_USER.extract(config);
        var password = DB_PASSWORD.extract(config);

        Preconditions.checkNotNull(dbConfiguration, "database is not configured in configuration file!");
        Preconditions.checkNotNull(password, "database password is not configured in configuration file!");
        Preconditions.checkNotNull(user, "database user is not configured in configuration file!");
        Preconditions.checkNotNull(database, "database is not configured in configuration file!");
        Preconditions.checkNotNull(host, "database host is not configured in configuration file!");
        Preconditions.checkNotNull(port, "database port is not configured in configuration file!");

        return DatabaseConfiguration.builder()
                .host(host)
                .port(Integer.valueOf(port))
                .database(database)
                .user(user)
                .password(password)
                .build();
    }
}
