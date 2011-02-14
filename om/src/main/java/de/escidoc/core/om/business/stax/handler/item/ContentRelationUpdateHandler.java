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
import de.escidoc.core.common.business.fedora.datastream.Datastream;
import de.escidoc.core.common.exceptions.application.invalid.InvalidContentException;
import de.escidoc.core.common.exceptions.application.missing.MissingAttributeValueException;
import de.escidoc.core.common.exceptions.application.notfound.ContentRelationNotFoundException;
import de.escidoc.core.common.exceptions.application.violated.OptimisticLockingException;
import de.escidoc.core.common.exceptions.system.EncodingSystemException;
import de.escidoc.core.common.exceptions.system.FedoraSystemException;
import de.escidoc.core.common.exceptions.system.IntegritySystemException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.util.logger.AppLogger;
import de.escidoc.core.common.util.stax.StaxParser;
import de.escidoc.core.common.util.stax.handler.OptimisticLockingHandler;
import de.escidoc.core.common.util.string.StringUtility;
import de.escidoc.core.common.util.xml.Elements;
import de.escidoc.core.common.util.xml.stax.events.EndElement;
import de.escidoc.core.common.util.xml.stax.events.StartElement;
import de.escidoc.core.common.util.xml.stax.handler.DefaultHandler;

import javax.naming.directory.NoSuchAttributeException;
import javax.xml.stream.XMLStreamException;
import java.util.HashMap;

/**
 * Handle ContentRelation XML to obtain all required values (Properties,
 * Metadata, .. ).
 * 
 * @author SWA
 * 
 */
public class ContentRelationUpdateHandler extends DefaultHandler {

    private static final AppLogger LOG =
        new AppLogger(ContentRelationUpdateHandler.class.getName());

    private static final String XPATH_CONTENT_RELATION =
        "/" + Elements.ELEMENT_CONTENT_RELATION;

    private static final String XPATH_CONTENT_RELATION_METADATA =
        XPATH_CONTENT_RELATION + "/md-records/md-record";

    private static final String XPATH_DESCRIPTION =
        XPATH_CONTENT_RELATION + "/description";

    private HashMap<String, Datastream> mdRecords = null;

    private StaxParser parser;

    private boolean parsingMetaData = false;

    private boolean parsingDescription = false;

    private MetadataHandlerUpdateContentRelations metadataHandler = null;

    private OptimisticLockingHandler optimisticLockingHandler = null;

    // helper to collect a characters for type

    private String tmpDescription = null;

    private String id = null;

    private String lmd = null;

    /**
     * ContentRelationHandler.
     * 
     * @param parser
     *            StAX Parser.
     * @throws SystemException
     *             Thrown if obtaining UserContext failed.
     */
    public ContentRelationUpdateHandler(final StaxParser parser,
        final String crId, final String lastModDate) throws SystemException,
        ContentRelationNotFoundException {

        this.parser = parser;
        this.id = crId;
        this.lmd = lastModDate;
        this.mdRecords = new HashMap<String, Datastream>();
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
        throws InvalidContentException, XMLStreamException,
        WebserverSystemException, MissingAttributeValueException,
        OptimisticLockingException {
        if (this.parsingMetaData) {
            this.metadataHandler.startElement(element);
        }
        else {
            // check if href or objid are correct
            String currentPath = parser.getCurPath();
            if (XPATH_CONTENT_RELATION.equals(currentPath)) {
                String href;
                String objid;
                try {
                    href =
                        element
                            .getAttribute(Constants.XLINK_URI, "href")
                            .getValue();
                    final String expectedHref =
                        Constants.CONTENT_RELATION_URL_BASE + this.id;
                    // check href
                    if (!href.equals(expectedHref)) {
                        throw new InvalidContentException(StringUtility
                            .format(
                                "Attribute xlink:href has invalid value.",
                                href, expectedHref));
                    }
                }
                catch (NoSuchAttributeException e) {

                }
                try {
                    objid = element.getAttribute(null, "objid").getValue();
                    if (!objid.equals(this.id)) {
                        throw new InvalidContentException(StringUtility
                            .format(
                                    "Attribute objid has invalid value.", objid,
                                    this.id));
                    }

                }
                catch (NoSuchAttributeException e) {
                }

                // check optimistic locking

                this.optimisticLockingHandler =
                    new OptimisticLockingHandler(id,
                        Constants.CONTENT_RELATION2_OBJECT_TYPE, this.lmd,
                        parser);
                this.optimisticLockingHandler.startElement(element);
            }
            else if (XPATH_CONTENT_RELATION_METADATA.equals(currentPath)) {
                this.parsingMetaData = true;
                this.metadataHandler =
                    new MetadataHandlerUpdateContentRelations(parser,
                        XPATH_CONTENT_RELATION_METADATA, this.id);
                this.metadataHandler.startElement(element);
            }
            else if (XPATH_DESCRIPTION.equals(currentPath)) {
                this.parsingDescription = true;
                this.tmpDescription = "";
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
     * @throws XMLStreamException
     */
    @Override
    public EndElement endElement(final EndElement element)
        throws WebserverSystemException, InvalidContentException {

        String currentPath = parser.getCurPath();
        if (XPATH_CONTENT_RELATION_METADATA.equals(currentPath)) {
            LOG.debug("Parser reached end of "
                + XPATH_CONTENT_RELATION_METADATA);
            // parser leaves the XML md-records element
            this.parsingMetaData = false;
            this.metadataHandler.endElement(element);
            Datastream mdRecord = this.metadataHandler.getMetadataRecord();
            if (this.mdRecords.containsKey(mdRecord.getName())) {
                String message =
                    "A md-record with the name '" + mdRecord.getName()
                        + "' occurs multiple times "
                        + "in the representation of a content relation.";
                LOG.error(message);
                throw new InvalidContentException(message);
            }
            this.mdRecords.put(mdRecord.getName(), mdRecord);
            this.metadataHandler = null;
        }
        else if (XPATH_DESCRIPTION.equals(currentPath)) {
            LOG.debug("Parser reached end of " + XPATH_DESCRIPTION);
            // parser leaves the XML description element
            this.parsingDescription = false;
        }
        else {
            if (this.parsingMetaData) {
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
     * @throws XMLStreamException
     * 
     */
    @Override
    public String characters(final String s, final StartElement element)
        throws WebserverSystemException {

        if (this.parsingMetaData) {
            this.metadataHandler.characters(s, element);
        }
        if (this.parsingDescription) {
            this.tmpDescription += s;
        }

        return s;
    }

    /**
     * Get the ContentRelation.
     * 
     * 
     * @return ContentRelationCreate
     */
    public HashMap<String, Datastream> getMdRecords()
        throws IntegritySystemException, FedoraSystemException,
        WebserverSystemException, EncodingSystemException {

        return this.mdRecords;
    }

    public String getDescription() {
        return this.tmpDescription;
    }

}
