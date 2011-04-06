/*
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the
 * Common Development and Distribution License, Version 1.0 only
 * (the "License").  You may not use this file except in compliance
 * with the License.
 *
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE
 * or http://www.escidoc.de/license.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each
 * file and include the License file at license/ESCIDOC.LICENSE.
 * If applicable, add the following below this CDDL HEADER, with the
 * fields enclosed by brackets "[]" replaced with your own identifying
 * information: Portions Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
 */

/*
 * Copyright 2008 Fachinformationszentrum Karlsruhe Gesellschaft
 * fuer wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Foerderung der Wissenschaft e.V.  
 * All rights reserved.  Use is subject to license terms.
 */
package de.escidoc.core.aa.business;

import de.escidoc.core.aa.business.persistence.Action;
import de.escidoc.core.aa.business.persistence.ActionDaoInterface;
import de.escidoc.core.aa.business.persistence.UnsecuredActionList;
import de.escidoc.core.aa.business.renderer.interfaces.ActionRendererInterface;
import de.escidoc.core.aa.business.stax.handler.UnsecuredActionStaxHandler;
import de.escidoc.core.aa.service.interfaces.ActionHandlerInterface;
import de.escidoc.core.aa.service.interfaces.RoleHandlerInterface;
import de.escidoc.core.common.business.fedora.Utility;
import de.escidoc.core.common.exceptions.application.invalid.InvalidXmlException;
import de.escidoc.core.common.exceptions.application.invalid.XmlCorruptedException;
import de.escidoc.core.common.exceptions.application.invalid.XmlSchemaValidationException;
import de.escidoc.core.common.exceptions.application.notfound.ContextNotFoundException;
import de.escidoc.core.common.exceptions.system.IntegritySystemException;
import de.escidoc.core.common.exceptions.system.SqlDatabaseSystemException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.exceptions.system.TripleStoreSystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.util.xml.XmlUtility;
import de.escidoc.core.common.util.xml.stax.StaxParser;

import java.io.ByteArrayInputStream;

/**
 * Business layer implementation of a handler that manages eSciDoc actions.
 *
 * @author Torsten Tetteroo
 */
public class ActionHandler implements ActionHandlerInterface {

    /**
     * The data access object to access action data.
     */
    private ActionDaoInterface actionDao;

    /**
     * The renderer.
     */
    private ActionRendererInterface renderer;

    private Utility utility;

    /**
     * See Interface for functional description.
     */
    @Override
    public String createUnsecuredActions(final String contextId, final String actions) throws ContextNotFoundException,
        XmlCorruptedException, XmlSchemaValidationException, IntegritySystemException, TripleStoreSystemException,
        SqlDatabaseSystemException, WebserverSystemException {

        utility.checkIsContext(contextId);

        final ByteArrayInputStream in = validateUnsecuredActions(actions);

        final StaxParser sp = new StaxParser(XmlUtility.NAME_UNSECURED_ACTIONS);
        final UnsecuredActionStaxHandler unsecuredActionStaxHandler = new UnsecuredActionStaxHandler();
        sp.addHandler(unsecuredActionStaxHandler);

        try {
            sp.parse(in);
        }
        catch (final InvalidXmlException e) {
            throw new XmlCorruptedException(e);
        }
        catch (final Exception e) {
            final String msg =
                "Unexpected exception in " + getClass().getName() + ".createUnsecuredActions: "
                    + e.getClass().getName();
            throw new WebserverSystemException(msg, e);
        }

        UnsecuredActionList unsecuredActionList = actionDao.retrieveUnsecuredActionList(contextId);
        if (unsecuredActionList == null) {
            unsecuredActionList = new UnsecuredActionList(contextId, unsecuredActionStaxHandler.getUnsecuredActions());
        }
        else {
            unsecuredActionList.setActionIds(unsecuredActionStaxHandler.getUnsecuredActions());
        }
        actionDao.saveOrUpdate(unsecuredActionList);
        return renderer.renderUnsecuredActionList(unsecuredActionList);
    }

    /**
     * See Interface for functional description.
     *
     * @see RoleHandlerInterface #deleteUnsecuredActions(java.lang.String)
     */
    @Override
    public void deleteUnsecuredActions(final String contextId) throws ContextNotFoundException, SystemException,
        TripleStoreSystemException, IntegritySystemException, SqlDatabaseSystemException, WebserverSystemException {

        Utility.getInstance().checkIsContext(contextId);
        final UnsecuredActionList unsecuredActionList = actionDao.retrieveUnsecuredActionList(contextId);

        if (unsecuredActionList == null) {
            // FIXME: UnsecureActionsNotFoundException needed
            throw new SystemException("Nothing to delete");
        }
        actionDao.delete(unsecuredActionList);
    }

    /**
     * See Interface for functional description.
     *
     * @see RoleHandlerInterface #retrieveUnsecuredActions(java.lang.String)
     */
    @Override
    public String retrieveUnsecuredActions(final String contextId) throws ContextNotFoundException,
        TripleStoreSystemException, IntegritySystemException, SqlDatabaseSystemException, WebserverSystemException {

        Utility.getInstance().checkIsContext(contextId);
        UnsecuredActionList unsecuredActionList = actionDao.retrieveUnsecuredActionList(contextId);
        if (unsecuredActionList == null) {
            unsecuredActionList = new UnsecuredActionList(contextId, null);
        }
        return renderer.renderUnsecuredActionList(unsecuredActionList);

    }

    /**
     * Validates data of a unsecured action list.
     *
     * @param xmlData The xml data.
     * @return Returns the xml data in a <code>ByteArrayInputStream</code>.
     * @throws XmlSchemaValidationException Thrown if data in not valid.
     * @throws XmlCorruptedException        Thrown if the XML data cannot be parsed.
     * @throws WebserverSystemException     Thrown in case of any other failure.
     */
    private static ByteArrayInputStream validateUnsecuredActions(final String xmlData) throws XmlCorruptedException,
        WebserverSystemException, XmlSchemaValidationException {

        return XmlUtility.createValidatedByteArrayInputStream(xmlData, XmlUtility.getUnsecuredActionsSchemaLocation());
    }

    /**
     * Injects the data access object to access {@link Action} objects from the database.
     *
     * @param actionDao The dao to set.
     */
    public void setActionDao(final ActionDaoInterface actionDao) {

        this.actionDao = actionDao;
    }

    /**
     * Injects the renderer.
     *
     * @param renderer The renderer to inject.
     */
    public void setRenderer(final ActionRendererInterface renderer) {

        this.renderer = renderer;
    }

    /**
     * Injects the utility.
     *
     * @param utility the {@link Utility} to be injected.
     */
    public void setUtility(final Utility utility) {

        this.utility = utility;
    }

}
