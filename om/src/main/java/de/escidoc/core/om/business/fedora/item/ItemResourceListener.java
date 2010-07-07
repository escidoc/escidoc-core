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

import java.util.Collection;
import java.util.List;
import java.util.Vector;

import de.escidoc.core.common.business.fedora.resources.ResourceType;
import de.escidoc.core.common.business.fedora.resources.interfaces.ResourceCacheInterface;
import de.escidoc.core.common.business.fedora.resources.listener.ResourceListener;
import de.escidoc.core.common.business.indexing.IndexingHandler;
import de.escidoc.core.common.business.interfaces.RecacherInterface;
import de.escidoc.core.common.exceptions.application.notfound.ComponentNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ItemNotFoundException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.util.service.BeanLocator;
import de.escidoc.core.common.util.service.UserContext;

/**
 * Handle the Item within the ResourceCache.
 * 
 * @author
 * 
 */
public class ItemResourceListener extends ItemHandlerRetrieve {

    private IndexingHandler indexingHandler = null;

    private ResourceCacheInterface itemCache = null;

    private final List<ResourceListener> itemListeners =
        new Vector<ResourceListener>();

    /**
     * Returns the item cache.
     * 
     * @return item cache.
     * 
     * @throws WebserverSystemException
     *             Thrown if a framework internal error occurs.
     */
    public ResourceCacheInterface getDbResourceCache()
        throws WebserverSystemException {
        if (itemCache == null) {
            itemCache =
                (ResourceCacheInterface) BeanLocator.getBean(
                    BeanLocator.AA_FACTORY_ID, "item.DbItemCache");
            addItemListener(itemCache);
        }
        return itemCache;
    }

    /**
     * Injects the indexing handler.
     * 
     * @spring.property ref="common.business.indexing.IndexingHandler"
     * @param indexingHandler
     *            The indexing handler.
     */
    public void setIndexingHandler(final IndexingHandler indexingHandler) {
        this.indexingHandler = indexingHandler;
        addItemListener(indexingHandler);
    }

    /**
     * Register an item listener.
     * 
     * @param listener
     *            listener which will be added to the list
     */
    private void addItemListener(final ResourceListener listener) {
        itemListeners.add(listener);
    }

    /**
     * Notify the listeners that an item was created.
     * 
     * @param id
     *            item id
     * @param xmlData
     *            complete item XML
     * 
     * @throws SystemException
     *             One of the listeners threw an exception.
     */
    protected void fireItemCreated(final String id, final String xmlData)
        throws SystemException {
        String restXml = null;
        String soapXml = null;

        if (UserContext.isRestAccess()) {
            restXml = xmlData;
            soapXml = getAlternateForm(id);
        }
        else {
            restXml = getAlternateForm(id);
            soapXml = xmlData;
        }
        for (int index = 0; index < itemListeners.size(); index++) {
            (itemListeners.get(index)).resourceCreated(id, restXml, soapXml);
        }
    }

    /**
     * Notify the listeners that an item was modified.
     * 
     * @param id
     *            item id
     * 
     * @throws ComponentNotFoundException
     *             Thrown if a component of an item with the provided id does
     *             not exist in the framework.
     * @throws ItemNotFoundException
     *             Thrown if an item with the provided id does not exist in the
     *             framework.
     * @throws SystemException
     *             One of the listeners threw an exception.
     */
    protected void fireItemModified(final String id)
        throws ComponentNotFoundException, ItemNotFoundException,
        SystemException {
        String restXml = null;
        String soapXml = null;

        setItem(id);
        if (UserContext.isRestAccess()) {
            restXml = render();
            soapXml = getAlternateForm(id);
        }
        else {
            restXml = getAlternateForm(id);
            soapXml = render();
        }
        for (int index = 0; index < itemListeners.size(); index++) {
            (itemListeners.get(index)).resourceModified(id, restXml, soapXml);
        }
    }

    /**
     * Notify the listeners that an item was modified.
     * 
     * @param id
     *            item id
     * @param xmlData
     *            complete item XML
     * 
     * @throws SystemException
     *             One of the listeners threw an exception.
     */
    public void fireItemModified(final String id, final String xmlData)
        throws SystemException {
        String restXml = null;
        String soapXml = null;

        if (UserContext.isRestAccess()) {
            restXml = xmlData;
            soapXml = getAlternateForm(id);
        }
        else {
            restXml = getAlternateForm(id);
            soapXml = xmlData;
        }
        for (int index = 0; index < itemListeners.size(); index++) {
            (itemListeners.get(index)).resourceModified(id, restXml, soapXml);
        }
    }

    /**
     * Notify the listeners that an item was deleted.
     * 
     * @param id
     *            item id
     * 
     * @throws SystemException
     *             One of the listeners threw an exception.
     */
    protected void fireItemDeleted(final String id) throws SystemException {
        for (int index = 0; index < itemListeners.size(); index++) {
            (itemListeners.get(index)).resourceDeleted(id);
        }
    }

    /**
     * Get the alternate form of an item representation. If the current request
     * came in via REST, then the SOAP form will be returned here and vice
     * versa.
     * 
     * @param id
     *            item id
     * 
     * @return alternate form of the item
     * @throws SystemException
     *             An internal error occurred.
     */
    private String getAlternateForm(final String id) throws SystemException {
        String result = null;
        boolean isRestAccess = UserContext.isRestAccess();

        try {
            if (isRestAccess) {
                UserContext.setRestAccess(false);
                result = render();
            }
            else {
                UserContext.setRestAccess(true);
                result = render();
            }
        }
        catch (WebserverSystemException e) {
            throw new SystemException(e);
        }
        catch (Exception e) {
            // should not happen here
            throw new SystemException(e);
        }
        finally {
            UserContext.setRestAccess(isRestAccess);
        }
        return result;
    }

    /**
     * Notify the listeners that an item was modified.
     * 
     * @param ids
     *            list of item ids
     * 
     * @throws ComponentNotFoundException
     *             Thrown if a component of an item with the provided id does
     *             not exist in the framework.
     * @throws ItemNotFoundException
     *             Thrown if an item with the provided id does not exist in the
     *             framework.
     * @throws SystemException
     *             One of the listeners threw an exception.
     */
    protected void queueItemsModified(final Collection<String> ids)
        throws ComponentNotFoundException, ItemNotFoundException,
        SystemException {
        try {
            RecacherInterface recacher =
                (RecacherInterface) BeanLocator.getBean(
                    BeanLocator.ADM_FACTORY_ID, "admin.Recacher");

            recacher.queueIds(ResourceType.ITEM, ids);
        }
        catch (Exception e) {
            throw new SystemException(e);
        }
        if (indexingHandler != null) {
            for (String id : ids) {
                String restXml = null;
                String soapXml = null;

                setItem(id);
                if (UserContext.isRestAccess()) {
                    restXml = render();
                    soapXml = getAlternateForm(id);
                }
                else {
                    restXml = getAlternateForm(id);
                    soapXml = render();
                }
                indexingHandler.resourceModified(id, restXml, soapXml);
            }
        }
    }
}
