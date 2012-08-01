package org.escidoc.core.business.domain.om.context;

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


}