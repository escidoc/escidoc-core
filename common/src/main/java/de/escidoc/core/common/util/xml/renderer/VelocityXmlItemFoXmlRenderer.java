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
package de.escidoc.core.common.util.xml.renderer;

import de.escidoc.core.common.business.Constants;
import de.escidoc.core.common.business.fedora.TripleStoreUtility;
import de.escidoc.core.common.business.fedora.Utility;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.util.xml.XmlUtility;
import de.escidoc.core.common.util.xml.factory.ItemFoXmlProvider;
import de.escidoc.core.common.util.xml.factory.XmlTemplateProvider;
import de.escidoc.core.common.util.xml.renderer.interfaces.ItemFoXmlRendererInterface;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * velocity render for FoXML representation of Item and Item sub-elements.
 * 
 * @author ??
 * 
 */
public class VelocityXmlItemFoXmlRenderer implements ItemFoXmlRendererInterface {

    private VelocityXmlCommonFoXmlRenderer commonRenderer =
        new VelocityXmlCommonFoXmlRenderer();

    // not needed if no one logs
    // private static AppLogger log =
    // new AppLogger(VelocityXmlItemFoXmlRenderer.class.getName());

    private String buildNumber = null;

    /**
     * See Interface for functional description.
     * 
     * @param datastreams
     * @return
     * @throws SystemException
     */
    public String renderItem(
        final Map<String, Object> values, final String itemId,
        final String lastModificationDate, final String[] components,
        final Map<String, String> properties,
        final List<Map<String, String>> contentRelations,
        final Map<String, String> propertiesAsReferences,
        final Map<String, String> propertiesVersion) throws SystemException {

        String result = null;
        values.put("title", "Item " + itemId);
        addRelsExtValues(values, itemId, lastModificationDate, components,
            properties, contentRelations, propertiesAsReferences,
            propertiesVersion);

        result = ItemFoXmlProvider.getInstance().getItemFoXml(values);
        return result;
    }

    public String renderComponent(final Map<String, Object> values)
        throws WebserverSystemException {
        String result = null;

        result = ItemFoXmlProvider.getInstance().getComponentFoXml(values);
        return result;
    }

    /**
     * See Interface for functional description.
     * 
     * @param properties
     * @param title
     * @param members
     * @param adminDescriptorId
     * @param containerId
     * @param lastModificationDate
     * @param contentRelations
     * @param comment
     * @return
     * @throws WebserverSystemException
     *             cf. Interface
     * @see de.escidoc.core.om.business.renderer.interfaces.ContainerFoXmlRendererInterface#renderRelsExt(java.util.HashMap,
     *      java.lang.String, java.util.ArrayList, java.lang.String,
     *      java.lang.String, java.lang.String, java.util.Vector,
     *      java.lang.String)
     */
    public String renderDefaultDc(final String componentId)
        throws WebserverSystemException {

        Map<String, Object> values = new HashMap<String, Object>();

        values.put("title", "component " + componentId);
        values.put("componentId", componentId);

        String result =
            ItemFoXmlProvider.getInstance().getComponentDefaultDc(values);
        return result;
    }

    private void addRelsExtValues(
        final Map<String, Object> values, final String itemId,
        final String lastModificationDate, final String[] components,
        final Map<String, String> properties,
        final List<Map<String, String>> contentRelations,
        final Map<String, String> propertiesAsReferences,
        final Map<String, String> propertiesVersion)
        throws WebserverSystemException {

        addRelsExtNamespaceValues(values);

        propertiesVersion.put(XmlTemplateProvider.LATEST_VERSION_DATE,
            lastModificationDate);

        if ((properties != null) && (properties.size() > 0)) {
            values.put("properties", properties);
            values.put("propertiesAsReferences", propertiesAsReferences);
            values.put("propertiesVersion", propertiesVersion);
        }
        if (this.buildNumber == null) {
            this.buildNumber = Utility.getInstance().getBuildNumber();
        }

        values
            .put(XmlTemplateProvider.FRAMEWORK_BUILD_NUMBER, this.buildNumber);
        values.put("itemId", itemId);

        // values.put("latestVersionUser",
        // Utility.getInstance().getCurrentUser()[0]);
        // values.put("latestVersionUserTitle",
        // Utility.getInstance().getCurrentUser()[1]);
        // values.put("latestVersionComment", comment);
        if ((contentRelations != null) && (!contentRelations.isEmpty())) {
            values.put("contentRelations", contentRelations);
        }
        List<String> componentsVector = new ArrayList<String>();
        if ((components != null) && (components.length > 0)) {

            componentsVector.addAll(Arrays.asList(components).subList(1, components.length));
            values.put("components", componentsVector);
        }

    }

