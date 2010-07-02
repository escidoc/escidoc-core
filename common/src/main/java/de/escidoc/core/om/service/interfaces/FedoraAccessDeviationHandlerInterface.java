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

import de.escidoc.core.common.business.fedora.MIMETypedStream;


/**
 * Interface of an fedora deviation handler.
 * 
 * @author MIH
 * 
 * @om
 */
public interface FedoraAccessDeviationHandlerInterface {

    /**
     * Overwrites the Fedora Method-Call getDatastreamDissemination. Variable
     * dsID contains uri to component-content . Calls Method-mapper with given
     * uri to retrieve content as byte[]. Fill MIMETypedStream with byte[] and
     * mime-type.
     * 
     * @param pid
     *            unused.
     * @param dsID
     *            uri to component-content
     * @param asOfDateTime
     *            unused.
     * 
     * @return MIMETypedStream mimeTypedStream
     * @throws Exception
     *             ex
     * 
     * @om
     */
    MIMETypedStream getDatastreamDissemination(
        String pid, String dsID, String asOfDateTime) throws Exception;

}
