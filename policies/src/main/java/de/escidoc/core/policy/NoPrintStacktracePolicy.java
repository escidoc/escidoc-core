package de.escidoc.core.policy;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.DeclareError;

/**
 * @author Eduard Hildebrandt
 */
@Aspect
final class NoPrintStacktracePolicy {

    @SuppressWarnings("unused")
    @DeclareError("call(* java.lang.Throwable.printStackTrace())")
    private static final String NO_PRINTSTACKTRACE =
            "Please do not call Throwable.printStackTrace() without arguments. These are often temporary debugging statements, and should be either removed from production code, or replaced by a more robust logging facility.";

    private NoPrintStacktracePolicy() {
    }

}
