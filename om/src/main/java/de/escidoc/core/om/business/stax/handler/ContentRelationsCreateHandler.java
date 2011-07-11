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
import de.escidoc.core.common.exceptions.system.TripleStoreSystemException;
import de.escidoc.core.common.util.stax.StaxParser;
import de.escidoc.core.common.util.xml.XmlUtility;
import de.escidoc.core.common.util.xml.stax.events.Attribute;
import de.escidoc.core.common.util.xml.stax.events.EndElement;
import de.escidoc.core.common.util.xml.stax.events.StartElement;
import de.escidoc.core.common.util.xml.stax.handler.DefaultHandler;
import de.escidoc.core.om.business.fedora.OntologyUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.naming.directory.NoSuchAttributeException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Rozita Friedman
 */
@Configurable
public class ContentRelationsCreateHandler extends DefaultHandler {

    @Autowired
    @Qualifier("business.TripleStoreUtility")
    private TripleStoreUtility tripleStoreUtility;

    private final StaxParser parser;

    private final String id;

    private static final String CONTAINER = "/container";

    private boolean inContentRelation;

    private String targetId;

    private String targetIdWithoutVersion;

    private String targetVersion;

    private String predicate;

    private List<Map<String, String>> relationsData;

    private static final Logger LOGGER = LoggerFactory.getLogger(ContentRelationsCreateHandler.class);

    /**
     * Instantiate a ContentRelationsCreateHandler.
     *
     * @param id     The id of the parsed object.
     * @param parser The parser.
     */
    public ContentRelationsCreateHandler(final String id, final StaxParser parser) {
        this.id = id;
        this.parser = parser;
    }

    /**
     * Handle the start of an element.
     *
     * @param element The element.
     * @return The element.
     * @throws SystemException Thrown in case of an internal error.
     */
    @Override
    public StartElement startElement(final StartElement element) throws ReadonlyAttributeViolationException,
        InvalidContentException, ReferencedResourceNotFoundException, RelationPredicateNotFoundException,
        SystemException {

        final String currentPath = parser.getCurPath();
        String contentRelationsPath = "/item/relations";
        String contentRelationPath = "/item/relations/relation";

        if (currentPath.startsWith(CONTAINER)) {
            contentRelationsPath = "/container/relations";
            contentRelationPath = "/container/relations/relation";
        }

        final String theName = element.getLocalName();

        if (contentRelationPath.equals(currentPath)) {
            this.inContentRelation = true;
            final int indexOfObjId = element.indexOfAttribute(null, "objid");
            if (indexOfObjId != -1) {
                throw new ReadonlyAttributeViolationException("Read only attribute \"objid\" of the " + "element "
                    + element.getLocalName() + " may not exist while create");
            }

        }
        if (this.inContentRelation) {
            if ("target".equals(theName)) {
                checkRefElement(element);
            }
            else if ("predicate".equals(theName)) {
                try {
                    final String xlinkHref = element.getAttribute(Constants.XLINK_URI, "href").getValue();
                    if (OntologyUtility.checkPredicate(xlinkHref)) {
                        this.predicate = xlinkHref;
                    }
                    else {
                        throw new RelationPredicateNotFoundException("Predicate " + xlinkHref + " is wrong. ");
                    }

                }
                catch (final NoSuchAttributeException e) {
                    throw new InvalidContentException("Attribute 'href' of the element '" + theName + "' is missing.",
                        e);
                }
                final int indexOfType = element.indexOfAttribute(Constants.XLINK_URI, "type");

                final Attribute type = element.getAttribute(indexOfType);
                final String typeValue = type.getValue();
                if (!typeValue.equals(Constants.XLINK_TYPE_SIMPLE)) {
                    throw new InvalidContentException("Attribute " + Constants.XLINK_URI + ':'
                        + "type must be set to 'simple'");
                }

            }
        }
        else if (contentRelationsPath.equals(currentPath)) {
            this.relationsData = new ArrayList<Map<String, String>>();
            final int indexOfTitle = element.indexOfAttribute(Constants.XLINK_URI, "title");

            if (indexOfTitle != -1) {
                throw new ReadonlyAttributeViolationException("Read only attribute \"title\" of the " + "element "
                    + element.getLocalName() + " may not exist while create");
            }
            final int indexOfHref = element.indexOfAttribute(Constants.XLINK_URI, "href");
            if (indexOfHref != -1) {
                throw new ReadonlyAttributeViolationException("Read only attribute \"href\" of the " + "element "
                    + element.getLocalName() + " may not exist while create");
            }
            try {
                final Attribute type = element.getAttribute(Constants.XLINK_URI, "type");
                if (!"simple".equals(type.getValue())) {
                    throw new InvalidContentException("Attribute " + Constants.XLINK_URI + ':'
                        + "type must be set to 'simple'");
                }
            }
            catch (final NoSuchAttributeException e) {
                if (LOGGER.isWarnEnabled()) {
                    LOGGER.warn("Error on getting attribute.");
                }
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Error on getting attribute.", e);
                }
                element.addAttribute(new Attribute("type", Constants.XLINK_URI, Constants.XLINK_PREFIX, "simple"));
            }

        }

