package org.escidoc.core.business.domain.common;

import net.sf.oval.ConstraintViolation;
import net.sf.oval.Validator;
import net.sf.oval.constraint.*;
import net.sf.oval.guard.Guarded;
import org.escidoc.core.business.domain.base.DomainObject;
import org.escidoc.core.business.util.aspect.ValidationPattern;
import org.escidoc.core.utils.io.Stream;

import java.net.URI;
import java.util.List;

/**
 * @author Marko Voss (marko.voss@fiz-karlsruhe.de)
 */
@Guarded(checkInvariants = true)
public class MdRecordDO extends DomainObject {

    @NotNull
    private final Stream content;

    @NotNull
    @NotBlank
    @Length(max = 64)
    @MatchPattern(pattern = {ValidationPattern.NC_NAME})
    private final String name;

    @NotBlank
    private String mdType;    // TODO: Enum?

    @NotBlank
    private URI schema;

    private boolean inherited;

    public MdRecordDO(@AssertFieldConstraints final String name, @AssertFieldConstraints final Stream content) {
        this.name = name;
        this.content = content;
    }

    public void setMdType(@AssertFieldConstraints final String mdType) {
        this.mdType = mdType;
    }

    public void setSchema(@AssertFieldConstraints final URI schema) {
        this.schema = schema;
    }

    public void setInherited(@AssertFieldConstraints final boolean inherited) {
        this.inherited = inherited;
    }

    @AssertFieldConstraints
    public Stream getContent() {
        return content;
    }

    @AssertFieldConstraints
    public String getName() {
        return name;
    }

    @AssertFieldConstraints
    public String getMdType() {
        return mdType;
    }

    @AssertFieldConstraints
    public URI getSchema() {
        return schema;
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

        MdRecordDO that = (MdRecordDO) o;

        if (inherited != that.inherited) {
            return false;
        }
        if (!content.equals(that.content)) {
            return false;
        }
        if (mdType != null ? !mdType.equals(that.mdType) : that.mdType != null) {
            return false;
        }
        if (!name.equals(that.name)) {
            return false;
        }
        if (schema != null ? !schema.equals(that.schema) : that.schema != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = content.hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + (mdType != null ? mdType.hashCode() : 0);
        result = 31 * result + (schema != null ? schema.hashCode() : 0);
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
        sb.append(", name='").append(name).append('\'');
        sb.append(", mdType='").append(mdType).append('\'');
        sb.append(", schema=").append(schema);
        sb.append(", inherited=").append(inherited);
        sb.append('}');
        return sb;
    }

    /**
     * Test invalid name
     */
    public static void main(String[] args) {
        Validator v = new Validator();
        List<ConstraintViolation> violations = v.validate(new MdRecordDO("foo:bar", new Stream()));
        System.out.println(violations);
    }
}