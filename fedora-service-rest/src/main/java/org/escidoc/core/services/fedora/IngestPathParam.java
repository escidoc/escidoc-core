package org.escidoc.core.services.fedora;

import net.sf.oval.constraint.NotEmpty;
import net.sf.oval.guard.Guarded;

import javax.validation.constraints.NotNull;

/**
 * @author <a href="mailto:mail@eduard-hildebrandt.de">Eduard Hildebrandt</a>
 */
@Guarded(applyFieldConstraintsToConstructors = true, applyFieldConstraintsToSetters = true,
        assertParametersNotNull = false, checkInvariants=true, inspectInterfaces = true)
public final class IngestPathParam {

    @NotNull
    @NotEmpty
    private final String pid;

    public IngestPathParam(final String pid) {
        if(pid == null) {
            this.pid = "new";
        } else {
            this.pid = pid;
        }
    }

    public String getPid() {
        return this.pid;
    }

    @Override
    public boolean equals(final Object o) {
        if(this == o) {
            return true;
        }
        if(o == null || getClass() != o.getClass()) {
            return false;
        }

        final IngestPathParam that = (IngestPathParam) o;

        if(pid != null ? ! pid.equals(that.pid) : that.pid != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return pid != null ? pid.hashCode() : 0;
    }
}
