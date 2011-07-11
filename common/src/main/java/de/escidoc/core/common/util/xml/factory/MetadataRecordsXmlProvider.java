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

import de.escidoc.core.common.exceptions.system.WebserverSystemException;

import java.util.Map;

public final class MetadataRecordsXmlProvider extends InfrastructureXmlProvider {

    private static final String MD_RECORDS_RESOURCE_NAME = "md-records";

    private static final String MD_RECORD_RESOURCE_NAME = "md-record";

    private static final String MD_RECORDS_PATH = "/common";

    private static final String MD_RECORD_PATH = "/common";

    private static final MetadataRecordsXmlProvider PROVIDER = new MetadataRecordsXmlProvider();

    private MetadataRecordsXmlProvider() {
    }

    public static MetadataRecordsXmlProvider getInstance() {
        return PROVIDER;
    }

    public String getMdRecordsXml(final Map values) throws WebserverSystemException {

        return getXml(MD_RECORDS_RESOURCE_NAME, MD_RECORDS_PATH, values);
    }

    public String getMdRecordXml(final Map values) throws WebserverSystemException {

        return getXml(MD_RECORD_RESOURCE_NAME, MD_RECORD_PATH, values);
    }

}
