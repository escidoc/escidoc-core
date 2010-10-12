package de.escidoc.core.purge;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * Test for {@link PurgeRequestBuilder}.
 *
 * @author <a href="mailto:mail@eduard-hildebrandt.de">Eduard Hildebrandt</a>
 */
public class PurgeRequestBuilderTest {

    @Test
    public void testPurgeRequest() {
        final PurgeRequest purgeRequest = PurgeRequestBuilder.createPurgeRequest()
                .withResourceId("test") // NON-NLS
                .build();
        assertEquals("wrong resource id", "test", purgeRequest.getResourceId())); // NON-NLS
    }
}
