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
public final class GetDatastreamPathParam {

    @NotNull
    @NotEmpty
    private final String pid;

    @NotNull
    @NotEmpty
    private final String dsID;

    public GetDatastreamPathParam(@AssertFieldConstraints final String pid, @AssertFieldConstraints final String dsID) {
        this.pid = pid;
        this.dsID = dsID;
    }

    public String getPid() {
        return pid;
    }

    public String getDsID() {
        return dsID;
    }

    @Override
    public boolean equals(final Object o) {
        if(this == o) {
            return true;
        }
        if(o == null || getClass() != o.getClass()) {
            return false;
        }

        final GetDatastreamPathParam that = (GetDatastreamPathParam) o;

        if(dsID != null ? ! dsID.equals(that.dsID) : that.dsID != null) {
            return false;
        }
        return ! (pid != null ? ! pid.equals(that.pid) : that.pid != null);

    }

    @Override
    public int hashCode() {
        int result = pid != null ? pid.hashCode() : 0;
        result = 31 * result + (dsID != null ? dsID.hashCode() : 0);
        return result;
    }
}
