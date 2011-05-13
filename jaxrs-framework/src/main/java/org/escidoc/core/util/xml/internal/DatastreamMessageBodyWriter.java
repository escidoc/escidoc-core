package org.escidoc.core.util.xml.internal;

import org.esidoc.core.utils.io.Datastream;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import static org.esidoc.core.utils.Preconditions.checkNotNull;

/**
 * {@link MessageBodyWriter} for {@link org.esidoc.core.utils.io.Datastream}.
 *
 * @author <a href="mailto:mail@eduard-hildebrandt.de">Eduard Hildebrandt</a>
 */
@Provider
public class DatastreamMessageBodyWriter implements MessageBodyWriter<Datastream> {

    public boolean isWriteable(final Class<?> type,
                               final Type genericType,
                               final Annotation[] annotations,
                               final MediaType mediaType) {
        return Datastream.class.isAssignableFrom(type);
    }

    public long getSize(final Datastream cachedOutputStream,
                        final Class<?> type,
                        final Type genericType,
                        final Annotation[] annotations,
                        final MediaType mediaType) {
        return cachedOutputStream.size();
    }

    public void writeTo(final Datastream cachedOutputStream,
                        final Class<?> type,
                        final Type genericType,
                        final Annotation[] annotations,
                        final MediaType mediaType,
                        final MultivaluedMap<String, Object> httpHeaders,
                        final OutputStream entityStream) throws IOException, WebApplicationException {
        checkNotNull(entityStream, "Entity stream can not be null.");
        cachedOutputStream.writeCacheTo(entityStream);
    }

}
