package no.unit.nva.cristin.institutions;

import no.unit.nva.cristin.institutions.model.UnitObject;

import java.net.URI;
import java.util.concurrent.ExecutionException;

public interface HttpExecutor {

    UnitObject[] execute(URI uri) throws ExecutionException, InterruptedException;

}
