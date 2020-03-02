package no.unit.nva.cristin.institutions.model;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static java.util.Collections.singletonMap;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


class UnitObjectTest {

    public static final String EMPTY_STRING = "";
    public static final String EXAMPLE_BASE_URI = "https://example.org/";
    public static final String AN_IDENTIFIER_STRING = "123.1.2.0";

    @Test
    void test_getUri_unqualified() {
        UnitObject unitObject = new UnitObject("194", null, null, null, null, null, false);
        assertEquals("194.0.0.0", unitObject.getId());
    }

    @Test
    void test_creatingName() {
        String language = "nn";
        String unitName = "Eining for semde";

        Map<String, String> nameMap = singletonMap(language, unitName);
        UnitObject unitObject = new UnitObjectBuilder(new Identifier(123))
                .withUnitName(nameMap)
                .build();

        assertEquals(unitName, unitObject.getName());
    }

    @Test
    void test_idCreation() {
        String id = "123";
        String expected = "123.0.0.0";

        UnitObject unitObject = new UnitObject(id, null, null, null, null, null, false);

        assertEquals(expected, unitObject.getId());
    }

    @Test
    void test_nullId() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                new UnitObject(null, null, null, null, null, null, false));

        assertEquals("The identifier must not be null or empty", exception.getMessage());
    }

    @Test
    void test_emptyId() {
        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class, () ->
                        new UnitObject("", null, null, null, null, null, false));

        assertEquals("The identifier must not be null or empty", exception.getMessage());
    }

    @Test
    void test_gettersAndSetters() {
        Map<String, String> unitName = singletonMap("en", "Some section");
        Map<String, String> institution = singletonMap("en", "Some institution");
        String uri = EXAMPLE_BASE_URI + AN_IDENTIFIER_STRING;
        String acronym = "SI";
        final String name = "Some section";
        UnitObject unitObject = new UnitObject(AN_IDENTIFIER_STRING, unitName, institution,
                uri, acronym, null, true);
        assertEquals(AN_IDENTIFIER_STRING, unitObject.getId());
        assertEquals(unitName, unitObject.getUnitName());
        assertEquals(institution, unitObject.getInstitution());
        assertEquals(uri, unitObject.getUri());
        assertEquals(acronym, unitObject.getAcronym());
        assertEquals(name, unitObject.getName());
        assertNull(unitObject.getSubunits());
        assertTrue(unitObject.isCristinUserInstitution());
        assertEquals(EMPTY_STRING, unitObject.getCountry());
        assertEquals(new Identifier(AN_IDENTIFIER_STRING).getIdentifier(), unitObject.getIdentifier().getIdentifier());
    }
}