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
import de.escidoc.core.common.exceptions.system.IntegritySystemException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.exceptions.system.TripleStoreSystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.exceptions.system.XmlParserSystemException;
import de.escidoc.core.common.util.service.UserContext;

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
    protected void fireContainerCreated(final String id, final String xmlData) throws SystemException,
        WebserverSystemException {
        final String restXml;
        final String soapXml;

        if (UserContext.isRestAccess()) {
            restXml = xmlData;
            soapXml = getAlternateForm(id);
        }
        else {
            restXml = getAlternateForm(id);
            soapXml = xmlData;
        }
        for (final ResourceListener containerListener : this.containerListeners) {
            containerListener.resourceCreated(id, restXml, soapXml);
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
    protected void fireContainerModified(final String id) throws ContainerNotFoundException, SystemException,
        WebserverSystemException, TripleStoreSystemException, IntegritySystemException, XmlParserSystemException {

        setContainer(id);
        final String soapXml;
        final String restXml;
        if (UserContext.isRestAccess()) {
            restXml = getContainerXml(getContainer());
            soapXml = getAlternateForm(id);
        }
        else {
            restXml = getAlternateForm(id);
            soapXml = getContainerXml(getContainer());
        }
        for (final ResourceListener containerListener : this.containerListeners) {
            containerListener.resourceModified(id, restXml, soapXml);
        }
    }

    /**
     * Notify the listeners that a container was modified.
     *
     * @param id      container id
     * @param xmlData complete container XML
     * @throws SystemException One of the listeners threw an exception.
     */
    protected void fireContainerModified(final String id, final String xmlData) throws SystemException,
        WebserverSystemException {
        final String restXml;
        final String soapXml;

        if (UserContext.isRestAccess()) {
            restXml = xmlData;
            soapXml = getAlternateForm(id);
        }
        else {
            restXml = getAlternateForm(id);
            soapXml = xmlData;
        }
        for (final ResourceListener containerListener : this.containerListeners) {
            containerListener.resourceModified(id, restXml, soapXml);
        }
    }

    /**
     * Notify the listeners that a container-member was modified.
     *
     * @param id member id
     * @throws SystemException One of the listeners threw an exception.
     */
    protected void fireContainerMembersModified(final String id) throws SystemException {
        final String restXml = null;
        final String soapXml = null;

        for (final ResourceListener containerMemberListener : this.containerMemberListeners) {
            containerMemberListener.resourceModified(id, restXml, soapXml);
        }
    }

    /**
     * Get the alternate form of a container representation. If the current request came in via REST, then the SOAP form
     * will be returned here and vice versa.
     *
     * @param id container id
     * @return alternate form of the container
     * @throws SystemException An internal error occurred.
     */
    private String getAlternateForm(final String id) throws SystemException, WebserverSystemException {
        String result = null;
        final boolean isRestAccess = UserContext.isRestAccess();

        try {
            if (isRestAccess) {
                UserContext.setRestAccess(false);
                result = getContainerXml(getContainer());
            }
            else {
                UserContext.setRestAccess(true);
                result = getContainerXml(getContainer());
            }
        }
        catch (final WebserverSystemException e) {
            throw new SystemException(e);
        }
        catch (final Exception e) {
            // should not happen here
            throw new SystemException(e);
        }
        finally {
            UserContext.setRestAccess(isRestAccess);
        }
        return result;
    }

}
