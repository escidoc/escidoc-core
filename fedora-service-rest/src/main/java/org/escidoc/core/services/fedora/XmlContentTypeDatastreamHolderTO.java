package org.escidoc.core.services.fedora;

import org.esidoc.core.utils.io.Stream;
import org.esidoc.core.utils.xml.DatastreamHolder;

import javax.xml.bind.annotation.XmlElement;

/**
 * @author <a href="mailto:mail@eduard-hildebrandt.de">Eduard Hildebrandt</a>
 */
public final class XmlContentTypeDatastreamHolderTO extends XmlContentTypeTO implements DatastreamHolder {

    private static final long serialVersionUID = - 5723767632577531513L;
    @XmlElement(namespace = NAMESPACE, name = ELEMENT_NAME)
    private Stream stream;

    @Override
    public Stream getDatastream() {
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
