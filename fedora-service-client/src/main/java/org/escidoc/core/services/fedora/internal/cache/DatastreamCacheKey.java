package org.escidoc.core.services.fedora.internal.cache;

import java.io.Serializable;

/**
 * Cache key for datastreams.
 *
 * @author <a href="mailto:mail@eduard-hildebrandt.de">Eduard Hildebrandt</a>
 */
public final class DatastreamCacheKey implements Serializable {

    private String pid;
    private String dsID;

    public DatastreamCacheKey(final String pid, final String dsID) {
        this.pid = pid;
        this.dsID = dsID;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(final String pid) {
        this.pid = pid;
    }

    public String getDsID() {
        return dsID;
    }

    public void setDsID(final String dsID) {
        this.dsID = dsID;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final DatastreamCacheKey that = (DatastreamCacheKey) o;

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
