package org.escidoc.core.util.xml.internal;

import net.sf.oval.guard.Guarded;
import org.apache.cxf.jaxrs.provider.JAXBElementProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.NotNull;
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
@Guarded
public class EsciDocJAXBElementProvider extends JAXBElementProvider {

    public final static Logger LOG = LoggerFactory.getLogger(EsciDocJAXBElementProvider.class);

    protected Object unmarshalFromInputStream(@NotNull final Unmarshaller unmarshaller,
                                              @NotNull final InputStream is,
                                              final MediaType mt)
            throws JAXBException {
        final EsciDocUnmarshallerListener unmarshallerListener = new EsciDocUnmarshallerListener(is);
        unmarshaller.setListener(unmarshallerListener);
        return unmarshaller.unmarshal(unmarshallerListener.getFilteredXmlStreamReader());
    }

    protected Object unmarshalFromReader(@NotNull final Unmarshaller unmarshaller,
                                         @NotNull final XMLStreamReader reader,
                                         final MediaType mt)
            throws JAXBException {
        final EsciDocUnmarshallerListener unmarshallerListener = new EsciDocUnmarshallerListener(reader);
        unmarshaller.setListener(unmarshallerListener);
        return unmarshaller.unmarshal(unmarshallerListener.getFilteredXmlStreamReader());
    }

    protected void marshalToOutputStream(@NotNull final Marshaller marshaller,
                                         final Object obj,
                                         @NotNull final OutputStream os,
                                         final MediaType mt)
            throws Exception {
        final EsciDocMarshallerListener marshallerListener = new EsciDocMarshallerListener(os);
        marshaller.setListener(marshallerListener);
        marshaller.marshal(obj, marshallerListener.getXMLStreamWriter());
    }

}
