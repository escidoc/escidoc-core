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

import java.util.HashMap;
import java.util.Map;

import com.sun.xacml.EvaluationCtx;
import de.escidoc.core.common.business.aa.authorisation.AttributeIds;
import de.escidoc.core.common.exceptions.application.missing.MissingAttributeValueException;
import de.escidoc.core.common.util.xml.XmlUtility;
import de.escidoc.core.common.util.xml.stax.events.StartElement;
import de.escidoc.core.common.util.xml.stax.handler.DefaultHandler;

/**
 * Stax handler implementation that handles the attributes that have to be fetched from an component Xml representation
 * or the component's properties Xml representation.<br> This handler extracts the attributes
 * ...:item:component:valid-status, ...:item:component:visibility, ...:item:component:content-category, and
 * ...:item:component:created-by. The attributes found are stored in the HashMap.
 *
 * @author Torsten Tetteroo
 */
public class ComponentStaxHandler extends DefaultHandler {

    private final EvaluationCtx ctx;

    private final String componentId;

    /**
     * contains the extracted Attributes.
     */
    private final Map<String, String> attributes = new HashMap<String, String>();

    /**
     * The constructor.
     *
     * @param ctx         The {@code EvaluationCtx} for that the item xml representation shall be parsed.
     * @param componentId The id of the item's component.
     */
    public ComponentStaxHandler(final EvaluationCtx ctx, final String componentId) {

        this.ctx = ctx;
        this.componentId = componentId;
    }

    /**
     * See Interface for functional description.
     *
     * @see DefaultHandler #startElement (de.escidoc.core.common.util.xml.stax.events.StartElement)
     */
    @Override
    public StartElement startElement(final StartElement element) throws MissingAttributeValueException {

        if (isNotReady()) {
            final String localName = element.getLocalName();
            if (XmlUtility.NAME_CREATED_BY.equals(localName)) {
                attributes.put(AttributeIds.URN_ITEM_COMPONENT_CREATED_BY_ATTR, XmlUtility
                    .getIdFromStartElement(element));
            }
        }

        return element;
    }

    /**
     * See Interface for functional description.
     *
     * @see DefaultHandler #characters(java.lang.String, de.escidoc.core.common.util.xml.stax.events.StartElement)
     */
    @Override
    public String characters(final String data, final StartElement element) throws Exception {

        if (isNotReady()) {
            super.characters(data, element);
            final String localName = element.getLocalName();
            if (XmlUtility.NAME_VALID_STATUS.equals(localName)) {
                attributes.put(AttributeIds.URN_ITEM_COMPONENT_VALID_STATUS_ATTR, data);
            }
            else if (XmlUtility.NAME_VISIBILITY.equals(localName)) {
                attributes.put(AttributeIds.URN_ITEM_COMPONENT_VISIBILITY_ATTR, data);
            }
            else if (XmlUtility.NAME_CONTENT_CATEGORY.equals(localName)) {
                attributes.put(AttributeIds.URN_ITEM_COMPONENT_CONTENT_CATEGORY_ATTR, data);
            }
        }

        return data;
    }

    /**
     * @return the attributes
     */
    public Map<String, String> getAttributes() {
        return attributes;
    }

    /**
     * @return the ctx
     */
    public EvaluationCtx getCtx() {
        return ctx;
    }

    /**
     * @return the componentId
     */
    public String getComponentId() {
        return componentId;
    }

}
