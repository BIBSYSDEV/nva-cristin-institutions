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
public class FetchCristinUnitTest {

    private static final String CRISTIN_GET_UNIT_WITH_SUBUNITS_FIRST_RESPONSE_JSON_FILE = "/cristinGetUnitWithSubunitsFirstResponse.json";
    private static final String CRISTIN_GET_UNIT_WITH_SUBUNITS_SECOND_RESPONSE_JSON_FILE = "/cristinGetUnitWithSubunitsSecondResponse.json";
    private static final String CRISTIN_GET_UNIT_WITHOUT_SUBUNITS_RESPONSE_JSON_FILE = "/cristinGetUnitWithoutSubunitsResponse.json";

    private static final String QUERY_PARAM_LANGUAGE_NB = "nb";
    private static final String PATH_PARAM_ID_NTNU = "194.0.0.0";

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
        when(mockCristinApiClient.fetchGetUnitResult(any())).thenReturn(mockGetCristinUnitWithSubunitsFirstResponseReader());
        when(mockCristinApiClient.getUnit(any(), any())).thenCallRealMethod();
        when(mockCristinApiClient.generateGetUnitUrl(any(), any())).thenCallRealMethod();

        Map<String, Object> event = new HashMap<>();

        Map<String, String> pathParams = new TreeMap<>();
        pathParams.put("id", PATH_PARAM_ID_NTNU);
        event.put("pathParameters", pathParams);

        Map<String, String> queryParams = new TreeMap<>();
        queryParams.put("language", QUERY_PARAM_LANGUAGE_NB);
        event.put("queryStringParameters", queryParams);

        FetchCristinUnit mockFetchCristinUnit = new FetchCristinUnit();
        mockFetchCristinUnit.setCristinApiClient(mockCristinApiClient);
        GatewayResponse response = mockFetchCristinUnit.handleRequest(event, null);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatusCode());
        assertEquals(response.getHeaders().get(HttpHeaders.CONTENT_TYPE), MediaType.APPLICATION_JSON);
    }

}
