package no.unit.nva.cristin.institutions;


import no.unit.nva.cristin.institutions.model.Identifier;
import no.unit.nva.cristin.institutions.model.Institution;
import no.unit.nva.cristin.institutions.model.InstitutionBuilder;
import no.unit.nva.cristin.institutions.model.UnitObject;
import org.apache.http.client.utils.URIBuilder;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class CristinApiClient {

    public static final String EQUALS_SIGN = "=";
    public static final String AMPERSAND = "&";
    private final transient HttpExecutor httpExecutor;
    private static final String SCHEME = "https";
    private static final String CRISTIN_API_HOST = "api.cristin.no";
    private static final String API_VERSION = "v2";
    private static final String CRISTIN_API_INSTITUTIONS_PATH = "institutions";
    private static final String CRISTIN_API_UNITS_PATH = "units";
    private static final String PER_PAGE_KEY = "per_page";
    private static final String COUNTRY_KEY = "country";
    private static final String LANGUAGE_KEY = "lang";
    private static final String PARENT_UNIT_ID_KEY = "parent_unit_id";
    private static final String COUNTRY_VALUE = "NO";
    private static final String PER_PAGE_VALUE = "100000";

    /**
     * API client to connect to Cristin.
     */
    protected CristinApiClient() {
        httpExecutor = new HttpExecutorImpl();
    }

    /**
     * API client for testing.
     *
     * @param executor A mocked HttpExecutor
     */
    public CristinApiClient(HttpExecutor executor) {
        httpExecutor = executor;
    }

    /**
     * Returns a list of for customer institutions in Norway.
     *
     * @param language One of {en, nb, nn}
     * @return An array of institutions
     * @throws ExecutionException   In case the HTTP request failed
     * @throws InterruptedException In case the HTTP request failed to complete
     * @throws URISyntaxException   In case the URI passed was malformed
     */
    public Institution[] getInstitutions(String language) throws ExecutionException,
            InterruptedException, URISyntaxException {
        UnitObject[] unitObjects = httpExecutor.execute(generateInstitutionsUri(language));
        return Arrays.stream(unitObjects).filter(UnitObject::isCristinUserInstitution)
                .map(unit -> new InstitutionBuilder(new Identifier(Integer.parseInt(unit.getId()))).withName(unit.getName()).build())
                .toArray(Institution[]::new);
    }

    /**
     * This method gets a specific organizational unit, either institution, faculty, department or section,
     * including all parent units.
     *
     * @param identifier An identifier object
     * @param language   One of {en, nb, nn}
     * @return A single unit with subunits
     * @throws ExecutionException   In case the HTTP request failed
     * @throws InterruptedException In case the HTTP request failed to complete
     * @throws URISyntaxException   In case the URI passed was malformed
     */
    public UnitObject getUnit(Identifier identifier, String language) throws ExecutionException,
            InterruptedException, URISyntaxException {
        Identifier subunitId = identifier.isSectionIdentifierQualified() ? identifier : null;
        UnitObject[] allUnitObjects = Stream.of(httpExecutor.execute(
                generateInstitutionUri(identifier.getInstitutionIdentifier(), language)),
                httpExecutor.execute(generateSubunitsUri(identifier.getInstitutionIdentifier(), language)))
                .flatMap(Stream::of).toArray(UnitObject[]::new);
        Nester nester = new Nester(allUnitObjects, subunitId);
        return nester.getUnitObject();
    }

    private URI generateSubunitsUri(String id, String language) throws URISyntaxException {
        Map<String, String> queryParameters = new ConcurrentHashMap<>();
        queryParameters.put(LANGUAGE_KEY, language);
        queryParameters.put(PER_PAGE_KEY, PER_PAGE_VALUE);
        queryParameters.put(PARENT_UNIT_ID_KEY, id);
        return generateUri(queryParameters, CRISTIN_API_UNITS_PATH);
    }

    private URI generateInstitutionUri(String id, String language) throws URISyntaxException {
        Map<String, String> queryParameters = Collections.singletonMap(LANGUAGE_KEY, language);
        return generateUri(queryParameters, CRISTIN_API_UNITS_PATH, id);
    }

    protected URI generateInstitutionsUri(String language) throws URISyntaxException {
        Map<String, String> queryParameters = new ConcurrentHashMap<>();
        queryParameters.put(COUNTRY_KEY, COUNTRY_VALUE);
        queryParameters.put(PER_PAGE_KEY, PER_PAGE_VALUE);
        queryParameters.put(LANGUAGE_KEY, language);
        return generateUri(queryParameters, CRISTIN_API_INSTITUTIONS_PATH);
    }

    private URI generateUri(Map<String, String> queryParameters, String... pathSegment) throws URISyntaxException {
        List<String> pathSegments = new ArrayList<>();
        pathSegments.add(API_VERSION);
        pathSegments.addAll(Arrays.stream(pathSegment).collect(Collectors.toList()));

        String query = queryParameters.entrySet().stream()
                .map(entry -> entry.getKey() + EQUALS_SIGN + entry.getValue()).collect(Collectors.joining(AMPERSAND));

        return new URIBuilder()
                .setScheme(SCHEME)
                .setHost(CRISTIN_API_HOST)
                .setPathSegments(pathSegments)
                .setCustomQuery(query)
                .build();
    }
}
