package util;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import com.google.gson.stream.JsonReader;

import java.io.FileReader;
import java.io.FileNotFoundException;

public class InfraUtil {

    // jsonFile to JSON object
    public static JSONObject fileToJson(String filePath) {
        JSONParser jsonParser = new JSONParser();
        Object obj = null;
        try {
            FileReader reader = new FileReader(filePath);
            obj = jsonParser.parse(reader);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        JSONObject JObject = (JSONObject) obj;
        return JObject;
    }

    // jsonString to JSON object
    public static JsonObject stringToJson(String jsonString) {
        Object obj = null;
        JsonParser parser = new JsonParser();
        JsonObject jObject = null;
        try {
            jObject = parser.parse(jsonString).getAsJsonObject();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return jObject;
    }

    public static <T> T load(String fileName, Class<T> tClass) {
        JsonReader jsonReader = null;
        try {
            jsonReader = new JsonReader(new FileReader(fileName));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        if (jsonReader == null)
            throw new RuntimeException("JsonReader instance failed in the installation process");
        Gson gson = new Gson();
        return gson.fromJson(jsonReader, tClass);
    }

}