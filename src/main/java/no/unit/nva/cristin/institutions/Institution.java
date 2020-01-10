package no.unit.nva.cristin.institutions;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

public class Institution {

    @SerializedName("cristin_institution_id")
    public String cristinInstitutionId;
    @SerializedName("institution_name")
    public Map<String, String> institutionName;
    public String acronym;
    public String country;
    @SerializedName("corresponding_unit")
    public Unit correspondingUnit;

}
