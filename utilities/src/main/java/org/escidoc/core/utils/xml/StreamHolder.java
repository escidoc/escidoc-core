package org.esidoc.core.utils.xml;

import org.esidoc.core.utils.io.Stream;

/**
 * Represents a transfer object holding a output stream as content.
 *
 * @author <a href="mailto:mail@eduard-hildebrandt.de">Eduard Hildebrandt</a>
 */
public interface StreamHolder {

    String NAMESPACE = "http://www.escidoc.org/ignore";
    String ELEMENT_NAME = "stream";

    Stream getStream();

    void setContent(final Stream stream );

}
