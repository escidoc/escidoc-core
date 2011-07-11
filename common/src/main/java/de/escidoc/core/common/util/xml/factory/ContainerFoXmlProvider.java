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
 * Container FoXML PROVIDER.
 */
public final class ContainerFoXmlProvider extends InfrastructureFoXmlProvider {

    private static final String CONTAINER_PATH = "/container";

    private static final String CONTAINER_RESOURCE_NAME = "container";

    private static final String RELS_EXT_PATH = CONTAINER_PATH;

    private static final String RELS_EXT_RESOURCE_NAME = "rels-ext";

    private static final ContainerFoXmlProvider PROVIDER = new ContainerFoXmlProvider();

    /**
     * Private constructor to prevent initialization.
     */
    private ContainerFoXmlProvider() {
    }

    /**
     * Gets the role xml PROVIDER.
     *
     * @return Returns the {@code UserAccountXmlProvider} object.
     */
    public static ContainerFoXmlProvider getInstance() {
        return PROVIDER;
    }

    /**
     * Render Container to XML.
     *
     * @param values Value Map.
     * @return XML representation of Container
     * @throws WebserverSystemException Thrown in case of internal failure.
     */
    public String getContainerFoXml(final Map values) throws WebserverSystemException {

        return getXml(CONTAINER_RESOURCE_NAME, CONTAINER_PATH, values);
    }

    /**
     * Render RELS-EXT.
     *
     * @param values Value Map.
     * @return XML representation of RELS-EXT
     * @throws WebserverSystemException Thrown in case of internal failure.
     */
    public String getContainerRelsExt(final Map values) throws WebserverSystemException {

        return getXml(RELS_EXT_RESOURCE_NAME, RELS_EXT_PATH, values);
    }

}
