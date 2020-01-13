package no.unit.nva.cristin.institutions;

import org.junit.Test;

import static org.junit.Assert.assertNull;

public class ConfigTest {

    @Test
    public void testCorsHeaderNotSet() {
        final Config config = Config.getInstance();
        config.setCorsHeader(null);
        final String corsHeader = config.getCorsHeader();
        assertNull(corsHeader);
    }

}
