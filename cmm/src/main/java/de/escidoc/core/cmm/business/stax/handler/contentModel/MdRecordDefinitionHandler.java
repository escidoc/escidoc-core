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
package de.escidoc.core.cmm.business.stax.handler.contentModel;

import de.escidoc.core.common.business.Constants;
import de.escidoc.core.common.business.fedora.resources.create.MdRecordDefinitionCreate;
import de.escidoc.core.common.exceptions.application.invalid.InvalidContentException;
import de.escidoc.core.common.exceptions.application.missing.MissingAttributeValueException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.util.stax.StaxParser;
import de.escidoc.core.common.util.xml.stax.events.EndElement;
import de.escidoc.core.common.util.xml.stax.events.StartElement;
import de.escidoc.core.common.util.xml.stax.handler.DefaultHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.directory.NoSuchAttributeException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Handler to extract md-record definitions from content-model xml by StaxParser.
 *
 * @author Frank Schwichtenberg
 */
public class MdRecordDefinitionHandler extends DefaultHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(MdRecordDefinitionHandler.class);

    private final StaxParser parser;

    private String metadataXPath = "/content-model/md-record-definitions/md-record-definition";

    private final List<MdRecordDefinitionCreate> mdRecordDefinitions;

    private MdRecordDefinitionCreate curMdRecordDefinition;

    public List<MdRecordDefinitionCreate> getMdRecordDefinitions() {
        return this.mdRecordDefinitions;
    }

    /**
     * Instantiate a MdRecordDefinitionHandler.
     *
     * @param parser The parser.
     * @param xpath  Metadata Records XPath (e.g. /item/md-records).
     */
    public MdRecordDefinitionHandler(final StaxParser parser, final String xpath) {

        this.mdRecordDefinitions = new ArrayList<MdRecordDefinitionCreate>();
        this.parser = parser;
        this.metadataXPath = xpath + "/md-record-definition";
    }

    /**
     * Handle the start of an element.
     *
     * @param element The element.
     * @return The element.
     * @throws MissingAttributeValueException If a required element is not set.
     * @throws InvalidContentException        If the parsed XML contains not allowed parts.
     * @throws WebserverSystemException       If the eSciDoc configuration file can not be read. FIXME should probably
     *                                        not be thrown so late.
     */
    @Override
    public StartElement startElement(final StartElement element) throws MissingAttributeValueException,
        InvalidContentException, WebserverSystemException {

        final String currentPath = parser.getCurPath();
        if (currentPath.equals(this.metadataXPath)) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Parser reached " + this.metadataXPath);
            }
            this.curMdRecordDefinition = new MdRecordDefinitionCreate();

            try {
                curMdRecordDefinition.setName(element.getAttributeValue(null, "name"));
            }
            catch (final NoSuchAttributeException e) {
                throw new InvalidContentException("Attribute name required for md-record-definition.", e);
            }
        }
        else if (currentPath.equals(this.metadataXPath + "/schema")) {

            try {
                curMdRecordDefinition.setSchemaHref(element.getAttributeValue(Constants.XLINK_NS_URI, "href"));
            }
            catch (final MalformedURLException e) {
                throw new InvalidContentException(e);
            }
            catch (final IOException e) {
                throw new WebserverSystemException("Configuration could not be read.", e);
            }
            catch (final NoSuchAttributeException e) {
                throw new InvalidContentException("No href for schema element.", e);
            }
        }

        return element;
    }

    @Override
    public EndElement endElement(final EndElement element) {

        final String currentPath = parser.getCurPath();
        if (currentPath.equals(this.metadataXPath)) {

            this.mdRecordDefinitions.add(this.curMdRecordDefinition);
        }

        return element;
    }

}
