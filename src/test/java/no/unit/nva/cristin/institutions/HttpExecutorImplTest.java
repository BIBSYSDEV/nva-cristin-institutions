package no.unit.nva.cristin.institutions;

import no.unit.nva.cristin.institutions.model.Identifier;
import no.unit.nva.cristin.institutions.model.UnitObject;
import no.unit.nva.cristin.institutions.model.UnitObjectBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HttpExecutorImplTest {

    @Mock
    private CompletableFuture<HttpResponse<String>> completableFuture;

    @Mock
    private HttpClient httpClient;

    @Mock
    private HttpResponse<String> httpResponse;

    @Test
    void execute() throws ExecutionException, InterruptedException {
        when(httpResponse.body()).thenReturn("{\"id\": \"123.0.0.0\"}");
        completableFuture = CompletableFuture.completedFuture(httpResponse);
        doReturn(completableFuture).when(httpClient).sendAsync(any(), eq(HttpResponse.BodyHandlers.ofString()));

        HttpExecutor httpExecutor = new HttpExecutorImpl(httpClient);
        UnitObject unitObject = new UnitObjectBuilder(new Identifier(123)).build();
        assertEquals(unitObject.getId(), httpExecutor.execute(URI.create("https://example.org"))[0].getId());
    }
}