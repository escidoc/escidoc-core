package de.escidoc.core.policy;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.DeclareError;

/**
 * @author Eduard Hildebrandt
 */
@Aspect
final class NoStringConcatPolicy {

    @SuppressWarnings("unused")
    @DeclareError("call(public java.lang.String java.lang.String.concat(..))")
    private static final String NO_STRING_concat =
            "Please do not use java.lang.String.concat(..). Such calls can be replaced with the '+' operator for " +
                    "increased code clarity and possible increased performance if the method was invoked on a constant with a constant argument.";

    private NoStringConcatPolicy() {
    }

}
