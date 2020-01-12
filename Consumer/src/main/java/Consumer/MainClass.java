package Consumer;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class MainClass {

    public static void main(String[] args) {

        ConsumerModule consumerModule = new ConsumerModule();
        Injector injector = Guice.createInjector(consumerModule);
        injector.getInstance(KafkaReceiver.class).start();

    }
}
