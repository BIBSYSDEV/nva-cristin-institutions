package no.unit.nva.cristin.institutions.model;

public class InstitutionBuilder {

    private final transient String identifier;
    private transient String name;

    public InstitutionBuilder(Identifier identifier) {
        this.identifier = identifier.getIdentifier();
    }

    public InstitutionBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public Institution build() {
        return new Institution(identifier, name);
    }


}
