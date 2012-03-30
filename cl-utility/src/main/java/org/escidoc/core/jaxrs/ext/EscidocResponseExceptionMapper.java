package org.escidoc.core.jaxrs.ext;

import org.apache.cxf.jaxrs.client.ClientWebApplicationException;
import org.apache.cxf.jaxrs.client.ResponseExceptionMapper;
import org.escidoc.core.domain.exception.ExceptionTO;
import org.escidoc.core.domain.exception.ExceptionTOFactory;

import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBContext;
import javax.xml.transform.stream.StreamSource;
import java.io.InputStream;

/**
 * The {@link ResponseExceptionMapper} implementation to handle {@link de.escidoc.core.common.exceptions.EscidocException}s
 * on client-side if the {@link de.escidoc.core.common.exceptions.EscidocException}s are returned in the response entity.
 *
 * @author Marko Voss (marko.voss@fiz-karlsruhe.de)
 */
public class EscidocResponseExceptionMapper implements ResponseExceptionMapper<Throwable> {

    public Throwable fromResponse(Response response) {
        if (response.getEntity() == null) {
            throw new ClientWebApplicationException("No Response entity available.");
        }
        if (!(response.getEntity() instanceof InputStream)) {
            throw new ClientWebApplicationException("Response entity is no InputStream: " +
                    response.getEntity().getClass().getName());
        }

        final StreamSource source = new StreamSource((InputStream)response.getEntity());

        Throwable t;

        try {
            final JAXBContext context = JAXBContext.newInstance(ExceptionTO.class);
            final ExceptionTO exceptionTO = context.createUnmarshaller().unmarshal(source, ExceptionTO.class).getValue();
            t = ExceptionTOFactory.createThrowable(exceptionTO);
        } catch (Exception e) {
            throw new ClientWebApplicationException(e.getMessage(), e, response);
        }

        return t;
    }
}
