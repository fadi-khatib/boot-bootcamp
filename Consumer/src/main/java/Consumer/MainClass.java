package Consumer;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class MainClass {

    public static void main(String[] args) {

        System.out.println("Hi Consumer");
        // configuration
        //ConsumerConfiguration SConfig = new ConsumerConfiguration();
        // create injector
        ConsumerModule consumerModule =  new ConsumerModule();
        Injector injector = Guice.createInjector(consumerModule);
        // start Jerseyserver
        injector.getInstance(KafkaReceiver.class).start();
    }
}
