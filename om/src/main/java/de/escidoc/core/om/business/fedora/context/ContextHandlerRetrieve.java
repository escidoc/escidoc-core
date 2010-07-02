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
package de.escidoc.core.om.business.fedora.context;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.escidoc.core.common.business.Constants;
import de.escidoc.core.common.business.fedora.TripleStoreUtility;
import de.escidoc.core.common.exceptions.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.application.notfound.AdminDescriptorNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ContextNotFoundException;
import de.escidoc.core.common.exceptions.application.security.AuthorizationException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.util.xml.XmlUtility;

/**
 * Context Retrieve Handler.
 * 
 * @author SWA
 * 
 */
public class ContextHandlerRetrieve extends ContextHandlerBase {

    private static final String IS_ROOT_RESOURCES = "isRootResources";

    /**
     * Get XML representation of Context.
     * 
     * @param contextHandler
     *            FedoraContextHandler
     * @return XML representation of Context.
     * @throws SystemException
     *             If anything fails.
     */
    protected String getContextXml(final FedoraContextHandler contextHandler)
        throws SystemException {

        return getRenderer().render(contextHandler);
    }

    /**
     * Get XML representation of Context Properties.
     * 
     * @param contextHandler
     *            FedoraContextHandler
     * @return XML representation of Context Properties.
     * @throws SystemException
     *             If anything fails.
     */
    protected String getPropertiesXml(final FedoraContextHandler contextHandler)
        throws SystemException {

        return getRenderer().renderProperties(contextHandler);
    }

    /**
     * Get XML representation of Context Resources.
     * 
     * @param contextHandler
     *            FedoraContextHandler
     * @return XML representation of Context Resources.
     * @throws SystemException
     *             If anything fails.
     */
    protected String getResourcesXml(final FedoraContextHandler contextHandler)
        throws SystemException {

        return getRenderer().renderResources(contextHandler);
    }

    /**
     * Get XML representation of filtered Context list.
     * 
     * @param contextHandler
     *            FedoraContextHandler
     * @param filter
     *            Filter parameter in XML structure.
     * @return XML representation of list of Contexts.
     * @throws SystemException
     *             If anything fails.
     */
    protected String getContextsXml(
        final FedoraContextHandler contextHandler, final String filter)
        throws SystemException {

        return getRenderer().renderContexts(contextHandler, filter);
    }

    /**
     * Get XML representation of Context member list.
     * 
     * @param contextHandler
     *            FedoraContextHandler
     * @param filterXml
     *            Filter parameter in XML structure.
     * @return XML representation of list of members.
     * @throws ContextNotFoundException
     *             If context is not found.
     * @throws SystemException
     *             If anything fails.
     */
    protected String getMemberListXml(
        final FedoraContextHandler contextHandler, final String filterXml)
        throws ContextNotFoundException, SystemException,
        AuthorizationException {

        String id = contextHandler.getContext().getId();

        final TripleStoreUtility tsu = TripleStoreUtility.getInstance();
        if ((!tsu.exists(id))
            || (!(Constants.CONTEXT_OBJECT_TYPE.equals(tsu.getObjectType(id))))) {
            throw new ContextNotFoundException("Context with id " + id
                + " not found!");
        }

        Map filterMap = XmlUtility.getFilterMap(filterXml);
        List<String> memberList = null;
        try {
            memberList = tsu.getContextMemberList(id, filterMap, null);
        }
        catch (MissingMethodParameterException e) {
            XmlUtility.handleUnexpectedStaxParserException("", e);
        }

        return getRenderer().renderMemberList(contextHandler, memberList);
    }

    /**
     * Get list with references of member.
     * 
     * @param contextHandler
     *            FedoraContextHandler
     * @param filter
     *            Filter parameter in XML structure.
     * @return XML representation of reference list of members.
     * @throws ContextNotFoundException
     *             If context is not found.
     * @throws SystemException
     *             If anything fails.
     */
    protected String getMemberRefListXml(
        final FedoraContextHandler contextHandler, final String filterXml)
        throws ContextNotFoundException, SystemException,
        AuthorizationException {

        String id = contextHandler.getContext().getId();

        final TripleStoreUtility tsu = TripleStoreUtility.getInstance();
        if ((!tsu.exists(id))
            || (!(Constants.CONTEXT_OBJECT_TYPE.equals(tsu.getObjectType(id))))) {
            throw new ContextNotFoundException("Context with id " + id
                + " not found!");
        }

        Map filterMap = XmlUtility.getFilterMap(filterXml);
        List<String> memberList = null;
        try {
            memberList = tsu.getContextMemberList(id, filterMap, null);
        }
        catch (MissingMethodParameterException e) {
            XmlUtility.handleUnexpectedStaxParserException("", e);
        }

        return getRenderer().renderMemberRefList(contextHandler, memberList);
    }

    /**
     * Get the XML representation of all admin-descriptors.
     * 
     * @param contextHandler
     *            FedoraContextHandler
     * @return XML representation of all admin-descriptors.
     * @throws ContextNotFoundException
     *             If context is not found.
     * @throws SystemException
     *             If anything fails.
     */
    protected String getAdminDescriptorsXml(
        final FedoraContextHandler contextHandler)
        throws ContextNotFoundException, SystemException {

        Map<String, Object> values = new HashMap<String, Object>();
        values.put(IS_ROOT_RESOURCES, true);

        return (getRenderer().renderAdminDescriptors(contextHandler, values));
    }

    /**
     * Get the XMl representation of one admin-descriptor, selected by name.
     * 
     * @param contextHandler
     *            FedoraContextHandler
     * @param name
     *            Name of admin-descriptor.
     * @param isRoot
     *            Set true if representation is root element.
     * @return XML representation of admin-descriptor, selected by name.
     * @throws ContextNotFoundException
     *             If context is not found.
     * @throws SystemException
     *             If anything fails.
     */
    protected String getAdminDescriptorXml(
        final FedoraContextHandler contextHandler, final String name,
        final boolean isRoot) throws ContextNotFoundException, SystemException,
        AdminDescriptorNotFoundException {
        String adminDescriptor =
            getRenderer().renderAdminDescriptor(contextHandler, name,
                getContext().getAdminDescriptor(name), isRoot);
        if (adminDescriptor.length() == 0) {
            throw new AdminDescriptorNotFoundException(
                "Admin-descriptor with a name " + name
                    + " does not exist in the context wirh id "
                    + getContext().getId());
        }
        return adminDescriptor;
    }
}
