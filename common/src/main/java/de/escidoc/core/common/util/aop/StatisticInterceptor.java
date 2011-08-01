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

import de.escidoc.core.common.business.Constants;
import de.escidoc.core.common.exceptions.EscidocException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.util.service.UserContext;
import de.escidoc.core.common.util.string.StringUtility;
import de.escidoc.core.statistic.StatisticRecord;
import de.escidoc.core.statistic.StatisticRecordBuilder;
import de.escidoc.core.statistic.StatisticService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;

import java.util.regex.Pattern;

/**
 * Interceptor used to create statistic data for the eSciDoc base services.
 * <p/>
 * <p/>
 * This Interceptor is invoked every time an service calls one of its classes.<br/> It must be the first interceptor in
 * the chain.<br/> This interceptor performs user authentication, too, as the user data is needed for the
 * statistics.<br/> This interceptor stores in the thread local {@code StatisticDataVo} object the following
 * information before calling the handler method: <ul> <li>{@code PARAM_HANDLER}, the name of the called
 * handler.</li> <li>{@code PARAM_REQUEST}, the name of the called handler method.</li>
 * <li>{@code PARAM_INTERNAL}, the flag indicating if this is an internal call from one infrastructure service to
 * another ( {@code VALUE_INTERNAL_TRUE}), or if it is an external call from a non-infrastructure service or
 * application ({@code VALUE_INTERNAL_FALSE} ).</li> <li>{@code PARAM_USER_ID}, the id of the user performing
 * the current request.</li> <li>{@code PARAM_OBJECT_ID}, the id of the accessed resource, if this is available in
 * the first parameter. To check this, it is asserted that the first parameter does not seem to contain XML data, i.e.
 * does not contain a &lt;</li> </ul> After the handler method call, the interceptor adds the following information and
 * sends the statistics data to the {@code StatisticQueueHandler}. <ul> <li>{@code PARAM_SUCCESS}, flag
 * indicating if the method call has successfully returned ({@code VALUE_SUCCESS_TRUE}) or if an exception has
 * occurred ({@code VALUE_SUCCESS_FALSE}).</li> <li>{@code PARAM_ELAPSED_TIME}, the elapsed time from start of
 * this interceptor to the returning from the called method.</li> <li>{@code PARAM_EXCEPTION_NAME}, in case of an
 * error, the name of the exception.</li> <li>{@code PARAM_EXCEPTION_MESSAGE}, in case of an error, the message of
 * the exception.</li> <li>{@code PARAM_EXCEPTION_SOURCE}, in case of an error, the source of the top level
 * exception. This source information consist of the full class name, the method name and the line from that the
 * exception has been thrown.</li> </ul> <br/> The called business methods may add further information to the statistic
 * data record. For information about how to access the thread local statistic data record and adding information to
 * it.
 *
 * @author Torsten Tetteroo
 */
@Aspect
public class StatisticInterceptor implements Ordered {

    private static final Logger LOGGER = LoggerFactory.getLogger(StatisticInterceptor.class);

    /**
     * Pattern used to determine that a method parameter is XML parameter and not an id parameter.
     */
    private static final Pattern PATTERN_DETERMINE_XML_PARAMETER = Pattern.compile("<");

    private static final String MSG_CLASS_CAST_EXCEPTION =
        "This ClassCastException should occur for binary content, only.";

    private static final String PARAM_ELAPSED_TIME = "elapsed_time";

    private static final String PARAM_EXCEPTION_NAME = "exception_name";

    private static final String PARAM_EXCEPTION_SOURCE = "exception_source";

    private static final String PARAM_HANDLER = "handler";

    private static final String PARAM_INTERNAL = "internal";

    private static final String PARAM_INTERFACE = "interface";

    private static final String PARAM_OBJID = "object_id";

    private static final String PARAM_PARENT_OBJID = "parent_" + PARAM_OBJID;

    private static final String PARAM_REQUEST = "request";

    private static final String PARAM_SUCCESSFUL = "successful";

    private static final String PARAM_USER_ID = "user_id";

    private static final String VALUE_INTERFACE_REST = "REST";

    private StatisticService statisticService;

    /**
     * See Interface for functional description.
     */
    @Override
    public int getOrder() {
        return AopUtil.PRECEDENCE_STATISTIC_INTERCEPTOR;
    }

