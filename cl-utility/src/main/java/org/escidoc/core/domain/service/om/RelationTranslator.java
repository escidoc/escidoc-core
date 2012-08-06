/**
 * 
 */
package org.escidoc.core.domain.service.om;

import org.escidoc.core.business.domain.om.item.RelationDO;
import org.escidoc.core.domain.relations.RelationTypeTO;
import org.escidoc.core.domain.service.EntityMapperTranslator;
import org.springframework.stereotype.Service;

/**
 * @author MIH
 * 
 */
@Service("domain.RelationTranslator")
public class RelationTranslator extends EntityMapperTranslator<RelationTypeTO, RelationDO> {
    public RelationDO To2Do(RelationTypeTO relationTo, String validationProfile) {
        RelationDO.Builder relationBuilder = new RelationDO.Builder(validationProfile);
        
        relationBuilder.predicate(relationTo.getPredicate());
        relationBuilder.resource(relationTo.getHref());
        
        RelationDO relationDo = relationBuilder.build();
        return relationDo;
    }

    public RelationTypeTO Do2To(RelationDO relationDo) {
        RelationTypeTO relationTo = new RelationTypeTO();
        return relationTo;
    }
}