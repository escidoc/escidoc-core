package org.escidoc.core.domain.sru;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import org.apache.axis.types.PositiveInteger;

/**
 * @author Marko Voss (marko.voss@fiz-karlsruhe.de)
 */
public class PositiveIntegerAdapter extends XmlAdapter<String, PositiveInteger>
{
    public PositiveInteger unmarshal(String value) {
        return new PositiveInteger(value);
    }

    public String marshal(PositiveInteger value) {
        if (value == null) {
            return null;
        }
        return value.toString();
    }
}