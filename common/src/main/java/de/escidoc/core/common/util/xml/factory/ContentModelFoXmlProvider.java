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

import de.escidoc.core.common.exceptions.system.WebserverSystemException;

import java.util.Map;

public class ContentModelFoXmlProvider extends InfrastructureFoXmlProvider {

    private static final String CONTENT_MODEL_PATH = "/content-model";

    private static final String CONTENT_MODEL_RESOURCE_NAME = "content-model";

    private static final String DS_COMPOSITE_RESOURCE_NAME = "dsCompositeModel";

    private static final String SERVICE_DEFINITION_RESOURCE_NAME =
        "service-definition-xslt-md";

    private static final String SERVICE_DEPLOYMENT_RESOURCE_NAME =
        "service-deployment-xslt-md";

    private static final String CONTENT_MODEL_RELS_EXT_PATH =
        CONTENT_MODEL_PATH;

    private static final String RELS_EXT_RESOURCE_NAME = "rels-ext";


    private static ContentModelFoXmlProvider PROVIDER = new ContentModelFoXmlProvider();

    /**
     * Private constructor to prevent initialization.
     * @om
     */
    private ContentModelFoXmlProvider() {
    }

    /**
     * Gets the role xml PROVIDER.
     * 
     * @return Returns the <code>UserAccountXmlProvider</code> object.
     * @om
     */
    public static ContentModelFoXmlProvider getInstance() {
        return PROVIDER;
    }

    /**
     * 
     * @param values
     * @return
     * @throws WebserverSystemException
     */
    public String getContentModelFoXml(final Map values)
        throws WebserverSystemException {

        return getXml(CONTENT_MODEL_RESOURCE_NAME, CONTENT_MODEL_PATH, values);
    }

    /**
     * 
     * @param values
     * @return
     * @throws WebserverSystemException
     */
    public String getContentModelDsComposite(final Map values)
        throws WebserverSystemException {

        return getXml(DS_COMPOSITE_RESOURCE_NAME, CONTENT_MODEL_PATH, values);
    }

    /**
     * 
     * @param values
     * @return
     * @throws WebserverSystemException
     */
    public String getServiceDefinitionFoXml(final Map values)
        throws WebserverSystemException {

        return getXml(SERVICE_DEFINITION_RESOURCE_NAME, CONTENT_MODEL_PATH,
            values);
    }

    /**
     * 
     * @param values
     * @return
     * @throws WebserverSystemException
     */
    public String getServiceDeploymentFoXml(final Map values)
        throws WebserverSystemException {

        return getXml(SERVICE_DEPLOYMENT_RESOURCE_NAME, CONTENT_MODEL_PATH,
            values);
    }

    /**
     * 
     * @param values
     * @return
     * @throws WebserverSystemException
     */
    public String getContentModelRelsExt(final Map values)
        throws WebserverSystemException {

        return getXml(RELS_EXT_RESOURCE_NAME, CONTENT_MODEL_RELS_EXT_PATH,
            values);
    }
}
