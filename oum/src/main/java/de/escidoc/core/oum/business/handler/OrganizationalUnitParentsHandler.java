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

import de.escidoc.core.common.exceptions.application.invalid.InvalidXmlException;
import de.escidoc.core.common.exceptions.application.invalid.XmlCorruptedException;
import de.escidoc.core.common.exceptions.application.missing.MissingAttributeValueException;
import de.escidoc.core.common.exceptions.application.notfound.OrganizationalUnitNotFoundException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.util.stax.StaxParser;
import de.escidoc.core.common.util.xml.XmlUtility;
import de.escidoc.core.common.util.xml.stax.events.EndElement;
import de.escidoc.core.common.util.xml.stax.events.StartElement;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Stax handler implementation that handles the refrences to parent ous that
 * have to be parsed and extracted during the creation of an organizational
 * unit.<br/>
 * 
 * @author MSC
 * 
 */
public class OrganizationalUnitParentsHandler
    extends OrganizationalUnitHandlerBase {

    private final List<String> parents = new ArrayList<String>();

    // private boolean parentsExist = false;

    private String rootElement = XmlUtility.NAME_ORGANIZATIONAL_UNIT;

    private boolean rootElementPathChecked = false;

    /**
     * 
     * @param parser
     *            The stax parser.
     * @throws WebserverSystemException
     *             Thrown in case of an internal error.
     */
    public OrganizationalUnitParentsHandler(final StaxParser parser)
        throws WebserverSystemException {

        super(null, parser);
    }

    // CHECKSTYLE:JAVADOC-OFF
    /**
     * See Interface for functional description.
     * 
     * @param element
     * @return
     * @throws MissingAttributeValueException
     * @throws OrganizationalUnitNotFoundException
     * @throws SystemException
     * @throws InvalidXmlException
     * @throws MissingAttributeValueException
     * @see de.escidoc.core.common.util.xml.stax.handler.DefaultHandler#
     *      startElement(de.escidoc.core.common.util.xml.stax.events.StartElement)
     */
    @Override
    public StartElement startElement(final StartElement element)
        throws OrganizationalUnitNotFoundException, SystemException,
        InvalidXmlException, MissingAttributeValueException {

        String curPath = getParser().getCurPath();
        if (!rootElementPathChecked) {
            if (!getParser().getCurPath().startsWith('/' + rootElement)) {
                throw new XmlCorruptedException("Root element is "
                    + element.getLocalName() + " not as expected" + rootElement
                    + "! ");
            }
            rootElementPathChecked = true;
        }

        // if (curPath.endsWith(XmlUtility.NAME_PARENT_OBJECTS)) {
        // parentsExist = true;
        // }
        // else
        if (curPath.endsWith(XmlUtility.NAME_PARENT)) {
            parents.add(checkParentRef(element));
        }
        return element;
    }

    /**
     * See Interface for functional description.
     * 
     * @param element
     * @return
     * @throws InvalidXmlException
     * @see de.escidoc.core.common.util.xml.stax.handler.DefaultHandler#
     *      endElement(de.escidoc.core.common.util.xml.stax.events.EndElement)
     */
    @Override
    public EndElement endElement(final EndElement element)
        throws InvalidXmlException {

        // if (getParser().getCurPath().equals("/organizational-unit/parents")
        // && parentsExist && parents.isEmpty()) {
        // throw new InvalidXmlException(
        // "Element parent-ous must not be empty!");
        // }
        return element;
    }

    // CHECKSTYLE:JAVADOC-ON

    /**
     * @return the parentOus
     */
    public List<String> getParentOus() {
        return parents;
    }

    /**
     * @param rootElement
     *            the rootElement to set
     */
    public void setRootElement(final String rootElement) {
        this.rootElement = rootElement;
    }

}
