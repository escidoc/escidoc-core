package org.escidoc.core.business.domain.base;


/**
 * General abstract class for business logic layer's domain objects.
 *
 * @author Michael Hoppe (michael.hoppe@fiz-karlsruhe.de)
 */
public abstract class DomainObject {

    protected String validationProfile;
    
    /**
     * Constructor with validation-profile.
     * 
     * @param validationProfile validationProfile
     */
    public DomainObject(String validationProfile) {
        this.validationProfile = validationProfile;
    }

    /**
     * @return the validationProfile
     */
    public String getValidationProfile() {
        return validationProfile;
    }

}
