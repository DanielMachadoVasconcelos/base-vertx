package services;

import io.reactivex.rxjava3.core.Flowable;
import io.vertx.core.AbstractVerticle;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import static utils.EventBusAddresses.TEMPERATURES;

public class TemperatureSensor extends AbstractVerticle {

    private final Random sensor;

    public TemperatureSensor() {
        this.sensor = new Random();
    }

    @Override
    public void start() {
        Flowable.interval(1, TimeUnit.SECONDS)
                .map(random -> sensor.nextInt(35))
                .map(temperature -> String.format("%sºC", temperature))
                .subscribe(message -> vertx.eventBus().send(TEMPERATURES, message));
    }
}
