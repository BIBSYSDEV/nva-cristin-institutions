package no.unit.nva.cristin.institutions;


import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import org.apache.http.client.utils.URIBuilder;

import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class CristinApiClient {

    private static final String HTTPS = "https";
    private static final String CRISTIN_API_HOST = "api.cristin.no";
    private static final String CRISTIN_API_INSTITUTIONS_PATH = "/v2/institutions/";
    private static final String CRISTIN_API_UNITS_PATH = "/v2/units/";

    protected List<Institution> queryInstitutions(Map<String, String> parameters) throws
            IOException, URISyntaxException {
        URL url = generateQueryInstitutionsUrl(parameters);
        try (InputStreamReader streamReader = fetchQueryInstitutionsResults(url)) {
            return fromJson(streamReader,
                    new TypeToken<ArrayList<Institution>>(){}.getType());
        }
    }

    protected Institution getInstitution(String id, String language) throws IOException, URISyntaxException {
        URL url = generateGetInstitutionUrl(id, language);
        try (InputStreamReader streamReader = fetchGetInstitutionResult(url)) {
            return fromJson(streamReader,
                    new TypeToken<ArrayList<Institution>>(){}.getType());
        }
    }

    protected Unit getUnit(String id, String language) throws IOException, URISyntaxException {
        URL url = generateGetUnitUrl(id, language);
        try (InputStreamReader streamReader = fetchGetUnitResult(url)) {
            return fromJson(streamReader, new TypeToken<Unit>(){}.getType());
        }
    }

    protected InputStreamReader fetchQueryInstitutionsResults(URL url) throws IOException {
        return new InputStreamReader(url.openStream());
    }

    protected InputStreamReader fetchGetInstitutionResult(URL url) throws IOException {
        return new InputStreamReader(url.openStream());
    }

    protected InputStreamReader fetchGetUnitResult(URL url) throws IOException {
        return new InputStreamReader(url.openStream());
    }

    protected URL generateQueryInstitutionsUrl(Map<String, String> parameters) throws MalformedURLException,
            URISyntaxException {
        URIBuilder uri = new URIBuilder()
                .setScheme(HTTPS)
                .setHost(CRISTIN_API_HOST)
                .setPath(CRISTIN_API_INSTITUTIONS_PATH);
        if (parameters != null) {
            parameters.keySet().forEach(s -> uri.addParameter(s, parameters.get(s)));
        }
        return uri.build().toURL();
    }

    protected URL generateGetInstitutionUrl(String id, String language) throws
            MalformedURLException, URISyntaxException {
        URI uri = new URIBuilder()
                .setScheme(HTTPS)
                .setHost(CRISTIN_API_HOST)
                .setPath(CRISTIN_API_INSTITUTIONS_PATH + id)
                .addParameter("lang", language)
                .build();
        return uri.toURL();
    }

    protected URL generateGetUnitUrl(String id, String language) throws MalformedURLException, URISyntaxException {
        URI uri = new URIBuilder()
                .setScheme(HTTPS)
                .setHost(CRISTIN_API_HOST)
                .setPath(CRISTIN_API_UNITS_PATH + id)
                .addParameter("lang", language)
                .build();
        return uri.toURL();
    }

    protected static <T> T fromJson(InputStreamReader reader, Type type) throws IOException {
        try {
            return new Gson().fromJson(reader, type);
        } catch (JsonSyntaxException e) {
            final String s = e.getMessage() + " " + reader;
            throw new IOException(s, e);
        }
    }

}
