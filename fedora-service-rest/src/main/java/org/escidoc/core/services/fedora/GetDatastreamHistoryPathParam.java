/**
 * 
 */
package org.escidoc.core.services.fedora;

import javax.validation.constraints.NotNull;

import net.sf.oval.constraint.NotEmpty;
import net.sf.oval.guard.Guarded;

/**
 * @author Marko Voß
 * 
 */
@Guarded(applyFieldConstraintsToConstructors = true, applyFieldConstraintsToSetters = true,
    assertParametersNotNull = false, checkInvariants = true, inspectInterfaces = true)
public final class GetDatastreamHistoryPathParam {

    @NotNull
    @NotEmpty
    private final String pid;

    @NotNull
    @NotEmpty
    private final String dsID;

    /**
     * @param pid
     * @param dsID
     */
    public GetDatastreamHistoryPathParam(final String pid, final String dsID) {
        super();
        this.pid = pid;
        this.dsID = dsID;
    }

    /**
     * @return the pid
     */
    public String getPid() {
        return pid;
    }

    /**
     * @return the dsID
     */
    public String getDsID() {
        return dsID;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final GetDatastreamHistoryPathParam that = (GetDatastreamHistoryPathParam) o;

        if (dsID != null ? !dsID.equals(that.dsID) : that.dsID != null) {
            return false;
        }
        if (pid != null ? !pid.equals(that.pid) : that.pid != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = pid != null ? pid.hashCode() : 0;
        result = 31 * result + (dsID != null ? dsID.hashCode() : 0);
        return result;
    }
}
