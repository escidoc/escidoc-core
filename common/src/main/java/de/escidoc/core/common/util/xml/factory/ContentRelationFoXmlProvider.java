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

package de.escidoc.core.common.util.xml.factory;

import de.escidoc.core.common.business.Constants;
import de.escidoc.core.common.business.fedora.resources.create.ContentRelationCreate;
import de.escidoc.core.common.business.fedora.resources.create.MdRecordCreate;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;

import java.util.HashMap;
import java.util.Map;

/**
 * FoXML representations of the Content Relation resource.<br/> See http://colab.mpdl.mpg.de/mediawiki/ESciDoc_Content_Relations_Concept
 *
 * @author Steffen Wagner
 */
public final class ContentRelationFoXmlProvider extends InfrastructureFoXmlProvider {

    private static final ContentRelationFoXmlProvider PROVIDER = new ContentRelationFoXmlProvider();

    // velocity template path
    private static final String VL_CONTENT_RELATION_PATH = "content-relation";

    // velocity template path
    private static final String VL_CONTENT_RELATION_RESOURCE_NAME = "content-relation";

    // velocity template path
    private static final String VL_RELS_EXT_RESOURCE_NAME = "rels-ext";

    /**
     * Private constructor to prevent initialization.
     */
    private ContentRelationFoXmlProvider() {
    }

    /**
     * Gets the ContentRelationFoXmlProvider PROVIDER.
     *
     * @return Returns the {@code ContentRelationFoXmlProvider} object.
     */
    public static ContentRelationFoXmlProvider getInstance() {
        return PROVIDER;
    }

    /**
     * Get FoXML of ContentRelation resource.
     *
     * @param cr The ContentRelation
     * @return FoXML representation of the ContentRelation
     * @throws SystemException Thrown if character encoding failed of mdrecord failed.
     */
    public String getFoXml(final ContentRelationCreate cr) throws SystemException {

        final Map<String, Object> values = new HashMap<String, Object>();

        values.put(XmlTemplateProviderConstants.FRAMEWORK_BUILD_NUMBER, cr.getBuildNumber());

        values.put(XmlTemplateProviderConstants.OBJID, cr.getObjid());
        values.put(XmlTemplateProviderConstants.TITLE, cr.getProperties().getTitle());

        // RELS-EXT
        values.putAll(getRelsExtValueMap(cr));
        values.putAll(getRelsExtNamespaceValues());

        // add Metadata as Map
        values.put(XmlTemplateProviderConstants.MD_RECORDS, getMetadataRecordsMap(cr.getMetadataRecords()));

        // DC (inclusive mapping)-------------------------
        final MdRecordCreate defaultMd =
            cr.getMetadataRecord(XmlTemplateProviderConstants.DEFAULT_METADATA_FOR_DC_MAPPING);
        if (defaultMd != null) {
            final String dcXml = cr.getDC(defaultMd, null);
            values.put(XmlTemplateProviderConstants.DC, dcXml);
        }
        values.put(XmlTemplateProviderConstants.IN_CREATE, XmlTemplateProviderConstants.TRUE);

        return getXml(VL_CONTENT_RELATION_RESOURCE_NAME, VL_CONTENT_RELATION_PATH, values);
    }

    /**
     * Get RELS-EXT for Content Relation.
     *
     * @param cr The ContentRelation
     * @return XML representation of RELS-EXT
     * @throws WebserverSystemException Thrown if rendering failed.
     */
    public String getRelsExt(final ContentRelationCreate cr) throws WebserverSystemException {

        final Map<String, String> values = getRelsExtValueMap(cr);
        values.putAll(getRelsExtNamespaceValues());
        values.put(XmlTemplateProviderConstants.FRAMEWORK_BUILD_NUMBER, cr.getBuildNumber());

        values.put(XmlTemplateProviderConstants.OBJID, cr.getObjid());
        values.put(XmlTemplateProviderConstants.TITLE, cr.getProperties().getTitle());
        return getXml(VL_RELS_EXT_RESOURCE_NAME, VL_CONTENT_RELATION_PATH, values);
    }

    /**
     * Prepare all for rels-ext relevant values from ContentRelation resource within a Map (where keys are valid for
     * used velocity templates).
     *
     * @param cr The ContentRelation object.
     * @return Map with rels-ext relevant values and velocity valid key names.
     */
    private static Map<String, String> getRelsExtValueMap(final ContentRelationCreate cr) {

        final Map<String, String> values = new HashMap<String, String>();

        values.put(XmlTemplateProviderConstants.CREATED_BY_ID, cr.getProperties().getCreatedById());
        values.put(XmlTemplateProviderConstants.CREATED_BY_TITLE, cr.getProperties().getCreatedByName());

        values.put(XmlTemplateProviderConstants.MODIFIED_BY_ID, cr.getProperties().getModifiedById());
        values.put(XmlTemplateProviderConstants.MODIFIED_BY_TITLE, cr.getProperties().getModifiedByName());

        values.put(XmlTemplateProviderConstants.PUBLIC_STATUS, cr.getProperties().getStatus().toString());
        values.put(XmlTemplateProviderConstants.PUBLIC_STATUS_COMMENT, cr.getProperties().getStatusComment());

        // relation (type, description, subject(s), object(s))
        values.put(XmlTemplateProviderConstants.CONTENT_RELATION_TYPE, cr.getType().toString());
        values.put(XmlTemplateProviderConstants.CONTENT_RELATION_DESCRIPTION, cr.getProperties().getDescription());
        values.put(XmlTemplateProviderConstants.CONTENT_RELATION_SUBJECT_ID, cr.getSubject());
        values.put(XmlTemplateProviderConstants.CONTENT_RELATION_OBJECT_ID, cr.getObject());
        values.put(XmlTemplateProviderConstants.CONTENT_RELATION_SUBJECT_VERSION_NUMBER, cr.getSubjectVersion());
        values.put(XmlTemplateProviderConstants.CONTENT_RELATION_OBJECT_VERSION_NUMBER, cr.getObjectVersion());

        return values;
    }

    /**
     * Getting Namespaces for RelsExt as Map.
     *
     * @return HashMap with namespace values for XML representation.
     */
    private static Map<String, String> getRelsExtNamespaceValues() {

        final Map<String, String> values = new HashMap<String, String>();

        values.put(XmlTemplateProviderConstants.ESCIDOC_PROPERTIES_NS_PREFIX, Constants.PROPERTIES_NS_PREFIX);
        values.put(XmlTemplateProviderConstants.ESCIDOC_PROPERTIES_NS, Constants.PROPERTIES_NS_URI);

        values.put(XmlTemplateProviderConstants.ESCIDOC_RELATION_NS_PREFIX, Constants.STRUCTURAL_RELATIONS_NS_PREFIX);
        values.put(XmlTemplateProviderConstants.ESCIDOC_RELATION_NS, Constants.STRUCTURAL_RELATIONS_NS_URI);

        values.put(XmlTemplateProviderConstants.CONTENT_RELATION_NAMESPACE_PREFIX,
            Constants.CONTENT_RELATIONS_NEW_NS_PREFIX_IN_RELSEXT);
        values.put(XmlTemplateProviderConstants.CONTENT_RELATION_NAMESPACE,
            Constants.CONTENT_RELATION_NAMESPACE_URI + '/');

        values.put(XmlTemplateProviderConstants.ESCIDOC_RESOURCE_NS_PREFIX, Constants.STRUCTURAL_RELATIONS_NS_PREFIX);
        values.put(XmlTemplateProviderConstants.ESCIDOC_RESOURCE_NS, Constants.RESOURCES_NS_URI);

        return values;
    }
}
