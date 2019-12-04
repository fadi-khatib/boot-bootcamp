package boot;

import javax.inject.Inject;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
//import com.google.gson;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;


public class ServerConfiguration {
    private String logMessage;
    private int port;

    @Inject
    public ServerConfiguration() {
        try {
//            JSONParser jsonParser = new JSONParser();
//
//            FileReader reader = new FileReader("/Users/fadikhatib/Documents/Bootcamp/Exercises/boot-bootcamp-gradle/server.config");
//            //Read JSON file
//            Object obj = jsonParser.parse(reader);
//
//            JSONObject config = (JSONObject) obj;
//            System.out.println(config.get("logMessage"));
//
//            this.logMessage = config.get("logMessage").toString();
//            this.port =  Integer.parseInt(config.get("port").toString());

            Gson gson = new Gson();
            JsonReader jsonReader = null;

            jsonReader = new JsonReader(new FileReader("server.config"));
            parseJson(jsonReader);
            //ServerConfiguration s =   (gson.fromJson(jsonReader, ServerConfiguration.class));
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
    public void parseJson(JsonReader jReader){
        jReader.setLenient(true);
        try {
            while (jReader.hasNext()) {
                JsonToken nextToken = jReader.peek();
                if (JsonToken.BEGIN_OBJECT.equals(nextToken)) {
                    jReader.beginObject();
                } else if (JsonToken.NAME.equals(nextToken)) {
                    String name = jReader.nextName();
                    System.out.println("Token KEY >>>> " + name);
                } else if (JsonToken.STRING.equals(nextToken)) {
                    this.logMessage = jReader.nextString();
                } else if (JsonToken.NUMBER.equals(nextToken)) {
                    this.port = jReader.nextInt();
                } else if (JsonToken.NULL.equals(nextToken)) {

                    jReader.nextNull();
                    System.out.println("Token Value >>>> null");

                } else if (JsonToken.END_OBJECT.equals(nextToken)) {
                    jReader.endObject();

                }
            }
        }catch(Exception e){

        }
    }
}
