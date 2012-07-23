package org.escidoc.core.business.domain.common;

import java.util.List;

import net.sf.oval.constraint.AssertFieldConstraints;
import net.sf.oval.constraint.NotNull;
import net.sf.oval.constraint.Size;
import net.sf.oval.guard.Guarded;

import org.escidoc.core.business.domain.base.DomainObject;
import org.escidoc.core.util.collections.CollectionFactory;

/**
 * @author Marko Voss (marko.voss@fiz-karlsruhe.de)
 * @author Michael Hoppe (michael.hoppe@fiz-karlsruhe.de)
 * TODO: Discuss: Existance of List-classes
 */
@Guarded(checkInvariants = true, inspectInterfaces = true)
public class MdRecordsDO implements DomainObject {

    @NotNull
    @Size(min = 1)  // TODO: constraint for default md-record "escidoc"? (EL or validation method)
    private List<MdRecordDO> list = CollectionFactory.getInstance().createList();

    @AssertFieldConstraints
    public List<MdRecordDO> getList() {
        return list;
    }

}
