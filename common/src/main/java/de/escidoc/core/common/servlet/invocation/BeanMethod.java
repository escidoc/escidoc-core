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

package de.escidoc.core.common.servlet.invocation;

import de.escidoc.core.common.business.fedora.EscidocBinaryContent;
import de.escidoc.core.common.exceptions.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.servlet.invocation.exceptions.MethodNotFoundException;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;
import de.escidoc.core.common.util.service.BeanLocator;
import de.escidoc.core.common.util.service.UserContext;
import de.escidoc.core.common.util.string.StringUtility;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * A bean method.
 * 
 * @author Michael Schwantner
 *
 */
public class BeanMethod {

    private static final Logger LOGGER = LoggerFactory.getLogger(BeanMethod.class);

    private static final Map<String, Object> RESOURCE_POOL =
        Collections.synchronizedMap(new HashMap<String, Object>());

    private final String beanId;

    private String method;

    private Object[] parameters;

    /**
     * Create a bean method.
     * 
     * @param beanId
     *            The id of the bean to invoke.
     * @param method
     *            The name of the bean method.
     * @param parameters
     *            The array containing the parameters.
     *
     */
    public BeanMethod(final String beanId, final String method,
        final Object[] parameters) {

        this.beanId = beanId;
        this.method = method;
        this.parameters = parameters;
    }

    /**
     * Invoke the method on the given resource. Protocol will be REST.
     * 
     * @param username
     *            The username.
     * @param password
     *            The password.
     * @return The return value of the method invoction.
     * @throws InvocationTargetException
     *             If the method throws a declared exception.
     * @throws MethodNotFoundException
     *             If the invoked method does not exist.
     * @throws WebserverSystemException
     *             If the invocation of the method causes an error.
     *
     */
    public Object invoke(final String username, final String password)
        throws InvocationTargetException, MethodNotFoundException,
        WebserverSystemException {
        return invokeWithProtocol(password, true);
    }

    /**
     * Invoke the method on the given resource with a given protocol. protocol
     * is the 3rd parameter (restAccess). If set to true it means REST-Access If
     * set to false it means SOAP-Access
     * 
     * @param eSciDocUserHandle
     *            The eSciDoc userHandle.
     * @param restAccess
     *            The restAccess.
     * 
     * @return The return value of the method invocation.
     * @throws InvocationTargetException
     *             If the method throws a declared exception.
     * @throws MethodNotFoundException
     *             If the invoked method does not exist.
     * @throws WebserverSystemException
     *             If the invocation of the method causes an error.
     *
     */
    public Object invokeWithProtocol(
        final String eSciDocUserHandle, final boolean restAccess)
        throws InvocationTargetException, MethodNotFoundException,
        WebserverSystemException {

        final Object result;
        try {
            if (eSciDocUserHandle != null) {
                UserContext.setUserContext(eSciDocUserHandle);
                UserContext.setRestAccess(restAccess);
            }
            Class[] parameterTypes = null;
            if (this.parameters != null) {
                final int noOfArguments = parameters.length;
                parameterTypes = new Class[noOfArguments];
                for (int i = 0; i < noOfArguments; ++i) {
                    if (this.parameters[i] != null) {
                        if (this.parameters[i] instanceof String) {
                            parameterTypes[i] = String.class;
                        }
                        else if (this.parameters[i] instanceof EscidocBinaryContent) {
                            parameterTypes[i] = EscidocBinaryContent.class;
                        }
                        else if (this.parameters[i] instanceof Map) {
                            parameterTypes[i] = Map.class;
                        }
                        else {
                            throw new InvocationTargetException(
                                new SystemException("Unsupported parameter type [" +
                                        this.parameters[i].getClass().getName()
                                        + ']'));
                        }
                    }
                    else {
                        parameterTypes[i] = String.class;
                    }
                }
            }
            final Method execute =
                getBean().getClass().getMethod(getMethod(), parameterTypes);
            result = execute.invoke(getBean(), getParameters());
        }
        catch (final SecurityException e) {
            throw new WebserverSystemException("Cannot execute method '" + this.method + "' on resource "
                    + getBeanId(), e);
        }
        catch (final IllegalArgumentException e) {
            throw new WebserverSystemException("Cannot execute method '" + this.method + "' on resource "
                    + getBeanId(), e);
        }
        catch (final NoSuchMethodException e) {
            throw new MethodNotFoundException("Cannot execute method '" + this.method + "' on resource "
                    + getBeanId(), e);
        }
        catch (final IllegalAccessException e) {
            throw new WebserverSystemException("Cannot execute method '" + this.method + "' on resource "
                    + getBeanId(), e);
        }
        catch (final MissingMethodParameterException e) {
            throw new WebserverSystemException("Cannot execute method '" + this.method + "' on resource "
                    + getBeanId(), e);
        }
        return result;
    }

    /**
     * Get a String representation of the resource method.
     * 
     * @return The String representation of the resource method.
     */
    @Override
    public String toString() {

        return '[' + getBeanId() + '.' + getMethod() + ']';
    }

    /**
     * Get a bean for this bean method.
     * 
     * @return The bean instance.
     * @throws WebserverSystemException
     *             If the bean cannot be instantiated.
     *
     */
    private Object getBean() throws WebserverSystemException {

        Object result = RESOURCE_POOL.get(getBeanId());
        if (result == null && getBeanId() != null) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(StringUtility.format("Create Bean", getBeanId()));
            }

            result = "service.StagingFileHandlerBean".equals(getBeanId()) ? BeanLocator.getBean(BeanLocator.ST_FACTORY_ID, getBeanId()) : BeanLocator.getBean(BeanLocator.COMMON_FACTORY_ID, getBeanId());
            RESOURCE_POOL.put(getBeanId(), result);
        }
        return result;
    }

    /**
     * @return Returns the method name.
     *
     */
    public String getMethod() {
        return this.method;
    }

    /**
     * @param method
     *            The method name to set.
     *
     */
    public void setMethod(final String method) {
        this.method = method;
    }

    /**
     * @return Returns the parameters.
     *
     */
    public Object[] getParameters() {
        return this.parameters;
    }

    /**
     * @param parameters
     *            The parameters to set.
     *
     */
    public void setParameters(final Object[] parameters) {
        this.parameters = parameters;
    }

    /**
     * @return Returns the bean id.
     *
     */
    public String getBeanId() {
        return this.beanId;
    }

}
