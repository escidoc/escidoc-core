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
 * Singleton which encapsulates method calls to render FOXML for the content object.
 *
 * @author André Schenk
 */
public final class ContextFoXmlProvider extends InfrastructureFoXmlProvider {

    private static final String CONTEXT_PATH = "/context";

    private static final String CONTEXT_RESOURCE_NAME = "context";

    private static final String RELS_EXT_RESOURCE_NAME = "rels-ext";

    private static final String DC_RESOURCE_NAME = "dc";

    private static final ContextFoXmlProvider PROVIDER = new ContextFoXmlProvider();

    /**
     * Private constructor to prevent initialization.
     */
    private ContextFoXmlProvider() {
    }

    /**
     * Gets the context xml provider.
     *
     * @return Returns the {@code ContextXmlProvider} object.
     */
    public static ContextFoXmlProvider getInstance() {
        return PROVIDER;
    }

    /**
     * Gets the FOXML of the context.
     *
     * @param values Values to replace the variables in the XML template.
     * @return Returns the FOXML representation of this object.
     * @throws WebserverSystemException Thrown if the instance cannot be created due to an internal error.
     */
    public String getContextFoXml(final Map<String, Object> values) throws WebserverSystemException {

        return getXml(CONTEXT_RESOURCE_NAME, CONTEXT_PATH, values);
    }

    /**
     * Gets the FOXML of the RELS-EXT data stream.
     *
     * @param values Values to replace the variables in the XML template.
     * @return Returns the FOXML representation of the RELS-EXT data stream for this object.
     * @throws WebserverSystemException Thrown if the instance cannot be created due to an internal error.
     */
    public String getRelsExt(final Map values) throws WebserverSystemException {

        return getXml(RELS_EXT_RESOURCE_NAME, CONTEXT_PATH, values);
    }

    /**
     * Gets the FOXML of the DC data stream.
     *
     * @param values Values to replace the variables in the XML template.
     * @return Returns the FOXML representation of the DC data stream for this object.
     * @throws WebserverSystemException Thrown if the instance cannot be created due to an internal error.
     */
    public String getDc(final Map values) throws WebserverSystemException {

        return getXml(DC_RESOURCE_NAME, CONTEXT_PATH, values);
    }
}
