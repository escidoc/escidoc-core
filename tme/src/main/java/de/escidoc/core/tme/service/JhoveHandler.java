package de.escidoc.core.tme.service;

import de.escidoc.core.common.exceptions.application.invalid.TmeException;
import de.escidoc.core.common.exceptions.application.invalid.XmlCorruptedException;
import de.escidoc.core.common.exceptions.application.invalid.XmlSchemaValidationException;
import de.escidoc.core.common.exceptions.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.application.security.AuthenticationException;
import de.escidoc.core.common.exceptions.application.security.AuthorizationException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.tme.business.interfaces.JhoveHandlerInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * Implementation for the Jhove Service layer of the TME component.
 *
 * @author Michael Schneider
 */
@Service("service.JhoveHandler")
public class JhoveHandler implements de.escidoc.core.tme.service.interfaces.JhoveHandlerInterface {

    @Autowired
    @Qualifier("business.JhoveHandler")
    private JhoveHandlerInterface business;

    /**
     * Private constructor to prevent initialization.
     */
    protected JhoveHandler() {
    }

    /**
     * See Interface for functional description.
     *
     * @param requests The list of files to examine.
     * @return A list with jhove results for the requested files.
     * @throws AuthenticationException      e
     * @throws AuthorizationException       e
     * @throws XmlCorruptedException        e
     * @throws XmlSchemaValidationException e
     * @throws MissingMethodParameterException
     *                                      e
     * @throws SystemException              e
     * @throws TmeException                 e
     */
    @Override
    public String extract(final String requests) throws AuthenticationException, AuthorizationException,
        XmlCorruptedException, XmlSchemaValidationException, MissingMethodParameterException, SystemException,
        TmeException {
        return business.extract(requests);
    }
}
