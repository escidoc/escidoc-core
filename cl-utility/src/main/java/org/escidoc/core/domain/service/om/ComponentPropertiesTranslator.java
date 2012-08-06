/**
 * 
 */
package org.escidoc.core.domain.service.om;

import java.net.URI;
import java.net.URISyntaxException;

import org.escidoc.core.business.domain.base.ID;
import org.escidoc.core.business.domain.base.Pid;
import org.escidoc.core.business.domain.om.component.ChecksumAlgorithm;
import org.escidoc.core.business.domain.om.component.ComponentPropertiesDO;
import org.escidoc.core.business.domain.om.component.ValidStatus;
import org.escidoc.core.domain.common.ReadOnlyLinkTO;
import org.escidoc.core.domain.common.ValidStatusTypeTO;
import org.escidoc.core.domain.components.ComponentPropertiesTypeTO;
import org.escidoc.core.domain.properties.ChecksumAlgorithmTypeTO;
import org.escidoc.core.domain.service.EntityMapperTranslator;
import org.springframework.stereotype.Service;

import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.util.configuration.EscidocConfiguration;
import de.escidoc.core.common.util.xml.XmlUtility;

/**
 * @author MIH
 * 
 */
@Service("domain.ComponentPropertiesTranslator")
public class ComponentPropertiesTranslator
    extends EntityMapperTranslator<ComponentPropertiesTypeTO, ComponentPropertiesDO> {
    public ComponentPropertiesDO To2Do(ComponentPropertiesTypeTO componentPropertiesTo, String validationProfile) {
        ComponentPropertiesDO.Builder componentPropertiesBuilder = new ComponentPropertiesDO.Builder(validationProfile);

        componentPropertiesBuilder.checksum(componentPropertiesTo.getChecksum());
        componentPropertiesBuilder.checksumAlgorithm(ChecksumAlgorithm.valueOf(componentPropertiesTo
            .getChecksumAlgorithm().value()));
        componentPropertiesBuilder.contentCategory(componentPropertiesTo.getContentCategory());
        componentPropertiesBuilder.createdBy(new ID(XmlUtility.getIdFromURI(componentPropertiesTo
            .getCreatedBy().getHref().toString())));
        componentPropertiesBuilder.creationDate(componentPropertiesTo.getCreationDate());
        componentPropertiesBuilder.fileName(componentPropertiesTo.getFileName());
        componentPropertiesBuilder.mimeType(componentPropertiesTo.getMimeType());
        componentPropertiesBuilder.pid(new Pid(componentPropertiesTo.getPid()));
        componentPropertiesBuilder.validStatusInfo(ValidStatus.valueOf(componentPropertiesTo.getValidStatus().value()));
        componentPropertiesBuilder.visibility(componentPropertiesTo.getVisibility());

        ComponentPropertiesDO componentPropertiesDo = componentPropertiesBuilder.build();
        return componentPropertiesDo;
    }

    public ComponentPropertiesTypeTO Do2To(ComponentPropertiesDO componentPropertiesDo) throws SystemException {
        ComponentPropertiesTypeTO componentPropertiesTo = new ComponentPropertiesTypeTO();

        try {
            componentPropertiesTo.setBase(new URI(EscidocConfiguration.ESCIDOC_CORE_BASEURL));
            componentPropertiesTo.setChecksum(componentPropertiesDo.getChecksum());
            componentPropertiesTo.setChecksumAlgorithm(ChecksumAlgorithmTypeTO.valueOf(componentPropertiesDo
                .getChecksumAlgorithm().toString()));
            componentPropertiesTo.setContentCategory(componentPropertiesDo.getContentCategory());

            if (componentPropertiesDo.getCreatedBy() != null) {
                ReadOnlyLinkTO createdByLink = new ReadOnlyLinkTO();
                createdByLink.setHref(new URI(XmlUtility
                    .getUserAccountHref(componentPropertiesDo.getCreatedBy().getValue())));
                createdByLink.setTitle("component properties");
                createdByLink.setType("simple");
                componentPropertiesTo.setCreatedBy(createdByLink);
            }

            componentPropertiesTo.setCreationDate(componentPropertiesDo.getCreationDate());
            // TODO: integrate description in DO
            // componentPropertiesTo.setDescription(componentPropertiesDo.get???)
            componentPropertiesTo.setFileName(componentPropertiesDo.getFileName());
            if (componentPropertiesDo.getOriginItem() != null && componentPropertiesDo.getOriginComponent() != null) {
                componentPropertiesTo.setHref(new URI(XmlUtility.getComponentHref(componentPropertiesDo
                    .getOriginItem().getValue(), componentPropertiesDo.getOriginComponent().getValue())));
            }
            // TODO: integrate lmd???
            //componentPropertiesTo.setLastModificationDate(componentPropertiesDo.get);
            componentPropertiesTo.setMimeType(componentPropertiesDo.getMimeType());
            if (componentPropertiesDo.getPid() != null) {
                componentPropertiesTo.setPid(componentPropertiesDo.getPid().getValue());
            }
            componentPropertiesTo.setTitle("component-properties");
            componentPropertiesTo.setType("simple");
            componentPropertiesTo.setValidStatus(ValidStatusTypeTO.valueOf(componentPropertiesDo.getValidStatusInfo().toString()));
            componentPropertiesTo.setVisibility(componentPropertiesDo.getVisibility());
        }
        catch (URISyntaxException e) {
            throw new SystemException(e.getMessage(), e);
        }

        return componentPropertiesTo;
    }
}