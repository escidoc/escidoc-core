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
package de.escidoc.core.oum.business.renderer;

import de.escidoc.core.common.business.Constants;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.util.xml.factory.OrganizationalUnitFoXmlProvider;
import de.escidoc.core.common.util.xml.factory.XmlTemplateProviderConstants;
import de.escidoc.core.oum.business.renderer.interfaces.OrganizationalUnitFoXmlRendererInterface;

import java.util.Map;

/**
 * Organizational unit foxml renderer implementation using the velocity template engine.
 *
 * @author Michael Schneider
 */
public class VelocityXmlOrganizationalUnitFoXmlRenderer implements OrganizationalUnitFoXmlRendererInterface {

    /**
     * See Interface for functional description.
     */
    @Override
    public String render(final Map<String, Object> values) throws WebserverSystemException {

        values.put(XmlTemplateProviderConstants.ESCIDOC_PROPERTIES_NS_PREFIX, Constants.PROPERTIES_NS_PREFIX);
        values.put(XmlTemplateProviderConstants.ESCIDOC_PROPERTIES_NS, Constants.PROPERTIES_NS_URI);
        values.put(XmlTemplateProviderConstants.ESCIDOC_SREL_NS_PREFIX, Constants.STRUCTURAL_RELATIONS_NS_PREFIX);
        values.put(XmlTemplateProviderConstants.VAR_STRUCT_RELATIONS_NAMESPACE, Constants.STRUCTURAL_RELATIONS_NS_URI);
        values.put(XmlTemplateProviderConstants.VAR_RESOURCES_ONTOLOGIES_NAMESPACE, Constants.RESOURCES_NS_URI);
        return OrganizationalUnitFoXmlProvider.getInstance().getOrganizationalUnitFoXml(values);
    }

    /**
     * See Interface for functional description.
     */
    @Override
    public String renderRelsExt(final Map<String, Object> values) throws WebserverSystemException {

        values.put(XmlTemplateProviderConstants.ESCIDOC_PROPERTIES_NS_PREFIX, Constants.PROPERTIES_NS_PREFIX);
        values.put(XmlTemplateProviderConstants.ESCIDOC_PROPERTIES_NS, Constants.PROPERTIES_NS_URI);
        values.put(XmlTemplateProviderConstants.ESCIDOC_SREL_NS_PREFIX, Constants.STRUCTURAL_RELATIONS_NS_PREFIX);
        values.put(XmlTemplateProviderConstants.VAR_STRUCT_RELATIONS_NAMESPACE, Constants.STRUCTURAL_RELATIONS_NS_URI);
        values.put(XmlTemplateProviderConstants.VAR_RESOURCES_ONTOLOGIES_NAMESPACE, Constants.RESOURCES_NS_URI);
        return OrganizationalUnitFoXmlProvider.getInstance().getRelsExt(values);
    }

}
