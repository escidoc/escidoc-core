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

import de.escidoc.core.common.business.fedora.datastream.Datastream;
import de.escidoc.core.common.exceptions.application.security.AuthorizationException;
import de.escidoc.core.common.exceptions.system.EncodingSystemException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.om.business.fedora.context.FedoraContextHandler;

import java.util.List;
import java.util.Map;

/**
 * Interface of a Context renderer.
 *
 * @author Steffen Wagner
 */
public interface ContextRendererInterface {

    /**
     * Gets the representation of an Context.
     *
     * @param contextHandler The Context to render.
     * @return Returns the XML representation of the Context.
     * @throws SystemException Thrown in case of an internal error.
     */
    String render(final FedoraContextHandler contextHandler) throws SystemException;

    /**
     * Gets the representation of the sub resource {@code properties} of Context.
     *
     * @param contextHandler The Context to render.
     * @return Returns the XML representation of the sub resource {@code properties} of a context.
     * @throws WebserverSystemException Thrown in case of an internal error.
     */
    String renderProperties(final FedoraContextHandler contextHandler) throws WebserverSystemException;

    /**
     * Gets the representation of the sub resource {@code resources} of a Context.
     *
     * @param contextHandler The FedoraContextHandler to render.
     * @return Returns the XML representation of the sub resource {@code resources} a Context.
     * @throws WebserverSystemException Thrown in case of an internal error.
     */
    String renderResources(final FedoraContextHandler contextHandler) throws WebserverSystemException;

    /**
     * Render all AdminDescriptors as full list.
     *
     * @param contextHandler The FedoraContextHandler.
     * @param values         HashMap with Renderer values.
     * @return The XML representation of all admin-descriptors of the Context.
     * @throws SystemException Thrown in case of an internal error.
     */
    String renderAdminDescriptors(final FedoraContextHandler contextHandler, Map<String, Object> values)
        throws SystemException;

    /**
     * Get the XML representation of one admin-descriptor datastream.
     *
     * @param contextHandler The FedoraContextHandler to render.
     * @param name           Name of the admin-descriptor (unique).
     * @param admDesc        The datastream of the admin-descriptor.
     * @param isRoot         Set true if render with XML root element.
     * @return Returns the XML representation of the sub resource {@code admin-descriptor} of the Context.
     * @throws EncodingSystemException  Thrown if transforming of datastream to required encoding fails.
     * @throws WebserverSystemException Thrown in case of an internal error.
     */
    String renderAdminDescriptor(
        final FedoraContextHandler contextHandler, final String name, final Datastream admDesc, final boolean isRoot)
        throws EncodingSystemException, WebserverSystemException;

    /**
     * Gets the representation of a filtered member list of Context.
     *
     * @param contextHandler The FedoraContextHandler.
     * @param memberList     List of context members.
     * @return Returns the XML representation of a filtered Context list.
     * @throws AuthorizationException Thrown if access to origin Item is restricted.
     * @throws SystemException        Thrown in case of an internal error.
     */
    String renderMemberList(final FedoraContextHandler contextHandler, final List<String> memberList)
        throws SystemException, AuthorizationException;

    /**
     * Gets the representation of a filtered member reference list of Context.
     *
     * @param contextHandler The FedoraContextHandler.
     * @param memberList     List of context members.
     * @return Returns the XML representation of a filtered Context list.
     * @throws AuthorizationException Thrown if access to origin Item is restricted.
     * @throws SystemException        Thrown in case of an internal error.
     */
    String renderMemberRefList(final FedoraContextHandler contextHandler, final List<String> memberList)
        throws SystemException, AuthorizationException;

}
