package org.escidoc.core.persistence.impl.fedora.resource;

import java.util.TreeMap;

public class VersionHistory extends TreeMap<String, Version>
    implements Resource {
    private static final long serialVersionUID = 6605427555433343633L;

    private boolean alwaysVisible = false;

    public String getTitle() {
        return "Version-History";
    }

    public String getLocalName() {
        return getPathComponent();
    }

    public String getPathComponent() {
        return "version-history";
    }

    public boolean isAlwaysVisible() {
        return alwaysVisible;
    }

    public void setAlwaysVisible(final boolean alwaysVisible) {
        this.alwaysVisible = alwaysVisible;
    }
}
