package de.escidoc.core.cache;

import java.util.Date;

/**
 * Represent a request to recache resources.
 *
 * @author <a href="mailto:mail@eduard-hildebrandt.de">Eduard Hildebrandt</a>
 */
public interface RecacheRequest {

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
     * Returns the recourse identifier.
     *
     * @return the recourse identifier
     */
    String getResourceId();

    /**
     * Returns the resource type.
     *
     * @return the resource type.
     */
    String getResourceType();
}
