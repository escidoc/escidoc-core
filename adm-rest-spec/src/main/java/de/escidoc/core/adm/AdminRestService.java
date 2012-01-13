/**
 * 
 */
package de.escidoc.core.adm;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.escidoc.core.domain.ResultTO;
import org.escidoc.core.domain.properties.JavaUtilPropertiesTO;
import org.escidoc.core.domain.sb.IndexConfigurationTO;
import org.escidoc.core.domain.taskparam.IdSetTaskParamTO;
import org.escidoc.core.domain.taskparam.ReindexTaskParamTO;
import org.escidoc.core.utils.io.MimeTypes;

import de.escidoc.core.common.exceptions.application.invalid.InvalidSearchQueryException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidXmlException;
import de.escidoc.core.common.exceptions.application.invalid.XmlCorruptedException;
import de.escidoc.core.common.exceptions.application.security.AuthenticationException;
import de.escidoc.core.common.exceptions.application.security.AuthorizationException;
import de.escidoc.core.common.exceptions.system.EncodingSystemException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.exceptions.system.TripleStoreSystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;

/**
 * @author Michael Hoppe
 * 
 */

@Path("/")
@Produces(MimeTypes.TEXT_XML)
@Consumes(MimeTypes.TEXT_XML)
public interface AdminRestService {

    @Path("/deleteobjects")
    ResultTO getPurgeStatus() throws AuthenticationException, AuthorizationException, SystemException;

    @POST
    @Path("/deleteobjects")
    ResultTO deleteObjects(IdSetTaskParamTO ids) throws AuthenticationException, AuthorizationException,
    InvalidXmlException, SystemException;

    @GET
    @Path("/reindex")
    ResultTO getReindexStatus() throws AuthenticationException, AuthorizationException, SystemException;

    @POST
    @Path("/reindex")
    ResultTO reindex(ReindexTaskParamTO taskParam) throws AuthenticationException, AuthorizationException, InvalidXmlException, SystemException;

    @POST
    @Path("/decrease-reindex-status")
    void decreaseReindexStatus(String objectType) throws AuthenticationException, AuthorizationException, InvalidXmlException, SystemException;

    @GET
    @Path("/get-repository-info")
    JavaUtilPropertiesTO getRepositoryInfo() throws AuthenticationException, AuthorizationException, WebserverSystemException, TripleStoreSystemException,
    EncodingSystemException, SystemException;

    @GET
    @Path("/get-index-configuration")
    IndexConfigurationTO getIndexConfiguration() throws AuthenticationException, AuthorizationException, WebserverSystemException, SystemException;

    @GET
    @Path("/load-examples/{type}")
    ResultTO loadExamples(@PathParam("type") String type) throws AuthenticationException, AuthorizationException, InvalidSearchQueryException, SystemException;

}
