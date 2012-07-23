package org.escidoc.core.business.util.aspect;

import net.sf.oval.guard.GuardAspect2;
import org.aspectj.lang.annotation.Aspect;

/**
 * Activate aspect-oriented OVal validation.
 *
 * @author Marko Voss (marko.voss@fiz-karlsruhe.de)
 */
@Aspect
public class ValidationAspect extends GuardAspect2 {

    public ValidationAspect() {
        super();
        // invert activation logic for custom profiles:
        getGuard().disableAllProfiles();
        getGuard().enableProfile("default");
    }
}
