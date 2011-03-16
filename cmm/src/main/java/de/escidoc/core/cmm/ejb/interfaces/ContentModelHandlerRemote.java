/*
 * Generated by XDoclet - Do not edit!
 */
package de.escidoc.core.cmm.ejb.interfaces;

import de.escidoc.core.common.business.fedora.EscidocBinaryContent;
import de.escidoc.core.common.exceptions.application.invalid.InvalidContentException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidSearchQueryException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidStatusException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidXmlException;
import de.escidoc.core.common.exceptions.application.invalid.XmlCorruptedException;
import de.escidoc.core.common.exceptions.application.invalid.XmlSchemaValidationException;
import de.escidoc.core.common.exceptions.application.missing.MissingAttributeValueException;
import de.escidoc.core.common.exceptions.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.application.notfound.ContentModelNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ContentStreamNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ResourceNotFoundException;
import de.escidoc.core.common.exceptions.application.security.AuthenticationException;
import de.escidoc.core.common.exceptions.application.security.AuthorizationException;
import de.escidoc.core.common.exceptions.application.violated.LockingException;
import de.escidoc.core.common.exceptions.application.violated.OptimisticLockingException;
import de.escidoc.core.common.exceptions.application.violated.ReadonlyVersionException;
import de.escidoc.core.common.exceptions.application.violated.ResourceInUseException;
import de.escidoc.core.common.exceptions.system.SystemException;
import org.springframework.security.context.SecurityContext;

import javax.ejb.EJBObject;
import java.rmi.RemoteException;
import java.util.Map;

/**
 * Remote interface for ContentModelHandler.
 */
public interface ContentModelHandlerRemote extends EJBObject {

    public String create(String xmlData,
                                   SecurityContext securityContext)
            throws InvalidContentException,
            MissingAttributeValueException,
            SystemException,
            AuthenticationException,
            AuthorizationException,
            MissingMethodParameterException,
            XmlCorruptedException,
            XmlSchemaValidationException,
            RemoteException;

    public String create(String xmlData, String authHandle, Boolean restAccess)
            throws InvalidContentException,
            MissingAttributeValueException,
            SystemException,
            AuthenticationException,
            AuthorizationException,
            MissingMethodParameterException,
            XmlCorruptedException,
            XmlSchemaValidationException,
            RemoteException;

    public void delete(String id, SecurityContext securityContext)
            throws SystemException,
            ContentModelNotFoundException,
            AuthenticationException,
            AuthorizationException,
            MissingMethodParameterException,
            LockingException,
            InvalidStatusException,
            ResourceInUseException, RemoteException;

    public void delete(String id, String authHandle, Boolean restAccess)
            throws SystemException,
            ContentModelNotFoundException,
            AuthenticationException,
            AuthorizationException,
            MissingMethodParameterException,
            LockingException,
            InvalidStatusException,
            ResourceInUseException, RemoteException;

    public String retrieve(String id,
                                     SecurityContext securityContext)
            throws ContentModelNotFoundException,
            SystemException,
            MissingMethodParameterException,
            AuthenticationException,
            AuthorizationException, RemoteException;

    public String retrieve(String id, String authHandle, Boolean restAccess)
            throws ContentModelNotFoundException,
            SystemException,
            MissingMethodParameterException,
            AuthenticationException,
            AuthorizationException, RemoteException;

    public String retrieveProperties(String id,
                                               SecurityContext securityContext)
            throws ContentModelNotFoundException,
            SystemException,
            AuthenticationException,
            AuthorizationException,
            MissingMethodParameterException,
            RemoteException;

    public String retrieveProperties(String id, String authHandle,
                                               Boolean restAccess)
            throws ContentModelNotFoundException,
            SystemException,
            AuthenticationException,
            AuthorizationException,
            MissingMethodParameterException,
            RemoteException;

    public String retrieveContentStreams(String id,
                                                   SecurityContext securityContext)
            throws ContentModelNotFoundException,
            SystemException,
            AuthenticationException,
            AuthorizationException,
            MissingMethodParameterException,
            RemoteException;

    public String retrieveContentStreams(String id, String authHandle,
                                                   Boolean restAccess)
            throws ContentModelNotFoundException,
            SystemException,
            AuthenticationException,
            AuthorizationException,
            MissingMethodParameterException,
            RemoteException;

    public String retrieveContentStream(String id, String name,
                                                  SecurityContext securityContext)
            throws ContentModelNotFoundException,
            SystemException,
            AuthenticationException,
            AuthorizationException,
            MissingMethodParameterException,
            RemoteException;

