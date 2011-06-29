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

/**
 * Relations XML Provider.
 */
public final class RelationsXmlProvider extends InfrastructureXmlProvider {

    private static final String RELATIONS_RESOURCE_NAME = "relations";

    private static final String RELATION_RESOURCE_NAME = "relation";

    private static final String RELATIONS_PATH = "/common";

    private static final String RELATION_PATH = "/common";

    private static final String RELATIONS_ROOT_ATTRIBUTES_RESOURCE_NAME = "relationsRootAttributes";

    private static final RelationsXmlProvider PROVIDER = new RelationsXmlProvider();

    private RelationsXmlProvider() {
    }

    /**
     * Get the Escidoc xml document to deliver.
     *
     * @return The Escidoc item document.
     */

    public static RelationsXmlProvider getInstance() {
        return PROVIDER;
    }

    /**
     * Get XML representation of Relations.
     *
     * @param values value map
     * @return XML representation of Relations
     * @throws WebserverSystemException Thrown if rending failed
     */
    public String getRelationsXml(final Map values) throws WebserverSystemException {

        return getXml(RELATIONS_RESOURCE_NAME, RELATIONS_PATH, values);
    }

    /**
     * Get XML representation of Relation.
     *
     * @param values value map
     * @return XML representation of Relations
     * @throws WebserverSystemException Thrown if rending failed
     */
    public String getRelationXml(final Map values) throws WebserverSystemException {

        return getXml(RELATION_RESOURCE_NAME, RELATION_PATH, values);
    }

    /**
     * Get attributes of root element of Relations.
     *
     * @param values value map
     * @return XML representation of Relations
     * @throws WebserverSystemException Thrown if rending failed
     */
    public String getRelationsRootAttributes(final Object values) throws WebserverSystemException {

        String result = "";
        if (values instanceof Map) {
            result = getXml(RELATIONS_ROOT_ATTRIBUTES_RESOURCE_NAME, RELATIONS_PATH, (Map) values);
        }
        return result;
    }

}
