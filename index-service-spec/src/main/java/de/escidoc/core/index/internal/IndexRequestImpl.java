package de.escidoc.core.index.internal;

import de.escidoc.core.index.IndexRequest;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.util.Date;
import java.util.UUID;

/**
 * Default implementation of {@link IndexRequest}.
 */
@XmlRootElement(name = "IndexRequest", namespace = "http://www.escidoc.de/schemas/index-service/1.0/")
@XmlType(name = "IndexRequest", namespace = "http://www.escidoc.de/schemas/index-service/1.0/")
public class IndexRequestImpl implements IndexRequest {

    private final String requestId = UUID.randomUUID().toString();

    private final Date creationTimestamp = new Date();

    private String action;

    private String indexName;

    private String resource;

    private String objectType;

    private String data;

    private boolean isReindexerCaller;

    @Override
    public String getRequestId() {
        return this.requestId;
    }

    @Override
    public Date getCreationTimestamp() {
        return new Date(this.creationTimestamp.getTime());
    }

    @Override
    public String getAction() {
        return this.action;
    }

    /**
     * Sets the indexing action.
     *
     * @param action the indexing action
     */
    public void setAction(final String action) {
        this.action = action;
    }

    @Override
    public String getIndexName() {
        return this.indexName;
    }

    /**
     * Sets the index name.
     *
     * @param indexName the index name
     */
    public void setIndexName(final String indexName) {
        this.indexName = indexName;
    }

    @Override
    public String getResource() {
        return this.resource;
    }

    /**
     * Sets the resource identifier.
     *
     * @param resource the resource identifier
     */
    public void setResource(final String resource) {
        this.resource = resource;
    }

    @Override
    public String getObjectType() {
        return this.objectType;
    }

    /**
     * Sets the object type.
     *
     * @param objectType the object type
     */
    public void setObjectType(final String objectType) {
        this.objectType = objectType;
    }

    @Override
    public String getData() {
        return this.data;
    }

    /**
     * Sets the data to index.
     *
     * @param data the data to index
     */
    public void setData(final String data) {
        this.data = data;
    }

    @Override
    public boolean getIsReindexerCaller() {
        return this.isReindexerCaller;
    }

    /**
     * Sets the data to index.
     *
     * @param isReindexerCaller boolean if message was sent by reindexer
     */
    public void setIsReindexerCaller(final boolean isReindexerCaller) {
        this.isReindexerCaller = isReindexerCaller;
    }

    @Override
    public String toString() {
        return "IndexRequestImpl{" + "requestId='" + this.requestId + '\'' + ", creationTimestamp="
            + this.creationTimestamp + ", action='" + this.action + '\'' + ", indexName='" + this.indexName + '\''
            + ", resource='" + this.resource + '\'' + ", objectType='" + this.objectType + '\'' + ", data='"
            + this.data + '\'' + ", isReindexerCaller='" + this.isReindexerCaller + '\'' + '}';
    }
}
