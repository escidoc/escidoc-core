package org.escidoc.core.util.xml.internal;

import org.esidoc.core.utils.io.Stream;
import org.esidoc.core.utils.xml.DatastreamHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * eSciDoc specific implementation of {@link Unmarshaller.Listener}
 *
 * @author <a href="mailto:mail@eduard-hildebrandt.de">Eduard Hildebrandt</a>
 */
public class EsciDocUnmarshallerListener extends Unmarshaller.Listener {

    private static final Logger LOG = LoggerFactory.getLogger(EsciDocUnmarshallerListener.class);

    private final List<UnmarshallerListener> unmarshallerListeners = new ArrayList<UnmarshallerListener>();

    private final ElementStreamFilter elementStreamFilter;

    public EsciDocUnmarshallerListener(final InputStream inputStream) {
        this.elementStreamFilter = new ElementStreamFilter(inputStream);
        init();
    }

    public EsciDocUnmarshallerListener(final XMLStreamReader inputStream) {
        this.elementStreamFilter = new ElementStreamFilter(inputStream);
        init();
    }

    private void init() {
        this.elementStreamFilter
                .addIgnoredElemenet(QName.valueOf("{http://www.escidoc.de/schemas/metadatarecords/0.5}md-record"));
        this.elementStreamFilter.addIgnoredElemenet(QName.valueOf("{info:fedora/fedora-system:def/foxml#}xmlContent"));
    }


    public XMLStreamReader getFilteredXmlStreamReader() {
        return this.elementStreamFilter.getFilteredXmlStreamReader();
    }

    public void addUnmarshallerListener(final UnmarshallerListener unmarshallerListener) {
        this.unmarshallerListeners.add(unmarshallerListener);
    }

    public void removeUnmarshallerListener(final UnmarshallerListener unmarshallerListener) {
        this.unmarshallerListeners.remove(unmarshallerListener);
    }

    @Override
    public void beforeUnmarshal(final Object target, final Object parent) {
        if(target instanceof DatastreamHolder) {
            final DatastreamHolder contentTO = (DatastreamHolder) target;
            beforeUnmarshalContentTO(contentTO);
        }
        for(final UnmarshallerListener unmarshallerListener : this.unmarshallerListeners) {
            unmarshallerListener.beforeUnmarshal(target, parent);
        }
    }

    @Override
    public void afterUnmarshal(final Object target, final Object parent) {
        if(target instanceof DatastreamHolder) {
            final DatastreamHolder contentTO = (DatastreamHolder) target;
            afterUnmarshalContentTO(contentTO);
        }
        for(final UnmarshallerListener unmarshallerListener : this.unmarshallerListeners) {
            unmarshallerListener.afterUnmarshal(target, parent);
        }
    }

    private void beforeUnmarshalContentTO(final DatastreamHolder datastreamHolder) {
        this.elementStreamFilter.setActive(true);
        this.elementStreamFilter.setOutputStream(datastreamHolder.getDatastream());
    }

    private void afterUnmarshalContentTO(final DatastreamHolder datastreamHolder) {
        this.elementStreamFilter.setActive(false);
        lockStream(datastreamHolder.getDatastream());
    }

    private void lockStream(final Stream stream) {
        try {
            stream.lock();
        } catch(final IOException e) {
            if(LOG.isWarnEnabled()) {
                final String errorMessage = "Error on locking stream.";
                LOG.warn(errorMessage, e);
            }
        }
    }

}
