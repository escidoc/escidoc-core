package de.escicore.index.internal;

import de.escicore.index.IndexRequest;
import de.escicore.index.IndexServiceException;
import de.escidoc.core.common.business.indexing.IndexingHandler;
import de.escidoc.core.common.exceptions.system.SystemException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class IndexServiceImpl {

    private static final Log LOG = LogFactory.getLog(IndexServiceImpl.class);

    private IndexingHandler indexingHandler;

    public void onNewIndexRequest(final IndexRequest indexRequest) throws IndexServiceException {
        final String indexName = indexRequest.getIndexName();
        final boolean allIndexes = indexName == null
                || indexName.trim().length() == 0
                || "all".equalsIgnoreCase(indexName); // NON-NLS
        // TODO: Refactor this code. IndexingHandler should be moved from commons module to this module.
        try {
            if (allIndexes) {
                indexingHandler.doIndexing(indexRequest.getResource(), indexRequest.getObjectType(), indexRequest.getAction(), false, null);
            } else {
                indexingHandler.doIndexing(indexRequest.getResource(), indexRequest.getObjectType(), indexName, indexRequest.getAction(), false, null);
            }
        } catch (final SystemException e) {
            final String errorMessage = "Error on indexing resource."; // NON-NLS
            LOG.error(errorMessage, e);
            throw new IndexServiceException(errorMessage, e);
        }
    }

    public void setIndexingHandler(final IndexingHandler indexingHandler) {
        this.indexingHandler = indexingHandler;
    }

    @Override
    public String toString() {
        return "IndexServiceImpl{" +
                "indexingHandler=" + indexingHandler +
                '}';
    }
}
