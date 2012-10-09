package org.escidoc.core.persistence.impl.fedora.resource;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * @author FRS
 * 
 */
public class Item {

    private String lastModificationDate;

    private String objid;

    private ItemProperties properties;

    /*
     * The following attributes are initialized because it should be possible to
     * visit them. A concrete visitor - e.g. the eSciDoc XML Serializer - must
     * decide if writing a 'components' element for an empty Components object.
     */
    private MdRecords mdRecords = new MdRecords();

    private ContentStreams contentStreams = new ContentStreams();

    private Components components = new Components();

    private Relations relations = new Relations();

    private Resources resources = new Resources();

    public void setLastModificationDate(String lastModificationDate) {
        this.lastModificationDate = lastModificationDate;
    }

    public String getLastModificationDate() {
        return lastModificationDate;
    }

    public ItemProperties getProperties() {
        return properties;
    }

    public String getObjid() {
        return objid;
    }

    public void setObjid(String objid) {
        this.objid = objid;
    }

    public void setProperties(ItemProperties properties) {
        this.properties = properties;
    }

    public MdRecords getMdRecords() {
        return mdRecords;
    }

    public void setMdRecords(MdRecords mdRecords) {
        this.mdRecords = mdRecords;
    }

    public void setContentStreams(ContentStreams contentStreams) {
        this.contentStreams = contentStreams;
    }

    public ContentStreams getContentStreams() {
        return contentStreams;
    }

    public Components getComponents() {
        return components;
    }

    public void setComponents(Components components) {
        this.components = components;
    }

    public Relations getRelations() {
        return relations;
    }

    public void setRelations(Relations relations) {
        this.relations = relations;
    }

    public Resources getResources() {
        return resources;
    }

    public void setResources(Resources resources) {
        this.resources = resources;
    }

    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
