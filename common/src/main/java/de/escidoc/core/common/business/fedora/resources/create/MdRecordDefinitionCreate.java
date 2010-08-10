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
package de.escidoc.core.common.business.fedora.resources.create;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import de.escidoc.core.common.exceptions.application.missing.MissingAttributeValueException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.util.configuration.EscidocConfiguration;
import de.escidoc.core.common.util.logger.AppLogger;
import de.escidoc.core.common.util.xml.XmlUtility;
import de.escidoc.core.common.util.xml.factory.ItemFoXmlProvider;
import de.escidoc.core.common.util.xml.factory.XmlTemplateProvider;

/**
 * MetadataRecordDefinition for create. This his a helper construct until all
 * values can be handled within the standard MdRecord class.
 * 
 * This class represent a metadata record.
 * 
 * @author FRS
 * 
 */
public class MdRecordDefinitionCreate {

    private static final AppLogger LOG =
        new AppLogger(MdRecordDefinitionCreate.class.getName());

    private ByteArrayOutputStream mdRecordDefinition = null;

    private String nameSpace = null;

    private String mdRecordDefinitionName = null;

    /*
     * Required as alternate ID.
     */
    private String type = null;

    private String schemaHref = null;

    private boolean hasSchemaHref;

    /**
     * Metadata Record Datastructure.
     */
    public MdRecordDefinitionCreate() {

    }

    /**
     * Set Name of Metadata Record.
     * 
     * @param name
     *            Name of MdRecord.
     * @throws MissingAttributeValueException
     *             Thrown if name is an empty String.
     */
    public void setName(final String name)
        throws MissingAttributeValueException {

        if ((name == null) || name.equals("")) {
            final String errorMsg = "the value of the" +
                    " \"name\" atribute of the element 'name' is missing";
            LOG.debug(errorMsg);
            throw new MissingAttributeValueException(errorMsg);
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
     * @param schemaHref
     *            XML SchemaHref URL
     * @throws MalformedURLException
     *             If the given schema href is no valid URL.
     * @throws IOException
     *             If the eSciDoc configuration file can not be read. FIXME
     *             should probably not be thrown so late.
     */
    public void setSchemaHref(final String schemaHref)
        throws MalformedURLException, IOException {
        URL url;
        if (schemaHref.startsWith("/")) {
            try {
                url = new URL(EscidocConfiguration.getInstance().get(
                    EscidocConfiguration.ESCIDOC_CORE_BASEURL)
                    + schemaHref);
            }
            catch (MalformedURLException e) {
                throw e;
            }
            // FIXME how to handle IOException from configuration
        }
        else {
            url = new URL(schemaHref);
        }
        this.schemaHref = url.toString();
        this.hasSchemaHref = true;
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
     * @param mdRecordDefinition
     *            The content itself.
     */
    public void setMdRecordDefinition(
        final ByteArrayOutputStream mdRecordDefinition) {

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
     * 
     * Persist Component to Repository.
     * 
     * @return FoXML representation of metadata record.
     * @throws SystemException
     *             Thrown if rendering failed.
     */
    public String getFOXML() throws SystemException {

        HashMap<String, String> templateValues = getValueMap();
        return ItemFoXmlProvider.getInstance().getMetadataFoXml(templateValues);
    }

    /**
     * Get a HashMap with all values of MdRecord (for FoXML renderer).
     * 
     * @return HashMap with all values of MdRecord
     * @throws SystemException
     *             Thrown if character encoding failed.
     */
    public HashMap<String, String> getValueMap() throws SystemException {

        HashMap<String, String> templateValues = new HashMap<String, String>();

        templateValues.put(XmlTemplateProvider.MD_RECORD_TYPE, this.type);
        templateValues.put(XmlTemplateProvider.MD_RECORD_SCHEMA,
            this.schemaHref);
        templateValues.put(XmlTemplateProvider.MD_RECORD_NAME,
            this.mdRecordDefinitionName);

        // add Metadata (as BLOB)
        try {
            templateValues.put(XmlTemplateProvider.MD_RECORD_CONTENT,
                getMdRecordDefinition()
                    .toString(XmlUtility.CHARACTER_ENCODING).trim());
        }
        catch (UnsupportedEncodingException e) {
            LOG.error(e.getMessage());
            throw new SystemException(e);
        }

        return templateValues;
    }

    public void setHasSchemaHref(boolean b) {
        this.hasSchemaHref = b;

    }

}
