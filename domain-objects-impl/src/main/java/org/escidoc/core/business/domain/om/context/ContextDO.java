package org.escidoc.core.business.domain.om.context;

import net.sf.oval.constraint.NotBlank;
import net.sf.oval.constraint.NotNull;

import org.escidoc.core.business.domain.base.AbstractBuilder;
import org.escidoc.core.business.domain.base.DomainObject;
import org.escidoc.core.business.domain.base.ID;
import org.escidoc.core.business.util.annotation.Validate;
import org.escidoc.core.business.util.aspect.ValidationProfile;

/**
 * @author Marko Voss (marko.voss@fiz-karlsruhe.de)
 * @author Michael Hoppe (michael.hoppe@fiz-karlsruhe.de)
 */
@Validate
public class ContextDO extends DomainObject {

    @NotNull(profiles = {ValidationProfile.EXISTS})
    private ID id;

    private ContextDO(Builder builder) {
        super(builder.validationProfile);
    }

    /**
     * @return the id
     */
    public ID getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(ID id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ContextDO that = (ContextDO) o;

        if (id != that.id) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
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
        sb.append("ContextDO");
        sb.append("{id=").append(id);
        sb.append('}');
        return sb;
    }

    public static class Builder extends AbstractBuilder {
        private ID id = null;

        public Builder(String validationProfile) {
            super(validationProfile);
        }

        public Builder id(ID id) {
            this.id = id;
            return this;
        }

        public ContextDO build() {
            return new ContextDO(this);
        }
        
    }
        
}