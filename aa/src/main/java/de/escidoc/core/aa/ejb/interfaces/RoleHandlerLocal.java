package de.escidoc.core.aa.ejb.interfaces;

import de.escidoc.core.common.exceptions.application.invalid.InvalidSearchQueryException;
import de.escidoc.core.common.exceptions.application.invalid.XmlCorruptedException;
import de.escidoc.core.common.exceptions.application.invalid.XmlSchemaValidationException;
import de.escidoc.core.common.exceptions.application.missing.MissingAttributeValueException;
import de.escidoc.core.common.exceptions.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.application.notfound.RoleNotFoundException;
import de.escidoc.core.common.exceptions.application.security.AuthenticationException;
import de.escidoc.core.common.exceptions.application.security.AuthorizationException;
import de.escidoc.core.common.exceptions.application.violated.OptimisticLockingException;
import de.escidoc.core.common.exceptions.application.violated.RoleInUseViolationException;
import de.escidoc.core.common.exceptions.application.violated.UniqueConstraintViolationException;
import de.escidoc.core.common.exceptions.system.SystemException;
import org.springframework.security.context.SecurityContext;

import javax.ejb.EJBLocalObject;
import java.util.Map;

/**
 * Local interface for RoleHandler.
 */
public interface RoleHandlerLocal extends EJBLocalObject {

    String create(String xmlData, SecurityContext securityContext)
            throws UniqueConstraintViolationException,
            XmlCorruptedException,
            XmlSchemaValidationException,
            MissingMethodParameterException,
            AuthenticationException,
            AuthorizationException,
            SystemException;

    String create(String xmlData, String authHandle, Boolean restAccess)
            throws UniqueConstraintViolationException,
            XmlCorruptedException,
            XmlSchemaValidationException,
            MissingMethodParameterException,
            AuthenticationException,
            AuthorizationException,
            SystemException;

    void delete(String id, SecurityContext securityContext)
            throws AuthenticationException,
            AuthorizationException,
            MissingMethodParameterException,
            RoleNotFoundException,
            RoleInUseViolationException,
            SystemException;

    void delete(String id, String authHandle, Boolean restAccess)
            throws AuthenticationException,
            AuthorizationException,
            MissingMethodParameterException,
            RoleNotFoundException,
            RoleInUseViolationException,
            SystemException;

    String retrieve(String id, SecurityContext securityContext)
            throws RoleNotFoundException,
            MissingMethodParameterException,
            AuthenticationException,
            AuthorizationException,
            SystemException;

    String retrieve(String id, String authHandle, Boolean restAccess)
            throws RoleNotFoundException,
            MissingMethodParameterException,
            AuthenticationException,
            AuthorizationException,
            SystemException;

    String retrieveResources(String id, SecurityContext securityContext)
            throws AuthenticationException,
            AuthorizationException,
            MissingMethodParameterException,
            RoleNotFoundException,
            SystemException;

    String retrieveResources(String id, String authHandle, Boolean restAccess)
            throws AuthenticationException,
            AuthorizationException,
            MissingMethodParameterException,
            RoleNotFoundException,
            SystemException;

    String update(String id, String xmlData, SecurityContext securityContext)
            throws RoleNotFoundException,
            XmlCorruptedException,
            XmlSchemaValidationException,
            MissingAttributeValueException,
            UniqueConstraintViolationException,
            OptimisticLockingException,
            MissingMethodParameterException,
            AuthenticationException,
            AuthorizationException,
            SystemException;

    String update(String id, String xmlData, String authHandle, Boolean restAccess)
            throws RoleNotFoundException,
            XmlCorruptedException,
            XmlSchemaValidationException,
            MissingAttributeValueException,
            UniqueConstraintViolationException,
            OptimisticLockingException,
            MissingMethodParameterException,
            AuthenticationException,
            AuthorizationException,
            SystemException;

    String retrieveRoles(Map filter, SecurityContext securityContext)
            throws MissingMethodParameterException,
            AuthenticationException,
            AuthorizationException,
            SystemException,
            InvalidSearchQueryException;

    String retrieveRoles(Map filter, String authHandle, Boolean restAccess)
            throws MissingMethodParameterException,
            AuthenticationException,
            AuthorizationException,
            SystemException,
            InvalidSearchQueryException;

}
