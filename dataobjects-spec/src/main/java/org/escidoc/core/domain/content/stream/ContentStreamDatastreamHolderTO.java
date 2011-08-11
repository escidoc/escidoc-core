package org.escidoc.core.domain.content.stream;

import org.escidoc.core.utils.io.Stream;
import org.escidoc.core.utils.xml.StreamHolder;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import static org.escidoc.core.utils.Preconditions.checkNotNull;

/**
 * @author <a href="mailto:mail@eduard-hildebrandt.de">Eduard Hildebrandt</a>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "content-stream", namespace = "http://www.escidoc.de/schemas/contentstreams/0.7")
public class ContentStreamDatastreamHolderTO extends ContentStreamTO implements StreamHolder {

    @XmlElement(namespace = NAMESPACE, name = ELEMENT_NAME)
    private Stream stream;

    public Stream getStream() {
        if (this.stream == null) {
            this.stream = new Stream();
        }
        return this.stream;
    }

    public void setContent(@NotNull final Stream stream) {
        this.stream = stream;
    }

}
