package de.escidoc.core.om.business.renderer;

import de.escidoc.core.common.business.Constants;
import de.escidoc.core.common.business.fedora.Utility;
import de.escidoc.core.common.business.fedora.resources.StatusType;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.util.xml.Elements;
import de.escidoc.core.common.util.xml.factory.ContainerFoXmlProvider;
import de.escidoc.core.common.util.xml.factory.XmlTemplateProviderConstants;
import de.escidoc.core.common.util.xml.renderer.VelocityXmlCommonFoXmlRenderer;
import de.escidoc.core.om.business.renderer.interfaces.ContainerFoXmlRendererInterface;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Render Container FoXMl.
 */
@Service
public class VelocityXmlContainerFoXmlRenderer implements ContainerFoXmlRendererInterface {

    private final VelocityXmlCommonFoXmlRenderer commonRenderer = new VelocityXmlCommonFoXmlRenderer();

    /**
     * See Interface for functional description.
     *
     * @param values               Map of values
     * @param properties           Map of expliced property values.
     * @param members              Map of expliced member values.
     * @param containerId          Objid of Container.
     * @param lastModificationDate Last Modification date of Container.
     * @return XML representation of Container
     */
    @Override
    public String render(
        final Map<String, Object> values, final Map<String, String> properties, final List<String> members,
        final String containerId, final String lastModificationDate, final List<Map<String, String>> contentRelations,
        final String comment, final Map<String, String> propertiesAsReferences) throws WebserverSystemException {

        values.put(XmlTemplateProviderConstants.TITLE, "Container " + containerId);
        addRelsExtValues(values, properties, members, containerId, lastModificationDate, contentRelations, comment,
            propertiesAsReferences);
        return ContainerFoXmlProvider.getInstance().getContainerFoXml(values);
    }

    /**
     * Add some values to different maps.
     * @param values
     * @param properties
     * @param members
     * @param containerId
     * @param lastModificationDate
     * @param contentRelations
     * @param comment
     * @param propertiesAsReferences
     * @throws de.escidoc.core.common.exceptions.system.WebserverSystemException
     */
    private static void addRelsExtValues(
        final Map<String, Object> values, final Map<String, String> properties, final Collection<String> members,
        final String containerId, final String lastModificationDate,
        final Collection<Map<String, String>> contentRelations, final String comment,
        final Map<String, String> propertiesAsReferences) throws WebserverSystemException {

        values.put(XmlTemplateProviderConstants.ESCIDOC_SREL_NS_PREFIX, Constants.STRUCTURAL_RELATIONS_NS_PREFIX);
        values.put(XmlTemplateProviderConstants.VAR_STRUCT_RELATIONS_NAMESPACE, Constants.STRUCTURAL_RELATIONS_NS_URI);

        values.put(XmlTemplateProviderConstants.ESCIDOC_PROPERTIES_NS_PREFIX, Constants.PROPERTIES_NS_PREFIX);
        values.put(XmlTemplateProviderConstants.ESCIDOC_PROPERTIES_NS, Constants.PROPERTIES_NS_URI);

        values.put(XmlTemplateProviderConstants.ESCIDOC_PROPERTIES_VERSION_NS_PREFIX, Constants.VERSION_NS_PREFIX);
        values.put(XmlTemplateProviderConstants.ESCIDOC_PROPERTIES_VERSION_NS, Constants.VERSION_NS_URI);

        values.put(XmlTemplateProviderConstants.ESCIDOC_RELEASE_NS_PREFIX, Constants.RELEASE_NS_PREFIX);
        values.put(XmlTemplateProviderConstants.ESCIDOC_RELEASE_NS, Constants.RELEASE_NS_URI);

        values.put("resourcesOntologiesNamespace", Constants.RESOURCES_NS_URI);
        values.put("contentRelationsNamespacePrefix", Constants.CONTENT_RELATIONS_NS_PREFIX_IN_RELSEXT);

        values.put(XmlTemplateProviderConstants.FRAMEWORK_BUILD_NUMBER, Utility.getBuildNumber());

        if (properties != null && !properties.isEmpty()) {
            values.put("properties", properties);
            values.put("propertiesAsReferences", propertiesAsReferences);
            // some values which are more relevant for ingest
            values.put(XmlTemplateProviderConstants.PUBLIC_STATUS, properties.get(Elements.ELEMENT_PUBLIC_STATUS));
            values.put(XmlTemplateProviderConstants.VERSION_STATUS, properties.get(Elements.ELEMENT_PUBLIC_STATUS));
            if (properties.get(Elements.ELEMENT_PUBLIC_STATUS).equals(StatusType.RELEASED.toString())) {
                // if status release add release number and date (date ist later
                // to update)
                values.put(XmlTemplateProviderConstants.LATEST_RELEASE_DATE, lastModificationDate);
                values.put(XmlTemplateProviderConstants.LATEST_RELEASE_NUMBER, "1");
            }
        }
        values.put(XmlTemplateProviderConstants.OBJID, containerId);
        values.put("latestVersionDate", lastModificationDate);
        values.put("latestVersionUserTitle", Utility.getCurrentUser()[1]);
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
     * @throws WebserverSystemException cf. Interface
     */
    @Override
    public String renderRelsExt(
        final Map<String, String> properties, final List<String> members, final String containerId,
        final String lastModificationDate, final List<Map<String, String>> contentRelations, final String comment,
        final Map<String, String> propertiesAsReferences) throws WebserverSystemException {

        final Map<String, Object> values = new HashMap<String, Object>();

        addRelsExtValues(values, properties, members, containerId, lastModificationDate, contentRelations, comment,
            propertiesAsReferences);
        return ContainerFoXmlProvider.getInstance().getContainerRelsExt(values);
    }

    /**
     * See Interface for functional description.
     *
     * @throws WebserverSystemException cf. Interface
     */
    @Override
    public String renderWov(
        final String id, final String title, final String versionNo, final String lastModificationDate,
        final String versionStatus, final String comment) throws WebserverSystemException {
        return commonRenderer.renderWov(id, title, versionNo, lastModificationDate, versionStatus, comment,
            Constants.CONTAINER_URL_BASE);
    }

}
