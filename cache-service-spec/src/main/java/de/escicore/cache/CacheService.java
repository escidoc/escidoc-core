package de.escicore.cache;

import org.apache.camel.InOnly;

/**
 * Service to manage the cache for resources.
 *
 * @author <a href="mailto:mail@eduard-hildebrandt.de">Eduard Hildebrandt</a>
 */
public interface CacheService {

    /**
     * Recache the specified resources.
     *
     * @param recacheRequest a request to recache resources
     */
    @InOnly
    void recache(RecacheRequest recacheRequest);

}
