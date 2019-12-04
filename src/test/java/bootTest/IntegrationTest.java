package bootTest;

//import io.logz.junit.extension.flakytest.FlakyTest;

import boot.HttpRequestHandler;
import boot.Msg;
import boot.ServerConfiguration;
import boot.ServerModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import io.logz.guice.jersey.JerseyServer;

//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.Test;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;


import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;




public class IntegrationTest {
    public HttpRequestHandler httpRequestHandler;
    public static Injector injector;



//    @BeforeAll
    @Before
    public void beforeAllTests(){
        System.out.println("Hi Test");
        //serverConfigurations = new ServerConfiguration();
        ServerConfiguration SConfig = new ServerConfiguration();
        injector = Guice.createInjector(new ServerModule(SConfig));
        try{
            injector.getInstance(JerseyServer.class).start();
        }catch(Exception e){
            System.out.println(e.toString());
        }
    }

    @Test
    public void testEndToEnd() {
        String key = generateKey(15);
        httpRequestHandler = new HttpRequestHandler(injector.getInstance(Client.class) , new Msg("fadi test End to end") ,"http://localhost:8080" );
        httpRequestHandler.setPath("/entry-point/send?Id="+key);
        Response sendResponse = httpRequestHandler.getRequest();
        String result = sendResponse.toString()+ "\n" + sendResponse.readEntity(String.class);
        System.out.println(result);

        httpRequestHandler.setPath("/entry-point/search?message="+key);
        Response searchRes = httpRequestHandler.getRequest();
        System.out.println(searchRes.readEntity(String.class));
    }
    //@BeforeEach
    @Before
    public void beforTest() {
        System.out.println("befor each test");

    }
    //@AfterEach
    @After
    public void afterTest() {
        System.out.println("after each test");

    }
    public String generateKey(int n){
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "0123456789" + "abcdefghijklmnopqrstuvxyz";
        StringBuilder sb = new StringBuilder(n);

        for (int i = 0; i < n; i++) {
            int index
                    = (int)(AlphaNumericString.length()
                    * Math.random());
            sb.append(AlphaNumericString
                    .charAt(index));
        }
        return sb.toString();
    }


}
