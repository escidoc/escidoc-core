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

import de.escidoc.core.common.business.fedora.resources.create.MdRecordCreate;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * XmlTemplateProviderConstants implementation using the velocity template engine.<br> This implementation uses the velocity
 * singleton pattern.
 *
 * @author Torsten Tetteroo
 */
public abstract class InfrastructureFoXmlProvider extends VelocityXmlProvider {

    protected static final String COMMON_TEMPLATE_PATH = "/common";

    private static final String FOXML_PATH = "foxml";

    private static final String METADATA_RECORD_RESOURCE_NAME = "md-record";

    /**
     * Protected constructor to prevent initialization.
     */
    protected InfrastructureFoXmlProvider() {
    }

    /**
     * Get sub-path for resource template.
     *
     * @return sub-path for resource template
     * @throws WebserverSystemException Never.
     */
    @Override
    protected String completePath() {
        return FOXML_PATH;
    }

    /**
     * Render metadata to FoXML.
     *
     * @param values value map for metadata
     * @return FoXML Representation of metadata
     * @throws WebserverSystemException Thrown if rendering failed.
     */
    public String getMetadataFoXml(final Map<String, String> values) throws WebserverSystemException {

        return getXml(METADATA_RECORD_RESOURCE_NAME, COMMON_TEMPLATE_PATH, values);
    }

    /**
     * Generate FoXML for all MetadataRecords.
     *
     * @param mdRecords Vector with MdRecordCreate.
     * @return MetadataReocrd FoXML.
     */
    public List<Map<String, String>> getMetadataRecordsMap(final Iterable<MdRecordCreate> mdRecords) {

        final List<Map<String, String>> values = new ArrayList<Map<String, String>>();

        if (mdRecords != null) {
            for (final MdRecordCreate mdRecord : mdRecords) {
                values.add(mdRecord.getValueMap());
            }
        }

        return values;
    }

}
