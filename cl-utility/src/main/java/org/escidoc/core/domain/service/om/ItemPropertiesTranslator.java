/**
 * 
 */
package org.escidoc.core.domain.service.om;

import java.net.URI;
import java.net.URISyntaxException;

import org.escidoc.core.business.domain.base.ID;
import org.escidoc.core.business.domain.base.LockStatus;
import org.escidoc.core.business.domain.base.Pid;
import org.escidoc.core.business.domain.common.LockInfoDO;
import org.escidoc.core.business.domain.common.StatusInfoDO;
import org.escidoc.core.business.domain.om.item.ItemPropertiesDO;
import org.escidoc.core.business.domain.om.item.ItemStatus;
import org.escidoc.core.domain.common.LinkForCreateTO;
import org.escidoc.core.domain.common.LockStatusTypeTO;
import org.escidoc.core.domain.common.ReadOnlyLinkTO;
import org.escidoc.core.domain.common.StatusTypeTO;
import org.escidoc.core.domain.item.ItemPropertiesTypeTO;
import org.escidoc.core.domain.properties.VersionTypeTO;
import org.escidoc.core.domain.service.EntityMapperTranslator;
import org.springframework.stereotype.Service;

import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.util.configuration.EscidocConfiguration;
import de.escidoc.core.common.util.xml.XmlUtility;

/**
 * @author MIH
 * 
 *         Translates Item Properties from/to DO/TO.
 * 
 */
@Service("domain.ItemPropertiesTranslator")
public class ItemPropertiesTranslator extends EntityMapperTranslator<ItemPropertiesTypeTO, ItemPropertiesDO> {

    /**
     * Translates Item-Properties from TO to DO.
     * 
     * @param itemPropertiesTo
     *            ItemPropertiesTypeTO
     * @param validationProfile
     *            for oval-validation @see ValidationProfile.java
     * @return ItemPropertiesDO DO
     * @throws SystemException
     *             e
     * 
     */
    public ItemPropertiesDO To2Do(ItemPropertiesTypeTO itemPropertiesTo, String validationProfile)
        throws SystemException {
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
                lockInfoBuilder
                    .owner(new ID(XmlUtility.getIdFromURI(itemPropertiesTo.getLockOwner().getHref().toString())))
                    .status(LockStatus.valueOf(itemPropertiesTo.getLockStatus().value()))
                    .timestamp(itemPropertiesTo.getLockDate()).build();
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
                    .objectStatus(ItemStatus.valueOf(itemPropertiesTo.getVersion().getStatus()))
                    .objectStatusComment(itemPropertiesTo.getVersion().getComment()).build();
            itemPropertiesBuilder.versionStatusInfo(statusInfoDo);
        }

        if (itemPropertiesTo.getPublicStatus() != null) {
            StatusInfoDO.Builder<ItemStatus> statusInfoBuilder =
                new StatusInfoDO.Builder<ItemStatus>(validationProfile);
            StatusInfoDO statusInfoDo =
                statusInfoBuilder
                    .objectStatus(ItemStatus.valueOf(itemPropertiesTo.getPublicStatus().value()))
                    .objectStatusComment(itemPropertiesTo.getPublicStatusComment()).build();
            itemPropertiesBuilder.statusInfo(statusInfoDo);
        }

