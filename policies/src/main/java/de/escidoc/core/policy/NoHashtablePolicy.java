package de.escidoc.core.policy;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.DeclareError;

/**
 * @author Eduard Hildebrandt
 */
@Aspect
final class NoHashtablePolicy {

    @SuppressWarnings("unused")
    @DeclareError("call(public * java.util.Hashtable.*(..)) && !call(public * java.util.Properties.*(..))")
    private static final String NO_HASHTABLE =
            "Please do not use java.util.Hashtable class. While still supported, these class is made obsolete by the " +
                    "JDK1.2 collection classes, and should not be used in new development.";

    private NoHashtablePolicy() {
    }

}
