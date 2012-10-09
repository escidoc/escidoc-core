package org.escidoc.core.persistence.impl.fedora.resource;

import org.apache.commons.lang.builder.ToStringBuilder;

import de.escidoc.core.common.business.fedora.resources.ResourceType;

public class Target {
    private String id;

    private ResourceType type;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setType(ResourceType type) {
        this.type = type;
    }

    public ResourceType getType() {
        return type;
    }

    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
