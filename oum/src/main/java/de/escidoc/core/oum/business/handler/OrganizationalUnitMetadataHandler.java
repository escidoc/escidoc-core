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
package de.escidoc.core.oum.business.handler;

import de.escidoc.core.common.business.Constants;
import de.escidoc.core.common.exceptions.application.invalid.InvalidXmlException;
import de.escidoc.core.common.exceptions.application.missing.MissingAttributeValueException;
import de.escidoc.core.common.exceptions.application.missing.MissingMdRecordException;
import de.escidoc.core.common.util.stax.StaxParser;
import de.escidoc.core.common.util.xml.XmlUtility;
import de.escidoc.core.common.util.xml.stax.events.Attribute;
import de.escidoc.core.common.util.xml.stax.events.EndElement;
import de.escidoc.core.common.util.xml.stax.events.StartElement;

import javax.naming.directory.NoSuchAttributeException;
import java.util.HashMap;
import java.util.Map;

/**
 * The MetadataHandler.
 *
 * @author Michael Schneider
 */
public class OrganizationalUnitMetadataHandler extends OrganizationalUnitHandlerBase {

    private static final String SCHEMA = "schema";

    private static final String TYPE = "md-type";

    private static final String NAME = "name";

    private boolean insideMdRecord;

    private boolean rootMetadataElementFound;

    private String currentMdRecordName;

    private final String rootPath;

    private String dcTitle = "";

    private String escidocMetadataRecordNameSpace;

    private final Map<String, Map<String, String>> metadataAttributes = new HashMap<String, Map<String, String>>();

    private static final String MANDATORY_MD_RECORD_NAME = "escidoc";

    private boolean mandatoryMdRecordFound;

    /**
     * Instantiate a MetaDataHandler.
     *
     * @param parser   The parser.
     * @param rootPath XML root element path
     */
    public OrganizationalUnitMetadataHandler(final StaxParser parser, final String rootPath) {
        super(parser);
        this.rootPath = rootPath == null || "/".equals(rootPath) ? "" : rootPath;
    }

    /**
     * Handle the start of an element.
     *
     * @param element The element.
     * @return The element.
     * @throws MissingAttributeValueException If a required attribute is missing.
     */
    @Override
    public StartElement startElement(final StartElement element) throws InvalidXmlException,
        MissingAttributeValueException {

        final String elementName = element.getLocalName();

        if (getMdRecordPath().equals(getParser().getCurPath())) {
            this.insideMdRecord = true;
            try {
                final Attribute name = element.getAttribute(null, NAME);
                this.currentMdRecordName = name.getValue();

                if (currentMdRecordName.length() == 0) {
                    throw new MissingAttributeValueException("The value of attribute 'name' of the element "
                        + elementName + " was not set!");

                }
                else if (MANDATORY_MD_RECORD_NAME.equals(this.currentMdRecordName)) {
                    this.mandatoryMdRecordFound = true;
                }
            }
            catch (final NoSuchAttributeException e) {
                throw new MissingAttributeValueException("The mandatory attribute 'name' of the element " + elementName
                    + " was not found!", e);
            }
            final Map<String, String> md = new HashMap<String, String>();
            final int indexOfType = element.indexOfAttribute(null, TYPE);
            if (indexOfType == -1) {
                md.put("type", Constants.UNKNOWN);
            }
            else {
                md.put("type", element.getAttribute(indexOfType).getValue());
            }
            final int indexOfSchema = element.indexOfAttribute(null, SCHEMA);
            if (indexOfSchema == -1) {
                md.put(SCHEMA, Constants.UNKNOWN);
            }
            else {
                md.put(SCHEMA, element.getAttribute(indexOfSchema).getValue());
            }
            metadataAttributes.put(this.currentMdRecordName, md);

        }
        else if (this.insideMdRecord && !this.rootMetadataElementFound) {
            this.rootMetadataElementFound = true;
            if (this.currentMdRecordName.equals(MANDATORY_MD_RECORD_NAME)) {
                this.escidocMetadataRecordNameSpace = element.getNamespace();
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
    public EndElement endElement(final EndElement element) throws MissingMdRecordException {

        if (getMdRecordPath().equals(getParser().getCurPath())) {
            this.insideMdRecord = false;
            this.rootMetadataElementFound = false;
            this.currentMdRecordName = null;
        }
        else if (getMdRecordsPath().equals(getParser().getCurPath()) && !this.mandatoryMdRecordFound) {
            throw new MissingMdRecordException("Mandatory md-record with a name " + MANDATORY_MD_RECORD_NAME
                + " is missing.");
        }
        return element;
    }

    /**
     * Handle the character section of an element.
     *
     * @param s       The contents of the character section.
     * @param element The element.
     * @return The character section.
     */
    @Override
    public String characters(final String s, final StartElement element) {

        if (MANDATORY_MD_RECORD_NAME.equals(this.currentMdRecordName) && "title".equals(element.getLocalName())
            && Constants.DC_NS_URI.equals(element.getNamespace())) {
            this.dcTitle = s;
        }
        return s;
    }

    /**
     * @return Returns metadata attributes.
     */
    public Map<String, Map<String, String>> getMetadataAttributes() {
        return this.metadataAttributes;
    }

    /**
     * Retrieves a namespace uri of a child element of "md-record" element, whose attribute "name" set to "escidoc".
     *
     * @return Namespace of MetadataRecord
     */
    public String getEscidocMetadataRecordNameSpace() {
        return this.escidocMetadataRecordNameSpace;
    }

    /**
     * @return the rootPath
     */
    private String getRootPath() {
        return this.rootPath;
    }

    /**
     * @return the mdRecordsPath
     */
    public String getMdRecordsPath() {
        return getRootPath() + '/' + XmlUtility.NAME_MDRECORDS;
    }

    /**
     * @return the mdRecordpath
     */
    public String getMdRecordPath() {
        return getMdRecordsPath() + '/' + XmlUtility.NAME_MDRECORD;
    }

    /**
     * @return the dcTitle
     */
    public String getDcTitle() {
        return this.dcTitle;
    }

}
