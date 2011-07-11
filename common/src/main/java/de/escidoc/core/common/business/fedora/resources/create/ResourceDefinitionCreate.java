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
import de.escidoc.core.common.util.xml.factory.ItemFoXmlProvider;
import net.sf.oval.constraint.AssertFieldConstraints;
import net.sf.oval.guard.Guarded;
import org.hibernate.validator.NotEmpty;

import javax.validation.constraints.NotNull;
import java.io.IOException;
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
@Guarded(applyFieldConstraintsToConstructors = true, applyFieldConstraintsToSetters = true, assertParametersNotNull = false, checkInvariants = true, inspectInterfaces = true)
public class ResourceDefinitionCreate {

    @NotNull
    @NotEmpty
    private String name;

    private String xsltHref;

    private String mdRecordName;

    /**
     * Set Name of Metadata Record.
     *
     * @param name Name of MdRecord.
     * @throws MissingAttributeValueException Thrown if name is an empty String.
     */
    public void setName(@AssertFieldConstraints
    final String name) throws MissingAttributeValueException {
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
    public Map<String, String> getValueMap() {
        return new HashMap<String, String>();
    }

    public String getXsltHref() {
        return this.xsltHref;
    }

    public void setXsltHref(final String xsltHref) throws IOException {
        final URL url =
            xsltHref.startsWith("/") ? new URL(EscidocConfiguration.getInstance().get(
                EscidocConfiguration.ESCIDOC_CORE_BASEURL)
                + xsltHref) : new URL(xsltHref);
        this.xsltHref = url.toString();
    }

    public String getMdRecordName() {
        return this.mdRecordName;
    }

    public void setMdRecordName(final String mdRecordName) {
        this.mdRecordName = mdRecordName;
    }

    public String getFedoraId(final String parentId) {
        return "info:fedora/sdef:" + parentId.replaceAll(":", "_") + '-' + this.name;
    }

}
