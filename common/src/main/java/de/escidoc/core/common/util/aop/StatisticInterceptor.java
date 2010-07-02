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
package de.escidoc.core.common.util.aop;

import java.lang.reflect.Method;
import java.util.regex.Pattern;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.Ordered;

import de.escidoc.core.common.business.queue.StatisticQueueHandler;
import de.escidoc.core.common.business.queue.vo.StatisticDataVo;
import de.escidoc.core.common.exceptions.EscidocException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.util.logger.AppLogger;
import de.escidoc.core.common.util.service.UserContext;
import de.escidoc.core.common.util.string.StringUtility;

/**
 * Interceptor used to create statistic data for the eSciDoc base services.
 * <p/>
 * 
 * This Interceptor is invoked every time an EJB calls one of its service
 * classes.<br/>
 * It must be the first interceptor in the chain.<br/>
 * This interceptor performs user authentication, too, as the user data is
 * needed for the statistics.<br/>
 * This interceptor stores in the thread local <code>StatisticDataVo</code>
 * object the following information before calling the handler method:
 * <ul>
 * <li><code>PARAM_HANDLER</code>, the name of the called handler.</li>
 * <li><code>PARAM_REQUEST</code>, the name of the called handler method.</li>
 * <li><code>PARAM_INTERFACE</code>, the interface that has been called, i.e.
 * REST or SOAP.</li>
 * <li><code>PARAM_INTERNAL</code>, the flag indicating if this is an internal
 * call from one infrastructure service (EJB) to another (
 * <code>VALUE_INTERNAL_TRUE</code>), or if it is an external call from a
 * non-infrastructure service or application (<code>VALUE_INTERNAL_FALSE</code>
 * ).</li>
 * <li><code>PARAM_USER_ID</code>, the id of the user performing the current
 * request.</li>
 * <li><code>PARAM_OBJECT_ID</code>, the id of the accessed resource, if this is
 * available in the first parameter. To check this, it is asserted that the
 * first parameter does not seem to contain XML data, i.e. does not contain a
 * &lt;</li>
 * </ul>
 * After the handler method call, the interceptor adds the following information
 * and sends the statistics data to the <code>StatisticQueueHandler</code>.
 * <ul>
 * <li><code>PARAM_SUCCESS</code>, flag indicating if the method call has
 * successfully returned (<code>VALUE_SUCCESS_TRUE</code>) or if an exception
 * has occurred (<code>VALUE_SUCCESS_FALSE</code>).</li>
 * <li><code>PARAM_ELAPSED_TIME</code>, the elapsed time from start of this
 * interceptor to the returning from the called method.</li>
 * <li><code>PARAM_EXCEPTION_NAME</code>, in case of an error, the name of the
 * exception.</li>
 * <li><code>PARAM_EXCEPTION_MESSAGE</code>, in case of an error, the message of
 * the exception.</li>
 * <li><code>PARAM_EXCEPTION_SOURCE</code>, in case of an error, the source of
 * the top level exception. This source information consist of the full class
 * name, the method name and the line from that the exception has been thrown.</li>
 * </ul>
 * <br/>
 * The called business methods may add further information to the statistic data
 * record. For information about how to access the thread local statistic data
 * record and adding information to it,
 * 
 * @see de.escidoc.core.common.business.queue.vo.StatisticDataVo
 * 
 * @spring.bean id="common.StatisticInterceptor" factory-method="aspectOf"
 *              lazy-init="false"
 * 
 * @author TTE
 * @common
 */
@Aspect
public class StatisticInterceptor implements Ordered {

    /**
     * Pattern used to determine that a method parameter is XML parameter and
     * not an id parameter.
     */
    private static final Pattern PATTERN_DETERMINE_XML_PARAMETER =
        Pattern.compile("<");

    private static final String MSG_CLASS_CAST_EXCEPTION =
        "This ClassCastException should occur for binary content, only.";

    /**
     * The logger.
     */
    private static final AppLogger LOG =
        new AppLogger(StatisticInterceptor.class.getName());

    private static final String PARAM_ELAPSED_TIME = "elapsed_time";

    private static final String PARAM_EXCEPTION_MESSAGE = "exception_message";

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

    private static final String VALUE_FALSE = "0";

    private static final String VALUE_TRUE = "1";

    private static final String VALUE_INTERFACE_SOAP = "SOAP";

    private static final String VALUE_INTERFACE_REST = "REST";

    private static final String VALUE_INTERNAL_FALSE = VALUE_FALSE;

