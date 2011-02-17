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
 * Copyright 2006-2008 Fachinformationszentrum Karlsruhe Gesellschaft
 * fuer wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Foerderung der Wissenschaft e.V.  
 * All rights reserved.  Use is subject to license terms.
 */
package de.escidoc.core.om.business.fedora.item;

import de.escidoc.core.common.business.Constants;
import de.escidoc.core.common.business.PropertyMapKeys;
import de.escidoc.core.common.business.fedora.resources.Container;
import de.escidoc.core.common.exceptions.application.invalid.InvalidStatusException;
import de.escidoc.core.common.exceptions.application.notfound.ItemNotFoundException;
import de.escidoc.core.common.exceptions.application.security.AuthorizationException;
import de.escidoc.core.common.exceptions.application.violated.LockingException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.servlet.invocation.BeanMethod;
import de.escidoc.core.common.servlet.invocation.MethodMapper;
import de.escidoc.core.common.util.service.BeanLocator;
import de.escidoc.core.common.util.service.UserContext;
import de.escidoc.core.common.util.stax.StaxParser;
import de.escidoc.core.common.util.stax.handler.foxml.ComponentIdsInItemFoxmlHandler;

import java.io.ByteArrayInputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

/**
 * 
 * Contains methods pertaining deletion of an item. Is extended at least by
 * FedoraItemHandler.
 * 
 * @author MSC
 * 
 */
public class ItemHandlerDelete extends ItemHandlerCreate {

    /**
     * Removes an item from repository.
     * 
     * @param id
     *            The item ID.
     * 
     * @throws ItemNotFoundException
     *             If the item can not be found in the repository.
     * @throws LockingException
     *             If the item is locked and the current user is not the one who
     *             locked it.
     * @throws InvalidStatusException
     *             If the item can not be deleted because it is released.
     * @throws SystemException
     *             Thrown in case of an internal system error.
     * @throws AuthorizationException
     *             If further needed access rights are not given.
     */
    void remove(final String id) throws ItemNotFoundException,
        LockingException, InvalidStatusException, SystemException,
        AuthorizationException {

        // TODO move precondition checks to service method (? FRS)
        setItem(id);
        checkLocked();
        // check if never released
        final String status =
            getItem().getProperty(PropertyMapKeys.PUBLIC_STATUS);
        if (!(status.equals(Constants.STATUS_PENDING))
            && !(status.equals(Constants.STATUS_IN_REVISION))) {
            throw new InvalidStatusException("Item " + getItem().getId()
                + " is in status " + status + ". Can not delete.");
        }

        // remove member entries referring this
        List<String> containers =
            getTripleStoreUtility().getContainers(getItem().getId());
        for (String container1 : containers) {
            try {
                String parent = container1;
                final Container container = new Container(parent);

                // call removeMember with current user context (access rights)
                String param =
                        "<param last-modification-date=\""
                                + container.getLastModificationDate() + "\"><id>"
                                + getItem().getId() + "</id></param>";

                MethodMapper methodMapper =
                        (MethodMapper) BeanLocator.getBean(
                                "Common.spring.ejb.context",
                                "common.CommonMethodMapper");
                BeanMethod method =
                        methodMapper.getMethod("/ir/container/" + parent
                                + "/members/remove", null, null, "POST", param);
                method
                        .invokeWithProtocol(
                                UserContext.getHandle(),
                                de.escidoc.core.om.business.fedora.deviation.Constants.USE_SOAP_REQUEST_PROTOCOL);
            } catch (InvocationTargetException e) {
                // unpack Exception from reflection API
                try {
                    throw e.getCause();
                } catch (AuthorizationException ee) { // Ignore FindBugs
                    String msg =
                            "Can not delete all member entries for item "
                                    + getItem().getId() + ". item can not be deleted.";
                    throw new AuthorizationException(msg, ee);
                } catch (Throwable ee) { // Ignore FindBugs
                    if (ee instanceof Error) {
                        throw (Error) ee;
                    }
                    String msg =
                            "An error occured removing member entries for item "
                                    + getItem().getId() + ". item can not be deleted.";
                    throw new SystemException(msg, ee); // Ignore FindBugs
                }
            } catch (Exception e) {
                String msg =
                        "An error occured removing member entries for item "
                                + getItem().getId() + ". Container can not be deleted.";
                throw new SystemException(msg, e);
            }
        }

        // delete every component, even those referred from old versions
        final byte[] foxml =
            getFedoraUtility().getObjectFoxml(getItem().getId());
        final ByteArrayInputStream in = new ByteArrayInputStream(foxml);

        final StaxParser sp = new StaxParser();
        final ComponentIdsInItemFoxmlHandler cih =
            new ComponentIdsInItemFoxmlHandler(sp);
        sp.addHandler(cih);
        try {
            sp.parse(in);
            sp.clearHandlerChain();
        }
        catch (final Exception e) {
            throw new WebserverSystemException(e);
        }
        final List<String> componentIds = cih.getComponentIds();
        for (String componentId : componentIds) {
            getFedoraUtility().deleteObject(componentId, false);
        }

        getFedoraUtility().deleteObject(getItem().getId(), true);
    }

}
