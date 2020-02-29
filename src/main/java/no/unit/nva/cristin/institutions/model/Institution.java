package no.unit.nva.cristin.institutions.model;

import com.google.gson.annotations.Expose;

public class Institution {
    @Expose
    private final String identifier;
    @Expose
    private final transient String name;

    public Institution(String identifier, String name) {
        this.identifier = identifier;
        this.name = name;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getName() {
        return name;
    }
}
