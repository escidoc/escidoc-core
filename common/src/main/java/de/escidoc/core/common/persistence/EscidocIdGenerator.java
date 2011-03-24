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

package de.escidoc.core.common.persistence;

import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.util.service.BeanLocator;
import org.hibernate.HibernateException;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.id.IdentifierGenerator;

import java.io.Serializable;

/**
 * Implementation of a hibernate id generator.<br>
 * This implementation fetches the ids from the {@link EscidocIdProvider}.
 * 
 * @author Torsten Tetteroo
 * 
 */
public class EscidocIdGenerator implements IdentifierGenerator {

    public static final String ID_PREFIX = "escidoc:";

    private EscidocIdProvider idProvider;



    /**
     * See Interface for functional description.
     * 
     * @param arg0
     * @param arg1
     * @return
     * @throws HibernateException
     * @see IdentifierGenerator#generate(
     *      SessionImplementor, Object)
     *
     */
    @Override
    public Serializable generate(
        final SessionImplementor sessionImplementor, final Object arg1) {

        try {
            return getIdProvider().getNextPid();
        }
        catch (final SystemException e) {
            throw new HibernateException("Failed to generate an id. ", e);
        }

    }



    /**
     * Gets (an initializes if needed) the {@link EscidocIdProvider}.
     * 
     * @return Returns the {@link EscidocIdGenerator} object.
     * @throws SystemException
     *             Thrown in case of an internal system error.
     *
     */
    public EscidocIdProvider getIdProvider() throws SystemException {

        if (this.idProvider == null) {
            this.idProvider =
                (EscidocIdProvider) BeanLocator.getBean(
                    BeanLocator.COMMON_FACTORY_ID,
                    EscidocIdProvider.SPRING_BEAN_ID);
        }
        return this.idProvider;
    }

}
