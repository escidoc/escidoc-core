package org.escidoc.core.util.xml.internal;

import net.sf.oval.guard.Guarded;
import org.esidoc.core.utils.io.Datastream;
import org.esidoc.core.utils.io.IOUtils;

import javax.validation.constraints.NotNull;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import static org.esidoc.core.utils.Preconditions.checkNotNull;


/**
 * {@link MessageBodyReader} for {@link org.esidoc.core.utils.io.Datastream}.
 *
 * @author <a href="mailto:mail@eduard-hildebrandt.de">Eduard Hildebrandt</a>
 */
@Provider
@Guarded(applyFieldConstraintsToConstructors = true, applyFieldConstraintsToSetters = true,
        assertParametersNotNull = false, checkInvariants=true, inspectInterfaces = true)
public class DatastreamMessageBodyReader implements MessageBodyReader<Datastream> {


    public boolean isReadable(final Class<?> type, final Type genericType,
                              final Annotation[] annotations, final MediaType mediaType) {
        return Datastream.class.isAssignableFrom(type);
    }

    public Datastream readFrom(final Class<Datastream> type,
                               final Type genericType,
                               final Annotation[] annotations,
                               final MediaType mediaType,
                               final MultivaluedMap<String, String> httpHeaders,
                               @NotNull final InputStream entityStream) throws IOException, WebApplicationException {
        final Datastream cachedOutputStream = new Datastream();
        IOUtils.copyAndCloseInput(entityStream, cachedOutputStream);
        cachedOutputStream.lock();
        return cachedOutputStream;
    }
}


