/**
 * 
 */
package de.escidoc.core.aa;

import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.escidoc.core.domain.aa.UserAccountListTO;
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
public interface UserAccountsRestService {

    /**
     * FIXME Map
     */
    @GET
    UserAccountListTO retrieveUserAccounts(Map<String, String[]> filter) throws MissingMethodParameterException,
    AuthenticationException, AuthorizationException, InvalidSearchQueryException, SystemException;

}
