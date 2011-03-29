package de.escidoc.core.om.ejb.interfaces;

import org.springframework.security.context.SecurityContext;

import javax.ejb.EJBLocalObject;
import java.util.Map;

/**
 * Local interface for FedoraDescribeDeviationHandler.
 */
public interface FedoraDescribeDeviationHandlerLocal extends EJBLocalObject {

    String getFedoraDescription(Map parameters, SecurityContext securityContext)
            throws Exception;

    String getFedoraDescription(Map parameters, String authHandle, Boolean restAccess) throws Exception;

}
