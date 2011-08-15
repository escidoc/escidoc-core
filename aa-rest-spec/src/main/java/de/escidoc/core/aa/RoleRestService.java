/**
 * 
 */
package de.escidoc.core.aa;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.escidoc.core.domain.aa.RoleResourcesTO;
import org.escidoc.core.domain.aa.RoleTO;
import org.escidoc.core.utils.io.MimeTypes;

import de.escidoc.core.common.exceptions.application.invalid.XmlCorruptedException;
import de.escidoc.core.common.exceptions.application.invalid.XmlSchemaValidationException;
import de.escidoc.core.common.exceptions.application.missing.MissingAttributeValueException;
import de.escidoc.core.common.exceptions.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.application.notfound.RoleNotFoundException;
import de.escidoc.core.common.exceptions.application.security.AuthenticationException;
import de.escidoc.core.common.exceptions.application.security.AuthorizationException;
import de.escidoc.core.common.exceptions.application.violated.OptimisticLockingException;
import de.escidoc.core.common.exceptions.application.violated.RoleInUseViolationException;
import de.escidoc.core.common.exceptions.application.violated.UniqueConstraintViolationException;
import de.escidoc.core.common.exceptions.system.SystemException;

/**
 * @author Michael Hoppe
 * 
 */

@Path("/")
@Produces(MimeTypes.TEXT_XML)
@Consumes(MimeTypes.TEXT_XML)
public interface RoleRestService {

    @PUT
    RoleTO create(RoleTO roleTo) throws UniqueConstraintViolationException, XmlCorruptedException,
        XmlSchemaValidationException, MissingMethodParameterException, AuthenticationException, AuthorizationException,
        SystemException;

    @DELETE
    @Path("/{id}")
    void delete(@PathParam("id") String id) throws AuthenticationException, AuthorizationException, MissingMethodParameterException,
        RoleNotFoundException, RoleInUseViolationException, SystemException;

    @GET
    @Path("/{id}")
    RoleTO retrieve(@PathParam("id") String id) throws RoleNotFoundException, MissingMethodParameterException,
        AuthenticationException, AuthorizationException, SystemException;

    @PUT
    @Path("/{id}")
    RoleTO update(@PathParam("id") String id, RoleTO roleTo) throws RoleNotFoundException, XmlCorruptedException,
        XmlSchemaValidationException, MissingAttributeValueException, UniqueConstraintViolationException,
        OptimisticLockingException, MissingMethodParameterException, AuthenticationException, AuthorizationException,
        SystemException;

    @GET
    @Path("/{id}/resources")
    RoleResourcesTO retrieveResources(@PathParam("id") String id) throws AuthenticationException, AuthorizationException,
        MissingMethodParameterException, RoleNotFoundException, SystemException;

}
