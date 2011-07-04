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

import de.escidoc.core.aa.business.persistence.UserAccount;
import de.escidoc.core.aa.business.persistence.UserAccountDaoInterface;
import de.escidoc.core.common.exceptions.application.violated.UniqueConstraintViolationException;
import de.escidoc.core.common.exceptions.system.SqlDatabaseSystemException;
import de.escidoc.core.common.util.string.StringUtility;
import de.escidoc.core.common.util.xml.XmlUtility;
import de.escidoc.core.common.util.xml.stax.events.StartElement;

/**
 * Stax Handler managing the properties of an UserAccount.
 *
 * @author Torsten Tetteroo
 */
public class UserAccountPropertiesStaxHandler extends UserAccountStaxHandlerBase {

    private final UserAccountDaoInterface dao;

    private boolean insideProperties;

    /**
     * The constructor.
     *
     * @param userAccount The {@code UserAccount} to handle.
     * @param dao         The data access object to retrieve {@code UserAccount} objects.
     * @param create      The flag indicating if new user data may be created ( {@code true} ) or data from
     *                    database shall be updated ( {@code false} ).
     */
    public UserAccountPropertiesStaxHandler(final UserAccount userAccount, final UserAccountDaoInterface dao,
        final boolean create) {

        super(userAccount, create);
        this.dao = dao;
    }

    /**
     * See Interface for functional description.
     *
     * @see DefaultHandler #startElement (de.escidoc.core.common.util.xml.stax.events.StartElement)
     */
    @Override
    public StartElement startElement(final StartElement element) {

        if (isNotReady() && XmlUtility.XPATH_USER_ACCOUNT_PROPERTIES.equals(element.getPath())) {

            this.insideProperties = true;
        }
        return element;
    }

    /**
     * See Interface for functional description.
     *
     * @see DefaultHandler #characters(java.lang.String, de.escidoc.core.common.util.xml.stax.events.Star tElement)
     */
    @Override
    public String characters(final String s, final StartElement element) throws UniqueConstraintViolationException,
        SqlDatabaseSystemException {

        if (isNotReady() && this.insideProperties) {
            final String elementName = element.getLocalName();
            if (XmlUtility.NAME_NAME.equals(elementName)) {
                getUserAccount().setName(s);
            }
            else if (XmlUtility.NAME_LOGIN_NAME.equals(elementName)) {
                assertUniqueLoginName(s);
                getUserAccount().setLoginname(s);

                // login-name is the last property extracted by this handler.
                // active is discarded in
                // create/update. Therefore, finished is set here.
                setReady();
            }
        }

        return s;
    }

    /**
     * Asserts that the loginname of the provided UserAccount is unique.
     *
     * @param toBeAsserted The login name that shall be asserted.
     * @throws SqlDatabaseSystemException Thrown In case of an database error.
     * @throws UniqueConstraintViolationException
     *                                    Thrown if the login name of the provided {@code UserAccount} object is
     *                                    not unique, i.e. it exists another account with the same loginname but a
     *                                    different user id.
     */
    private void assertUniqueLoginName(final String toBeAsserted) throws SqlDatabaseSystemException,
        UniqueConstraintViolationException {

        if ("current".equalsIgnoreCase(toBeAsserted)) {
            throw new UniqueConstraintViolationException("Login name may not be 'current' as this is a reserved String");
        }
        final UserAccount loginNameUserAccount = dao.retrieveUserAccountByLoginName(toBeAsserted);
        if (loginNameUserAccount != null) {
            if (loginNameUserAccount.getId().equals(getUserAccount().getId())) {
                return;
            }
            throw new UniqueConstraintViolationException(StringUtility.format(
                "Login name must be unique within eSciDoc", toBeAsserted));
        }
    }

}
