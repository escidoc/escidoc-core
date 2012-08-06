/**
 * 
 */
package org.escidoc.core.domain.service.om;

import org.escidoc.core.business.domain.base.ID;
import org.escidoc.core.business.domain.base.LockStatus;
import org.escidoc.core.business.domain.base.Pid;
import org.escidoc.core.business.domain.common.LockInfoDO;
import org.escidoc.core.business.domain.common.StatusInfoDO;
import org.escidoc.core.business.domain.om.item.ItemPropertiesDO;
import org.escidoc.core.business.domain.om.item.ItemStatus;
import org.escidoc.core.domain.item.ItemPropertiesTypeTO;
import org.escidoc.core.domain.service.EntityMapperTranslator;
import org.springframework.stereotype.Service;

import de.escidoc.core.common.util.xml.XmlUtility;

/**
 * @author MIH
 * 
 */
@Service("domain.ItemPropertiesTranslator")
public class ItemPropertiesTranslator extends EntityMapperTranslator<ItemPropertiesTypeTO, ItemPropertiesDO> {
    public ItemPropertiesDO To2Do(ItemPropertiesTypeTO itemPropertiesTo, String validationProfile) {
        ItemPropertiesDO.Builder itemPropertiesBuilder = new ItemPropertiesDO.Builder(validationProfile);

        itemPropertiesBuilder.contentModel(new ID(XmlUtility.getIdFromURI(itemPropertiesTo
            .getContentModel().getHref().toString())));
        itemPropertiesBuilder.context(new ID(XmlUtility
            .getIdFromURI(itemPropertiesTo.getContext().getHref().toString())));
        itemPropertiesBuilder.createdBy(new ID(XmlUtility.getIdFromURI(itemPropertiesTo
            .getCreatedBy().getHref().toString())));
        itemPropertiesBuilder.creationDate(itemPropertiesTo.getCreationDate());
        itemPropertiesBuilder.objectPid(new Pid(itemPropertiesTo.getPid()));

        if (itemPropertiesTo.getLockStatus() != null) {
            LockInfoDO.Builder lockInfoBuilder = new LockInfoDO.Builder(validationProfile);
            LockInfoDO lockInfoDo =
                lockInfoBuilder.owner(
                    new ID(XmlUtility.getIdFromURI(itemPropertiesTo.getLockOwner().getHref().toString()))).status(
                    LockStatus.valueOf(itemPropertiesTo.getLockStatus().value())).timestamp(
                    itemPropertiesTo.getLockDate()).build();
            itemPropertiesBuilder.lockInfo(lockInfoDo);
        }

        if (itemPropertiesTo.getVersion() != null) {
            itemPropertiesBuilder.modifiedBy(new ID(XmlUtility.getIdFromURI(itemPropertiesTo
                .getVersion().getModifiedBy().getHref().toString())));
            itemPropertiesBuilder.timestamp(itemPropertiesTo.getVersion().getDate());
            itemPropertiesBuilder.versionNumber(itemPropertiesTo.getVersion().getNumber());
            itemPropertiesBuilder.versionPid(new Pid(itemPropertiesTo.getVersion().getPid()));
            StatusInfoDO.Builder<ItemStatus> statusInfoBuilder =
                new StatusInfoDO.Builder<ItemStatus>(validationProfile);
            StatusInfoDO statusInfoDo =
                statusInfoBuilder
                    .objectStatus(ItemStatus.valueOf(itemPropertiesTo.getVersion().getStatus())).objectStatusComment(
                        itemPropertiesTo.getVersion().getComment()).build();
            itemPropertiesBuilder.versionStatusInfo(statusInfoDo);
        }

        if (itemPropertiesTo.getPublicStatus() != null) {
            StatusInfoDO.Builder<ItemStatus> statusInfoBuilder =
                new StatusInfoDO.Builder<ItemStatus>(validationProfile);
            StatusInfoDO statusInfoDo =
                statusInfoBuilder
                    .objectStatus(ItemStatus.valueOf(itemPropertiesTo.getPublicStatus().value())).objectStatusComment(
                        itemPropertiesTo.getPublicStatusComment()).build();
            itemPropertiesBuilder.statusInfo(statusInfoDo);
        }

        ItemPropertiesDO itemPropertiesDo = itemPropertiesBuilder.build();
        return itemPropertiesDo;
    }

    public ItemPropertiesTypeTO Do2To(ItemPropertiesDO itemPropertiesDo) {
        ItemPropertiesTypeTO itemPropertiesTo = new ItemPropertiesTypeTO();
        return itemPropertiesTo;
    }

}