    private static final String VALUE_INTERNAL_TRUE = VALUE_TRUE;

    private static final String VALUE_SUCCESS_FALSE = VALUE_FALSE;

    private static final String VALUE_SUCCESS_TRUE = VALUE_TRUE;

    private StatisticQueueHandler statisticQueueHandler;

    // CHECKSTYLE:JAVADOC-OFF

    /**
     * See Interface for functional description.
     * 
     * @return
     * @see org.springframework.core.Ordered#getOrder()
     * @common
     */
    public int getOrder() {

        return AopUtil.PRECEDENCE_STATISTIC_INTERCEPTOR;
    }

    // CHECKSTYLE:JAVADOC-ON

    /**
     * Around advice to create a statistic record for the current method call.<br>
     * 
     * @param joinPoint
     *            The current {@link ProceedingJoinPoint}.
     * @return Returns the changed result.
     * @throws Throwable
     *             Thrown in case of an error.
     * @common
     */
    @Around("call(public !static * de.escidoc.core.*.service.interfaces.*.*(..))"
        + " && within(de.escidoc.core.*.ejb.*Bean)"
        + " && !call(* de.escidoc.core..*.SemanticStoreHandler*.*(..))"
        + " && !call(* de.escidoc.core..*.StatisticDataHandler*.*(..))"
        + " && !call(* de.escidoc.core.common..*.*(..))")
    public Object createStatisticRecord(final ProceedingJoinPoint joinPoint)
        throws Throwable {

        if (LOG.isDebugEnabled()) {
            LOG.debug(StringUtility.concatenateWithBracketsToString(
                "createStatisticRecord", this));
        }

        long invocationStartTime = System.currentTimeMillis();
        StatisticDataVo callersStatisticData = null;
        StatisticDataVo statisticData = null;
        try {

            // save the statistic data record of the caller. This one can be
            // set in case of executing the EJB in the same thread as the caller
            // in case of VM local EJB calls.
            callersStatisticData = StatisticDataVo.getThreadLocalInstance();

            // create a new statistic data record stored in thread local
            // for this scope
            statisticData = new StatisticDataVo();
            StatisticDataVo.setThreadLocalInstance(statisticData);
            statisticData.clearParameters();

            // insert callee and method info
            final MethodSignature methodSignature =
                ((MethodSignature) joinPoint.getSignature());
            Method calledMethod = methodSignature.getMethod();
            String calleeClassName = methodSignature.getDeclaringTypeName();

            // Object callee = invocation.getTargetObject();
            // Matcher matcher =
            // PATTERN_PARSE_CALLEE_NAME.matcher(callee.toString());
            // String calleeClassName;
            // if (matcher.find()) {
            // calleeClassName = matcher.group(1);
            // }
            // else {
            // throw new WebserverSystemException(StringUtility
            // .concatenateWithBracketsToString(
            // "Unexpected format for callee", callee.toString()));
            // }
            statisticData.addParameter(PARAM_HANDLER, calleeClassName);
            final String calledMethodName = calledMethod.getName();
            statisticData.addParameter(PARAM_REQUEST, calledMethodName);

            // insert interface info
            if (UserContext.isRestAccess()) {
                statisticData.addParameter(PARAM_INTERFACE,
                    VALUE_INTERFACE_REST);
            }
            else {
                statisticData.addParameter(PARAM_INTERFACE,
                    VALUE_INTERFACE_SOAP);
            }

            // insert internal (0)/external (1) info
            if (UserContext.isExternalUser()) {
                statisticData
                    .addParameter(PARAM_INTERNAL, VALUE_INTERNAL_FALSE);
            }
            else {
                statisticData.addParameter(PARAM_INTERNAL, VALUE_INTERNAL_TRUE);
            }

            handleObjectIds(statisticData, calledMethodName, joinPoint
                .getArgs());

            final Object ret = proceed(joinPoint);

            statisticData.addParameter(PARAM_SUCCESSFUL, VALUE_SUCCESS_TRUE);
            return ret;
        }
        catch (Exception e) {
            statisticData.addParameter(PARAM_SUCCESSFUL, VALUE_SUCCESS_FALSE);
            statisticData.addParameter(PARAM_EXCEPTION_NAME, e
                .getClass().getName());

//            if (e.getMessage() == null) {
//                statisticData.addParameter(PARAM_EXCEPTION_MESSAGE, "");
//            }
//            else {
//                statisticData.addParameter(PARAM_EXCEPTION_MESSAGE, e
//                    .getMessage());
//            }
            String source;
            final StackTraceElement[] elements = e.getStackTrace();
            if (elements != null && elements.length > 0) {
                StackTraceElement element = elements[0];
                source =
                    StringUtility.concatenateWithBracketsToString(element
                        .getClassName(), element.getMethodName(), element
                        .getLineNumber());
            }
            else {
                source = "unknown";
            }
            statisticData.addParameter(PARAM_EXCEPTION_SOURCE, source);
            if (e instanceof EscidocException) {
                throw e;
            }
            else {
                // this should not occur. To report this failure, the exception
                // is wrapped by a SystemException
                throw new SystemException(
                    "Service throws unexpected exception. ", e);
            }
        }
        finally {
            // insert elapsed time
            statisticData.addParameter(PARAM_ELAPSED_TIME, ""
                + (System.currentTimeMillis() - invocationStartTime));

            // insert user info
            final String userId = UserContext.getId();
            if (userId != null) {
                statisticData.addParameter(PARAM_USER_ID, userId);
            }

            statisticQueueHandler.putMessage(statisticData);

            // restore the callers statistic data record
            StatisticDataVo.setThreadLocalInstance(callersStatisticData);
        }
    }

