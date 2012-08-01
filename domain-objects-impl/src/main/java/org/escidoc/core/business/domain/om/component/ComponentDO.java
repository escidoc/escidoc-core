package org.escidoc.core.business.domain.om.component;

import java.util.Set;

import net.sf.oval.constraint.AssertFieldConstraints;
import net.sf.oval.constraint.NotBlank;
import net.sf.oval.constraint.NotNull;
import net.sf.oval.guard.Guarded;

import org.escidoc.core.business.domain.base.DomainObject;
import org.escidoc.core.business.domain.base.ID;
import org.escidoc.core.business.domain.common.MdRecordDO;

/**
 * @author Michael Hoppe (michael.hoppe@fiz-karlsruhe.de)
 */
@Guarded(checkInvariants = true)
public class ComponentDO extends DomainObject {
    @NotNull
    private Boolean inherited = false;

    private ID id;
    
    @NotNull
    private ComponentPropertiesDO properties;

    @NotNull
    private ContentDO content;

    @NotNull
    private Set<MdRecordDO> mdRecords;
    
    public ComponentDO(Builder builder) {
        this.inherited = builder.inherited;
        this.id = builder.id;
        this.properties = builder.properties;
        this.content = builder.content;
        this.mdRecords = builder.mdRecords;
    }

    /**
     * @return the inherited
     */
    @AssertFieldConstraints
    public Boolean getInherited() {
        return inherited;
    }

    /**
     * @param inherited the inherited to set
     */
    public void setInherited(@AssertFieldConstraints Boolean inherited) {
        this.inherited = inherited;
    }

    /**
     * @return the id
     */
    @AssertFieldConstraints
    public ID getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(@AssertFieldConstraints ID id) {
        this.id = id;
    }

    /**
     * @return the properties
     */
    @AssertFieldConstraints
    public ComponentPropertiesDO getProperties() {
        return properties;
    }

    /**
     * @param properties the properties to set
     */
    public void setProperties(@AssertFieldConstraints ComponentPropertiesDO properties) {
        this.properties = properties;
    }

    /**
     * @return the content
     */
    @AssertFieldConstraints
    public ContentDO getContent() {
        return content;
    }

    /**
     * @param content the content to set
     */
    public void setContent(@AssertFieldConstraints ContentDO content) {
        this.content = content;
    }

    /**
     * @return the mdRecords
     */
    @AssertFieldConstraints
    public Set<MdRecordDO> getMdRecords() {
        return mdRecords;
    }

    /**
     * @param mdRecords the mdRecords to set
     */
    public void setMdRecords(@AssertFieldConstraints Set<MdRecordDO> mdRecords) {
        this.mdRecords = mdRecords;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ComponentDO componentDO = (ComponentDO) o;

        if (!inherited.equals(componentDO.inherited)) {
            return false;
        }
        if (!id.equals(componentDO.id)) {
            return false;
        }
        if (!properties.equals(componentDO.properties)) {
            return false;
        }
        if (!content.equals(componentDO.content)) {
            return false;
        }
        if (!mdRecords.equals(componentDO.mdRecords)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = inherited.hashCode();
        result = 31 * result + id.hashCode();
        result = 31 * result + properties.hashCode();
        result = 31 * result + content.hashCode();
        result = 31 * result + mdRecords.hashCode();
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
        sb.append("ComponentDO");
        sb.append("{inherited=").append(inherited);
        sb.append(", id=").append(id);
        sb.append(", properties=").append(properties);
        sb.append(", content=").append(content);
        sb.append(", mdRecords=").append(mdRecords);
        sb.append('}');
        return sb;
    }
    
    public static class Builder {
        private Boolean inherited = false;

        private ID id = null;
        
        private ComponentPropertiesDO properties = null;

        private ContentDO content = null;

        private Set<MdRecordDO> mdRecords = null;

        public Builder inherited(Boolean inherited) {
            this.inherited = inherited;
            return this;
        }

        public Builder id(ID id) {
            this.id = id;
            return this;
        }

        public Builder properties(ComponentPropertiesDO properties) {
            this.properties = properties;
            return this;
        }

        public Builder content(ContentDO content) {
            this.content = content;
            return this;
        }

        public Builder mdRecords(Set<MdRecordDO> mdRecords) {
            this.mdRecords = mdRecords;
            return this;
        }

        public ComponentDO build() {
            return new ComponentDO(this);
        }
        
    }
}
