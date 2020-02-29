package no.unit.nva.cristin.institutions;

import no.unit.nva.cristin.institutions.model.Identifier;
import no.unit.nva.cristin.institutions.util.UnitGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FetchCristinUnitObjectTest {

    private static final String QUERY_STRING_PARAMETERS_KEY = "queryStringParameters";
    private static final String PATH_PARAMETERS_KEY = "pathParameters";
    private static final String ID_KEY = "id";
    private static final String LANGUAGE_KEY = "language";
    private static final String LANGUAGE_INVALID = "invalid";
    private static final String EMPTY_STRING = "";
    private static final String LANGUAGE_NB = "nb";
    private static final String ID_NTNU = "194.0.0.0";
    public static final String AN_INVALID_IDENTIFIER = "an invalid identifier";
    public static final String ERROR_INVALID_IDENTIFIER = "{\"error\":\"The input id \\\"" + AN_INVALID_IDENTIFIER
                    + "\\\" did not have expected structure \\\"{integer}.{integer}.{integer}.{integer}\\\"\"}";
    public static final String ERROR_PARAMETER_ID_IS_MANDATORY = "{\"error\":\"Parameter 'id' is mandatory\"}";
    public static final String ERROR_PARAMETER_LANGUAGE_HAS_INVALID_VALUE =
            "{\"error\":\"Parameter 'language' has invalid value\"}";

    @Mock
    CristinApiClient mockCristinApiClient;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testFetchCristinUnitSuccessfulResponse() throws Exception {

        when(mockCristinApiClient.getUnit(any(), any()))
                .thenReturn(UnitGenerator.generateMockUnit(new Identifier(100, 2, 1, 1), "nb"));

        Map<String, Object> event = new HashMap<>();

        Map<String, String> pathParams = new TreeMap<>();
        pathParams.put(ID_KEY, ID_NTNU);
        event.put(PATH_PARAMETERS_KEY, pathParams);

        Map<String, String> queryParams = new TreeMap<>();
        queryParams.put(LANGUAGE_KEY, LANGUAGE_NB);
        event.put(QUERY_STRING_PARAMETERS_KEY, queryParams);

        FetchCristinUnit mockFetchCristinUnit = new FetchCristinUnit();
        mockFetchCristinUnit.setCristinApiClient(mockCristinApiClient);
        GatewayResponse response = mockFetchCristinUnit.handleRequest(event, null);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatusCode());
        assertEquals(response.getHeaders().get(HttpHeaders.CONTENT_TYPE), MediaType.APPLICATION_JSON);
    }

    @Test
    public void testErrorResponse() throws Exception {
        when(mockCristinApiClient.getUnit(any(), any())).thenThrow(new InterruptedException());

        Map<String, Object> event = new HashMap<>();

        Map<String, String> pathParams = new TreeMap<>();
        pathParams.put(ID_KEY, ID_NTNU);
        event.put(PATH_PARAMETERS_KEY, pathParams);

        Map<String, String> queryParams = new TreeMap<>();
        queryParams.put(LANGUAGE_KEY, LANGUAGE_NB);
        event.put(QUERY_STRING_PARAMETERS_KEY, queryParams);

        FetchCristinUnit mockFetchCristinUnit = new FetchCristinUnit(mockCristinApiClient);
        GatewayResponse response = mockFetchCristinUnit.handleRequest(event, null);

        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatusCode());
        assertEquals(response.getHeaders().get(HttpHeaders.CONTENT_TYPE), MediaType.APPLICATION_JSON);
    }


    @Test
    public void testEmptyIdParam() {

        Map<String, Object> event = new HashMap<>();
        Map<String, String> pathParams = new TreeMap<>();
        pathParams.put(ID_KEY, EMPTY_STRING);
        event.put(PATH_PARAMETERS_KEY, pathParams);

        FetchCristinUnit mockFetchCristinUnit = new FetchCristinUnit(mockCristinApiClient);
        GatewayResponse response = mockFetchCristinUnit.handleRequest(event, null);

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatusCode());
        assertEquals(response.getHeaders().get(HttpHeaders.CONTENT_TYPE), MediaType.APPLICATION_JSON);
        assertEquals(response.getBody(), ERROR_PARAMETER_ID_IS_MANDATORY);
    }


    @Test
    public void testInvalidLanguageParam() {

        Map<String, Object> event = new HashMap<>();

        Map<String, String> pathParams = new TreeMap<>();
        pathParams.put(ID_KEY, ID_NTNU);
        event.put(PATH_PARAMETERS_KEY, pathParams);

        Map<String, String> queryParams = new TreeMap<>();
        queryParams.put(LANGUAGE_KEY, LANGUAGE_INVALID);
        event.put(QUERY_STRING_PARAMETERS_KEY, queryParams);

        FetchCristinUnit mockFetchCristinUnit = new FetchCristinUnit(mockCristinApiClient);
        GatewayResponse response = mockFetchCristinUnit.handleRequest(event, null);

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatusCode());
        assertEquals(response.getHeaders().get(HttpHeaders.CONTENT_TYPE), MediaType.APPLICATION_JSON);
        assertEquals(response.getBody(), ERROR_PARAMETER_LANGUAGE_HAS_INVALID_VALUE);
    }

    @Test
    public void test_badlyFormattedIdentifier() {
        Map<String, Object> event = new HashMap<>();

        Map<String, String> pathParams = new TreeMap<>();
        pathParams.put(ID_KEY, AN_INVALID_IDENTIFIER);
        event.put(PATH_PARAMETERS_KEY, pathParams);

        Map<String, String> queryParams = new TreeMap<>();
        queryParams.put(LANGUAGE_KEY, LANGUAGE_NB);
        event.put(QUERY_STRING_PARAMETERS_KEY, queryParams);
        FetchCristinUnit mockFetchCristinUnit = new FetchCristinUnit(mockCristinApiClient);
        GatewayResponse response = mockFetchCristinUnit.handleRequest(event, null);

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatusCode());
        assertEquals(response.getHeaders().get(HttpHeaders.CONTENT_TYPE), MediaType.APPLICATION_JSON);
        assertEquals(ERROR_INVALID_IDENTIFIER,
                response.getBody());
    }



}
