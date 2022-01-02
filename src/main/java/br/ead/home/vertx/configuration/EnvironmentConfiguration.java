package br.ead.home.vertx.configuration;

import io.vertx.core.json.JsonObject;
import io.vertx.core.json.pointer.JsonPointer;

import java.util.Optional;

public enum EnvironmentConfiguration {

    VERSION("VERSION", "/version"),
    SERVER_PORT("SERVER_PORT", "/server/port"),
    DB_HOST("DB_HOST", "/db/host"),
    DB_PORT("DB_PORT", "/db/port"),
    DB_DATABASE("DB_DATABASE", "/db/database"),
    DB_USER("DB_USER", "/db/user"),
    DB_PASSWORD("DB_PASSWORD", "/db/password");

    private final String environment;
    private final String yaml;

    EnvironmentConfiguration(String environment, String yaml) {
        this.environment = environment;
        this.yaml = yaml;
    }

    public String getEnvironment() {
        return environment;
    }

    public String getYaml() {
        return yaml;
    }

    public String extract(JsonObject jsonObject) {
        return Optional.ofNullable(jsonObject.getString(this.getEnvironment()))
                       .orElseGet(() -> JsonPointer.from(this.getYaml()).queryJson(jsonObject).toString());
    }
}
