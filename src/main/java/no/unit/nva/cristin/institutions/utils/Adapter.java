package no.unit.nva.cristin.institutions.utils;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import no.unit.nva.cristin.institutions.model.UnitObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Adapter<T> implements JsonDeserializer<Object[]> {

    public static final String ERROR_MESSAGE_TEMPLATE = "Unknown JSON type \"%s\" in data:%n%n%s";
    private final transient Class<T> clazz;

    public Adapter(Class<T> clazz) {
        this.clazz = clazz;
    }

    /**
     * Allows the deserialization of UnitObject objects and arrays of UnitObject objects from JSON.
     * @param json The JSON object to be deserialized
     * @param typeOfT The type of the the object
     * @param context The deserialization context
     * @return An array of Units
     * @throws JsonParseException In case a valid JSON object cannot be processed to a UnitObject object
     */
    @Override
    public UnitObject[] deserialize(JsonElement json,
                                    Type typeOfT,
                                    JsonDeserializationContext context) {
        List<UnitObject> result = new ArrayList<>();
        if (json.isJsonArray()) {
            for (JsonElement e : json.getAsJsonArray()) {
                result.add(context.deserialize(e, clazz));
            }
        } else if (json.isJsonObject()) {
            result.add(context.deserialize(json, clazz));
        } else {
            throw new RuntimeException(String.format(ERROR_MESSAGE_TEMPLATE, json.getClass(), json.toString()));
        }
        return result.toArray(new UnitObject[0]);
    }
}