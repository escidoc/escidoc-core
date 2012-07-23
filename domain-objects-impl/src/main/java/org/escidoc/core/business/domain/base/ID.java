package org.escidoc.core.business.domain.base;

import net.sf.oval.constraint.AssertFieldConstraints;
import net.sf.oval.constraint.NotBlank;
import net.sf.oval.constraint.NotNull;
import net.sf.oval.guard.Guarded;

/**
 * @author Marko Voss (marko.voss@fiz-karlsruhe.de)
 */
@Guarded(checkInvariants = true, inspectInterfaces = true)
public final class ID {

    @NotNull
    @NotBlank
    private final String value;

    public ID(@AssertFieldConstraints final String value) {
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

        ID id1 = (ID) o;

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
        return new StringBuilder("ID{value='").append(value).append("'}");
    }
}