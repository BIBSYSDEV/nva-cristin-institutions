package no.unit.nva.cristin.institutions.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class UnitObject {

    public static final String FULL_PATH = ".0.0.0";
    public static final String EMPTY_IDENTIFIER_ERROR = "The identifier must not be null or empty";
    public static final String EMPTY_STRING = "";

    @SerializedName(alternate = {"cristin_unit_id", "cristin_institution_id"}, value = "id")
    @Expose
    private final String id;
    @Expose
    private final String name;
    @Expose(serialize = false)
    @SerializedName(alternate = {"unit_name", "institution_name"}, value = "unitName")
    private final Map<String, String> unitName;
    @Expose(serialize = false)
    @SerializedName("country")
    private final transient String country;
    @Expose(serialize = false)
    @SerializedName(alternate = "cristin_user_institution", value = "isCristinUser")
    private final transient boolean cristinUserInstitution;
    @Expose(serialize = false)
    private final transient Map<String, String> institution;
    @Expose(serialize = false)
    @SerializedName(alternate = "url", value = "uri")
    private final transient String uri;
    @Expose(serialize = false)
    private final transient String acronym;
    @Expose
    private List<UnitObject> subunits;

    public static Comparator<UnitObject> UnitIdReverseComparator = (unitOne, unitTwo) -> {
        String unitOneId = unitOne.getId().toUpperCase(Locale.getDefault());
        String unitTwoId = unitTwo.getId().toUpperCase(Locale.getDefault());
        return unitTwoId.compareTo(unitOneId);
    };

    public String getId() {
        return this.id;
    }

    /**
     * This constructor allows the creation of a basic UnitObject object.
     * @param id A string of dot-separated integers following pattern "{institution}.{faculty}.{department}.{section}".
     * @param unitName A singleton map containing a key for language and a string value for unit name
     * @param institution The name of the institution to which the unit belongs
     * @param uri The Cristin URI of the unit
     * @param acronym The acronym of the institution to which the unit belongs
     * @param subunits A list of child Units
     * @param cristinUserInstitution A boolean value denoting the institution to which the unit belongs
     *                               is a Cristin user
     */
    public UnitObject(String id,
                      Map<String, String> unitName,
                      Map<String, String> institution,
                      String uri,
                      String acronym,
                      List<UnitObject> subunits,
                      boolean cristinUserInstitution) {
        if (isNull(id) || id.isEmpty()) {
            throw new IllegalArgumentException(EMPTY_IDENTIFIER_ERROR);
        }
        if (isUnqualifiedId(id)) {
            this.id = id + FULL_PATH;
        } else {
            this.id = id;
        }

        this.unitName = unitName;
        this.institution = institution;
        this.uri = uri;
        this.acronym = acronym;
        if (nonNull(this.unitName) && this.unitName.size() > 0) {
            this.name = this.unitName.get(this.unitName.keySet().iterator().next());
        } else {
            this.name = EMPTY_STRING;
        }
        this.subunits = subunits;
        this.cristinUserInstitution = cristinUserInstitution;
        this.country = EMPTY_STRING;
    }

    public String getUri() {
        return this.uri;
    }

    public Map<String, String> getInstitution() {
        return this.institution;
    }

    public String getAcronym() {
        return this.acronym;
    }

    /**
     * getName Gets the unit name, initializing the name from the first value in unitName in case this is null or empty.
     * @return String unit name
     */
    public String getName() {
        return this.name;
    }

    public List<UnitObject> getSubunits() {
        return subunits;
    }

    /**
     * setSubunits Sets the subunits given, or creates an empty arraylist.
     * @param unitObjects A list of Units
     */
    public void setSubunits(List<UnitObject> unitObjects) {
        if (isNull(this.subunits)) {
            this.subunits = new ArrayList<>();
        }
        this.subunits.addAll(new ArrayList<>(unitObjects));
    }

    /**
     * getIdentifier Returns an Identifier object of the id.
     * @return an Identifier object
     */
    public Identifier getIdentifier() {
        return new Identifier(id);
    }

    public Map<String, String> getUnitName() {
        return unitName;
    }

    public boolean isCristinUser() {
        return this.cristinUserInstitution;
    }

    private boolean isUnqualifiedId(String id) {
        try {
            Double.parseDouble(id);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    public String getCountry() {
        return this.country;
    }
}
