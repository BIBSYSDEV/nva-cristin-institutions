package no.unit.nva.cristin.institutions;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Handler for requests to Lambda function.
 */
public class FetchCristinUnit implements RequestHandler<Map<String, Object>, GatewayResponse> {

    private static final String QUERY_STRING_PARAMETERS_KEY = "queryStringParameters";
    private static final String PATH_PARAMETERS_KEY = "pathParameters";
    private static final String ID_KEY = "id";
    private static final String LANGUAGE_KEY = "language";

    private static final String ID_IS_NULL = "Parameter 'id' is mandatory";
    private static final String LANGUAGE_INVALID = "Parameter 'language' has invalid value";

    private static final String EMPTY_STRING = "";
    private static final String DEFAULT_LANGUAGE_CODE = "nb";
    private static final List<String> VALID_LANGUAGE_CODES = Arrays.asList("nb", "en");

    private transient CristinApiClient cristinApiClient;
    private final transient PresentationConverter presentationConverter = new PresentationConverter();

    public FetchCristinUnit() {
        cristinApiClient = new CristinApiClient();
    }

    public FetchCristinUnit(CristinApiClient cristinApiClient) {
        this.cristinApiClient = cristinApiClient;
    }

    public void setCristinApiClient(CristinApiClient cristinApiClient) {
        this.cristinApiClient = cristinApiClient;
    }

    @Override
    @SuppressWarnings("unchecked")
    public GatewayResponse handleRequest(Map<String, Object> input, Context context) {

        GatewayResponse gatewayResponse = new GatewayResponse();
        try {
            this.checkParameters(input);
        } catch (RuntimeException e) {
            gatewayResponse.setErrorBody(e.getMessage());
            gatewayResponse.setStatusCode(Response.Status.BAD_REQUEST.getStatusCode());
            return gatewayResponse;
        }

        Map<String, String> pathParameters = (Map<String, String>) input.get(PATH_PARAMETERS_KEY);
        String id = pathParameters.get(ID_KEY);
        Map<String, String> queryStringParameters = Optional.ofNullable((Map<String, String>) input
                .get(QUERY_STRING_PARAMETERS_KEY)).orElse(new ConcurrentHashMap<>());
        String language = queryStringParameters.getOrDefault(LANGUAGE_KEY, DEFAULT_LANGUAGE_CODE);

        try {

            Unit unit = cristinApiClient.getUnit(id, language);
            UnitPresentation unitPresentations = presentationConverter.asUnitPresentation(unit);
            gatewayResponse.setStatusCode(Response.Status.OK.getStatusCode());
            gatewayResponse.setBody(new Gson().toJson(unitPresentations,
                    new TypeToken<UnitPresentation>(){}.getType()));

        } catch (IOException | URISyntaxException e) {
            gatewayResponse.setStatusCode(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
            gatewayResponse.setErrorBody(e.getMessage());
        }

        return gatewayResponse;
    }

    @SuppressWarnings("unchecked")
    private void checkParameters(Map<String, Object> input) {
        Map<String, String> pathParameters = (Map<String, String>) input.get(PATH_PARAMETERS_KEY);
        String id = pathParameters.getOrDefault(ID_KEY, EMPTY_STRING);
        if (id.isEmpty()) {
            throw new RuntimeException(ID_IS_NULL);

        }
        Map<String, String> queryStringParameters = Optional.ofNullable((Map<String, String>) input
                .get(QUERY_STRING_PARAMETERS_KEY)).orElse(new ConcurrentHashMap<>());
        String language = queryStringParameters.getOrDefault(LANGUAGE_KEY, DEFAULT_LANGUAGE_CODE);
        if (!VALID_LANGUAGE_CODES.contains(language)) {
            throw new RuntimeException(LANGUAGE_INVALID);
        }
    }


}
