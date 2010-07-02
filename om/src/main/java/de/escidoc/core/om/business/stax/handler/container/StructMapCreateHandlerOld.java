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
package de.escidoc.core.om.business.stax.handler.container;

import java.util.List;
import java.util.Vector;

import de.escidoc.core.common.business.Constants;
import de.escidoc.core.common.business.fedora.TripleStoreUtility;
import de.escidoc.core.common.business.fedora.Utility;
import de.escidoc.core.common.exceptions.application.invalid.InvalidContentException;
import de.escidoc.core.common.exceptions.system.TripleStoreSystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.util.logger.AppLogger;
import de.escidoc.core.common.util.stax.StaxParser;
import de.escidoc.core.common.util.xml.stax.events.Attribute;
import de.escidoc.core.common.util.xml.stax.events.StartElement;
import de.escidoc.core.common.util.xml.stax.handler.DefaultHandler;

/**
 * 
 * @author FRS
 * 
 * @om
 */
public class StructMapCreateHandlerOld extends DefaultHandler {

    private StaxParser parser;

    private String structMapPath;

    private String containerId;

    private List<String> entries = new Vector<String>();

    private static AppLogger logger =
        new AppLogger(StructMapCreateHandlerOld.class.getName());

    public StructMapCreateHandlerOld(String containerId, String structMapPath,
        StaxParser parser) {
        this.containerId = containerId;
        this.structMapPath = structMapPath;
        this.parser = parser;
    }

    public StructMapCreateHandlerOld(String containerId, StaxParser parser) {
        this.containerId = containerId;
        this.structMapPath = "/container/struct-map";
        this.parser = parser;
    }

    public StartElement startElement(final StartElement element)
        throws InvalidContentException, TripleStoreSystemException,
        WebserverSystemException {
        String curPath = parser.getCurPath();

        if (curPath.startsWith(structMapPath)) {

            if (curPath.equals(structMapPath)) {

                // int indexOfType =
                // element.indexOfAttribute(Constants.XLINK_URI,
                // "type");
                // if (indexOfType == (-1)) {
                // Attribute type = new Attribute("type", Constants.XLINK_URI,
                // Constants.XLINK_PREFIX, "simple");
                // element.addAttribute(type);
                // } else {
                // Attribute type = element.getAttribute(indexOfType);
                // String typeValue = type.getValue();
                // if(!typeValue.equals(Constants.XLINK_TYPE_SIMPLE)) {
                // type.setValue("simple");
                // }
                // }
                //
                // int indexOfHref = element.indexOfAttribute(
                // Constants.XLINK_URI, "href");
                // if (indexOfHref != (-1)) {
                // String message = "Read only attribute \"href\" of the "
                // + "element " + element.getLocalName() + " may not exist while
                // create";
                // getLogger().error(message);
                // throw new ReadonlyAttributeViolationException(message);
                // }
                // int indexOfTitle = element.indexOfAttribute(
                // Constants.XLINK_URI, "title");
                // if (indexOfTitle != (-1)) {
                // String message = "Read only attribute \"title\" of the "
                // + "element " + element.getLocalName() + " may not exist while
                // create";
                // getLogger().error(message);
                // throw new ReadonlyAttributeViolationException(message);
                // }
            }
            else if (curPath.equals(structMapPath
                + "/member-ref-list/member/item-ref")) {
                entries.add(checkRefElement(element, "item"));
            }
            else if (curPath.equals(structMapPath
                + "/member-ref-list/member/container-ref")) {
                entries.add(checkRefElement(element, "container"));
            }

        }

        return element;
    }

    private String checkRefElement(StartElement element, String objectType)
        throws InvalidContentException, TripleStoreSystemException,
        WebserverSystemException {
        String entryId = null;
        int indexOfObjId = element.indexOfAttribute(null, "objid");
        if (indexOfObjId != -1) {
            entryId = element.getAttribute(indexOfObjId).getValue();
            if (!TripleStoreUtility.getInstance().exists(entryId)) {
                throw new InvalidContentException(
                    "Referenced object in struct-map does not exist.");
            }
            if (!TripleStoreUtility
                .getInstance().getObjectType(entryId).equals(objectType)) {
                throw new InvalidContentException(
                    "Referenced object in struct-map is no " + objectType + ".");
            }
        }
        // int indexOfType = element.indexOfAttribute(Constants.XLINK_URI,
        // "type");
        // if (indexOfType == (-1)) {
        // Attribute type = new Attribute("type", Constants.XLINK_URI,
        // Constants.XLINK_PREFIX, "simple");
        // element.addAttribute(type);
        // }
        // else {
        // Attribute type = element.getAttribute(indexOfType);
        // String typeValue = type.getValue();
        // if (!typeValue.equals(Constants.XLINK_TYPE_SIMPLE)) {
        // type.setValue("simple");
        // }
        // }

        // int indexOfTitle = element.indexOfAttribute(Constants.XLINK_URI,
        // "title");
        // if (indexOfTitle != (-1)) {
        // String message = "Read only attribute \"title\" of the "
        // + "element " + element.getLocalName()
        // + " may not exist while create";
        // getLogger().error(message);
        // throw new ReadonlyAttributeViolationException(message);
        // }
        int indexOfHref = element.indexOfAttribute(Constants.XLINK_URI, "href");
        if (indexOfHref != -1) {
            Attribute xlinkHref = element.getAttribute(indexOfHref);
            String xlinkHrefValue = xlinkHref.getValue();
            entryId = Utility.getId(xlinkHrefValue);
            if (!TripleStoreUtility.getInstance().exists(entryId)) {
                throw new InvalidContentException(
                    "Referenced object in struct-map does not exist.");
            }
            if (!TripleStoreUtility
                .getInstance().getObjectType(entryId).equals(objectType)) {
                throw new InvalidContentException(
                    "Referenced object in struct-map is no " + objectType + ".");
            }
            String xlinkPrefix = xlinkHref.getPrefix();
            if (!xlinkHrefValue.equals("/ir/" + objectType + "/" + entryId)) {
                throw new InvalidContentException("The value of attribute "
                    + element.getLocalName() + "." + xlinkPrefix
                    + ":href has to look like: ir/" + objectType + "/"
                    + entryId);
            }
        }
        return entryId;

    }

    /**
     * @return Returns the logger.
     */
    public static AppLogger getLogger() {
        return logger;
    }

    public List<String> getEntries() {
        return entries;
    }

}
