/*
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the Common Development and Distribution License, Version 1.0
 * only (the "License"). You may not use this file except in compliance with the License.
 *
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE or http://www.escidoc.de/license. See the License
 * for the specific language governing permissions and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each file and include the License file at
 * license/ESCIDOC.LICENSE. If applicable, add the following below this CDDL HEADER, with the fields enclosed by
 * brackets "[]" replaced with your own identifying information: Portions Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
 *
 * Copyright 2006-2011 Fachinformationszentrum Karlsruhe Gesellschaft fuer wissenschaftlich-technische Information mbH
 * and Max-Planck-Gesellschaft zur Foerderung der Wissenschaft e.V. All rights reserved. Use is subject to license
 * terms.
 */

package de.escidoc.core.aa.business;

import de.escidoc.core.aa.business.persistence.ActionDaoInterface;
import de.escidoc.core.aa.business.persistence.UnsecuredActionList;
import de.escidoc.core.aa.business.renderer.interfaces.ActionRendererInterface;
import de.escidoc.core.aa.business.stax.handler.UnsecuredActionStaxHandler;
import de.escidoc.core.aa.service.interfaces.ActionHandlerInterface;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;

/**
 * Business layer implementation of a handler that manages eSciDoc actions.
 *
 * @author Torsten Tetteroo
 */
@Service("business.ActionHandler")
public class ActionHandler implements ActionHandlerInterface {

    @Autowired
    @Qualifier("persistence.ActionDao")
    private ActionDaoInterface actionDao;

    @Autowired
    @Qualifier("eSciDoc.core.aa.business.renderer.VelocityXmlActionRenderer")
    private ActionRendererInterface renderer;

    @Autowired
    @Qualifier("business.Utility")
    private Utility utility;

    @Autowired
    @Qualifier("common.xml.XmlUtility")
    private XmlUtility xmlUtility;

    /**
     * Protected constructor to prevent instantiation outside of the Spring-context.
     */
    protected ActionHandler() {
    }

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
    public void deleteUnsecuredActions(final String contextId) throws ContextNotFoundException, SystemException {
        this.utility.checkIsContext(contextId);
        final UnsecuredActionList unsecuredActionList = actionDao.retrieveUnsecuredActionList(contextId);

        if (unsecuredActionList == null) {
            // FIXME: UnsecureActionsNotFoundException needed
            throw new SystemException("Nothing to delete");
        }
        this.actionDao.delete(unsecuredActionList);
    }

    /**
     * See Interface for functional description.
     *
     * @see RoleHandlerInterface #retrieveUnsecuredActions(java.lang.String)
     */
    @Override
    public String retrieveUnsecuredActions(final String contextId) throws ContextNotFoundException,
        TripleStoreSystemException, IntegritySystemException, SqlDatabaseSystemException, WebserverSystemException {
        this.utility.checkIsContext(contextId);
        UnsecuredActionList unsecuredActionList = actionDao.retrieveUnsecuredActionList(contextId);
        if (unsecuredActionList == null) {
            unsecuredActionList = new UnsecuredActionList(contextId, null);
        }
        return this.renderer.renderUnsecuredActionList(unsecuredActionList);
    }

    /**
     * Validates data of a unsecured action list.
     *
     * @param xmlData The xml data.
     * @return Returns the xml data in a {@code ByteArrayInputStream}.
     * @throws XmlSchemaValidationException Thrown if data in not valid.
     * @throws XmlCorruptedException        Thrown if the XML data cannot be parsed.
     * @throws WebserverSystemException     Thrown in case of any other failure.
     */
    private ByteArrayInputStream validateUnsecuredActions(final String xmlData) throws XmlCorruptedException,
        WebserverSystemException, XmlSchemaValidationException {

        return xmlUtility.createValidatedByteArrayInputStream(xmlData, XmlUtility.getUnsecuredActionsSchemaLocation());
    }

}
