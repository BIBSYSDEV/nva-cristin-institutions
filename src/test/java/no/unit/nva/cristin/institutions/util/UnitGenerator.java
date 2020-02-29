package no.unit.nva.cristin.institutions.util;

import no.unit.nva.cristin.institutions.model.Identifier;
import no.unit.nva.cristin.institutions.model.UnitObject;
import no.unit.nva.cristin.institutions.model.UnitObjectBuilder;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.IntStream;

public class UnitGenerator {

    public static final String SOME_URI_BASE = "https://example.org/";
    public static final String MATCH_DOT = "\\.";

    /**
     * Allows the creation of a number of mock UnitObject objects with a set of language strings.
     * @param count The number of objects to be created
     * @param language The language of the strings to be created, e.g. en, nb, nn
     * @return An array of UnitObject objects
     */
    public static UnitObject[] generateMockUnits(int count, String language) {
        List<UnitObject> unitObjects = new ArrayList<>();
        while (count > 0) {
            unitObjects.add(generateMockUnit(new Identifier(count), language));
            count--;
        }

        return unitObjects.toArray(UnitObject[]::new);
    }

    /**
     * Creates an array of objects within a particular institution based on the input identifier, creating all the
     *     defined subunits for the tree specified by the identifier.
     * @param identifier An identifier object
     * @param language A language for string, typically one of "en", "nb", "nn"
     * @return An array of UnitObject objects
     */
    public static UnitObject[] generateMockUnits(Identifier identifier, String language) {
        List<UnitObject> unitObjects = new ArrayList<>();
        String[] ids = identifier.getIdentifier().split(MATCH_DOT);
        Set<Identifier> identifiers = new HashSet<>();
        int base = Integer.parseInt(ids[0]);
        int faculty = Integer.parseInt(ids[1]);
        int department = Integer.parseInt(ids[2]);
        int section = Integer.parseInt(ids[3]);

        for (int fac : IntStream.rangeClosed(faculty, faculty + 5).toArray()) {
            for (int dep : IntStream.rangeClosed(department, department + 5).toArray()) {
                for (int sec : IntStream.rangeClosed(section, section + 5).toArray()) {
                    identifiers.add(new Identifier(base, fac, dep, sec));
                }
            }
        }

        for (Identifier id : identifiers) {
            unitObjects.add(generateMockUnit(id, language));
        }

        return unitObjects.toArray(UnitObject[]::new);
    }

    /**
     * Generates a single unit object with multiple nested subunits including the unit denoted by the given identifier.
     * @param identifier An Identifier object
     * @param faculties The number of faculties to be generated
     * @param departments The number of departments to be generated
     * @param institutes The number of institutes to be generated
     * @param language The language of the generated strings, typically one of en, nb, nn
     * @return
     */
    public static UnitObject generateMockInstitution(Identifier identifier,
                                                     int faculties,
                                                     int departments,
                                                     int institutes,
                                                     String language) {

        int base = Integer.parseInt(identifier.getInstitution());

        int[] facultiesArray = IntStream.rangeClosed(1, faculties).toArray();
        int[] departmentsArray = IntStream.rangeClosed(1, departments).toArray();
        int[] institutesArray = IntStream.rangeClosed(1, institutes).toArray();

        List<UnitObject> facultyList = new ArrayList<>();

        for (int faculty : facultiesArray) {
            List<UnitObject> departmentList = new ArrayList<>();
            for (int department : departmentsArray) {
                List<UnitObject> instituteList = new ArrayList<>();
                for (int institute : institutesArray) {
                    Identifier instituteIdentifier = new Identifier(base, faculty, department, institute);
                    instituteList.add(generateMockUnit(instituteIdentifier, language));
                }
                Identifier departmentIdentifier = new Identifier(base, faculty, department);
                UnitObject unitObject = generateMockUnit(departmentIdentifier, language);
                unitObject.setSubunits(instituteList);
                departmentList.add(unitObject);
            }
            Identifier facultyIdentifier = new Identifier(base, faculty);
            UnitObject unitObject = generateMockUnit(facultyIdentifier, language);
            unitObject.setSubunits(departmentList);
            facultyList.add(unitObject);
        }
        Identifier institutionIdentifier = new Identifier(base);
        UnitObject unitObject = generateMockUnit(institutionIdentifier, language);
        unitObject.setSubunits(facultyList);
        return unitObject;
    }

    /**
     * Generates a single mock UnitObject object.
     * @param identifier The identifier of the unit
     * @param lang The language of the generated strings, typically one of en, nb, nn
     * @return A UnitObject object
     */
    public static UnitObject generateMockUnit(Identifier identifier, String lang) {
        String name = randomString(1).toUpperCase() + randomString(7);
        return new UnitObjectBuilder(identifier)
                .withAcronym(randomString(4).toUpperCase())
                .withUri(SOME_URI_BASE + randomString(6))
                .withUnitName(Collections.singletonMap(lang, name))
                .withIsCristinUser(true)
                .build();
    }

    private static String randomString(int size) {
        byte[] array = new byte[size]; // length is bounded by 7
        new Random().nextBytes(array);
        return new String(array, StandardCharsets.UTF_8);
    }
}
