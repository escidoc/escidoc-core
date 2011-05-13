/*
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the Common Development and Distribution License, Version 1.0
 * only (the "License"). You may not use this file except in compliance with the License.
 *
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE or http://www.escidoc.de/license. See the License for
 * the specific language governing permissions and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each file and include the License file at
 * license/ESCIDOC.LICENSE. If applicable, add the following below this CDDL HEADER, with the fields enclosed by
 * brackets "[]" replaced with your own identifying information: Portions Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
 *
 * Copyright 2006-2011 Fachinformationszentrum Karlsruhe Gesellschaft fuer wissenschaftlich-technische Information mbH
 * and Max-Planck-Gesellschaft zur Foerderung der Wissenschaft e.V. All rights reserved. Use is subject to license
 * terms.
 */

package de.escidoc.core.common.util.aop;

import de.escidoc.core.common.exceptions.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.util.string.StringUtility;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.Ordered;

import java.lang.reflect.Method;

/**
 * AOP Interceptor used for checking that no method parameter is null.<p/>
 * <p/>
 * This Interceptor is invoked every time an service calls one of its classes.<br>
 *
 * @author Michael Schneider
 */
@Aspect
public class ParameterCheckInterceptor implements Ordered {

    /**
     * See Interface for functional description.
     */
    @Override
    public int getOrder() {
        return AopUtil.PRECEDENCE_PARAMETER_CHECK_INTERCEPTOR;
    }

    @Before("execution(public * de.escidoc.core.*.service.*.*(..))"
        + " && !execution(* de.escidoc.core..*.PolicyDecisionPoint*.evaluateRoles(..))"
        + " && !execution(* de.escidoc.core..*.PolicyDecisionPoint*.getRoleUserWhereClause(..))"
        + " && !execution(* de.escidoc.core..*.PolicyDecisionPoint*.findAttribute(..))"
        + " && (!execution(* de.escidoc.core..*.*HandlerInterface.retrieve*List(String))"
        + " || execution(* de.escidoc.core.aa..*.*HandlerInterface.retrieve*List(String)))"
        + " && !execution(* de.escidoc.core..*.Fedora*Handler*.*(..))")
    public void checkParameters(final JoinPoint joinPoint) throws MissingMethodParameterException {
        final Object[] arguments = joinPoint.getArgs();
        final int length = arguments.length;
        for (int i = 0; i < length; ++i) {
            if (arguments[i] == null || "".equals(arguments[i])) {
                final MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
                final Method calledMethod = methodSignature.getMethod();
                final String target = methodSignature.getDeclaringTypeName();
                throw new MissingMethodParameterException(StringUtility.format("The parameter at position " + (i + 1)
                    + " must be provided", target + '.' + calledMethod.getName()));
            }
        }
    }

}