    private void addRelsExtNamespaceValues(Map<String, Object> values) {

        // values.put("itemNamespace", Constants.ITEM_NAMESPACE_URI);
        // values.put("itemNamespacePrefix", Constants.ITEM_NAMESPACE_PREFIX);

        values.put("escidocPropertiesNamespacePrefix",
            Constants.PROPERTIES_NS_PREFIX);
        values.put("escidocPropertiesNamespace", Constants.PROPERTIES_NS_URI);

        values.put("escidocPropertiesVersionNamespacePrefix",
            Constants.VERSION_NS_PREFIX);
        values.put("escidocPropertiesVersionNamespace",
            Constants.VERSION_NS_URI);

        values.put("escidocPropertiesReleaseNamespacePrefix",
            Constants.RELEASE_NS_PREFIX);
        values.put("escidocPropertiesReleaseNamespace",
            Constants.RELEASE_NS_URI);

        values.put("escidocResourcesNamespace", Constants.RESOURCES_NS_URI);

        values.put("escidocRelationsNamespacePrefix",
            Constants.STRUCTURAL_RELATIONS_NS_PREFIX);
        values.put("escidocRelationsNamespace",
            Constants.STRUCTURAL_RELATIONS_NS_URI);

        values.put("contentRelationsNamespacePrefix",
            Constants.CONTENT_RELATIONS_NS_PREFIX_IN_RELSEXT);

    }

    /**
     * See Interface for functional description.
     * 
     * @param properties
     * @param title
     * @param members
     * @param adminDescriptorId
     * @param containerId
     * @param lastModificationDate
     * @param contentRelations
     * @param comment
     * @return
     * @throws WebserverSystemException
     *             cf. Interface
     * @see de.escidoc.core.om.business.renderer.interfaces.ContainerFoXmlRendererInterface#renderRelsExt(java.util.HashMap,
     *      java.lang.String, java.util.ArrayList, java.lang.String,
     *      java.lang.String, java.lang.String, java.util.Vector,
     *      java.lang.String)
     */
    public String renderItemRelsExt(
        final String itemId, final String lastModificationDate,
        final String[] components, final Map<String, String> properties,
        final List<Map<String, String>> contentRelations,
        final Map<String, String> propertiesAsReferences,
        final Map<String, String> propertiesVersion)
        throws WebserverSystemException {

        Map<String, Object> values = new HashMap<String, Object>();
        addRelsExtValues(values, itemId, lastModificationDate, components,
            properties, contentRelations, propertiesAsReferences,
            propertiesVersion);

        return ItemFoXmlProvider.getInstance().getItemRelsExt(values);
    }

    /**
     * See Interface for functional description.
     * 
     * @param id
     * @param title
     * @param versionNo
     * @param lastModificationDate
     * @param versionStatus
     * @param validStatus
     * @param comment
     * @return
     * @throws WebserverSystemException
     *             cf. Interface
     * @see de.escidoc.core.om.business.renderer.interfaces.ContainerFoXmlRendererInterface#renderWov(java.lang.String,
     *      java.lang.String, java.lang.String, java.lang.String,
     *      java.lang.String, java.lang.String, java.lang.String)
     */
    public String renderWov(
        final String id, final String title, final String versionNo,
        final String lastModificationDate, final String versionStatus,
        final String comment) throws WebserverSystemException {
        return commonRenderer.renderWov(id, title, versionNo,
            lastModificationDate, versionStatus, comment,
            Constants.ITEM_URL_BASE);
    }