    public String retrieveContentStream(String id, String name,
                                                  String authHandle, Boolean restAccess)
            throws ContentModelNotFoundException,
            SystemException,
            AuthenticationException,
            AuthorizationException,
            MissingMethodParameterException,
            RemoteException;

    public EscidocBinaryContent retrieveContentStreamContent(String id,
                                                                                                    String name,
                                                                                                    SecurityContext securityContext)
            throws ContentModelNotFoundException,
            SystemException,
            AuthenticationException,
            AuthorizationException,
            MissingMethodParameterException,
            ContentStreamNotFoundException,
            InvalidStatusException, RemoteException;

    public EscidocBinaryContent retrieveContentStreamContent(String id,
                                                                                                    String name,
                                                                                                    String authHandle,
                                                                                                    Boolean restAccess)
            throws ContentModelNotFoundException,
            SystemException,
            AuthenticationException,
            AuthorizationException,
            MissingMethodParameterException,
            ContentStreamNotFoundException,
            InvalidStatusException, RemoteException;

    public String retrieveResources(String id,
                                              SecurityContext securityContext)
            throws ContentModelNotFoundException,
            SystemException,
            AuthenticationException,
            AuthorizationException,
            MissingMethodParameterException,
            RemoteException;

    public String retrieveResources(String id, String authHandle,
                                              Boolean restAccess)
            throws ContentModelNotFoundException,
            SystemException,
            AuthenticationException,
            AuthorizationException,
            MissingMethodParameterException,
            RemoteException;

    public String retrieveVersionHistory(String id,
                                                   SecurityContext securityContext)
            throws ContentModelNotFoundException,
            SystemException,
            AuthenticationException,
            AuthorizationException,
            MissingMethodParameterException,
            RemoteException;

    public String retrieveVersionHistory(String id, String authHandle,
                                                   Boolean restAccess)
            throws ContentModelNotFoundException,
            SystemException,
            AuthenticationException,
            AuthorizationException,
            MissingMethodParameterException,
            RemoteException;

    public String retrieveContentModels(Map parameterMap,
                                                  SecurityContext securityContext)
            throws InvalidSearchQueryException,
            SystemException, RemoteException;

    public String retrieveContentModels(Map parameterMap, String authHandle,
                                                  Boolean restAccess)
            throws InvalidSearchQueryException,
            SystemException, RemoteException;

    public String update(String id, String xmlData,
                                   SecurityContext securityContext)
            throws InvalidXmlException,
            ContentModelNotFoundException,
            OptimisticLockingException,
            SystemException,
            AuthenticationException,
            AuthorizationException,
            MissingMethodParameterException,
            ReadonlyVersionException,
            MissingAttributeValueException,
            InvalidContentException, RemoteException;

    public String update(String id, String xmlData, String authHandle,
                                   Boolean restAccess)
            throws InvalidXmlException,
            ContentModelNotFoundException,
            OptimisticLockingException,
            SystemException,
            AuthenticationException,
            AuthorizationException,
            MissingMethodParameterException,
            ReadonlyVersionException,
            MissingAttributeValueException,
            InvalidContentException, RemoteException;

    public EscidocBinaryContent retrieveMdRecordDefinitionSchemaContent(
            String id, String name,
            SecurityContext securityContext)
            throws AuthenticationException,
            AuthorizationException,
            MissingMethodParameterException,
            ContentModelNotFoundException,
            SystemException, RemoteException;

    public EscidocBinaryContent retrieveMdRecordDefinitionSchemaContent(
            String id, String name, String authHandle, Boolean restAccess)
            throws AuthenticationException,
            AuthorizationException,
            MissingMethodParameterException,
            ContentModelNotFoundException,
            SystemException, RemoteException;

    public EscidocBinaryContent retrieveResourceDefinitionXsltContent(
            String id, String name,
            SecurityContext securityContext)
            throws AuthenticationException,
            AuthorizationException,
            MissingMethodParameterException,
            ContentModelNotFoundException,
            ResourceNotFoundException,
            SystemException, RemoteException;

    public EscidocBinaryContent retrieveResourceDefinitionXsltContent(
            String id, String name, String authHandle, Boolean restAccess)
            throws AuthenticationException,
            AuthorizationException,
            MissingMethodParameterException,
            ContentModelNotFoundException,
            ResourceNotFoundException,
            SystemException, RemoteException;

}
