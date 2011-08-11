package org.escidoc.core.services.fedora;

import org.escidoc.core.utils.io.Stream;
import org.escidoc.core.utils.xml.StreamHolder;

import javax.xml.bind.annotation.XmlElement;

/**
 * @author <a href="mailto:mail@eduard-hildebrandt.de">Eduard Hildebrandt</a>
 */
public final class XmlContentTypeDatastreamHolderTO extends XmlContentTypeTO implements StreamHolder {

    private static final long serialVersionUID = - 5723767632577531513L;
    @XmlElement(namespace = NAMESPACE, name = ELEMENT_NAME)
    private Stream stream;

    @Override
    public Stream getStream() {
        if(this.stream == null) {
            this.stream = new Stream();
        }
        return this.stream;
    }

    @Override
    public void setContent(final Stream stream) {
        this.stream = stream;
    }

}
