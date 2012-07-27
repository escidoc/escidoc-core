package org.escidoc.core.business.domain.om.item;

import java.util.List;

import net.sf.oval.ConstraintViolation;
import net.sf.oval.Validator;
import net.sf.oval.constraint.AssertFieldConstraints;
import net.sf.oval.constraint.NotBlank;
import net.sf.oval.constraint.NotNull;
import net.sf.oval.guard.Guarded;

import org.escidoc.core.business.domain.base.DomainObject;
import org.escidoc.core.utils.io.Stream;

/**
 * @author Michael Hoppe (michael.hoppe@fiz-karlsruhe.de)
 */
@Guarded(checkInvariants = true, inspectInterfaces = true)
public class ContentModelSpecificDO implements DomainObject {

    @NotNull
    private final Stream content;

    private boolean inherited;

    public ContentModelSpecificDO(@AssertFieldConstraints final Stream content) {
        this.content = content;
    }

    public void setInherited(@AssertFieldConstraints final boolean inherited) {
        this.inherited = inherited;
    }

    @AssertFieldConstraints
    public Stream getContent() {
        return content;
    }

    @AssertFieldConstraints
    public boolean isInherited() {
        return inherited;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ContentModelSpecificDO that = (ContentModelSpecificDO) o;

        if (inherited != that.inherited) {
            return false;
        }
        if (!content.equals(that.content)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = content.hashCode();
        result = 31 * result + (inherited ? 1 : 0);
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
        sb.append("MdRecordDO");
        sb.append("{content=").append(content);
        sb.append(", inherited=").append(inherited);
        sb.append('}');
        return sb;
    }

    /**
     * Test invalid name
     */
    public static void main(String[] args) {
        Validator v = new Validator();
        List<ConstraintViolation> violations = v.validate(new ContentModelSpecificDO(new Stream()));
        System.out.println(violations);
    }
}