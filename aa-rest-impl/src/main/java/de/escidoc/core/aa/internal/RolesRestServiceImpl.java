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
 * Copyright 2006-2011 Fachinformationszentrum Karlsruhe Gesellschaft
 * fuer wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Foerderung der Wissenschaft e.V.  
 * All rights reserved.  Use is subject to license terms.
 */
package de.escidoc.core.aa.internal;

import org.escidoc.core.domain.aa.RoleListTO;
import org.escidoc.core.domain.service.ServiceUtility;
import org.escidoc.core.domain.sru.parameters.SruSearchRequestParametersBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.escidoc.core.aa.RolesRestService;
import de.escidoc.core.aa.service.interfaces.RoleHandlerInterface;
import de.escidoc.core.common.exceptions.application.invalid.InvalidSearchQueryException;
import de.escidoc.core.common.exceptions.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.application.security.AuthenticationException;
import de.escidoc.core.common.exceptions.application.security.AuthorizationException;
import de.escidoc.core.common.exceptions.system.SystemException;

/**
 * @author Michael Hoppe
 *
 */
public class RolesRestServiceImpl implements RolesRestService {

    @Autowired
    @Qualifier("service.RoleHandler")
    private RoleHandlerInterface roleHandler;

    /**
     * 
     */
    public RolesRestServiceImpl() {
    }

    /* (non-Javadoc)
     * @see de.escidoc.core.aa.RoleRestService#retrieveRoles(SruSearchRequestParametersBean)
     */
    @Override
    public RoleListTO retrieveRoles(final SruSearchRequestParametersBean filter, final String roleId, final String userId, final String omitHighlighting) throws MissingMethodParameterException,
        AuthenticationException, AuthorizationException, InvalidSearchQueryException, SystemException {
        return ServiceUtility.fromXML(RoleListTO.class, this.roleHandler.retrieveRoles(ServiceUtility.toMap(filter, roleId, userId, omitHighlighting)));
    }

}
