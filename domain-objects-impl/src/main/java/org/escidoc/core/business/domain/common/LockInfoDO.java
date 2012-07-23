package org.escidoc.core.business.domain.common;

import net.sf.oval.constraint.AssertFieldConstraints;
import net.sf.oval.constraint.NotBlank;
import net.sf.oval.constraint.NotNull;
import net.sf.oval.guard.Guarded;

import org.escidoc.core.business.domain.base.ID;
import org.escidoc.core.business.domain.base.LockStatus;
import org.joda.time.DateTime;

/**
 * @author Marko Voss (marko.voss@fiz-karlsruhe.de)
 */
@Guarded(checkInvariants = true, inspectInterfaces = true)
public final class LockInfoDO {

    @NotNull
    private final LockStatus status;

    @NotNull
    private final DateTime timestamp;

    @NotNull
    private final ID owner;

    /**
     * Constructor
     *
     * @param status The lock status of the resource.
     * @param timestamp The timestamp when the resource got locked.
     * @param owner The owner of this lock.
     */
    public LockInfoDO(@AssertFieldConstraints final LockStatus status,
        @AssertFieldConstraints final DateTime timestamp,
        @AssertFieldConstraints final ID owner) {

        this.status = status;
        this.timestamp = timestamp;
        this.owner = owner;
    }

    @AssertFieldConstraints
    public LockStatus getStatus() {
        return status;
    }

    @AssertFieldConstraints
    public DateTime getTimestamp() {
        return timestamp;
    }

    @AssertFieldConstraints
    public ID getOwner() {
        return owner;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        LockInfoDO that = (LockInfoDO) o;

        if (!timestamp.equals(that.timestamp)) {
            return false;
        }
        if (!owner.equals(that.owner)) {
            return false;
        }
        if (status != that.status) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = status.hashCode();
        result = 31 * result + timestamp.hashCode();
        result = 31 * result + owner.hashCode();
        return result;
    }

    @Override
    @NotNull
    @NotBlank
    public String toString() {
        return toStringBuilder().toString();
    }

    @NotNull
    public StringBuilder toStringBuilder() {
        return new StringBuilder("LockInfoDO{status=").append(status).append(", timestamp=").append(timestamp)
                                                      .append(", owner=").append(owner).append('}');
    }
}
