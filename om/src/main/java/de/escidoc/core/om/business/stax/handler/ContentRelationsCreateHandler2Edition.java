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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContentRelationsCreateHandler2Edition extends DefaultHandler {

    public static final String CONTAINER = "/container";

    private static final AppLogger LOGGER = new AppLogger(ContentRelationsCreateHandler2Edition.class.getName());

    private final StaxParser parser;

    private boolean inContentRelation = false;

    private String currentPath = null;

    private String contentRelationPath = null;

    private String targetId = null;

    private String predicate = null;

    private final List<Map<String, String>> relationsData =
        new ArrayList<Map<String, String>>();

    private final Collection<String> relationsDataCheck = new ArrayList<String>();

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
     *
     * @om
     */
    @Override
    public StartElement startElement(final StartElement element)
        throws InvalidContentException, ReferencedResourceNotFoundException,
        RelationPredicateNotFoundException, TripleStoreSystemException,
        WebserverSystemException, EncodingSystemException,
        XmlParserSystemException, InvalidXmlException {
        currentPath = parser.getCurPath();
        contentRelationPath = "/item/relations/relation";
        if (currentPath.startsWith(CONTAINER)) {
            contentRelationPath = "/container/relations/relation";

        }
        final String theName = element.getLocalName();

        if (contentRelationPath.equals(currentPath)) {
            inContentRelation = true;
            final int indexOfObjId = element.indexOfAttribute(null, "objid");
            final int indexOfHref =
                element.indexOfAttribute(Constants.XLINK_URI, "href");
            String href = null;
            if (indexOfHref != -1) {
                href = element.getAttribute(indexOfHref).getValue();
                if (href.length() == 0) {
                    final String message =
                        "The value of attribute 'xlink:href' of "
                            + " the element '" + theName
                            + "' may not be an empty string";
                    LOGGER.error(message);
                    throw new InvalidContentException(message);
                }
            }
            String objid = null;
            if (indexOfObjId != -1) {
                objid = element.getAttribute(indexOfObjId).getValue();
                if (objid.length() == 0) {
                    final String message =
                        "The value of attribute 'objid' of " + " the element '"
                            + theName + "' may not be an empty string";
                    LOGGER.error(message);
                    throw new InvalidContentException(message);
                }
            }

            checkRefElement(objid, href);
            final int indexOfPredicate = element.indexOfAttribute(null, "predicate");
            predicate = element.getAttribute(indexOfPredicate).getValue();
            if (!ContentRelationsUtility.validPredicate(predicate)) {
                final String message = "Predicate " + predicate + " is wrong. ";
                LOGGER.error(message);
                throw new RelationPredicateNotFoundException(message);
            }
        }
        return element;
    }

    /**
     * Handle the end of an element.
     * 
     * @param element
     *            The element.
     * @return The element.
     * @om
     */
    @Override
    public EndElement endElement(final EndElement element) {
        if (inContentRelation) {
            inContentRelation = false;
            final String relationDataCheck = predicate + "###" + targetId;
            if (!relationsDataCheck.contains(relationDataCheck)) {
                relationsDataCheck.add(relationDataCheck);
                final Map<String, String> relationData =
                    new HashMap<String, String>();
                relationsData.add(relationData);
                final int index = predicate.lastIndexOf('#');
                final String predicateNs = predicate.substring(0, index);
                final String predicateValue = predicate.substring(index + 1);
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

    private void checkRefElement(final String objectId, final String href)
        throws InvalidContentException, TripleStoreSystemException,
        ReferencedResourceNotFoundException, WebserverSystemException {
        targetId = null;
        if (href != null) {
            targetId = Utility.getId(href);
        }
        else {
            targetId = objectId;
        }
        final String targetIdWithoutVersion =
            XmlUtility.getObjidWithoutVersion(targetId);
        String targetVersion =
            targetId.replaceFirst(targetIdWithoutVersion, "");
        if (targetVersion.length() > 0) {
            targetVersion = targetVersion.substring(1);
            final String message =
                "A relation target may not be referenced by an "
                    + " identifier containing a version number. Use a floating "
                    + "identifier like 'escidoc:123' to reference a target";
            LOGGER.error(message);
            throw new InvalidContentException(message);
        }

        final String targetObjectType =
            TripleStoreUtility.getInstance().getObjectType(targetId);

        if (!TripleStoreUtility.getInstance().exists(targetId)) {
            final String message =
                "Related " + targetObjectType + " with id " + targetId
                    + " does not exist.";
            LOGGER.error(message);
            throw new ReferencedResourceNotFoundException(message);
        }

        if (!Constants.ITEM_OBJECT_TYPE.equals(targetObjectType)
            && !Constants.CONTAINER_OBJECT_TYPE.equals(targetObjectType)) {
            final String message =
                "A related resource has to be either 'item' or 'container'. "
                    + "A object with id " + targetId
                    + " is neither 'item' nor 'container' ";

            LOGGER.error(message);
            throw new InvalidContentException(message);
        }
        if (href != null) {
            if (targetObjectType.equals(Constants.ITEM_OBJECT_TYPE)
                && !href.equals("/ir/item/" + targetId)) {

                final String message =
                    "The 'href' attribute, which represents"
                        + " a target rest-url has a wrong syntax. The url has to look like: "
                        + "/ir/item/" + targetId;
                LOGGER.error(message);
                throw new InvalidContentException(message);

            }
            else if (targetObjectType.equals(Constants.CONTAINER_OBJECT_TYPE)
                && !href.equals("/ir/container/" + targetId)) {

                final String message =
                    "The 'href' attribute, which represents"
                        + " a target rest-url has a wrong syntax. The url has to look like: "
                        + "/ir/container/" + targetId;
                LOGGER.error(message);
                throw new InvalidContentException(message);

            }
        }
    }

}
