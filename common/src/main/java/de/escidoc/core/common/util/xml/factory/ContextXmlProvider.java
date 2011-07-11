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
 * XML Provider of Context.
 *
 * @author Steffen Wagner
 */
public final class ContextXmlProvider extends InfrastructureXmlProvider {

    private static final String CONTEXT_PATH = "/context";

    private static final String RESOURCES_PATH = CONTEXT_PATH;

    private static final String DATA_PATH = CONTEXT_PATH;

    private static final String CONTEXT_RESOURCE_NAME = "context";

    private static final String CONTEXT_LIST_RESOURCE_NAME = "context-list";

    private static final String CONTEXT_MEMBER_LIST_RESOURCE_NAME = "member-list";

    private static final String CONTEXT_MEMBER_REF_LIST_RESOURCE_NAME = "member-ref-list";

    private static final String CONTEXT_RESOURCE_WITHDRAWN_NAME = "withdrawn";

    private static final String PROPERTIES_RESOURCE_NAME = "properties";

    private static final String ADMIN_DESCRIPTOR_RESOURCE_NAME = "admin-descriptor";

    private static final String ADMIN_DESCRIPTORS_RESOURCE_NAME = "admin-descriptors";

    private static final String RESOURCES_RESOURCE_NAME = "resources";

    private static final ContextXmlProvider PROVIDER = new ContextXmlProvider();

    /**
     * Private constructor to prevent initialization.
     */
    private ContextXmlProvider() {
    }

    /**
     * Gets the XML PROVIDER.
     *
     * @return Returns the {@code ContextXmlProvider} object.
     */
    public static ContextXmlProvider getInstance() {
        return PROVIDER;
    }

    /**
     * Get the Context XML representation.
     *
     * @param values Map of values.
     * @return XML Context representation.
     * @throws WebserverSystemException Thrown if anything fails.
     */
    public String getContextXml(final Map<String, Object> values) throws WebserverSystemException {

        return getXml(CONTEXT_RESOURCE_NAME, CONTEXT_PATH, values);
    }

    /**
     * Get XML representation of Context properties.
     *
     * @param values Map of values.
     * @return XML representation of Properties.
     * @throws WebserverSystemException If anything fails.
     */
    public String getPropertiesXml(final Map<String, Object> values) throws WebserverSystemException {

        return getXml(PROPERTIES_RESOURCE_NAME, CONTEXT_PATH, values);
    }

    /**
     * Get the XML representation of Contexts admin-descriptor.
     *
     * @param values value map
     * @return XML representation of admin-descriptor
     * @throws WebserverSystemException Thrown if rendering failed.
     */
    public String getAdminDescriptorXml(final Map<String, Object> values) throws WebserverSystemException {

        return getXml(ADMIN_DESCRIPTOR_RESOURCE_NAME, CONTEXT_PATH, values);
    }

    /**
     * Get the XML representation of Contexts admin-descriptors.
     *
     * @param values value map
     * @return XML representation of admin-descriptor
     * @throws WebserverSystemException Thrown if rendering failed.
     */
    public String getAdminDescriptorsXml(final Map<String, Object> values) throws WebserverSystemException {

        return getXml(ADMIN_DESCRIPTORS_RESOURCE_NAME, CONTEXT_PATH, values);
    }

    /**
     * Get XML representation of Resources.
     *
     * @param values Map of values.
     * @return XML representation of Resources.
     * @throws WebserverSystemException If anything fails.
     */
    public String getResourcesXml(final Map<String, Object> values) throws WebserverSystemException {

        return getXml(RESOURCES_RESOURCE_NAME, RESOURCES_PATH, values);
    }

    /**
     * Get XML representation of Data.
     *
     * @param values Map of values.
     * @return XML representation of context list.
     * @throws WebserverSystemException If anything fails.
     */
    public String getDataXml(final Map<String, Object> values) throws WebserverSystemException {

        return getXml(CONTEXT_RESOURCE_NAME, DATA_PATH, values);
    }

    /**
     * Generates a list of Contexts.
     *
     * @param values Map of values.
     * @return XML representation of context list.
     * @throws WebserverSystemException If anything fails.
     */
    public String getContextListXml(final Map<String, Object> values) throws WebserverSystemException {

        return getXml(CONTEXT_LIST_RESOURCE_NAME, RESOURCES_PATH, values);
    }

    /**
     * Generates a list of context members.
     *
     * @param values Map of values.
     * @return XML representation of MemberList.
     * @throws WebserverSystemException If anything fails.
     */
    public String getMemberListXml(final Map<String, Object> values) throws WebserverSystemException {
        return getXml(CONTEXT_MEMBER_LIST_RESOURCE_NAME, RESOURCES_PATH, values);
    }

    /**
     * Generates a list of context member references.
     *
     * @param values Map of values.
     * @return XML representation of MemberRefList
     * @throws WebserverSystemException If anything fails.
     */
    public String getMemberRefListXml(final Map<String, Object> values) throws WebserverSystemException {
        return getXml(CONTEXT_MEMBER_REF_LIST_RESOURCE_NAME, RESOURCES_PATH, values);
    }

    /**
     * Get withdrawn message as XML snippet.
     *
     * @param values Value Map.
     * @return XML snippet with withdrawn message.
     * @throws WebserverSystemException If anything fails.
     */
    public String getWithdrawnMessageXml(final Map<String, Object> values) throws WebserverSystemException {
        return getXml(CONTEXT_RESOURCE_WITHDRAWN_NAME, RESOURCES_PATH, values);
    }
}
