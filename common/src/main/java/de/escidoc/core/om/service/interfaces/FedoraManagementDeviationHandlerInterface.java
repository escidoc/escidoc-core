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
package de.escidoc.core.om.service.interfaces;

/**
 * Interface of an fedora deviation handler for management service.
 * 
 * @author MIH
 * 
 * @om
 */
public interface FedoraManagementDeviationHandlerInterface {

    /**
     * Overwrites the Fedora Method-Call export. Variable pid contains uri to
     * resource. Calls Method-mapper with given uri to retrieve object as xml.
     * return xml-string as byte[].
     * 
     * @param pid
     *            uri to the resource.
     * @param format
     *            unused
     * @param context
     *            unused.
     * 
     * @return byte[] byte[] with the fedora-object as escidoc-xml
     * @throws Exception
     *             ex
     * 
     * @om
     */
    byte[] export(
            final String pid, 
            final String format, 
            final String context) throws Exception;

    /**
     * writes the given xml into the cache.
     * 
     * @param pid
     *            uri to the resource.
     * @param xml
     *            xml-representation of the object
     * 
     * @throws Exception
     *             ex
     * 
     * @om
     */
    void cache(final String pid, final String xml) throws Exception;

    /**
     * removes the given pid from the cache.
     * 
     * @param pid
     *            uri to the resource.
     * @throws Exception
     *             ex
     * 
     * @om
     */
    void removeFromCache(final String pid) throws Exception;

}
