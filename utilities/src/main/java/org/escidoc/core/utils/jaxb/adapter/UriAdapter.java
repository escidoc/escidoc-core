package org.escidoc.core.utils.jaxb.adapter;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.net.URI;

/**
 * @author Marko Voss (marko.voss@fiz-karlsruhe.de)
 */
public class UriAdapter extends XmlAdapter<String, URI> {

    public URI unmarshal(String value) {
        return URI.create(value);
    }

    public String marshal(URI value) {
        if (value == null) {
            return null;
        }
        return value.toASCIIString();
    }

}