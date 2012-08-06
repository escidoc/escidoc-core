/**
 * 
 */
package org.escidoc.core.domain.service.om;

import org.escidoc.core.business.domain.om.component.ContentDO;
import org.escidoc.core.business.domain.om.component.Storage;
import org.escidoc.core.domain.common.StorageTypeTO;
import org.escidoc.core.domain.components.ContentTypeTO;
import org.escidoc.core.domain.service.EntityMapperTranslator;
import org.springframework.stereotype.Service;

import de.escidoc.core.common.exceptions.system.SystemException;

/**
 * @author MIH
 * 
 *         Translates Item-Component Content from/to DO/TO.
 * 
 */
@Service("domain.ContentTranslator")
public class ContentTranslator extends EntityMapperTranslator<ContentTypeTO, ContentDO> {

    /**
     * Translates Item-Component Content from TO to DO.
     * 
     * @param contentTo
     *            ContentTypeTO
     * @param validationProfile
     *            for oval-validation @see ValidationProfile.java
     * @return ContentDO DO
     * @throws SystemException
     *             e
     * 
     */
    public ContentDO To2Do(ContentTypeTO contentTo, String validationProfile) throws SystemException {
        ContentDO.Builder contentBuilder = new ContentDO.Builder(validationProfile);

        contentBuilder.location(contentTo.getHref());
        contentBuilder.storage(Storage.valueOf(contentTo.getStorage().value()));

        ContentDO contentDo = contentBuilder.build();
        return contentDo;
    }

    /**
     * Translates Item-Component Content from DO to TO.
     * 
     * @param contentDo
     *            ContentDO
     * @return ContentTypeTO TO
     * @throws SystemException
     *             e
     * 
     */
    public ContentTypeTO Do2To(ContentDO contentDo) throws SystemException {
        ContentTypeTO contentTo = new ContentTypeTO();

        contentTo.setHref(contentDo.getLocation());
        contentTo.setStorage(StorageTypeTO.valueOf(contentDo.getStorage().toString()));
        contentTo.setTitle("component content");
        contentTo.setType("simple");

        return contentTo;
    }
}