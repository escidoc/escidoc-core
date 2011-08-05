package de.escidoc.core.policy;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.DeclareError;

/**
 * @author Eduard Hildebrandt
 */
@Aspect
final class NoLoggingAccessPolicy {

    @SuppressWarnings("unused")
    @DeclareError("call(* java.util.logging..*.*(..))" )
    private static final String NO_LOGGING_API =
        "Direct access of JDK Logging API is not allowed. Please use the SL4J framework instead.";

    private NoLoggingAccessPolicy(){
    }

}
