/**
 * 
 */
package org.escidoc.core.domain.service.om;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;

import org.escidoc.core.business.domain.om.component.ComponentDO;
import org.escidoc.core.business.domain.om.component.ComponentsDO;
import org.escidoc.core.domain.components.ComponentTypeTO;
import org.escidoc.core.domain.components.ComponentsTypeTO;
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
@Service("domain.ComponentsTranslator")
public class ComponentsTranslator extends EntityMapperTranslator<ComponentsTypeTO, ComponentsDO> {

    @Autowired
    @Qualifier("domain.ComponentTranslator")
    private ComponentTranslator componentTranslator;
    
    public ComponentsDO To2Do(ComponentsTypeTO componentsTo, String validationProfile) {
        ComponentsDO.Builder componentsBuilder = new ComponentsDO.Builder(validationProfile);
        if (componentsTo.getComponent() != null && !componentsTo.getComponent().isEmpty()) {
            Set<ComponentDO> componentDos = new HashSet<ComponentDO>();
            for (ComponentTypeTO componentTo : componentsTo.getComponent()) {
                componentDos.add(componentTranslator.To2Do(componentTo, validationProfile));
            }
            componentsBuilder.components(componentDos);
        }
        ComponentsDO componentsDo = componentsBuilder.build();
        return componentsDo;
    }

    public ComponentsTypeTO Do2To(ComponentsDO componentsDo) throws SystemException {
        ComponentsTypeTO componentsTo = new ComponentsTypeTO();
        
        try {
            componentsTo.setBase(new URI(EscidocConfiguration.ESCIDOC_CORE_BASEURL));
            if (componentsDo.getOrigin() != null) {
                componentsTo.setHref(new URI(XmlUtility.getItemHref(componentsDo.getOrigin().getValue()) + "/components"));
            }
            //TODO: inherited doesnt exist in TO
            //componentsTo.setInherited(value);
            //TODO: integrate lmd in DO??
            //componentsTo.setLastModificationDate(componentsDo.get???);
            componentsTo.setTitle("available components");
            componentsTo.setType("simple");

            if (componentsDo.getComponents() != null && !componentsDo.getComponents().isEmpty()) {
                for (ComponentDO componentDo : componentsDo.getComponents()) {
                    componentsTo.getComponent().add(componentTranslator.Do2To(componentDo));
                }
            }
        }
        catch (URISyntaxException e) {
            throw new SystemException(e.getMessage(), e);
        }
        
        return componentsTo;
    }
}