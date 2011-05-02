package de.escidoc.core.aa.ejb.interfaces;

import de.escidoc.core.common.exceptions.application.invalid.XmlCorruptedException;
import de.escidoc.core.common.exceptions.application.invalid.XmlSchemaValidationException;
import de.escidoc.core.common.exceptions.application.notfound.ContextNotFoundException;
import de.escidoc.core.common.exceptions.application.security.AuthenticationException;
import de.escidoc.core.common.exceptions.application.security.AuthorizationException;
import de.escidoc.core.common.exceptions.system.SystemException;
import org.springframework.security.context.SecurityContext;

import javax.ejb.EJBLocalObject;

/**
 * Local interface for ActionHandler.
 */
public interface ActionHandlerLocal extends EJBLocalObject {

    String createUnsecuredActions(String contextId, String actions, SecurityContext securityContext)
        throws ContextNotFoundException, XmlCorruptedException, XmlSchemaValidationException, AuthenticationException,
        AuthorizationException, SystemException;

    String createUnsecuredActions(String contextId, String actions, String authHandle, Boolean restAccess)
        throws ContextNotFoundException, XmlCorruptedException, XmlSchemaValidationException, AuthenticationException,
        AuthorizationException, SystemException;

    void deleteUnsecuredActions(String contextId, SecurityContext securityContext) throws ContextNotFoundException,
        AuthenticationException, AuthorizationException, SystemException;

    void deleteUnsecuredActions(String contextId, String authHandle, Boolean restAccess)
        throws ContextNotFoundException, AuthenticationException, AuthorizationException, SystemException;

    String retrieveUnsecuredActions(String contextId, SecurityContext securityContext) throws ContextNotFoundException,
        AuthenticationException, AuthorizationException, SystemException;

    String retrieveUnsecuredActions(String contextId, String authHandle, Boolean restAccess)
        throws ContextNotFoundException, AuthenticationException, AuthorizationException, SystemException;

}
