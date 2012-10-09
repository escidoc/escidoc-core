package org.escidoc.core.persistence.impl.fedora.resource;

import org.apache.commons.lang.builder.ToStringBuilder;

public class ComponentContent {
    private String location;

    private String name;

    private String storage;

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStorage() {
        return storage;
    }

    public void setStorage(String storage) {
        this.storage = storage;
    }

    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
