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
package de.escidoc.core.aa.business.stax.handler;

import de.escidoc.core.aa.business.UserAccountHandler;
import de.escidoc.core.aa.business.persistence.RoleGrant;
import de.escidoc.core.aa.business.persistence.UserAccount;
import de.escidoc.core.aa.business.persistence.UserAccountDaoInterface;
import de.escidoc.core.common.exceptions.application.violated.AlreadyRevokedException;
import de.escidoc.core.common.exceptions.system.SqlDatabaseSystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.util.xml.stax.events.StartElement;

import java.util.Date;

/**
 * Stax handler implementation that handles the revocation of a grant. It revokes the grant and extracts the revocation
 * remark from the parsed xml data and sets it in the grant.
 *
 * @author Torsten Tetteroo
 */
public class RevokeStaxHandler extends UserAccountStaxHandlerBase {

    private final UserAccount authenticateUser;

    /**
     * The constructor.
     *
     * @param grant The {@code RoleGrant} to handle.
     * @param dao   The data access object to retrieve {@code Grant} objects.
     * @throws AlreadyRevokedException Thrown if the provided garnt is already revoked.
     * @throws de.escidoc.core.common.exceptions.system.WebserverSystemException
     * @throws de.escidoc.core.common.exceptions.system.SqlDatabaseSystemException
     */
    public RevokeStaxHandler(final RoleGrant grant, final UserAccountDaoInterface dao) throws AlreadyRevokedException,
        SqlDatabaseSystemException, WebserverSystemException {

        super(grant, false);
        this.authenticateUser = UserAccountHandler.getAuthenticatedUser(dao);
        if (grant.getRevocationDate() != null) {
            throw new AlreadyRevokedException();
        }
    }

    /**
     * See Interface for functional description.
     *
     * @see DefaultHandler #startElement(de.escidoc.core.common.util.xml.stax.events.StartElement)
     */
    @Override
    public StartElement startElement(final StartElement element) {

        if (isNotReady() && getGrant().getRevocationDate() == null) {
            getGrant().setUserAccountByRevokerId(this.authenticateUser);
            getGrant().setRevocationDate(new Date());
        }
        return element;
    }

    /**
     * See Interface for functional description.
     *
     * @see DefaultHandler #characters(java.lang.String, de.escidoc.core.common.util.xml.stax.events.StartElement)
     */
    @Override
    public String characters(final String s, final StartElement element) {

        if (isNotReady()) {
            final String localName = element.getLocalName();
            if ("revocation-remark".equals(localName)) {
                getGrant().setRevocationRemark(s);

                setReady();
            }
        }

        return s;
    }

}
