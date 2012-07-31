package org.escidoc.core.business.domain.om.item;

import java.util.Set;

import net.sf.oval.constraint.AssertFieldConstraints;
import net.sf.oval.constraint.NotBlank;
import net.sf.oval.constraint.NotNull;
import net.sf.oval.guard.Guarded;

import org.escidoc.core.business.domain.base.DomainObject;
import org.escidoc.core.business.domain.base.ID;
import org.escidoc.core.business.domain.common.MdRecordDO;
import org.escidoc.core.business.domain.om.component.ComponentsDO;
import org.escidoc.core.util.collections.CollectionFactory;

/**
 * @author Marko Voss (marko.voss@fiz-karlsruhe.de)
 * @author Michael Hoppe (michael.hoppe@fiz-karlsruhe.de)
 */
@Guarded(checkInvariants = true, inspectInterfaces = true)
public final class ItemDO extends DomainObject {

	private ID id = null;

	@NotNull
    private ItemPropertiesDO properties = null;

    @NotNull
    private final ComponentsDO components = new ComponentsDO();

    @NotNull
    private Set<MdRecordDO> mdRecords = CollectionFactory.getInstance().createSet();

    @NotNull
    private Set<RelationDO> relations = CollectionFactory.getInstance().createSet();

    @AssertFieldConstraints
	public void setId(ID id) {
		this.id = id;
	}

    @AssertFieldConstraints
	public void setProperties(ItemPropertiesDO properties) {
		this.properties = properties;
	}

    @AssertFieldConstraints
	public ID getId() {
		return id;
	}

    @AssertFieldConstraints
    public ItemPropertiesDO getProperties() {
        return properties;
    }

    @AssertFieldConstraints
    public ComponentsDO getComponents() {
        return components;
    }

    @AssertFieldConstraints
    public Set<MdRecordDO> getMdRecords() {
        return mdRecords;
    }

    @AssertFieldConstraints
	public Set<RelationDO> getRelations() {
		return relations;
	}

    @AssertFieldConstraints
	public void setRelations(Set<RelationDO> relations) {
		this.relations = relations;
	}

    @AssertFieldConstraints
	public void setMdRecords(Set<MdRecordDO> mdRecords) {
		this.mdRecords = mdRecords;
	}

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ItemDO itemDO = (ItemDO) o;

        if (!components.equals(itemDO.components)) {
            return false;
        }
        if (!mdRecords.equals(itemDO.mdRecords)) {
            return false;
        }
        if (!properties.equals(itemDO.properties)) {
            return false;
        }
        if (!relations.equals(itemDO.relations)) {
            return false;
        }
        if (!id.equals(itemDO.id)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = properties.hashCode();
        result = 31 * result + components.hashCode();
        result = 31 * result + mdRecords.hashCode();
        result = 31 * result + relations.hashCode();
        result = 31 * result + id.hashCode();
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
        sb.append("ItemDO");
        sb.append("{properties=").append(properties);
        sb.append(", components=").append(components);
        sb.append(", mdRecords=").append(mdRecords);
        sb.append(", relations=").append(relations);
        sb.append(", id=").append(id);
        sb.append('}');
        return sb;
    }
}