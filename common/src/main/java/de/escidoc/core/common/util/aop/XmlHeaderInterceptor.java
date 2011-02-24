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

import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.util.logger.AppLogger;
import de.escidoc.core.common.util.service.UserContext;
import de.escidoc.core.common.util.string.StringUtility;
import de.escidoc.core.common.util.xml.XmlUtility;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.Ordered;

import java.util.regex.Pattern;

/**
 * Interceptor to insert the xml header and an optional (in case access via
 * REST) a style sheet definition into an XML document.
 * 
 * @spring.bean id="common.XmlHeaderInterceptor" factory-method="aspectOf"
 *              lazy-init="false"
 * 
 * @author TTE
 * 
 * @common
 */
@Aspect
public class XmlHeaderInterceptor implements Ordered {

    /**
     * The logger.
     */
    private static final AppLogger LOG =
        new AppLogger(XmlHeaderInterceptor.class.getName());

    private static final Pattern PATTERN_XML_HEADER =
        Pattern.compile("<\\?xml version=[^>]+\\?>");

    private static final Pattern XML_DOCUMENT_START_PATTERN =
        Pattern.compile("<?xml version=");

    private static final Pattern XML_DOCUMENT_START_XSLT_PATTERN =
        Pattern.compile("<?xml version=[^>]+<?xml-stylesheet ");

    // CHECKSTYLE:JAVADOC-OFF

    /**
     * See Interface for functional description.
     * 
     * @return
     * @see org.springframework.core.Ordered#getOrder()
     * @common
     */
    @Override
    public int getOrder() {

        return AopUtil.PRECEDENCE_XML_HEADER_INTERCEPTOR;
    }

    // CHECKSTYLE:JAVADOC-ON

    /**
     * Around advice to add xml header information and (in case of REST) a style
     * sheet to the method result.<br>
     * Note: As it is not possible to return different references when using
     * after-returning advises, the around advice is used here.
     * 
     * @param joinPoint
     *            The current {@link ProceedingJoinPoint}.
     * @return Returns the changed result.
     * @throws Throwable
     *             Thrown in case of an error.
     * @common
     */
    @Around("call(public !static java.lang.String de.escidoc.core.*.service.interfaces.*.*(..))"
        + " && within(de.escidoc.core.*.ejb.*Bean)"
        + " && !call(* de.escidoc.core..*.SemanticStoreHandler*.*(..))"
        + " && !call(* de.escidoc.core.common..*.*(..))")
    public Object processResult(final ProceedingJoinPoint joinPoint)
        throws Throwable {

        if (LOG.isDebugEnabled()) {
            LOG.debug(StringUtility.format(
                "processResult", this));
        }

        return post(joinPoint.proceed());
    }

    /**
     * Post handling.
     * 
     * @param result
     *            The result of the method call.
     * @return Returns the method call result with inserted xml header and an
     *         optional (in case access via REST) a style sheet definition
     * @throws WebserverSystemException
     *             Thrown in case of an internal system error.
     */
    private Object post(final Object result) throws WebserverSystemException {

        final CharSequence res = (String) result;
        if (!XML_DOCUMENT_START_PATTERN.matcher(res).find()) {

            final StringBuilder ret = new StringBuilder(XmlUtility.DOCUMENT_START);
            
            if (UserContext.isRestAccess()) {
                ret.append(XmlUtility
                    .getStylesheetDefinition());
            }
            
            ret.append(result);

            return ret.toString();
        }
        else if (UserContext.isRestAccess()
            && !XML_DOCUMENT_START_XSLT_PATTERN.matcher(res).find()) {
            return PATTERN_XML_HEADER.matcher(res).replaceFirst(
                XmlUtility.DOCUMENT_START 
                + XmlUtility.getStylesheetDefinition());
        }
        return result;
    }
}
