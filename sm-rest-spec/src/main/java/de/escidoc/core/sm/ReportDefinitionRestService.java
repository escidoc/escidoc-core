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

    /**
     * Create new Report Definition with given xmlData as TO.<br/>
     * <p/>
     * <b>Prerequisites:</b><br/>
     * <p/>
     * The provided XML data in the body is only accepted if the size is less than ESCIDOC_MAX_XML_SIZE.<br/>
     * <p/>
     * <b>Tasks:</b><br/> <ul> <li>The Report Definition is created. </li> <li>The XML data is returned.</li> </ul>
     *
     * @param reportDefinitionTO The XML representation of the Report Definition to be created corresponding to XML-schema
     *                "report-definition.xsd" as TO.
     * @return The XML representation of the created Report Definition corresponding to XML-schema
     *         "report-definition.xsd" as TO.
     * @throws AuthenticationException        Thrown in case of failed authentication.
     * @throws AuthorizationException         Thrown in case of failed authorization.
     * @throws XmlSchemaValidationException   ex
     * @throws XmlCorruptedException          ex
     * @throws MissingMethodParameterException
     *                                        ex
     * @throws ScopeNotFoundException         ex
     * @throws ScopeContextViolationException ex
     * @throws InvalidSqlException            ex
     * @throws SystemException                ex
     */
    @PUT
    ReportDefinitionTO create(ReportDefinitionTO reportDefinitionTO) throws AuthenticationException, AuthorizationException, XmlSchemaValidationException,
        XmlCorruptedException, MissingMethodParameterException, InvalidSqlException, ScopeNotFoundException,
        ScopeContextViolationException, SystemException;

    /**
     * Delete the specified Report Definition.<br/>
     * <p/>
     * <b>Prerequisites:</b><br/>
     * <p/>
     * The Report Definition must exist<br/>
     * <p/>
     * <b>Tasks:</b><br/> <ul> <li>The Report Definition is accessed using the provided reference.</li> <li>The Report
     * Definition is deleted.</li> <li>No data is returned.</li> </ul>
     *
     * @param id The Report Definition ID to be deleted.
     * @throws AuthenticationException Thrown in case of failed authentication.
     * @throws AuthorizationException  Thrown in case of failed authorization.
     * @throws ReportDefinitionNotFoundException
     *                                 e.
     * @throws MissingMethodParameterException
     *                                 e.
     * @throws SystemException         e.
     */
    @DELETE
    @Path("/{id}")
    void delete(@PathParam("id") String id) throws AuthenticationException, AuthorizationException,
        ReportDefinitionNotFoundException, MissingMethodParameterException, SystemException;

    /**
     * Retrieve the Report Definition with the given id.<br/>
     * <p/>
     * <b>Prerequisites:</b><br/>
     * <p/>
     * The Report Definition must exist<br/>
     * <p/>
     * <b>Tasks:</b><br/> <ul> <li>The Report Definition is accessed using the provided reference.</li> <li>The XML data
     * is returned as TO.</li> </ul>
     *
     * @param id The Report Definition ID to be retrieved.
     * @return The XML representation of the retrieved Report Definition corresponding to XML-schema
     *         "report-definition.xsd" as TO.
     * @throws AuthenticationException Thrown in case of failed authentication.
     * @throws AuthorizationException  Thrown in case of failed authorization.
     * @throws ReportDefinitionNotFoundException
     *                                 e.
     * @throws MissingMethodParameterException
     *                                 e.
     * @throws SystemException         e.
     */
    @GET
    @Path("/{id}")
    ReportDefinitionTO retrieve(@PathParam("id") String id) throws AuthenticationException, AuthorizationException,
        ReportDefinitionNotFoundException, MissingMethodParameterException, SystemException;

    /**
     * Update the Report Definition.<br/>
     * <p/>
     * <b>Prerequisites:</b><br/>
     * <p/>
     * The provided XML data in the body is only accepted if the size is less than ESCIDOC_MAX_XML_SIZE.<br/>
     * <p/>
     * The Report Definition must exist<br/>
     * <p/>
     * <b>Tasks:</b><br/> <ul> <li>the Report Definition is updated. </li> <li>The XML data is returned as TO.</li> </ul>
     *
     * @param id The Report Definition ID.
     * @param reportDefinitionTO            The XML representation of the Report Definition to be created corresponding to
     *                           XML-schema "report-definition.xsd" as TO.
     * @return The XML representation of the updated Report Definition corresponding to XML-schema
     *         "report-definition.xsd" as TO.
     * @throws AuthenticationException        Thrown in case of failed authentication.
     * @throws AuthorizationException         Thrown in case of failed authorization.
     * @throws ReportDefinitionNotFoundException
     *                                        e.
     * @throws MissingMethodParameterException
     *                                        e.
     * @throws ScopeNotFoundException         ex
     * @throws ScopeContextViolationException ex
     * @throws InvalidSqlException            ex
     * @throws XmlSchemaValidationException   e.
     * @throws XmlCorruptedException          e.
     * @throws SystemException                e.
     */
    @PUT
    @Path("/{id}")
    ReportDefinitionTO update(@PathParam("id") String id, ReportDefinitionTO reportDefinitionTO) throws AuthenticationException, AuthorizationException,
        ReportDefinitionNotFoundException, MissingMethodParameterException, ScopeNotFoundException,
        InvalidSqlException, ScopeContextViolationException, XmlSchemaValidationException, XmlCorruptedException,
        SystemException;

}
