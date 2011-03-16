package de.escidoc.core.om.business.renderer;

import de.escidoc.core.common.business.Constants;
import de.escidoc.core.common.business.fedora.Utility;
import de.escidoc.core.common.business.fedora.resources.StatusType;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.util.xml.Elements;
import de.escidoc.core.common.util.xml.factory.ContainerFoXmlProvider;
import de.escidoc.core.common.util.xml.factory.XmlTemplateProvider;
import de.escidoc.core.common.util.xml.renderer.VelocityXmlCommonFoXmlRenderer;
import de.escidoc.core.om.business.renderer.interfaces.ContainerFoXmlRendererInterface;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * Render Container FoXMl.
 * 
 * 
 */
public class VelocityXmlContainerFoXmlRenderer
    implements ContainerFoXmlRendererInterface {

    private final VelocityXmlCommonFoXmlRenderer commonRenderer =
        new VelocityXmlCommonFoXmlRenderer();


    /**
     * See Interface for functional description.
     * 
     * @param values
     *            Map of values
     * @param properties
     *            Map of expliced property values.
     * @param members
     *            Map of expliced member values.
     * @param containerId
     *            Objid of Container.
     * @param lastModificationDate
     *            Last Modification date of Container.
     * @param contentRelations
     * @param comment
     * @param propertiesAsReferences
     * @return XML representation of Container
     * @throws SystemException
     * @see de.escidoc.core.oum.business.renderer.interfaces.OrganizationalUnitFoXmlRendererInterface#render(Map)
     */
    @Override
    public String render(
        final Map<String, Object> values,
        final Map<String, String> properties, final List<String> members,
        final String containerId, final String lastModificationDate,
        final List<Map<String, String>> contentRelations,
        final String comment,
        final Map<String, String> propertiesAsReferences)
        throws SystemException {

        values.put(XmlTemplateProvider.TITLE, "Container " + containerId);
        addRelsExtValues(values, properties, members, containerId,
            lastModificationDate, contentRelations, comment,
            propertiesAsReferences);
        return ContainerFoXmlProvider.getInstance().getContainerFoXml(values);
    }

    /**
     * Add some values to different maps.
     * 
     * @param values
     * @param properties
     * @param members
     * @param containerId
     * @param lastModificationDate
     * @param contentRelations
     * @param comment
     * @param propertiesAsReferences
     * @throws WebserverSystemException
     */
    private static void addRelsExtValues(final Map<String, Object> values, final Map<String, String> properties,
                                         final Collection<String> members, final String containerId,
                                         final String lastModificationDate,
                                         final Collection<Map<String, String>> contentRelations, final String comment,
                                         final Map<String, String> propertiesAsReferences)
        throws WebserverSystemException {

        values.put(XmlTemplateProvider.ESCIDOC_SREL_NS_PREFIX,
            Constants.STRUCTURAL_RELATIONS_NS_PREFIX);
        values.put(XmlTemplateProvider.VAR_STRUCT_RELATIONS_NAMESPACE,
            Constants.STRUCTURAL_RELATIONS_NS_URI);

        values.put(XmlTemplateProvider.ESCIDOC_PROPERTIES_NS_PREFIX,
            Constants.PROPERTIES_NS_PREFIX);
        values.put(XmlTemplateProvider.ESCIDOC_PROPERTIES_NS,
            Constants.PROPERTIES_NS_URI);

        values.put(XmlTemplateProvider.ESCIDOC_PROPERTIES_VERSION_NS_PREFIX,
            Constants.VERSION_NS_PREFIX);
        values.put(XmlTemplateProvider.ESCIDOC_PROPERTIES_VERSION_NS,
            Constants.VERSION_NS_URI);

        values.put(XmlTemplateProvider.ESCIDOC_RELEASE_NS_PREFIX,
            Constants.RELEASE_NS_PREFIX);
        values.put(XmlTemplateProvider.ESCIDOC_RELEASE_NS,
            Constants.RELEASE_NS_URI);

        values.put("resourcesOntologiesNamespace", Constants.RESOURCES_NS_URI);
        values.put("contentRelationsNamespacePrefix",
            Constants.CONTENT_RELATIONS_NS_PREFIX_IN_RELSEXT);

        values.put(XmlTemplateProvider.FRAMEWORK_BUILD_NUMBER, Utility
            .getInstance().getBuildNumber());

        if (properties != null && !properties.isEmpty()) {
            values.put("properties", properties);
            values.put("propertiesAsReferences", propertiesAsReferences);
            // some values which are more relevant for ingest
            values.put(XmlTemplateProvider.PUBLIC_STATUS, properties
                .get(Elements.ELEMENT_PUBLIC_STATUS));
            values.put(XmlTemplateProvider.VERSION_STATUS, properties
                .get(Elements.ELEMENT_PUBLIC_STATUS));
            if (properties.get(Elements.ELEMENT_PUBLIC_STATUS).equals(
                StatusType.RELEASED.toString())) {
                // if status release add release number and date (date ist later
                // to update)
                values.put(XmlTemplateProvider.LATEST_RELEASE_DATE, lastModificationDate);
                values.put(XmlTemplateProvider.LATEST_RELEASE_NUMBER, "1");
            }
        }
        values.put(XmlTemplateProvider.OBJID, containerId);
        values.put("latestVersionDate", lastModificationDate);
        values.put("latestVersionUserTitle", Utility
            .getInstance().getCurrentUser()[1]);
        values.put("latestVersionComment", comment);
        if (contentRelations != null && !contentRelations.isEmpty()) {
            values.put("contentRelations", contentRelations);
        }
        if (members != null && !members.isEmpty()) {
            values.put("members", members);
        }
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
     * @see ContainerFoXmlRendererInterface#renderRelsExt(HashMap,
     *      String, ArrayList, String,
     *      String, String, Vector,
     *      String)
     */
    @Override
    public String renderRelsExt(
        final Map<String, String> properties, final List<String> members,
        final String containerId, final String lastModificationDate,
        final List<Map<String, String>> contentRelations,
        final String comment,
        final Map<String, String> propertiesAsReferences)
        throws WebserverSystemException {

        final Map<String, Object> values = new HashMap<String, Object>();

        addRelsExtValues(values, properties, members, containerId,
            lastModificationDate, contentRelations, comment,
            propertiesAsReferences);
        return ContainerFoXmlProvider.getInstance().getContainerRelsExt(values);
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
     * @see ContainerFoXmlRendererInterface#renderWov(String,
     *      String, String, String,
     *      String, String, String)
     */
    @Override
    public String renderWov(
        final String id, final String title, final String versionNo,
        final String lastModificationDate, final String versionStatus,
        final String comment) throws WebserverSystemException {
        return commonRenderer.renderWov(id, title, versionNo,
            lastModificationDate, versionStatus, comment,
            Constants.CONTAINER_URL_BASE);
    }


}
