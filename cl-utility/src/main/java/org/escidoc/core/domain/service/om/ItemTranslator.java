/**
 * 
 */
package org.escidoc.core.domain.service.om;

import org.escidoc.core.business.domain.om.item.ItemDO;
import org.escidoc.core.domain.item.ItemTypeTO;
import org.escidoc.core.domain.service.EntityMapperTranslator;

/**
 * @author MIH
 *
 */
public class ItemTranslator extends EntityMapperTranslator<ItemTypeTO, ItemDO>
  {
    public ItemDO To2Do(ItemTypeTO value)
    {
      ItemDO itemDo = new ItemDO();
      return itemDo;
    }
   
    public ItemTypeTO Do2To(ItemDO value)
    {
      ItemTypeTO itemTo = new ItemTypeTO();
      return itemTo;  
    }
  }