package org.escidoc.core.util.xml.internal;

/**
 * @author <a href="mailto:mail@eduard-hildebrandt.de">Eduard Hildebrandt</a>
 */
public interface UnmarshallerListener {

    void beforeUnmarshal(Object target, Object parent);

    void afterUnmarshal(Object target, Object parent);

}
