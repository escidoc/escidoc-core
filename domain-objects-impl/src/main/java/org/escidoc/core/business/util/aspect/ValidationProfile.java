package org.escidoc.core.business.util.aspect;

/**
 * @author Marko Voss (marko.voss@fiz-karlsruhe.de)
 */
public final class ValidationProfile {

    /**
     * The {@link org.escidoc.core.business.domain.base.DomainObject} exists in the repository.
     */
    public static final String EXISTS = "exists";

    /**
     * The {@link org.escidoc.core.business.domain.base.DomainObject} does not exist in the repository.
     */
    public static final String NEW = "new";

    /**
     * Avoid instantiation.
     */
    private ValidationProfile(){}
}