        ItemPropertiesDO itemPropertiesDo = itemPropertiesBuilder.build();
        return itemPropertiesDo;
    }

    /**
     * Translates Item-Properties from DO to TO.
     * 
     * @param itemPropertiesDo
     *            ItemPropertiesDO
     * @return ItemPropertiesTypeTO TO
     * @throws SystemException
     *             e
     * 
     */
    public ItemPropertiesTypeTO Do2To(ItemPropertiesDO itemPropertiesDo) throws SystemException {
        ItemPropertiesTypeTO itemPropertiesTo = new ItemPropertiesTypeTO();
        try {
            itemPropertiesTo.setBase(new URI(EscidocConfiguration.ESCIDOC_CORE_BASEURL));

            if (itemPropertiesDo.getContentModel() != null) {
                LinkForCreateTO contentModelLink = new LinkForCreateTO();
                contentModelLink.setHref(new URI(XmlUtility.getContentModelHref(itemPropertiesDo
                    .getContentModel().getValue())));
                // TODO: get title of content-model
                contentModelLink.setTitle("content-model of item");
                contentModelLink.setType("simple");
                itemPropertiesTo.setContentModel(contentModelLink);
            }

            // TODO: content-model specific in DO?
            // itemPropertiesTo.setContentModelSpecific(itemPropertiesDo.get???);

            if (itemPropertiesDo.getContext() != null) {
                LinkForCreateTO contextLink = new LinkForCreateTO();
                contextLink.setHref(new URI(XmlUtility.getContextHref(itemPropertiesDo.getContext().getValue())));
                // TODO: get title of context
                contextLink.setTitle("context of item");
                contextLink.setType("simple");
                itemPropertiesTo.setContext(contextLink);
            }

            if (itemPropertiesDo.getCreatedBy() != null) {
                ReadOnlyLinkTO createdByLink = new ReadOnlyLinkTO();
                createdByLink
                    .setHref(new URI(XmlUtility.getUserAccountHref(itemPropertiesDo.getCreatedBy().getValue())));
                // TODO: get title of user-account
                createdByLink.setTitle("creator of item");
                createdByLink.setType("simple");
                itemPropertiesTo.setCreatedBy(createdByLink);
            }

            itemPropertiesTo.setCreationDate(itemPropertiesDo.getCreationDate());
            itemPropertiesTo.setHref(new URI(XmlUtility.getItemHref(itemPropertiesDo.getOrigin().getValue())));
            itemPropertiesTo.setLastModificationDate(itemPropertiesDo.getTimestamp());

            // TODO: set latest version + latest release as links in TO
            // itemPropertiesTo.setLatestRelease(latestReleaseTo);
            // itemPropertiesTo.setLatestVersion(value);

            if (itemPropertiesDo.getLockInfo() != null) {
                itemPropertiesTo.setLockDate(itemPropertiesDo.getLockInfo().getTimestamp());
                if (itemPropertiesDo.getLockInfo().getOwner() != null) {
                    ReadOnlyLinkTO lockOwnerLink = new ReadOnlyLinkTO();
                    lockOwnerLink.setHref(new URI(XmlUtility.getUserAccountHref(itemPropertiesDo
                        .getLockInfo().getOwner().getValue())));
                    // TODO: get title of user-account
                    lockOwnerLink.setTitle("owner of lock");
                    lockOwnerLink.setType("simple");
                    itemPropertiesTo.setLockOwner(lockOwnerLink);
                    itemPropertiesTo.setLockStatus(LockStatusTypeTO.valueOf(itemPropertiesDo
                        .getLockInfo().getStatus().toString()));
                }
            }

            if (itemPropertiesDo.getOrigin() != null) {
                LinkForCreateTO originLink = new LinkForCreateTO();
                originLink.setHref(new URI(XmlUtility.getItemHref(itemPropertiesDo.getOrigin().getValue())));
                // TODO: get title of user-account
                originLink.setTitle("origin");
                originLink.setType("simple");
                itemPropertiesTo.setOrigin(originLink);
            }

            if (itemPropertiesDo.getObjectPid() != null) {
                itemPropertiesTo.setPid(itemPropertiesDo.getObjectPid().getValue());
            }

            if (itemPropertiesDo.getStatusInfo() != null && itemPropertiesDo.getStatusInfo().getObjectStatus() != null) {
                itemPropertiesTo.setPublicStatus(StatusTypeTO.valueOf(itemPropertiesDo
                    .getStatusInfo().getObjectStatus().toString()));
                itemPropertiesTo.setPublicStatusComment(itemPropertiesDo.getStatusInfo().getObjectStatusComment());
            }
            // TODO: get Title of item
            itemPropertiesTo.setTitle("item");
            itemPropertiesTo.setType("simple");

            VersionTypeTO versionTo = new VersionTypeTO();
            versionTo.setComment(itemPropertiesDo.getVersionStatusInfo().getObjectStatusComment());
            versionTo.setDate(itemPropertiesDo.getTimestamp());
            if (itemPropertiesDo.getOrigin() != null && itemPropertiesDo.getVersionNumber() != null) {
                versionTo.setHref(new URI(XmlUtility.getItemHref(itemPropertiesDo.getOrigin().getValue()) + ":"
                    + itemPropertiesDo.getVersionNumber()));
            }

            if (itemPropertiesDo.getModifiedBy() != null) {
                ReadOnlyLinkTO modifierLink = new ReadOnlyLinkTO();
                modifierLink
                    .setHref(new URI(XmlUtility.getUserAccountHref(itemPropertiesDo.getModifiedBy().getValue())));
                // TODO: get title of user-account
                modifierLink.setTitle("modifier of version");
                modifierLink.setType("simple");
                versionTo.setModifiedBy(modifierLink);
            }

            versionTo.setNumber(itemPropertiesDo.getVersionNumber());
            versionTo.setPid(itemPropertiesDo.getVersionPid().getValue());
            if (itemPropertiesDo.getVersionStatusInfo() != null
                && itemPropertiesDo.getVersionStatusInfo().getObjectStatus() != null) {
                versionTo.setStatus(itemPropertiesDo.getVersionStatusInfo().getObjectStatus().toString());
            }
            versionTo.setTitle("version info");
            versionTo.setType("simple");
            itemPropertiesTo.setVersion(versionTo);
        }
        catch (URISyntaxException e) {
            throw new SystemException(e.getMessage(), e);
        }

        return itemPropertiesTo;
    }

}