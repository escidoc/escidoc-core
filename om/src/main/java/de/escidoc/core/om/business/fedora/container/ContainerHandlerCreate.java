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
package de.escidoc.core.om.business.fedora.container;

import de.escidoc.core.common.business.fedora.resources.StatusType;
import de.escidoc.core.common.exceptions.system.EncodingSystemException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.util.xml.Elements;
import de.escidoc.core.common.util.xml.XmlUtility;
import de.escidoc.core.common.util.xml.factory.XmlTemplateProvider;
import de.escidoc.core.om.business.stax.handler.MetadataHandler;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * The methods of the class deals with datastreams for fedora objects, which
 * will represent the Container Resource.
 * 
 * @author ROF
 * @om
 */
public class ContainerHandlerCreate extends ContainerResourceListener {

    /**
     * Get FoXML of Container.
     * 
     * @param containerDataStreams
     *            Map with datastreams of the Container.
     * @param metadataHandler
     * @param containerId
     *            objid of the Container.
     * @param contentModel
     * @param properties
     * @param members
     * @param lastModificationDate
     * @param contentRelations
     * @param comment
     * @param propertiesAsReferences
     * @return FoXML of Container.
     * @throws SystemException
     */
    protected String getContainerFoxml(
        final Map<String, Object> containerDataStreams,
        final MetadataHandler metadataHandler, final String containerId,
        final String contentModel, final Map<String, String> properties,
        final List<String> members, final String lastModificationDate,
        final List<Map<String, String>> contentRelations,
        final String comment,
        final Map<String, String> propertiesAsReferences)
        throws SystemException {

        Map<String, Map<String, String>> metadataAttributes =
            metadataHandler.getMetadataAttributes();
        Map<String, Object> values = new HashMap<String, Object>();

        values.put(XmlTemplateProvider.OBJID, containerId);
        values.put("title", "Container " + containerId);

        values.put(XmlTemplateProvider.PUBLIC_STATUS, properties
            .get(Elements.ELEMENT_PUBLIC_STATUS));
        values.put(XmlTemplateProvider.VERSION_STATUS, properties
            .get(Elements.ELEMENT_PUBLIC_STATUS));

        if (properties.get(Elements.ELEMENT_PUBLIC_STATUS).equals(
            StatusType.RELEASED.toString())) {
            // if status release add release number and date (date is later
            // to update)
            values.put(XmlTemplateProvider.LATEST_RELEASE_DATE,
                XmlTemplateProvider.PLACEHOLDER);
            values.put(XmlTemplateProvider.LATEST_RELEASE_NUMBER, "1");
        }

        // dc-mapping prototyping
        String dcXml;
        try {
            dcXml =
                XmlUtility
                    .createDC(
                        metadataHandler.getEscidocMdRecordNameSpace(),
                        ((ByteArrayOutputStream) ((Map) containerDataStreams
                            .get("md-records"))
                            .get(XmlTemplateProvider.DEFAULT_METADATA_FOR_DC_MAPPING))
                            .toString(XmlUtility.CHARACTER_ENCODING),
                        containerId, contentModel);
        }
        catch (UnsupportedEncodingException e) {
            throw new EncodingSystemException(e.getMessage(), e);
        }

        if (dcXml != null && dcXml.trim().length() > 0) {
            values.put(XmlTemplateProvider.DC, dcXml);
        }

        for (Entry<String, Object> stringObjectEntry : containerDataStreams.entrySet()) {

            Entry entry = stringObjectEntry;
            String outsideKey = (String) entry.getKey();
            if (entry.getValue() instanceof ByteArrayOutputStream) {
                ByteArrayOutputStream outsideValue = (ByteArrayOutputStream) entry.getValue();
                try {
                    // now we map to Velocity Variable Names
                    if (outsideKey
                            .equals(Elements.ELEMENT_CONTENT_MODEL_SPECIFIC)) {
                        values.put(XmlTemplateProvider.CONTENT_MODEL_SPECIFIC,
                                outsideValue
                                        .toString(XmlUtility.CHARACTER_ENCODING));
                    } else {
                        values.put(outsideKey, outsideValue
                                .toString(XmlUtility.CHARACTER_ENCODING));
                    }
                } catch (UnsupportedEncodingException e) {
                    throw new EncodingSystemException(e);
                }

            } else if ("md-records".equals(outsideKey)) {

                Map insideHash = (HashMap) entry.getValue();
                if (!insideHash.isEmpty()) {
                    Collection<Map<String, String>> mdRecords =
                            new ArrayList<Map<String, String>>(insideHash.size());
                    values.put(XmlTemplateProvider.MD_RECORDS, mdRecords);
                    Set content2 = insideHash.entrySet();
                    for (Object aContent2 : content2) {
                        Map<String, String> mdRecord =
                                new HashMap<String, String>();
                        Entry entry2 = (Entry) aContent2;
                        String insideKey = (String) entry2.getKey();
                        Map<String, String> mdAttributes =
                                metadataAttributes.get(insideKey);
                        String schema = null;
                        String type = null;
                        if (mdAttributes != null) {
                            schema = mdAttributes.get("schema");
                            type = mdAttributes.get("type");
                        }
                        ByteArrayOutputStream insideValue = (ByteArrayOutputStream) entry2.getValue();
                        mdRecord.put(XmlTemplateProvider.MD_RECORD_SCHEMA,
                                schema);
                        mdRecord.put(XmlTemplateProvider.MD_RECORD_TYPE, type);
                        mdRecord.put(XmlTemplateProvider.MD_RECORD_NAME,
                                insideKey);
                        try {
                            mdRecord.put(XmlTemplateProvider.MD_RECORD_CONTENT,
                                    insideValue
                                            .toString(XmlUtility.CHARACTER_ENCODING));

                        } catch (UnsupportedEncodingException e) {
                            throw new EncodingSystemException(e);
                        }
                        mdRecords.add(mdRecord);
                    }
                }
            }
        }

        return getFoxmlRenderer().render(values, properties, members,
            containerId, lastModificationDate, contentRelations, comment,
            propertiesAsReferences);
    }
}
