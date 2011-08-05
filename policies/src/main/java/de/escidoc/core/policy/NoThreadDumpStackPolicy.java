package de.escidoc.core.policy;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.DeclareError;

/**
 * @author Eduard Hildebrandt
 */
@Aspect
final class NoThreadDumpStackPolicy {

    @SuppressWarnings("unused")
    @DeclareError("call(* java.lang.Thread.dumpStack())")
    private static final String NO_THREAD_DUMP_STACK =
            "Please do not call Thread.dumpStack().  These are often temporary debugging statements, " +
                    "and should be either removed from production code, or replaced by a more robust logging facility.";

    private NoThreadDumpStackPolicy() {
    }

}
