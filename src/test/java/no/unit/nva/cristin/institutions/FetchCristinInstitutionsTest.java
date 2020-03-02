package no.unit.nva.cristin.institutions;

import no.unit.nva.cristin.institutions.model.Identifier;
import no.unit.nva.cristin.institutions.model.Institution;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FetchCristinInstitutionsTest {

    private static final String QUERY_STRING_PARAMETERS_KEY = "queryStringParameters";
    private static final String NAME_KEY = "name";
    private static final String LANGUAGE_KEY = "language";
    private static final String LANGUAGE_NB = "nb";
    private static final String LANGUAGE_INVALID = "invalid";
    private static final String NAME_NTNU = "ntnu";
    private static final String MOCK_EXCEPTION = "Mock exception";
    public static final int CHAR_A = 97;
    public static final int CHAR_Z = 122;


    @Mock
    CristinApiClient mockCristinApiClient;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testFetchCristinInstitutionsSuccessfulResponse() throws Exception {
        when(mockCristinApiClient.getInstitutions(any())).thenReturn(getMockInstitutions(5));
        Map<String, Object> event = new HashMap<>();
        Map<String, String> queryParams = new TreeMap<>();
        queryParams.put(NAME_KEY, NAME_NTNU);
        queryParams.put(LANGUAGE_KEY, LANGUAGE_NB);
        event.put(QUERY_STRING_PARAMETERS_KEY, queryParams);

        FetchCristinInstitutions mockFetchCristinInstitutions = new FetchCristinInstitutions();
        mockFetchCristinInstitutions.setCristinApiClient(mockCristinApiClient);
        GatewayResponse response = mockFetchCristinInstitutions.handleRequest(event, null);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatusCode());
        assertEquals(response.getHeaders().get(HttpHeaders.CONTENT_TYPE), MediaType.APPLICATION_JSON);
    }

    private Institution[] getMockInstitutions(int count) {

        List<Institution> institutionList = new ArrayList<>();
        Random random = new Random();

        while (count > 0) {
            institutionList.add(new Institution(new Identifier(count).getInstitutionIdentifier(),
                    random.ints(CHAR_A, CHAR_Z + 1)
                            .limit(10)
                            .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                            .toString()));
            count--;
        }
        return institutionList.toArray(Institution[]::new);
    }

    @Test
    public void testQueryErrorResponse() throws Exception {
        when(mockCristinApiClient.getInstitutions(any())).thenThrow(new InterruptedException(MOCK_EXCEPTION));

        Map<String, Object> event = new HashMap<>();
        Map<String, String> queryParams = new TreeMap<>();
        queryParams.put(NAME_KEY, NAME_NTNU);
        queryParams.put(LANGUAGE_KEY, LANGUAGE_NB);
        event.put(QUERY_STRING_PARAMETERS_KEY, queryParams);

        FetchCristinInstitutions mockFetchCristinInstitutions = new FetchCristinInstitutions(mockCristinApiClient);
        GatewayResponse response = mockFetchCristinInstitutions.handleRequest(event, null);

        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatusCode());
        assertEquals(response.getHeaders().get(HttpHeaders.CONTENT_TYPE), MediaType.APPLICATION_JSON);
    }

    @Test
    public void testSuccessfulResponseWithGetInstitutionFailure() throws Exception {
        when(mockCristinApiClient.getInstitutions(any())).thenReturn(getMockInstitutions(5));

        Map<String, Object> event = new HashMap<>();
        Map<String, String> queryParams = new TreeMap<>();
        queryParams.put(NAME_KEY, NAME_NTNU);
        queryParams.put(LANGUAGE_KEY, LANGUAGE_NB);
        event.put(QUERY_STRING_PARAMETERS_KEY, queryParams);

        FetchCristinInstitutions mockFetchCristinInstitutions = new FetchCristinInstitutions(mockCristinApiClient);
        GatewayResponse response = mockFetchCristinInstitutions.handleRequest(event, null);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatusCode());
        assertEquals(response.getHeaders().get(HttpHeaders.CONTENT_TYPE), MediaType.APPLICATION_JSON);
    }


    @Test
    public void testInvalidLanguageParam() {

        Map<String, Object> event = new HashMap<>();
        Map<String, String> queryParams = new TreeMap<>();
        queryParams.put(NAME_KEY, NAME_NTNU);
        queryParams.put(LANGUAGE_KEY, LANGUAGE_INVALID);
        event.put(QUERY_STRING_PARAMETERS_KEY, queryParams);

        FetchCristinInstitutions mockFetchCristinInstitutions = new FetchCristinInstitutions(mockCristinApiClient);
        GatewayResponse response = mockFetchCristinInstitutions.handleRequest(event, null);

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatusCode());
        assertEquals(response.getHeaders().get(HttpHeaders.CONTENT_TYPE), MediaType.APPLICATION_JSON);
        assertTrue(response.getBody().contains(FetchCristinInstitutions.LANGUAGE_INVALID));

    }
}
