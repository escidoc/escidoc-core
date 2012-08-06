package org.escidoc.core.business.domain.om.item;

import net.sf.oval.constraint.AssertFieldConstraints;
import net.sf.oval.constraint.NotBlank;
import net.sf.oval.constraint.NotNegative;
import net.sf.oval.constraint.NotNull;

import org.escidoc.core.business.domain.base.ID;
import org.escidoc.core.business.domain.base.Pid;
import org.escidoc.core.business.domain.common.CommonPropertiesDO;
import org.escidoc.core.business.domain.common.LockInfoDO;
import org.escidoc.core.business.domain.common.StatusInfoDO;
import org.escidoc.core.business.util.annotation.Validate;
import org.escidoc.core.business.util.aspect.ValidationProfile;
import org.joda.time.DateTime;

/**
 * @author Marko Voss (marko.voss@fiz-karlsruhe.de)
 * @author Michael Hoppe (michael.hoppe@fiz-karlsruhe.de)
 */
@Validate
public class ItemPropertiesDO extends CommonPropertiesDO {

    @NotNull(profiles = { ValidationProfile.EXISTS })
    private DateTime timestamp;

    @NotNull(profiles = { ValidationProfile.EXISTS })
	private ID origin;

	@NotNull
	private ID context;

	@NotNull
	private ID contentModel;

	// xml: object-status, object-status-comment
	@NotNull(profiles = { ValidationProfile.EXISTS })
	private StatusInfoDO<ItemStatus> statusInfo;

    @NotNegative
    private Integer versionNumber;

    @NotNull(profiles = { ValidationProfile.EXISTS })
    private StatusInfoDO<ItemStatus> versionStatusInfo;

	// Only show in latest version
	private LockInfoDO lockInfo;

	@NotBlank
	private Pid objectPid;

    @NotNull(profiles = { ValidationProfile.EXISTS })
    private ID modifiedBy;

    //xml: version-pid
    @NotBlank
    private Pid versionPid;
    
	public ItemPropertiesDO(Builder builder) {
	    super(builder);
	    this.timestamp = builder.timestamp;
	    this.origin = builder.origin;
	    this.context = builder.context;
	    this.contentModel = builder.contentModel;
	    this.statusInfo = builder.statusInfo;
	    this.versionNumber = builder.versionNumber;
	    this.versionStatusInfo = builder.versionStatusInfo;
	    this.lockInfo = builder.lockInfo;
	    this.objectPid = builder.objectPid;
	    this.modifiedBy = builder.modifiedBy;
        this.versionPid = builder.versionPid;
	}

	/**
     * @return the timestamp
     */
    @AssertFieldConstraints
    public DateTime getTimestamp() {
        return timestamp;
    }

