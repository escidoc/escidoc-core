package org.escidoc.core.business.domain.common;

import net.sf.oval.constraint.AssertFieldConstraints;
import net.sf.oval.constraint.NotBlank;
import net.sf.oval.constraint.NotNull;

import org.escidoc.core.business.domain.base.AbstractBuilder;
import org.escidoc.core.business.domain.base.DomainObject;
import org.escidoc.core.business.domain.base.ID;
import org.escidoc.core.business.domain.base.LockStatus;
import org.escidoc.core.business.util.annotation.Validate;
import org.joda.time.DateTime;

/**
 * @author Marko Voss (marko.voss@fiz-karlsruhe.de)
 */
@Validate
public final class LockInfoDO extends DomainObject {

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
    private LockInfoDO(Builder builder) {
        super(builder.validationProfile);
        this.status = builder.status;
        this.timestamp = builder.timestamp;
        this.owner = builder.owner;
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

    public static class Builder extends AbstractBuilder {
        private LockStatus status = null;

        private DateTime timestamp = null;

        private ID owner = null;

        public Builder(String validationProfile) {
            super(validationProfile);
        }
        
        public Builder status(LockStatus status) {
            this.status = status;
            return this;
        }

        public Builder timestamp(DateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Builder owner(ID owner) {
            this.owner = owner;
            return this;
        }

        public LockInfoDO build() {
            return new LockInfoDO(this);
        }
        
    }
}
