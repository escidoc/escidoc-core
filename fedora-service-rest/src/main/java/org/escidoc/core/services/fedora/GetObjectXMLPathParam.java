package org.escidoc.core.services.fedora;

import net.sf.oval.constraint.AssertFieldConstraints;
import net.sf.oval.constraint.NotEmpty;
import net.sf.oval.guard.Guarded;

import javax.validation.constraints.NotNull;

/**
 * @author <a href="mailto:mail@eduard-hildebrandt.de">Eduard Hildebrandt</a>
 */
@Guarded(applyFieldConstraintsToConstructors = true, applyFieldConstraintsToSetters = true,
        assertParametersNotNull = true, checkInvariants=true, inspectInterfaces = true)
public final class GetObjectXMLPathParam {

    @NotNull
    @NotEmpty
    private String pid;

    public String getPid() {
        return pid;
    }

    public void setPid(@AssertFieldConstraints String pid) {
        this.pid = pid;
    }

}
