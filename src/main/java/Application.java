import io.vertx.core.Vertx;
import services.TemperatureSensor;
import utils.EventBusAddresses;

public class Application {

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new TemperatureSensor());
        vertx.eventBus().consumer(EventBusAddresses.TEMPERATURES,
                message -> System.out.println("message: " + message.body()));
    }
}
