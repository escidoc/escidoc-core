package de.escidoc.core.policy;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.DeclareError;
import org.aspectj.lang.annotation.DeclareWarning;

@Aspect
final class NoMapImplAsReturnValuePolicy {

    @SuppressWarnings("unused")
    @DeclareError(
            "execution(" + "(java.util.HashMap+" + " || java.util.Hashtable+" +
                    " || java.util.concurrent.ConcurrentHashMap+" + " || java.util.WeakHashMap+" +
                    " || java.util.TreeMap+" + " || java.awt.RenderingHints+" +
                    " || java.util.LinkedHashMap+" + " || java.util.IdentityHashMap+" + " || java.util.EnumMap+" +
                    " || java.util.IdentityHashMap+" + " || java.util.IdentityHashMap+" +
                    " || java.util.IdentityHashMap+" + " || java.util.IdentityHashMap+" +
                    " || java.util.IdentityHashMap+" + ")" + " *..*.*(..)) && !execution(java.util.Properties+ *..*.*(..))")
    private static final String NO_MAP_IMPL_AS_RETURN_VALUE =
            "Returning a Map implementation as not allowed. Please use the Map interface instead of the " +
                    " implementation class. For example: Use java.lang.Map instead of java.lang.HashMap.";

    private NoMapImplAsReturnValuePolicy() {
    }

}
