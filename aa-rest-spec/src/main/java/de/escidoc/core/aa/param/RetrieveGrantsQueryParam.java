package de.escidoc.core.aa.param;

import net.sf.oval.guard.Guarded;
import org.escidoc.core.domain.sru.parameters.SruSearchRequestParametersBean;

/**
 * @author Marko Voss (marko.voss@fiz-karlsruhe.de)
 */
@Guarded(applyFieldConstraintsToConstructors = true, applyFieldConstraintsToSetters = true, assertParametersNotNull = false, checkInvariants = true, inspectInterfaces = true)
public class RetrieveGrantsQueryParam extends SruSearchRequestParametersBean {
}
