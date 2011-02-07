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
package de.escidoc.core.om.business.stax.handler.item;

import de.escidoc.core.common.business.fedora.TripleStoreUtility;
import de.escidoc.core.common.business.fedora.resources.Item;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.util.logger.AppLogger;
import de.escidoc.core.common.util.stax.StaxParser;
import de.escidoc.core.common.util.xml.stax.events.StartElement;
import de.escidoc.core.common.util.xml.stax.handler.DefaultHandler;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * @author FRS
 * 
 */
public class ItemPropertiesUpdateHandler extends DefaultHandler {

    private Map<String, String> properties;

    private StaxParser parser = null;

    private String propertiesPath = null;

    private String itemId = null;

    private final Object lastVersionNumber;

    private final Object lastModificationDate;

    private final Object lastValidStatus;

    private final Object lastStatus;

    private final Object lastRevisionNumber;

    private final Object lastRevisionDate;

    private List expected = null;

    // names of elements that must occur
    private static final String[] expectedElements = null;

    private static AppLogger log =
        new AppLogger(ItemPropertiesUpdateHandler.class.getName());

    public ItemPropertiesUpdateHandler(Item item, String propertiesPath,
        StaxParser parser) throws SystemException {
        this.itemId = item.getId();
        this.propertiesPath = propertiesPath;
        this.parser = parser;
        this.properties = new HashMap<String, String>();
        if (expectedElements != null) {
            this.expected = new Vector(Arrays.asList(expectedElements));
        }

        // TODO check this; was local var
        properties = item.getResourceProperties();

        this.lastVersionNumber =
            properties.get(TripleStoreUtility.PROP_LATEST_VERSION_NUMBER);
        this.lastModificationDate =
            properties.get(TripleStoreUtility.PROP_LATEST_VERSION_DATE);
        this.lastValidStatus =
            properties.get(TripleStoreUtility.PROP_LATEST_VERSION_VALID_STATUS);
        this.lastStatus =
            properties.get(TripleStoreUtility.PROP_LATEST_VERSION_STATUS);

        // if (versionData.containsKey("revisionNo")) {
        this.lastRevisionNumber =
            properties.get(TripleStoreUtility.PROP_LATEST_RELEASE_NUMBER);
        this.lastRevisionDate =
            properties.get(TripleStoreUtility.PROP_LATEST_RELEASE_DATE);

        /*
         * read-only elements are ignored now // pid is expected if set if
         * (properties.containsKey(TripleStoreUtility.PROP_PID) &&
         * properties.get(TripleStoreUtility.PROP_PID) != null && ((String)
         * properties.get(TripleStoreUtility.PROP_PID)).length() > 0) {
         * expected.add("pid"); } // latest-release.pid is expected if set if
         * (properties.containsKey(TripleStoreUtility.PROP_LATEST_RELEASE_PID) &&
         * properties.get(TripleStoreUtility.PROP_LATEST_RELEASE_PID) != null &&
         * ((String) properties
         * .get(TripleStoreUtility.PROP_LATEST_RELEASE_PID)).length() > 0) {
         * expected.add("latest-release.pid"); // current-version.pid is
         * expected if latest-release.pid is set and // current-version is
         * latest-release (this is sufficient because we // are in update which
         * is only allowed on latest version) if
         * (properties.get(TripleStoreUtility.PROP_LATEST_RELEASE_NUMBER)
         * .equals( properties
         * .get(TripleStoreUtility.PROP_LATEST_VERSION_NUMBER))) {
         * expected.add("current-version.pid"); } }
         */
    }

    /*
     * read-only elements are ignored now
     */
    @Override
    public StartElement startElement(StartElement element) {
        String curPath = parser.getCurPath();
        return element;
    }
    
    @Override
    public String characters(String data, StartElement element) {
        return data;
    }

    /**
     * @deprecated
     * @return
     */
    @Deprecated
    public HashMap<String, String> getProperties() {
        // this handler do not check every element/attribute any longer and has
        // not the whole properties
        throw new UnsupportedOperationException(
            "Can not return whole properties.");
    }
}
