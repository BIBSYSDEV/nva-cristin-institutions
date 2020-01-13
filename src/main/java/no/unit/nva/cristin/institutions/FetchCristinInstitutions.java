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
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Handler for requests to Lambda function.
 */
public class FetchCristinInstitutions implements RequestHandler<Map<String, Object>, GatewayResponse> {

    private static final String QUERY_STRING_PARAMETERS_KEY = "queryStringParameters";
    private static final String NAME_IS_NULL = "Parameter 'name' is mandatory";
    private static final String NAME_ILLEGAL_CHARACTERS = "Parameter 'name' may only contain alphanumeric " +
            "characters, dash and whitespace";
    private static final String LANGUAGE_INVALID = "Parameter 'language' has invalid value";
    private static final String ERROR_KEY = "error";
    private static final String DEFAULT_LANGUAGE_CODE = "nb";
    private static final List<String> VALID_LANGUAGE_CODES = Arrays.asList("nb", "en");

    private transient CristinApiClient cristinApiClient;

    public FetchCristinInstitutions() {
        cristinApiClient = new CristinApiClient();
    }

    public FetchCristinInstitutions(CristinApiClient cristinApiClient) {
        this.cristinApiClient = cristinApiClient;
    }

    public void setCristinApiClient(CristinApiClient cristinApiClient) {
        this.cristinApiClient = cristinApiClient;
    }

    @Override
    @SuppressWarnings("unchecked")
    public GatewayResponse handleRequest(Map<String, Object> input, Context context) {

        GatewayResponse gatewayResponse = new GatewayResponse();
        Map<String, String> queryStringParameters = (Map<String, String>) input.get(QUERY_STRING_PARAMETERS_KEY);
        String name = queryStringParameters.getOrDefault("name", "");
        if (name.isEmpty()) {
            gatewayResponse.setStatusCode(Response.Status.BAD_REQUEST.getStatusCode());
            gatewayResponse.setBody(getErrorAsJson(NAME_IS_NULL));
            return gatewayResponse;
        }
        if (!isValidName(name)) {
            gatewayResponse.setStatusCode(Response.Status.BAD_REQUEST.getStatusCode());
            gatewayResponse.setBody(getErrorAsJson(NAME_ILLEGAL_CHARACTERS));
            return gatewayResponse;
        }

        String language = queryStringParameters.getOrDefault("language", DEFAULT_LANGUAGE_CODE);
        if (!VALID_LANGUAGE_CODES.contains(language)) {
            gatewayResponse.setStatusCode(Response.Status.BAD_REQUEST.getStatusCode());
            gatewayResponse.setBody(getErrorAsJson(LANGUAGE_INVALID));
            return gatewayResponse;
        }

        try {

            Map<String, String> parameters = new ConcurrentHashMap<>();
            parameters.put("name", name);
            parameters.put("lang", language);
            parameters.put("page", "1");
            parameters.put("per_page", "5");

            List<Institution> institutions = cristinApiClient.queryInstitutions(parameters);
            List<InstitutionPresentation> institutionPresentations = institutions.stream()
                    .map(institution -> {
                        try {
                            return cristinApiClient.getInstitution(institution.cristinInstitutionId, language);
                        } catch (IOException | URISyntaxException e) {
                            System.out.println("Error fetching cristin institution with id: " + institution.cristinInstitutionId);
                        }
                        return institution;
                    })
                    .map(this::asInstitutionPresentation)
                    .collect(Collectors.toList());

            Type institutionListType = new TypeToken<ArrayList<InstitutionPresentation>>() {
            }.getType();

            gatewayResponse.setStatusCode(Response.Status.OK.getStatusCode());
            gatewayResponse.setBody(new Gson().toJson(institutionPresentations, institutionListType));

        } catch (IOException | URISyntaxException e) {
            gatewayResponse.setStatusCode(Response.Status.SERVICE_UNAVAILABLE.getStatusCode());
            gatewayResponse.setBody(getErrorAsJson(e.getMessage()));
        }

        return gatewayResponse;
    }

    private InstitutionPresentation asInstitutionPresentation(Institution institution) {
        InstitutionPresentation institutionPresentation = new InstitutionPresentation();
        institutionPresentation.cristinInstitutionId = institution.cristinInstitutionId;

        Optional.ofNullable(institution.institutionName).orElse(new TreeMap<String, String>() {
        }).forEach((key, value) -> {
            NamePresentation namePresentation = new NamePresentation();
            namePresentation.language = key;
            namePresentation.name = value;
            institutionPresentation.institutionNames.add(namePresentation);
        });

        institutionPresentation.acronym = Optional.ofNullable(institution.acronym).orElse("");
        institutionPresentation.country = Optional.ofNullable(institution.country).orElse("");

        if (Optional.ofNullable(institution.correspondingUnit).isPresent()) {
            institutionPresentation.cristinUnitId = institution.correspondingUnit.cristinUnitId;
        }

        return institutionPresentation;
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

    private boolean isValidName(String str) {
        char[] charArray = str.toCharArray();
        for (char c : charArray) {
            if (!Character.isWhitespace(c) && !Character.isLetterOrDigit(c) && c != '-') {
                return false;
            }
        }
        return true;
    }

}
