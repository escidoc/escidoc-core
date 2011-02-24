package de.escidoc.core.common.business.fedora.resources.create;

import de.escidoc.core.common.business.fedora.resources.StatusType;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.util.service.UserContext;

/**
 * Version properties of versionable resource (Item/Container)(can represent
 * current and latest version).
 * 
 * @author SWA
 * 
 */
public class VersionProperties {

    private String versionNumber = "1";

    private String createdById = null;

    private String createdByName = null;

    private String modifiedById = null;

    private String modifiedByName = null;

    private StatusType status = StatusType.PENDING;

    private String comment = "Object created.";

    private String pid = null;

    private String date = null;

    /**
     * Version properties of Item.
     * 
     * @throws WebserverSystemException
     *             Thrown if obtaining user context failed.
     */
    public VersionProperties() throws WebserverSystemException {

        // setting up some default values
        setCreatedById(UserContext.getId());
        setCreatedByName(UserContext.getRealName());
        setModifiedById(UserContext.getId());
        setModifiedByName(UserContext.getRealName());

    }

    /**
     * @param versionNo
     *            the versionNumber to set
     */
    public void setNumber(final String versionNo) {
        this.versionNumber = versionNo;
    }

    /**
     * @return the versionNumber
     */
    public final String getNumber() {
        return versionNumber;
    }

    /**
     * Set Id of Creator.
     * 
     * @param createdById
     *            the creator id
     */
    public final void setCreatedById(final String createdById) {
        this.createdById = createdById;
    }

    /**
     * Get Id of creator.
     * 
     * @return the creator id
     */
    public final String getCreatedById() {
        return createdById;
    }

    /**
     * Set id of modifier of this version.
     * 
     * @param modifiedById
     *            the modifiedById to set
     */
    public final void setModifiedById(final String modifiedById) {
        this.modifiedById = modifiedById;
    }

    /**
     * Get id of modifier of this version.
     * 
     * @return the modifiedById
     */
    public final String getModifiedById() {
        return modifiedById;
    }

    /**
     * Get status of version.
     * 
     * @param status
     *            the status to set
     */
    public final void setStatus(final StatusType status) {
        this.status = status;
    }

    /**
     * @return the status
     */
    public final StatusType getStatus() {
        return status;
    }

    /**
     * @param comment
     *            the statusComment to set
     */
    public void setComment(final String comment) {
        this.comment = comment;
    }

    /**
     * @return the statusComment
     */
    public final String getComment() {
        return comment;
    }

    /**
     * @param createdByName
     *            the createdByName to set
     */
    public final void setCreatedByName(final String createdByName) {
        this.createdByName = createdByName;
    }

    /**
     * @return the createdByName
     */
    public final String getCreatedByName() {
        return createdByName;
    }

    /**
     * @param modifiedByName
     *            the modifiedByName to set
     */
    public final void setModifiedByName(final String modifiedByName) {
        this.modifiedByName = modifiedByName;
    }

    /**
     * @return the modifiedByName
     */
    public String getModifiedByName() {
        return modifiedByName;
    }

    /**
     * @param pid
     *            the pid to set
     */
    public void setPid(final String pid) {
        this.pid = pid;
    }

    /**
     * @return the pid
     */
    public final String getPid() {
        return pid;
    }

    /**
     * @param date
     *            the date to set
     */
    public final void setDate(final String date) {
        this.date = date;
    }

    /**
     * @return the date
     */
    public final String getDate() {
        return date;
    }

}
