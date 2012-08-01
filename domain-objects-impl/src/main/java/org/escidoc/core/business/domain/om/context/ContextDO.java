package org.escidoc.core.business.domain.om.context;

import net.sf.oval.constraint.NotBlank;
import net.sf.oval.constraint.NotNull;
import net.sf.oval.guard.Guarded;

import org.escidoc.core.business.domain.base.DomainObject;
import org.escidoc.core.business.domain.base.ID;

/**
 * @author Marko Voss (marko.voss@fiz-karlsruhe.de)
 */
@Guarded(checkInvariants = true)
public class ContextDO extends DomainObject {

    private ID id = null;

    /**
     * @return the id
     */
    public ID getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(ID id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ContextDO that = (ContextDO) o;

        if (id != that.id) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
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
        sb.append("ContextDO");
        sb.append("{id=").append(id);
        sb.append('}');
        return sb;
    }


}