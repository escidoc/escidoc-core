package org.escidoc.core.services.fedora;

import javax.xml.bind.annotation.XmlEnumValue;

/**
 * @author <a href="mailto:mail@eduard-hildebrandt.de">Eduard Hildebrandt</a>
 */
public enum ControlGroup {

    @XmlEnumValue("X")
    X("X"),
    @XmlEnumValue("M")
    M("M"),
    @XmlEnumValue("R")
    R("R"),
    @XmlEnumValue("E")
    E("E");

    private final String value;

    ControlGroup(final String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ControlGroup fromValue(final String v) {
        for(final ControlGroup c : ControlGroup.values()) {
            if(c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
