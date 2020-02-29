package no.unit.nva.cristin.institutions;

import no.unit.nva.cristin.institutions.model.Identifier;
import no.unit.nva.cristin.institutions.model.UnitObject;
import no.unit.nva.cristin.institutions.model.UnitObjectBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.Objects.nonNull;

public class Nester {

    public static final String MATCH_DOT = "\\.";
    public static final int UNSPECIFIED_LEVEL = 0;
    public static final int SECTION_LEVEL = 3;
    public static final int DEPARTMENT_LEVEL = 2;
    public static final int FACULTY_LEVEL = 1;
    private transient UnitObject unitObject;

    /**
     * A method to take a flat array of Units and nest them according to their identifiers,
     * so that 123.1.1.1 is nested under 123.1.1.0, which is nested under 123.1.0.0 and so on.
     *
     * @param unitObjects       An array of UnitObject objects
     * @param subunitIdentifier In cases where only parent unitObjects are to be returned, pass the subunit identifier
     */
    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
    public Nester(UnitObject[] unitObjects, Identifier subunitIdentifier) {
        UnitObject[] units = unitObjects;
        Arrays.sort(unitObjects, UnitObject.UnitIdReverseComparator);
        List<UnitObject> secondLevel = new ArrayList<>();
        List<UnitObject> thirdLevel = new ArrayList<>();
        List<UnitObject> children = new ArrayList<>();

        if (nonNull(subunitIdentifier)) {
            units = Arrays.stream(unitObjects)
                    .filter(unit -> unit.getId().equals(subunitIdentifier.getIdentifier())
                            || unit.getId().equals(subunitIdentifier.getInstitutionIdentifier())
                            || unit.getId().equals(subunitIdentifier.getFacultyIdentifier())
                            || unit.getId().equals(subunitIdentifier.getDepartmentIdentifier()))
                    .toArray(UnitObject[]::new);
        }

        for (UnitObject currentUnitObject : units) {
            Integer[] ids = splitId(currentUnitObject.getId());
            if (ids[SECTION_LEVEL] != UNSPECIFIED_LEVEL) {
                children.add(currentUnitObject);
                continue;
            } else if (ids[DEPARTMENT_LEVEL] != UNSPECIFIED_LEVEL && !children.isEmpty()) {
                currentUnitObject.setSubunits(children);
                children.clear();
            }
            if (ids[DEPARTMENT_LEVEL] != UNSPECIFIED_LEVEL) {
                thirdLevel.add(currentUnitObject);
                continue;
            } else if (ids[FACULTY_LEVEL] != UNSPECIFIED_LEVEL && !thirdLevel.isEmpty()) {
                currentUnitObject.setSubunits(thirdLevel);
                thirdLevel.clear();
            }
            if (ids[FACULTY_LEVEL] != UNSPECIFIED_LEVEL) {
                secondLevel.add(currentUnitObject);
            } else {
                this.unitObject = new UnitObjectBuilder(new Identifier(currentUnitObject.getId()))
                        .withAcronym(currentUnitObject.getAcronym())
                        .withInstitution(currentUnitObject.getInstitution())
                        .withUnitName(currentUnitObject.getUnitName())
                        .withUri(currentUnitObject.getUri())
                        .withSubunits(secondLevel)
                        .build();
            }
        }
    }

    private Integer[] splitId(String id) {
        String[] splitId = id.split(MATCH_DOT);
        return Arrays.stream(splitId).map(Integer::parseInt).toArray(Integer[]::new);
    }

    public UnitObject getUnitObject() {
        return unitObject;
    }
}
