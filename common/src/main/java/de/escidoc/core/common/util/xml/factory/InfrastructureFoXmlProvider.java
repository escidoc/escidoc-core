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
 * Copyright 2007-2008 Fachinformationszentrum Karlsruhe Gesellschaft
 * fuer wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Foerderung der Wissenschaft e.V.  
 * All rights reserved.  Use is subject to license terms.
 */
package de.escidoc.core.common.util.xml.factory;

import de.escidoc.core.common.business.fedora.resources.create.MdRecordCreate;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * XmlTemplateProvider implementation using the velocity template engine.<br>
 * This implementation uses the velocity singleton pattern.
 * 
 * @author TTE
 * @common
 */
public abstract class InfrastructureFoXmlProvider extends VelocityXmlProvider {

    protected static final String COMMON_TEMPLATE_PATH = "/common";

    private static final String FOXML_PATH = "foxml";

    private static final String METADATA_RECORD_RESOURCE_NAME = "md-record";

    // private HashMap<String, String> keyMapping = null;

    /**
     * Protected constructor to prevent initialization.
     * 
     * @common
     */
    protected InfrastructureFoXmlProvider() {

        // if (this.keyMapping == null) {
        // initKeyMapping();
        // }
    }

    /**
     * Get sub-path for resource template.
     * 
     * @return sub-path for resource template
     * @throws WebserverSystemException
     *             Never.
     */
    @Override
    protected String completePath() throws WebserverSystemException {
        return FOXML_PATH;
    }

    /**
     * Render metadata to FoXML.
     * 
     * @param values
     *            value map for metadata
     * @return FoXML Representation of metadata
     * @throws WebserverSystemException
     *             Thrown if rendering failed.
     */
    public String getMetadataFoXml(final Map<String, String> values)
        throws WebserverSystemException {

        return getXml(METADATA_RECORD_RESOURCE_NAME, COMMON_TEMPLATE_PATH,
            values);
    }

    // /**
    // * Mapping of eSciDoc ontologie key names to velocity template key names.
    // *
    // * @param values
    // * Map with values, where key is eSciDoc (properties) ontologie
    // * name. Keys where no mapping is defined are copied with
    // * ontologie name to return value (map).
    // * @return Map where key names are Velocity template names.
    // */
    // @Deprecated
    // public HashMap<String, String> valueMapping(final Map<String, String>
    // values) {
    //
    // /*
    // * See initKeyMapping() for the mapping table.
    // */
    //
    // if (values == null) {
    // return null;
    // }
    //
    // HashMap<String, String> mappedValues = new HashMap<String, String>();
    //
    // Iterator<String> keys = values.keySet().iterator();
    //
    // while (keys.hasNext()) {
    // String key = keys.next();
    // String mappedKey = mapValue(key);
    // mappedValues.put(mappedKey, values.get(key));
    // }
    //
    // return mappedValues;
    // }

