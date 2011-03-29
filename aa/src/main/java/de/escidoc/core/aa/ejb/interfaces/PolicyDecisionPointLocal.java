package de.escidoc.core.aa.ejb.interfaces;

import de.escidoc.core.common.exceptions.application.invalid.XmlCorruptedException;
import de.escidoc.core.common.exceptions.application.invalid.XmlSchemaValidationException;
import de.escidoc.core.common.exceptions.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.application.notfound.ResourceNotFoundException;
import de.escidoc.core.common.exceptions.application.security.AuthenticationException;
import de.escidoc.core.common.exceptions.application.security.AuthorizationException;
import de.escidoc.core.common.exceptions.system.SystemException;
import org.springframework.security.context.SecurityContext;

import javax.ejb.EJBLocalObject;
import java.util.List;

/**
 * Local interface for PolicyDecisionPoint.
 */
public interface PolicyDecisionPointLocal extends EJBLocalObject {

    String evaluate(String requestsXml, SecurityContext securityContext)
            throws ResourceNotFoundException,
            XmlCorruptedException,
            XmlSchemaValidationException,
            MissingMethodParameterException,
            AuthenticationException,
            AuthorizationException,
            SystemException;

    String evaluate(String requestsXml, String authHandle, Boolean restAccess)
            throws ResourceNotFoundException,
            XmlCorruptedException,
            XmlSchemaValidationException,
            MissingMethodParameterException,
            AuthenticationException,
            AuthorizationException,
            SystemException;

    boolean[] evaluateRequestList(List requests, SecurityContext securityContext)
            throws ResourceNotFoundException,
            MissingMethodParameterException,
            AuthenticationException,
            AuthorizationException,
            SystemException;

    boolean[] evaluateRequestList(List requests, String authHandle, Boolean restAccess)
            throws ResourceNotFoundException,
            MissingMethodParameterException,
            AuthenticationException,
            AuthorizationException,
            SystemException;

    List evaluateRetrieve(String resourceName, List ids, SecurityContext securityContext)
            throws AuthenticationException,
            AuthorizationException,
            MissingMethodParameterException,
            ResourceNotFoundException,
            SystemException;

    List evaluateRetrieve(String resourceName, List ids, String authHandle, Boolean restAccess)
            throws AuthenticationException,
            AuthorizationException,
            MissingMethodParameterException,
            ResourceNotFoundException,
            SystemException;

    List evaluateMethodForList(String resourceName, String methodName, List argumentList,
                               SecurityContext securityContext)
            throws AuthenticationException,
            AuthorizationException,
            MissingMethodParameterException,
            ResourceNotFoundException,
            SystemException;

    List evaluateMethodForList(String resourceName, String methodName, List argumentList, String authHandle,
                               Boolean restAccess)
            throws AuthenticationException,
            AuthorizationException,
            MissingMethodParameterException,
            ResourceNotFoundException,
            SystemException;

    void touch(SecurityContext securityContext)
            throws SystemException;

    void touch(String authHandle, Boolean restAccess)
            throws SystemException;

}
