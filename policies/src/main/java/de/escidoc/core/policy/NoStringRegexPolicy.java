package de.escidoc.core.policy;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.DeclareError;

/**
 * @author Eduard Hildebrandt
 */
@Aspect
final class NoStringRegexPolicy {

    /*private final static String DESCRIPTION = " Such calls are usually not performant because the " +
                    "pattern is recompiled everytime the operation is called. " +
                    "Please use the java.util.regex.* API instead and make the patttern instance static.";

    @SuppressWarnings("unused")
    @DeclareError("call(public java.lang.String java.lang.String.replaceAll(..))")
    private static final String NO_STRING_REPLACE_ALL =
            "Using java.lang.String.replaceAll(..) is not allowed." + DESCRIPTION;

    @SuppressWarnings("unused")
    @DeclareError("call(public java.lang.String java.lang.String.replace(..))")
    private static final String NO_STRING_REPLACE =
            "Using java.lang.String.replace(..) is not allowed." + DESCRIPTION;

    @SuppressWarnings("unused")
    @DeclareError("call(public java.lang.String java.lang.String.replaceFirst(..))")
    private static final String NO_STRING_REPLACE_FIRST =
            "Using java.lang.String.replaceFirst(..) is not allowed." + DESCRIPTION;

    @SuppressWarnings("unused")
    @DeclareError("call(public java.lang.String java.lang.String.matches(..))")
    private static final String NO_STRING_MATCHES =
            "Using java.lang.String.matches(..) is not allowed."  + DESCRIPTION;

    @SuppressWarnings("unused")
    @DeclareError("call(public java.lang.String java.lang.String.split(..))")
    private static final String NO_STRING_SPLIT =
            "Using java.lang.String.split(..) is not allowed."  + DESCRIPTION;*/

    private NoStringRegexPolicy() {
    }
}
