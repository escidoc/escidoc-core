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
package de.escidoc.core.om.service;

import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.util.xml.XmlUtility;
import de.escidoc.core.om.service.interfaces.FedoraRestDeviationHandlerInterface;

import org.escidoc.core.utils.io.EscidocBinaryContent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Map;

/**
 * Fedora access deviation handler that provides interface for fedoragsearch and delegates to the resource handlers with
 * providing the user information.
 * <p/>
 * <p/>
 * Security note: This handler should not be intercepted for authorization, as it delegates to secured resource handlers
 * with providing the original user information.
 *
 * @author Michael Hoppe
 */
@Service("service.FedoraRestDeviationHandler")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class FedoraRestDeviationHandler implements FedoraRestDeviationHandlerInterface {

    @Autowired
    @Qualifier("business.FedoraRestDeviationHandler")
    private de.escidoc.core.om.business.interfaces.FedoraRestDeviationHandlerInterface handler;

    /**
     * Protected constructor to prevent instantiation outside of the Spring-context.
     */
    protected FedoraRestDeviationHandler() {
    }

    /**
     * @param pid        unused.
     * @param dsID       uri to component-content
     * @param parameters REST-GET-Parameters.
     * @return EscidocBinaryContent escidocBinaryContent
     * @throws SystemException ex
     * @see de.escidoc.core.om.service.interfaces .FedorarestDeviationHandlerInterface #getDatastreamDissemination(
     *      java.lang.String,java.lang.String,java.lang.String)
     */
    @Override
    public EscidocBinaryContent getDatastreamDissemination(
        final String pid, final String dsID, final Map<String, String[]> parameters) throws SystemException {
        try {
            return handler.getDatastreamDissemination(pid, URLDecoder.decode(dsID, XmlUtility.CHARACTER_ENCODING),
                parameters);
        }
        catch (UnsupportedEncodingException e) {
            throw new SystemException(e);
        }
    }

    /**
     * @param pid        uri to the resource.
     * @param parameters REST-GET-Parameters.
     * @return String String with the fedora-object as escidoc-xml
     * @throws SystemException ex
     * @see de.escidoc.core.om.service.interfaces .FedorarestDeviationHandlerInterface
     *      #export(java.lang.String,java.lang.String,java.lang.String)
     */
    @Override
    public String export(final String pid, final Map<String, String[]> parameters) throws SystemException {
        return handler.export(pid, parameters);
    }

    /**
     * writes the given xml into the cache.
     *
     * @param pid uri to the resource.
     * @param content EscidocBinaryContent
     * @throws SystemException ex
     */
    @Override
    public void cache(final String pid, final EscidocBinaryContent content) throws SystemException {
        handler.cache(pid, content);
    }

    /**
     * removes the given pid from the cache.
     *
     * @param pid uri to the resource.
     * @throws SystemException ex
     */
    @Override
    public void removeFromCache(final String pid) throws SystemException {
        handler.removeFromCache(pid);
    }

    /**
     * retrieves the given pid not from cache.
     *
     * @param pid uri to the resource.
     * @return EscidocBinaryContent with the object
     * @throws SystemException ex
     */
    @Override
    public EscidocBinaryContent retrieveUncached(final String pid) throws SystemException {
        return handler.retrieveUncached(pid);
    }

}
