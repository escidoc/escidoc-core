/**
 * 
 */
package org.escidoc.core.domain.service.om;

import java.util.HashSet;
import java.util.Set;

import org.escidoc.core.business.domain.base.ID;
import org.escidoc.core.business.domain.common.MdRecordDO;
import org.escidoc.core.business.domain.om.component.ComponentDO;
import org.escidoc.core.domain.components.ComponentTypeTO;
import org.escidoc.core.domain.metadatarecords.MdRecordTypeTO;
import org.escidoc.core.domain.service.EntityMapperTranslator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

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

    public ComponentTypeTO Do2To(ComponentDO componentDo) {
        ComponentTypeTO componentTo = new ComponentTypeTO();
        return componentTo;
    }
}