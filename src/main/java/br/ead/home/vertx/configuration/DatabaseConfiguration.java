package br.ead.home.vertx.configuration;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class DatabaseConfiguration {

  String host;
  int port;
  String database;
  String user;
  String password;

  @Override
  public String toString() {
    return "DbConfig{" +
      "host='" + host + '\'' +
      ", port=" + port +
      ", database='" + database + '\'' +
      ", user='" + user + '\'' +
      ", password='****'" +
      '}';
  }
}