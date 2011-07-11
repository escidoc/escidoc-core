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
package de.escidoc.core.oum.business.handler;

import de.escidoc.core.common.business.Constants;
import de.escidoc.core.common.exceptions.application.missing.MissingAttributeValueException;
import de.escidoc.core.common.exceptions.application.notfound.OrganizationalUnitNotFoundException;
import de.escidoc.core.common.exceptions.system.IntegritySystemException;
import de.escidoc.core.common.exceptions.system.TripleStoreSystemException;
import de.escidoc.core.common.util.stax.StaxParser;
import de.escidoc.core.common.util.xml.XmlUtility;
import de.escidoc.core.common.util.xml.stax.events.StartElement;

import javax.naming.directory.NoSuchAttributeException;

/**
 * @author Michael Schneider
 */
public class OrganizationalUnitHandlerBase extends HandlerBase {

    private String id;

    /**
     * @param parser The stax parser.
     */
    public OrganizationalUnitHandlerBase(final StaxParser parser) {
        super(parser);
    }

    /**
     * Check the given element if it contains a valid reference to another organizational unit.
     *
     * @param element The element.
     * @return The id of the referenced organizational unit.
     * @throws MissingAttributeValueException If the href is not found.
     * @throws OrganizationalUnitNotFoundException
     *                                        If the id does not point to an organizational unit.
     * @throws de.escidoc.core.common.exceptions.system.TripleStoreSystemException
     * @throws de.escidoc.core.common.exceptions.system.IntegritySystemException
     */
    protected String checkParentRef(final StartElement element) throws MissingAttributeValueException,
        OrganizationalUnitNotFoundException, TripleStoreSystemException, IntegritySystemException {

        String result;
        try {
            result = XmlUtility.getIdFromURI(element.getAttribute(Constants.XLINK_URI, "href").getValue());
        }
        catch (final NoSuchAttributeException e) {
            try {
                result = element.getAttribute(null, "objid").getValue();
            }
            catch (final NoSuchAttributeException e1) {
                throw new MissingAttributeValueException("Parent attribute 'href' or 'objid' has to be set! ", e1);
            }
        }
        try {
            this.getUtility().checkIsOrganizationalUnit(result);
        }
        catch (final OrganizationalUnitNotFoundException e) {
            throw new OrganizationalUnitNotFoundException("Reference to parent organizational-unit is not valid! "
                + e.getMessage(), e);
        }
        return result;
    }

    /**
     * @return the id
     */
    public String getId() {
        return this.id;
    }

    /**
     * Retrieve the value of the given property from the triplestore.
     *
     * @param property The property.
     * @return The value of the property.
     * @throws de.escidoc.core.common.exceptions.system.TripleStoreSystemException
     */
    public String getProperty(final String property) throws TripleStoreSystemException {

        return getTripleStoreUtility().getPropertiesElements(getId(), property);
    }

}
