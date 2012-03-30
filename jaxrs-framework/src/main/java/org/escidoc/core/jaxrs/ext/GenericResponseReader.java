package org.escidoc.core.jaxrs.ext;

import org.apache.cxf.jaxrs.client.ClientWebApplicationException;
import org.apache.cxf.jaxrs.ext.MessageContext;
import org.apache.cxf.message.Message;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Providers;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

/**
 * The {@link MessageBodyReader} implementation to read a {@link GenericResponse}. This reader may be used on
 * client-side.
 *
 * <br/><br/><b>Thread-safety:</b> Use <u><b>one</b></u> {@link GenericResponseReader} instance per JAX-RS interface
 * instance.
 *
 * <br/><br/>Example:<br/><br/>{@link org.apache.cxf.jaxrs.client.JAXRSClientFactory}.create("http://foo.com", JAXRSInterface.class,
                 new {@link GenericResponseReader}())
 *
 * @author Marko Voss (marko.voss@fiz-karlsruhe.de)
 */
public class GenericResponseReader implements MessageBodyReader<Response> {

    /**
     * <b>Thread-safety:</b> Use <u><b>one</b></u> {@link GenericResponseReader} instance per JAX-RS interface
     * instance.
     */
    @Context
    private MessageContext context;

    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return GenericResponse.class.isAssignableFrom(type);
    }

    public Response readFrom(Class<Response> type, Type genericType, Annotation[] annotations, MediaType mediaType,
                             MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
            throws IOException, WebApplicationException {

        int status = Integer.valueOf(getContext().get(Message.RESPONSE_CODE).toString());

        Response.ResponseBuilder rb = Response.status(status);

        for (String header : httpHeaders.keySet()) {
            List<String> values = httpHeaders.get(header);
            for (String value : values) {
                rb.header(header, value);
            }
        }

        if (genericType != null && genericType instanceof ParameterizedType) {
            ParameterizedType p = ((ParameterizedType) genericType);
            if (p.getActualTypeArguments() != null && p.getActualTypeArguments().length > 0) {
                Type genType = p.getActualTypeArguments()[0];
                
                if (genType instanceof Class) {
                    Class genTypeClass = (Class) genType;
                    Providers providers = getContext().getProviders();
                    MessageBodyReader reader =
                            providers.getMessageBodyReader(genTypeClass, genType, annotations, mediaType);
                    if (reader == null) {
                        throw new ClientWebApplicationException("No reader for Response entity "
                                + genType.getClass().getName());
                    }
                    Object entity = reader.readFrom(genTypeClass, genType, annotations, mediaType, httpHeaders, entityStream);
                    return new GenericResponse(rb, entity);
                }
            }
        }
        throw new ClientWebApplicationException("Unable to handle response: " + type.getName());
    }

    protected MessageContext getContext() {
        return context;
    }
}

