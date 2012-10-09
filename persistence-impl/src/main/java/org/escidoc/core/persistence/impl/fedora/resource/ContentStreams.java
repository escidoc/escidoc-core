package org.escidoc.core.persistence.impl.fedora.resource;

import java.util.HashMap;

public class ContentStreams extends HashMap<String, ContentStream> {
    private static final long serialVersionUID = 1145522303777229778L;

    private boolean inherited = false;

    public boolean isInherited() {
        return inherited;
    }

    public void setInherited(boolean inherited) {
        this.inherited = inherited;
    }
}
