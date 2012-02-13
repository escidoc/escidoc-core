/**
 * 
 */
package de.escidoc.core.adm;

import de.escidoc.core.common.exceptions.application.invalid.InvalidSearchQueryException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidXmlException;
import de.escidoc.core.common.exceptions.application.security.AuthenticationException;
import de.escidoc.core.common.exceptions.application.security.AuthorizationException;
import de.escidoc.core.common.exceptions.system.EncodingSystemException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.exceptions.system.TripleStoreSystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import org.escidoc.core.domain.ResultTO;
import org.escidoc.core.domain.properties.JavaUtilPropertiesTO;
import org.escidoc.core.domain.sb.IndexConfigurationTO;
import org.escidoc.core.domain.taskparam.DeleteObjectsTaskParamTO;
import org.escidoc.core.domain.taskparam.ReindexTaskParamTO;
import org.escidoc.core.utils.io.MimeTypes;

import javax.ws.rs.*;

/**
 * @author Michael Hoppe
 * 
 */

@Path("/")
@Produces(MimeTypes.TEXT_XML)
@Consumes(MimeTypes.TEXT_XML)
public interface AdminRestService {

    /**
     * Get the current status of the running/finished purging process.
     *
     * @return current status (how many objects are still in the queue) as TO
     * @throws SystemException         Thrown in case of an internal error.
     * @throws AuthenticationException Thrown if the authentication failed due to an invalid provided eSciDoc user
     *                                 handle.
     * @throws AuthorizationException  Thrown if the authorization failed.
     */
    @Path("/deleteobjects")
    ResultTO getPurgeStatus() throws AuthenticationException, AuthorizationException, SystemException;

    /**
     * Delete a list of objects given by their object ids from Fedora. In case of Items this method will also delete all
     * depending Components of the given Items. The deletion runs asynchronously and returns some useful information to
     * the user, e.g. the total number of objects to delete. <b>Example:</b><br/> <br/>
     * <p/>
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;
     * </pre>
     * <p/>
     * <pre>
     * &lt;param&gt;
     * </pre>
     * <p/>
     * <pre>
     *   &lt;id&gt;escidoc:1&lt;/id&gt;
     * </pre>
     * <p/>
     * <pre>
     *   &lt;id&gt;escidoc:2&lt;/id&gt;
     * </pre>
     * <p/>
     * <pre>
     *   &lt;id&gt;escidoc:3&lt;/id&gt;
     * </pre>
     * <p/>
     * <pre>
     * &lt;/param&gt;
     * </pre>
     *
     * @param ids The XML representation of task parameters conforming to members-task-param.xsd as TO.
     * Including a id list of the objects to be deleted from Fedora. (example above)
     * @return total number of objects deleted, ...
     * @throws InvalidXmlException     Thrown if the taskParam has an invalid structure
     * @throws SystemException         Thrown in case of an internal error.
     * @throws AuthenticationException Thrown if the authentication failed due to an invalid provided eSciDoc user
     *                                 handle.
     * @throws AuthorizationException  Thrown if the authorization failed.
     */
    @POST
    @Path("/deleteobjects")
    ResultTO deleteObjects(DeleteObjectsTaskParamTO ids) throws AuthenticationException, AuthorizationException,
    InvalidXmlException, SystemException;

    /**
     * Get the current status of the running/finished reindexing process.
     *
     * @return current status (how many objects are still in the queue) as TO
     * @throws SystemException         Thrown in case of an internal error.
     * @throws AuthenticationException Thrown if the authentication failed due to an invalid provided eSciDoc user
     *                                 handle.
     * @throws AuthorizationException  Thrown if the authorization failed.
     */
    @GET
    @Path("/reindex")
    ResultTO getReindexStatus() throws AuthenticationException, AuthorizationException, SystemException;

