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

import de.escidoc.core.common.exceptions.application.invalid.XmlCorruptedException;
import de.escidoc.core.common.exceptions.application.missing.MissingAttributeValueException;
import de.escidoc.core.common.exceptions.application.notfound.OrganizationalUnitNotFoundException;
import de.escidoc.core.common.exceptions.system.IntegritySystemException;
import de.escidoc.core.common.exceptions.system.TripleStoreSystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.util.stax.StaxParser;
import de.escidoc.core.common.util.xml.XmlUtility;
import de.escidoc.core.common.util.xml.stax.events.StartElement;

import java.util.ArrayList;
import java.util.List;

/**
 * Stax handler implementation that handles the refrences to parent ous that have to be parsed and extracted during the
 * creation of an organizational unit.<br/>
 *
 * @author Michael Schneider
 */
public class OrganizationalUnitParentsHandler extends OrganizationalUnitHandlerBase {

    private final List<String> parents = new ArrayList<String>();

    private String rootElement = XmlUtility.NAME_ORGANIZATIONAL_UNIT;

    private boolean rootElementPathChecked;

    /**
     * @param parser The stax parser.
     * @throws WebserverSystemException Thrown in case of an internal error.
     */
    public OrganizationalUnitParentsHandler(final StaxParser parser) {

        super(parser);
    }

    /**
     * See Interface for functional description.
     */
    @Override
    public StartElement startElement(final StartElement element) throws OrganizationalUnitNotFoundException,
        MissingAttributeValueException, XmlCorruptedException, TripleStoreSystemException, IntegritySystemException,
        WebserverSystemException {

        final String curPath = getParser().getCurPath();
        if (!this.rootElementPathChecked) {
            if (!getParser().getCurPath().startsWith('/' + this.rootElement)) {
                throw new XmlCorruptedException("Root element is " + element.getLocalName() + " not as expected"
                    + this.rootElement + "! ");
            }
            this.rootElementPathChecked = true;
        }

        if (curPath.endsWith(XmlUtility.NAME_PARENT)) {
            parents.add(checkParentRef(element));
        }
        return element;
    }

    /**
     * @return the parentOus
     */
    public List<String> getParentOus() {
        return this.parents;
    }

    /**
     * @param rootElement the rootElement to set
     */
    public void setRootElement(final String rootElement) {
        this.rootElement = rootElement;
    }

}
