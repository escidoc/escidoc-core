package de.escidoc.core.sm.ejb.interfaces;

import de.escidoc.core.common.exceptions.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.application.security.AuthenticationException;
import de.escidoc.core.common.exceptions.application.security.AuthorizationException;
import de.escidoc.core.common.exceptions.system.SystemException;
import org.springframework.security.context.SecurityContext;

import javax.ejb.EJBLocalObject;

/**
 * Local interface for StatisticDataHandler.
 */
public interface StatisticDataHandlerLocal extends EJBLocalObject {

    void create(String xmlData, SecurityContext securityContext)
            throws AuthenticationException,
            AuthorizationException,
            MissingMethodParameterException,
            SystemException;

    void create(String xmlData, String authHandle, Boolean restAccess)
            throws AuthenticationException,
            AuthorizationException,
            MissingMethodParameterException,
            SystemException;

}
