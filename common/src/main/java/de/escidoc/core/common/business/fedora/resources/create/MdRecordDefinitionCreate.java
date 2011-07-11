/*
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the Common Development and Distribution License, Version 1.0
 * only (the "License"). You may not use this file except in compliance with the License.
 *
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE or http://www.escidoc.de/license. See the License for
 * the specific language governing permissions and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each file and include the License file at
 * license/ESCIDOC.LICENSE. If applicable, add the following below this CDDL HEADER, with the fields enclosed by
 * brackets "[]" replaced with your own identifying information: Portions Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
 *
 * Copyright 2006-2011 Fachinformationszentrum Karlsruhe Gesellschaft fuer wissenschaftlich-technische Information mbH
 * and Max-Planck-Gesellschaft zur Foerderung der Wissenschaft e.V. All rights reserved. Use is subject to license
 * terms.
 */

package de.escidoc.core.common.business.fedora.resources.create;

import de.escidoc.core.common.exceptions.application.missing.MissingAttributeValueException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.util.configuration.EscidocConfiguration;
import de.escidoc.core.common.util.xml.XmlUtility;
import de.escidoc.core.common.util.xml.factory.ItemFoXmlProvider;
import de.escidoc.core.common.util.xml.factory.XmlTemplateProviderConstants;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * MetadataRecordDefinition for create. This his a helper construct until all values can be handled within the standard
 * MdRecord class.
 * <p/>
 * This class represent a metadata record.
 *
 * @author Frank Schwichtenberg
 */
public class MdRecordDefinitionCreate {

    private ByteArrayOutputStream mdRecordDefinition;

    private String mdRecordDefinitionName;

    /*
     * Required as alternate ID.
     */
    private static final String TYPE = null;

    private String schemaHref;

    /**
     * Set Name of Metadata Record.
     *
     * @param name Name of MdRecord.
     * @throws MissingAttributeValueException Thrown if name is an empty String.
     */
    public void setName(final String name) throws MissingAttributeValueException {

        if (name == null || name.length() == 0) {
            throw new MissingAttributeValueException("the value of the"
                + " \"name\" atribute of the element 'name' is missing");
        }

        this.mdRecordDefinitionName = name;
    }

    /**
     * Get Name of Metadata Record.
     *
     * @return name of metadata record.
     */
    public String getName() {

        return this.mdRecordDefinitionName;
    }

    /**
     * Set schemaHref for metadata record.
     *
     * @param schemaHref XML SchemaHref URL
     * @throws IOException           If the eSciDoc configuration file can not be read. FIXME should probably not be
     *                               thrown so late.
     */
    public void setSchemaHref(final String schemaHref) throws IOException {
        final URL url =
            schemaHref.startsWith("/") ? new URL(EscidocConfiguration.getInstance().get(
                EscidocConfiguration.ESCIDOC_CORE_BASEURL)
                + schemaHref) : new URL(schemaHref);
        this.schemaHref = url.toString();
    }

    /**
     * Get schemaHref of metadata record.
     *
     * @return XML SchemaHref URL
     */
    public String getSchemaHref() {

        return this.schemaHref;
    }

    /**
     * Set Content of MetadataRecord (the record itself).
     *
     * @param mdRecordDefinition The content itself.
     */
    public void setMdRecordDefinition(final ByteArrayOutputStream mdRecordDefinition) {

        this.mdRecordDefinition = mdRecordDefinition;
    }

    /**
     * Get OutputStream of MdRecord.
     *
     * @return Content of MdRecord.
     */
    public ByteArrayOutputStream getMdRecordDefinition() {

        if (this.mdRecordDefinition == null) {
            this.mdRecordDefinition = new ByteArrayOutputStream();
        }
        return this.mdRecordDefinition;
    }

    /**
     * Persist Component to Repository.
     *
     * @return FoXML representation of metadata record.
     * @throws SystemException Thrown if rendering failed.
     */
    public String getFOXML() throws SystemException {

        final Map<String, String> templateValues = getValueMap();
        return ItemFoXmlProvider.getInstance().getMetadataFoXml(templateValues);
    }

    /**
     * Get a HashMap with all values of MdRecord (for FoXML renderer).
     *
     * @return HashMap with all values of MdRecord
     * @throws SystemException Thrown if character encoding failed.
     */
    public Map<String, String> getValueMap() throws SystemException {

        final Map<String, String> templateValues = new HashMap<String, String>();

        templateValues.put(XmlTemplateProviderConstants.MD_RECORD_TYPE, TYPE);
        templateValues.put(XmlTemplateProviderConstants.MD_RECORD_SCHEMA, this.schemaHref);
        templateValues.put(XmlTemplateProviderConstants.MD_RECORD_NAME, this.mdRecordDefinitionName);

        // add Metadata (as BLOB)
        try {
            templateValues.put(XmlTemplateProviderConstants.MD_RECORD_CONTENT, getMdRecordDefinition().toString(
                XmlUtility.CHARACTER_ENCODING).trim());
        }
        catch (final UnsupportedEncodingException e) {
            throw new SystemException(e);
        }

        return templateValues;
    }

}
