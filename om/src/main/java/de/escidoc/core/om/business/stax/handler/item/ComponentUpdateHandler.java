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
package de.escidoc.core.om.business.stax.handler.item;

import de.escidoc.core.common.business.Constants;
import de.escidoc.core.common.business.fedora.TripleStoreUtility;
import de.escidoc.core.common.business.fedora.Utility;
import de.escidoc.core.common.exceptions.application.invalid.InvalidContentException;
import de.escidoc.core.common.exceptions.system.TripleStoreSystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.util.stax.StaxParser;
import de.escidoc.core.common.util.xml.stax.events.StartElement;
import de.escidoc.core.common.util.xml.stax.handler.DefaultHandler;

import java.util.List;

public class ComponentUpdateHandler extends DefaultHandler {

    private StaxParser parser = null;

    private String componentPath = null;

    private String itemId = null;

    private String componentId = null;

    public ComponentUpdateHandler(final String itemId, final String componentPath,
        final StaxParser parser) {
        this.itemId = itemId;
        this.parser = parser;
        this.componentPath = componentPath;
    }

    @Override
    public StartElement startElement(final StartElement element)
        throws TripleStoreSystemException, WebserverSystemException,
        InvalidContentException {
        final String curPath = parser.getCurPath();
        if ((curPath.startsWith(componentPath)) && (curPath.equals(componentPath))) {
            // do my job
            // save componentId
            final int indexObjid = element.indexOfAttribute(null, "objid");
            final int indexHref =
                element.indexOfAttribute(Constants.XLINK_NS_URI, "href");
            if (indexObjid >= 0 || indexHref >= 0) {
                if (indexObjid >= 0) {
                    componentId =
                        element.getAttribute(indexObjid).getValue();
                }
                else {
                    componentId =
                        Utility.getId(element
                            .getAttribute(indexHref).getValue());
                }

                if (componentId.length() > 0) {
                    // check if component exists
                    boolean componentExists = false;
                    final List<String> existingComponents =
                        TripleStoreUtility.getInstance().getComponents(
                            itemId);
                    for (final String existingComponent : existingComponents) {
                        if (existingComponent.equals(componentId)) {
                            componentExists = true;
                            break;
                        }
                    }
                    if (!componentExists) {
                        // isExist(componentId)){
                        throw new InvalidContentException(
                            "Component with id " + componentId
                                + " does not exist in item " + itemId + '.');
                    }
                }
            }
        }
        return element;
    }

    @Override
    public String characters(final String data, final StartElement element) {
        final String curPath = parser.getCurPath();

        // check content
        if (curPath.equals(componentPath + "/content")) {

            // check title
            int index =
                element.indexOfAttribute(
                    de.escidoc.core.common.business.Constants.XLINK_URI,
                    "title");

            // check href
            index =
                element
                    .indexOfAttribute(
                        de.escidoc.core.common.business.Constants.XLINK_URI,
                        "href");
        }
        return data;
    }

}
