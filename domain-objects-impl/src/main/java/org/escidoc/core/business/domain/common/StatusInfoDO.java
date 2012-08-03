package org.escidoc.core.business.domain.common;

import net.sf.oval.constraint.AssertFieldConstraints;
import net.sf.oval.constraint.NotBlank;
import net.sf.oval.constraint.NotNull;

import org.escidoc.core.business.domain.base.AbstractBuilder;
import org.escidoc.core.business.domain.base.DomainObject;
import org.escidoc.core.business.util.annotation.Validate;

/**
 * @author Marko Voss (marko.voss@fiz-karlsruhe.de)
 * @author Michael Hoppe (michael.hoppe@fiz-karlsruhe.de)
 */
@Validate
public final class StatusInfoDO<T extends Enum<?>> extends DomainObject {

	@NotNull
	private T objectStatus;

	@NotNull
	@NotBlank
	// comment needed? only in case of withdrawn?
	private String objectStatusComment;

	/**
	 * Constructor
	 * 
	 * @param objectStatus
	 *            The public status of the resource.
	 * @param objectStatusComment
	 *            The comment about this status.
	 */
	private StatusInfoDO(Builder<T> builder) {
	    super(builder.validationProfile);
		this.objectStatus = builder.objectStatus;
		this.objectStatusComment = builder.objectStatusComment;
	}

	@AssertFieldConstraints
	public T getObjectStatus() {
		return objectStatus;
	}

	@AssertFieldConstraints
	public String getObjectStatusComment() {
		return objectStatusComment;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		StatusInfoDO<T> that = (StatusInfoDO<T>) o;

		if (!objectStatusComment.equals(that.objectStatusComment)) {
			return false;
		}
		if (objectStatus != that.objectStatus) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		int result = objectStatus.hashCode();
		result = 31 * result + objectStatusComment.hashCode();
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
		return new StringBuilder("StatusInfoDO{objectStatus=").append(
				objectStatus).append(", objectStatusComment='").append(
				objectStatusComment).append("'}");
	}

    public static class Builder<T extends Enum<?>> extends AbstractBuilder {
        private T objectStatus = null;

        private String objectStatusComment = null;

        public Builder(String validationProfile) {
            super(validationProfile);
        }
        
        public Builder<T> objectStatus(T objectStatus) {
            this.objectStatus = objectStatus;
            return this;
        }

        public Builder<T> objectStatusComment(String objectStatusComment) {
            this.objectStatusComment = objectStatusComment;
            return this;
        }

        public StatusInfoDO<T> build() {
            return new StatusInfoDO<T>(this);
        }
        
    }
}