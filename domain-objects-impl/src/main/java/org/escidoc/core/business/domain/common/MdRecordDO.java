package org.escidoc.core.business.domain.common;

import java.net.URI;

import net.sf.oval.constraint.AssertFieldConstraints;
import net.sf.oval.constraint.Length;
import net.sf.oval.constraint.MatchPattern;
import net.sf.oval.constraint.NotBlank;
import net.sf.oval.constraint.NotNull;
import net.sf.oval.guard.Guarded;

import org.escidoc.core.business.domain.base.DomainObject;
import org.escidoc.core.business.util.aspect.ValidationPattern;
import org.escidoc.core.utils.io.Stream;

/**
 * @author Marko Voss (marko.voss@fiz-karlsruhe.de)
 * @author Michael Hoppe (michael.hoppe@fiz-karlsruhe.de)
 */
@Guarded(checkInvariants = true)
public class MdRecordDO extends DomainObject {

    @NotNull
    private Stream content;

    @NotNull
    @NotBlank
    @Length(max = 64)
    @MatchPattern(pattern = {ValidationPattern.NC_NAME})
    private String name;

    @NotBlank
    private String mdType;    // TODO: Enum?

    @NotBlank
    private URI schema;

    private Boolean inherited;

    public MdRecordDO(Builder builder) {
        this.name = builder.name;
        this.content = builder.content;
        this.mdType = builder.mdType;
        this.schema = builder.schema;
        this.inherited = builder.inherited;
    }

    /**
     * @return the content
     */
    @AssertFieldConstraints
    public Stream getContent() {
        return content;
    }

    /**
     * @param content the content to set
     */
    public void setContent(@AssertFieldConstraints Stream content) {
        this.content = content;
    }

    /**
     * @return the name
     */
    @AssertFieldConstraints
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(@AssertFieldConstraints String name) {
        this.name = name;
    }

    /**
     * @return the mdType
     */
    @AssertFieldConstraints
    public String getMdType() {
        return mdType;
    }

    /**
     * @param mdType the mdType to set
     */
    public void setMdType(@AssertFieldConstraints String mdType) {
        this.mdType = mdType;
    }

    /**
     * @return the schema
     */
    @AssertFieldConstraints
    public URI getSchema() {
        return schema;
    }

    /**
     * @param schema the schema to set
     */
    public void setSchema(@AssertFieldConstraints URI schema) {
        this.schema = schema;
    }

    /**
     * @return the inherited
     */
    @AssertFieldConstraints
    public Boolean getInherited() {
        return inherited;
    }

    /**
     * @param inherited the inherited to set
     */
    public void setInherited(@AssertFieldConstraints Boolean inherited) {
        this.inherited = inherited;
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
    
    public static class Builder {
        private Stream content = null;
        private String name = null;
        private String mdType = null;   
        private URI schema = null;
        private Boolean inherited = null;

        public Builder content(Stream content) {
            this.content = content;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder mdType(String mdType) {
            this.mdType = mdType;
            return this;
        }

        public Builder schema(URI schema) {
            this.schema = schema;
            return this;
        }

        public Builder inherited(Boolean inherited) {
            this.inherited = inherited;
            return this;
        }

        public MdRecordDO build() {
            return new MdRecordDO(this);
        }
    }

}