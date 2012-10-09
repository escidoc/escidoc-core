package org.escidoc.core.persistence.impl.fedora.resource;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * @author FRS
 * 
 */
public class Component {
    private boolean inherited = false;

    private String lastModificationDate;

    private String objid;

    private ComponentProperties properties;

    private MdRecords mdRecords;

    private ComponentContent content;

    public boolean isInherited() {
        return inherited;
    }

    public void setInherited(boolean inherited) {
        this.inherited = inherited;
    }

    public String getLastModificationDate() {
        return lastModificationDate;
    }

    public void setLastModificationDate(String lastModificationDate) {
        this.lastModificationDate = lastModificationDate;
    }

    public String getObjid() {
        return objid;
    }

    public void setObjid(String objid) {
        this.objid = objid;
    }

    public ComponentProperties getProperties() {
        return properties;
    }

    public void setProperties(ComponentProperties properties) {
        this.properties = properties;
    }

    public MdRecords getMdRecords() {
        return mdRecords;
    }

    public void setMdRecords(MdRecords mdRecords) {
        this.mdRecords = mdRecords;
    }

    public void setContent(ComponentContent content) {
        this.content = content;
    }

    public ComponentContent getContent() {
        return content;
    }

    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
