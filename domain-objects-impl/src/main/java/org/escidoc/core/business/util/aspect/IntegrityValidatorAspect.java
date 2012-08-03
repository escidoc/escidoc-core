package org.escidoc.core.business.util.aspect;

import java.util.List;

import net.sf.oval.ConstraintViolation;
import net.sf.oval.Validator;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.escidoc.core.business.domain.base.DomainObject;
import org.escidoc.core.business.util.annotation.Validate;

import de.escidoc.core.common.exceptions.system.SystemException;

/**
 * @author Michael Hoppe (michael.hoppe@fiz-karlsruhe.de)
 */
@Aspect
public class IntegrityValidatorAspect {
    @After("execution(org.escidoc.core.business.domain..*DO.new(*))"
        + " || execution(* org.escidoc.core.business.domain..*DO.set*(*))")
    public void validate(final JoinPoint joinPoint)
        throws Throwable {
        if (joinPoint.getThis().getClass().getAnnotation(Validate.class) != null) {
            DomainObject domainObject = (DomainObject)joinPoint.getThis();
            Validator v = new Validator(); 

            v.disableAllProfiles();
            v.enableProfile("default");
            v.enableProfile(domainObject.getValidationProfile());
            
            List<ConstraintViolation> violations = v.validate(joinPoint.getThis());
            if(!violations.isEmpty())
            {
               StringBuilder errors = new StringBuilder();
               for (ConstraintViolation violation : violations) {
                   errors.append(violation.getMessage()).append("\n");
               }
               throw new SystemException(errors.toString());
            }
        }
    }

}
