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
 * Copyright 2007-2008 Fachinformationszentrum Karlsruhe Gesellschaft
 * fuer wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Foerderung der Wissenschaft e.V.  
 * All rights reserved.  Use is subject to license terms.
 */
package de.escidoc.core.aa.business.stax.handler;

import de.escidoc.core.common.business.Constants;
import de.escidoc.core.common.business.fedora.TripleStoreUtility;
import de.escidoc.core.common.business.fedora.Utility;
import de.escidoc.core.common.exceptions.EscidocException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidStatusException;
import de.escidoc.core.common.exceptions.application.notfound.OrganizationalUnitNotFoundException;
import de.escidoc.core.common.util.string.StringUtility;
import de.escidoc.core.common.util.xml.XmlUtility;
import de.escidoc.core.common.util.xml.stax.events.EndElement;
import de.escidoc.core.common.util.xml.stax.handler.LinkCollectionStaxHandler;

/**
 * Stax handler that manages the organizational-units of a user account.<br>
 * This handler is a specialization of {@link LinkCollectionStaxHandler} and
 * verifies
 * <ul>
 * <li>each addressed organizational unit references an existing organizational
 * unit.</li>
 * <li>the public-status of the organizational unit is &quot;opened&quot;
 * </ul>
 * 
 * @author TTE
 * @aa
 */
public class OrganizationalUnitsHandler extends LinkCollectionStaxHandler {

    private static final String ERR_MSG_OU_NOT_OPENED =
        "Referenced organizational unit is not in public status opened.";

    public static final int UNINITIALIZED = -1;

    private final TripleStoreUtility tsu;

    /**
     * The constructor.
     * 
     * @param tsu
     *            The triple store utility to retrieve organizational units
     *            properties.
     * @aa
     */
    public OrganizationalUnitsHandler(final TripleStoreUtility tsu) {

        super(XmlUtility.XPATH_USER_ACCOUNT_ORGANIZATIONAL_UNIT,
            XmlUtility.BASE_ORGANIZATIONAL_UNIT,
            OrganizationalUnitNotFoundException.class);
        this.tsu = tsu;
    }

    // CHECKSTYLE:JAVADOC-OFF

    /**
     * See Interface for functional description.
     * 
     * @param element
     * @return
     * @throws EscidocException
     * @see de.escidoc.core.common.util.xml.stax.handler.LinkCollectionStaxHandler
     *      #endLinkElement(de.escidoc.core.common.util.xml.stax.events.EndElement)
     * @aa
     */
    @Override
    public EndElement endLinkElement(final EndElement element)
        throws EscidocException {

        Utility.getInstance().checkIsOrganizationalUnit(getObjid());
        if (!Constants.STATUS_OU_OPENED.equals(tsu.getPublicStatus(getObjid()))) {
            throw new InvalidStatusException(StringUtility
                .concatenateWithBracketsToString(ERR_MSG_OU_NOT_OPENED,
                    getObjid()));
        }
        return super.endLinkElement(element);
    }

    // CHECKSTYLE:JAVADOC-ON
}
