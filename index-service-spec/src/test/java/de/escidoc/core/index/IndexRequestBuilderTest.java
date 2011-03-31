package de.escidoc.core.index;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * Unit test for {@link IndexRequestBuilder} and its implementations.
 */
public class IndexRequestBuilderTest {

    @Test
    public void testCreateIndexRequest() {
        final IndexRequest indexRequest = IndexRequestBuilder.createIndexRequest().withIndexName("indexName") // NON-NLS
        .withAction("action") // NON-NLS
            .withObjectType("objectType") // NON-NLS
            .withResource("resource") // NON-NLS
            .withData("<xml/>") // NON-NLS
            .build();
        assertEquals("wrong index name", "indexName", indexRequest.getIndexName()); // NON-NLS
        assertEquals("wrong action", "action", indexRequest.getAction()); // NON-NLS
        assertEquals("wrong object type", "objectType", indexRequest.getObjectType()); // NON-NLS
        assertEquals("wrong resource", "resource", indexRequest.getResource()); // NON-NLS
        assertEquals("wrong xml", "<xml/>", indexRequest.getData()); // NON-NLS
    }

}
