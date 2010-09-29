package de.escicore.index;

import org.apache.camel.InOnly;

/**
 * Service to index data for later search.
 *
 * @author <a href="mailto:mail@eduard-hildebrandt.de">Eduard Hildebrandt</a>
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
