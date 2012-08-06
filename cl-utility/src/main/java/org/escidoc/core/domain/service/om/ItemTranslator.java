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
import org.escidoc.core.business.domain.om.item.ItemDO;
import org.escidoc.core.business.domain.om.item.RelationDO;
import org.escidoc.core.domain.item.ItemTypeTO;
import org.escidoc.core.domain.metadatarecords.MdRecordTypeTO;
import org.escidoc.core.domain.metadatarecords.MdRecordsTypeTO;
import org.escidoc.core.domain.relations.RelationTypeTO;
import org.escidoc.core.domain.relations.RelationsTypeTO;
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
 *         Translates Item from/to DO/TO.
 * 
 */
@Service("domain.ItemTranslator")
public class ItemTranslator extends EntityMapperTranslator<ItemTypeTO, ItemDO> {

    @Autowired
    @Qualifier("domain.ComponentsTranslator")
    private ComponentsTranslator componentsTranslator;

    @Autowired
    @Qualifier("domain.MdRecordTranslator")
    private MdRecordTranslator mdRecordTranslator;

    @Autowired
    @Qualifier("domain.ItemPropertiesTranslator")
    private ItemPropertiesTranslator itemPropertiesTranslator;

    @Autowired
    @Qualifier("domain.RelationTranslator")
    private RelationTranslator relationTranslator;

    /**
     * Translates Item from TO to DO.
     * 
     * @param itemTo
     *            ItemTypeTO
     * @param validationProfile
     *            for oval-validation @see ValidationProfile.java
     * @return ItemDO DO
     * @throws SystemException
     *             e
     * 
     */
    public ItemDO To2Do(ItemTypeTO itemTo, String validationProfile) throws SystemException {
        ItemDO.Builder itemBuilder = new ItemDO.Builder(validationProfile);

        if (itemTo.getHref() != null) {
            itemBuilder.id(new ID(XmlUtility.getIdFromURI(itemTo.getHref().toString())));
        }

        if (itemTo.getComponents() != null && itemTo.getComponents().getComponent() != null
            && !itemTo.getComponents().getComponent().isEmpty()) {
            itemBuilder.components(componentsTranslator.To2Do(itemTo.getComponents(), validationProfile));
        }

        if (itemTo.getMdRecords() != null && itemTo.getMdRecords().getMdRecord() != null
            && !itemTo.getMdRecords().getMdRecord().isEmpty()) {
            Set<MdRecordDO> mdRecordDos = new HashSet<MdRecordDO>();
            for (MdRecordTypeTO mdRecordTo : itemTo.getMdRecords().getMdRecord()) {
                mdRecordDos.add(mdRecordTranslator.To2Do(mdRecordTo, validationProfile));
            }
            itemBuilder.mdRecords(mdRecordDos);
        }

        if (itemTo.getProperties() != null) {
            itemBuilder.properties(itemPropertiesTranslator.To2Do(itemTo.getProperties(), validationProfile));
        }

        if (itemTo.getRelations() != null && itemTo.getRelations().getRelation() != null
            && !itemTo.getRelations().getRelation().isEmpty()) {
            Set<RelationDO> relationDos = new HashSet<RelationDO>();
            for (RelationTypeTO relationTo : itemTo.getRelations().getRelation()) {
                relationDos.add(relationTranslator.To2Do(relationTo, validationProfile));
            }
            itemBuilder.relations(relationDos);
        }

        ItemDO itemDo = itemBuilder.build();
        return itemDo;
    }

    /**
     * Translates Item from DO to TO.
     * 
     * @param itemDo
     *            ItemDO
     * @return ItemTypeTO TO
     * @throws SystemException
     *             e
     * 
     */
    public ItemTypeTO Do2To(ItemDO itemDo) throws SystemException {
        ItemTypeTO itemTo = new ItemTypeTO();
        try {
            itemTo.setBase(new URI(EscidocConfiguration.ESCIDOC_CORE_BASEURL));
            itemTo.setTitle("item-title");
            itemTo.setType("simple");

            if (itemDo.getId() != null) {
                itemTo.setHref(new URI(XmlUtility.getItemHref(itemDo.getId().getValue())));
            }

            if (itemDo.getComponents() != null && itemDo.getComponents().getComponents() != null
                && !itemDo.getComponents().getComponents().isEmpty()) {
                itemTo.setComponents(componentsTranslator.Do2To(itemDo.getComponents()));
            }

            if (itemDo.getMdRecords() != null && !itemDo.getMdRecords().isEmpty()) {
                MdRecordsTypeTO mdRecordsTo = new MdRecordsTypeTO();
                mdRecordsTo.setHref(new URI(XmlUtility.getItemHref(itemDo.getId().getValue()) + "/md-records"));
                mdRecordsTo.setTitle("Available Metadata");
                mdRecordsTo.setType("simple");
                for (MdRecordDO mdRecordDo : itemDo.getMdRecords()) {
                    mdRecordsTo.getMdRecord().add(mdRecordTranslator.Do2To(mdRecordDo));
                }
            }

            if (itemDo.getProperties() != null) {
                itemTo.setProperties(itemPropertiesTranslator.Do2To(itemDo.getProperties()));
            }

            if (itemDo.getRelations() != null && !itemDo.getRelations().isEmpty()) {
                RelationsTypeTO relationsTo = new RelationsTypeTO();
                relationsTo.setHref(new URI(XmlUtility.getItemHref(itemDo.getId().getValue()) + "/relations"));
                relationsTo.setTitle("Available Relations");
                relationsTo.setType("simple");
                for (RelationDO relationDo : itemDo.getRelations()) {
                    relationsTo.getRelation().add(relationTranslator.Do2To(relationDo));
                }
            }
        }
        catch (URISyntaxException e) {
            throw new SystemException(e.getMessage(), e);
        }

        return itemTo;
    }
}