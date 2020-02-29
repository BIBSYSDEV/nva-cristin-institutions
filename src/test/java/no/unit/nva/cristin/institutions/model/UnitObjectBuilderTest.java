package no.unit.nva.cristin.institutions.model;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UnitObjectBuilderTest {

    public static final String SOME_ID = "100.2.1.5";
    public static final String SOME_UNIT_NAME = "some_unit_name";
    public static final String NORWEGIAN_BOKMAAL = "nb";
    public static final String SOME_INSTITUTION_URI = "https://example.org/some_institution";
    public static final String SOME_INSTITUTION_NAME = "Some institution_name";
    private static final String SOME_INSTITUITION_ACRONYM = "SI";
    public static final String ANOTHER_ID = "100.1.12.1";

    @Test
    public void it_exists() {
        UnitObject unitObject = new UnitObjectBuilder(new Identifier(SOME_ID)).build();
        assertEquals(SOME_ID, unitObject.getId());
    }

    @Test
    public void overloaded_constructor() {

        Map<String, String> unitName = new HashMap<>();
        unitName.put(NORWEGIAN_BOKMAAL, SOME_UNIT_NAME);
        Map<String, String> institution = new HashMap<>();
        List<UnitObject> subunits = null;
        UnitObject unitObject = new UnitObjectBuilder(SOME_ID,
                unitName, institution, SOME_INSTITUTION_URI, SOME_INSTITUITION_ACRONYM,
                subunits, true)
                .build();
        assertEquals(SOME_INSTITUITION_ACRONYM, unitObject.getAcronym());
        assertEquals(SOME_INSTITUITION_ACRONYM, unitObject.getAcronym());
        assertEquals(SOME_UNIT_NAME, unitObject.getName());
        assertNull(unitObject.getSubunits());
        assertEquals(unitObject.getInstitution(), institution);
        assertEquals(SOME_INSTITUTION_URI, unitObject.getUri());
        assertTrue(unitObject.isCristinUser());
    }

    @Test
    public void test_builder() {
        Map<String, String> unitName = new HashMap<>();
        unitName.put(NORWEGIAN_BOKMAAL, SOME_UNIT_NAME);
        Map<String, String> institution = new HashMap<>();
        UnitObject subunit = new UnitObjectBuilder(new Identifier(ANOTHER_ID)).build();
        List<UnitObject> subunits = Collections.singletonList(subunit);
        UnitObject unitObject = new UnitObjectBuilder(new Identifier(SOME_ID))
                .withUnitName(unitName)
                .withAcronym(SOME_INSTITUITION_ACRONYM)
                .withInstitution(institution)
                .withSubunits(subunits)
                .withUri(SOME_INSTITUTION_URI)
                .build();
        assertEquals(SOME_INSTITUITION_ACRONYM, unitObject.getAcronym());
        assertEquals(SOME_INSTITUITION_ACRONYM, unitObject.getAcronym());
        assertEquals(SOME_UNIT_NAME, unitObject.getName());
        assertEquals(subunits, unitObject.getSubunits());
        assertEquals(unitObject.getInstitution(), institution);
        assertEquals(SOME_INSTITUTION_URI, unitObject.getUri());
    }

}