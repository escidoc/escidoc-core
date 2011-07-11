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

import de.escidoc.core.common.util.xml.XmlUtility;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.Ordered;

import java.util.regex.Pattern;

/**
 * Interceptor to insert the xml header and an optional (in case access via REST) a style sheet definition into an XML
 * document.
 *
 * @author Torsten Tetteroo
 */
@Aspect
public class XmlHeaderInterceptor implements Ordered {

    private static final Pattern PATTERN_XML_HEADER = Pattern.compile("<\\?xml version=[^>]+\\?>");

    private static final Pattern XML_DOCUMENT_START_PATTERN = Pattern.compile("<?xml version=");

    private static final Pattern XML_DOCUMENT_START_XSLT_PATTERN =
        Pattern.compile("<?xml version=[^>]+<?xml-stylesheet ");

    /**
     * See Interface for functional description.
     */
    @Override
    public int getOrder() {

        return AopUtil.PRECEDENCE_XML_HEADER_INTERCEPTOR;
    }

    /**
     * Around advice to add xml header information and (in case of REST) a style sheet to the method result.<br> Note:
     * As it is not possible to return different references when using after-returning advises, the around advice is
     * used here.
     *
     * @param joinPoint The current {@link ProceedingJoinPoint}.
     * @return Returns the changed result.
     * @throws Throwable Thrown in case of an error.
     */
    @Around("execution(public java.lang.String de.escidoc.core.*.service.*.*(..))"
        + " && !execution(* de.escidoc.core..*.SemanticStoreHandler*.*(..))"
        + " && !execution(* de.escidoc.core.common..*.*(..))")
    public Object processResult(final ProceedingJoinPoint joinPoint) throws Throwable {
        return post(joinPoint.proceed());
    }

    /**
     * Post handling.
     *
     * @param result The result of the method call.
     * @return Returns the method call result with inserted xml header and an optional (in case access via REST) a style
     *         sheet definition
     */
    private static Object post(final Object result) {
        if (result == null) {
            return null;
        }
        final CharSequence res = (CharSequence) result;
        if (!XML_DOCUMENT_START_PATTERN.matcher(res).find()) {
            final StringBuilder ret = new StringBuilder(XmlUtility.DOCUMENT_START);
            ret.append(XmlUtility.getStylesheetDefinition());
            ret.append(result);
            return ret.toString();
        }
        else if (!XML_DOCUMENT_START_XSLT_PATTERN.matcher(res).find()) {
            return PATTERN_XML_HEADER.matcher(res).replaceFirst(
                XmlUtility.DOCUMENT_START + XmlUtility.getStylesheetDefinition());
        }
        return result;
    }
}
