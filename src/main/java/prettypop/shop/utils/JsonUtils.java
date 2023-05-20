package prettypop.shop.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class JsonUtils {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static String decodeJsonString(String json) {
        return URLDecoder.decode(json, StandardCharsets.UTF_8);
    }

    public static String encodeJsonString(String json) {
        return URLEncoder.encode(json, StandardCharsets.UTF_8);
    }

    public static <K, V> String mapToJson(Map<K, V> map) {
        try {
            String string = objectMapper.writeValueAsString(map);
            return URLEncoder.encode(string, StandardCharsets.UTF_8);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    public static Map<String, Object> jsonToMap(String string) {
        string = URLDecoder.decode(string, StandardCharsets.UTF_8);
        try {
            return objectMapper.readValue(string, new TypeReference<Map<String, Object>>() {});
        } catch (JsonProcessingException e) {
            return new HashMap<>();
        }
    }
}
