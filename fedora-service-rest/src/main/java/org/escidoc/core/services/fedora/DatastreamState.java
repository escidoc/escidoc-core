package org.escidoc.core.services.fedora;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;

/**
 * @author <a href="mailto:mail@eduard-hildebrandt.de">Eduard Hildebrandt</a>
 */
@XmlEnum
public enum DatastreamState {

    @XmlEnumValue("A")
    A("A"),
    @XmlEnumValue("I")
    I("I"),
    @XmlEnumValue("D")
    D("D");

    private final String value;

    DatastreamState(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static DatastreamState fromValue(String v) {
        for (DatastreamState c : DatastreamState.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}
