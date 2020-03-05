package no.unit.nva.cristin.institutions;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Handler for requests to Lambda function.
 */
public class FetchCristinInstitutions implements RequestHandler<Map<String, Object>, GatewayResponse> {

    private static final String QUERY_STRING_PARAMETERS_KEY = "queryStringParameters";
    private static final String NAME_KEY = "name";
    private static final String LANGUAGE_KEY = "language";

    protected static final String NAME_IS_NULL = "Parameter 'name' is mandatory";
    protected static final String NAME_ILLEGAL_CHARACTERS = "Parameter 'name' may only contain alphanumeric "
            + "characters, dash and whitespace";
    protected static final String LANGUAGE_INVALID = "Parameter 'language' has invalid value";

    private static final String EMPTY_STRING = "";
    private static final char CHARACTER_DASH = '-';
    private static final String DEFAULT_LANGUAGE_CODE = "nb";
    private static final List<String> VALID_LANGUAGE_CODES = Arrays.asList("nb", "en");

    private static final String CRISTIN_QUERY_PARAMETER_NAME_KEY = "name";
    private static final String CRISTIN_QUERY_PARAMETER_LANGUAGE_KEY = "lang";
    private static final String CRISTIN_QUERY_PARAMETER_PAGE_KEY = "page";
    private static final String CRISTIN_QUERY_PARAMETER_PAGE_VALUE = "1";
    private static final String CRISTIN_QUERY_PARAMETER_PER_PAGE_KEY = "per_page";
    private static final String CRISTIN_QUERY_PARAMETER_PER_PAGE_VALUE = "5";

    private transient CristinApiClient cristinApiClient;
    private final transient PresentationConverter presentationConverter = new PresentationConverter();

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
        try {
            this.checkParameters(input);
        } catch (RuntimeException e) {
            gatewayResponse.setErrorBody(e.getMessage());
            gatewayResponse.setStatusCode(Response.Status.BAD_REQUEST.getStatusCode());
            return gatewayResponse;
        }

        Map<String, String> queryStringParameters = (Map<String, String>) input.get(QUERY_STRING_PARAMETERS_KEY);
        String name = queryStringParameters.get(NAME_KEY);
        String language = queryStringParameters.getOrDefault(LANGUAGE_KEY, DEFAULT_LANGUAGE_CODE);

        try {
            Map<String, String> cristinQueryParameters = createCristinQueryParameters(name, language);
            List<Institution> institutions = cristinApiClient.queryInstitutions(cristinQueryParameters);
            List<InstitutionPresentation> institutionPresentations = institutions.stream()
                    .map(institution -> {
                        try {
                            return cristinApiClient.getInstitution(institution.cristinInstitutionId, language);
                        } catch (IOException | URISyntaxException e) {
                            System.out.println("Error fetching cristin institution with id: "
                                    + institution.cristinInstitutionId);
                        }
                        return institution;
                    })
                    .map(presentationConverter::asInstitutionPresentation)
                    .collect(Collectors.toList());

            gatewayResponse.setStatusCode(Response.Status.OK.getStatusCode());
            gatewayResponse.setBody(new Gson().toJson(institutionPresentations,
                    new TypeToken<ArrayList<InstitutionPresentation>>(){}.getType()));

        } catch (IOException | URISyntaxException e) {
            gatewayResponse.setStatusCode(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
            gatewayResponse.setErrorBody(e.getMessage());
        }

        return gatewayResponse;
    }


    @SuppressWarnings("unchecked")
    private void checkParameters(Map<String, Object> input) {
        Map<String, String> queryStringParameters = (Map<String, String>) input.get(QUERY_STRING_PARAMETERS_KEY);
        String name = queryStringParameters.getOrDefault(NAME_KEY, EMPTY_STRING);
        if (name.isEmpty()) {
            throw new RuntimeException(NAME_IS_NULL);
        }
        if (!isValidName(name)) {
            throw new RuntimeException(NAME_ILLEGAL_CHARACTERS);

        }

        String language = queryStringParameters.getOrDefault(LANGUAGE_KEY, DEFAULT_LANGUAGE_CODE);
        if (!VALID_LANGUAGE_CODES.contains(language)) {
            throw new RuntimeException(LANGUAGE_INVALID);
        }
    }

    private boolean isValidName(String str) {
        char[] charArray = str.toCharArray();
        for (char c : charArray) {
            if (!isValidCharacter(c)) {
                return false;
            }
        }
        return true;
    }

    private boolean isValidCharacter(char c) {
        return Character.isWhitespace(c) || Character.isLetterOrDigit(c) || c == CHARACTER_DASH;
    }

    private Map<String, String> createCristinQueryParameters(String name, String language) {
        Map<String, String> queryParameters = new ConcurrentHashMap<>();
        queryParameters.put(CRISTIN_QUERY_PARAMETER_NAME_KEY, name);
        queryParameters.put(CRISTIN_QUERY_PARAMETER_LANGUAGE_KEY, language);
        queryParameters.put(CRISTIN_QUERY_PARAMETER_PAGE_KEY, CRISTIN_QUERY_PARAMETER_PAGE_VALUE);
        queryParameters.put(CRISTIN_QUERY_PARAMETER_PER_PAGE_KEY, CRISTIN_QUERY_PARAMETER_PER_PAGE_VALUE);
        return queryParameters;
    }

}
