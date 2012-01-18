/**
 * 
 */
package de.escidoc.core.sm;

import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.escidoc.core.domain.sm.StatisticDataTO;
import org.escidoc.core.utils.io.MimeTypes;

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
public interface StatisticDataRestService {

    /**
     * Create a Statistic Record.<br/>
     * <p/>
     * <b>Prerequisites:</b><br/>
     * <p/>
     * The provided XML data in the body is only accepted if the size is less than ESCIDOC_MAX_XML_SIZE.<br/>
     * <p/>
     * <b>Tasks:</b><br/> <ul> <li>The Statistic Record is created. Creation is done asynchronously by writing the
     * Statistic Record into a message-queue.</li> <li>No data is returned.</li> </ul>
     *
     * @param statisticDataTO The XML representation of the Statistic Record to be created corresponding to XML-schema
     *                "statistic-data.xsd" as TO.
     * @throws AuthenticationException Thrown in case of failed authentication.
     * @throws AuthorizationException  Thrown in case of failed authorization.
     * @throws MissingMethodParameterException
     *                                 ex
     * @throws SystemException         ex
     */
    @PUT
    void create(StatisticDataTO statisticDataTO) throws AuthenticationException, AuthorizationException,
        MissingMethodParameterException, SystemException;

}
