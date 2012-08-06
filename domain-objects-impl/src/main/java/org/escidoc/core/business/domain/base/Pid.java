package org.escidoc.core.business.domain.base;

import net.sf.oval.constraint.AssertFieldConstraints;
import net.sf.oval.constraint.NotBlank;
import net.sf.oval.constraint.NotNull;
import net.sf.oval.guard.Guarded;

/**
 * Class encapsulates Pid.
 * 
 * @author Michael Hoppe (michael.hoppe@fiz-karlsruhe.de)
 */
@Guarded(checkInvariants = true, inspectInterfaces = true)
public final class Pid {

    @NotNull
    @NotBlank
    private final String value;

    public Pid(@AssertFieldConstraints final String value) {
        this.value = value;
    }

    @AssertFieldConstraints
    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Pid id1 = (Pid) o;

        return value.equals(id1.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    @NotNull
    @NotBlank
    public String toString() {
        return toStringBuilder().toString();
    }

    @NotNull
    public StringBuilder toStringBuilder() {
        return new StringBuilder("Pid{value='").append(value).append("'}");
    }
}