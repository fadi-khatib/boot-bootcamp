package boot;

import javax.inject.Inject;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;

public class ServerConfiguration {
    private String logMessage;
    private int port;

    @Inject
    public ServerConfiguration() {
        try {
            JSONParser jsonParser = new JSONParser();

            FileReader reader = new FileReader("/Users/fadikhatib/Documents/Bootcamp/Exercises/boot-bootcamp-gradle/server.config");
            Object obj = jsonParser.parse(reader);

            JSONObject config = (JSONObject) obj;
            System.out.println(config.get("logMessage"));

            this.logMessage = config.get("logMessage").toString();
            this.port =  Integer.parseInt(config.get("port").toString());
        }catch(Exception e){

        }

    }
    public void setLogMessage(String logMessage){
        this.logMessage = logMessage;
    }

    public String getLogMessage(){
        return logMessage;
    }
    public int getPort(){
        return port;
    }
}
