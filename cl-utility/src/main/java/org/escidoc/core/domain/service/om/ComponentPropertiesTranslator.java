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
 *         Translates Item-Component Properties from/to DO/TO.
 * 
 */
@Service("domain.ComponentPropertiesTranslator")
public class ComponentPropertiesTranslator
    extends EntityMapperTranslator<ComponentPropertiesTypeTO, ComponentPropertiesDO> {

    /**
     * Translates Item-Component Properties from TO to DO.
     * 
     * @param componentPropertiesTo
     *            componentPropertiesTypeTO
     * @param validationProfile
     *            for oval-validation @see ValidationProfile.java
     * @return ComponentPropertiesDO DO
     * @throws SystemException
     *             e
     * 
     */
    public ComponentPropertiesDO To2Do(ComponentPropertiesTypeTO componentPropertiesTo, String validationProfile)
        throws SystemException {

        ComponentPropertiesDO.Builder componentPropertiesBuilder = new ComponentPropertiesDO.Builder(validationProfile);

        // checksum
        componentPropertiesBuilder.checksum(componentPropertiesTo.getChecksum());

        // checksum algorithm
        if (componentPropertiesTo.getChecksumAlgorithm() != null) {
            componentPropertiesBuilder.checksumAlgorithm(ChecksumAlgorithm.valueOf(componentPropertiesTo
                .getChecksumAlgorithm().value()));
        }

        // content category
        componentPropertiesBuilder.contentCategory(componentPropertiesTo.getContentCategory());

        // created by
        if (componentPropertiesTo.getCreatedBy() != null && componentPropertiesTo.getCreatedBy().getHref() != null) {
            componentPropertiesBuilder.createdBy(new ID(XmlUtility.getIdFromURI(componentPropertiesTo
                .getCreatedBy().getHref().toString())));
        }

        // creation date
        componentPropertiesBuilder.creationDate(componentPropertiesTo.getCreationDate());
        // file name
        componentPropertiesBuilder.fileName(componentPropertiesTo.getFileName());
        // mime type
        componentPropertiesBuilder.mimeType(componentPropertiesTo.getMimeType());
        // pid
        componentPropertiesBuilder.pid(new Pid(componentPropertiesTo.getPid()));

        // valid status
        if (componentPropertiesTo.getValidStatus() != null) {
            componentPropertiesBuilder.validStatusInfo(ValidStatus.valueOf(componentPropertiesTo
                .getValidStatus().value()));
        }

        // visibility
        componentPropertiesBuilder.visibility(componentPropertiesTo.getVisibility());

        ComponentPropertiesDO componentPropertiesDo = componentPropertiesBuilder.build();
        return componentPropertiesDo;
    }

    /**
     * Translates Item-Component Properties from DO to TO.
     * 
     * @param componentPropertiesDo
     *            componentPropertiesDo
     * @return ComponentPropertiesTypeTO TO
     * @throws SystemException
     *             e
     * 
     */
    public ComponentPropertiesTypeTO Do2To(ComponentPropertiesDO componentPropertiesDo) throws SystemException {
        ComponentPropertiesTypeTO componentPropertiesTo = new ComponentPropertiesTypeTO();

        try {
            // base
            componentPropertiesTo.setBase(new URI(EscidocConfiguration.ESCIDOC_CORE_BASEURL));
            // checksum
            componentPropertiesTo.setChecksum(componentPropertiesDo.getChecksum());
            // checksum algorithm
            componentPropertiesTo.setChecksumAlgorithm(ChecksumAlgorithmTypeTO.valueOf(componentPropertiesDo
                .getChecksumAlgorithm().toString()));
            // content category
            componentPropertiesTo.setContentCategory(componentPropertiesDo.getContentCategory());

            // created by
            if (componentPropertiesDo.getCreatedBy() != null) {
                ReadOnlyLinkTO createdByLink = new ReadOnlyLinkTO();
                createdByLink.setHref(new URI(XmlUtility.getUserAccountHref(componentPropertiesDo
                    .getCreatedBy().getValue())));
                createdByLink.setTitle("component properties");
                createdByLink.setType("simple");
                componentPropertiesTo.setCreatedBy(createdByLink);
            }

            // creation date
            componentPropertiesTo.setCreationDate(componentPropertiesDo.getCreationDate());
            // TODO: integrate description in DO
            // componentPropertiesTo.setDescription(componentPropertiesDo.get???)

            // file name
            componentPropertiesTo.setFileName(componentPropertiesDo.getFileName());
            // href
            if (componentPropertiesDo.getOriginItem() != null && componentPropertiesDo.getOriginComponent() != null) {
                componentPropertiesTo.setHref(new URI(XmlUtility.getComponentHref(componentPropertiesDo
                    .getOriginItem().getValue(), componentPropertiesDo.getOriginComponent().getValue())));
            }

            // TODO: integrate lmd???
            // componentPropertiesTo.setLastModificationDate(componentPropertiesDo.get);

            // mime type
            componentPropertiesTo.setMimeType(componentPropertiesDo.getMimeType());

            // pid
            if (componentPropertiesDo.getPid() != null) {
                componentPropertiesTo.setPid(componentPropertiesDo.getPid().getValue());
            }

            // title
            componentPropertiesTo.setTitle("component-properties");
            // type
            componentPropertiesTo.setType("simple");
            // valid status
            componentPropertiesTo.setValidStatus(ValidStatusTypeTO.valueOf(componentPropertiesDo
                .getValidStatusInfo().toString()));
            // visibility
            componentPropertiesTo.setVisibility(componentPropertiesDo.getVisibility());
        }
        catch (URISyntaxException e) {
            throw new SystemException(e.getMessage(), e);
        }

        return componentPropertiesTo;
    }
}