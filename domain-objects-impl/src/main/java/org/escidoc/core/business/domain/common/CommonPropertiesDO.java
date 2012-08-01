package org.escidoc.core.business.domain.common;

import net.sf.oval.constraint.AssertFieldConstraints;

import org.escidoc.core.business.domain.base.DomainObject;
import org.escidoc.core.business.domain.base.ID;
import org.escidoc.core.business.domain.om.item.ItemPropertiesDO.Builder;
import org.joda.time.DateTime;

/**
 * @author Marko Voss (marko.voss@fiz-karlsruhe.de)
 */
public abstract class CommonPropertiesDO extends DomainObject {

    //@NotNull(profiles = {ValidationProfile.EXISTS})
    private DateTime creationDate;

    //@NotNull(profiles = {ValidationProfile.EXISTS})
    private ID createdBy;

    // @NotNull TODO: required or optional?
    // @Length TODO: specify max length?
    //@NotBlank
    private String name;

    // @NotNull TODO: required or optional?
    // @Length TODO: specify max length?
    //@NotBlank
    private String description;

    public CommonPropertiesDO(Builder builder) {
        this.creationDate = builder.creationDate;
        this.createdBy = builder.createdBy;
        this.name = builder.name;
        this.description = builder.description;
    }

    public void setCreationDate(@AssertFieldConstraints final DateTime creationDate) {
        this.creationDate = creationDate;
    }

    public void setCreatedBy(@AssertFieldConstraints final ID createdBy) {
        this.createdBy = createdBy;
    }

    public void setName(@AssertFieldConstraints final String name) {
        this.name = name;
    }

    public void setDescription(@AssertFieldConstraints final String description) {
        this.description = description;
    }

    @AssertFieldConstraints
    public DateTime getCreationDate() {
        return creationDate;
    }

    @AssertFieldConstraints
    public ID getCreatedBy() {
        return createdBy;
    }

    @AssertFieldConstraints
    public String getName() {
        return name;
    }

    @AssertFieldConstraints
    public String getDescription() {
        return description;
    }

    public abstract static class Builder {
        private String name = null;

        private String description = null;

        private ID createdBy = null;

        private DateTime creationDate = null;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder createdBy(ID createdBy) {
            this.createdBy = createdBy;
            return this;
        }

        public Builder creationDate(DateTime creationDate) {
            this.creationDate = creationDate;
            return this;
        }
        
    }
}
