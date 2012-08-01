package org.escidoc.core.business.domain.common;

import net.sf.oval.constraint.AssertFieldConstraints;
import net.sf.oval.constraint.NotBlank;
import net.sf.oval.constraint.NotNegative;
import net.sf.oval.constraint.NotNull;
import net.sf.oval.guard.Guarded;

import org.escidoc.core.business.domain.base.ID;
import org.escidoc.core.business.domain.om.item.ItemStatus;
import org.joda.time.DateTime;

/**
 * @author Marko Voss (marko.voss@fiz-karlsruhe.de)
 */
@Guarded(checkInvariants = true)
public class VersionInfoDO {

    @NotNegative
    private final int versionNumber;

    //@NotNull
    private final DateTime timestamp;

    private StatusInfoDO<ItemStatus> versionStatusInfo;

    //@NotNull
    private ID modifiedBy;

    /**
     * A PID for a resource must not exist but is final here and can be <tt>null</tt>, because this version information
     * is an information about an existing version of a resource. If this resource is being changed and a new version
     * information is being created for the changed resource, it then may have a PID or not.
     */
    //xml: version-pid
    //@NotBlank
    private final String pid;

    /**
     * Constructor
     *
     * @param versionNumber The version number of this version.
     * @param timestamp The timestamp of this version.
     * @param pid The PID of this version if exists or <tt>null</tt> if no PID is available.
     */
    public VersionInfoDO(@AssertFieldConstraints final int versionNumber,
        @AssertFieldConstraints final DateTime timestamp, @AssertFieldConstraints final String pid) {

        this.versionNumber = versionNumber;
        this.timestamp = timestamp;
        this.pid = pid;
    }

    @AssertFieldConstraints
    public int getVersionNumber() {
        return versionNumber;
    }

    @AssertFieldConstraints
    public DateTime getTimestamp() {
        return timestamp;
    }

    @AssertFieldConstraints
    public String getPid() {
        return pid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        VersionInfoDO that = (VersionInfoDO) o;

        if (versionNumber != that.versionNumber) {
            return false;
        }
        if (pid != null ? !pid.equals(that.pid) : that.pid != null) {
            return false;
        }
        if (!timestamp.equals(that.timestamp)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = versionNumber;
        result = 31 * result + timestamp.hashCode();
        result = 31 * result + (pid != null ? pid.hashCode() : 0);
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
        final StringBuilder sb = new StringBuilder();
            sb.append("VersionInfoDO");
            sb.append("{versionNumber=").append(versionNumber);
            sb.append(", timestamp=").append(timestamp);
            sb.append(", pid='").append(pid).append('\'');
            sb.append('}');
        return sb;
    }
}