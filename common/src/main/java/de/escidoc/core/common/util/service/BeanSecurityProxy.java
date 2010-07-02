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
package de.escidoc.core.common.util.service;

import java.lang.reflect.Method;

import javax.ejb.EJBContext;

import org.jboss.security.SecurityProxy;

import de.escidoc.core.common.util.logger.AppLogger;

/**
 * Currently not used. <p/>
 * 
 * A simple example of a custom SecurityProxy implementation.
 * 
 * @author Bernhard Kraus (Accenture)
 */
public class BeanSecurityProxy implements SecurityProxy {

    private static final AppLogger LOG =
        new AppLogger(BeanSecurityProxy.class.getName());

    private EJBContext ctx;

    /**
     * Init the bean proxy class.
     * 
     * @param beanHome
     *            the home bean
     * @param beanRemote
     *            the remote bean
     * @param securityMgr
     *            the security manager
     * @throws InstantiationException
     *             could not be instantiated
     */
    public void init(
        final Class beanHome, final Class beanRemote, final Object securityMgr)
        throws InstantiationException {

    }

    /**
     * Set the EJBContext.
     * 
     * @param context
     *            the EJBContext object
     */
    public void setEJBContext(final EJBContext context) {
        this.ctx = context;
    }

    /**
     * Access to home methods.
     * 
     * @param m
     *            The method of the home bean
     * @param args
     *            The arguments as array
     * @throws Exception
     *             SecurityException
     */
    public void invokeHome(final Method m, final Object[] args)
        throws Exception {
        // We don't validate access to home methods
    }

    /**
     * Invoke methods in the remote bean.
     * 
     * @param m
     *            The method of the home bean
     * @param args
     *            The arguments as array
     * @param bean
     *            the remote object
     * @throws Exception
     *             SecurityException
     */
    public void invoke(final Method m, final Object[] args, final Object bean)
        throws Exception {
        // Who is calling the bean
        if (LOG.isDebugEnabled()) {
            LOG.debug("Principal " + ctx.getCallerPrincipal());
        }
    }

    /**
     * Init the bean proxy class.
     * 
     * @param arg0
     *            the 1st class
     * @param arg1
     *            the 2nd class
     * @param arg2
     *            the 3rd class
     * @param arg3
     *            the 4th class
     * @param arg4
     *            the 5th class
     * @throws InstantiationException
     *             could not be instantiated
     */
    public void init(
        final Class arg0, final Class arg1, final Class arg2, final Class arg3,
        final Object arg4) throws InstantiationException {

    }
}
