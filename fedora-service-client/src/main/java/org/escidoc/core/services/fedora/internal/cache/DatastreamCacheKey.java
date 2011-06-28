package org.escidoc.core.services.fedora.internal.cache;

import net.sf.oval.constraint.AssertFieldConstraints;
import net.sf.oval.constraint.NotEmpty;
import net.sf.oval.guard.Guarded;
import org.joda.time.DateTime;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Cache key for datastreams.
 *
 * @author <a href="mailto:mail@eduard-hildebrandt.de">Eduard Hildebrandt</a>
 */
@Guarded(applyFieldConstraintsToConstructors = true, applyFieldConstraintsToSetters = true,
        assertParametersNotNull = false, checkInvariants = true, inspectInterfaces = true)
public final class DatastreamCacheKey implements Serializable {

    private static final long serialVersionUID = 4334788048803076431L;
    @NotNull
    @NotEmpty
    private final String pid;

    private final String dsID;

    private final DateTime timestamp;

    public DatastreamCacheKey(@AssertFieldConstraints final String pid, @AssertFieldConstraints final String dsID,
                              @AssertFieldConstraints final DateTime timestamp) {
        this.pid = pid;
        this.dsID = dsID;
        this.timestamp = timestamp;
    }

    public String getPid() {
        return pid;
    }

    public String getDsID() {
        return dsID;
    }

    public DateTime getTimestamp() {
        return timestamp;
    }

    @Override
    public boolean equals(final Object o) {
        if(this == o) {
            return true;
        }
        if(o == null || getClass() != o.getClass()) {
            return false;
        }
        final DatastreamCacheKey that = (DatastreamCacheKey) o;
        if(dsID != null ? ! dsID.equals(that.dsID) : that.dsID != null) {
            return false;
        }
        if(! pid.equals(that.pid)) {
            return false;
        }
        return ! (timestamp != null ? ! timestamp.equals(that.timestamp) : that.timestamp != null);
    }

    @Override
    public int hashCode() {
        int result = pid.hashCode();
        result = 31 * result + (dsID != null ? dsID.hashCode() : 0);
        result = 31 * result + (timestamp != null ? timestamp.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "DatastreamCacheKey{" + "pid='" + pid + '\'' + ", dsID='" + dsID + '\'' + ", timestamp=" + timestamp +
                '}';
    }
}
