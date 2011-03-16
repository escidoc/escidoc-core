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

package de.escidoc.core.common.axis;

import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import org.apache.axis.providers.java.EJBProvider;
import org.apache.axis.providers.java.JavaProvider;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;
import de.escidoc.core.common.util.service.BeanLocator;
import de.escidoc.core.common.util.service.UserContext;
import de.escidoc.core.common.util.string.StringUtility;
import org.apache.axis.AxisFault;
import org.apache.axis.Handler;
import org.apache.axis.MessageContext;
import org.apache.axis.handlers.soap.SOAPService;
import org.apache.axis.providers.java.RPCProvider;
import org.apache.ws.security.WSConstants;
import org.apache.ws.security.WSSecurityEngineResult;
import org.apache.ws.security.WSUsernameTokenPrincipal;
import org.apache.ws.security.handler.WSHandlerConstants;
import org.apache.ws.security.handler.WSHandlerResult;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * Axis provider implementation that extends
 * {@link RPCProvider} to lookup a spring bean
 * that shall be exposed as a web service. Besides the creation of the service
 * object, the security parameters from webservice security are used to
 * initialize the spring-security (acegisecurity) security context for the
 * service call.<br>
 * 
 * @author TTE
 *
 */
public class EscidocSpringProvider extends RPCProvider {

    /**
     * The serial version uid.
     */
    private static final long serialVersionUID = 8212241336639346861L;

    /**
     * The logger.
     */
    private static final Logger LOGGER =
        LoggerFactory.getLogger(EscidocSpringProvider.class);

    protected static final String MISSING_MANDATORY_PARAMETER =
        "Missing mandatory parameter in deployment descriptor";

    protected static final String OPTION_SPRING_BEAN = "springBean";



    /**
     * See Interface for functional description.<br>
     * Realizes spring bean lookup and security context initialization.
     * 
     * @param messageContext
     * @param className
     * @return
     * @throws Exception
     * @see EJBProvider#makeNewServiceObject(
     *      MessageContext, String)
     */
    @Override
    protected Object makeNewServiceObject(
        final MessageContext messageContext, final String className)
        throws Exception {
        final Object springBean = lookupSpringBean(messageContext.getService());
        // initialize user context from webservice security data
        try {
            UserContext.setUserContext(getHandle(messageContext));
            try {
                UserContext.getSecurityContext();
            }
            catch (final SystemException e1) {
                throw new InvocationTargetException(e1);
            }
        }
        catch (final Exception ex) {
            if(LOGGER.isWarnEnabled()) {
                LOGGER.warn("Setting user context failed.");
            }
            if(LOGGER.isDebugEnabled()) {
                LOGGER.debug("Setting user context failed.", ex);
            }
        }

        return springBean;
    }

    /**
     * See Interface for functional description.
     * 
     * @param className
     * @param service
     * @param messageContext
     * @return
     * @throws AxisFault
     * @see JavaProvider
     *      #getServiceClass(java.lang.String,
     *      org.apache.axis.handlers.soap.SOAPService,
     *      org.apache.axis.MessageContext)
     *
     */
    @Override
    protected Class getServiceClass(final String className, final SOAPService service,
                                    final MessageContext messageContext) throws AxisFault {
        try {
            return BeanLocator.getBeanType(BeanLocator.COMMON_FACTORY_ID,getSpringBeanId(service));
        }
        catch (final WebserverSystemException e) {
            throw new AxisFault(StringUtility.format("Spring bean type lookup failed", getSpringBeanId(service)), e);
        }
    }

    /**
     * See Interface for functional description.
     * 
     * @return
     * @see JavaProvider
     *      #getServiceClassNameOptionName()
     *
     */
    @Override
    protected String getServiceClassNameOptionName() {

        return OPTION_SPRING_BEAN;
    }



    /**
     * Method that fetches the user credentials from the axis message context
     * and returns them as a array of {username, password}.
     * 
     * @param messageContext
     *            The axis message context
     * @return user name and password as {@link String} array {username,
     *         password}
     */
    private static String getHandle(final MessageContext messageContext) {

        // get the result Vector from the property
        final List results = (List) messageContext
                .getProperty(WSHandlerConstants.RECV_RESULTS);
        String eSciDocUserHandle = null;
        if (results == null) {
            // If username/password are sent as
            // http-basic-authentication-header
            // results is null, and username/password
            // are set in messageContext-attributes
            // so get username and password
            // from messageContext-attributes
            if (messageContext.getUsername() != null
                && messageContext.getPassword() != null) {
                eSciDocUserHandle = messageContext.getPassword();
            }
            else {
                LOGGER
                    .info("No security results!! Setting empty username and password");
                eSciDocUserHandle = "";
            }
        }
        else {
            for (final Object result : results) {
                final WSHandlerResult hResult = (WSHandlerResult) result;
                // Needs to be a Vector. Handed over from WSHandlerResult.getResults()
                final List hResults = hResult.getResults();
                for (final Object hResult1 : hResults) {
                    final WSSecurityEngineResult eResult =
                            (WSSecurityEngineResult) hResult1;
                    // Note: an encryption action does not have an associated
                    // principal
                    // only Signature and UsernameToken actions return a
                    // principal
                    if (eResult.getAction() != WSConstants.ENCR) {
                        final WSUsernameTokenPrincipal principal =
                                (WSUsernameTokenPrincipal) eResult.getPrincipal();
                        eSciDocUserHandle = principal.getPassword();
                    }
                }
            }
        }
        return eSciDocUserHandle;
    }

    /**
     * Looks up the spring bean identified by parameter
     * {@link OPTION_SPRING_BEAN}.
     * 
     * @param service
     *            The {@link Handler} used to get the springBean option from.
     * @return The identified spring bean. This will not be <code>null</code>.
     *         If a bean cannot be found, an {@link AxisFault} is thrown.
     * @throws AxisFault
     *             Thrown if bean lookup fails.
     *
     */
    private Object lookupSpringBean(final Handler service) throws AxisFault {

        final String springBeanId = getSpringBeanId(service);
        final Object springBean;
        try {
            springBean =
                BeanLocator
                    .getBean(BeanLocator.COMMON_FACTORY_ID, springBeanId);
        }
        catch (final WebserverSystemException e) {
            throw new AxisFault(StringUtility.format(
                "Spring bean lookup failed", springBeanId), e);
        }
        return springBean;
    }

    /**
     * Gets the spring id from the service handler.
     * 
     * @param service
     *            The service handler.
     * @return Returns the spring id. This will not be <code>null</code>. If
     *         a bean id cannot be found, an {@link AxisFault} is thrown.
     * @throws AxisFault
     *             Thrown if the parameter {@link OPTION_SPRING_BEAN} is not
     *             found and no spring bean id could be returned.
     *
     */
    private String getSpringBeanId(final Handler service) throws AxisFault {

        String springBeanId = null;
        if (service != null) {
            springBeanId = (String) service.getOption(OPTION_SPRING_BEAN);
        }
        if (springBeanId == null) {
            springBeanId = (String) getOption(OPTION_SPRING_BEAN);
        }
        if (springBeanId == null) {
            throw new AxisFault(StringUtility.format(
                MISSING_MANDATORY_PARAMETER, OPTION_SPRING_BEAN));
        }

        return springBeanId;
    }
}
