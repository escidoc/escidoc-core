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

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import de.escidoc.core.common.exceptions.application.missing.MissingAttributeValueException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.util.configuration.EscidocConfiguration;
import de.escidoc.core.common.util.logger.AppLogger;
import de.escidoc.core.common.util.xml.factory.ItemFoXmlProvider;

/**
 * MetadataRecordDefinition for create. This his a helper construct until all
 * values can be handled within the standard MdRecord class.
 * 
 * This class represent a metadata record.
 * 
 * @author FRS
 * 
 */
public class ResourceDefinitionCreate {

    private static final AppLogger LOG = new AppLogger(
        ResourceDefinitionCreate.class.getName());

    private String name = null;

    private String xsltHref = null;

    private String mdRecordName = null;

    /**
     * Metadata Record Datastructure.
     */
    public ResourceDefinitionCreate() {

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
            final String errorMsg =
                "the value of the"
                    + " \"name\" atribute of the element 'resource-definition' is missing";
            LOG.debug(errorMsg);
            throw new MissingAttributeValueException(errorMsg);
        }

        this.name = name;
    }

    /**
     * Get Name of Metadata Record.
     * 
     * @return name of metadata record.
     */
    public String getName() {

        return this.name;
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

        // templateValues.put(XmlTemplateProvider.MD_RECOD_TYPE, this.type);
        // templateValues.put(XmlTemplateProvider.MD_RECORD_SCHEMA,
        // this.schema);
        // templateValues.put(XmlTemplateProvider.MD_RECORD_NAME,
        // this.name);

        return templateValues;
    }

    public String getXsltHref() {
        return xsltHref;
    }

    public void setXsltHref(String xsltHref) throws MalformedURLException,
        IOException {
        URL url;
        if (xsltHref.startsWith("/")) {
            try {
                url =
                    new URL(EscidocConfiguration.getInstance().get(
                        EscidocConfiguration.ESCIDOC_CORE_BASEURL)
                        + xsltHref);
            }
            catch (MalformedURLException e) {
                throw e;
            }
            // FIXME how to handle IOException from configuration
        }
        else {
            url = new URL(xsltHref);
        }
        this.xsltHref = url.toString();
    }

    public String getMdRecordName() {
        return mdRecordName;
    }

    public void setMdRecordName(String mdRecordName) {
        this.mdRecordName = mdRecordName;
    }

    public String getFedoraId(String parentId) {
        if (name == null) {
            throw new NullPointerException(
                "Name must not be null to provide FedoraId.");
        }
        return "info:fedora/sdef:" + parentId.replaceAll(":", "_") + "-"
            + this.name;
    }

}
