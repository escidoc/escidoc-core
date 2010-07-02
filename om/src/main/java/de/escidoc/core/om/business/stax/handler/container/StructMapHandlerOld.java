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

import java.util.ArrayList;
import java.util.List;

import javax.naming.directory.NoSuchAttributeException;

import de.escidoc.core.common.business.fedora.TripleStoreUtility;
import de.escidoc.core.common.exceptions.application.invalid.InvalidContentException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.util.logger.AppLogger;
import de.escidoc.core.common.util.stax.StaxParser;
import de.escidoc.core.common.util.xml.stax.events.StartElement;
import de.escidoc.core.common.util.xml.stax.handler.DefaultHandler;

/**
 * 
 * @author FRS
 * 
 * @om
 */
public class StructMapHandlerOld extends DefaultHandler {

    private StaxParser parser;

    private String structMapPath;

    private String containerId;

    private List entries = new ArrayList();

    private static AppLogger logger =
        new AppLogger(StructMapHandlerOld.class.getName());

    /**
     * Extracting member ids.
     * 
     * @param containerId
     *            The container id.
     * @param structMapPath
     *            Simple (w/o namespaces (X)Path to struct-map.
     * @param parser
     *            The parser this handler is attached to.
     */
    public StructMapHandlerOld(String containerId, String structMapPath,
        StaxParser parser) {
        this.containerId = containerId;
        this.structMapPath = structMapPath;
        this.parser = parser;
    }

    /**
     * Extracting member ids. structMapPath defaults to "/container/struct-map".
     * 
     * @param containerId
     *            The container id.
     * @param parser
     *            The parser this handler is attached to.
     */
    public StructMapHandlerOld(String containerId, StaxParser parser) {
        this.containerId = containerId;
        this.structMapPath = "/container/struct-map";
        this.parser = parser;
    }

    public StartElement startElement(final StartElement element)
        throws InvalidContentException, SystemException {
        String curPath = parser.getCurPath();

        if (curPath.startsWith(structMapPath)) {

            if (curPath.equals(structMapPath)) {

                // try {
                // String xlinkType = element.getAttribute(
                // de.escidoc.core.common.business.Constants.XLINK_URI,
                // "type").getValue();
                // if (xlinkType == null || !xlinkType.equals("simple")) {
                // throw new InvalidContentException(
                // "xlink:type is not simple.");
                // }

                // String xlinkTitle = element.getAttribute(
                // de.escidoc.core.common.business.Constants.XLINK_URI,
                // "title").getValue();
                // if (!xlinkTitle.equals("Struct Map")) {
                // throw new InvalidContentException(
                // "Invalid title of struct-map");
                // }

                // String xlinkHref = element.getAttribute(
                // de.escidoc.core.common.business.Constants.XLINK_URI,
                // "href").getValue();
                // if (!xlinkHref.equals("/ir/container/" + containerId
                // + "/struct-map")) {
                // throw new InvalidContentException(
                // "Invalid 'xlink:href' of 'struct-map' in container "
                // + containerId + ".");
                // }
                // }
                // catch (NoSuchAttributeException e) {
                // throw new InvalidContentException(e);
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
        throws InvalidContentException, SystemException {
        try {
            String entryId = element.getAttribute(null, "objid").getValue();
            if (!TripleStoreUtility.getInstance().exists(entryId)) {
                throw new InvalidContentException(
                    "Referenced object in struct-map does not exist.");
            }
            if (!TripleStoreUtility
                .getInstance().getObjectType(entryId).equals(objectType)) {
                throw new InvalidContentException(
                    "Referenced object in struct-map is no " + objectType + ".");
            }

            // String xlinkType = element
            // .getAttribute(de.escidoc.core.common.business.Constants.XLINK_URI,
            // "type").getValue();
            // if (xlinkType == null || !xlinkType.equals("simple")) {
            // throw new InvalidContentException("xlink:type is not simple.");
            // }

            // try {
            // String xlinkTitle =
            // element.getAttribute(de.escidoc.core.common.business.Constants.XLINK_URI,
            // "title").getValue();
            // if
            // (!TripleStoreUtility.getInstance().getTitle(entryId).equals(xlinkTitle))
            // {
            // throw new InvalidContentException(
            // "Referenced object in struct-map has invalid title.");
            // }
            // }
            // catch (NoSuchAttributeException e) {
            // // no title is a good title. TODO ?
            // }

            String xlinkHref =
                element
                    .getAttribute(
                        de.escidoc.core.common.business.Constants.XLINK_URI,
                        "href").getValue();
            if (!xlinkHref.equals("/ir/" + objectType + "/" + entryId)) {
                throw new InvalidContentException(
                    "Mismatch of 'objid' and 'xlink:href' in '" + objectType
                        + "-ref' " + entryId + ".");
            }

            return entryId;
        }
        catch (NoSuchAttributeException e) {
            throw new InvalidContentException(e);
        }
    }

    /**
     * @return Returns the logger.
     */
    public static AppLogger getLogger() {
        return logger;
    }

    public List getEntries() {
        return entries;
    }
}
