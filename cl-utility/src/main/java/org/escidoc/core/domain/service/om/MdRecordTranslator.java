/**
 * 
 */
package org.escidoc.core.domain.service.om;

import java.net.URI;
import java.net.URISyntaxException;

import org.escidoc.core.business.domain.common.MdRecordDO;
import org.escidoc.core.domain.metadatarecords.MdRecordTypeTO;
import org.escidoc.core.domain.service.EntityMapperTranslator;
import org.escidoc.core.utils.xml.StreamElement;
import org.springframework.stereotype.Service;

import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.util.configuration.EscidocConfiguration;
import de.escidoc.core.common.util.xml.XmlUtility;

/**
 * @author MIH
 * 
 *         Translates MD-Record from/to DO/TO.
 * 
 */
@Service("domain.MdRecordTranslator")
public class MdRecordTranslator extends EntityMapperTranslator<MdRecordTypeTO, MdRecordDO> {

    /**
     * Translates MD-Record from TO to DO.
     * 
     * @param mdRecordTo
     *            MdRecordTypeTO
     * @param validationProfile
     *            for oval-validation @see ValidationProfile.java
     * @return MdRecordDO DO
     * @throws SystemException
     *             e
     * 
     */
    public MdRecordDO To2Do(MdRecordTypeTO mdRecordTo, String validationProfile) throws SystemException {
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

    /**
     * Translates MD-Record from DO to TO.
     * 
     * @param mdRecordDo
     *            MdRecordDO
     * @return MdRecordTypeTO TO
     * @throws SystemException
     *             e
     * 
     */
    public MdRecordTypeTO Do2To(MdRecordDO mdRecordDo) throws SystemException {
        MdRecordTypeTO mdRecordTo = new MdRecordTypeTO();

        try {
            mdRecordTo.setAny(new StreamElement(mdRecordDo.getContent()));
            mdRecordTo.setBase(new URI(EscidocConfiguration.ESCIDOC_CORE_BASEURL));
            if (mdRecordDo.getOrigin() != null) {
                mdRecordTo.setHref(new URI(XmlUtility.getItemHref(mdRecordDo.getOrigin().getValue())));
            }
            mdRecordTo.setInherited(mdRecordDo.getInherited());
            // TODO: lmd in DO???
            // mdRecordTo.setLastModificationDate(mdRecordDo.get???);
            mdRecordTo.setMdType(mdRecordDo.getMdType());
            mdRecordTo.setName(mdRecordDo.getName());
            mdRecordTo.setSchema(mdRecordDo.getSchema());
            mdRecordTo.setTitle("metadata of item");
            mdRecordTo.setType("simple");
        }
        catch (URISyntaxException e) {
            throw new SystemException(e.getMessage(), e);
        }

        return mdRecordTo;
    }
}