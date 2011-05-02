package de.escidoc.core.index;

import java.util.Date;

/**
 * Represents an index request.
 */
public interface IndexRequest {

    /**
     * Returns the request identifier.
     *
     * @return the request identifier
     */
    String getRequestId();

    /**
     * Returns the request creation timestamp.
     *
     * @return the request creation timestamp
     */
    Date getCreationTimestamp();

    /**
     * Returns the indexing action.
     *
     * @return the indexing action
     */
    String getAction();

    /**
     * Returns the index name.
     *
     * @return the index name
     */
    String getIndexName();

    /**
     * Returns the resource identifier.
     *
     * @return the resource identifier
     */
    String getResource();

    /**
     * Return the object type.
     *
     * @return the object type
     */
    String getObjectType();

    /**
     * Returns the data to index.
     *
     * @return the data to index
     */
    String getData();

    /**
     * Returns the isReindexerCaller-Property.
     *
     * @return the isReindexerCaller-Property.
     */
    boolean getIsReindexerCaller();

}
