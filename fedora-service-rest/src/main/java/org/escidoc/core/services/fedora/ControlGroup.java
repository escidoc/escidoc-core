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

    ControlGroup(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ControlGroup fromValue(String v) {
        for (ControlGroup c : ControlGroup.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
