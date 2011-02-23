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
 * Copyright 2006-2008 Fachinformationszentrum Karlsruhe Gesellschaft
 * fuer wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Foerderung der Wissenschaft e.V.  
 * All rights reserved.  Use is subject to license terms.
 */
package de.escidoc.core.adm.business.renderer;

import de.escidoc.core.adm.business.renderer.interfaces.AdminRendererInterface;
import de.escidoc.core.common.business.Constants;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.util.logger.AppLogger;
import de.escidoc.core.common.util.xml.XmlUtility;
import de.escidoc.core.common.util.xml.factory.AdminXmlProvider;
import de.escidoc.core.common.util.xml.factory.XmlTemplateProvider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Admin renderer implementation using the velocity template engine.
 * 
 * @author MIH
 * @spring.bean id="eSciDoc.core.adm.business.renderer.VelocityXmlAdminRenderer"
 */
public class VelocityXmlAdminRenderer 
    implements AdminRendererInterface {

    /** The logger. */
    private static final AppLogger LOG =
        new AppLogger(VelocityXmlAdminRenderer.class.getName());

    // CHECKSTYLE:JAVADOC-OFF

    /**
     * See Interface for functional description.
     *
     * @return
     * @throws WebserverSystemException
     * @see de.escidoc.core.adm.business.renderer.interfaces.AdminRendererInterface
     *      #renderIndexConfiguration(HashMap<String, HashMap<String, 
            HashMap<String, Object>>>)
     */
    @Override
    public String renderIndexConfiguration(final Map<String, Map<String,
            Map<String, Object>>> indexConfiguration)
        throws WebserverSystemException {

        long start = System.nanoTime();
        Map<String, Object> values = new HashMap<String, Object>();

        addCommonValues(values);
        addIndexConfigurationValues(indexConfiguration, values);

        final String ret = getAdminXmlProvider().getIndexConfigurationXml(values);
        if (LOG.isDebugEnabled()) {
            long runtime = System.nanoTime() - start;
            LOG.debug("Built XML in " + Long.valueOf(runtime) + "ns");
        }
        return ret;
    }

    /**
     * Adds the common values to the provided map.
     * 
     * @param values
     *            The map to add values to.
     * 
     * @throws WebserverSystemException
     *             Thrown in case of an internal error.
     */
    private void addCommonValues(final Map<String, Object> values)
        throws WebserverSystemException {

        values.put("indexConfigurationNamespacePrefix", 
                Constants.INDEX_CONFIGURATION_NS_PREFIX);
        values.put("indexConfigurationNamespace", 
                Constants.INDEX_CONFIGURATION_NS_URI);
        values.put(XmlTemplateProvider.VAR_ESCIDOC_BASE_URL, XmlUtility
                .getEscidocBaseUrl());
    }

    /**
     * Adds the values of the indexConfiguration 
     * that shall be rendered to the provided
     * {@link Map}.
     * 
     * @param indexConfiguration
     * @param values
     * @throws WebserverSystemException
     */
    @SuppressWarnings("unchecked")
    private void addIndexConfigurationValues(
        final Map<String, Map<String, 
        Map<String, Object>>> indexConfiguration, 
                        final Map<String, Object> values)
        throws WebserverSystemException {
        Collection<HashMap<String, Object>> resourcesVm =
            new ArrayList<HashMap<String, Object>>();
        if (indexConfiguration != null && !indexConfiguration.isEmpty()) {
            Set<Entry<String, Map<String, Map<String, Object>>>> indexConfigurationEntrySet =
                    indexConfiguration.entrySet();
            for (Entry<String, Map<String, Map<String, Object>>> entry : indexConfigurationEntrySet) {
                HashMap<String, Object> resourceVm = 
                                    new HashMap<String, Object>();
                resourceVm.put("resourceName", entry.getKey());
                Map<String, Map<String, Object>> indexMap = entry.getValue();
                if (indexMap != null && !indexMap.isEmpty()) {
                    Collection<HashMap<String, Object>> indexesVm =
                                new ArrayList<HashMap<String, Object>>();
                    Set<Entry<String, Map<String, Object>>> indexMapEntrySet = indexMap.entrySet();
                    for (Entry<String, Map<String, Object>> indexMapEntry : indexMapEntrySet) {
                        HashMap<String, Object> indexVm = new HashMap<String, Object>();
                        indexVm.put("indexName", indexMapEntry.getKey());
                        Map<String, Object> indexParamsMap = indexMapEntry.getValue();
                        if (indexParamsMap != null && !indexParamsMap.isEmpty()) {
                            for (Entry<String, Object> mapEntry : indexParamsMap.entrySet()) {
                                String indexParamName = mapEntry.getKey();
                                if ("prerequisites".equals(indexParamName)) {
                                    HashMap<String, String> prerequisitesMap = 
                                        (HashMap<String, String>) mapEntry.getValue();
                                    if (prerequisitesMap != null  && !prerequisitesMap.isEmpty()) {
                                        Map<String, String> prerequisitesVm = new HashMap<String, String>();
                                        Set<Entry<String, String>>prerequisitesMapEntrySet =
                                                prerequisitesMap.entrySet();
                                        for (Entry<String, String> prerequisitesMapEntry :
                                                prerequisitesMapEntrySet) {
                                            prerequisitesVm.put(
                                                    prerequisitesMapEntry.getKey(), prerequisitesMapEntry.getValue());
                                        }
                                        indexVm.put("prerequisites", prerequisitesVm);
                                    }
                                } else {
                                    indexVm.put(indexParamName, indexParamsMap.get(indexParamName));
                                }
                            }
                        }
                        indexesVm.add(indexVm);
                    }
                    resourceVm.put("indexes", indexesVm);
                }
                resourcesVm.add(resourceVm);
            }
        }
        values.put("resources", resourcesVm);
    }

    /**
     * Gets the {@link AdminXmlProvider} object.
     * 
     * @return Returns the {@link AdminXmlProvider} object.
     * @throws WebserverSystemException
     *             Thrown in case of an internal error.
     */
    private AdminXmlProvider getAdminXmlProvider()
        throws WebserverSystemException {

        return AdminXmlProvider.getInstance();
    }

}
