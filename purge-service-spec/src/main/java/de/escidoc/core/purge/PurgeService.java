package de.escidoc.core.purge;

import org.apache.camel.InOnly;

/**
 * Service to purge resources from repository.
 */
public interface PurgeService {

    /**
     * Purge a resource from repository.
     *
     * @param purgeRequest the request to purge data from repository
     */
    @InOnly
    void purge(PurgeRequest purgeRequest);

}
