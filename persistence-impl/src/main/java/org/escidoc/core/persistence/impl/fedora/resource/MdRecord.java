package org.escidoc.core.persistence.impl.fedora.resource;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * @author FRS
 * 
 */
public class MdRecord {

    private boolean inherited = false;

    private String name;

    private String schema;

    private String type;

    private String xmlContent;

    public boolean isInherited() {
        return inherited;
    }

    public void setInherited(boolean inherited) {
        this.inherited = inherited;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getXmlContent() {
        return xmlContent;
    }

    public void setXmlContent(String xmlContent) {
        this.xmlContent = xmlContent;
    }

    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
