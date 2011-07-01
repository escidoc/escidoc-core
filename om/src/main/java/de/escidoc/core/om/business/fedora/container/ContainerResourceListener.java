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
package de.escidoc.core.om.business.fedora.container;

import de.escidoc.core.common.business.fedora.resources.listener.ResourceListener;
import de.escidoc.core.common.exceptions.application.notfound.ContainerNotFoundException;
import de.escidoc.core.common.exceptions.system.SystemException;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Container cache handler.
 *
 * @author Steffen Wagner
 */
public class ContainerResourceListener extends ContainerHandlerRetrieve {

    private final Collection<ResourceListener> containerListeners = new ArrayList<ResourceListener>();

    private final Collection<ResourceListener> containerMemberListeners = new ArrayList<ResourceListener>();

    /**
     * Register a container listener.
     *
     * @param listener listener which will be added to the list
     */
    public void addContainerListener(final ResourceListener listener) {
        containerListeners.add(listener);
    }

    /**
     * Unregister a container listener.
     *
     * @param listener listener which will be removed from the list
     */
    public void removeContainerListener(final ResourceListener listener) {
        containerListeners.remove(listener);
    }

    /**
     * Register a container listener.
     *
     * @param listener listener which will be added to the list
     */
    public void addContainerMemberListener(final ResourceListener listener) {
        containerMemberListeners.add(listener);
    }

    /**
     * Unregister a container listener.
     *
     * @param listener listener which will be removed from the list
     */
    public void removeContainerMemberListener(final ResourceListener listener) {
        containerMemberListeners.remove(listener);
    }

    /**
     * Notify the listeners that a container was created.
     *
     * @param id      container id
     * @param xmlData complete container XML
     * @throws SystemException One of the listeners threw an exception.
     */
    protected void fireContainerCreated(final String id, final String xmlData) throws SystemException {
        for (final ResourceListener containerListener : this.containerListeners) {
            containerListener.resourceCreated(id, xmlData);
        }
    }

    /**
     * Notify the listeners that a container was deleted.
     *
     * @param id container id
     * @throws SystemException One of the listeners threw an exception.
     */
    protected void fireContainerDeleted(final String id) throws SystemException {
        for (final ResourceListener containerListener : this.containerListeners) {
            containerListener.resourceDeleted(id);
        }
    }

    /**
     * Notify the listeners that a container was modified.
     *
     * @param id container id
     * @throws ContainerNotFoundException Thrown if a container with the provided id does not exist in the framework.
     * @throws SystemException            One of the listeners threw an exception.
     */
    protected void fireContainerModified(final String id) throws ContainerNotFoundException, SystemException {
        setContainer(id);
        final String xml = getContainerXml(getContainer());
        for (final ResourceListener containerListener : this.containerListeners) {
            containerListener.resourceModified(id, xml);
        }
    }

    /**
     * Notify the listeners that a container was modified.
     *
     * @param id      container id
     * @param xmlData complete container XML
     * @throws SystemException One of the listeners threw an exception.
     */
    protected void fireContainerModified(final String id, final String xmlData) throws SystemException {
        for (final ResourceListener containerListener : this.containerListeners) {
            containerListener.resourceModified(id, xmlData);
        }
    }

    /**
     * Notify the listeners that a container-member was modified.
     *
     * @param id member id
     * @throws SystemException One of the listeners threw an exception.
     */
    protected void fireContainerMembersModified(final String id) throws SystemException {

        for (final ResourceListener containerMemberListener : this.containerMemberListeners) {
            containerMemberListener.resourceModified(id, null);
        }
    }
}
