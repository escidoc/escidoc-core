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
package de.escidoc.core.om.business.renderer;

import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.util.xml.factory.ContextFoXmlProvider;
import de.escidoc.core.om.business.renderer.interfaces.ContextFoXmlRendererInterface;

import java.util.Map;

/**
 * Renderer that uses Velocity to create a context foxml.
 *
 * @author André Schenk
 */
public class VelocityXmlContextFoXmlRenderer implements ContextFoXmlRendererInterface {

    /**
     * See Interface for functional description.
     */
    @Override
    public String render(final Map<String, Object> values) throws WebserverSystemException {

        return ContextFoXmlProvider.getInstance().getContextFoXml(values);
    }

    /**
     * See Interface for functional description.
     *
     */
    @Override
    public String renderRelsExt(final Map<String, Object> values) throws WebserverSystemException {

        return ContextFoXmlProvider.getInstance().getRelsExt(values);
    }

    /**
     * See Interface for functional description.
     *
     * @see de.escidoc.core.om.business.renderer.interfaces.ContextFoXmlRendererInterface#renderRelsExt(Map)
     */
    @Override
    public String renderDc(final Map<String, Object> values) throws WebserverSystemException {
        return ContextFoXmlProvider.getInstance().getDc(values);
    }

}
