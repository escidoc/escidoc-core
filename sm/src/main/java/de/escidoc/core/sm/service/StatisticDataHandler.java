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
package de.escidoc.core.sm.service;

import de.escidoc.core.common.exceptions.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.application.security.AuthenticationException;
import de.escidoc.core.common.exceptions.application.security.AuthorizationException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.sm.service.interfaces.StatisticDataHandlerInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * A statistic data resource handler.
 *
 * @author Michael Hoppe
 */
@Service("service.StatisticDataHandler")
public class StatisticDataHandler implements StatisticDataHandlerInterface {

    @Autowired
    @Qualifier("business.StatisticDataHandler")
    private de.escidoc.core.sm.business.interfaces.StatisticDataHandlerInterface handler;

    /**
     * Private constructor to prevent initialization.
     */
    protected StatisticDataHandler() {
    }

    /**
     * See Interface for functional description.
     *
     * @param xmlData statistic data as xml in statistic-data schema.
     * @throws AuthenticationException Thrown in case of failed authentication.
     * @throws AuthorizationException  Thrown in case of failed authorization.
     * @throws MissingMethodParameterException
     *                                 ex
     * @throws SystemException         ex
     * @see de.escidoc.core.sm.service.interfaces .StatisticDataHandlerInterface #create(java.lang.String)
     */
    @Override
    public void create(final String xmlData) throws AuthenticationException, AuthorizationException,
        MissingMethodParameterException, SystemException {
        handler.create(xmlData);
    }

}
