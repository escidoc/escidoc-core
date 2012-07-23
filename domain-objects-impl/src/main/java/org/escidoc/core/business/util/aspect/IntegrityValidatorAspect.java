package org.escidoc.core.business.util.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.escidoc.core.business.domain.base.DomainObject;
import org.escidoc.core.business.domain.base.ID;

import java.lang.reflect.Field;

/**
 * @author Marko Voss (marko.voss@fiz-karlsruhe.de)
 */
@Aspect
public class IntegrityValidatorAspect {

    @Around("call(* testHookForSemanticValidation(..))")
    public Object validate(final ProceedingJoinPoint joinPoint)
        throws Throwable {

        System.out.println("IntegrityValidatorAspect running...");

        for (Object obj : joinPoint.getArgs()) {

            if (obj instanceof DomainObject) {

                System.out.println("\n-------------------------------------");
                System.out.println("IntegrityValidatorAspect: Checking fields of class '" + obj.getClass().getName() + "'");

                Class<?> currentClazz = obj.getClass();

                while (currentClazz != null) {
                    for (Field field : currentClazz.getDeclaredFields()) {

                        if (ID.class.isAssignableFrom(field.getType())) {

                            field.setAccessible(true);
                            ID ref = (ID) field.get(obj);

                            System.out.println("Field: " + field.getName() + "; Value: " + ref);

                            // if (!PersistenceAPI.exists(ref.getId(), ref.getType())) {
                            //     throw new SystemIntegrityException("Resource '" + ref.getId() + "'" does not exist.);
                            // }
                        }
                    }
                    currentClazz = currentClazz.getSuperclass();
                }

                System.out.println("\nIntegrityValidatorAspect: DONE");
                System.out.println("-------------------------------------");
            }
        }

        return joinPoint.proceed();
    }
}
