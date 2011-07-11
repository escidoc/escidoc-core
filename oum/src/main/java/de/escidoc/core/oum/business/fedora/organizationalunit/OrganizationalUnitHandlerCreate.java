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
package de.escidoc.core.oum.business.fedora.organizationalunit;

import de.escidoc.core.common.business.fedora.Utility;
import de.escidoc.core.common.exceptions.system.EncodingSystemException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.util.xml.Elements;
import de.escidoc.core.common.util.xml.XmlUtility;
import de.escidoc.core.common.util.xml.factory.XmlTemplateProviderConstants;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * The methods of the class deals with datastreams for fedora objects, which will represent the
 * OrganizationalUnitCreator.
 *
 * @author Michael Schneider
 */
public class OrganizationalUnitHandlerCreate extends OrganizationalUnitHandlerRetrieve {

    /**
     * Build the foxml representation of an organizational unit.
     *
     * @param id                 The id of the organizational unit.
     * @param relsExtValues      The rels-ext values.
     * @param parents            The parent organizational units.
     * @param metadataProperties The metadata properties.
     * @param metadataStreams    The metadata streams.
     * @param dcStream           The dc stream.
     * @return the foxml representation of the organizational unit.
     * @throws SystemException If anything fails.
     */
    protected String getOrganizationalUnitFoxml(
        final String id, final Map<String, Object> relsExtValues, final List<String> parents,
        final Map<String, Map<String, String>> metadataProperties,
        final Map<String, ByteArrayOutputStream> metadataStreams, final String dcStream) throws SystemException {

        final Map<String, Object> values = new HashMap<String, Object>();
        values.putAll(relsExtValues);
        values.put(XmlTemplateProviderConstants.FRAMEWORK_BUILD_NUMBER, Utility.getBuildNumber());
        values.put(XmlTemplateProviderConstants.VAR_ID, id);
        // values.put("organizationDetails", organizationDetails);
        values.put(XmlTemplateProviderConstants.VAR_PARENTS, parents);

        if (metadataStreams != null) {
            final Collection<Map<String, String>> mdRecords =
                new ArrayList<Map<String, String>>(metadataStreams.size());
            for (final Entry<String, ByteArrayOutputStream> stringByteArrayOutputStreamEntry : metadataStreams
                .entrySet()) {
                final Map<String, String> mdRecord = new HashMap<String, String>();
                if (metadataProperties != null) {
                    final Map<String, String> properties =
                        metadataProperties.get(stringByteArrayOutputStreamEntry.getKey());
                    mdRecord.put(XmlTemplateProviderConstants.MD_RECORD_TYPE, properties
                        .get(Elements.MD_RECORD_ATTRIBUTE_TYPE));
                    mdRecord.put(XmlTemplateProviderConstants.MD_RECORD_SCHEMA, properties
                        .get(Elements.MD_RECORD_ATTRIBUTE_SCHEMA));
                }
                mdRecord.put(XmlTemplateProviderConstants.MD_RECORD_NAME, stringByteArrayOutputStreamEntry.getKey());
                try {
                    final String metadata =
                        stringByteArrayOutputStreamEntry.getValue().toString(XmlUtility.CHARACTER_ENCODING);
                    mdRecord.put(XmlTemplateProviderConstants.MD_RECORD_CONTENT, metadata);
                }
                catch (final UnsupportedEncodingException e) {
                    throw new EncodingSystemException("Metadata record '" + stringByteArrayOutputStreamEntry.getKey()
                        + "' has wrong encoding!", e);
                }
                mdRecords.add(mdRecord);
            }
            values.put(XmlTemplateProviderConstants.MD_RECORDS, mdRecords);
        }
        if (dcStream != null && dcStream.trim().length() != 0) {
            values.put(XmlTemplateProviderConstants.DC, dcStream);
        }
        return getFoxmlRenderer().render(values);
    }

    /**
     * Build the foxml representation of the rels-ext datastream of an organizational unit.
     *
     * @param id            The id of the organizational unit.
     * @param relsExtValues The rels-ext values.
     * @param parents       The parent organizational units.
     * @return The foxml representation of the rels-ext datastream of an organizational unit.
     * @throws de.escidoc.core.common.exceptions.system.WebserverSystemException
     */
    protected String getOrganizationalUnitRelsExt(
        final String id, final Map<String, Object> relsExtValues, final List<String> parents)
        throws WebserverSystemException {

        final Map<String, Object> values = new HashMap<String, Object>();
        values.putAll(relsExtValues);
        values.put(XmlTemplateProviderConstants.VAR_ID, id);
        values.put(XmlTemplateProviderConstants.VAR_PARENTS, parents);
        return getFoxmlRenderer().renderRelsExt(values);
    }

}
