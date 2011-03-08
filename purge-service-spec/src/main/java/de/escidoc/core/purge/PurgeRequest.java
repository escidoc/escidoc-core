package de.escidoc.core.purge;

import java.util.Date;

/**
 * Represents a request to purge data from repository.
 */
public interface PurgeRequest {

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
     * Returns the resource identifier.
     *
     * @return the resource identifier
     */
    String getResourceId();

}
