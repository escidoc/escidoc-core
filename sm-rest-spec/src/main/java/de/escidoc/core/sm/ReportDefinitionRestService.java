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

import org.escidoc.core.domain.sm.ReportDefinitionTO;
import org.escidoc.core.utils.io.MimeTypes;

import de.escidoc.core.common.exceptions.application.invalid.InvalidSqlException;
import de.escidoc.core.common.exceptions.application.invalid.XmlCorruptedException;
import de.escidoc.core.common.exceptions.application.invalid.XmlSchemaValidationException;
import de.escidoc.core.common.exceptions.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.application.notfound.ReportDefinitionNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ScopeNotFoundException;
import de.escidoc.core.common.exceptions.application.security.AuthenticationException;
import de.escidoc.core.common.exceptions.application.security.AuthorizationException;
import de.escidoc.core.common.exceptions.application.violated.ScopeContextViolationException;
import de.escidoc.core.common.exceptions.system.SystemException;

/**
 * @author Michael Hoppe
 * 
 */

@Path("/")
@Produces(MimeTypes.TEXT_XML)
@Consumes(MimeTypes.TEXT_XML)
public interface ReportDefinitionRestService {

    @PUT
    ReportDefinitionTO create(ReportDefinitionTO reportDefinitionTO) throws AuthenticationException, AuthorizationException, XmlSchemaValidationException,
        XmlCorruptedException, MissingMethodParameterException, InvalidSqlException, ScopeNotFoundException,
        ScopeContextViolationException, SystemException;

    @DELETE
    @Path("/{id}")
    void delete(@PathParam("id") String id) throws AuthenticationException, AuthorizationException,
        ReportDefinitionNotFoundException, MissingMethodParameterException, SystemException;

    @GET
    @Path("/{id}")
    ReportDefinitionTO retrieve(@PathParam("id") String id) throws AuthenticationException, AuthorizationException,
        ReportDefinitionNotFoundException, MissingMethodParameterException, SystemException;

    @PUT
    @Path("/{id}")
    ReportDefinitionTO update(@PathParam("id") String id, ReportDefinitionTO reportDefinitionTO) throws AuthenticationException, AuthorizationException,
        ReportDefinitionNotFoundException, MissingMethodParameterException, ScopeNotFoundException,
        InvalidSqlException, ScopeContextViolationException, XmlSchemaValidationException, XmlCorruptedException,
        SystemException;

}
