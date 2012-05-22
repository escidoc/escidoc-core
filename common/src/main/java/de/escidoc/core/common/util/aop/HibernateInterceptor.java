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
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.core.Ordered;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.orm.hibernate3.SessionFactoryUtils;
import org.springframework.orm.hibernate3.SessionHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * @author Michael Hoppe
 *
 */
@Aspect
public class HibernateInterceptor implements Ordered {

    private SessionFactory sessionFactory;

    private final FlushMode FLUSH_MODE = FlushMode.COMMIT;

    /**
     * See Interface for functional description.
     */
    @Override
    public int getOrder() {
        return AopUtil.PRECEDENCE_HIBERNATE_INTERCEPTOR;
    }

    @Around("execution(public * org.escidoc.core..internal.*RestServiceImpl.*(..))")
    public Object invoke(ProceedingJoinPoint joinPoint) throws Throwable {
        boolean participate = false;
        if (TransactionSynchronizationManager.hasResource(sessionFactory)) {
            // Do not modify the Session: just set the participate flag.
            participate = true;
        }
        else {
            Session session = getSession(sessionFactory);
            TransactionSynchronizationManager.bindResource(sessionFactory, new SessionHolder(session));
        }
        try {
            return proceed(joinPoint);
        }
        finally {
            if (!participate) {
                SessionHolder sessionHolder =
                    (SessionHolder) TransactionSynchronizationManager.unbindResource(sessionFactory);
                closeSession(sessionHolder.getSession(), sessionFactory);
            }
        }
    }

    private Session getSession(SessionFactory sessionFactory) throws DataAccessResourceFailureException {
        Session session = SessionFactoryUtils.getSession(sessionFactory, true);
        session.setFlushMode(FLUSH_MODE);
        return session;
    }

    /**
     * Close the given Session.
     * @param session the Session used for filtering
     * @param sessionFactory the SessionFactory that this filter uses
     */
    private void closeSession(Session session, SessionFactory sessionFactory) {
        session.flush();
        SessionFactoryUtils.closeSession(session);
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
     * Set the Hibernate SessionFactory that should be used to create
     * Hibernate Sessions.
     */
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

}
