package no.unit.nva.cristin.institutions.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import no.unit.nva.cristin.institutions.model.Identifier;
import no.unit.nva.cristin.institutions.model.UnitObject;
import no.unit.nva.cristin.institutions.model.UnitObjectBuilder;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Type;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AdapterTest {

    @Disabled
    @Test
    void serialize_single() {
        UnitObject unitObjectOne = new UnitObjectBuilder(new Identifier(123))
                .withAcronym("BTSU")
                .withInstitution(Collections.singletonMap("nb", "BTS Universitet"))
                .withUnitName(Collections.singletonMap("nb", "Avdeling for forskning"))
                .build();
        Type type = new TypeToken<UnitObject[]>() {
        }.getType();
        Adapter<UnitObject> objectToListAdapter = new Adapter<>(UnitObject.class);

        Gson gson = new GsonBuilder().registerTypeAdapter(type, objectToListAdapter).setPrettyPrinting().create();
        String json = gson.toJson(unitObjectOne);
        String expected = "{\n"
                + "  \"id\": \"123.0.0.0\",\n"
                + "  \"name\": \"Avdeling for forskning\",\n"
                + "  \"unitName\": {\n"
                + "    \"nb\": \"Avdeling for forskning\"\n"
                + "  }\n"
                + "}";
        assertEquals(expected, json);
    }

    @Disabled
    @Test
    void serialize_multiple() {
        UnitObject unitObjectOne = new UnitObjectBuilder(new Identifier(123)).build();
        UnitObject unitObjectTwo = new UnitObjectBuilder(new Identifier(321)).build();
        UnitObject[] unitObjects = new UnitObject[2];
        unitObjects[0] = unitObjectOne;
        unitObjects[1] = unitObjectTwo;
        Type type = new TypeToken<UnitObject[]>() {
        }.getType();
        Adapter<UnitObject> objectToListAdapter = new Adapter<>(UnitObject.class);

        Gson gson = new GsonBuilder().registerTypeAdapter(type, objectToListAdapter).setPrettyPrinting().create();
        String json = gson.toJson(unitObjects);
        String expected = "[\n"
                + "  {\n"
                + "    \"id\": \"123.0.0.0\",\n"
                + "    \"name\": \"\",\n"
                + "    \"unitName\": {}\n"
                + "  },\n"
                + "  {\n"
                + "    \"id\": \"321.0.0.0\",\n"
                + "    \"name\": \"\",\n"
                + "    \"unitName\": {}\n"
                + "  }\n"
                + "]";
        assertEquals(expected, json);
    }

    @Test
    void deserialize_single() {

        String data = "{\n"
                + "    \"id\": \"123.0.0.0\",\n"
                + "    \"isCristinUser\": false\n"
                + "  }";
        UnitObject unitObject = new UnitObjectBuilder(new Identifier(123)).build();
        Type type = new TypeToken<UnitObject[]>() {
        }.getType();
        Adapter<UnitObject> objectToListAdapter = new Adapter<>(UnitObject.class);
        Gson gson = new GsonBuilder().registerTypeAdapter(type, objectToListAdapter).setPrettyPrinting().create();
        UnitObject[] fromJson = gson.fromJson(data, type);
        assertEquals(unitObject.getId(), fromJson[0].getId());
    }

    @Test
    void deserialize_multiple() {

        String data = "[\n"
                + "  {\n"
                + "    \"id\": \"123.0.0.0\",\n"
                + "    \"isCristinUser\": false\n"
                + "  },\n" 
                + "  {\n"
                + "    \"id\": \"321.0.0.0\",\n"
                + "    \"isCristinUser\": false\n"
                + "  }\n"
                + "]";
        UnitObject unitObjectOne = new UnitObjectBuilder(new Identifier(123)).build();
        UnitObject unitObjectTwo = new UnitObjectBuilder(new Identifier(321)).build();
        UnitObject[] unitObjects = new UnitObject[2];
        unitObjects[0] = unitObjectOne;
        unitObjects[1] = unitObjectTwo;
        Type type = new TypeToken<UnitObject[]>() {
        }.getType();
        Adapter<UnitObject> objectToListAdapter = new Adapter<>(UnitObject.class);
        Gson gson = new GsonBuilder().registerTypeAdapter(type, objectToListAdapter).setPrettyPrinting().create();
        UnitObject[] fromJson = gson.fromJson(data, type);
        assertEquals(unitObjects[0].getId(), fromJson[0].getId());
        assertEquals(unitObjects[1].getId(), fromJson[1].getId());
    }

    @Test
    void test_throwsUnknownJsonType() {
        Type type = new TypeToken<UnitObject[]>() {
        }.getType();
        Adapter<UnitObject> objectToListAdapter = new Adapter<>(UnitObject.class);
        Gson gson = new GsonBuilder().registerTypeAdapter(type, objectToListAdapter).setPrettyPrinting().create();

        String json = "123";
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            gson.fromJson(json, type);
        });

        String expectedError = "Unknown JSON type \"class com.google.gson.JsonPrimitive\" in data:"
                + System.lineSeparator()
                + System.lineSeparator()
                + "123";

        assertEquals(expectedError, exception.getMessage());
    }
}