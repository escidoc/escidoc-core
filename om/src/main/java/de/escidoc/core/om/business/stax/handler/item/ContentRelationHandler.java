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
import de.escidoc.core.common.business.fedora.Utility;
import de.escidoc.core.common.business.fedora.resources.create.ContentRelationCreate;
import de.escidoc.core.common.exceptions.application.invalid.InvalidContentException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidStatusException;
import de.escidoc.core.common.exceptions.application.missing.MissingAttributeValueException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.util.logger.AppLogger;
import de.escidoc.core.common.util.stax.StaxParser;
import de.escidoc.core.common.util.xml.Elements;
import de.escidoc.core.common.util.xml.XmlUtility;
import de.escidoc.core.common.util.xml.stax.events.Attribute;
import de.escidoc.core.common.util.xml.stax.events.EndElement;
import de.escidoc.core.common.util.xml.stax.events.StartElement;
import de.escidoc.core.common.util.xml.stax.handler.DefaultHandler;

import javax.naming.directory.NoSuchAttributeException;
import javax.xml.stream.XMLStreamException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Handle ContentRelation XML to obtain all required values (Properties,
 * Metadata, .. ).
 * 
 * @author SWA
 * 
 */
public class ContentRelationHandler extends DefaultHandler {

    private static final AppLogger LOG =
        new AppLogger(ContentRelationHandler.class.getName());

    private static final String XPATH_CONTENT_RELATION =
            '/' + Elements.ELEMENT_CONTENT_RELATION;

    private static final String XPATH_CONTENT_RELATION_PROPERTIES =
        XPATH_CONTENT_RELATION + "/properties";

    private static final String XPATH_CONTENT_RELATION_METADATA =
        XPATH_CONTENT_RELATION + "/md-records/md-record";

    private static final String XPATH_TYPE = XPATH_CONTENT_RELATION + "/type";

    private static final String XPATH_SUBJECT =
        XPATH_CONTENT_RELATION + "/subject";

    private static final String XPATH_OBJECT =
        XPATH_CONTENT_RELATION + "/object";

    private final StaxParser parser;

    private boolean parsingProperties = false;

    private boolean parsingMetaData = false;

    private boolean parsingType = false;

    private ContentRelationPropertiesHandler propertiesHandler = null;

    private MetadataHandler2 metadataHandler = null;

    private ContentRelationCreate contentRelation = null;

    // helper to collect a characters for type
    private String tmpType = null;

    /**
     * ContentRelationHandler.
     * 
     * @param parser
     *            StAX Parser.
     * @throws WebserverSystemException
     *             Thrown if obtaining UserContext failed.
     */
    public ContentRelationHandler(final StaxParser parser)
        throws WebserverSystemException {

        this.parser = parser;
        this.contentRelation = new ContentRelationCreate();
    }

    /**
     * Parser hits an XML start element.
     * 
     * @param element
     *            StAX Parser StartElement
     * @return StartElement The StartElement.
     * @throws InvalidContentException
     *             Thrown if metadata content is invalid
     * @throws MissingAttributeValueException
     *             Thrown if attributes of metadata elements are missing
     * @throws XMLStreamException
     *             Thrown if metadata stream is unable to parse
     * @throws WebserverSystemException
     *             Thrown if setting ContentRelationProperties failed (obtaining
     *             UserContext error).
     */
    @Override
    public StartElement startElement(final StartElement element)
        throws InvalidContentException, MissingAttributeValueException,
        XMLStreamException, WebserverSystemException {

        if (this.parsingProperties) {
            this.propertiesHandler.startElement(element);
        }
        else if (this.parsingMetaData) {
            this.metadataHandler.startElement(element);
        }
        else {
            String currentPath = parser.getCurPath();

            if (XPATH_CONTENT_RELATION_PROPERTIES.equals(currentPath)) {
                LOG
                    .debug("Parser reached "
                        + XPATH_CONTENT_RELATION_PROPERTIES);

                this.parsingProperties = true;
                this.propertiesHandler =
                    new ContentRelationPropertiesHandler(parser);
                this.propertiesHandler.startElement(element);
            }
            else if (XPATH_CONTENT_RELATION_METADATA.equals(currentPath)) {
                this.parsingMetaData = true;
                this.metadataHandler =
                    new MetadataHandler2(parser,
                        XPATH_CONTENT_RELATION_METADATA);
                this.metadataHandler.startElement(element);
            }
            else if (XPATH_SUBJECT.equals(currentPath)) {
                String objid = handleReferences(element);
                if (objid != null) {
                    String subjectIdWithoutVersion =
                        XmlUtility.getObjidWithoutVersion(objid);
                    this.contentRelation.setSubject(subjectIdWithoutVersion);
                    String subjectVersion =
                        XmlUtility.getVersionNumberFromObjid(objid);
                    this.contentRelation.setSubjectVersion(subjectVersion);
                }
            }
            else if (XPATH_OBJECT.equals(currentPath)) {
                String objid = handleReferences(element);
                if (objid != null) {
                    String objectIdWithoutVersion =
                        XmlUtility.getObjidWithoutVersion(objid);
                    this.contentRelation.setObject(objectIdWithoutVersion);
                    String objectVersion =
                        XmlUtility.getVersionNumberFromObjid(objid);
                    this.contentRelation.setObjectVersion(objectVersion);
                }
            }
            else if (XPATH_TYPE.equals(currentPath)) {
                this.parsingType = true;
                this.tmpType = "";
            }
        }

        return element;
    }

