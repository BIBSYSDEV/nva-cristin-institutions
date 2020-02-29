package no.unit.nva.cristin.institutions;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import no.unit.nva.cristin.institutions.model.UnitObject;
import no.unit.nva.cristin.institutions.utils.Adapter;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.ExecutionException;


public class HttpExecutorImpl implements HttpExecutor {
    private final transient HttpClient httpClient;

    /**
     * Creates the default HttpClient.
     */
    public HttpExecutorImpl() {
        this.httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .followRedirects(HttpClient.Redirect.ALWAYS)
                .connectTimeout(Duration.ofSeconds(30))
                .build();
    }

    /**
     * Allows input of an HttpClient for mocking.
     * @param httpClient A mocked HttpClient
     */
    public HttpExecutorImpl(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @Override
    public UnitObject[] execute(URI uri) throws ExecutionException, InterruptedException {

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(uri)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
                .GET()
                .build();

        Type type = new TypeToken<UnitObject[]>() {}.getType();
        Adapter<UnitObject> objectToListAdapter = new Adapter<>(UnitObject.class);

        Gson gson = new GsonBuilder().registerTypeAdapter(type, objectToListAdapter).setPrettyPrinting().create();

        return this.httpClient.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApply(content -> gson.fromJson(content, UnitObject[].class)).get();
    }
}
