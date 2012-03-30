/**
 *
 */
package de.escidoc.core.context;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.xml.bind.JAXBElement;

import de.escidoc.core.context.param.*;
import net.sf.oval.constraint.NotNull;
import org.escidoc.core.domain.context.AdminDescriptorTO;
import org.escidoc.core.domain.context.AdminDescriptorsTO;
import org.escidoc.core.domain.context.ContextPropertiesTO;
import org.escidoc.core.domain.context.ContextResourcesTO;
import org.escidoc.core.domain.context.ContextTO;
import org.escidoc.core.domain.result.ResultTO;
import org.escidoc.core.domain.sru.ResponseTypeTO;
import org.escidoc.core.domain.taskparam.status.StatusTaskParamTO;
import org.escidoc.core.utils.io.MimeTypes;

import de.escidoc.core.common.exceptions.application.invalid.ContextNotEmptyException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidContentException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidStatusException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidXmlException;
import de.escidoc.core.common.exceptions.application.invalid.XmlCorruptedException;
import de.escidoc.core.common.exceptions.application.invalid.XmlSchemaValidationException;
import de.escidoc.core.common.exceptions.application.missing.MissingAttributeValueException;
import de.escidoc.core.common.exceptions.application.missing.MissingElementValueException;
import de.escidoc.core.common.exceptions.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.application.notfound.AdminDescriptorNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ContentModelNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ContextNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.OperationNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.OrganizationalUnitNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.StreamNotFoundException;
import de.escidoc.core.common.exceptions.application.security.AuthenticationException;
import de.escidoc.core.common.exceptions.application.security.AuthorizationException;
import de.escidoc.core.common.exceptions.application.violated.ContextNameNotUniqueException;
import de.escidoc.core.common.exceptions.application.violated.LockingException;
import de.escidoc.core.common.exceptions.application.violated.OptimisticLockingException;
import de.escidoc.core.common.exceptions.application.violated.ReadonlyAttributeViolationException;
import de.escidoc.core.common.exceptions.application.violated.ReadonlyElementViolationException;
import de.escidoc.core.common.exceptions.system.SystemException;
import org.escidoc.core.utils.io.Stream;

/**
 * @author Marko Voß
 */
@Path("/ir/context")
public interface ContextRestService {

    @PUT
    @Produces(MimeTypes.TEXT_XML)
    @Consumes(MimeTypes.TEXT_XML)
    ContextTO create(@NotNull @QueryParam("") CreateQueryParam queryParam,
                     @NotNull ContextTO contextTO)
            throws MissingMethodParameterException, ContextNameNotUniqueException,
            AuthenticationException, AuthorizationException, SystemException, ContentModelNotFoundException,
            ReadonlyElementViolationException, MissingAttributeValueException, MissingElementValueException,
            ReadonlyAttributeViolationException, InvalidContentException, OrganizationalUnitNotFoundException,
            InvalidStatusException, XmlCorruptedException, XmlSchemaValidationException;

    @GET
    @Path("/{id}")
    @Produces(MimeTypes.TEXT_XML)
    ContextTO retrieve(@NotNull @PathParam("id") String id,
                       @NotNull @QueryParam("") RetrieveQueryParam queryParam)
            throws ContextNotFoundException, MissingMethodParameterException,
            AuthenticationException, AuthorizationException, SystemException;

    @PUT
    @Path("/{id}")
    @Produces(MimeTypes.TEXT_XML)
    @Consumes(MimeTypes.TEXT_XML)
    ContextTO update(@NotNull @PathParam("id") String id,
                     @NotNull @QueryParam("") UpdateQueryParam queryParam,
                     @NotNull ContextTO contextTO)
            throws ContextNotFoundException,
            MissingMethodParameterException, InvalidContentException, InvalidStatusException, AuthenticationException,
            AuthorizationException, ReadonlyElementViolationException, ReadonlyAttributeViolationException,
            OptimisticLockingException, ContextNameNotUniqueException, InvalidXmlException, MissingElementValueException,
            SystemException;

    @DELETE
    @Path("/{id}")
    void delete(@NotNull @PathParam("id") String id,
                @NotNull @QueryParam("") DeleteQueryParam queryParam)
            throws ContextNotFoundException, ContextNotEmptyException,
            MissingMethodParameterException, InvalidStatusException, AuthenticationException, AuthorizationException,
            SystemException;

    @GET
    @Path("/{id}/properties")
    @Produces(MimeTypes.TEXT_XML)
    ContextPropertiesTO retrieveProperties(@NotNull @PathParam("id") String id,
                                           @NotNull @QueryParam("") RetrievePropertiesQueryParam queryParam)
            throws ContextNotFoundException, SystemException;

