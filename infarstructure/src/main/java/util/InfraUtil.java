package util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.FileNotFoundException;
import java.io.FileReader;

public class InfraUtil {

    // jsonString to JSON object
    public static <T> T stringToObject(String json, Class<T> tClass) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(json, tClass);
        } catch ( Exception e ) {
            throw new RuntimeException(e);
        }
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