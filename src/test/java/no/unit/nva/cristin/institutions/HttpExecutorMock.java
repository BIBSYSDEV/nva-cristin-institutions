package no.unit.nva.cristin.institutions;

import no.unit.nva.cristin.institutions.model.Identifier;
import no.unit.nva.cristin.institutions.model.UnitObject;
import no.unit.nva.cristin.institutions.util.UnitGenerator;

import java.net.URI;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HttpExecutorMock implements HttpExecutor {

    @Override
    public UnitObject[] execute(URI uri) {

        String language = null;
        String uriString = uri.toString();
        Pattern pattern = Pattern.compile("lang.([a-z]+)");
        Matcher matcher = pattern.matcher(uri.getQuery());
        if (matcher.find()) {
            language = matcher.group(1);
        }
        String identifier = null;
        Pattern identifierPattern = Pattern.compile("([0-9]+\\.[0-9]+\\.[0-9]+\\.[0-9]+)");
        Matcher identifierMatcher = identifierPattern.matcher(uri.toString());
        if (identifierMatcher.find()) {
            identifier = identifierMatcher.group(1);
        }

        if (uriString.contains("country")) {
            return UnitGenerator.generateMockUnits(20, language);
        } else if (uri.getQuery().matches(".*[0-9]+\\.0\\.0\\.0.*")) {
            return UnitGenerator.generateMockUnits(new Identifier(identifier), language);
        } else if (uriString.matches(".*/[0-9]+\\.0\\.0\\.0.*")) {
            return new UnitObject[]{
                    UnitGenerator.generateMockInstitution(new Identifier(identifier), 5, 5, 2, language)};
        } else if (uriString.matches(".*[0-9]+\\.[0-9]+\\.[0-9]+\\.[0-9]+.*")) {
            return UnitGenerator.generateMockUnits(new Identifier(identifier), language);
        } else {
            return null;
        }
    }
}