    /**
     * Render RELS-EXT of a Component.
     * 
     * @param id
     *            Objid of Component.
     * @param properties
     *            Component properties
     * @param inCreate
     *            Set true if Component is to create, false if it's an update.
     * @return RELS-EXT XML representation
     * 
     * @throws WebserverSystemException
     *             Thrown in case of internal error.
     */
    public String renderComponentRelsExt(
        final String id, final Map<String, String> properties,
        final boolean inCreate) throws WebserverSystemException {
        Map<String, Object> values = new HashMap<String, Object>();

        addRelsExtNamespaceValues(values);
        // values.put("itemId", itemId);
        if (this.buildNumber == null) {
            this.buildNumber = Utility.getInstance().getBuildNumber();
        }

        values.put(XmlTemplateProvider.OBJECT_PID,
            properties.get(TripleStoreUtility.PROP_OBJECT_PID));
        values
            .put(XmlTemplateProvider.FRAMEWORK_BUILD_NUMBER, this.buildNumber);
        values.put(XmlTemplateProvider.OBJID, id);
        values.put(XmlTemplateProvider.CREATED_BY_ID,
            properties.get(TripleStoreUtility.PROP_CREATED_BY_ID));
        values
            .put(
                XmlTemplateProvider.CREATED_BY_TITLE,
                getEscpapedValue(
                    properties.get(TripleStoreUtility.PROP_CREATED_BY_TITLE),
                    false));

        values
            .put(
                XmlTemplateProvider.CONTENT_CATEGORY,
                getEscpapedValue((String) properties
                    .get(TripleStoreUtility.PROP_COMPONENT_CONTENT_CATEGORY),
                    false));
        values.put(
            XmlTemplateProvider.VISIBILITY,
            getEscpapedValue(
                (String) properties.get(TripleStoreUtility.PROP_VISIBILITY),
                false));
        // values.put(XmlTemplateProvider.VAR_FILESIZE, getEscpapedValue(
        // (String) properties.get(TripleStoreUtility.PROP_FILESIZE), false));
        // values.put(XmlTemplateProvider.VAR_FILENAME, getEscpapedValue(
        // (String) properties.get(TripleStoreUtility.PROP_FILENAME), false));
        // if (properties.get(TripleStoreUtility.PROP_DESCRIPTION) != null) {
        // values.put(XmlTemplateProvider.VAR_DESCRIPTION, getEscpapedValue(
        // (String) properties.get(TripleStoreUtility.PROP_DESCRIPTION),
        // false));
        // }
        if (properties.get(TripleStoreUtility.PROP_MIME_TYPE) != null) {
            values.put(
                XmlTemplateProvider.MIME_TYPE,
                getEscpapedValue(
                    (String) properties.get(TripleStoreUtility.PROP_MIME_TYPE),
                    false));
        }
        if (properties.get(TripleStoreUtility.PROP_VALID_STATUS) != null) {
            values.put(
                XmlTemplateProvider.VALID_STATUS,
                getEscpapedValue((String) properties
                    .get(TripleStoreUtility.PROP_VALID_STATUS), false));
        }
        // if (properties.get(TripleStoreUtility.PROP_LOCATOR_URL) != null) {
        // values.put(XmlTemplateProvider.VAR_LOCATOR_URL, getEscpapedValue(
        // (String) properties.get(TripleStoreUtility.PROP_LOCATOR_URL),
        // false));
        // }
        if (inCreate) {
            values.put("inCreate", inCreate);
        }

        String result =
            ItemFoXmlProvider.getInstance().getComponentRelsExt(values);
        return result;
    }

    /**
     * Escapes the value for xml.
     * 
     * @param value
     *            The string to escape.
     * @param isAttribute
     *            If it is an attribute or element value.
     * @return The escaped string.
     */
    private String getEscpapedValue(
        final String value, final boolean isAttribute) {

        return XmlUtility.escapeForbiddenXmlCharacters(value);
    }
}
