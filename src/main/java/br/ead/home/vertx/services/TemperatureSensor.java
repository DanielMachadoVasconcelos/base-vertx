package br.ead.home.vertx.services;

import com.github.javafaker.Faker;
import com.github.javafaker.Weather;
import io.reactivex.rxjava3.core.Flowable;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME;
import static br.ead.home.vertx.utils.EventBusAddresses.TEMPERATURES;

public class TemperatureSensor extends AbstractVerticle {

    private final Weather sensor;

    public TemperatureSensor() {
        Faker faker = new Faker(Locale.ENGLISH);
        this.sensor = faker.weather();
    }

    @Override
    public void start() {
        Flowable.interval(1, TimeUnit.SECONDS)
                .map(random -> sensor.temperatureCelsius(25, 35))
                .map(temperature -> new JsonObject().put("temperature", temperature))
                .map(json -> json.put("timestamp", LocalDateTime.now().format(ISO_LOCAL_DATE_TIME)))
                .subscribe(temperature -> vertx.eventBus().send(TEMPERATURES, temperature));
    }
}
