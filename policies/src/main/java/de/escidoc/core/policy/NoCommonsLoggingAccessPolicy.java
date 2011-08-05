package de.escidoc.core.policy;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.DeclareError;

/**
 * @author Eduard Hildebrandt
 */
@Aspect
final class NoCommonsLoggingAccessPolicy {

    @SuppressWarnings("unused")
    @DeclareError("call(* org.apache.commons.logging..*.*(..))" )
    private static final String NO_COMMONS_LOGGING_API =
        "Direct access of Commons Logging API is not allowed. Please use the SL4J framework instead.";

    private NoCommonsLoggingAccessPolicy(){
    }

}
