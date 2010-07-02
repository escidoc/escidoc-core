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
package de.escidoc.core.aa.business.stax.handler;

import java.util.ArrayList;
import java.util.Collection;

import com.sun.xacml.EvaluationCtx;
import com.sun.xacml.attr.StringAttribute;

import de.escidoc.core.common.business.aa.authorisation.AttributeIds;
import de.escidoc.core.common.util.xml.XmlUtility;
import de.escidoc.core.common.util.xml.stax.events.EndElement;
import de.escidoc.core.common.util.xml.stax.events.StartElement;

/**
 * Stax handler implementation that handles the attributes that have to be
 * fetched from an item Xml representation.<br>
 * This handler extracts the attributes ...:item:component,
 * ...:item:version-status, and ...:item:version-status. The attributes found
 * are stored in the <code>RequestAttributesCache</code>.
 * 
 * @author TTE
 * @aa
 */
public class ItemStaxHandler extends AbstractResourceAttributeStaxHandler {

    private final Collection<StringAttribute> componentIds =
        new ArrayList<StringAttribute>();

    /**
     * The constructor.
     * 
     * @param ctx
     *            The <code>EvaluationCtx</code> for that the item xml
     *            representation shall be parsed. Found attributes are cached
     *            using this context as part of the key.
     * @param resourceId
     *            The id of the item resource. Used as part of the cache key.
     */
    public ItemStaxHandler(final EvaluationCtx ctx, final String resourceId) {

        super(ctx, resourceId, AttributeIds.URN_ITEM_MODIFIED_BY_ATTR,
            AttributeIds.URN_ITEM_PUBLIC_STATUS_ATTR,
            AttributeIds.URN_ITEM_VERSION_STATUS_ATTR);
    }

    // CHECKSTYLE:JAVADOC-OFF

    /**
     * See Interface for functional description.
     * 
     * @param element
     * @return
     * @throws Exception
     * @see de.escidoc.core.common.util.xml.stax.handler.DefaultHandler
     *      #startElement
     *      (de.escidoc.core.common.util.xml.stax.events.StartElement)
     * @aa
     */
    @Override
    public StartElement startElement(final StartElement element)
        throws Exception {

        super.startElement(element);
        if (isNotReady() && !isInMetadata()) {

            final String localName = element.getLocalName();
            if (XmlUtility.NAME_COMPONENT.equals(localName)) {
                componentIds.add(new StringAttribute(XmlUtility
                    .getIdFromStartElement(element)));
            }
            else if (XmlUtility.NAME_LOCK_OWNER.equals(localName)) {
                cacheAttribute(AttributeIds.URN_ITEM_LOCK_OWNER_ATTR,
                    XmlUtility.getIdFromStartElement(element));
            }
        }

        return element;
    }

    /**
     * See Interface for functional description.
     * 
     * @param element
     * @return
     * @throws Exception
     * @see de.escidoc.core.common.util.xml.stax.handler.DefaultHandler
     *      #endElement(de.escidoc.core.common.util.xml.stax.events.EndElement)
     * @aa
     */
    @Override
    public EndElement endElement(final EndElement element) throws Exception {

        super.endElement(element);
        if (isNotReady() && !isInMetadata()) {

            if (XmlUtility.NAME_COMPONENTS.equals(element.getLocalName())) {
                cacheAttribute(AttributeIds.URN_ITEM_COMPONENT_ATTR,
                    componentIds);
                setReady();
            }
        }

        return element;
    }

}
