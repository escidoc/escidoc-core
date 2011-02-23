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
import de.escidoc.core.common.exceptions.application.invalid.InvalidContentException;
import de.escidoc.core.common.exceptions.application.notfound.ReferencedResourceNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.RelationPredicateNotFoundException;
import de.escidoc.core.common.exceptions.application.violated.ReadonlyAttributeViolationException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.util.logger.AppLogger;
import de.escidoc.core.common.util.stax.StaxParser;
import de.escidoc.core.common.util.xml.XmlUtility;
import de.escidoc.core.common.util.xml.stax.events.Attribute;
import de.escidoc.core.common.util.xml.stax.events.EndElement;
import de.escidoc.core.common.util.xml.stax.events.StartElement;
import de.escidoc.core.common.util.xml.stax.handler.DefaultHandler;
import de.escidoc.core.om.business.fedora.OntologyUtility;

import javax.naming.directory.NoSuchAttributeException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * 
 * 
 * @author ROF
 * 
 */
public class ContentRelationsCreateHandler extends DefaultHandler {

    private final StaxParser parser;

    private final String id;

    public static final String CONTAINER = "/container";

    private boolean inContentRelation = false;

    private String currentPath = null;

    private String contentRelationsPath = null;

    private String contentRelationPath = null;

    private String targetId = null;

    private String targetIdWithoutVersion = null;

    private String targetVersion = null;

    private String predicate = null;

    private List<Map<String, String>> relationsData = null;

    private static final AppLogger LOGGER =
        new AppLogger(ContentRelationsCreateHandler.class.getName());

    /**
     * Instantiate a ContentRelationsCreateHandler.
     * 
     * @param id
     *            The id of the parsed object.
     * @param parser
     *            The parser.
     */
    public ContentRelationsCreateHandler(final String id,
        final StaxParser parser) {
        this.id = id;
        this.parser = parser;
    }

