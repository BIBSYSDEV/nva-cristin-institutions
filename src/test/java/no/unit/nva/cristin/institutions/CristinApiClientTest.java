package no.unit.nva.cristin.institutions;

import no.unit.nva.cristin.institutions.model.Identifier;
import no.unit.nva.cristin.institutions.model.Institution;
import no.unit.nva.cristin.institutions.model.UnitObject;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CristinApiClientTest {
    public static final String LANGUAGE_NB = "nb";
    public static final String INSTITUTION_IDENTIFIER_ZEROS_MATCH = ".0.0.0";

    @Disabled
    @Test
    void getTopLevels() throws ExecutionException, InterruptedException, URISyntaxException {
        HttpExecutor httpExecutor = new HttpExecutorMock();

        CristinApiClient cristinApiClient = new CristinApiClient(httpExecutor);
        Institution[] result = cristinApiClient.getInstitutions(LANGUAGE_NB);
        assertNotNull(result);
        assertTrue(result.length > 1);
        for (Institution institution : result) {
            assertTrue(institution.getIdentifier().contains(INSTITUTION_IDENTIFIER_ZEROS_MATCH));
            assertFalse(institution.getName().isEmpty());
        }
    }

    @Test
    void getUnit() throws ExecutionException, InterruptedException, URISyntaxException {
        HttpExecutor httpExecutor = new HttpExecutorMock();

        CristinApiClient cristinApiClient = new CristinApiClient(httpExecutor);
        Identifier identifier = new Identifier("194.0.0.0");
        UnitObject unitObject = cristinApiClient.getUnit(identifier, LANGUAGE_NB);
        assertTrue(unitObject.getSubunits().size() > 1);
    }

    @Test
    void getSubunit() throws InterruptedException, ExecutionException, URISyntaxException {
        CristinApiClient cristinApiClient = new CristinApiClient(new HttpExecutorMock());
        Identifier identifier = new Identifier("194.1.2.5");
        UnitObject res = cristinApiClient.getUnit(identifier, LANGUAGE_NB);
        assertEquals(1, res.getSubunits().size());
        assertEquals(1, res.getSubunits().get(0).getSubunits().size());
        assertEquals(1, res.getSubunits().get(0).getSubunits().get(0).getSubunits().size());
    }
}