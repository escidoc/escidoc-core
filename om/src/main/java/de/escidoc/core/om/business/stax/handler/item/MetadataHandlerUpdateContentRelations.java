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

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

import javax.xml.stream.XMLStreamException;

import de.escidoc.core.common.business.fedora.datastream.Datastream;
import de.escidoc.core.common.business.fedora.resources.create.MdRecordCreate;
import de.escidoc.core.common.exceptions.application.invalid.InvalidContentException;
import de.escidoc.core.common.exceptions.application.missing.MissingAttributeValueException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.util.logger.AppLogger;
import de.escidoc.core.common.util.stax.StaxParser;
import de.escidoc.core.common.util.stax.handler.MultipleExtractor;
import de.escidoc.core.common.util.xml.Elements;
import de.escidoc.core.common.util.xml.factory.XmlTemplateProvider;
import de.escidoc.core.common.util.xml.stax.events.EndElement;
import de.escidoc.core.common.util.xml.stax.events.StartElement;
import de.escidoc.core.common.util.xml.stax.handler.DefaultHandler;

/**
 * The MetadataHandler. The parser handles only one Metadata Record!
 * 
 * @author SWA
 * 
 * @om
 */
public class MetadataHandlerUpdateContentRelations extends DefaultHandler {

    private static final AppLogger LOG =
        new AppLogger(MetadataHandlerUpdateContentRelations.class.getName());

    private final StaxParser parser;

    private String metadataXPath = "//md-record";

    private Datastream metadataRecord = null;

    private String mdType = null;

    private String name = null;

    private String schema = null;

    private String nameSpace = null;

    private boolean parsingMetadata = false;

    private MultipleExtractor me = null;

    private boolean payloadRootElement = false;

    private String contentRelationId = null;

    /**
     * Instantiate a MetaDataHandler.
     * 
     * @param parser
     *            The parser.
     * @param xpath
     *            Metadata Records XPath (e.g. /item/md-records/md-record).
     */
    public MetadataHandlerUpdateContentRelations(final StaxParser parser,
        final String xpath, final String crId) {

        this.parser = parser;
        this.metadataXPath = xpath;
        this.contentRelationId = crId;
    }

    /**
     * Set XPath for metadata record.
     * 
     * @param xpath
     *            Metadata Records XPath (e.g. /item/md-records/md-record).
     */
    public void setMetadataRecordXPath(final String xpath) {

        this.metadataXPath = xpath;
    }

    /**
     * Handle the start of an element.
     * 
     * @param element
     *            The element.
     * @return The element.
     * @throws MissingAttributeValueException
     * @throws MissingAttributeValueException
     *             If a required attribute is missing.
     * @throws XMLStreamException
     * @throws InvalidContentException
     * @see de.escidoc.core.common.util.xml.stax.handler.DefaultHandler#startElement
     *      (de.escidoc.core.common.util.xml.stax.events.StartElement)
     */
    @Override
    public StartElement startElement(final StartElement element)
        throws InvalidContentException, WebserverSystemException {

        if (this.parsingMetadata) {
            this.me.startElement(element);

            // extract namespace and attributes 'type' from payload root element
            if (this.payloadRootElement) {
                this.nameSpace = element.getNamespace();
                this.payloadRootElement = false;
            }
        }
        else {
            String currentPath = parser.getCurPath();
            if (currentPath.equals(this.metadataXPath)) {
                LOG.debug("Parser reached " + this.metadataXPath);
                this.parsingMetadata = true;

                this.mdType = getAttributeValue(element, null, "md-type");
                this.name = getAttributeValue(element, null, "name");
                this.schema = getAttributeValue(element, null, "schema");
                this.payloadRootElement = true;
                this.me =
                    new MultipleExtractor(this.metadataXPath, "name", parser);
                this.me.startElement(element);
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
     * @throws XMLStreamException
     * @see de.escidoc.core.common.util.xml.stax.handler.DefaultHandler#endElement
     *      (de.escidoc.core.common.util.xml.stax.events.EndElement)
     * @om
     */
    @Override
    public EndElement endElement(final EndElement element)
        throws WebserverSystemException {

        if (this.metadataXPath.equals(parser.getCurPath())
            && this.parsingMetadata) {
            LOG.debug("End of " + this.metadataXPath);

            this.parsingMetadata = false;

            this.me.endElement(element);
            HashMap<String, ?> tmp =
                (HashMap<String, ?>) this.me.getOutputStreams().get(
                    Elements.ELEMENT_MD_RECORDS);
            HashMap<String, String> mdProperties =
                new HashMap<String, String>();
            if (XmlTemplateProvider.DEFAULT_METADATA_FOR_DC_MAPPING
                .equals(this.name)) {
                mdProperties.put("nsUri", this.nameSpace);
            }
            this.metadataRecord =
                new Datastream(this.name, this.contentRelationId,
                    ((ByteArrayOutputStream) tmp.get(name)).toByteArray(),
                    "text/xml", mdProperties);
            this.metadataRecord
                .addAlternateId(Datastream.METADATA_ALTERNATE_ID);
            if (this.mdType == null) {
                this.mdType = "unknown";
            }
            this.metadataRecord.addAlternateId(this.mdType);
            if (this.schema == null) {
                this.schema = "unknown";
            }
            this.metadataRecord.addAlternateId(this.schema);
            this.me = null;
        }
        else if (this.parsingMetadata) {
            this.me.endElement(element);
        }

        return element;
    }

    /**
     * Handle the character section of an element.
     * 
     * @param s
     *            The contents of the character section.
     * @param element
     *            The element.
     * @return The character section.
     * @throws XMLStreamException
     * @see de.escidoc.core.common.util.xml.stax.handler.DefaultHandler#characters
     *      (java.lang.String,
     *      de.escidoc.core.common.util.xml.stax.events.StartElement)
     * @om
     */
    @Override
    public String characters(final String s, final StartElement element)
        throws WebserverSystemException {

        if (this.parsingMetadata) {
            this.me.characters(s, element);
        }

        return s;
    }

    /**
     * Get data structure MetadataRecord.
     * 
     * @return Return the MetadataRecord.
     */
    public Datastream getMetadataRecord() {

        return this.metadataRecord;
    }

}
