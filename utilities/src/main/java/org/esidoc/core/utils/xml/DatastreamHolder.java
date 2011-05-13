package org.esidoc.core.utils.xml;

import org.esidoc.core.utils.io.Datastream;

/**
 * Represents a transfer object holding a output stream as content.
 *
 * @author <a href="mailto:mail@eduard-hildebrandt.de">Eduard Hildebrandt</a>
 */
public interface DatastreamHolder {

    public final static String NAMESPACE = "http://www.escidoc.org/ignore";
    public final static String ELEMENT_NAME = "datastream";

    Datastream getDatastream();

    void setContent(final Datastream datastream);

}
