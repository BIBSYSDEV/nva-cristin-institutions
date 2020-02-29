package no.unit.nva.cristin.institutions.model;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BadlyFormattedIdentifierExceptionTest {

    public static final String ERRONEOUS_IDENTIFIER = "123";
    public static final String ERROR_MESSAGE = "The input id \"" + ERRONEOUS_IDENTIFIER
            + "\" did not have expected structure \"{integer}.{integer}.{integer}.{integer}\"";

    @Test
    void itExists() throws BadlyFormattedIdentifierException {
        BadlyFormattedIdentifierException exception = assertThrows(BadlyFormattedIdentifierException.class,
            () -> {
                throw new BadlyFormattedIdentifierException(ERRONEOUS_IDENTIFIER);
            },
            "Expected BadlyFormattedIdentifierException, but it didn't get thrown"
        );
        assertTrue(exception.getMessage().contains(ERROR_MESSAGE));
    }

}