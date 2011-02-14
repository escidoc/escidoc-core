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
 * Copyright 2007-2008 Fachinformationszentrum Karlsruhe Gesellschaft
 * fuer wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Foerderung der Wissenschaft e.V.  
 * All rights reserved.  Use is subject to license terms.
 */
package de.escidoc.core.common.util.aop;

import de.escidoc.core.common.annotation.Validate;
import de.escidoc.core.common.exceptions.application.invalid.InvalidXmlException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.exceptions.system.XmlParserSystemException;
import de.escidoc.core.common.util.xml.XmlUtility;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.Ordered;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Interceptor to validate incoming XML documents. The validation takes only
 * place if the called method is annotated accordingly.
 * 
 * @spring.bean id="common.XmlValidationInterceptor" factory-method="aspectOf"
 *              lazy-init="false"
 * 
 * @author MSC
 * 
 * @common
 */
@Aspect
public class XmlValidationInterceptor implements Ordered {

    // CHECKSTYLE:JAVADOC-OFF

    /**
     * See Interface for functional description.
     * 
     * @return
     * @see org.springframework.core.Ordered#getOrder()
     * @common
     */
    public int getOrder() {

        return AopUtil.PRECEDENCE_XML_VALIDATION_INTERCEPTOR;
    }

    // CHECKSTYLE:JAVADOC-ON

    /**
     * Before advice to perform the schema validation of xml data of the current
     * request.
     * 
     * @param joinPoint
     *            The current {@link JoinPoint}.
     * @throws Throwable
     *             Thrown in case of an error.
     * @common
     */
//    @Before("call(public !static * de.escidoc.core.*.service.interfaces.*.*(..))"
//        + " && within(de.escidoc.core.*.ejb.*Bean)")
    @Before("call(public !static * de.escidoc.core.*.service.interfaces.*.*(..))")
    public void validate(final JoinPoint joinPoint) throws Throwable {

        Method calledMethod =
            ((MethodSignature) joinPoint.getSignature()).getMethod();
        Annotation annotation = calledMethod.getAnnotation(Validate.class);
        if (annotation != null) {
            Object[] arguments = joinPoint.getArgs();
            validate((String) arguments[((Validate) annotation).param()],
                ((Validate) annotation).resolver(), ((Validate) annotation)
                    .root());
        }
    }

    private static Map<String, String> schemaLocations =
        new HashMap<String, String>();

    /**
     * Validates the provided xml data using the specified schema.
     * 
     * @param xml
     *            The xml data to validate.
     * @param resolvingMethod
     *            The name of the resolving method used to identify the schema
     *            location.
     * @throws InvalidXmlException
     *             Thrown in case of failed xml schema validation.
     * @throws WebserverSystemException
     *             Thrown in case of an internal error.
     * @throws XmlParserSystemException
     * @common
     */
    private void validate(
        final String xml, final String resolvingMethod, final String root)
        throws InvalidXmlException, WebserverSystemException,
        XmlParserSystemException {

        XmlUtility.validate(xml, getSchemaLocation(resolvingMethod), root);
    }

    /**
     * Gets the location of the schema using the provided resolvingMethod value.
     * 
     * @param resolvingMethod
     *            The name of the resolving method used to identify the schema
     *            location.
     * @return Returns the schema location.
     * @throws WebserverSystemException
     *             Thrown in case of an internal error.
     * @common
     */
    private String getSchemaLocation(final String resolvingMethod)
        throws WebserverSystemException {

        String result = schemaLocations.get(resolvingMethod);
        if (result == null) {
            Class[] paramTypes = {};
            try {
                Method getSchemaLocationM =
                    XmlUtility.class.getMethod(resolvingMethod, paramTypes);
                result =
                    (String) getSchemaLocationM.invoke(null, new Object[0]);
            }
            catch (Exception e) {
                throw new WebserverSystemException(
                    "Could not find schema location for schema "
                        + resolvingMethod + "!", e);
            }
        }
        return result;

    }

}
