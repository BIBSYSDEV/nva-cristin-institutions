package no.unit.nva.cristin.institutions;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.TreeMap;

public class PresentationConverter {

    protected InstitutionPresentation asInstitutionPresentation(Institution institution) {
        InstitutionPresentation institutionPresentation = new InstitutionPresentation();
        institutionPresentation.cristinInstitutionId = institution.cristinInstitutionId;

        Optional.ofNullable(institution.institutionName).orElse(new TreeMap<String, String>() {
        }).forEach((key, value) -> {
            NamePresentation namePresentation = new NamePresentation();
            namePresentation.language = key;
            namePresentation.name = value;
            institutionPresentation.institutionNames.add(namePresentation);
        });

        institutionPresentation.acronym = Optional.ofNullable(institution.acronym).orElse("");
        institutionPresentation.country = Optional.ofNullable(institution.country).orElse("");

        if (Optional.ofNullable(institution.correspondingUnit).isPresent()) {
            institutionPresentation.cristinUnitId = institution.correspondingUnit.cristinUnitId;
        }

        return institutionPresentation;
    }

    protected List<UnitPresentation> asSubunits(Unit unit) {

        List<UnitPresentation> unitPresentations = new ArrayList<>();

        Optional.ofNullable(unit.subunits).orElse(new ArrayList<Unit>() {
        }).forEach(subunit -> {
            UnitPresentation unitPresentation = new UnitPresentation();

            unitPresentation.cristinUnitId = subunit.cristinUnitId;

            Optional.ofNullable(subunit.unitName).orElse(new TreeMap<>())
                    .forEach((language, name) -> {
                        NamePresentation namePresentation = new NamePresentation();
                        namePresentation.language = language;
                        namePresentation.name = name;
                        unitPresentation.unitNames.add(namePresentation);
                    });

            unitPresentations.add(unitPresentation);
        });

        return unitPresentations;
    }

}
