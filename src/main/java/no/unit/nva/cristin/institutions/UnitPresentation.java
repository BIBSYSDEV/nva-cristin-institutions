package no.unit.nva.cristin.institutions;

import java.util.ArrayList;
import java.util.List;

public class UnitPresentation {

    public String cristinUnitId;
    public UnitInstitutionPresentation institution = null;
    public List<NamePresentation> unitNames = new ArrayList<>();
    public List<SubunitPresentation> subunits = new ArrayList<>();
    public ParentUnitPresentation parentUnit = null;

}
