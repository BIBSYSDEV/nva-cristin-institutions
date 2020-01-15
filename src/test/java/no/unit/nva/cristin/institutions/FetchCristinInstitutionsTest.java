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
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FetchCristinInstitutionsTest {

    private static final String CRISTIN_GET_INSTITUTION_RESPONSE_JSON_FILE = "/cristinGetInstitutionResponse.json";
    private static final String CRISTIN_QUERY_INSTITUTION_RESPONSE_JSON_FILE = "/cristinQueryInstitutionsResponse.json";
    private static final String QUERY_STRING_PARAMETERS_KEY = "queryStringParameters";
    private static final String NAME_KEY = "name";
    private static final String LANGUAGE_KEY = "language";
    private static final String LANGUAGE_NB = "nb";
    private static final String LANGUAGE_INVALID = "invalid";
    private static final String NAME_NTNU = "ntnu";
    private static final String NAME_ILLEGAL_CHARACTERS = "abc123- ?";
    private static final String INVALID_JSON = "This is not valid JSON!";
    private static final String DEV_NULL = "/dev/null";

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @Mock
    CristinApiClient mockCristinApiClient;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }


    private InputStreamReader mockQueryCristinQueryInstitutionResponseReader() {
        InputStream getResultAsStream = FetchCristinInstitutionsTest.class
                .getResourceAsStream(CRISTIN_QUERY_INSTITUTION_RESPONSE_JSON_FILE);
        return new InputStreamReader(getResultAsStream);
    }

    private InputStreamReader mockGetCristinGetInstitutionResponseReader() {
        InputStream getResultAsStream = FetchCristinInstitutionsTest.class
                .getResourceAsStream(CRISTIN_GET_INSTITUTION_RESPONSE_JSON_FILE);
        return new InputStreamReader(getResultAsStream);
    }


    @Test
    public void testFetchCristinInstitutionsSuccessfulResponse() throws Exception {
        when(mockCristinApiClient.fetchQueryInstitutionsResults(any()))
                .thenReturn(mockQueryCristinQueryInstitutionResponseReader());
        when(mockCristinApiClient.fetchGetInstitutionResult(any()))
                .thenAnswer(i -> mockGetCristinGetInstitutionResponseReader());
        when(mockCristinApiClient.queryInstitutions(any())).thenCallRealMethod();
        when(mockCristinApiClient.getInstitution(any(), any())).thenCallRealMethod();
        when(mockCristinApiClient.generateQueryInstitutionsUrl(any())).thenCallRealMethod();
        when(mockCristinApiClient.generateGetInstitutionUrl(any(), any())).thenCallRealMethod();

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

    @Test
    public void testQueryErrorResponse() throws Exception {
        when(mockCristinApiClient.queryInstitutions(any())).thenThrow(new IOException("Mock exception"));

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
        when(mockCristinApiClient.fetchQueryInstitutionsResults(any()))
                .thenReturn(mockQueryCristinQueryInstitutionResponseReader());
        when(mockCristinApiClient.queryInstitutions(any())).thenCallRealMethod();
        when(mockCristinApiClient.getInstitution(any(), any())).thenThrow(new IOException("Mock exception"));
        when(mockCristinApiClient.generateQueryInstitutionsUrl(any())).thenCallRealMethod();

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
    public void testEmptyNameParam() {

        Map<String, Object> event = new HashMap<>();
        Map<String, String> queryParams = new TreeMap<>();
        queryParams.put(NAME_KEY, "");
        event.put(QUERY_STRING_PARAMETERS_KEY, queryParams);

        FetchCristinInstitutions mockFetchCristinInstitutions = new FetchCristinInstitutions(mockCristinApiClient);
        GatewayResponse response = mockFetchCristinInstitutions.handleRequest(event, null);

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatusCode());
        assertEquals(response.getHeaders().get(HttpHeaders.CONTENT_TYPE), MediaType.APPLICATION_JSON);
        assertEquals(response.getBody(), "{\"error\":\"Parameter 'name' is mandatory\"}");
    }


    @Test
    public void testIllegalCharactersNameParam() {

        Map<String, Object> event = new HashMap<>();
        Map<String, String> queryParams = new TreeMap<>();
        queryParams.put(NAME_KEY, NAME_ILLEGAL_CHARACTERS);
        event.put(QUERY_STRING_PARAMETERS_KEY, queryParams);

        FetchCristinInstitutions mockFetchCristinInstitutions = new FetchCristinInstitutions(mockCristinApiClient);
        GatewayResponse response = mockFetchCristinInstitutions.handleRequest(event, null);

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatusCode());
        assertEquals(response.getHeaders().get(HttpHeaders.CONTENT_TYPE), MediaType.APPLICATION_JSON);
        assertEquals(response.getBody(), "{\"error\":\"Parameter 'name' may only contain alphanumeric "
                + "characters, dash and whitespace\"}");
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
        assertEquals(response.getBody(), "{\"error\":\"Parameter 'language' has invalid value\"}");
    }

    @Test
    public void testCristinGenerateQueryInstitutionsUrlFromNull() throws IOException, URISyntaxException {
        CristinApiClient cristinApiClient = new CristinApiClient();
        cristinApiClient.generateQueryInstitutionsUrl(null);
    }

    @Test
    public void testCristinInstitutionsConnection() throws IOException {
        CristinApiClient cristinApiClient = new CristinApiClient();
        URL invalidUrl = Paths.get(DEV_NULL).toUri().toURL();
        cristinApiClient.fetchQueryInstitutionsResults(invalidUrl);
    }

    @Test
    public void testCristinInstitutionConnection() throws IOException {
        CristinApiClient cristinApiClient = new CristinApiClient();
        URL invalidUrl = Paths.get(DEV_NULL).toUri().toURL();
        cristinApiClient.fetchGetInstitutionResult(invalidUrl);
    }

    @Test(expected = IOException.class)
    public void testExceptionOnInvalidJson() throws IOException {
        CristinApiClient cristinApiClient = new CristinApiClient();
        InputStream inputStream = new ByteArrayInputStream(INVALID_JSON.getBytes(Charset.forName("UTF-8")));
        InputStreamReader reader = new InputStreamReader(inputStream);
        cristinApiClient.fromJson(reader, Institution.class);
        fail();
    }
}
