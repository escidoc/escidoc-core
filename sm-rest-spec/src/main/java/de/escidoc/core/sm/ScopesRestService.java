/**
 * 
 */
package de.escidoc.core.sm;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.xml.bind.JAXBElement;

import org.escidoc.core.domain.sru.ResponseType;
import org.escidoc.core.utils.io.MimeTypes;

import de.escidoc.core.common.exceptions.application.invalid.InvalidSearchQueryException;
import de.escidoc.core.common.exceptions.application.missing.MissingMethodParameterException;
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
public interface ScopesRestService {

    /**
     * Retrieve Scopes the user is allowed to see. For further information about the filter-names, please see the
     * explain-plan.<br/>
     * <p/>
     * Returns list of Scopes the user may see.<br/>
     * <p/>
     * <b>Prerequisites:</b><br/>
     * <p/>
     * <b>Tasks:</b><br/> <ul> <li>All Scopes are accessed.</li> <li>The XML data is returned as TO.</li> </ul>
     *
     * @param operation
     *            The Standard SRU Get-Parameter operation
     * @param version
     *            The Standard SRU Get-Parameter version
     * @param query
     *            The Standard SRU Get-Parameter query
     * @param startRecord
     *            The Standard SRU Get-Parameter startRecord
     * @param maximumRecords
     *            The Standard SRU Get-Parameter maximumRecords
     * @param recordPacking
     *            The Standard SRU Get-Parameter recordPacking
     * @param recordSchema
     *            The Standard SRU Get-Parameter recordSchema
     * @param recordXPath
     *            The Standard SRU Get-Parameter recordXPath
     * @param resultSetTTL
     *            The Standard SRU Get-Parameter resultSetTTL
     * @param sortKeys
     *            The Standard SRU Get-Parameter sortKeys
     * @param stylesheet
     *            The Standard SRU Get-Parameter stylesheet
     * @param scanClause
     *            The Standard SRU Get-Parameter scanClause
     * @param responsePosition
     *            The Standard SRU Get-Parameter responsePosition
     * @param maximumTerms
     *            The Standard SRU Get-Parameter maximumTerms
     * @return The XML representation of the Scopes corresponding to SRW schema. The list only contains these Scopes the
     *         user is allowed to see as TO.
     * @throws MissingMethodParameterException
     *                                     If the parameter filter is not given.
     * @throws InvalidSearchQueryException thrown if the given search query could not be translated into a SQL query
     * @throws AuthenticationException     Thrown in case of failed authentication.
     * @throws AuthorizationException      Thrown in case of failed authorization.
     * @throws SystemException             e.
     */
    @GET
    JAXBElement<? extends ResponseType> retrieveScopes(
        @QueryParam("operation") String operation,
        @QueryParam("version") String version,
        @QueryParam("query") String query,
        @QueryParam("startRecord") String startRecord,
        @QueryParam("maximumRecords") String maximumRecords,
        @QueryParam("recordPacking") String recordPacking,
        @QueryParam("recordSchema") String recordSchema,
        @QueryParam("recordXPath") String recordXPath,
        @QueryParam("resultSetTTL") String resultSetTTL,
        @QueryParam("sortKeys") String sortKeys,
        @QueryParam("stylesheet") String stylesheet,
        @QueryParam("scanClause") String scanClause,
        @QueryParam("responsePosition") String responsePosition,
        @QueryParam("maximumTerms") String maximumTerms) throws InvalidSearchQueryException,
        MissingMethodParameterException, AuthenticationException, AuthorizationException, SystemException;

}