    /**
     * Parser hits an XML end element.
     * 
     * @param element
     *            StAX EndElement
     * @return StAX EndElement
     * 
     * @throws InvalidContentException
     *             Thrown if metadata has invalid content.
     * @throws WebserverSystemException
     *             Thrown if streaming failed.
     */
    @Override
    public EndElement endElement(final EndElement element)
        throws InvalidContentException, WebserverSystemException {

        String currentPath = parser.getCurPath();

        if (XPATH_CONTENT_RELATION_PROPERTIES.equals(currentPath)) {
            LOG.debug("Parser reached end of "
                + XPATH_CONTENT_RELATION_PROPERTIES);
            // parser leaves the XML component element
            this.parsingProperties = false;
            this.propertiesHandler.endElement(element);
            this.contentRelation.setProperties(this.propertiesHandler
                .getProperties());
            this.propertiesHandler = null;
        }
        else if (XPATH_CONTENT_RELATION_METADATA.equals(currentPath)) {
            LOG.debug("Parser reached end of "
                + XPATH_CONTENT_RELATION_METADATA);
            // parser leaves the XML md-records element
            this.parsingMetaData = false;
            this.metadataHandler.endElement(element);
            this.contentRelation.addMdRecord(this.metadataHandler
                .getMetadataRecord());
            this.metadataHandler = null;
        }
        else if (XPATH_TYPE.equals(currentPath)) {
            LOG.debug("Parser reached end of " + XPATH_TYPE);
            // parser leaves the XML type element
            this.parsingType = false;
            try {
                this.contentRelation.setType(new URI(this.tmpType));
            }
            catch (URISyntaxException e) {
                throw new InvalidContentException(e);
            }
        }
        else {
            if (this.parsingProperties) {
                this.propertiesHandler.endElement(element);
            }
            else if (this.parsingMetaData) {
                this.metadataHandler.endElement(element);
            }
        }

        return element;
    }

    /**
     * Parser hits an XML character element.
     * 
     * @param s
     *            XML character element.
     * @param element
     *            StAX StartElement
     * @return XML character element.
     * 
     * @throws InvalidStatusException
     *             Thrown if value of status is invalid text.
     * @throws WebserverSystemException
     *             Thrown if streaming failed.
     */
    @Override
    public String characters(final String s, final StartElement element)
        throws WebserverSystemException, InvalidStatusException {

        if (this.parsingProperties) {
            this.propertiesHandler.characters(s, element);
        }
        else if (this.parsingMetaData) {
            this.metadataHandler.characters(s, element);
        }
        else if (this.parsingType) {
            this.tmpType += s;
        }

        return s;
    }

    /**
     * Get the ContentRelation.
     * 
     * Attention! ContentRelationCreate is only a transition object. Later
     * implementation has to return the ContentRelationCreate class.
     * 
     * @return ContentRelationCreate
     */
    public ContentRelationCreate getContentRelation() {

        return this.contentRelation;
    }

    /**
     * Handle href or objid references. Version number is kept.
     * 
     * @param element
     *            StartElement
     * @return objid (with version number if set)
     * @throws MissingAttributeValueException
     *             Thrown if element has neither objid nor href attribute
     */
    private String handleReferences(final StartElement element)
        throws MissingAttributeValueException {

        String objid;
        try {
            Attribute curAttr =
                element.getAttribute(null, Elements.ATTRIBUTE_XLINK_OBJID);
            objid = curAttr.getValue();
        }
        catch (NoSuchAttributeException e) {
            try {
                Attribute curAttr =
                    element.getAttribute(Constants.XLINK_NS_URI,
                        Elements.ATTRIBUTE_XLINK_HREF);
                objid = Utility.getId(curAttr.getValue());
            }
            catch (NoSuchAttributeException e1) {
                throw new MissingAttributeValueException(
                    "objid or href is missing for subject reference", e);
            }
        }

        return objid;
    }
}
