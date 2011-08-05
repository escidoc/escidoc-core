package de.escidoc.core.policy;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.DeclareError;

/**
 * @author Eduard Hildebrandt
 */
@Aspect
final class NoVectorPolicy {

    @SuppressWarnings("unused")
    @DeclareError("call(public * java.util.Vector.*(..)) && !within(de.escidoc.core.common.business.filter..*)")
    private static final String NO_VECTOR =
            "Please do not use java.util.Vector class. While still supported, these class is made obsolete by the " +
                    "JDK1.2 collection classes, and should not be used in new development.";

    private NoVectorPolicy() {
    }

}
