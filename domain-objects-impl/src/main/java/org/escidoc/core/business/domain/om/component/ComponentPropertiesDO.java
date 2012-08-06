package org.escidoc.core.business.domain.om.component;

import net.sf.oval.constraint.AssertFieldConstraints;
import net.sf.oval.constraint.NotBlank;
import net.sf.oval.constraint.NotNull;

import org.escidoc.core.business.domain.base.ID;
import org.escidoc.core.business.domain.base.Pid;
import org.escidoc.core.business.domain.common.CommonPropertiesDO;
import org.escidoc.core.business.util.annotation.Validate;
import org.escidoc.core.business.util.aspect.ValidationProfile;

/**
 * @author Michael Hoppe (michael.hoppe@fiz-karlsruhe.de)
 */
@Validate
public class ComponentPropertiesDO extends CommonPropertiesDO {

    @NotNull(profiles = { ValidationProfile.EXISTS })
    private ID originItem;

    @NotNull(profiles = { ValidationProfile.EXISTS })
    private ID originComponent;

	private ValidStatus validStatusInfo;

    @NotNull
    private String visibility;

    private Pid pid;

    @NotNull
    private String contentCategory;

    private String fileName;

    private String mimeType;

    private String checksum;

    private ChecksumAlgorithm checksumAlgorithm;

	public ComponentPropertiesDO(Builder builder) {
	    super(builder);
        this.originItem = builder.originItem;
        this.originComponent = builder.originComponent;
	    this.validStatusInfo = builder.validStatusInfo;
	    this.visibility = builder.visibility;
	    this.pid = builder.pid;
	    this.contentCategory = builder.contentCategory;
	    this.fileName = builder.fileName;
	    this.mimeType = builder.mimeType;
	    this.checksum = builder.checksum;
	    this.checksumAlgorithm = builder.checksumAlgorithm;
	}

    /**
     * @return the originItem
     */
    @AssertFieldConstraints
    public ID getOriginItem() {
        return originItem;
    }

    /**
     * @param originItem the originItem to set
     */
    public void setOriginItem(@AssertFieldConstraints ID originItem) {
        this.originItem = originItem;
    }

    /**
     * @return the originComponent
     */
    @AssertFieldConstraints
    public ID getOriginComponent() {
        return originComponent;
    }

    /**
     * @param originComponent the originComponent to set
     */
    public void setOriginComponent(@AssertFieldConstraints ID originComponent) {
        this.originComponent = originComponent;
    }

    /**
     * @return the validStatusInfo
     */
	@AssertFieldConstraints
    public ValidStatus getValidStatusInfo() {
        return validStatusInfo;
    }

    /**
     * @param validStatusInfo the validStatusInfo to set
     */
    public void setValidStatusInfo(@AssertFieldConstraints ValidStatus validStatusInfo) {
        this.validStatusInfo = validStatusInfo;
    }

    /**
     * @return the visibility
     */
    @AssertFieldConstraints
    public String getVisibility() {
        return visibility;
    }

    /**
     * @param visibility the visibility to set
     */
    public void setVisibility(@AssertFieldConstraints String visibility) {
        this.visibility = visibility;
    }

    /**
     * @return the pid
     */
    @AssertFieldConstraints
    public Pid getPid() {
        return pid;
    }

    /**
     * @param pid the pid to set
     */
    public void setPid(@AssertFieldConstraints Pid pid) {
        this.pid = pid;
    }

    /**
     * @return the contentCategory
     */
    @AssertFieldConstraints
    public String getContentCategory() {
        return contentCategory;
    }

    /**
     * @param contentCategory the contentCategory to set
     */
    public void setContentCategory(@AssertFieldConstraints String contentCategory) {
        this.contentCategory = contentCategory;
    }

    /**
     * @return the fileName
     */
    @AssertFieldConstraints
    public String getFileName() {
        return fileName;
    }

    /**
     * @param fileName the fileName to set
     */
    public void setFileName(@AssertFieldConstraints String fileName) {
        this.fileName = fileName;
    }

    /**
     * @return the mimeType
     */
    @AssertFieldConstraints
    public String getMimeType() {
        return mimeType;
    }

    /**
     * @param mimeType the mimeType to set
     */
    public void setMimeType(@AssertFieldConstraints String mimeType) {
        this.mimeType = mimeType;
    }

    /**
     * @return the checksum
     */
    @AssertFieldConstraints
    public String getChecksum() {
        return checksum;
    }

