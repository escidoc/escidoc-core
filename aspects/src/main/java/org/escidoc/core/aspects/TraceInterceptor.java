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

package org.escidoc.core.aspects;

import org.aspectj.lang.JoinPoint.StaticPart;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.StringWriter;

@Aspect
public class TraceInterceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger("de.escidoc.core.trace");

    private static final int DEPTH_SPACES = 2;

    @Around("execution(public * de.escidoc.core..*.* (..))" + " && !within(org.escidoc.core.aspects..*)" +
            " && !within(de.escidoc.core.common.util.aop..*)" + " && if(" + "false" + ')')
    // enable this aspect only if you need to trace
    public Object traceMethod(final ProceedingJoinPoint joinPoint) throws Throwable {
        if(LOGGER.isDebugEnabled()) {
            final StaticPart staticPart = joinPoint.getStaticPart();
            final Signature signature = staticPart.getSignature();
            final String depthString = getDepthString();
            try {
                LOGGER.debug(createMessage(true, depthString, signature));
                final Object returnValue = joinPoint.proceed();
                LOGGER.debug(createMessage(false, depthString, signature));
                return returnValue;
            } catch(final Exception e) {
                LOGGER.debug(createExceptionMessage(depthString, e));
                throw e;
            }
        }
        return joinPoint.proceed();
    }

    private static String createMessage(final boolean in, final String depthString, final Signature signature) {
        final StringWriter inMessage = new StringWriter();
        inMessage.append('[').append(String.valueOf(Thread.currentThread().getId())).append(']');
        inMessage.append(depthString);
        if(in) {
            inMessage.append(">> ");
            TraceDepthThreadLocal.increaseDepth();
        } else {
            inMessage.append("<< ");
            TraceDepthThreadLocal.decreaseDepth();
        }
        inMessage.append(signature.getDeclaringType().getName()).append('.').append(signature.getName()).append("()");
        return inMessage.toString();
    }

    private static String createExceptionMessage(final String depthString, final Throwable t) {
        final StringWriter message = new StringWriter();
        message.append('[').append(String.valueOf(Thread.currentThread().getId())).append(']');
        message.append(depthString).append("XX ").append(t.getClass().getName());
        TraceDepthThreadLocal.decreaseDepth();
        return message.toString();
    }

    private static String getDepthString() {
        final StringWriter depthStringWriter = new StringWriter(TraceDepthThreadLocal.getDepth() * DEPTH_SPACES);
        for(int i = 0; i < TraceDepthThreadLocal.getDepth(); i++) {
            for(int j = 0; j < DEPTH_SPACES; j++) {
                depthStringWriter.append(' ');
            }
        }
        return depthStringWriter.toString();
    }

}
