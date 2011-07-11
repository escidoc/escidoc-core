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

package de.escidoc.core.common.util.xml.renderer;

import de.escidoc.core.common.business.Constants;
import de.escidoc.core.common.business.fedora.Utility;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.util.xml.Elements;
import de.escidoc.core.common.util.xml.factory.CommonFoXmlProvider;
import de.escidoc.core.common.util.xml.factory.XmlTemplateProviderConstants;

import java.util.HashMap;
import java.util.Map;

public class VelocityXmlCommonFoXmlRenderer {

    /**
     * See Interface for functional description.
     *
     * @param id
     * @param title
     * @param versionNo
     * @param lastModificationDate
     * @param versionStatus
     * @param comment
     * @param baseUrl
     * @throws WebserverSystemException cf. Interface
     * @return
     */
    public String renderWov(
        final String id, final String title, final String versionNo, final String lastModificationDate,
        final String versionStatus, final String comment, final String baseUrl) throws WebserverSystemException {

        final String currentUserId = Utility.getCurrentUser()[0];
        final String currentUserName = Utility.getCurrentUser()[1];

        final Map<String, String> values = new HashMap<String, String>();

        values.put(XmlTemplateProviderConstants.VAR_NAMESPACE_PREFIX, Constants.WOV_NAMESPACE_PREFIX);
        values.put(XmlTemplateProviderConstants.VAR_NAMESPACE, Constants.WOV_NAMESPACE_URI);
        // expand the objid to escidoc:123:1 for version no 1
        if ("1".equals(versionNo)) {
            values.put(XmlTemplateProviderConstants.OBJID, id + ":1");
        }
        else {
            values.put(XmlTemplateProviderConstants.OBJID, id);
        }
        values.put(XmlTemplateProviderConstants.TITLE, title);
        values.put(XmlTemplateProviderConstants.HREF, baseUrl + id + ':' + versionNo);
        values.put(XmlTemplateProviderConstants.VERSION_NUMBER, versionNo);
        values.put(XmlTemplateProviderConstants.TIMESTAMP, lastModificationDate);
        values.put(XmlTemplateProviderConstants.VERSION_STATUS, versionStatus);
        values.put(XmlTemplateProviderConstants.VERSION_COMMENT, comment);
        values.put(XmlTemplateProviderConstants.TIMESTAMP, lastModificationDate);
        // AGENT_TITLE AGENT_BASE_URI AGENT_ID_VALUE AGENT_ID_TYPE
        // AGENT_ID_VALUE
        values.put(XmlTemplateProviderConstants.VAR_AGENT_ID_VALUE, currentUserId);
        values.put(XmlTemplateProviderConstants.VAR_AGENT_ID_TYPE, Constants.PREMIS_ID_TYPE_ESCIDOC);
        values.put(XmlTemplateProviderConstants.VAR_AGENT_BASE_URI, Constants.USER_ACCOUNT_URL_BASE);
        values.put(XmlTemplateProviderConstants.VAR_AGENT_TITLE, currentUserName);
        // EVENT_XMLID EVENT_ID_TYPE EVENT_ID_VALUE
        values.put(XmlTemplateProviderConstants.VAR_EVENT_XMLID, "v1e" + System.currentTimeMillis());
        values.put(XmlTemplateProviderConstants.VAR_EVENT_ID_VALUE, Constants.CONTAINER_URL_BASE + id + "/resources/"
            + Elements.ELEMENT_WOV_VERSION_HISTORY + '#' + values.get(XmlTemplateProviderConstants.VAR_EVENT_XMLID));
        values.put(XmlTemplateProviderConstants.VAR_EVENT_ID_TYPE, Constants.PREMIS_ID_TYPE_URL_RELATIVE);
        values.put(XmlTemplateProviderConstants.VAR_OBJECT_ID_TYPE, Constants.PREMIS_ID_TYPE_ESCIDOC);
        values.put(XmlTemplateProviderConstants.VAR_OBJECT_ID_VALUE, id);
        return CommonFoXmlProvider.getInstance().getWov(values);
    }

}
