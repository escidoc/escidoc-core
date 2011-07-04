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

import de.escidoc.core.aa.business.persistence.RoleGrant;
import de.escidoc.core.aa.business.persistence.UserAccount;
import de.escidoc.core.common.util.xml.stax.handler.DefaultHandler;

/**
 * Base class of stax handlers that work with user accounts or grants.
 *
 * @author Torsten Tetteroo
 */
public abstract class UserAccountStaxHandlerBase extends DefaultHandler {

    /**
     * The user account to handle.
     */
    private UserAccount userAccount;

    /**
     * The grant to handle.
     */
    private RoleGrant grant;

    /**
     * Flag indicating if new user data may be created ({@code true}) or data from database shall be updated
     * ({@code false}).
     */
    private final boolean create;

    /**
     * The constructor.
     *
     * @param userAccount The user account to handle.
     * @param create      The flag indicating if new object shall be created ( {@code true} ), or a object shall be
     *                    updated.
     */
    protected UserAccountStaxHandlerBase(final UserAccount userAccount, final boolean create) {

        this.userAccount = userAccount;
        this.create = create;
    }

    /**
     * The constructor.
     *
     * @param grant  The grant to handle.
     * @param create The flag indicating if new object shall be created ( {@code true} ), or a object shall be
     *               updated.
     */
    protected UserAccountStaxHandlerBase(final RoleGrant grant, final boolean create) {

        this.grant = grant;
        this.create = create;
    }

    /**
     * @return Returns the create.
     */
    protected boolean isCreate() {
        return this.create;
    }

    /**
     * Gets the user data from the data provider.
     *
     * @return Returns the stored {@code UserAccount} object or {@code null}.
     */
    protected UserAccount getUserAccount() {

        return this.userAccount;
    }

    /**
     * Gets the user data from the data provider.
     *
     * @return Returns the stored {@code UserAccount} object or {@code null}.
     */
    protected RoleGrant getGrant() {

        return this.grant;
    }
}
