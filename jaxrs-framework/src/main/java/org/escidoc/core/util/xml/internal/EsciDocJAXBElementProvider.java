package org.escidoc.core.util.xml.internal;

import org.apache.cxf.jaxrs.provider.JAXBElementProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLStreamReader;
import java.io.InputStream;
import java.io.OutputStream;

import static org.esidoc.core.utils.Preconditions.checkNotNull;

/**
 * eSciDoc specific implementation of {@link JAXBElementProvider}.
 *
 * @author <a href="mailto:mail@eduard-hildebrandt.de">Eduard Hildebrandt</a>
 */
public class EsciDocJAXBElementProvider extends JAXBElementProvider {

    public final static Logger LOG = LoggerFactory.getLogger(EsciDocJAXBElementProvider.class);

    protected Object unmarshalFromInputStream(final Unmarshaller unmarshaller,
                                              final InputStream is,
                                              final MediaType mt)
            throws JAXBException {
        checkNotNull(unmarshaller, "Unmarshaller can not be null.");
        checkNotNull(is, "Input stream can not be null.");
        final EsciDocUnmarshallerListener unmarshallerListener = new EsciDocUnmarshallerListener(is);
        unmarshaller.setListener(unmarshallerListener);
        return unmarshaller.unmarshal(unmarshallerListener.getFilteredXmlStreamReader());
    }

    protected Object unmarshalFromReader(final Unmarshaller unmarshaller,
                                         final XMLStreamReader reader,
                                         final MediaType mt)
            throws JAXBException {
        checkNotNull(unmarshaller, "Unmarshaller can not be null.");
        checkNotNull(reader, "XML stream reader can not be null.");
        final EsciDocUnmarshallerListener unmarshallerListener = new EsciDocUnmarshallerListener(reader);
        unmarshaller.setListener(unmarshallerListener);
        return unmarshaller.unmarshal(unmarshallerListener.getFilteredXmlStreamReader());
    }

    protected void marshalToOutputStream(final Marshaller marshaller,
                                         final Object obj,
                                         final OutputStream os,
                                         final MediaType mt)
            throws Exception {
        checkNotNull(marshaller, "Marshaller can not be null.");
        checkNotNull(os, "Input stream can not be null.");
        final EsciDocMarshallerListener marshallerListener = new EsciDocMarshallerListener(os);
        marshaller.setListener(marshallerListener);
        marshaller.marshal(obj, marshallerListener.getXMLStreamWriter());
    }

}
