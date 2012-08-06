/**
 * 
 */
package org.escidoc.core.domain.service.om;

import org.escidoc.core.business.domain.common.MdRecordDO;
import org.escidoc.core.domain.metadatarecords.MdRecordTypeTO;
import org.escidoc.core.domain.service.EntityMapperTranslator;
import org.springframework.stereotype.Service;

/**
 * @author MIH
 *
 */
@Service("domain.MdRecordTranslator")
public class MdRecordTranslator extends EntityMapperTranslator<MdRecordTypeTO, MdRecordDO>
  {
    public MdRecordDO To2Do(MdRecordTypeTO mdRecordTo, String validationProfile) {
        MdRecordDO.Builder mdRecordBuilder = new MdRecordDO.Builder(validationProfile);
        
        if (mdRecordTo.getAny() != null) {
            mdRecordBuilder.content(mdRecordTo.getAny().getStream());
        }
        mdRecordBuilder.mdType(mdRecordTo.getMdType());
        mdRecordBuilder.name(mdRecordTo.getName());
        mdRecordBuilder.schema(mdRecordTo.getSchema());
        
        MdRecordDO mdRecordDo = mdRecordBuilder.build();
        return mdRecordDo;
    }
   
    public MdRecordTypeTO Do2To(MdRecordDO mdRecordDo) {
        MdRecordTypeTO mdRecordTo = new MdRecordTypeTO();
        return mdRecordTo;
    }
  }