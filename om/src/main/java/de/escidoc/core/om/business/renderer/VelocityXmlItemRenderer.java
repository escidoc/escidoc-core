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
/**
 *
 */
package de.escidoc.core.om.business.renderer;

import de.escidoc.core.common.business.fedora.resources.GenericResource;

import java.util.List;

/**
 * @author Frank Schwichtenberg
 * @deprecated Item renderer interface is implemented by item retrieve handler.
 */
@Deprecated
public class VelocityXmlItemRenderer {

    /*
     * (non-Javadoc)
     * 
     * @see de.escidoc.core.om.business.renderer.interfaces.ItemRendererInterface#renderComponent(de.escidoc.core.common.business.fedora.resources.Item)
     */
    public String renderComponent(final GenericResource item) {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.escidoc.core.om.business.renderer.interfaces.ItemRendererInterface#renderComponents(de.escidoc.core.common.business.fedora.resources.Item)
     */

    /*
     * (non-Javadoc)
     * 
     * @see de.escidoc.core.om.business.renderer.interfaces.ItemRendererInterface#renderItemRefs(java.util.List)
     */
    public String renderItemRefs(final List<String> itemRefs) {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.escidoc.core.om.business.renderer.interfaces.ItemRendererInterface#renderItems(java.util.List)
     */
    public String renderItems(final List<String> items) {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.escidoc.core.om.business.renderer.interfaces.ItemRendererInterface#renderProperties(de.escidoc.core.common.business.fedora.resources.Item)
     */
    public String renderProperties(final GenericResource item) {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.escidoc.core.om.business.renderer.interfaces.ItemRendererInterface#renderRelations(de.escidoc.core.common.business.fedora.resources.Item)
     */
    public String renderRelations(final GenericResource item) {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.escidoc.core.om.business.renderer.interfaces.ItemRendererInterface#renderResources(de.escidoc.core.common.business.fedora.resources.Item)
     */
    public String renderResources(final GenericResource item) {
        // TODO Auto-generated method stub
        return null;
    }

}
