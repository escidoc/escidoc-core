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
package de.escidoc.core.common.util.security.client;

import de.escidoc.core.common.util.logger.AppLogger;
import org.apache.axis.AxisFault;
import org.apache.axis.MessageContext;
import org.apache.axis.handlers.BasicHandler;

import java.util.Iterator;

/**
 * An default handler for the Axis framework.
 * 
 * @author Bernhard Kraus (Accenture)
 * 
 */
public class AuthHandler extends BasicHandler {

    /**
     * The serial version uid.
     */
    private static final long serialVersionUID = -4770170546756445073L;
    
    private static final AppLogger LOG =
        new AppLogger(AuthHandler.class.getName());

    /**
     * The invoke method of the handler.
     * 
     * @param ctx
     *            The context of the message
     * @throws AxisFault
     *             the exception
     */
    public void invoke(final MessageContext ctx) throws AxisFault {
        Iterator it = ctx.getAllPropertyNames();
        while (it.hasNext()) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("The properties:" + it.next());
            }
        }
    }

}
