package de.escidoc.core.index.internal;

import de.escidoc.core.adm.service.interfaces.AdminHandlerInterface;
import de.escidoc.core.common.business.indexing.IndexingHandler;
import de.escidoc.core.common.exceptions.EscidocException;
import de.escidoc.core.common.util.service.UserContext;
import de.escidoc.core.index.IndexRequest;
import de.escidoc.core.index.IndexServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service("de.escidoc.core.index.internal.IndexServiceImpl")
public class IndexServiceImpl {

    @Autowired
    @Qualifier("common.business.indexing.IndexingHandler")
    private IndexingHandler indexingHandler;

    @Autowired
    @Qualifier("service.AdminHandler")
    private AdminHandlerInterface adminHandler;

    /**
     * Protected constructor to prevent instantiation outside of the Spring-context.
     */
    protected IndexServiceImpl() {
    }

    public void onNewIndexRequest(final IndexRequest indexRequest) throws IndexServiceException {
        final String indexName = indexRequest.getIndexName();
        final boolean allIndexes =
            indexName == null || indexName.trim().length() == 0 || "all".equalsIgnoreCase(indexName); // NON-NLS
        // TODO: Refactor this code. IndexingHandler should be moved from commons module to this module.
        try {
            if (UserContext.getSecurityContext() == null
                || UserContext.getSecurityContext().getAuthentication() == null) {
                UserContext.setUserContext("");
            }
            UserContext.runAsInternalUser();
            //If reindexer wrote in queue, decrease number of objects to index in AdminHandler
            if (indexRequest.getIsReindexerCaller()) {
                adminHandler.decreaseReindexStatus(indexRequest.getObjectType());
            }
            if (allIndexes) {
                indexingHandler.doIndexing(indexRequest.getResource(), indexRequest.getObjectType(), indexRequest
                    .getAction(), true, null);
            }
            else {
                indexingHandler.doIndexing(indexRequest.getResource(), indexRequest.getObjectType(), indexName,
                    indexRequest.getAction(), true, null);
            }
            //If reindexer wrote in queue, also index synchronous indexes
            if (indexRequest.getIsReindexerCaller()) {
                if (allIndexes) {
                    indexingHandler.doIndexing(indexRequest.getResource(), indexRequest.getObjectType(), indexRequest
                        .getAction(), false, null);
                }
                else {
                    indexingHandler.doIndexing(indexRequest.getResource(), indexRequest.getObjectType(), indexName,
                        indexRequest.getAction(), false, null);
                }
            }
        }
        catch (final EscidocException e) {
            throw new IndexServiceException("Error on indexing resource " + indexRequest.getResource() + ", index "
                + indexName + ", all indexes " + allIndexes + ", action " + indexRequest.getAction() + ':'
                + e.getMessage(), e);
        }
    }

    public void setIndexingHandler(final IndexingHandler indexingHandler) {
        this.indexingHandler = indexingHandler;
    }

    public void setAdminHandler(final AdminHandlerInterface adminHandler) {
        this.adminHandler = adminHandler;
    }

    @Override
    public String toString() {
        return "IndexServiceImpl{" + "indexingHandler=" + this.indexingHandler + '}';
    }
}
