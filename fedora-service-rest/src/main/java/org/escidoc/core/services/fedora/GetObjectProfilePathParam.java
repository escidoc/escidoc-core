package org.escidoc.core.services.fedora;

import net.sf.oval.constraint.AssertFieldConstraints;
import net.sf.oval.constraint.NotEmpty;
import net.sf.oval.guard.Guarded;

import javax.validation.constraints.NotNull;

/**
 * @author <a href="mailto:mail@eduard-hildebrandt.de">Eduard Hildebrandt</a>
 */
@Guarded(applyFieldConstraintsToConstructors = true, applyFieldConstraintsToSetters = true,
        assertParametersNotNull = false, checkInvariants = true, inspectInterfaces = true)
public final class GetObjectProfilePathParam {

    @NotNull
    @NotEmpty
    private final String pid;

    public GetObjectProfilePathParam(@AssertFieldConstraints final String pid) {
        this.pid = pid;
    }

    public String getPid() {
        return pid;
    }

    @Override
    public boolean equals(final Object o) {
        if(this == o) {
            return true;
        }
        if(o == null || getClass() != o.getClass()) {
            return false;
        }

        final GetObjectProfilePathParam that = (GetObjectProfilePathParam) o;

        return ! (pid != null ? ! pid.equals(that.pid) : that.pid != null);

    }

    @Override
    public int hashCode() {
        return pid != null ? pid.hashCode() : 0;
    }
}
