package org.escidoc.core.business.domain.om.component;

import java.net.URI;

import net.sf.oval.constraint.AssertFieldConstraints;
import net.sf.oval.constraint.NotBlank;
import net.sf.oval.constraint.NotNull;
import net.sf.oval.guard.Guarded;

import org.escidoc.core.business.domain.base.DomainObject;

/**
 * @author Michael Hoppe (michael.hoppe@fiz-karlsruhe.de)
 */
@Guarded(checkInvariants = true)
public class ContentDO extends DomainObject {
    @NotNull
    private URI location;

    private Storage storage;
    
    public ContentDO(Builder builder) {
        this.location = builder.location;
        this.storage = builder.storage;
    }
    
    /**
     * @return the location
     */
    @AssertFieldConstraints
    public URI getLocation() {
        return location;
    }

    /**
     * @param location the location to set
     */
    public void setLocation(@AssertFieldConstraints URI location) {
        this.location = location;
    }

    /**
     * @return the storage
     */
    @AssertFieldConstraints
    public Storage getStorage() {
        return storage;
    }

    /**
     * @param storage the storage to set
     */
    public void setStorage(@AssertFieldConstraints Storage storage) {
        this.storage = storage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ContentDO contentDO = (ContentDO) o;

        if (!location.equals(contentDO.location)) {
            return false;
        }
        if (!storage.equals(contentDO.storage)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = location.hashCode();
        result = 31 * result + storage.hashCode();
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
        sb.append("ContentDO");
        sb.append("{location=").append(location);
        sb.append(", storage=").append(storage);
        sb.append('}');
        return sb;
    }
    
    public static class Builder {
        private URI location = null;

        private Storage storage = null;

        public Builder location(URI location) {
            this.location = location;
            return this;
        }

        public Builder storage(Storage storage) {
            this.storage = storage;
            return this;
        }

        public ContentDO build() {
            return new ContentDO(this);
        }
        
    }
}
