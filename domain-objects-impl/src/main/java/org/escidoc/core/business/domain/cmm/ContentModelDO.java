package org.escidoc.core.business.domain.cmm;

import org.escidoc.core.business.domain.base.AbstractBuilder;
import org.escidoc.core.business.domain.base.DomainObject;
import org.escidoc.core.business.domain.om.item.ContentModelSpecificDO;
import org.escidoc.core.business.util.annotation.Validate;

/**
 * @author Marko Voss (marko.voss@fiz-karlsruhe.de)
 */
@Validate
public class ContentModelDO extends DomainObject {

    public ContentModelDO(Builder builder) {
        super(builder.validationProfile);
    }

    public abstract static class Builder extends AbstractBuilder {
      public Builder(String validationProfile) {
          super(validationProfile);
      }

      public ContentModelDO build() {
          return new ContentModelDO(this);
      }
      
    }
      
}