    /**
     * Continue the invocation.
     * 
     * @param joinPoint
     *            The current {@link ProceedingJoinPoint}.
     * @return Returns the result of the continued invocation.
     * @throws Throwable
     *             Thrown in case of an error during proceeding the method call.
     */
    private Object proceed(final ProceedingJoinPoint joinPoint)
        throws Throwable {

        if (LOG.isDebugEnabled()) {
            LOG.debug(StringUtility.concatenateWithBracketsToString("proceed",
                this));
        }

        return joinPoint.proceed();
    }

/**
     * Inserts the method parameter that hold object ids into the provided
     * {@link StatisticDataVo} object.<br>
     * The objids are taken from the method parameters, that are string
     * parameters, contain a :, but do not seem to be XML data, i.e. does not
     * contain '<'. <br>
     * The last found objid is logged as PARAM_OBJID, because this addresses the
     * accessed (sub- )resource, e.g. item or component of an item. The other
     * object ids (if any) are logged as PARAM_PARENT_OBJID + index.
     * 
     * @param statisticData
     *            The {@link StatisticDataVo} object to put the object ids into.
     * @param calledMethodName
     *            The name of the called method.
     * @param arguments
     *            The arguments of the method call.
     * @common
     */
    private void handleObjectIds(
        final StatisticDataVo statisticData, final String calledMethodName,
        final Object[] arguments) {

        if (arguments != null && arguments.length > 0) {
            int indexLastObjid = -1;
            for (int i = 0; i < arguments.length; i++) {
                try {
                    final String argument = (String) arguments[i];
                    if (argument != null
                        && !PATTERN_DETERMINE_XML_PARAMETER
                            .matcher(argument).find()) {
                        indexLastObjid = i;
                    }
                    else {
                        // First string parameter found that holds xml. Suspend
                        // loop;
                        break;
                    }
                }
                catch (ClassCastException e) {
                    // e.g., this is the case for binary content
                    // (createStagingFile), ignore exception
                    if (LOG.isDebugEnabled()) {
                        LOG.debug(StringUtility.concatenateToString(
                            MSG_CLASS_CAST_EXCEPTION, calledMethodName, e
                                .getMessage()));
                    }
                    // Parameter found that is not a string. In this case, the
                    // loop is stopped and no objids are logged, as it seems to
                    // be a special method, e.g. evaluateRetrieve of the PDP.
                    indexLastObjid = -1;
                    break;
                }
            }
            if (indexLastObjid >= 0) {
                statisticData.addParameter(PARAM_OBJID,
                    (String) arguments[indexLastObjid]);
                for (int i = indexLastObjid - 1, parent = 1; i >= 0; i--) {
                    statisticData.addParameter(StringUtility
                        .concatenateToString(PARAM_PARENT_OBJID, parent++),
                        (String) arguments[i]);
                }
            }
        }
    }

    /**
     * Injects the {@link StatisticQueueHandler}.
     * 
     * @param statisticQueueHandler
     *            The {@link StatisticQueueHandler} to inject.
     * @spring.property ref="common.StatisticQueueHandler"
     * @common
     */
    public void setStatisticQueueHandler(
        final StatisticQueueHandler statisticQueueHandler) {

        this.statisticQueueHandler = statisticQueueHandler;
    }

}
