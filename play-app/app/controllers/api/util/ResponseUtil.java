package controllers.api.util;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import play.libs.Json;

import java.util.List;
import java.util.UUID;

public class ResponseUtil {
    public static ObjectNode getSuccess() {
        ObjectNode json = Json.newObject();
        json.put("status","SUCCESS");
        return json;
    }

    public static ObjectNode getSuccess(Long id) {
        ObjectNode json = Json.newObject();
        json.put("status","SUCCESS");
        if (id != null) {
            json.put("id", id);
        }
        return json;
    }

    public static ObjectNode getSuccess(String id) {
        ObjectNode json = Json.newObject();
        json.put("status","SUCCESS");
        if (id != null) {
            json.put("id", id);
        }
        return json;
    }

    public static ObjectNode getSuccess(UUID id) {
        return getSuccess(id.toString());
    }

    public static ObjectNode getErrorAsJson(String error) {
        ObjectNode json = Json.newObject();
        json.put("error", error);
        return json;
    }

    public static ObjectNode getErrorAsJson(List<String> errors) {
        ObjectNode json = Json.newObject();
        ArrayNode jsonArray = json.putArray("error");
        for (String error : errors) {
            jsonArray.add(error);
        }
        return json;
    }

    public static ObjectNode getErrorAsJson(String key, String error) {
        ObjectNode json = Json.newObject();
        ArrayNode jsonArray = json.putArray(key);
        jsonArray.add(error);
        return json;
    }
}
