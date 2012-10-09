package org.escidoc.core.persistence.impl.fedora.resource;

public class Predicate {
    private String namespace;

    private String localName;

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        if ((namespace.endsWith("/")) || (namespace.endsWith("#"))) {
            this.namespace = namespace.substring(0, namespace.length() - 1);
        }
        else {
            this.namespace = namespace;
        }
    }

    public String getLocalName() {
        return localName;
    }

    public void setLocalName(String localName) {
        this.localName = localName;
    }

    @Override
    public String toString() {
        String uri = namespace;

        uri += "#";
        uri += localName;
        return uri;
    }

}
