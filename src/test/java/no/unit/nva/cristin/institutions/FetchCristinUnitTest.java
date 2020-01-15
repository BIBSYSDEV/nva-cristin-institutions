package no.unit.nva.cristin.institutions;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mockito.runners.MockitoJUnitRunner;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FetchCristinUnitTest {

    private static final String CRISTIN_GET_UNIT_WITH_SUBUNITS_FIRST_RESPONSE_JSON_FILE =
            "/cristinGetUnitWithSubunitsFirstResponse.json";
    private static final String CRISTIN_GET_UNIT_WITH_SUBUNITS_SECOND_RESPONSE_JSON_FILE =
            "/cristinGetUnitWithSubunitsSecondResponse.json";
    private static final String CRISTIN_GET_UNIT_WITHOUT_SUBUNITS_RESPONSE_JSON_FILE =
            "/cristinGetUnitWithoutSubunitsResponse.json";
    private static final String QUERY_STRING_PARAMETERS_KEY = "queryStringParameters";
    private static final String PATH_PARAMETERS_KEY = "pathParameters";
    private static final String ID_KEY = "id";
    private static final String LANGUAGE_KEY = "language";
    private static final String LANGUAGE_INVALID = "invalid";
    private static final String DEV_NULL = "/dev/null";
    private static final String EMPTY_STRING = "";
    private static final String LANGUAGE_NB = "nb";
    private static final String ID_NTNU = "194.0.0.0";
    private static final String MOCK_EXCEPTION = "Mock exception";


    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @Mock
    CristinApiClient mockCristinApiClient;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }


    private InputStreamReader mockGetCristinUnitWithSubunitsFirstResponseReader() {
        InputStream getResultAsStream = FetchCristinInstitutionsTest.class
                .getResourceAsStream(CRISTIN_GET_UNIT_WITH_SUBUNITS_FIRST_RESPONSE_JSON_FILE);
        return new InputStreamReader(getResultAsStream);
    }

    private InputStreamReader mockGetCristinUnitWithSubunitsSecondResponseReader() {
        InputStream getResultAsStream = FetchCristinInstitutionsTest.class
                .getResourceAsStream(CRISTIN_GET_UNIT_WITH_SUBUNITS_SECOND_RESPONSE_JSON_FILE);
        return new InputStreamReader(getResultAsStream);
    }

    private InputStreamReader mockGetCristinUnitWithoutSubunitsResponseReader() {
        InputStream getResultAsStream = FetchCristinInstitutionsTest.class
                .getResourceAsStream(CRISTIN_GET_UNIT_WITHOUT_SUBUNITS_RESPONSE_JSON_FILE);
        return new InputStreamReader(getResultAsStream);
    }


    @Test
    public void testFetchCristinUnitSuccessfulResponse() throws Exception {
        when(mockCristinApiClient.fetchGetUnitResult(any()))
                .thenReturn(mockGetCristinUnitWithSubunitsFirstResponseReader());
        when(mockCristinApiClient.getUnit(any(), any())).thenCallRealMethod();
        when(mockCristinApiClient.generateGetUnitUrl(any(), any())).thenCallRealMethod();

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
        when(mockCristinApiClient.getUnit(any(), any())).thenThrow(new IOException(MOCK_EXCEPTION));

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
        assertEquals(response.getBody(), "{\"error\":\"Parameter 'id' is mandatory\"}");
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
        assertEquals(response.getBody(), "{\"error\":\"Parameter 'language' has invalid value\"}");
    }

    @Test
    public void testCristinUnitConnection() throws IOException {
        CristinApiClient cristinApiClient = new CristinApiClient();
        URL invalidUrl = Paths.get(DEV_NULL).toUri().toURL();
        cristinApiClient.fetchGetUnitResult(invalidUrl);
    }

}
