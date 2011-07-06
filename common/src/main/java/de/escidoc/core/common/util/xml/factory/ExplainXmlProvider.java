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

import de.escidoc.core.common.business.fedora.resources.ResourceType;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.util.xml.XmlUtility;

import java.net.URL;
import java.util.Map;

/**
 * XML provider to render an SRU explain response.
 *
 * @author Andr&eacute; Schenk
 */
public final class ExplainXmlProvider extends InfrastructureXmlProvider {

    private static final String RESOURCE_NAME = "explain";

    private static final String RESOURCES_PATH = "/common";

    private static final ExplainXmlProvider PROVIDER = new ExplainXmlProvider();

    /**
     * private Constructor for Singleton.
     */
    private ExplainXmlProvider() {
    }

    /**
     * Render an SRU explain response to describe the aggregation definition database.
     *
     * @param values map containing variable names and values to fill out the Velocity macro.
     * @return explain response XML
     * @throws WebserverSystemException Thrown if Velocity could not be initialized
     */
    public String getExplainAggregationDefinitionXml(final Map<String, Object> values) throws WebserverSystemException {
        values.put("DATABASE", "statistic/aggregation-definitions");
        values.put("RESOURCE_NAME", XmlUtility.NAME_AGGREGATION_DEFINITION);
        return getExplainXml(values);
    }

    /**
     * Render an SRU explain response to describe the container member database.
     *
     * @param values map containing variable names and values to fill out the Velocity macro.
     * @return explain response XML
     * @throws WebserverSystemException Thrown if Velocity could not be initialized
     */
    public String getExplainContainerMembersXml(final Map<String, Object> values) throws WebserverSystemException {
        values.put("DATABASE", "ir/container/&lt;container id&gt;/resources/members");
        values.put("RESOURCE_NAME", ResourceType.CONTAINER.getLabel() + " member");
        return getExplainXml(values);
    }

    /**
     * Render an SRU explain response to describe the container database.
     *
     * @param values map containing variable names and values to fill out the Velocity macro.
     * @return explain response XML
     * @throws WebserverSystemException Thrown if Velocity could not be initialized
     */
    public String getExplainContainerXml(final Map<String, Object> values) throws WebserverSystemException {
        values.put("DATABASE", "ir/container");
        values.put("RESOURCE_NAME", ResourceType.CONTAINER.getLabel());
        return getExplainXml(values);
    }

    /**
     * Render an SRU explain response to describe the Content Model database.
     *
     * @param values map containing variable names and values to fill out the Velocity macro.
     * @return explain response XML
     * @throws WebserverSystemException Thrown if Velocity could not be initialized
     */
    public String getExplainContentModelXml(final Map<String, Object> values) throws WebserverSystemException {
        values.put("DATABASE", "cmm/content-model");
        values.put("RESOURCE_NAME", ResourceType.CONTENT_MODEL.getLabel());
        return getExplainXml(values);
    }

    /**
     * Render an SRU explain response to describe the content relation database.
     *
     * @param values map containing variable names and values to fill out the Velocity macro.
     * @return explain response XML
     * @throws WebserverSystemException Thrown if Velocity could not be initialized
     */
    public String getExplainContentRelationXml(final Map<String, Object> values) throws WebserverSystemException {
        values.put("DATABASE", "ir/content-relations");
        values.put("RESOURCE_NAME", ResourceType.CONTENT_RELATION.getLabel());
        return getExplainXml(values);
    }

    /**
     * Render an SRU explain response to describe the context member database.
     *
     * @param values map containing variable names and values to fill out the Velocity macro.
     * @return explain response XML
     * @throws WebserverSystemException Thrown if Velocity could not be initialized
     */
    public String getExplainContextMembersXml(final Map<String, Object> values) throws WebserverSystemException {
        values.put("DATABASE", "ir/context/&lt;context id&gt;/resources/members");
        values.put("RESOURCE_NAME", ResourceType.CONTEXT.getLabel() + " member");
        return getExplainXml(values);
    }

    /**
     * Render an SRU explain response to describe the context database.
     *
     * @param values map containing variable names and values to fill out the Velocity macro.
     * @return explain response XML
     * @throws WebserverSystemException Thrown if Velocity could not be initialized
     */
    public String getExplainContextXml(final Map<String, Object> values) throws WebserverSystemException {
        values.put("DATABASE", "ir/contexts");
        values.put("RESOURCE_NAME", ResourceType.CONTEXT.getLabel());
        return getExplainXml(values);
    }

    /**
     * Render an SRU explain response to describe the item database.
     *
     * @param values map containing variable names and values to fill out the Velocity macro.
     * @return explain response XML
     * @throws WebserverSystemException Thrown if Velocity could not be initialized
     */
    public String getExplainItemXml(final Map<String, Object> values) throws WebserverSystemException {
        values.put("DATABASE", "ir/items");
        values.put("RESOURCE_NAME", ResourceType.ITEM.getLabel());
        return getExplainXml(values);
    }

    /**
     * Render an SRU explain response to describe the organizational unit database.
     *
     * @param values map containing variable names and values to fill out the Velocity macro.
     * @return explain response XML
     * @throws WebserverSystemException Thrown if Velocity could not be initialized
     */
    public String getExplainOuXml(final Map<String, Object> values) throws WebserverSystemException {
        values.put("DATABASE", "oum/organizational-units");
        values.put("RESOURCE_NAME", ResourceType.OU.getLabel());
        return getExplainXml(values);
    }

