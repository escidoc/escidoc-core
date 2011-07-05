package org.escidoc.core.util.xml.internal;

import org.esidoc.core.utils.io.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.Marshaller;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * eSciDoc specific implementation of {@link Marshaller.Listener}.
 *
 * @author <a href="mailto:mail@eduard-hildebrandt.de">Eduard Hildebrandt</a>
 */
public class EsciDocMarshallerListener extends Marshaller.Listener {

    private static final Logger LOG = LoggerFactory.getLogger(EsciDocUnmarshallerListener.class);
    private static final String EMPTY_STRING = "";

    private final OutputStream outputStream;
    private final FilteringXMLStreamWriter filteringXmlStreamWriter;
    private final List<MarshallerListener> marshallerListeners = new ArrayList<MarshallerListener>();

    public EsciDocMarshallerListener(final OutputStream outputStream) {
        this.outputStream = outputStream;
        final XMLOutputFactory xmlOutputFactory = XMLOutputFactory.newInstance();
        final XMLStreamWriter xmlStreamWriter;
        try {
            xmlStreamWriter = xmlOutputFactory.createXMLStreamWriter(outputStream);
        } catch(XMLStreamException e) {
            throw new IllegalStateException("Error on creating XML factory.", e);
        }
        filteringXmlStreamWriter = new FilteringXMLStreamWriter(xmlStreamWriter);
    }

    public XMLStreamWriter getXMLStreamWriter() {
        return this.filteringXmlStreamWriter;
    }

    public void addMarshallerListener(final MarshallerListener marshallerListener) {
        this.marshallerListeners.add(marshallerListener);
    }

    public void removeMarshallerListener(final MarshallerListener marshallerListener) {
        this.marshallerListeners.remove(marshallerListener);
    }

    @Override
    public void beforeMarshal(final Object source) {
        if(source instanceof Stream) {
            final Stream stream = (Stream) source;
            try {
                filteringXmlStreamWriter.writeCharacters(EMPTY_STRING);
                filteringXmlStreamWriter.flush();
            } catch(final XMLStreamException e) {
                LOG.debug("Error on writing XML stream.", e);
            }
            try {
                stream.writeCacheTo(outputStream);
            } catch(IOException e) {
                LOG.debug("Error on writing content to stream.", e);
            }
        }
        for(final MarshallerListener marshallerListener : this.marshallerListeners) {
            marshallerListener.beforeMarshal(source);
        }
    }

    @Override
    public void afterMarshal(final Object source) {
        for(final MarshallerListener marshallerListener : this.marshallerListeners) {
            marshallerListener.afterMarshal(source);
        }
    }

}
