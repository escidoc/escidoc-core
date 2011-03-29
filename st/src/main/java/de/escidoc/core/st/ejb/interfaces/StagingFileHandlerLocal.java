package de.escidoc.core.st.ejb.interfaces;

import de.escidoc.core.common.business.fedora.EscidocBinaryContent;
import de.escidoc.core.common.exceptions.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.application.notfound.StagingFileNotFoundException;
import de.escidoc.core.common.exceptions.application.security.AuthenticationException;
import de.escidoc.core.common.exceptions.application.security.AuthorizationException;
import de.escidoc.core.common.exceptions.system.SystemException;
import org.springframework.security.context.SecurityContext;

import javax.ejb.EJBLocalObject;

/**
 * Local interface for StagingFileHandler.
 */
public interface StagingFileHandlerLocal extends EJBLocalObject {

    String create(EscidocBinaryContent binaryContent, SecurityContext securityContext)
            throws MissingMethodParameterException,
            AuthenticationException,
            AuthorizationException,
            SystemException;

    String create(EscidocBinaryContent binaryContent, String authHandle, Boolean restAccess)
            throws MissingMethodParameterException,
            AuthenticationException,
            AuthorizationException,
            SystemException;

    EscidocBinaryContent retrieve(String stagingFileId, SecurityContext securityContext)
            throws StagingFileNotFoundException,
            AuthenticationException,
            AuthorizationException,
            MissingMethodParameterException,
            SystemException;

    EscidocBinaryContent retrieve(String stagingFileId, String authHandle, Boolean restAccess)
            throws StagingFileNotFoundException,
            AuthenticationException,
            AuthorizationException,
            MissingMethodParameterException,
            SystemException;

}
