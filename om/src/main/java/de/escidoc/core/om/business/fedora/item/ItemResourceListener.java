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

import java.util.ArrayList;
import java.util.Collection;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.escidoc.core.common.business.fedora.resources.listener.ResourceListener;
import de.escidoc.core.common.business.indexing.IndexingHandler;
import de.escidoc.core.common.exceptions.application.notfound.ComponentNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ItemNotFoundException;
import de.escidoc.core.common.exceptions.system.SystemException;

/**
 * Handle the Item within the ResourceCache.
 */
public class ItemResourceListener extends ItemHandlerRetrieve {

    @Autowired
    @Qualifier("common.business.indexing.IndexingHandler")
    private IndexingHandler indexingHandler;

    private final Collection<ResourceListener> itemListeners = new ArrayList<ResourceListener>();

    @PostConstruct
    private void init() {
        addItemListener(indexingHandler);
    }

    /**
     * Register an item listener.
     *
     * @param listener listener which will be added to the list
     */
    private void addItemListener(final ResourceListener listener) {
        itemListeners.add(listener);
    }

    /**
     * Notify the listeners that an item was created.
     *
     * @param id      item id
     * @param xmlData complete item XML
     * @throws SystemException One of the listeners threw an exception.
     */
    protected void fireItemCreated(final String id, final String xmlData) throws SystemException {
        for (final ResourceListener itemListener : this.itemListeners) {
            itemListener.resourceCreated(id, xmlData);
        }
    }

    /**
     * Notify the listeners that an item was modified.
     *
     * @param id      item id
     * @param xmlData complete item XML
     * @throws SystemException One of the listeners threw an exception.
     */
    public void fireItemModified(final String id, final String xmlData) throws SystemException {
        for (final ResourceListener itemListener : this.itemListeners) {
            itemListener.resourceModified(id, xmlData);
        }
    }

    /**
     * Notify the listeners that an item was deleted.
     *
     * @param id item id
     * @throws SystemException One of the listeners threw an exception.
     */
    protected void fireItemDeleted(final String id) throws SystemException {
        for (final ResourceListener itemListener : this.itemListeners) {
            itemListener.resourceDeleted(id);
        }
    }

    /**
     * Notify the listeners that an item was modified.
     *
     * @param ids list of item ids
     * @throws ComponentNotFoundException Thrown if a component of an item with the provided id does not exist in the
     *                                    framework.
     * @throws ItemNotFoundException      Thrown if an item with the provided id does not exist in the framework.
     * @throws SystemException            One of the listeners threw an exception.
     */
    protected void queueItemsModified(final Iterable<String> ids) throws ComponentNotFoundException,
        ItemNotFoundException, SystemException {
        if (this.indexingHandler != null) {
            for (final String id : ids) {
                setItem(id);
                final String xml = render();
                indexingHandler.resourceModified(id, xml);
            }
        }
    }
}
