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
 * Copyright 2006-2012 Fachinformationszentrum Karlsruhe Gesellschaft
 * fuer wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Foerderung der Wissenschaft e.V.  
 * All rights reserved.  Use is subject to license terms.
 */
package de.escidoc.core.common.util.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.orm.hibernate3.HibernateAccessor;
import org.springframework.orm.hibernate3.SessionFactoryUtils;
import org.springframework.orm.hibernate3.SessionHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * @author Michael Hoppe
 *
 */
@Aspect
public class HibernateInterceptorAdvice extends HibernateAccessor implements Ordered {

    private static final Logger LOGGER = LoggerFactory.getLogger(HibernateInterceptorAdvice.class);

    private boolean exceptionConversionEnabled = true;

    /**
     * Set whether to convert any HibernateException raised to a Spring DataAccessException,
     * compatible with the <code>org.springframework.dao</code> exception hierarchy.
     * <p>Default is "true". Turn this flag off to let the caller receive raw exceptions
     * as-is, without any wrapping.
     * @see org.springframework.dao.DataAccessException
     */
    public void setExceptionConversionEnabled(boolean exceptionConversionEnabled) {
        this.exceptionConversionEnabled = exceptionConversionEnabled;
    }

    /**
     * See Interface for functional description.
     */
    @Override
    public int getOrder() {
        return AopUtil.PRECEDENCE_HIBERNATE_INTERCEPTOR;
    }

    @Around("execution(public * de.escidoc.core.*.internal.*RestServiceImpl.*(..))")
    public Object invoke(ProceedingJoinPoint joinPoint) throws Throwable {
        Session session = getSession();
        SessionHolder sessionHolder =
            (SessionHolder) TransactionSynchronizationManager.getResource(getSessionFactory());

        boolean existingTransaction = (sessionHolder != null && sessionHolder.containsSession(session));
        if (existingTransaction) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Found thread-bound Session for HibernateInterceptor");
            }
        }
        else {
            if (sessionHolder != null) {
                sessionHolder.addSession(session);
            }
            else {
                TransactionSynchronizationManager.bindResource(getSessionFactory(), new SessionHolder(session));
            }
        }

        FlushMode previousFlushMode = null;
        try {
            previousFlushMode = applyFlushMode(session, existingTransaction);
            enableFilters(session);
            return proceed(joinPoint);
        }
        catch (HibernateException ex) {
            if (this.exceptionConversionEnabled) {
                throw convertHibernateAccessException(ex);
            }
            else {
                throw ex;
            }
        }
        finally {
            flushIfNecessary(session, existingTransaction);
            if (existingTransaction) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Not closing pre-bound Hibernate Session after HibernateInterceptor");
                }
                disableFilters(session);
                if (previousFlushMode != null) {
                    session.setFlushMode(previousFlushMode);
                }
            }
            else {
                SessionFactoryUtils.closeSession(session);
                if (sessionHolder == null || sessionHolder.doesNotHoldNonDefaultSession()) {
                    TransactionSynchronizationManager.unbindResource(getSessionFactory());
                }
            }
        }
    }

    /**
     * Return a Session for use by this interceptor.
     * @see SessionFactoryUtils#getSession
     */
    protected Session getSession() {
        return SessionFactoryUtils
            .getSession(getSessionFactory(), getEntityInterceptor(), getJdbcExceptionTranslator());
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

}
