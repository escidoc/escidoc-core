package org.escidoc.core.util.xml.internal;

/**
 * @author <a href="mailto:mail@eduard-hildebrandt.de">Eduard Hildebrandt</a>
 */
public interface MarshallerListener {

    void beforeMarshal(Object source);

    void afterMarshal(Object source);

}
