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

import java.util.Map;

import de.escidoc.core.common.business.Constants;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.util.xml.factory.OrganizationalUnitFoXmlProvider;
import de.escidoc.core.common.util.xml.factory.XmlTemplateProvider;
import de.escidoc.core.oum.business.renderer.interfaces.OrganizationalUnitFoXmlRendererInterface;

/**
 * Organizational unit foxml renderer implementation using the velocity template
 * engine.
 * 
 * @author MSC
 * 
 */
public class VelocityXmlOrganizationalUnitFoXmlRenderer
    implements OrganizationalUnitFoXmlRendererInterface {

    // CHECKSTYLE:JAVADOC-OFF
    /**
     * See Interface for functional description.
     * 
     * @param datastreams
     * @return
     * @throws SystemException
     * @see de.escidoc.core.oum.business.renderer.interfaces.OrganizationalUnitFoXmlRendererInterface#render(Map)
     */
    public String render(final Map<String, Object> values)
        throws SystemException {

        String result = null;

        values.put(XmlTemplateProvider.ESCIDOC_PROPERTIES_NS_PREFIX,
            Constants.PROPERTIES_NS_PREFIX);
        values.put(XmlTemplateProvider.ESCIDOC_PROPERTIES_NS,
            Constants.PROPERTIES_NS_URI);
        values.put(XmlTemplateProvider.ESCIDOC_SREL_NS_PREFIX,
            Constants.STRUCTURAL_RELATIONS_NS_PREFIX);
        values.put(XmlTemplateProvider.VAR_STRUCT_RELATIONS_NAMESPACE,
            Constants.STRUCTURAL_RELATIONS_NS_URI);
        values.put(XmlTemplateProvider.VAR_RESOURCES_ONTOLOGIES_NAMESPACE,
            Constants.RESOURCES_NS_URI);

        result =
            OrganizationalUnitFoXmlProvider
                .getInstance().getOrganizationalUnitFoXml(values);
        return result;
    }

    /**
     * See Interface for functional description.
     * 
     * @param values
     * @return
     * @throws WebserverSystemException
     * @see de.escidoc.core.oum.business.renderer.interfaces.OrganizationalUnitFoXmlRendererInterface#renderRelsExt(Map)
     */
    public String renderRelsExt(final Map<String, Object> values)
        throws WebserverSystemException {

        String result = null;

        values.put(XmlTemplateProvider.ESCIDOC_PROPERTIES_NS_PREFIX,
            Constants.PROPERTIES_NS_PREFIX);
        values.put(XmlTemplateProvider.ESCIDOC_PROPERTIES_NS,
            Constants.PROPERTIES_NS_URI);
        values.put(XmlTemplateProvider.ESCIDOC_SREL_NS_PREFIX,
            Constants.STRUCTURAL_RELATIONS_NS_PREFIX);
        values.put(XmlTemplateProvider.VAR_STRUCT_RELATIONS_NAMESPACE,
            Constants.STRUCTURAL_RELATIONS_NS_URI);
        values.put(XmlTemplateProvider.VAR_RESOURCES_ONTOLOGIES_NAMESPACE,
            Constants.RESOURCES_NS_URI);
        result =
            OrganizationalUnitFoXmlProvider.getInstance().getRelsExt(values);
        return result;
    }

    // CHECKSTYLE:JAVADOC-ON
}
