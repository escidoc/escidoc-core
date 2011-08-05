package de.escidoc.core.policy;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.DeclareError;

/**
 * @author Eduard Hildebrandt
 */
@Aspect
final class NoStringToStringPolicy {

    @SuppressWarnings("unused")
    @DeclareError("call(public java.lang.String java.lang.String.toString())")
    private static final String NO_STRING_TO_STRING =
            "Please do not call toString() on java.lang.String class. This is an unnecessary call.";

    private NoStringToStringPolicy() {
    }

}
