package de.escidoc.core.common.util.aop;

import org.aspectj.lang.reflect.MethodSignature;

/**
 * Utility class for aop in eSciDoc.<br>
 * Besides providing utility methods, the order of the interceptors (advises) is
 * defined here.
 * 
 * @author TTE
 * @common
 */
public final class AopUtil {

    public static final int PRECEDENCE_STATISTIC_INTERCEPTOR = 0;

    public static final int PRECEDENCE_AUTHENTICATION_INTERCEPTOR = 1;

    public static final int PRECEDENCE_PARAMETER_CHECK_INTERCEPTOR = 2;

    public static final int PRECEDENCE_XML_VALIDATION_INTERCEPTOR = 3;

    public static final int PRECEDENCE_SECURITY_INTERCEPTOR = 4;

    public static final int PRECEDENCE_XML_HEADER_INTERCEPTOR = 5;

    /**
     * Private constructor to prevent initialization.
     * 
     * @common
     */
    private AopUtil() {
    }

}
