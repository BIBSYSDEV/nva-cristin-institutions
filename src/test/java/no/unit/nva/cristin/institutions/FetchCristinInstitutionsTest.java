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
import java.io.InputStream;
import java.io.InputStreamReader;
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

    private static final String QUERY_PARAM_LANGUAGE_NB = "nb";
    private static final String QUERY_PARAM_NAME_NTNU = "ntnu";

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
        when(mockCristinApiClient.fetchQueryInstitutionsResults(any())).thenReturn(mockQueryCristinQueryInstitutionResponseReader());
        when(mockCristinApiClient.fetchGetInstitutionResult(any())).thenAnswer(i -> mockGetCristinGetInstitutionResponseReader());
        when(mockCristinApiClient.queryInstitutions(any())).thenCallRealMethod();
        when(mockCristinApiClient.getInstitution(any(), any())).thenCallRealMethod();
        when(mockCristinApiClient.generateQueryInstitutionsUrl(any())).thenCallRealMethod();
        when(mockCristinApiClient.generateGetInstitutionUrl(any(), any())).thenCallRealMethod();

        Map<String, Object> event = new HashMap<>();
        Map<String, String> queryParams = new TreeMap<>();
        queryParams.put("name", QUERY_PARAM_NAME_NTNU);
        queryParams.put("language", QUERY_PARAM_LANGUAGE_NB);
        event.put("queryStringParameters", queryParams);

        FetchCristinInstitutions mockFetchCristinInstitutions = new FetchCristinInstitutions();
        mockFetchCristinInstitutions.setCristinApiClient(mockCristinApiClient);
        GatewayResponse response = mockFetchCristinInstitutions.handleRequest(event, null);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatusCode());
        assertEquals(response.getHeaders().get(HttpHeaders.CONTENT_TYPE), MediaType.APPLICATION_JSON);
    }

}