    @GET
    @Path("/{id}/resources/{resourceName}")
    @Produces(MimeTypes.ALL)
    Stream retrieveResource(@NotNull @PathParam("id") String id,
                            @NotNull @PathParam("resourceName") String resourceName,
                            @NotNull @QueryParam("") RetrieveResourceQueryParam queryParam,
                            @QueryParam("x-info5-roleId") String roleId,
                            @QueryParam("x-info5-userId") String userId,
                            @QueryParam("x-info5-omitHighlighting") String omitHighlighting)
            throws OperationNotFoundException, ContextNotFoundException, MissingMethodParameterException,
            AuthenticationException, AuthorizationException, SystemException;

    @GET
    @Path("/{id}/resources")
    @Produces(MimeTypes.TEXT_XML)
    ContextResourcesTO retrieveResources(@NotNull @PathParam("id") String id,
                                         @NotNull @QueryParam("") RetrieveResourcesQueryParam queryParam)
            throws ContextNotFoundException, MissingMethodParameterException, AuthenticationException,
            AuthorizationException, SystemException;

    @GET
    @Path("/{id}/resources/members")
    @Produces(MimeTypes.TEXT_XML)
    JAXBElement<? extends ResponseTypeTO> retrieveMembers(@NotNull @PathParam("id") String id,
                                                        @NotNull @QueryParam("") RetrieveMembersQueryParam queryParam,
                                                        @QueryParam("x-info5-roleId") String roleId,
                                                        @QueryParam("x-info5-userId") String userId,
                                                        @QueryParam("x-info5-omitHighlighting") String omitHighlighting)
            throws ContextNotFoundException, MissingMethodParameterException, SystemException;

    @GET
    @Path("/{id}/admin-descriptor/{name}")
    @Produces(MimeTypes.TEXT_XML)
    AdminDescriptorTO retrieveAdminDescriptor(@NotNull @PathParam("id") String id,
                                              @NotNull @PathParam("name") String name,
                                              @NotNull @QueryParam("") RetrieveAdminDescriptorQueryParam queryParam)
            throws ContextNotFoundException, MissingMethodParameterException, AuthenticationException,
            AuthorizationException, SystemException, AdminDescriptorNotFoundException;

    @GET
    @Path("/{id}/admin-descriptors")
    @Produces(MimeTypes.TEXT_XML)
    AdminDescriptorsTO retrieveAdminDescriptors(@NotNull @PathParam("id") String id,
                                                @NotNull @QueryParam("") RetrieveAdminDescriptorsQueryParam queryParam)
            throws ContextNotFoundException, MissingMethodParameterException, AuthenticationException,
            AuthorizationException, SystemException;

    @POST
    @Path("/{id}/admin-descriptor/")
    @Produces(MimeTypes.TEXT_XML)
    @Consumes(MimeTypes.TEXT_XML)
    AdminDescriptorTO updateAdminDescriptor(@NotNull @PathParam("id") String id,
                                            @NotNull @QueryParam("") UpdateAdminDescriptorQueryParam queryParam,
                                            @NotNull AdminDescriptorTO adminDescriptorTO)
            throws ContextNotFoundException, MissingMethodParameterException, AuthenticationException,
            AuthorizationException, SystemException, OptimisticLockingException, AdminDescriptorNotFoundException,
            InvalidXmlException;

    @POST
    @Path("/{id}/open")
    @Produces(MimeTypes.TEXT_XML)
    @Consumes(MimeTypes.TEXT_XML)
    ResultTO open(@NotNull @PathParam("id") String id,
                  @NotNull @QueryParam("") OpenQueryParam queryParam,
                  @NotNull StatusTaskParamTO statusTaskParam)
            throws ContextNotFoundException, MissingMethodParameterException, InvalidStatusException,
            AuthenticationException, AuthorizationException, OptimisticLockingException, InvalidXmlException,
            SystemException, LockingException, StreamNotFoundException;

    @POST
    @Path("/{id}/close")
    @Produces(MimeTypes.TEXT_XML)
    @Consumes(MimeTypes.TEXT_XML)
    ResultTO close(@NotNull @PathParam("id") String id,
                   @NotNull @QueryParam("") CloseQueryParam queryParam,
                   @NotNull StatusTaskParamTO statusTaskParam)
            throws ContextNotFoundException, MissingMethodParameterException, AuthenticationException,
            AuthorizationException, SystemException, OptimisticLockingException, InvalidXmlException,
            InvalidStatusException, LockingException, StreamNotFoundException;
}
