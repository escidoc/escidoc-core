package de.escidoc.core.policy;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.DeclareError;
import org.aspectj.lang.annotation.DeclareWarning;

/**
 * @author Eduard Hildebrandt
 */
@Aspect
final class NoStringGetBytesPolicy {

    @SuppressWarnings("unused")
    @DeclareError("call(public byte[] java.lang.String.getBytes())")
    private static final String NO_STRING_GET_BYTES_WITHOUT_CHARSET =
            "Please do not use java.lang.String.getBytes() without specifing a charset. This method encodes the " +
                    "string into a sequence of bytes using the platform's default charset and may produce unexpected " +
                    "results on plattforms with a different charset.";

    @SuppressWarnings("unused")
    @DeclareWarning("call(public byte[] java.lang.String.getBytes(..))")
    private static final String WARNING_STRING_ON_GET_BYTES_WITH_CHARSET =
            "Please use java.lang.String.getBytes(...) with caution. The content of the string will be " +
                    "dublicated as byte[] in memory. If the string is very long this can produce a " +
                    "OutOfMemoryError. Consider using streams instead.";

    private NoStringGetBytesPolicy() {
    }
}
