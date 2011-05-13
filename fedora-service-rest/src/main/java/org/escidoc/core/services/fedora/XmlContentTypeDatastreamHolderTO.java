package org.escidoc.core.services.fedora;

import org.esidoc.core.utils.io.Datastream;
import org.esidoc.core.utils.xml.DatastreamHolder;

import javax.xml.bind.annotation.XmlElement;

/**
 * @author <a href="mailto:mail@eduard-hildebrandt.de">Eduard Hildebrandt</a>
 */
public final class XmlContentTypeDatastreamHolderTO extends XmlContentTypeTO implements DatastreamHolder {

    @XmlElement(namespace = NAMESPACE, name = ELEMENT_NAME)
    private Datastream datastream;

    public Datastream getDatastream() {
        if (this.datastream == null) {
            this.datastream = new Datastream();
        }
        return this.datastream;
    }

    public void setContent(final Datastream datastream) {
        this.datastream = datastream;
    }

}
