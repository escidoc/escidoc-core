/**
 * 
 */
package de.escidoc.core.context;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.xml.bind.JAXBElement;

import org.escidoc.core.domain.sru.ResponseType;
import org.escidoc.core.utils.io.MimeTypes;

import de.escidoc.core.common.exceptions.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.system.SystemException;

/**
 * @author Marko VoÃŸ
 * 
 */

@Path("/")
@Produces(MimeTypes.TEXT_XML)
@Consumes(MimeTypes.TEXT_XML)
public interface ContextsRestService {

    /**
     * The list of all contexts matching the given filter criteria will be created.
     * <p/>
     * <br/>
     * See chapter "Filters" for detailed information about filter definitions.
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
     * @param userId
     *            The custom SRU Get Parameter x-info5-userId
     * @param roleId
     *            The custom SRU Get Parameter x-info5-roleId
     * @param omitHighlighting
     *            The custom SRU Get Parameter x-info5-omitHighlighting
     * @return The XML representation of the the filtered list of contexts corresponding to SRW schema as JAXBElement.
     * @throws MissingMethodParameterException
     *             If the parameter filter is not given.
     * @throws SystemException
     *             Thrown if a framework internal error occurs.
     */
    @GET
    JAXBElement<? extends ResponseType> retrieveContexts(
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
        @QueryParam("maximumTerms") String maximumTerms, 
        @QueryParam("x-info5-roleId") String roleId,
        @QueryParam("x-info5-userId") String userId, 
        @QueryParam("x-info5-omitHighlighting") String omitHighlighting) throws MissingMethodParameterException, SystemException;

}
