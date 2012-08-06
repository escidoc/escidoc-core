/**
 * 
 */
package org.escidoc.core.domain.service.om;

import org.escidoc.core.business.domain.om.item.RelationDO;
import org.escidoc.core.domain.relations.RelationTypeTO;
import org.escidoc.core.domain.service.EntityMapperTranslator;
import org.springframework.stereotype.Service;

import de.escidoc.core.common.exceptions.system.SystemException;

/**
 * @author MIH
 * 
 *         Translates Item-Relation from/to DO/TO.
 * 
 */
@Service("domain.RelationTranslator")
public class RelationTranslator extends EntityMapperTranslator<RelationTypeTO, RelationDO> {

    /**
     * Translates Item-Relation from TO to DO.
     * 
     * @param relationTo
     *            RelationTypeTO
     * @param validationProfile
     *            for oval-validation @see ValidationProfile.java
     * @return RelationDO DO
     * @throws SystemException
     *             e
     * 
     */
    public RelationDO To2Do(RelationTypeTO relationTo, String validationProfile) throws SystemException {
        RelationDO.Builder relationBuilder = new RelationDO.Builder(validationProfile);

        relationBuilder.predicate(relationTo.getPredicate());
        relationBuilder.resource(relationTo.getHref());

        RelationDO relationDo = relationBuilder.build();
        return relationDo;
    }

    /**
     * Translates Item-Relation from DO to TO.
     * 
     * @param relationDo
     *            RelationDO
     * @return RelationTypeTO TO
     * @throws SystemException
     *             e
     * 
     */
    public RelationTypeTO Do2To(RelationDO relationDo) throws SystemException {
        RelationTypeTO relationTo = new RelationTypeTO();

        relationTo.setHref(relationDo.getResource());
        relationTo.setPredicate(relationDo.getPredicate());
        relationTo.setTitle("relations of item");
        relationTo.setType("simple");

        return relationTo;
    }
}