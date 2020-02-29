package no.unit.nva.cristin.institutions.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Objects.isNull;

public class UnitObjectBuilder {
    private final transient String id;
    private transient Map<String, String> unitName = new ConcurrentHashMap<>();
    private transient Map<String, String> institution = new ConcurrentHashMap<>();
    private transient String uri;
    private transient String acronym;
    private transient List<UnitObject> subunits;
    private transient boolean isCristinUser;

    public UnitObjectBuilder(Identifier id) {
        this.id = id.getIdentifier();
    }

    /**
     * Build object for the creation of Units.
     * @param id A string of dot-separated integers following pattern "{institution}.{faculty}.{department}.{section}".
     * @param unitName A singleton map containing a key for language and a string value for unit name
     * @param institution The name of the institution to which the unit belongs
     * @param uri The Cristin URI of the unit
     * @param acronym The acronym of the institution to which the unit belongs
     * @param subunits A list of child Units
     * @param isCristinUser A boolean value denoting the institution to which the unit belongs is a Cristin user
     */
    public UnitObjectBuilder(String id,
                             Map<String, String> unitName,
                             Map<String, String> institution,
                             String uri, String acronym,
                             List<UnitObject> subunits,
                             boolean isCristinUser) {
        this.id = id;
        this.unitName = unitName;
        this.institution = institution;
        this.uri = uri;
        this.acronym = acronym;
        this.subunits = subunits;
        this.isCristinUser = isCristinUser;
    }

    public UnitObjectBuilder withUnitName(Map<String, String> unitName) {
        this.unitName = unitName;
        return this;
    }

    public UnitObjectBuilder withInstitution(Map<String, String> institution) {
        this.institution = institution;
        return this;
    }

    public UnitObjectBuilder withUri(String uri) {
        this.uri = uri;
        return this;
    }

    public UnitObjectBuilder withAcronym(String acronym) {
        this.acronym = acronym;
        return this;
    }

    /**
     * setSubunits Sets the subunits given, or creates an empty arraylist.
     * @param unitObjects A list of Units
     */
    public UnitObjectBuilder withSubunits(List<UnitObject> unitObjects) {
        if (isNull(this.subunits)) {
            this.subunits = new ArrayList<>();
        }
        this.subunits.addAll(new ArrayList<>(unitObjects));
        return this;
    }

    public UnitObject build() {
        return new UnitObject(id, unitName, institution, uri, acronym, subunits, isCristinUser);
    }

    public UnitObjectBuilder withIsCristinUser(boolean isCristinUser) {
        this.isCristinUser = isCristinUser;
        return this;
    }
}
