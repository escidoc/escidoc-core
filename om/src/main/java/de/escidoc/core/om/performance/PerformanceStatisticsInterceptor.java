package de.escidoc.core.om.performance;

/*
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the
 * Common Development and Distribution License, Version 1.0 only
 * (the "License").  You may not use this file except in compliance
 * with the License.
 *
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE
 * or http://www.escidoc.de/license.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each
 * file and include the License file at license/ESCIDOC.LICENSE.
 * If applicable, add the following below this CDDL HEADER, with the
 * fields enclosed by brackets "[]" replaced with your own identifying
 * information: Portions Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
 */

/*
 * Copyright 2006-2008 Fachinformationszentrum Karlsruhe Gesellschaft
 * fuer wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Foerderung der Wissenschaft e.V.
 * All rights reserved.  Use is subject to license terms.
 */

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is the interceptor being used for collecting runtime performance statistics. There are two possibilities
 * to view collected statistics: 1. The statistics bean collects all execution times and computes some basic desciptive
 * statistics. Theses statistics are available via JMX. 2. A specific logger for this class can be configured. The
 * logger should be set to level "TRACE" in order to get the log messages: <category
 * name="de.escidoc.core.om.performance.PerformanceStatisticsInterceptor" > <priority value="TRACE"/> </category>
 *
 * @author Kai Strnad
 */
public class PerformanceStatisticsInterceptor implements MethodInterceptor {

    /**
     * Logger for execution times.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(PerformanceStatisticsInterceptor.class);

    /**
     * The statistics.
     */
    private Statistics statistics;

    /**
     * Divisor for nanosecond to millisecond.
     */
    private static final long DIVISOR = 1000000L;

    /**
     * Set statistics bean.
     *
     * @param statistics the statistics bean
     */
    public final void setStatistics(final Statistics statistics) {
        this.statistics = statistics;
    }

    /**
     * Intercept at pointcut.
     *
     * @param invocation the MethodInvocation
     * @return the return value from invocation.
     * @throws Throwable t
     */
    @Override
    public final Object invoke(final MethodInvocation invocation) throws Throwable {
        final String className = invocation.getMethod().getDeclaringClass().getName();
        final String methodName = invocation.getMethod().getName();
        if (methodName.matches(".*addValueToStatistics")) {
            return invocation.proceed();
        }

        final String name = className + '.' + invocation.getMethod().getName();
        final long t1 = System.nanoTime();
        final Object rval = invocation.proceed();
        final long t2 = System.nanoTime();
        final long executionTime = (t2 - t1) / DIVISOR;
        LOGGER.trace(name + ' ' + executionTime);
        statistics.addValueToStatistics(name, executionTime);
        return rval;
    }

}
