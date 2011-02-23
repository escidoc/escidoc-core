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
 * Copyright 2008 Fachinformationszentrum Karlsruhe Gesellschaft
 * fuer wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Foerderung der Wissenschaft e.V.  
 * All rights reserved.  Use is subject to license terms.
 */
package de.escidoc.core.common.axis;

import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.util.logger.AppLogger;
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
 * {@link org.apache.axis.providers.java.RPCProvider} to lookup a spring bean
 * that shall be exposed as a web service. Besides the creation of the service
 * object, the security parameters from webservice security are used to
 * initialize the spring-security (acegisecurity) security context for the
 * service call.<br>
 * 
 * @author TTE
 * @common
 */
public class EscidocSpringProvider extends RPCProvider {

    /**
     * The serial version uid.
     */
    private static final long serialVersionUID = 8212241336639346861L;

    /**
     * The logger.
     */
    private static final AppLogger LOG =
        new AppLogger(EscidocSpringProvider.class.getName());

    protected static final String MISSING_MANDATORY_PARAMETER =
        "Missing mandatory parameter in deployment descriptor";

    protected static final String OPTION_SPRING_BEAN = "springBean";

    // CHECKSTYLE:JAVADOC-OFF

    /**
     * See Interface for functional description.<br>
     * Realizes spring bean lookup and security context initialization.
     * 
     * @param messageContext
     * @param className
     * @return
     * @throws Exception
     * @see org.apache.axis.providers.java.EJBProvider#makeNewServiceObject(
     *      org.apache.axis.MessageContext, java.lang.String)
     */
    @Override
    protected Object makeNewServiceObject(
        final MessageContext messageContext, final String className)
        throws Exception {

        Object springBean = lookupSpringBean(messageContext.getService());

        LOG.debug("makeNewServiceObject: Bean created");

        // initialize user context from webservice security data
        try {
            UserContext.setUserContext(getHandle(messageContext));
            try {
                UserContext.getSecurityContext();
            }
            catch (SystemException e1) {
                throw new InvocationTargetException(e1);
            }
        }
        catch (Exception ex) {
            LOG.error("Setting UserContext failed.", ex);
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
     * @see org.apache.axis.providers.java.JavaProvider
     *      #getServiceClass(java.lang.String,
     *      org.apache.axis.handlers.soap.SOAPService,
     *      org.apache.axis.MessageContext)
     * @common
     */
    @Override
    protected Class getServiceClass(
        final String className, final SOAPService service,
        final MessageContext messageContext) throws AxisFault {

        try {
            final Class beanType =
                BeanLocator.getBeanType(BeanLocator.COMMON_FACTORY_ID,
                    getSpringBeanId(service));
            return beanType;
        }
        catch (WebserverSystemException e) {
            throw new AxisFault(StringUtility.format(
                "Spring bean type lookup failed", getSpringBeanId(service)), e);
        }
    }

    /**
     * See Interface for functional description.
     * 
     * @return
     * @see org.apache.axis.providers.java.JavaProvider
     *      #getServiceClassNameOptionName()
     * @common
     */
    @Override
    protected String getServiceClassNameOptionName() {

        return OPTION_SPRING_BEAN;
    }

    // CHECKSTYLE:JAVADOC-ON

    /**
     * Method that fetches the user credentials from the axis message context
     * and returns them as a array of {username, password}.
     * 
     * @param messageContext
     *            The axis message context
     * @return user name and password as {@link String} array {username,
     *         password}
     */
    private String getHandle(final MessageContext messageContext) {

        String eSciDocUserHandle = null;
        List results;
        // get the result Vector from the property
        results =
            (List) messageContext
                .getProperty(WSHandlerConstants.RECV_RESULTS);
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
                LOG
                    .info("No security results!! Setting empty username and password");
                eSciDocUserHandle = "";
            }
        }
        else {
            for (Object result : results) {
                WSHandlerResult hResult = (WSHandlerResult) result;
                // Needs to be a Vector. Handed over from WSHandlerResult.getResults()
                List hResults = hResult.getResults();
                for (Object hResult1 : hResults) {
                    WSSecurityEngineResult eResult =
                            (WSSecurityEngineResult) hResult1;
                    // Note: an encryption action does not have an associated
                    // principal
                    // only Signature and UsernameToken actions return a
                    // principal
                    if (eResult.getAction() != WSConstants.ENCR) {
                        WSUsernameTokenPrincipal principal =
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
     * @common
     */
    private Object lookupSpringBean(final Handler service) throws AxisFault {

        String springBeanId = getSpringBeanId(service);
        Object springBean;
        try {
            springBean =
                BeanLocator
                    .getBean(BeanLocator.COMMON_FACTORY_ID, springBeanId);
        }
        catch (WebserverSystemException e) {
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
     * @common
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