    /**
     * Handle the start of an element.
     * 
     * @param element
     *            The element.
     * @return The element.
     * @throws SystemException
     *             Thrown in case of an internal error.
     * @see de.escidoc.core.common.util.xml.stax.handler.DefaultHandler#startElement
     *      (de.escidoc.core.common.util.xml.stax.events.StartElement)
     */
    @Override
    public StartElement startElement(final StartElement element)
        throws ReadonlyAttributeViolationException, InvalidContentException,
        ReferencedResourceNotFoundException,
        RelationPredicateNotFoundException, SystemException {

        currentPath = parser.getCurPath();
        contentRelationsPath = "/item/relations";
        contentRelationPath = "/item/relations/relation";

        if (currentPath.startsWith(CONTAINER)) {
            contentRelationsPath = "/container/relations";
            contentRelationPath = "/container/relations/relation";
        }
        
        String theName = element.getLocalName();

        if (contentRelationPath.equals(currentPath)) {
            inContentRelation = true;
            int indexOfObjId = element.indexOfAttribute(null, "objid");
            if (indexOfObjId != (-1)) {

                String message =
                    "Read only attribute \"objid\" of the " + "element "
                        + element.getLocalName()
                        + " may not exist while create";
                LOGGER.info(message);
                throw new ReadonlyAttributeViolationException(message);
            }

        }
        if (inContentRelation) {
            if ("target".equals(theName)) {
                checkRefElement(element);
            }
            else if ("predicate".equals(theName)) {
                try {
                    String xlinkHref =
                        element
                            .getAttribute(Constants.XLINK_URI, "href")
                            .getValue();
                    if (OntologyUtility.checkPredicate(xlinkHref)) {
                        predicate = xlinkHref;
                    } else {
                        String message =
                                "Predicate " + xlinkHref + " is wrong. ";
                        throw new RelationPredicateNotFoundException(message);
                    }

                }
                catch (NoSuchAttributeException e) {
                    String message =
                        "Attribute 'href' of the element '" + theName
                            + "' is missing.";
                    LOGGER.info(message);
                    throw new InvalidContentException(message);
                }
                int indexOfType =
                    element.indexOfAttribute(Constants.XLINK_URI, "type");

                Attribute type = element.getAttribute(indexOfType);
                String typeValue = type.getValue();
                if (!typeValue.equals(Constants.XLINK_TYPE_SIMPLE)) {
                    String message =
                        "Attribute " + Constants.XLINK_URI + ':'
                            + "type must be set to 'simple'";
                    throw new InvalidContentException(message);
                }

            }
        }
        else if (contentRelationsPath.equals(currentPath)) {
            relationsData = new ArrayList<Map<String, String>>();
            int indexOfTitle =
                element.indexOfAttribute(Constants.XLINK_URI, "title");

            if (indexOfTitle != (-1)) {
                String message =
                    "Read only attribute \"title\" of the " + "element "
                        + element.getLocalName()
                        + " may not exist while create";
                LOGGER.info(message);
                throw new ReadonlyAttributeViolationException(message);
            }
            int indexOfHref =
                element.indexOfAttribute(Constants.XLINK_URI, "href");

            if (indexOfHref != (-1)) {
                String message =
                    "Read only attribute \"href\" of the " + "element "
                        + element.getLocalName()
                        + " may not exist while create";
                LOGGER.error(message);
                throw new ReadonlyAttributeViolationException(message);
            }
            try {
                Attribute type =
                    element.getAttribute(Constants.XLINK_URI, "type");
                if (!"simple".equals(type.getValue())) {
                    String message =
                        "Attribute " + Constants.XLINK_URI + ':'
                            + "type must be set to 'simple'";
                    throw new InvalidContentException(message);
                }
            }
            catch (NoSuchAttributeException e) {
                element.addAttribute(new Attribute("type", Constants.XLINK_URI,
                    Constants.XLINK_PREFIX, "simple"));
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
     * @see de.escidoc.core.common.util.xml.stax.handler.DefaultHandler#endElement
     *      (de.escidoc.core.common.util.xml.stax.events.EndElement)
     */
    @Override
    public EndElement endElement(final EndElement element) {
        if (inContentRelation && "relation".equals(element.getLocalName())) {
            inContentRelation = false;
            Map<String, String> relationData = new HashMap<String, String>();
            relationsData.add(relationData);
            relationData.put("predicate", predicate);
            relationData.put("target", targetIdWithoutVersion);
            relationData.put("targetVersion", targetVersion);
            targetId = null;
            targetIdWithoutVersion = null;
            targetVersion = null;
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

    /**
     * 
     * @param element
     * @throws InvalidContentException
     * @throws ReadonlyAttributeViolationException
     * @throws ReferencedResourceNotFoundException
     * @throws SystemException
     */
    private void checkRefElement(StartElement element)
        throws InvalidContentException, ReadonlyAttributeViolationException,
        ReferencedResourceNotFoundException, SystemException {
        try {
            String objectId = element.getAttribute(null, "objid").getValue();
            String xlinkHref =
                element.getAttribute(Constants.XLINK_URI, "href").getValue();
            targetId = XmlUtility.getIdFromURI(xlinkHref);
            if (!objectId.equals(targetId)) {
                String message =
                    "Value of the attribute 'href' is wrong. It must contain "
                        + objectId + " instead of " + targetId;
                LOGGER.info(message);
                throw new InvalidContentException(message);
            }
            targetIdWithoutVersion =
                XmlUtility.getObjidWithoutVersion(targetId);
            targetVersion = targetId.replaceFirst(targetIdWithoutVersion, "");
            if (targetVersion.length() > 0) {
                targetVersion = targetVersion.substring(1);
            }
            else {
                targetVersion = null;
            }

            String targetObjectType =
                TripleStoreUtility.getInstance().getObjectType(
                    targetIdWithoutVersion);
            targetExist(targetObjectType);
            if (!xlinkHref.equals("/ir/" + targetObjectType + '/' + targetId)) {
                String message =
                    "Value of the attribute 'href' is wrong. It must be"
                        + "/ir/" + targetObjectType + '/' + targetId;
                LOGGER.info(message);
                throw new InvalidContentException(message);
            }

            int indexOfTitle =
                element.indexOfAttribute(Constants.XLINK_URI, "title");
            if (indexOfTitle != (-1)) {
                String message =
                    "Read only attribute \"title\" of the " + "element "
                        + element.getLocalName()
                        + " may not exist while create";
                LOGGER.info(message);
                throw new ReadonlyAttributeViolationException(message);
            }
            // targetId = "<info:fedora/" + targetId + ">";

        }
        catch (NoSuchAttributeException e) {
            String msg =
                "Expected attribute in object reference " + "in 'relation' of "
                    + id + " is not set. (create item)";
            LOGGER.info(msg, e);
            throw new InvalidContentException(msg, e);

        }
    }

    /**
     * 
     * @param targetObjectType
     * @throws ReferencedResourceNotFoundException
     * @throws SystemException
     */
    private void targetExist(final String targetObjectType)
        throws ReferencedResourceNotFoundException, SystemException {
        if (!TripleStoreUtility.getInstance().exists(targetIdWithoutVersion)) {
            String message =
                "Referenced target resource with id " + targetIdWithoutVersion
                    + " does not exist.";
            LOGGER.error(message);
            throw new ReferencedResourceNotFoundException(message);

        }
        if (targetVersion != null) {
            String targetLatestVersion = null;
            if ("item".equals(targetObjectType)) {
                targetLatestVersion =
                    TripleStoreUtility.getInstance().getPropertiesElements(
                        targetIdWithoutVersion,
                        TripleStoreUtility.PROP_LATEST_VERSION_NUMBER);
            }

            else if ("container".equals(targetObjectType)) {
                targetLatestVersion =
                    TripleStoreUtility.getInstance().getPropertiesElements(
                        targetIdWithoutVersion,
                        TripleStoreUtility.PROP_LATEST_VERSION_NUMBER);
            }
            if (targetLatestVersion == null || Integer.parseInt(targetVersion) > Integer
                .parseInt(targetLatestVersion)) {
                String message =
                    "Referenced target resource with id "
                        + targetIdWithoutVersion + ':' + targetVersion
                        + " does not exist.";
                LOGGER.info(message);
                throw new ReferencedResourceNotFoundException(message);
            }
        }
    }

}
