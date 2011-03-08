package de.escidoc.core.index;

import org.apache.camel.InOnly;

/**
 * Service to index data for later search.
 */
public interface IndexService {

    /**
     * Index data for later search.
     *
     * @param indexRequest request with parameters to index data
     */
    @InOnly
    void index(IndexRequest indexRequest);

}
