package org.esidoc.core.utils.xml;

import org.esidoc.core.utils.io.Stream;

/**
 * Represents a transfer object holding a output stream as content.
 *
 * @author <a href="mailto:mail@eduard-hildebrandt.de">Eduard Hildebrandt</a>
 */
public interface DatastreamHolder {

    String NAMESPACE = "http://www.escidoc.org/ignore";
    String ELEMENT_NAME = "datastream";

    Stream getDatastream();

    void setContent(final Stream stream );

}
