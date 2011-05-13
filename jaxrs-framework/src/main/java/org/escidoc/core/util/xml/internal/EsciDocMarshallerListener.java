package org.escidoc.core.util.xml.internal;

import net.sf.oval.guard.Guarded;
import org.esidoc.core.utils.io.Datastream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.NotNull;
import javax.xml.bind.Marshaller;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import static org.esidoc.core.utils.Preconditions.checkNotNull;

/**
 * eSciDoc specific implementation of {@link Marshaller.Listener}.
 *
 * @author <a href="mailto:mail@eduard-hildebrandt.de">Eduard Hildebrandt</a>
 */
@Guarded
public class EsciDocMarshallerListener extends Marshaller.Listener {

    public final static Logger LOG = LoggerFactory.getLogger(EsciDocUnmarshallerListener.class);
    private final static String EMPTY_STRING = "";

    private OutputStream outputStream;
    private FilteringXMLStreamWriter filteringXmlStreamWriter;
    private List<MarshallerListener> marshallerListeners = new ArrayList<MarshallerListener>();

    public EsciDocMarshallerListener(OutputStream outputStream) {
        this.outputStream = outputStream;
        final XMLOutputFactory xmlOutputFactory = XMLOutputFactory.newInstance();
        final XMLStreamWriter xmlStreamWriter;
        try {
            xmlStreamWriter = xmlOutputFactory.createXMLStreamWriter(outputStream);
        } catch (XMLStreamException e) {
            throw new IllegalStateException("Error on creating XML factory.", e);
        }
        filteringXmlStreamWriter = new FilteringXMLStreamWriter(xmlStreamWriter);
    }

    public XMLStreamWriter getXMLStreamWriter() {
        return this.filteringXmlStreamWriter;
    }

    public void addMarshallerListener(@NotNull final MarshallerListener marshallerListener) {
        this.marshallerListeners.add(marshallerListener);
    }

    public void removeMarshallerListener(@NotNull final MarshallerListener marshallerListener) {
        this.marshallerListeners.remove(marshallerListener);
    }

    public void beforeMarshal(@NotNull final Object source) {
        if (source instanceof Datastream) {
            final Datastream datastream = (Datastream) source;
            try {
                filteringXmlStreamWriter.writeCharacters(EMPTY_STRING);
                filteringXmlStreamWriter.flush();
            } catch (final XMLStreamException e) {
                LOG.debug("Error on writing XML stream.", e);
            }
            try {
                datastream.writeCacheTo(outputStream);
            } catch (IOException e) {
                LOG.debug("Error on writing content to stream.", e);
            }
        }
        for (MarshallerListener marshallerListener : this.marshallerListeners) {
            marshallerListener.beforeMarshal(source);
        }
    }

    public void afterMarshal(final Object source) {
        for (MarshallerListener marshallerListener : this.marshallerListeners) {
            marshallerListener.afterMarshal(source);
        }
    }

}
