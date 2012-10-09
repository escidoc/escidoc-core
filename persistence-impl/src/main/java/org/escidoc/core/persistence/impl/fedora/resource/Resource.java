package org.escidoc.core.persistence.impl.fedora.resource;

public interface Resource {
    String getLocalName();

    String getPathComponent();

    String getTitle();

    boolean isAlwaysVisible();
}
