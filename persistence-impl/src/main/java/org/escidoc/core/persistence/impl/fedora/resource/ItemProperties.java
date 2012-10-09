package org.escidoc.core.persistence.impl.fedora.resource;

import org.apache.commons.lang.builder.ToStringBuilder;

import de.escidoc.core.common.business.fedora.resources.LockStatus;

/**
 * @author FRS
 * 
 */
public class ItemProperties {

    private String name = "";

    private String description = "";

    private String creationDate;

    private String createdBy;

    private String createdByTitle;

    private String publicStatus;

    private String publicStatusComment;

    private String context;

    private String contextTitle;

    private String contentModel;

    private String contentModelTitle;

    private String origin;

    private String originTitle;

    private LockStatus lockStatus = LockStatus.UNLOCKED;

    private String lockDate;

    private String lockOwner;

    private String lockOwnerTitle;

    private String pid;

    private Version version;

    private Version latestVersion;

    private Version latestRelease;

    private String contentModelSpecificElement;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Version getVersion() {
        return version;
    }

    public void setVersion(Version version) {
        this.version = version;
    }

    public String getContentModelSpecificElement() {
        return contentModelSpecificElement;
    }

    public void setContentModelSpecificElement(
        String contentModelSpecificElement) {
        this.contentModelSpecificElement = contentModelSpecificElement;
    }

    public Version getLatestVersion() {
        return latestVersion;
    }

    public void setLatestVersion(Version latestVersion) {
        this.latestVersion = latestVersion;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public void setCreatedByTitle(String createdByTitle) {
        this.createdByTitle = createdByTitle;
    }

    public String getCreatedByTitle() {
        return createdByTitle;
    }

    public String getPublicStatus() {
        return publicStatus;
    }

    public void setPublicStatus(String publicStatus) {
        this.publicStatus = publicStatus;
    }

    public String getPublicStatusComment() {
        return publicStatusComment;
    }

    public void setPublicStatusComment(String publicStatusComment) {
        this.publicStatusComment = publicStatusComment;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public void setContextTitle(String contextTitle) {
        this.contextTitle = contextTitle;
    }

    public String getContextTitle() {
        return contextTitle;
    }

    public String getContentModel() {
        return contentModel;
    }

    public void setContentModel(String contentModel) {
        this.contentModel = contentModel;
    }

    public void setContentModelTitle(String contentModelTitle) {
        this.contentModelTitle = contentModelTitle;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getOriginTitle() {
        return originTitle;
    }

    public void setOriginTitle(String originTitle) {
        this.originTitle = originTitle;
    }

    public String getContentModelTitle() {
        return contentModelTitle;
    }

    public LockStatus getLockStatus() {
        return lockStatus;
    }

    public void setLockStatus(LockStatus lockStatus) {
        this.lockStatus = lockStatus;
    }

    public String getLockDate() {
        return lockDate;
    }

    public void setLockDate(String lockDate) {
        this.lockDate = lockDate;
    }

    public String getLockOwner() {
        return lockOwner;
    }

    public void setLockOwner(String lockOwner) {
        this.lockOwner = lockOwner;
    }

    public void setLockOwnerTitle(String lockOwnerTitle) {
        this.lockOwnerTitle = lockOwnerTitle;
    }

    public String getLockOwnerTitle() {
        return lockOwnerTitle;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public Version getLatestRelease() {
        return latestRelease;
    }

    public void setLatestRelease(Version latestRelease) {
        this.latestRelease = latestRelease;
    }

    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
