package Consumer;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;


import java.io.FileReader;
import java.io.StringReader;

public class util {

    // jsonFile to JSON object
    public static JSONObject fileToJson(String filePath){
        JSONParser jsonParser = new JSONParser();
        Object obj = null;
        JSONObject JObject = null;
        try {
            FileReader reader = new FileReader(filePath);
            obj = jsonParser.parse(reader);
            JObject = (JSONObject) obj;
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
        return JObject;
    }
    // jsonString to JSON object
    public static JsonObject StringToJson(String jsonString){
        Object obj = null;
        JsonParser parser = new JsonParser();
        JsonObject jObject = null;
        try {
            jObject = parser.parse(jsonString).getAsJsonObject();
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
        if(jObject == null){
            int lastJsonIndex = jsonString.lastIndexOf("}");
            jsonString = jsonString.substring(0,lastJsonIndex+1);
            try {
                System.out.println("new fixed json string is: "+jsonString);
                jObject = parser.parse(jsonString).getAsJsonObject();
            }catch(Exception e){
                System.out.println(e.getMessage());
            }
        }
        return jObject;
    }

}