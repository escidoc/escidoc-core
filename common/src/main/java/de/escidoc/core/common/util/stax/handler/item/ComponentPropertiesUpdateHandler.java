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
package de.escidoc.core.common.util.stax.handler.item;

import de.escidoc.core.common.business.fedora.TripleStoreUtility;
import de.escidoc.core.common.business.fedora.resources.item.Component;
import de.escidoc.core.common.exceptions.application.invalid.InvalidContentException;
import de.escidoc.core.common.exceptions.system.TripleStoreSystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.util.logger.AppLogger;
import de.escidoc.core.common.util.stax.StaxParser;
import de.escidoc.core.common.util.xml.Elements;
import de.escidoc.core.common.util.xml.stax.events.EndElement;
import de.escidoc.core.common.util.xml.stax.events.StartElement;
import de.escidoc.core.common.util.xml.stax.handler.DefaultHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * ComponentPropertiesUpdateHandler handles XML with properties section for
 * eSciDoc Component.
 * 
 * @author FRS
 * 
 */
public class ComponentPropertiesUpdateHandler extends DefaultHandler {

    private final Map<String, String> properties;

    private StaxParser parser = null;

    private String propertiesPath = null;

    private List<String> expected = null;

    private static AppLogger logger =
        new AppLogger(ComponentPropertiesUpdateHandler.class.getName());

    // names of elements that must be deleted if they do not occur
    private static final String[] expectedElements =
        { TripleStoreUtility.PROP_MIME_TYPE,
            TripleStoreUtility.PROP_VALID_STATUS };

    public ComponentPropertiesUpdateHandler(final Component component,
        final String propertiesPath, final StaxParser parser)
        throws TripleStoreSystemException, WebserverSystemException {

        this.parser = parser;
        this.propertiesPath = propertiesPath;

        // TODO check this; was local var
        this.properties = component.getResourceProperties();

        this.expected = new ArrayList<String>(Arrays.asList(expectedElements));
    }

    public ComponentPropertiesUpdateHandler(
        final Map<String, String> properties, final String propertiesPath,
        final StaxParser parser) throws TripleStoreSystemException,
        WebserverSystemException {

        this.parser = parser;
        this.propertiesPath = propertiesPath;

        // TODO check this; was local var
        this.properties = properties;

        this.expected = new ArrayList<String>(Arrays.asList(expectedElements));
    }

    @Override
    public String characters(final String data, final StartElement element)
        throws InvalidContentException, WebserverSystemException {
        String curPath = parser.getCurPath();

        if (curPath.startsWith(propertiesPath)) {
            // do my job
            // if (curPath.equals(propertiesPath + "/description")) {
            // // should be saved/deleted
            // expected.remove(TripleStoreUtility.PROP_DESCRIPTION);
            // properties.put(TripleStoreUtility.PROP_DESCRIPTION, data);
            // }
            // visibility
            if (curPath.equals(propertiesPath + "/visibility")) {
                // just save, xml-schema ensures correct values
                if (data.length() > 0) {
                    properties.put(TripleStoreUtility.PROP_VISIBILITY, data);
                }
                else {
                    throw new InvalidContentException("Components.properties."
                        + Elements.ELEMENT_VISIBILITY + " has invalid value.");
                }
            }
            // content-category
            else if (curPath.equals(propertiesPath + "/"
                + Elements.ELEMENT_COMPONENT_CONTENT_CATEGORY)) {
                // ensure there is a value and save
                if (data.length() > 0) {
                    properties.put(TripleStoreUtility.PROP_CONTENT_CATEGORY,
                        data);
                }
                else {
                    throw new InvalidContentException("Components.properties."
                        + Elements.ELEMENT_COMPONENT_CONTENT_CATEGORY
                        + " has invalid value.");
                }
            }
            // else if (curPath.equals(propertiesPath + "/file-name")) {
            // // ensure there is a value and save
            // if (data.length() > 0) {
            // properties.put(TripleStoreUtility.PROP_FILENAME, data);
            // }
            // else {
            // throw new InvalidContentException("Components.properties."
            // + "file-name" + " has invalid value.");
            // }
            // }
            else if (curPath.equals(propertiesPath + "/mime-type")) {
                // should be saved/deleted
                expected.remove(TripleStoreUtility.PROP_MIME_TYPE);
                properties.put(TripleStoreUtility.PROP_MIME_TYPE, data);
            }
            else if (curPath.equals(propertiesPath + "/valid-status")) {
                // should be saved/deleted
                expected.remove(TripleStoreUtility.PROP_VALID_STATUS);
                properties.put(TripleStoreUtility.PROP_VALID_STATUS, data);
            }
            // else if (curPath.equals(propertiesPath + "/file-size")) {
            // // ensure there is a value and save
            // if (data.length() > 0) {
            // properties.put(TripleStoreUtility.PROP_FILESIZE, data);
            // }
            // else {
            // throw new InvalidContentException("Components.properties."
            // + "file-size" + " has invalid value.");
            // }
            // }
            // locator-url
            // else if (curPath.equals(propertiesPath + "/locator-url")) {
            // // should be saved/deleted
            // expected.remove(TripleStoreUtility.PROP_LOCATOR_URL);
            // properties.put(TripleStoreUtility.PROP_LOCATOR_URL, data);
            // }
        }
        return data;
    }

    @Override
    public EndElement endElement(final EndElement element) {
        String curPath = parser.getCurPath();
        if (curPath.equals(propertiesPath)) {
            // delete properties not send
            Iterator<String> it = expected.iterator();
            while (it.hasNext()) {
                String prop = it.next();
                properties.remove(prop);
            }
        }
        return element;
    }

    public Map<String, String> getProperties() {
        return properties;
    }
}
