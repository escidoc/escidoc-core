package org.escidoc.core.services.fedora;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;

/**
 * @author <a href="mailto:mail@eduard-hildebrandt.de">Eduard Hildebrandt</a>
 */
@XmlEnum
public enum ChecksumType {

    @XmlEnumValue("DEFAULT")
    DEFAULT("DEFAULT"),
    @XmlEnumValue("DISABLED")
    DISABLED("DISABLED"),
    @XmlEnumValue("MD5")
    MD5("MD5"),
    @XmlEnumValue("SHA-1")
    SHA1("SHA-1"),
    @XmlEnumValue("SHA-256")
    SHA256("SHA-256"),
    @XmlEnumValue("SHA-385")
    SHA385("SHA-385"),
    @XmlEnumValue("SHA-512")
    SHA512("SHA-512");

    private final String value;

    ChecksumType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ChecksumType fromValue(String v) {
        for (ChecksumType c : ChecksumType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
