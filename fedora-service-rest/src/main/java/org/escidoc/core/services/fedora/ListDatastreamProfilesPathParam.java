/**
 * 
 */
package org.escidoc.core.services.fedora;

import javax.validation.constraints.NotNull;

import net.sf.oval.constraint.AssertFieldConstraints;
import net.sf.oval.constraint.NotEmpty;
import net.sf.oval.guard.Guarded;

/**
 * @author Marko Voß
 * 
 */
@Guarded(applyFieldConstraintsToConstructors = true, applyFieldConstraintsToSetters = true, assertParametersNotNull = false, checkInvariants = true, inspectInterfaces = true)
public class ListDatastreamProfilesPathParam {

    @NotNull
    @NotEmpty
    private final String pid;

    public ListDatastreamProfilesPathParam(@AssertFieldConstraints final String pid) {
        this.pid = pid;
    }

    public String getPid() {
        return pid;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final ListDatastreamProfilesPathParam that = (ListDatastreamProfilesPathParam) o;

        return !(pid != null ? !pid.equals(that.pid) : that.pid != null);

    }

    @Override
    public int hashCode() {
        return pid != null ? pid.hashCode() : 0;
    }
}