    /**
     * @param checksum the checksum to set
     */
    public void setChecksum(@AssertFieldConstraints String checksum) {
        this.checksum = checksum;
    }

    /**
     * @return the checksumAlgorithm
     */
    @AssertFieldConstraints
    public ChecksumAlgorithm getChecksumAlgorithm() {
        return checksumAlgorithm;
    }

    /**
     * @param checksumAlgorithm the checksumAlgorithm to set
     */
    public void setChecksumAlgorithm(@AssertFieldConstraints ChecksumAlgorithm checksumAlgorithm) {
        this.checksumAlgorithm = checksumAlgorithm;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ComponentPropertiesDO componentPropertiesDO = (ComponentPropertiesDO) o;

        if (!originItem.equals(componentPropertiesDO.originItem)) {
            return false;
        }
        if (!originComponent.equals(componentPropertiesDO.originComponent)) {
            return false;
        }
        if (!validStatusInfo.equals(componentPropertiesDO.validStatusInfo)) {
            return false;
        }
        if (!visibility.equals(componentPropertiesDO.visibility)) {
            return false;
        }
        if (!pid.equals(componentPropertiesDO.pid)) {
            return false;
        }
        if (!contentCategory.equals(componentPropertiesDO.contentCategory)) {
            return false;
        }
        if (!fileName.equals(componentPropertiesDO.fileName)) {
            return false;
        }
        if (!mimeType.equals(componentPropertiesDO.mimeType)) {
            return false;
        }
        if (!checksum.equals(componentPropertiesDO.checksum)) {
            return false;
        }
        if (!checksumAlgorithm.equals(componentPropertiesDO.checksumAlgorithm)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = validStatusInfo.hashCode();
        result = 31 * result + originItem.hashCode();
        result = 31 * result + originComponent.hashCode();
        result = 31 * result + visibility.hashCode();
        result = 31 * result + pid.hashCode();
        result = 31 * result + contentCategory.hashCode();
        result = 31 * result + fileName.hashCode();
        result = 31 * result + mimeType.hashCode();
        result = 31 * result + checksum.hashCode();
        result = 31 * result + checksumAlgorithm.hashCode();
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
        sb.append("ComponentPropertiesDO");
        sb.append("{originItem=").append(originItem);
        sb.append("{originComponent=").append(originComponent);
        sb.append("{validStatusInfo=").append(validStatusInfo);
        sb.append(", visibility=").append(visibility);
        sb.append(", pid=").append(pid);
        sb.append(", contentCategory=").append(contentCategory);
        sb.append(", fileName=").append(fileName);
        sb.append(", mimeType=").append(mimeType);
        sb.append(", checksum=").append(checksum);
        sb.append(", checksumAlgorithm=").append(checksumAlgorithm);
        sb.append('}');
        return sb;
    }
    
    public static class Builder extends CommonPropertiesDO.Builder {
        private ID originItem = null;

        private ID originComponent = null;

        private ValidStatus validStatusInfo = null;

        private String visibility = null;

        private Pid pid = null;

        private String contentCategory = null;

        private String fileName = null;

        private String mimeType = null;

        private String checksum = null;

        private ChecksumAlgorithm checksumAlgorithm = null;

        public Builder(String validationProfile) {
            super(validationProfile);
        }
        
        public Builder originItem(ID originItem) {
            this.originItem = originItem;
            return this;
        }

        public Builder originComponent(ID originComponent) {
            this.originComponent = originComponent;
            return this;
        }

        public Builder validStatusInfo(ValidStatus validStatusInfo) {
            this.validStatusInfo = validStatusInfo;
            return this;
        }

        public Builder visibility(String visibility) {
            this.visibility = visibility;
            return this;
        }

        public Builder pid(Pid pid) {
            this.pid = pid;
            return this;
        }

        public Builder contentCategory(String contentCategory) {
            this.contentCategory = contentCategory;
            return this;
        }
        
        public Builder fileName(String fileName) {
            this.fileName = fileName;
            return this;
        }
        
        public Builder mimeType(String mimeType) {
            this.mimeType = mimeType;
            return this;
        }
        
        public Builder checksum(String checksum) {
            this.checksum = checksum;
            return this;
        }
        
        public Builder checksumAlgorithm(ChecksumAlgorithm checksumAlgorithm) {
            this.checksumAlgorithm = checksumAlgorithm;
            return this;
        }
        
        public ComponentPropertiesDO build() {
            return new ComponentPropertiesDO(this);
        }
        
    }
}
