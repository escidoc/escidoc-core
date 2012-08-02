/**
 * 
 */
package org.escidoc.core.domain.service.om;

import org.escidoc.core.business.domain.base.ID;
import org.escidoc.core.business.domain.om.item.ItemDO;
import org.escidoc.core.business.util.aspect.ValidationProfile;
import org.escidoc.core.domain.item.ItemTypeTO;
import org.escidoc.core.domain.service.EntityMapperTranslator;
import org.springframework.stereotype.Service;

/**
 * @author MIH
 *
 */
@Service("domain.ItemTranslator")
public class ItemTranslator extends EntityMapperTranslator<ItemTypeTO, ItemDO>
  {
    public ItemDO To2Do(ItemTypeTO value)
    {
      ItemDO.Builder itemBuilder = new ItemDO.Builder(ValidationProfile.EXISTS);
      ItemDO itemDo = itemBuilder.id(new ID("1")).build();
      return itemDo;
    }
   
    public ItemTypeTO Do2To(ItemDO value)
    {
      ItemTypeTO itemTo = new ItemTypeTO();
      return itemTo;  
    }
  }