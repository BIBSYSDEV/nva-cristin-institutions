package no.unit.nva.cristin.institutions;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import javax.ws.rs.core.Response;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

/**
 * Handler for requests to Lambda function.
 */
public class FetchCristinUnit implements RequestHandler<Map<String, Object>, GatewayResponse> {

    private static final String QUERY_STRING_PARAMETERS_KEY = "queryStringParameters";
    private static final String PATH_PARAMETERS_KEY = "pathParameters";
    private static final String ID_IS_NULL = "Parameter 'id' is mandatory";
    private static final String LANGUAGE_INVALID = "Parameter 'language' has invalid value";
    private static final String ERROR_KEY = "error";
    private static final String DEFAULT_LANGUAGE_CODE = "nb";
    private static final List<String> VALID_LANGUAGE_CODES = Arrays.asList("nb", "en");

    private transient CristinApiClient cristinApiClient;

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

        Map<String, String> pathParameters = (Map<String, String>) input.get(PATH_PARAMETERS_KEY);
        String id = pathParameters.getOrDefault("id", "");
        if (id.isEmpty()) {
            gatewayResponse.setStatusCode(Response.Status.BAD_REQUEST.getStatusCode());
            gatewayResponse.setBody(getErrorAsJson(ID_IS_NULL));
            return gatewayResponse;
        }

        Map<String, String> queryStringParameters = Optional.ofNullable((Map<String, String>) input
                .get(QUERY_STRING_PARAMETERS_KEY)).orElse(new TreeMap<>());
        String language = queryStringParameters.getOrDefault("language", DEFAULT_LANGUAGE_CODE);
        if (!VALID_LANGUAGE_CODES.contains(language)) {
            gatewayResponse.setStatusCode(Response.Status.BAD_REQUEST.getStatusCode());
            gatewayResponse.setBody(getErrorAsJson(LANGUAGE_INVALID));
            return gatewayResponse;
        }

        try {

            Unit unit = cristinApiClient.getUnit(id, language);
            List<UnitPresentation> subunitPresentations = asSubunits(unit);
            Type unitListType = new TypeToken<ArrayList<UnitPresentation>>() {
            }.getType();
            gatewayResponse.setStatusCode(Response.Status.OK.getStatusCode());
            gatewayResponse.setBody(new Gson().toJson(subunitPresentations, unitListType));

        } catch (IOException | URISyntaxException e) {
            gatewayResponse.setStatusCode(Response.Status.SERVICE_UNAVAILABLE.getStatusCode());
            gatewayResponse.setBody(getErrorAsJson(e.getMessage()));
        }

        return gatewayResponse;
    }

    private List<UnitPresentation> asSubunits(Unit unit) {

        List<UnitPresentation> unitPresentations = new ArrayList<>();

        Optional.ofNullable(unit.subunits).orElse(new ArrayList<Unit>() {
        }).forEach(subunit -> {
            UnitPresentation unitPresentation = new UnitPresentation();

            unitPresentation.cristinUnitId = subunit.cristinUnitId;

            Optional.ofNullable(subunit.unitName).orElse(new TreeMap<>())
                    .forEach((language, name) -> {
                        NamePresentation namePresentation = new NamePresentation();
                        namePresentation.language = language;
                        namePresentation.name = name;
                        unitPresentation.unitNames.add(namePresentation);
                    });

            unitPresentations.add(unitPresentation);
        });

        return unitPresentations;
    }


    /**
     * Get error message as a json string.
     *
     * @param message message from exception
     * @return String containing an error message as json
     */
    private String getErrorAsJson(String message) {
        JsonObject json = new JsonObject();
        json.addProperty(ERROR_KEY, message);
        return json.toString();
    }


}
