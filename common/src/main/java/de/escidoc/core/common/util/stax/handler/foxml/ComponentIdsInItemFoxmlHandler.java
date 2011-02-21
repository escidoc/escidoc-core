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
package de.escidoc.core.common.util.stax.handler.foxml;

import de.escidoc.core.common.business.Constants;
import de.escidoc.core.common.business.fedora.Utility;
import de.escidoc.core.common.util.stax.StaxParser;
import de.escidoc.core.common.util.xml.stax.events.Attribute;
import de.escidoc.core.common.util.xml.stax.events.StartElement;
import de.escidoc.core.common.util.xml.stax.handler.DefaultHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * 
 * @author
 * 
 */
public class ComponentIdsInItemFoxmlHandler extends DefaultHandler {

    private final StaxParser parser;

    private boolean inDescription = false;

    private String versionPid = null;

    private static final String DESCRIPTION_PATH =
        "/digitalObject/datastream/datastreamVersion/xmlContent/RDF/Description";

    private final List<String> componentIds = new ArrayList<String>();

    // private static AppLogger log =
    // new AppLogger(ComponentIdsInItemFoxmlHandler.class.getName());

    /**
     * 
     * @param parser
     *            StAX parser.
     */
    public ComponentIdsInItemFoxmlHandler(final StaxParser parser) {
        this.parser = parser;

    }

    @Override
    public StartElement startElement(final StartElement element) {

        String theName = element.getLocalName();

        String currentPath = parser.getCurPath();

        if (!inDescription && DESCRIPTION_PATH.equals(currentPath)) {
            inDescription = true;

        }
        if (inDescription && theName.equals("component")) {

            int indexOfComponentId =
                element.indexOfAttribute(Constants.RDF_NAMESPACE_URI,
                    "resource");
            if (indexOfComponentId != (-1)) {
                Attribute resource = element.getAttribute(indexOfComponentId);
                String resourceValue = resource.getValue();
                if (resourceValue.length() > 0) {
                    resourceValue = Utility.getId(resourceValue);
                    if (!componentIds.contains(resourceValue)) {
                        componentIds.add(resourceValue);
                    }
                }
            }

        }

        return element;
    }

    public String characters(final String data, final StartElement element) {
        String namespace = element.getNamespace();
        if (inDescription && element.getLocalName().equals("pid")
            && namespace.equals(Constants.VERSION_NS_URI)) {
            versionPid = data;
        }
        return data;
    }

    public StartElement endElement(final StartElement element) {
        String currentPath = parser.getCurPath();
        if (DESCRIPTION_PATH.equals(currentPath)) {
            inDescription = false;
        }
        return element;
    }

    /**
     * 
     * @return Vector with ids of the Components.
     */
    public List<String> getComponentIds() {
        return this.componentIds;
    }

    /**
     * Get PID of the resource version.
     * 
     * @return PID of version.
     */
    public String getVersionPid() {
        return this.versionPid;
    }
}
