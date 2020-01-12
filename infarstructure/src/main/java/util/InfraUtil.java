package util;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

import java.io.FileNotFoundException;
import java.io.FileReader;

public class InfraUtil {

    // jsonString to JSON object
    public static JsonObject stringToJson(String jsonString) {
        Object obj = null;
        JsonParser parser = new JsonParser();
        JsonObject jObject = null;
        try {
            jObject = parser.parse(jsonString).getAsJsonObject();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
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