    /**
     * @param timestamp the timestamp to set
     */
    public void setTimestamp(@AssertFieldConstraints DateTime timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * @return the origin
     */
    @AssertFieldConstraints
    public ID getOrigin() {
        return origin;
    }

    /**
     * @param origin the origin to set
     */
    public void setOrigin(@AssertFieldConstraints ID origin) {
        this.origin = origin;
    }

    /**
     * @return the context
     */
    @AssertFieldConstraints
    public ID getContext() {
        return context;
    }

    /**
     * @param context the context to set
     */
    public void setContext(@AssertFieldConstraints ID context) {
        this.context = context;
    }

    /**
     * @return the contentModel
     */
    @AssertFieldConstraints
    public ID getContentModel() {
        return contentModel;
    }

    /**
     * @param contentModel the contentModel to set
     */
    public void setContentModel(@AssertFieldConstraints ID contentModel) {
        this.contentModel = contentModel;
    }

    /**
     * @return the statusInfo
     */
    @AssertFieldConstraints
    public StatusInfoDO<ItemStatus> getStatusInfo() {
        return statusInfo;
    }

    /**
     * @param statusInfo the statusInfo to set
     */
    public void setStatusInfo(@AssertFieldConstraints StatusInfoDO<ItemStatus> statusInfo) {
        this.statusInfo = statusInfo;
    }

    /**
     * @return the versionNumber
     */
    @AssertFieldConstraints
    public Integer getVersionNumber() {
        return versionNumber;
    }

    /**
     * @param versionNumber the versionNumber to set
     */
    public void setVersionNumber(@AssertFieldConstraints Integer versionNumber) {
        this.versionNumber = versionNumber;
    }

    /**
     * @return the versionStatusInfo
     */
    @AssertFieldConstraints
    public StatusInfoDO<ItemStatus> getVersionStatusInfo() {
        return versionStatusInfo;
    }

    /**
     * @param versionStatusInfo the versionStatusInfo to set
     */
    public void setVersionStatusInfo(@AssertFieldConstraints StatusInfoDO<ItemStatus> versionStatusInfo) {
        this.versionStatusInfo = versionStatusInfo;
    }

    /**
     * @return the lockInfo
     */
    @AssertFieldConstraints
    public LockInfoDO getLockInfo() {
        return lockInfo;
    }

    /**
     * @param lockInfo the lockInfo to set
     */
    public void setLockInfo(@AssertFieldConstraints LockInfoDO lockInfo) {
        this.lockInfo = lockInfo;
    }

    /**
     * @return the objectPid
     */
    @AssertFieldConstraints
    public Pid getObjectPid() {
        return objectPid;
    }

    /**
     * @param objectPid the objectPid to set
     */
    public void setObjectPid(@AssertFieldConstraints Pid objectPid) {
        this.objectPid = objectPid;
    }

    /**
     * @return the modifiedBy
     */
    @AssertFieldConstraints
    public ID getModifiedBy() {
        return modifiedBy;
    }

    /**
     * @param modifiedBy the modifiedBy to set
     */
    public void setModifiedBy(@AssertFieldConstraints ID modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    /**
     * @return the versionPid
     */
    @AssertFieldConstraints
    public Pid getVersionPid() {
        return versionPid;
    }

    /**
     * @param versionPid the versionPid to set
     */
    public void setVersionPid(@AssertFieldConstraints Pid versionPid) {
        this.versionPid = versionPid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ItemPropertiesDO itemPropertiesDO = (ItemPropertiesDO) o;

        if (!timestamp.equals(itemPropertiesDO.timestamp)) {
            return false;
        }
        if (!origin.equals(itemPropertiesDO.origin)) {
            return false;
        }
        if (!context.equals(itemPropertiesDO.context)) {
            return false;
        }
        if (!contentModel.equals(itemPropertiesDO.contentModel)) {
            return false;
        }
        if (!statusInfo.equals(itemPropertiesDO.statusInfo)) {
            return false;
        }

        if (!versionNumber.equals(itemPropertiesDO.versionNumber)) {
            return false;
        }
        if (!versionStatusInfo.equals(itemPropertiesDO.versionStatusInfo)) {
            return false;
        }
        if (!lockInfo.equals(itemPropertiesDO.lockInfo)) {
            return false;
        }
        if (!objectPid.equals(itemPropertiesDO.objectPid)) {
            return false;
        }
        if (!modifiedBy.equals(itemPropertiesDO.modifiedBy)) {
            return false;
        }
        if (!versionPid.equals(itemPropertiesDO.versionPid)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = timestamp.hashCode();
        result = 31 * result + origin.hashCode();
        result = 31 * result + context.hashCode();
        result = 31 * result + contentModel.hashCode();
        result = 31 * result + statusInfo.hashCode();
        result = 31 * result + versionNumber.hashCode();
        result = 31 * result + versionStatusInfo.hashCode();
        result = 31 * result + lockInfo.hashCode();
        result = 31 * result + objectPid.hashCode();
        result = 31 * result + modifiedBy.hashCode();
        result = 31 * result + versionPid.hashCode();
        return result;
    }

    @Override
    @NotNull
    @NotBlank
    public String toString() {
        return toStringBuilder().toString();
    }

    @NotNull
    public StringBuilder toStringBuilder() {
        final StringBuilder sb = new StringBuilder();
        sb.append("ItemPropertiesDO");
        sb.append("{timestamp=").append(timestamp);
        sb.append(", origin=").append(origin);
        sb.append(", context=").append(context);
        sb.append(", contentModel=").append(contentModel);
        sb.append(", statusInfo=").append(statusInfo);
        sb.append(", versionNumber=").append(versionNumber);
        sb.append(", versionStatusInfo=").append(versionStatusInfo);
        sb.append(", lockInfo=").append(lockInfo);
        sb.append(", objectPid=").append(objectPid);
        sb.append(", modifiedBy=").append(modifiedBy);
        sb.append(", versionPid=").append(versionPid);
        sb.append('}');
        return sb;
    }
    
    public static class Builder extends CommonPropertiesDO.Builder {
        private DateTime timestamp;

        private ID origin = null;

        private ID context = null;

        private ID contentModel = null;

        private StatusInfoDO<ItemStatus> statusInfo = null;

        private Integer versionNumber = null;

        private StatusInfoDO<ItemStatus> versionStatusInfo = null;

        private LockInfoDO lockInfo = null;

        private Pid objectPid = null;

        private ID modifiedBy = null;

        private Pid versionPid = null;
        
        public Builder(String validationProfile) {
            super(validationProfile);
        }
        
        public Builder timestamp(DateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Builder origin(ID origin) {
            this.origin = origin;
            return this;
        }

        public Builder context(ID context) {
            this.context = context;
            return this;
        }

        public Builder contentModel(ID contentModel) {
            this.contentModel = contentModel;
            return this;
        }
        
        public Builder statusInfo(StatusInfoDO<ItemStatus> statusInfo) {
            this.statusInfo = statusInfo;
            return this;
        }
        
        public Builder versionNumber(int versionNumber) {
            this.versionNumber = versionNumber;
            return this;
        }
        
        public Builder versionStatusInfo(StatusInfoDO<ItemStatus> versionStatusInfo) {
            this.versionStatusInfo = versionStatusInfo;
            return this;
        }
        
        public Builder lockInfo(LockInfoDO lockInfo) {
            this.lockInfo = lockInfo;
            return this;
        }
        
        public Builder objectPid(Pid objectPid) {
            this.objectPid = objectPid;
            return this;
        }
        
        public Builder modifiedBy(ID modifiedBy) {
            this.modifiedBy = modifiedBy;
            return this;
        }
        
        public Builder versionPid(Pid versionPid) {
            this.versionPid = versionPid;
            return this;
        }
        
        public ItemPropertiesDO build() {
            return new ItemPropertiesDO(this);
        }
        
    }
}
