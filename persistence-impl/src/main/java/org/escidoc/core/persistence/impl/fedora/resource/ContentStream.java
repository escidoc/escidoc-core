package org.escidoc.core.persistence.impl.fedora.resource;

import org.apache.commons.lang.builder.ToStringBuilder;

public class ContentStream {
    private boolean inherited = false;

    private String name = null;

    private String label = null;

    private String mimeType = null;

    private String reference = null;

    private String storage = null;

    public boolean isInherited() {
        return inherited;
    }

    public void setInherited(boolean inherited) {
        this.inherited = inherited;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getReference() {
        return reference;
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
