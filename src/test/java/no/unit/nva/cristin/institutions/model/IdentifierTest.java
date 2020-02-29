package no.unit.nva.cristin.institutions.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


class IdentifierTest {

    private static final String WELL_FORMATTED_IDENTIFIER = "194.12.65.10";
    private static final String BADLY_FORMATTED_IDENTIFIER = "I am not an identifier";
    private static final String ERROR_MESSAGE_TEMPLATE = "The input id \"%s\" did not have expected structure "
            + "\"{integer}.{integer}.{integer}.{integer}\"";
    public static final String EXPECTED_INSTITUTION_IDENTIFIER = "194.0.0.0";
    public static final String EXPECTED_FACULTY_IDENTIFIER = "194.12.0.0";
    public static final String EXPECTED_DEPARTMENT_IDENTIFIER = "194.12.65.0";
    public static final String EXPECTED_SECTION_IDENTIFIER = "194.12.65.10";
    private static final String WELL_FORMATTED_IDENTIFIER_NO_SECTION = "194.12.65.0";

    @Test
    void getIdentifier() {
        Identifier identifier = new Identifier(WELL_FORMATTED_IDENTIFIER);
        assertEquals(WELL_FORMATTED_IDENTIFIER, identifier.getIdentifier());
    }

    @Test
    void getInstitutionIdentifier() {
        Identifier identifier = new Identifier(WELL_FORMATTED_IDENTIFIER);
        assertEquals(EXPECTED_INSTITUTION_IDENTIFIER, identifier.getInstitutionIdentifier());
    }

    @Test
    void getFacultyIdentifier() {
        Identifier identifier = new Identifier(WELL_FORMATTED_IDENTIFIER);
        assertEquals(EXPECTED_FACULTY_IDENTIFIER, identifier.getFacultyIdentifier());
    }

    @Test
    void getDepartmentIdentifier() {
        Identifier identifier = new Identifier(WELL_FORMATTED_IDENTIFIER);
        assertEquals(EXPECTED_DEPARTMENT_IDENTIFIER, identifier.getDepartmentIdentifier());

    }

    @Test
    void getSectionIdentifier() {
        Identifier identifier = new Identifier(WELL_FORMATTED_IDENTIFIER);
        assertEquals(EXPECTED_SECTION_IDENTIFIER, identifier.getSectionIdentifier());
    }

    @Test
    void throws_BadlyFormattedIdentifierException() {
        BadlyFormattedIdentifierException badlyFormattedIdentifierException =
                assertThrows(BadlyFormattedIdentifierException.class, () ->
                                new Identifier(BADLY_FORMATTED_IDENTIFIER),
                        "Expected a BadlyFormattedIdentifierException, but it wasn't thrown");

        assertEquals(String.format(ERROR_MESSAGE_TEMPLATE, BADLY_FORMATTED_IDENTIFIER),
                badlyFormattedIdentifierException.getMessage());
    }

    @Test
    void isQualified_isSectionIdentifierQualified() {
        Identifier identifier = new Identifier(WELL_FORMATTED_IDENTIFIER);
        assertTrue(identifier.isSectionIdentifierQualified());
    }

    @Test
    void isNotQualified_isSectionIdentifierQualified() {
        Identifier identifier = new Identifier(WELL_FORMATTED_IDENTIFIER_NO_SECTION);
        assertFalse(identifier.isSectionIdentifierQualified());
    }

    @Test
    void isNull() {
        BadlyFormattedIdentifierException exception =
                assertThrows(BadlyFormattedIdentifierException.class, () -> new Identifier(null));

        assertEquals(String.format(ERROR_MESSAGE_TEMPLATE, (Object) null), exception.getMessage());
    }

    @Test
    void test_getFacultyIdentifier() {
        Identifier identifier = new Identifier(123, 12, 12, 1);
        assertEquals("123.12.0.0", identifier.getFacultyIdentifier());
    }

    @Test
    void test_getDepartmentIdentifier() {
        Identifier identifier = new Identifier(123, 12, 12, 1);
        assertEquals("123.12.12.0", identifier.getDepartmentIdentifier());
    }

    @Test
    void test_getFacultyIdentifier_null() {
        Identifier identifier = new Identifier(123, 0, 0, 0);
        assertNull(identifier.getFacultyIdentifier());
    }

    @Test
    void test_getDepartmentIdentifier_null() {
        Identifier identifier = new Identifier(123, 12, 0, 0);
        assertNull(identifier.getDepartmentIdentifier());
    }

    @Test
    void test_gettersAndSetters() {
        int originalInstitution = 123;
        String institution = "122";
        String faculty = "1";
        String department = "1";
        String section = "1";
        Identifier identifier = new Identifier(originalInstitution);
        identifier.setInstitution(institution);
        identifier.setFaculty(faculty);
        identifier.setDepartment(department);
        identifier.setSection(section);
        assertEquals(institution, identifier.getInstitution());
        assertEquals(faculty, identifier.getFaculty());
        assertEquals(department, identifier.getDepartment());
        assertEquals(section, identifier.getSection());
    }
}