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
package de.escidoc.core.om.business.stax.handler;

import de.escidoc.core.common.business.Constants;
import de.escidoc.core.common.business.fedora.TripleStoreUtility;
import de.escidoc.core.common.business.fedora.Utility;
import de.escidoc.core.common.exceptions.application.invalid.InvalidContentException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidXmlException;
import de.escidoc.core.common.exceptions.application.notfound.ReferencedResourceNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.RelationPredicateNotFoundException;
import de.escidoc.core.common.exceptions.application.violated.ReadonlyAttributeViolationException;
import de.escidoc.core.common.exceptions.system.EncodingSystemException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.exceptions.system.TripleStoreSystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.exceptions.system.XmlParserSystemException;
import de.escidoc.core.common.util.logger.AppLogger;
import de.escidoc.core.common.util.stax.StaxParser;
import de.escidoc.core.common.util.xml.XmlUtility;
import de.escidoc.core.common.util.xml.stax.events.EndElement;
import de.escidoc.core.common.util.xml.stax.events.StartElement;
import de.escidoc.core.common.util.xml.stax.handler.DefaultHandler;
import de.escidoc.core.om.business.fedora.ContentRelationsUtility;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class ContentRelationsCreateHandler2Edition extends DefaultHandler {

    public static final String CONTAINER = "/container";

    private static AppLogger LOG = new AppLogger(ContentRelationsCreateHandler2Edition.class.getName());

    private final StaxParser parser;

    private boolean inContentRelation = false;

    private int number = 0;

    private String currentPath = null;

    private String contentRelationPath = null;

    private String targetId = null;

    private String predicate = null;

    private List<Map<String, String>> relationsData =
        new Vector<Map<String, String>>();

    private final List<String> relationsDataCheck = new Vector<String>();

    /**
     * Instantiate a ContentRelationsCreateHandler.
     * 
     * @param id
     *            The id of the parsed object.
     * @param parser
     *            The parser.
     * @om
     */
    public ContentRelationsCreateHandler2Edition(final String id, final StaxParser parser) {
        this.parser = parser;
    }

    /**
     * Handle the start of an element.
     * 
     * @param element
     *            The element.
     * @return The element.
     * @throws InvalidContentException
     * @throws SystemException
     * @throws ReferencedResourceNotFoundException
     * @throws ReadonlyAttributeViolationException
     * @throws SystemException
     *             Thrown in case of an internal error.
     * @throws RelationPredicateNotFoundException
     * @see de.escidoc.core.om.business.stax.handler.DefaultHandler#startElement
     *      (de.escidoc.core.om.business.stax.events.StartElement)
     * @om
     */
    @Override
    public StartElement startElement(final StartElement element)
        throws InvalidContentException, ReferencedResourceNotFoundException,
        RelationPredicateNotFoundException, TripleStoreSystemException,
        WebserverSystemException, EncodingSystemException,
        XmlParserSystemException, InvalidXmlException {

        currentPath = parser.getCurPath();
        // contentRelationsPath = "/item/relations";
        contentRelationPath = "/item/relations/relation";

        // hrefBasePath = "/ir/item/";
        if (currentPath.startsWith(CONTAINER)) {
            // contentRelationsPath = "/container/relations";
            contentRelationPath = "/container/relations/relation";

            // hrefBasePath = "/ir/container/";
        }
        String theName = element.getLocalName();

        if (contentRelationPath.equals(currentPath)) {
            inContentRelation = true;
            number++;
            int indexOfObjId = element.indexOfAttribute(null, "objid");
            int indexOfHref =
                element.indexOfAttribute(Constants.XLINK_URI, "href");
            String href = null;
            if (indexOfHref != -1) {
                href = element.getAttribute(indexOfHref).getValue();
                if (href.equals("")) {
                    String message =
                        "The value of attribute 'xlink:href' of "
                            + " the element '" + theName
                            + "' may not be an empty string";
                    LOG.error(message);
                    throw new InvalidContentException(message);
                }
            }
            String objid = null;
            if (indexOfObjId != -1) {
                objid = element.getAttribute(indexOfObjId).getValue();
                if (objid.equals("")) {
                    String message =
                        "The value of attribute 'objid' of " + " the element '"
                            + theName + "' may not be an empty string";
                    LOG.error(message);
                    throw new InvalidContentException(message);
                }
            }

            checkRefElement(objid, href);
            int indexOfPredicate = element.indexOfAttribute(null, "predicate");
            predicate = element.getAttribute(indexOfPredicate).getValue();
            if (!ContentRelationsUtility.validPredicate(predicate)) {
                String message = "Predicate " + predicate + " is wrong. ";
                LOG.error(message);
                throw new RelationPredicateNotFoundException(message);
            }
            // int indexOfTitle = element.indexOfAttribute(Constants.XLINK_URI,
            // "title");
            // if (indexOfTitle != (-1)) {
            // String message = "Read only attribute \"title\" of the "
            // + "element " + theName + " may not exist while create";
            // LOG.error(message);
            // throw new ReadonlyAttributeViolationException(message);
            // }
            // lax handling
            // int indexOfType = element.indexOfAttribute(Constants.XLINK_URI,
            // "type");
            // if (indexOfType == (-1)) {
            // Attribute type = new Attribute("type", Constants.XLINK_URI,
            // Constants.XLINK_PREFIX, Constants.XLINK_TYPE_SIMPLE);
            // element.addAttribute(type);
            // }
            // else {
            // Attribute type = element.getAttribute(indexOfType);
            // String typeValue = type.getValue();
            // if (!typeValue.equals(Constants.XLINK_TYPE_SIMPLE)) {
            // type.setValue(Constants.XLINK_TYPE_SIMPLE);
            // }
            // }

        }
        // else if (contentRelationsPath.equals(currentPath)) {
        //
        // int indexOfTitle = element.indexOfAttribute(Constants.XLINK_URI,
        // "title");
        //
        // if (indexOfTitle != (-1)) {
        // String message = "Read only attribute \"title\" of the "
        // + "element " + element.getLocalName()
        // + " may not exist while create";
        // LOG.error(message);
        // throw new ReadonlyAttributeViolationException(message);
        // }
        // int indexOfHref = element.indexOfAttribute(Constants.XLINK_URI,
        // "href");
        //
        // if (indexOfHref != (-1)) {
        // String message = "Read only attribute \"href\" of the "
        // + "element " + element.getLocalName()
        // + " may not exist while create";
        // LOG.error(message);
        // throw new ReadonlyAttributeViolationException(message);
        // }
        // int indexOfType = element.indexOfAttribute(Constants.XLINK_URI,
        // "type");
        // if (indexOfType == (-1)) {
        // Attribute type = new Attribute("type", Constants.XLINK_URI,
        // Constants.XLINK_PREFIX, Constants.XLINK_TYPE_SIMPLE);
        // element.addAttribute(type);
        // }
        // else {
        // Attribute type = element.getAttribute(indexOfType);
        // String typeValue = type.getValue();
        // if (!typeValue.equals(Constants.XLINK_TYPE_SIMPLE)) {
        // type.setValue(Constants.XLINK_TYPE_SIMPLE);
        // }
        // }
        //
        // }

        return element;
    }

    /**
     * Handle the end of an element.
     * 
     * @param element
     *            The element.
     * @return The element.
     * @see de.escidoc.core.om.business.stax.handler.DefaultHandler#endElement
     *      (de.escidoc.core.om.business.stax.events.EndElement)
     * @om
     */
    @Override
    public EndElement endElement(final EndElement element) {
        if (inContentRelation) {
            inContentRelation = false;
            String relationDataCheck = predicate + "###" + targetId;
            if (!relationsDataCheck.contains(relationDataCheck)) {
                relationsDataCheck.add(relationDataCheck);
                HashMap<String, String> relationData =
                    new HashMap<String, String>();
                relationsData.add(relationData);
                int index = predicate.lastIndexOf("#");
                String predicateNs = predicate.substring(0, index);
                String predicateValue = predicate.substring(index + 1);
                relationData.put("predicateNs", predicateNs);
                relationData.put("predicateValue", predicateValue);
                relationData.put("target", targetId);
            }
            targetId = null;

            predicate = null;
        }
        return element;
    }

    /**
     * @return Returns the title.
     */
    public List<Map<String, String>> getContentRelationsData() {
        return relationsData;
    }

    private void checkRefElement(String objectId, String href)
        throws InvalidContentException, TripleStoreSystemException,
        ReferencedResourceNotFoundException, WebserverSystemException {
        targetId = null;
        if (href != null) {
            targetId = Utility.getId(href);
            // if ((objectId != null) && (!targetId.equals(objectId))) {
            // String message = "Mismatch: 'objid' attribute value"
            // + " has to be equal the last part of the 'xlink:href'"
            // + " attribute value";
            // LOG.error(message);
            // throw new InvalidContentException(message);
            // }
        }
        else {
            targetId = objectId;
        }
        String targetIdWithoutVersion =
            XmlUtility.getObjidWithoutVersion(targetId);
        String targetVersion =
            targetId.replaceFirst(targetIdWithoutVersion, "");
        if (targetVersion.length() > 0) {
            targetVersion = targetVersion.substring(1);
            String message =
                "A relation target may not be referenced by an "
                    + " identifier containing a version number. Use a floating "
                    + "identifier like 'escidoc:123' to reference a target";
            LOG.error(message);
            throw new InvalidContentException(message);
        }

        String targetObjectType =
            TripleStoreUtility.getInstance().getObjectType(targetId);

        if (!TripleStoreUtility.getInstance().exists(targetId)) {
            String message =
                "Related " + targetObjectType + " with id " + targetId
                    + " does not exist.";
            LOG.error(message);
            throw new ReferencedResourceNotFoundException(message);
        }

        if (!Constants.ITEM_OBJECT_TYPE.equals(targetObjectType)
            && !Constants.CONTAINER_OBJECT_TYPE.equals(targetObjectType)) {
            String message =
                "A related resource has to be either 'item' or 'container'. "
                    + "A object with id " + targetId
                    + " is neither 'item' nor 'container' ";

            LOG.error(message);
            throw new InvalidContentException(message);
        }
        if (href != null) {
            if (targetObjectType.equals(Constants.ITEM_OBJECT_TYPE)
                && !href.equals("/ir/item/" + targetId)) {

                String message =
                    "The 'href' attribute, which represents"
                        + " a target rest-url has a wrong syntax. The url has to look like: "
                        + "/ir/item/" + targetId;
                LOG.error(message);
                throw new InvalidContentException(message);

            }
            else if (targetObjectType.equals(Constants.CONTAINER_OBJECT_TYPE)
                && !href.equals("/ir/container/" + targetId)) {

                String message =
                    "The 'href' attribute, which represents"
                        + " a target rest-url has a wrong syntax. The url has to look like: "
                        + "/ir/container/" + targetId;
                LOG.error(message);
                throw new InvalidContentException(message);

            }
        }
    }

}
