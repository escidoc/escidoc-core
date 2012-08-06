/**
 * 
 */
package org.escidoc.core.domain.service.om;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;

import org.escidoc.core.business.domain.base.ID;
import org.escidoc.core.business.domain.common.MdRecordDO;
import org.escidoc.core.business.domain.om.component.ComponentDO;
import org.escidoc.core.domain.components.ComponentTypeTO;
import org.escidoc.core.domain.metadatarecords.MdRecordTypeTO;
import org.escidoc.core.domain.metadatarecords.MdRecordsTypeTO;
import org.escidoc.core.domain.service.EntityMapperTranslator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.util.configuration.EscidocConfiguration;
import de.escidoc.core.common.util.xml.XmlUtility;

/**
 * @author MIH
 * 
 */
@Service("domain.ComponentTranslator")
public class ComponentTranslator extends EntityMapperTranslator<ComponentTypeTO, ComponentDO> {

    @Autowired
    @Qualifier("domain.ContentTranslator")
    private ContentTranslator contentTranslator;
    
    @Autowired
    @Qualifier("domain.MdRecordTranslator")
    private MdRecordTranslator mdRecordTranslator;
    
    @Autowired
    @Qualifier("domain.ComponentPropertiesTranslator")
    private ComponentPropertiesTranslator componentPropertiesTranslator;
    
    public ComponentDO To2Do(ComponentTypeTO componentTo, String validationProfile) {
        ComponentDO.Builder componentBuilder = new ComponentDO.Builder(validationProfile);
        if (componentTo.getHref() != null) {
            componentBuilder.id(new ID(XmlUtility.getIdFromURI(componentTo.getHref().toString())));
        }
        if (componentTo.getContent() != null) {
            componentBuilder.content(contentTranslator.To2Do(componentTo.getContent(), validationProfile));
        }
        if (componentTo.getProperties() != null) {
            componentBuilder.properties(componentPropertiesTranslator.To2Do(componentTo.getProperties(), validationProfile));
        }
        if (componentTo.getMdRecords() != null && componentTo.getMdRecords().getMdRecord() != null
            && !componentTo.getMdRecords().getMdRecord().isEmpty()) {
            Set<MdRecordDO> mdRecordDos = new HashSet<MdRecordDO>();
            for (MdRecordTypeTO mdRecordTo : componentTo.getMdRecords().getMdRecord()) {
                mdRecordDos.add(mdRecordTranslator.To2Do(mdRecordTo, validationProfile));
            }
            componentBuilder.mdRecords(mdRecordDos);
        }

        ComponentDO componentDo = componentBuilder.build();
        return componentDo;
    }

    public ComponentTypeTO Do2To(ComponentDO componentDo) throws SystemException {
        ComponentTypeTO componentTo = new ComponentTypeTO();
        try {
            componentTo.setBase(new URI(EscidocConfiguration.ESCIDOC_CORE_BASEURL));
            componentTo.setContent(contentTranslator.Do2To(componentDo.getContent()));
            if (componentDo.getOrigin() != null && componentDo.getId() != null) {
                componentTo.setHref(new URI(XmlUtility.getComponentHref(componentDo.getOrigin().getValue(), componentDo
                    .getId().getValue())));
            }

            componentTo.setInherited(componentDo.getInherited());

            //TODO: integrate lmd in DO??
            //componentTo.setLastModificationDate(componentDo.get???);
            
            if (componentDo.getMdRecords() != null && !componentDo.getMdRecords().isEmpty()) {
                MdRecordsTypeTO mdRecordsTo = new MdRecordsTypeTO();
                for (MdRecordDO mdRecordDo : componentDo.getMdRecords()) {
                    mdRecordsTo.getMdRecord().add(mdRecordTranslator.Do2To(mdRecordDo));
                }
                componentTo.setMdRecords(mdRecordsTo);
            }

            if (componentDo.getProperties() != null) {
                componentTo.setProperties(componentPropertiesTranslator.Do2To(componentDo.getProperties()));
            }
            componentTo.setTitle("component properties");
            componentTo.setType("simple");
            
        }
        catch (URISyntaxException e) {
            throw new SystemException(e.getMessage(), e);
        }
        return componentTo;
    }
}