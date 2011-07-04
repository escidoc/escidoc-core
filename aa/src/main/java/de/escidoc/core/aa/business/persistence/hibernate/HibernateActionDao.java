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
package de.escidoc.core.aa.business.persistence.hibernate;

import de.escidoc.core.aa.business.persistence.ActionDaoInterface;
import de.escidoc.core.aa.business.persistence.UnsecuredActionList;
import de.escidoc.core.common.exceptions.system.SqlDatabaseSystemException;
import de.escidoc.core.common.persistence.hibernate.AbstractHibernateDao;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import java.util.List;

/**
 * Escidoc action data access object using hibernate.
 *
 * @author Torsten Tetteroo
 */
public class HibernateActionDao extends AbstractHibernateDao implements ActionDaoInterface {

    /**
     * See Interface for functional description.
     */
    @Override
    public UnsecuredActionList retrieveUnsecuredActionList(final String contextId) throws SqlDatabaseSystemException {

        UnsecuredActionList ret = null;
        if (contextId != null) {
            final List<UnsecuredActionList> list =
                getHibernateTemplate().findByCriteria(
                    DetachedCriteria.forClass(UnsecuredActionList.class).add(Restrictions.eq("contextId", contextId)));
            if (list != null && !list.isEmpty()) {
                ret = list.get(0);
            }
        }
        return ret;
    }

    /**
     * See Interface for functional description.
     */
    @Override
    public void saveOrUpdate(final UnsecuredActionList unsecuredActionList) throws SqlDatabaseSystemException {

        super.saveOrUpdate(unsecuredActionList);
    }

    /**
     * See Interface for functional description.
     *
     * @see ActionDaoInterface #delete(de.escidoc.core.aa.business.persistence.UnsecuredActionList)
     */
    @Override
    public void delete(final UnsecuredActionList unsecuredActionList) throws SqlDatabaseSystemException {

        super.delete(unsecuredActionList);
    }

    /**
     * Wrapper of setSessionFactory to enable bean stuff generation for this bean.
     *
     * @param mySessionFactory The sessionFactory to set.
     */
    public final void setMySessionFactory(final SessionFactory mySessionFactory) {

        setSessionFactory(mySessionFactory);
    }
}
