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
import org.escidoc.core.domain.sru.parameters.SruSearchRequestParametersBean;
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
public interface AggregationDefinitionsRestService {

    /**
     * Retrieves all resources the User is allowed to see.<br/>
     * <p/>
     * <b>Prerequisites:</b><br/>
     * <p/>
     * <b>Tasks:</b><br/> <ul> <li>All Aggregation Definitions the user may see are accessed.</li> <li>The XML data is
     * returned.</li> </ul>
     *
     * @param parameters
     *            The Standard SRU Get-Parameters as Object
     * @return The XML representation of the list of Aggregation Definitions corresponding to XML-schema
     *         "aggregation-definition-list.xsd" as JAXBElement.
     * @throws MissingMethodParameterException
     *                                     If the parameter filter is not given.
     * @throws InvalidSearchQueryException thrown if the given search query could not be translated into a SQL query
     * @throws AuthenticationException     Thrown in case of failed authentication.
     * @throws AuthorizationException      Thrown in case of failed authorization.
     * @throws SystemException             e.
     */
    @GET
    JAXBElement<? extends ResponseType> retrieveAggregationDefinitions(
        @QueryParam("") SruSearchRequestParametersBean parameters) throws InvalidSearchQueryException,
        MissingMethodParameterException, AuthenticationException, AuthorizationException, SystemException;

}
