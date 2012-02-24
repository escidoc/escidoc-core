package org.escidoc.core.domain.sru;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import org.apache.axis.types.NonNegativeInteger;

/**
 * @author Marko Voss (marko.voss@fiz-karlsruhe.de)
 */
public class NonNegativeIntegerAdapter extends XmlAdapter<String, NonNegativeInteger> {

    public NonNegativeInteger unmarshal(String value) {
        return new NonNegativeInteger(value);
    }

    public String marshal(NonNegativeInteger value) {
        if (value == null) {
            return null;
        }
        return value.toString();
    }
}