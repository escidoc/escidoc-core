package org.escidoc.core.persistence.impl.fedora.resource;

/**
 * @author FRS
 * 
 */
public class MdRecordDefinition {

    private String name;

    private boolean hasSchema = false;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean hasSchema() {
        return hasSchema;
    }

    public void setSchema(boolean hasSchema) {
        this.hasSchema = hasSchema;
    }
}