        return element;
    }

    /**
     * Handle the end of an element.
     *
     * @param element The element.
     * @return The element.
     */
    @Override
    public EndElement endElement(final EndElement element) {
        if (this.inContentRelation && "relation".equals(element.getLocalName())) {
            this.inContentRelation = false;
            final Map<String, String> relationData = new HashMap<String, String>();
            relationsData.add(relationData);
            relationData.put("predicate", this.predicate);
            relationData.put("target", this.targetIdWithoutVersion);
            relationData.put("targetVersion", this.targetVersion);
            this.targetId = null;
            this.targetIdWithoutVersion = null;
            this.targetVersion = null;
            this.predicate = null;
        }
        return element;
    }

    /**
     * @return Returns the title.
     */
    public List<Map<String, String>> getContentRelationsData() {
        return this.relationsData;
    }

    /**
     *
     * @param element
     * @throws InvalidContentException
     * @throws ReadonlyAttributeViolationException
     * @throws ReferencedResourceNotFoundException
     * @throws SystemException
     * @throws de.escidoc.core.common.exceptions.system.WebserverSystemException
     * @throws de.escidoc.core.common.exceptions.system.TripleStoreSystemException
     */
    private void checkRefElement(final StartElement element) throws InvalidContentException,
        ReadonlyAttributeViolationException, ReferencedResourceNotFoundException, SystemException {
        try {
            final String objectId = element.getAttribute(null, "objid").getValue();
            final String xlinkHref = element.getAttribute(Constants.XLINK_URI, "href").getValue();
            this.targetId = XmlUtility.getIdFromURI(xlinkHref);
            if (!objectId.equals(this.targetId)) {
                throw new InvalidContentException("Value of the attribute 'href' is wrong. It must contain " + objectId
                    + " instead of " + this.targetId);
            }
            this.targetIdWithoutVersion = XmlUtility.getObjidWithoutVersion(this.targetId);
            this.targetVersion = targetId.replaceFirst(this.targetIdWithoutVersion, "");
            this.targetVersion = targetVersion.length() > 0 ? targetVersion.substring(1) : null;

            final String targetObjectType = this.tripleStoreUtility.getObjectType(this.targetIdWithoutVersion);
            targetExist(targetObjectType);
            if (!xlinkHref.equals("/ir/" + targetObjectType + '/' + this.targetId)) {
                throw new InvalidContentException("Value of the attribute 'href' is wrong. It must be" + "/ir/"
                    + targetObjectType + '/' + this.targetId);
            }

            final int indexOfTitle = element.indexOfAttribute(Constants.XLINK_URI, "title");
            if (indexOfTitle != -1) {
                throw new ReadonlyAttributeViolationException("Read only attribute \"title\" of the " + "element "
                    + element.getLocalName() + " may not exist while create");
            }
            // targetId = "<info:fedora/" + targetId + ">";

        }
        catch (final NoSuchAttributeException e) {
            throw new InvalidContentException("Expected attribute in object reference " + "in 'relation' of " + this.id
                + " is not set. (create item)", e);

        }
    }

    /**
     *
     * @param targetObjectType
     * @throws ReferencedResourceNotFoundException
     * @throws de.escidoc.core.common.exceptions.system.WebserverSystemException
     * @throws de.escidoc.core.common.exceptions.system.TripleStoreSystemException
     */
    private void targetExist(final String targetObjectType) throws ReferencedResourceNotFoundException,
        TripleStoreSystemException {
        if (!this.tripleStoreUtility.exists(this.targetIdWithoutVersion)) {
            throw new ReferencedResourceNotFoundException("Referenced target resource with id "
                + this.targetIdWithoutVersion + " does not exist.");

        }
        if (this.targetVersion != null) {
            String targetLatestVersion = null;
            if ("item".equals(targetObjectType) || "container".equals(targetObjectType)) {
                targetLatestVersion =
                    this.tripleStoreUtility.getPropertiesElements(this.targetIdWithoutVersion,
                        TripleStoreUtility.PROP_LATEST_VERSION_NUMBER);
            }
            if (targetLatestVersion == null
                || Integer.parseInt(this.targetVersion) > Integer.parseInt(targetLatestVersion)) {
                throw new ReferencedResourceNotFoundException("Referenced target resource with id "
                    + this.targetIdWithoutVersion + ':' + this.targetVersion + " does not exist.");
            }
        }
    }

}
