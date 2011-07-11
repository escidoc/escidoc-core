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
 * Copyright 2008 Fachinformationszentrum Karlsruhe Gesellschaft
 * fuer wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Foerderung der Wissenschaft e.V.  
 * All rights reserved.  Use is subject to license terms.
 */
package de.escidoc.core.aa.business.renderer;

import de.escidoc.core.common.business.Constants;
import de.escidoc.core.common.util.xml.XmlUtility;
import de.escidoc.core.common.util.xml.factory.XmlTemplateProviderConstants;

import java.util.Map;

/**
 * Abstract renderer class.
 *
 * @author Torsten Tetteroo
 */
public class AbstractRenderer {

    /**
     * Adds the xlink name space values to the provided map.
     *
     * @param values The map to add values to.
     */
    protected void addXlinkNamespaceValues(final Map<String, Object> values) {

        values.put(XmlTemplateProviderConstants.VAR_XLINK_NAMESPACE_PREFIX, Constants.XLINK_NS_PREFIX);
        values.put(XmlTemplateProviderConstants.VAR_XLINK_NAMESPACE, Constants.XLINK_NS_URI);
    }

    /**
     * Adds the structural relations name space values to the provided map.
     *
     * @param values The map to add values to.
     */
    protected void addStructuralRelationNamespaceValues(final Map<String, Object> values) {

        values.put(XmlTemplateProviderConstants.ESCIDOC_SREL_NS_PREFIX, Constants.STRUCTURAL_RELATIONS_NS_PREFIX);
        values.put(XmlTemplateProviderConstants.ESCIDOC_SREL_NS, Constants.STRUCTURAL_RELATIONS_NS_URI);
    }

    /**
     * Adds the properties name space values to the provided map.
     *
     * @param values The map to add values to.
     */
    protected void addPropertiesNamespaceValues(final Map<String, Object> values) {

        values.put(XmlTemplateProviderConstants.ESCIDOC_PROPERTIES_NS_PREFIX, Constants.PROPERTIES_NS_PREFIX);
        values.put(XmlTemplateProviderConstants.ESCIDOC_PROPERTIES_NS, Constants.PROPERTIES_NS_URI);
    }

    /**
     * Adds the rdf values to the provided map.
     *
     * @param values The map to add values to.
     */
    protected void addRdfValues(final Map<String, Object> values) {

        values.put("rdfNamespacePrefix", Constants.RDF_NAMESPACE_PREFIX);
        values.put("rdfNamespace", Constants.RDF_NAMESPACE_URI);
    }

    /**
     * Adds the value of the escidoc base url to the provided map.
     *
     * @param values The map to add the value to.
     */
    protected void addEscidocBaseUrlValue(final Map<String, Object> values) {

        values.put(XmlTemplateProviderConstants.VAR_ESCIDOC_BASE_URL, XmlUtility.getEscidocBaseUrl());
    }

}
