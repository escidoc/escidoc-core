package org.escidoc.core.jaxrs.ext;

import net.sf.oval.constraint.NotNull;
import net.sf.oval.guard.Guarded;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

/**
 * @author Marko Voss (marko.voss@fiz-karlsruhe.de)
 */
@Guarded
public class GenericResponse<T> extends Response {

    private Response response;

    /**
     * Create a new instance.
     *
     * @param responseBuilder the {@link ResponseBuilder} to create the {@link Response} from
     * @param entityObject the object to append to the {@link Response}
     */
    public GenericResponse(@NotNull final ResponseBuilder responseBuilder, @NotNull final T entityObject) {
        /*
        set the entity here to ensure, that the entity is of type T (no matter if the user already put the entity into
        the responseBuilder.
         */
        this.response = responseBuilder.clone().entity(entityObject).build();
    }

    @Override
    @NotNull
    public T getEntity() {
        return (T)this.response.getEntity();
    }

    @Override
    public int getStatus() {
        return this.response.getStatus();
    }

    @Override
    public MultivaluedMap<String, Object> getMetadata() {
        return this.response.getMetadata();
    }
}