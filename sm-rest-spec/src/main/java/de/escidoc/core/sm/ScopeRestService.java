/**
 * 
 */
package de.escidoc.core.sm;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.escidoc.core.domain.sm.ScopeListTO;
import org.escidoc.core.domain.sm.ScopeTO;
import org.escidoc.core.domain.sru.parameters.SruSearchRequestParametersBean;
import org.escidoc.core.utils.io.MimeTypes;

import de.escidoc.core.common.exceptions.application.invalid.InvalidSearchQueryException;
import de.escidoc.core.common.exceptions.application.invalid.XmlCorruptedException;
import de.escidoc.core.common.exceptions.application.invalid.XmlSchemaValidationException;
import de.escidoc.core.common.exceptions.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.application.notfound.ScopeNotFoundException;
import de.escidoc.core.common.exceptions.application.security.AuthenticationException;
import de.escidoc.core.common.exceptions.application.security.AuthorizationException;
import de.escidoc.core.common.exceptions.system.SystemException;

/**
 * @author Michael Hoppe
 * 
 */

@Path("/")
@Produces(MimeTypes.TEXT_XML)
@Consumes(MimeTypes.TEXT_XML)
public interface ScopeRestService {

    @PUT
    ScopeTO create(ScopeTO scopeTO) throws AuthenticationException, AuthorizationException, XmlSchemaValidationException,
        XmlCorruptedException, MissingMethodParameterException, SystemException;

    @DELETE
    @Path("/{id}")
    void delete(@PathParam("id") String id) throws AuthenticationException, AuthorizationException, ScopeNotFoundException,
        MissingMethodParameterException, SystemException;

    @GET
    @Path("/{id}")
    ScopeTO retrieve(@PathParam("id") String id) throws AuthenticationException, AuthorizationException, ScopeNotFoundException,
        MissingMethodParameterException, SystemException;

    @PUT
    @Path("/{id}")
    ScopeTO update(@PathParam("id") String id, ScopeTO scopeTO) throws AuthenticationException, AuthorizationException,
        ScopeNotFoundException, MissingMethodParameterException, XmlSchemaValidationException, XmlCorruptedException,
        SystemException;

}
