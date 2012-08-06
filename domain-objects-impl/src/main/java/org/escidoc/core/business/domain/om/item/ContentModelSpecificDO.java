package org.escidoc.core.business.domain.om.item;

import net.sf.oval.constraint.AssertFieldConstraints;
import net.sf.oval.constraint.NotBlank;
import net.sf.oval.constraint.NotNull;

import org.escidoc.core.business.domain.base.AbstractBuilder;
import org.escidoc.core.business.domain.base.DomainObject;
import org.escidoc.core.business.util.annotation.Validate;
import org.escidoc.core.utils.io.Stream;

/**
 * @author Michael Hoppe (michael.hoppe@fiz-karlsruhe.de)
 */
@Validate
public class ContentModelSpecificDO extends DomainObject {

    @NotNull
    private final Stream content;

    private Boolean inherited;

    private ContentModelSpecificDO(Builder builder) {
        super(builder.validationProfile);
        this.content = builder.content;
        this.inherited = builder.inherited;
    }

    public void setInherited(@AssertFieldConstraints final boolean inherited) {
        this.inherited = inherited;
    }

    @AssertFieldConstraints
    public Stream getContent() {
        return content;
    }

    @AssertFieldConstraints
    public boolean isInherited() {
        return inherited;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ContentModelSpecificDO that = (ContentModelSpecificDO) o;

        if (inherited != that.inherited) {
            return false;
        }
        if (!content.equals(that.content)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = content.hashCode();
        result = 31 * result + (inherited ? 1 : 0);
        return result;
    }

    @Override
    @NotNull
    @NotBlank
    public String toString() {
        return toStringBuilder().toString();
    }

    @NotNull
    public StringBuilder toStringBuilder() {
        final StringBuilder sb = new StringBuilder();
        sb.append("ContentModelSpecificDO");
        sb.append("{content=").append(content);
        sb.append(", inherited=").append(inherited);
        sb.append('}');
        return sb;
    }

    public abstract static class Builder extends AbstractBuilder {
        private Stream content = null;

        private Boolean inherited = null;

        public Builder(String validationProfile) {
            super(validationProfile);
        }

        public Builder content(Stream content) {
            this.content = content;
            return this;
        }

        public Builder inherited(Boolean inherited) {
            this.inherited = inherited;
            return this;
        }

        public ContentModelSpecificDO build() {
            return new ContentModelSpecificDO(this);
        }
        
    }
        
}