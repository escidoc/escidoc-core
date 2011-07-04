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
package de.escidoc.core.om.business.renderer.interfaces;

import de.escidoc.core.common.exceptions.application.notfound.ComponentNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ItemNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.MdRecordNotFoundException;
import de.escidoc.core.common.exceptions.system.EncodingSystemException;
import de.escidoc.core.common.exceptions.system.FedoraSystemException;
import de.escidoc.core.common.exceptions.system.IntegritySystemException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.exceptions.system.TripleStoreSystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;

import java.util.List;

/**
 * Interface of an item renderer.
 *
 * @author Michael Schneider
 */
public interface ItemRendererInterface {

    /**
     * Gets the representation of an item.
     *
     * @return Returns the XML representation of the item.
     * @throws ItemNotFoundException      Thrown if Item could not be found.
     * @throws ComponentNotFoundException Thrown if Component of Item could not be found.
     * @throws SystemException            Thrown in case of an internal error.
     */
    String render() throws SystemException, ItemNotFoundException, ComponentNotFoundException;

    /**
     * Gets the representation of the sub resource {@code properties} of an item.
     *
     * @return Returns the XML representation of the sub resource {@code properties} of an item.
     * @throws ItemNotFoundException    Thrown if Item could not be found.
     * @throws WebserverSystemException Thrown in case of an internal error.
     * @throws de.escidoc.core.common.exceptions.system.TripleStoreSystemException
     */
    String renderProperties() throws ItemNotFoundException, SystemException;

    String renderMdRecords(boolean isRoot) throws WebserverSystemException, EncodingSystemException,
        FedoraSystemException, IntegritySystemException, TripleStoreSystemException;

    /**
     * @param name
     * @param isOrigin
     * @param isRoot
     * @return XML representation of md-record
     * @throws de.escidoc.core.common.exceptions.system.WebserverSystemException
     * @throws de.escidoc.core.common.exceptions.application.notfound.MdRecordNotFoundException
     * @throws de.escidoc.core.common.exceptions.system.TripleStoreSystemException
     * @throws de.escidoc.core.common.exceptions.system.IntegritySystemException
     * @throws de.escidoc.core.common.exceptions.system.EncodingSystemException
     */
    String renderMdRecord(String name, boolean isOrigin, boolean isRoot) throws WebserverSystemException,
        IntegritySystemException, EncodingSystemException, MdRecordNotFoundException, TripleStoreSystemException;

    /**
     * @param isRoot
     * @return XML representation of Components list.
     * @throws ComponentNotFoundException Thrown if Component of Item could not be found.
     * @throws de.escidoc.core.common.exceptions.system.WebserverSystemException
     * @throws de.escidoc.core.common.exceptions.system.TripleStoreSystemException
     * @throws de.escidoc.core.common.exceptions.system.SystemException
     */
    String renderComponents(final boolean isRoot) throws SystemException, ComponentNotFoundException;

    String renderComponent(final String componentId, final boolean isRoot) throws SystemException,
        ComponentNotFoundException;

    String renderRelations() throws SystemException;

    /**
     * Gets the representation of the sub resource {@code resources} of an item.
     *
     * @return Returns the XML representation of the sub resource {@code resources} of an item.
     * @throws WebserverSystemException Thrown in case of an internal error.
     */
    String renderResources() throws WebserverSystemException;

    /**
     * Gets the representation of the virtual resource {@code items}.
     *
     * @param items The list of items.
     * @return Returns the XML representation of the virtual sub resource {@code items} of an item.
     * @throws SystemException            Thrown in case of an internal error.
     * @throws ComponentNotFoundException Thrown if Component of Item could not be found.
     */
    String renderItems(final List<String> items) throws SystemException, ComponentNotFoundException;

}
