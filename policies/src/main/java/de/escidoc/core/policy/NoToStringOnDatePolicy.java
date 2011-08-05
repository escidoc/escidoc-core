package de.escidoc.core.policy;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.DeclareError;

/**
 * @author Eduard Hildebrandt
 */
@Aspect
final class NoToStringOnDatePolicy {

    @SuppressWarnings("unused")
    @DeclareError("call(public java.lang.String java.util.Date.toString())")
    private static final String NO_TO_STRING_ON_DATE =
            "Calling toString() on a java.util.Date object is not allowed. Such calls are usually incorrect in an " +
                    "internationalized environment.";

    private NoToStringOnDatePolicy() {
    }

}