    // /**
    // * Define Mapping between Predicates (URI) and variable names within
    // * templates.
    // */
    // @Deprecated
    // private void initKeyMapping() {
    //
    // this.keyMapping = new HashMap<String, String>();
    // /*
    // * Common
    // */
    // this.keyMapping.put(TripleStoreUtility.PROP_CREATED_BY_ID,
    // XmlTemplateProvider.CREATED_BY_ID);
    //
    // this.keyMapping.put(TripleStoreUtility.PROP_CREATED_BY_TITLE,
    // XmlTemplateProvider.CREATED_BY_TITLE);
    //
    // /*
    // * Common Version.
    // */
    // this.keyMapping.put(TripleStoreUtility.PROP_MODIFIED_BY_ID,
    // XmlTemplateProvider.MODIFIED_BY_ID);
    //
    // this.keyMapping.put(TripleStoreUtility.PROP_MODIFIED_BY_TITLE,
    // XmlTemplateProvider.MODIFIED_BY_TITLE);
    //
    // this.keyMapping.put(TripleStoreUtility.PROP_LATEST_VERSION_COMMENT,
    // XmlTemplateProvider.VAR_LATEST_VERSION_COMMENT);
    //
    // /*
    // * Latest Version
    // */
    // this.keyMapping.put(TripleStoreUtility.PROP_LATEST_VERSION_NUMBER,
    // XmlTemplateProvider.VAR_LATEST_VERSION_NUMBER);
    //
    // this.keyMapping.put(TripleStoreUtility.PROP_LATEST_VERSION_DATE,
    // XmlTemplateProvider.VAR_LATEST_VERSION_DATE);
    //
    // this.keyMapping.put(TripleStoreUtility.PROP_LATEST_VERSION_STATUS,
    // XmlTemplateProvider.VAR_LATEST_VERSION_STATUS);
    //
    // this.keyMapping.put(TripleStoreUtility.PROP_LATEST_VERSION_COMMENT,
    // XmlTemplateProvider.VAR_LATEST_VERSION_COMMENT);
    //
    // /*
    // * Item/Container
    // */
    // this.keyMapping.put(TripleStoreUtility.PROP_CONTEXT_ID,
    // XmlTemplateProvider.VAR_CONTEXT_ID);
    //
    // this.keyMapping.put(TripleStoreUtility.PROP_CONTEXT_TITLE,
    // XmlTemplateProvider.VAR_CONTEXT_TITLE);
    //
    // this.keyMapping.put(TripleStoreUtility.PROP_CONTENT_MODEL_ID,
    // XmlTemplateProvider.VAR_CONTENT_MODEL_ID);
    //
    // this.keyMapping.put(TripleStoreUtility.PROP_CONTENT_MODEL_TITLE,
    // XmlTemplateProvider.VAR_CONTENT_MODEL_TITLE);
    //
    // /*
    // * Component
    // */
    // this.keyMapping.put(TripleStoreUtility.PROP_MIME_TYPE,
    // XmlTemplateProvider.VAR_MIME_TYPE);
    //
    // // this.keyMapping.put(TripleStoreUtility.PROP_TYPE,
    // // XmlTemplateProvider.VAR_TYPE);
    // this.keyMapping.put(TripleStoreUtility.PROP_COMPONENT_CONTENT_CATEGORY,
    // XmlTemplateProvider.VAR_COMPONENT_TYPE);
    //
    // // http://escidoc.de/core/01/properties/mime-type
    // this.keyMapping.put(TripleStoreUtility.PROP_MIME_TYPE,
    // XmlTemplateProvider.VAR_MIME_TYPE);
    //
    // this.keyMapping.put(TripleStoreUtility.PROP_VISIBILITY,
    // XmlTemplateProvider.VAR_VISIBILITY);
    //
    // this.keyMapping.put(TripleStoreUtility.PROP_VALID_STATUS,
    // XmlTemplateProvider.VAR_VALID_STATUS);
    //
    // /*
    // * Metadata
    // */
    // }
    //
    // /**
    // * Get variable name, which is used in template, for a Predicate.
    // *
    // * @param key
    // * @return
    // */
    // @Deprecated
    // private String mapValue(final String key) {
    //
    // if (this.keyMapping == null) {
    // initKeyMapping();
    // }
    //
    // if (this.keyMapping.containsKey(key)) {
    // return this.keyMapping.get(key);
    // }
    //
    // return key;
    // }

    /**
     * Generate FoXML for all MetadataRecords.
     * 
     * @param mdRecords
     *            Vector with MdRecordCreate.
     * @return MetadataReocrd FoXML.
     * @throws SystemException
     *             Thrown if converting of characters to default character set
     *             failed.
     */
    public List<Map<String, String>> getMetadataRecordsMap(
        final List<MdRecordCreate> mdRecords) throws SystemException {

        List<Map<String, String>> values =
            new ArrayList<Map<String, String>>();

        if (mdRecords != null) {
            for (MdRecordCreate mdRecord : mdRecords) {
                values.add(mdRecord.getValueMap());
            }
        }

        return values;
    }

}
