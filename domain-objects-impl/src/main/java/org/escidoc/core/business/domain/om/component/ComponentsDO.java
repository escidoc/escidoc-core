package org.escidoc.core.business.domain.om.component;

import java.util.Set;

import net.sf.oval.constraint.AssertFieldConstraints;
import net.sf.oval.constraint.NotBlank;
import net.sf.oval.constraint.NotNull;

import org.escidoc.core.business.domain.base.AbstractBuilder;
import org.escidoc.core.business.domain.base.DomainObject;
import org.escidoc.core.business.util.annotation.Validate;

/**
 * @author Michael Hoppe (michael.hoppe@fiz-karlsruhe.de)
 */
@Validate
public class ComponentsDO extends DomainObject {
    @NotNull
    private Boolean inherited = false;
    
    @NotNull
    private Set<ComponentDO> components;
    
    public ComponentsDO(Builder builder) {
        super(builder.validationProfile);
        this.inherited = builder.inherited;
        this.components = builder.components;
    }

    /**
     * @return the inherited
     */
    @AssertFieldConstraints
    public boolean isInherited() {
        return inherited;
    }

    /**
     * @param inherited the inherited to set
     */
    public void setInherited(@AssertFieldConstraints boolean inherited) {
        this.inherited = inherited;
    }

    /**
     * @return the components
     */
    @AssertFieldConstraints
    public Set<ComponentDO> getComponents() {
        return components;
    }

    /**
     * @param components the components to set
     */
    public void setComponents(@AssertFieldConstraints Set<ComponentDO> components) {
        this.components = components;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ComponentsDO componentsDO = (ComponentsDO) o;

        if (!components.equals(componentsDO.components)) {
            return false;
        }
        if (!inherited.equals(componentsDO.inherited)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = inherited.hashCode();
        result = 31 * result + components.hashCode();
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
        sb.append("ComponentsDO");
        sb.append("{inherited=").append(inherited);
        sb.append(", components=").append(components);
        sb.append('}');
        return sb;
    }
    
    public static class Builder extends AbstractBuilder {
        private Boolean inherited = false;

        private Set<ComponentDO> components = null;

        public Builder(String validationProfile) {
            super(validationProfile);
        }

        public Builder inherited(Boolean inherited) {
            this.inherited = inherited;
            return this;
        }

        public Builder components(Set<ComponentDO> components) {
            this.components = components;
            return this;
        }

        public ComponentsDO build() {
            return new ComponentsDO(this);
        }
        
    }
}
