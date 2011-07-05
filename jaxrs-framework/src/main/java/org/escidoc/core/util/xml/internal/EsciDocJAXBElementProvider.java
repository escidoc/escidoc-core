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

/**
 * eSciDoc specific implementation of {@link JAXBElementProvider}.
 *
 * @author <a href="mailto:mail@eduard-hildebrandt.de">Eduard Hildebrandt</a>
 */
@Guarded(applyFieldConstraintsToConstructors = true, applyFieldConstraintsToSetters = true,
        assertParametersNotNull = false, checkInvariants = true, inspectInterfaces = true)
public class EsciDocJAXBElementProvider extends JAXBElementProvider {

    public static final Logger LOG = LoggerFactory.getLogger(EsciDocJAXBElementProvider.class);

    @Override
    protected Object unmarshalFromInputStream(@NotNull final Unmarshaller unmarshaller, @NotNull final InputStream is,
                                              final MediaType mt) throws JAXBException {
        final EsciDocUnmarshallerListener unmarshallerListener = new EsciDocUnmarshallerListener(is);
        unmarshaller.setListener(unmarshallerListener);
        return unmarshaller.unmarshal(unmarshallerListener.getFilteredXmlStreamReader());
    }

    @Override
    protected Object unmarshalFromReader(@NotNull final Unmarshaller unmarshaller,
                                         @NotNull final XMLStreamReader reader, final MediaType mt)
            throws JAXBException {
        final EsciDocUnmarshallerListener unmarshallerListener = new EsciDocUnmarshallerListener(reader);
        unmarshaller.setListener(unmarshallerListener);
        return unmarshaller.unmarshal(unmarshallerListener.getFilteredXmlStreamReader());
    }

    @Override
    protected void marshalToOutputStream(@NotNull final Marshaller marshaller, final Object obj,
                                         @NotNull final OutputStream os, final MediaType mt) throws Exception {
        final EsciDocMarshallerListener marshallerListener = new EsciDocMarshallerListener(os);
        marshaller.setListener(marshallerListener);
        marshaller.marshal(obj, marshallerListener.getXMLStreamWriter());
    }

}
