/**
 * 
 */
package org.escidoc.core.domain.service.om;

import org.escidoc.core.business.domain.om.component.ContentDO;
import org.escidoc.core.business.domain.om.component.Storage;
import org.escidoc.core.domain.components.ContentTypeTO;
import org.escidoc.core.domain.service.EntityMapperTranslator;
import org.springframework.stereotype.Service;

/**
 * @author MIH
 * 
 */
@Service("domain.ContentTranslator")
public class ContentTranslator extends EntityMapperTranslator<ContentTypeTO, ContentDO> {
    public ContentDO To2Do(ContentTypeTO contentTo, String validationProfile) {
        ContentDO.Builder contentBuilder = new ContentDO.Builder(validationProfile);
        
        contentBuilder.location(contentTo.getHref());
        contentBuilder.storage(Storage.valueOf(contentTo.getStorage().value()));

        ContentDO contentDo = contentBuilder.build();
        return contentDo;
    }

    public ContentTypeTO Do2To(ContentDO contentDo) {
        ContentTypeTO contentTo = new ContentTypeTO();
        return contentTo;
    }
}