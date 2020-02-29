package no.unit.nva.cristin.institutions.model;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class Identifier {
    public static final String IDENTIFIER_TEMPLATE = "%s.%s.%s.%s";
    public static final String EMPTY_SUBUNIT = "0";
    public static final String MATCH_SEPARATOR = "\\.";
    public static final int FOUR_SEGMENTS = 4;

    private String institution;
    private String faculty;
    private String department;
    private String section;

    public String getInstitution() {
        return institution;
    }

    public void setInstitution(String institution) {
        this.institution = institution;
    }

    public String getFaculty() {
        return faculty;
    }

    public void setFaculty(String faculty) {
        this.faculty = faculty;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }


    /**
     * An object to hold identifiers composed according to pattern "{institution}.{faculty}.{department}.{section}".
     *
     * @param identifier A dot-separated string formatted according to pattern {integer}.{integer}.{integer}.{integer}
     * @throws BadlyFormattedIdentifierException In case the identifier does not match the input pattern
     */
    public Identifier(String identifier) {
        if (isNull(identifier) || identifier.isEmpty()) {
            throw new BadlyFormattedIdentifierException(identifier);
        }
        if (identifier.split(MATCH_SEPARATOR).length == FOUR_SEGMENTS) {
            String[] segments = identifier.split(MATCH_SEPARATOR);
            this.institution  = segments[0];
            this.faculty = segments[1];
            this.department = segments[2];
            this.section = segments[3];
        } else {
            throw new BadlyFormattedIdentifierException(identifier);
        }
    }

    /**
     * A method to create a top-level unit.
     *
     * @param institution An integer representing the top-level unit
     */
    public Identifier(int institution) {
        this(String.format(IDENTIFIER_TEMPLATE,
                institution, EMPTY_SUBUNIT, EMPTY_SUBUNIT, EMPTY_SUBUNIT));
    }

    /**
     * A method to create a faculty level unit.
     *
     * @param institution An integer representing the top-level unit
     * @param faculty     An integer representing the faculty level unit
     */
    public Identifier(int institution, int faculty) {
        this(String.format(IDENTIFIER_TEMPLATE, institution, faculty, EMPTY_SUBUNIT, EMPTY_SUBUNIT));
    }

    /**
     * A method to create a department level unit.
     *
     * @param institution An integer representing the top-level unit
     * @param faculty     An integer representing the faculty level unit
     * @param department  An integer representing the department-level unit
     */
    public Identifier(int institution, int faculty, int department) {
        this(String.format(IDENTIFIER_TEMPLATE, institution, faculty, department, EMPTY_SUBUNIT));
    }

    /**
     * A method to create a section level unit.
     *
     * @param institution An integer representing the top-level unit
     * @param faculty     An integer representing the faculty level unit
     * @param department  An integer representing the department-level unit
     * @param section     An integer representing the section-level unit
     */
    public Identifier(int institution, int faculty, int department, int section) {
        this(String.format(IDENTIFIER_TEMPLATE, institution, faculty, department, section));

    }

    public String getIdentifier() {
        return String.format(IDENTIFIER_TEMPLATE, institution, faculty, department, section);
    }

    public String getInstitutionIdentifier() {
        return String.format(IDENTIFIER_TEMPLATE, institution, EMPTY_SUBUNIT, EMPTY_SUBUNIT, EMPTY_SUBUNIT);

    }

    /**
     * getFacultyIdentifier This method returns the faculty Identifier string for a given Identifier.
     *
     * @return Either a string formatted according to "{integer}.{integer}.0.0" or null for a top-level identifier
     */
    public String getFacultyIdentifier() {
        return nonNull(faculty) && !faculty.equals(EMPTY_SUBUNIT)
                ? String.format(IDENTIFIER_TEMPLATE,
                institution, faculty, EMPTY_SUBUNIT, EMPTY_SUBUNIT) : null;
    }

    /**
     * getDepartmentIdentifier This method returns the department Identifier for a given Identifier.
     *
     * @return Either a string formatted according to "{integer}.{integer}.{integer}.0" or null for a top-level or
     *     faculty identifier
     */
    public String getDepartmentIdentifier() {
        return nonNull(department) && !department.equals(EMPTY_SUBUNIT)
                ? String.format(IDENTIFIER_TEMPLATE,
                institution, faculty, department, EMPTY_SUBUNIT) : null;
    }

    public String getSectionIdentifier() {
        return getIdentifier();
    }

    public boolean isSectionIdentifierQualified() {
        return Integer.parseInt(section) > 0;
    }
}