    /**
     * Reinitialize the search index. The initialization runs asynchronously and returns some useful information to the
     * user, e.g. the total number of objects found.<b>Example:</b><br/> <br/>
     * <p/>
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;
     * </pre>
     * <p/>
     * <pre>
     * &lt;param&gt;
     * </pre>
     * <p/>
     * <pre>
     *   &lt;index-name&gt;all&lt;/index-name&gt;
     * </pre>
     * <p/>
     * <pre>
     *   &lt;clear-index&gt;true&lt;/clear-index&gt;
     * </pre>
     * <p/>
     * <pre>
     *   &lt;commit-writes&gt;false&lt;/commit-writes&gt;
     * </pre>
     * <p/>
     * <pre>
     * &lt;/param&gt;
     * </pre>
     *
     *
     * @param taskParam The XML representation of task parameters conforming to reindex-task-param.xsd as TO.
     * @return total number of objects found, ... as TO
     * @throws InvalidXmlException Thrown if the given XML is invalid.
     * @throws SystemException             Thrown in case of an internal error.
     * @throws AuthenticationException     Thrown if the authentication failed due to an invalid provided eSciDoc user
     *                                     handle.
     * @throws AuthorizationException      Thrown if the authorization failed.
     */
    @POST
    @Path("/reindex")
    ResultTO reindex(ReindexTaskParamTO taskParam) throws AuthenticationException, AuthorizationException, InvalidXmlException, SystemException;

    /**
     * Decrease the type of the current status of the running reindexing process by 1.
     *
     * @param objectType object type to decrease
     * @throws InvalidXmlException     Thrown if the given XML is invalid.
     * @throws SystemException         Thrown in case of an internal error.
     * @throws AuthenticationException Thrown if the authentication failed due to an invalid provided eSciDoc user
     *                                 handle.
     * @throws AuthorizationException  Thrown if the authorization failed.
     */
    @POST
    @Path("/decrease-reindex-status")
    void decreaseReindexStatus(String objectType) throws AuthenticationException, AuthorizationException, InvalidXmlException, SystemException;

    /**
     * Provides an XML structure containing public configuration properties of the eSciDoc Infrastructure and the
     * earliest creation date of eSciDoc repository objects as TO.
     *
     * @return XML structure with eSciDoc configuration properties as TO
     * @throws SystemException         Thrown in case of an internal error.
     * @throws AuthenticationException Thrown if the authentication failed due to an invalid provided eSciDoc user
     *                                 handle.
     * @throws AuthorizationException  Thrown if the authorization failed.
     */
    @GET
    @Path("/get-repository-info")
    JavaUtilPropertiesTO getRepositoryInfo() throws AuthenticationException, AuthorizationException, WebserverSystemException, TripleStoreSystemException,
    EncodingSystemException, SystemException;

    /**
     * Provides a xml structure containing the index-configuration as TO.
     *
     * @return xml structure with index configuration as TO
     * @throws SystemException         Thrown if a framework internal error occurs.
     * @throws AuthenticationException Thrown if the authentication fails due to an invalid provided eSciDoc user
     *                                 handle.
     * @throws AuthorizationException  Thrown if authorization fails.
     */
    @GET
    @Path("/get-index-configuration")
    IndexConfigurationTO getIndexConfiguration() throws AuthenticationException, AuthorizationException, WebserverSystemException, SystemException;

    /**
     * Loads a set of example objects into the framework.
     *
     * @param type Specifies the type of example set which is to load.
     * @return some useful information as TO
     * @throws InvalidSearchQueryException Thrown if a given search query could not be translated into a SQL query.
     * @throws SystemException             Thrown in case of an internal error.
     * @throws AuthenticationException     Thrown if the authentication failed due to an invalid provided eSciDoc user
     *                                     handle.
     * @throws AuthorizationException      Thrown if the authorization failed.
     */
    @GET
    @Path("/load-examples/{type}")
    ResultTO loadExamples(@PathParam("type") String type) throws AuthenticationException, AuthorizationException, InvalidSearchQueryException, SystemException;

}
