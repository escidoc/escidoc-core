package de.escidoc.core.policy;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.DeclareError;

/**
 * @author Eduard Hildebrandt
 */
@Aspect
final class OnlyPrivateFieldsPolicy {

    /*@SuppressWarnings("unused")
    @DeclareError("get(!private !final * *) || set(!private !final * *)")
    private static final String ONLY_PRIVATE_FIELDS =
        "Don't use non final public, protected or default fields."
        + " Please make all non final fields private and use set/get methods.";*/

    private OnlyPrivateFieldsPolicy() {
    }
}
