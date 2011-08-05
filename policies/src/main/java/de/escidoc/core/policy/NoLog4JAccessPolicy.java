package de.escidoc.core.policy;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.DeclareError;

/**
 * @author Eduard Hildebrandt
 */
@Aspect
final class NoLog4JAccessPolicy {

    @SuppressWarnings("unused")
    @DeclareError("call(* org.apache.log4j..*.*(..))" )
    private static final String NO_LOG4J_API =
        "Direct access of Log4J API is not allowed. Please use the SL4J framework instead.";

    private NoLog4JAccessPolicy(){
    }

}