    /**
     * Around advice to create a statistic record for the current method call.<br>
     *
     * @param joinPoint The current {@link ProceedingJoinPoint}.
     * @return Returns the changed result.
     * @throws Throwable Thrown in case of an error.
     */
    @Around("execution(public * de.escidoc.core.*.service.*.*(..))"
        + " && !within(de.escidoc.core.aa.service.EscidocUserDetailsService)"
        + " && !execution(* de.escidoc.core..*.SemanticStoreHandler*.*(..))"
        + " && !execution(* de.escidoc.core..*.StatisticService*.*(..))"
        + " && !execution(* de.escidoc.core.common..*.*(..))")
    // enable this aspect only if you need
    public Object createStatisticRecord(final ProceedingJoinPoint joinPoint) throws Throwable {
        final long invocationStartTime = System.currentTimeMillis();
        boolean successful = true;
        boolean internal = false;
        String exceptionName = null;
        String exceptionSource = null;
        try {
            // insert internal (0)/external (1) info
            if (!UserContext.isExternalUser()) {
                internal = true;
            }
            return proceed(joinPoint);
        }
        catch (final Exception e) {
            successful = false;
            exceptionName = e.getClass().getName();
            final StackTraceElement[] elements = e.getStackTrace();
            if (elements != null && elements.length > 0) {
                final StackTraceElement element = elements[0];
                exceptionSource =
                    StringUtility.format(element.getClassName(), element.getMethodName(), element.getLineNumber());
            }
            else {
                exceptionSource = Constants.UNKNOWN;
            }
            if (e instanceof EscidocException) {
                throw e;
            }
            else {
                // this should not occur. To report this failure, the exception is wrapped by a SystemException
                throw new SystemException("Service throws unexpected exception. ", e);
            }
        }
        finally {
            // get callee and method info
            final MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
            // get interface info
            // create a new statistic data record stored in thread local for this scope
            final StatisticRecordBuilder statisticRecordBuilder = StatisticRecordBuilder.createStatisticRecord();
            handleObjectIds(statisticRecordBuilder, methodSignature.getMethod().getName(), joinPoint.getArgs());
            final String interfaceInfo = VALUE_INTERFACE_REST;
            final StatisticRecord statisticRecord =
                statisticRecordBuilder
                    .withParameter(
                        PARAM_HANDLER,
                        methodSignature.getDeclaringTypeName().replaceAll("\\.interfaces", "").replaceAll("Interface$",
                            ""))
                    .withParameter(PARAM_REQUEST, methodSignature.getMethod().getName())
                    .withParameter(PARAM_INTERFACE, interfaceInfo)
                    .withParameter(PARAM_INTERNAL, internal)
                    .withParameter(PARAM_SUCCESSFUL, successful)
                    .withParameter(PARAM_EXCEPTION_NAME, exceptionName)
                    .withParameter(PARAM_EXCEPTION_SOURCE, exceptionSource)
                    .withParameter(PARAM_USER_ID, UserContext.getId())
                    .withParameter(PARAM_ELAPSED_TIME, String.valueOf(System.currentTimeMillis() - invocationStartTime))
                    .build();
            this.statisticService.createStatisticRecord(statisticRecord);
        }
    }

    /**
     * Continue the invocation.
     *
     * @param joinPoint The current {@link ProceedingJoinPoint}.
     * @return Returns the result of the continued invocation.
     * @throws Throwable Thrown in case of an error during proceeding the method call.
     */
    private static Object proceed(final ProceedingJoinPoint joinPoint) throws Throwable {
        return joinPoint.proceed();
    }

    /**
     * Inserts the method parameter that hold object ids into the provided {@link StatisticRecord} object.<br> The
     * objids are taken from the method parameters, that are string parameters, contain a :, but do not seem to be XML
     * data, i.e. does not contain '<'. <br> The last found objid is logged as PARAM_OBJID, because this addresses the
     * accessed (sub- )resource, e.g. item or component of an item. The other object ids (if any) are logged as
     * PARAM_PARENT_OBJID + index.
     *
     * @param statisticRecordBuilder The {@link StatisticRecordBuilder} object to put the object ids into.
     * @param calledMethodName       The name of the called method.
     * @param arguments              The arguments of the method call.
     */
    private static void handleObjectIds(
        final StatisticRecordBuilder statisticRecordBuilder, final String calledMethodName, final Object[] arguments) {

        if (arguments != null && arguments.length > 0) {
            int indexLastObjid = -1;
            for (int i = 0; i < arguments.length; i++) {
                if (!(arguments[i] instanceof String)) {
                    // e.g., this is the case for binary content
                    // (createStagingFile)
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug(MSG_CLASS_CAST_EXCEPTION + calledMethodName);
                    }
                    // Parameter found that is not a string. In this case, the
                    // loop is stopped and no objids are logged, as it seems to
                    // be a special method, e.g. evaluateRetrieve of the PDP.
                    indexLastObjid = -1;
                    break;
                }
                final CharSequence argument = (CharSequence) arguments[i];
                if (argument != null && !PATTERN_DETERMINE_XML_PARAMETER.matcher(argument).find()) {
                    indexLastObjid = i;
                }
                else {
                    // First string parameter found that holds xml. Suspend
                    // loop;
                    break;
                }
            }
            if (indexLastObjid >= 0) {
                statisticRecordBuilder.withParameter(PARAM_OBJID, (String) arguments[indexLastObjid]);
                for (int i = indexLastObjid - 1, parent = 1; i >= 0; i--) {
                    statisticRecordBuilder.withParameter(PARAM_PARENT_OBJID + parent, (String) arguments[i]);
                    parent++;
                }
            }
        }
    }

    public void setStatisticService(final StatisticService statisticService) {
        this.statisticService = statisticService;
    }
}
