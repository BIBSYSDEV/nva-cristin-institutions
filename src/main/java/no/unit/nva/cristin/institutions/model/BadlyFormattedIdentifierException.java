package no.unit.nva.cristin.institutions.model;

public class BadlyFormattedIdentifierException extends RuntimeException {

    private static final String MESSAGE_TEMPLATE = "The input id \"%s\" did not have expected structure "
            + "\"{integer}.{integer}.{integer}.{integer}\"";

    public BadlyFormattedIdentifierException(String identifier) {
        super(String.format(MESSAGE_TEMPLATE, identifier));
    }
}