    /**
     * Render an SRU explain response to describe the report definition database.
     *
     * @param values map containing variable names and values to fill out the Velocity macro.
     * @return explain response XML
     * @throws WebserverSystemException Thrown if Velocity could not be initialized
     */
    public String getExplainReportDefinitionXml(final Map<String, Object> values) throws WebserverSystemException {
        values.put("DATABASE", "statistic/report-definitions");
        values.put("RESOURCE_NAME", XmlUtility.NAME_REPORT_DEFINITION);
        return getExplainXml(values);
    }

    /**
     * Render an SRU explain response to describe the role grant database.
     *
     * @param values map containing variable names and values to fill out the Velocity macro.
     * @return explain response XML
     * @throws WebserverSystemException Thrown if Velocity could not be initialized
     */
    public String getExplainRoleGrantXml(final Map<String, Object> values) throws WebserverSystemException {
        values.put("DATABASE", "aa/grants");
        values.put("RESOURCE_NAME", XmlUtility.NAME_GRANT);
        return getExplainXml(values);
    }

    /**
     * Render an SRU explain response to describe the role database.
     *
     * @param values map containing variable names and values to fill out the Velocity macro.
     * @return explain response XML
     * @throws WebserverSystemException Thrown if Velocity could not be initialized
     */
    public String getExplainRoleXml(final Map<String, Object> values) throws WebserverSystemException {
        values.put("DATABASE", "aa/roles");
        values.put("RESOURCE_NAME", XmlUtility.NAME_ROLE);
        return getExplainXml(values);
    }

    /**
     * Render an SRU explain response to describe the scope database.
     *
     * @param values map containing variable names and values to fill out the Velocity macro.
     * @return explain response XML
     * @throws WebserverSystemException Thrown if Velocity could not be initialized
     */
    public String getExplainScopeXml(final Map<String, Object> values) throws WebserverSystemException {
        values.put("DATABASE", "statistic/scopes");
        values.put("RESOURCE_NAME", XmlUtility.NAME_SCOPE);
        return getExplainXml(values);
    }

    /**
     * Render an SRU explain response to describe the set definition database.
     *
     * @param values map containing variable names and values to fill out the Velocity macro.
     * @return explain response XML
     * @throws WebserverSystemException Thrown if Velocity could not be initialized
     */
    public String getExplainSetDefinitionXml(final Map<String, Object> values) throws WebserverSystemException {
        values.put("DATABASE", "oai/set-definitions");
        values.put("RESOURCE_NAME", XmlUtility.NAME_SET_DEFINITION);
        return getExplainXml(values);
    }

    /**
     * Render an SRU explain response to describe the toc database.
     *
     * @param values map containing variable names and values to fill out the Velocity macro.
     * @return explain response XML
     * @throws WebserverSystemException Thrown if Velocity could not be initialized
     */
    public String getExplainTocXml(final Map<String, Object> values) throws WebserverSystemException {
        values.put("DATABASE", "ir/container/&lt;container id&gt;/tocs");
        values.put("RESOURCE_NAME", "toc");
        return getExplainXml(values);
    }

    /**
     * Render an SRU explain response to describe the user account database.
     *
     * @param values map containing variable names and values to fill out the Velocity macro.
     * @return explain response XML
     * @throws WebserverSystemException Thrown if Velocity could not be initialized
     */
    public String getExplainUserAccountXml(final Map<String, Object> values) throws WebserverSystemException {
        values.put("DATABASE", "aa/user-accounts");
        values.put("RESOURCE_NAME", XmlUtility.NAME_USER_ACCOUNT);
        return getExplainXml(values);
    }

    /**
     * Render an SRU explain response to describe the user group database.
     *
     * @param values map containing variable names and values to fill out the Velocity macro.
     * @return explain response XML
     * @throws WebserverSystemException Thrown if Velocity could not be initialized
     */
    public String getExplainUserGroupXml(final Map<String, Object> values) throws WebserverSystemException {
        values.put("DATABASE", "aa/user-groups");
        values.put("RESOURCE_NAME", XmlUtility.NAME_USER_GROUP);
        return getExplainXml(values);
    }

    /**
     * Render an SRU explain response.
     *
     * @param values map containing variable names and values to fill out the Velocity macro.
     * @return explain response XML
     * @throws WebserverSystemException Thrown if Velocity could not be initialized
     */
    private String getExplainXml(final Map<String, Object> values) throws WebserverSystemException {
        try {
            final URL baseUrl = new URL(XmlUtility.getEscidocBaseUrl());

            values.put("HOST", baseUrl.getHost());
            values.put("PORT", baseUrl.getPort());
        }
        catch (final Exception e) {
            throw new WebserverSystemException(e);
        }
        return getXml(RESOURCE_NAME, RESOURCES_PATH, values);
    }

    /**
     * Gets the explain XML provider.
     *
     * @return Returns the {@code ExplainXmlProvider} object.
     * @throws WebserverSystemException Thrown if the instance cannot be created due to an internal error.
     */
    public static ExplainXmlProvider getInstance() {
        return PROVIDER;
    }
}
