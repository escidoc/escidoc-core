package de.escidoc.core.cache;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * Test for {@link RecacheRequestBuilder}.
 *
 * @author <a href="mailto:mail@eduard-hildebrandt.de">Eduard Hildebrandt</a>
 */
public class RecacheRequestBuilderTest {

    @Test
    public void testCreateRecacheRequest() {
        final RecacheRequest recacheRequest = RecacheRequestBuilder.createRecacheRequest()
                .withResourceId("test") // NON-NLS
                .withResourceType("resourceType") // NON-NLS
                .build();
        assertEquals("wrong resource id", "test", recacheRequest.getResourceId()); // NON-NLS
        assertEquals("wrong resource type", "resourceType", recacheRequest.getResourceType()); // NON-NLS
    }

}
