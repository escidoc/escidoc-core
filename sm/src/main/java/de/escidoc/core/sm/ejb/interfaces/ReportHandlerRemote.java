package de.escidoc.core.sm.ejb.interfaces;

import de.escidoc.core.common.exceptions.application.invalid.InvalidSqlException;
import de.escidoc.core.common.exceptions.application.invalid.XmlCorruptedException;
import de.escidoc.core.common.exceptions.application.invalid.XmlSchemaValidationException;
import de.escidoc.core.common.exceptions.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.application.notfound.ReportDefinitionNotFoundException;
import de.escidoc.core.common.exceptions.application.security.AuthenticationException;
import de.escidoc.core.common.exceptions.application.security.AuthorizationException;
import de.escidoc.core.common.exceptions.system.SystemException;
import org.springframework.security.context.SecurityContext;

import javax.ejb.EJBObject;
import java.rmi.RemoteException;

/**
 * Remote interface for ReportHandler.
 */
public interface ReportHandlerRemote extends EJBObject {

    String retrieve(String xml, SecurityContext securityContext)
            throws AuthenticationException,
            AuthorizationException,
            XmlCorruptedException,
            XmlSchemaValidationException,
            ReportDefinitionNotFoundException,
            MissingMethodParameterException,
            InvalidSqlException,
            SystemException, RemoteException;

    String retrieve(String xml, String authHandle, Boolean restAccess)
            throws AuthenticationException,
            AuthorizationException,
            XmlCorruptedException,
            XmlSchemaValidationException,
            ReportDefinitionNotFoundException,
            MissingMethodParameterException,
            InvalidSqlException,
            SystemException, RemoteException;

}
