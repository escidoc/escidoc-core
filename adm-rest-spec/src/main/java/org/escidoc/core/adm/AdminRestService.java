/**
 *
 */
package org.escidoc.core.adm;

import de.escidoc.core.common.exceptions.application.invalid.InvalidSearchQueryException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidXmlException;
import de.escidoc.core.common.exceptions.application.security.AuthenticationException;
import de.escidoc.core.common.exceptions.application.security.AuthorizationException;
import de.escidoc.core.common.exceptions.system.EncodingSystemException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.exceptions.system.TripleStoreSystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import net.sf.oval.constraint.NotNull;
import org.escidoc.core.domain.properties.java.PropertiesTypeTO;
import org.escidoc.core.domain.result.ResultTypeTO;
import org.escidoc.core.domain.sb.IndexConfigurationTypeTO;
import org.escidoc.core.domain.taskparam.deleteobjects.DeleteObjectsTaskParamTO;
import org.escidoc.core.domain.taskparam.reindex.ReindexTaskParamTO;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBElement;

/**
 * @author Michael Hoppe
 * @author Marko Voss (marko.voss@fiz-karlsruhe.de)
 */
@Path("/adm/admin")
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
    @GET
    @Path("/deleteobjects")
    @Produces(MediaType.TEXT_XML)
    JAXBElement<ResultTypeTO> getPurgeStatus()
        throws AuthenticationException, AuthorizationException, SystemException;

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
     * @param ids The XML representation of task parameters conforming to members-task-param.xsd as TO. Including a id
     *            list of the objects to be deleted from Fedora. (example above)
     * @return total number of objects deleted, ...
     * @throws InvalidXmlException     Thrown if the taskParam has an invalid structure
     * @throws SystemException         Thrown in case of an internal error.
     * @throws AuthenticationException Thrown if the authentication failed due to an invalid provided eSciDoc user
     *                                 handle.
     * @throws AuthorizationException  Thrown if the authorization failed.
     */
    @POST
    @Path("/deleteobjects")
    @Produces(MediaType.TEXT_XML)
    @Consumes(MediaType.TEXT_XML)
    JAXBElement<ResultTypeTO> deleteObjects(@NotNull DeleteObjectsTaskParamTO ids)
        throws AuthenticationException, AuthorizationException, InvalidXmlException, SystemException;

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
    @Produces(MediaType.TEXT_XML)
    JAXBElement<ResultTypeTO> getReindexStatus()
        throws AuthenticationException, AuthorizationException, SystemException;

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
     * @param taskParam The XML representation of task parameters conforming to reindex-task-param.xsd as TO.
     * @return total number of objects found, ... as TO
     * @throws InvalidXmlException     Thrown if the given XML is invalid.
     * @throws SystemException         Thrown in case of an internal error.
     * @throws AuthenticationException Thrown if the authentication failed due to an invalid provided eSciDoc user
     *                                 handle.
     * @throws AuthorizationException  Thrown if the authorization failed.
     */
    @POST
    @Path("/reindex")
    @Produces(MediaType.TEXT_XML)
    @Consumes(MediaType.TEXT_XML)
    JAXBElement<ResultTypeTO> reindex(@NotNull ReindexTaskParamTO taskParam)
        throws AuthenticationException, AuthorizationException, InvalidXmlException, SystemException;

    /**
     * Decrease the type of the current status of the running re-indexing process by 1.
     *
     * FIXME: use type or query/path-param instead if String in body!
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
    @Consumes(MediaType.TEXT_PLAIN)
    void decreaseReindexStatus(@NotNull String objectType)
        throws AuthenticationException, AuthorizationException, InvalidXmlException, SystemException;

    /**
     * Provides an XML structure containing property with given key out of public configuration properties of the
     * eSciDoc Infrastructure as TO.
     * Supported keys are:
     * gsearch.url
     * escidoc-core.build
     * escidoc-core.baseurl
     * escidoc-core.repository-name
     * escidoc-core.admin-email
     * escidoc-core.earliest-date
     * escidoc-core.database.version
     * escidoc-core.database.consistent
     * escidoc-core.om.content.checksum-algorithm
     * 
     * @param key property-key
     * @return XML structure with eSciDoc configuration property as TO
     * @throws SystemException
     *             Thrown in case of an internal error.
     * @throws AuthenticationException
     *             Thrown if the authentication failed due to an invalid provided eSciDoc user handle.
     * @throws AuthorizationException
     *             Thrown if the authorization failed.
     */
    @GET
    @Path("/get-repository-info/{key}")
    @Produces(MediaType.TEXT_XML)
    JAXBElement<PropertiesTypeTO> getRepositoryInfo(@NotNull @PathParam("key") String key)
        throws AuthenticationException, AuthorizationException, WebserverSystemException, TripleStoreSystemException,
        EncodingSystemException, SystemException;

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
    @Produces(MediaType.TEXT_XML)
    JAXBElement<PropertiesTypeTO> getRepositoryInfo()
        throws AuthenticationException, AuthorizationException, WebserverSystemException, TripleStoreSystemException,
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
    @Produces(MediaType.TEXT_XML)
    JAXBElement<IndexConfigurationTypeTO> getIndexConfiguration()
        throws AuthenticationException, AuthorizationException, WebserverSystemException, SystemException;

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
    @Produces(MediaType.TEXT_XML)
    JAXBElement<ResultTypeTO> loadExamples(@NotNull @PathParam("type") String type)
        throws AuthenticationException, AuthorizationException, InvalidSearchQueryException, SystemException;
}