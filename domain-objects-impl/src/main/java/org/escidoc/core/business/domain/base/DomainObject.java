package org.escidoc.core.business.domain.base;


/**
 * General interface for business logic layer's domain objects.
 *
 * @author Michael Hoppe (michael.hoppe@fiz-karlsruhe.de)
 */
public abstract class DomainObject {

    protected String validationProfile;
    
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
