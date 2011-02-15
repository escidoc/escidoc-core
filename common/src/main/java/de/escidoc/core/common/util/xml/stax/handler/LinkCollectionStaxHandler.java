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
package de.escidoc.core.common.util.xml.stax.handler;

import de.escidoc.core.common.exceptions.EscidocException;
import de.escidoc.core.common.util.xml.stax.events.EndElement;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Stax handler implementation that handles a "collection" of link elements,
 * e.g. organizational units of an user account.<br>
 * This handler specializes {@link LinkStaxHandler}.
 * 
 * @see LinkStaxHandler
 * @author TTE
 * @um
 * 
 */
public class LinkCollectionStaxHandler extends LinkStaxHandler {
    
    private final List<String> hrefs = new ArrayList<String>();
    private final List<String> objids = new ArrayList<String>();
    private int index = 0;
    
    /**
     * The constructor.
     * 
     * @param linkElementPath
     *            The path to the link elements that shall be handled by this
     *            handler.
     * @um
     */
    public LinkCollectionStaxHandler(final String linkElementPath) {

        super(linkElementPath);
    }
    
    /**
     * Constructs a {@link LinkCollectionStaxHandler} that checks the base uri
     * of the link.
     * 
     * @param elementPath
     *            The path to the link elements that shall be handled by this
     *            handler.
     * @param hrefBaseUri
     *            The base uri of the href pointing to the objects that are
     *            referenced by the links that shall be parsed by this handler.
     *            In case of REST, this value is used to check the provided
     *            href. In case of SOAP it is ignored.
     * @param exceptionClass
     *            The type of the exception to throw if href base uri is not
     *            matched in case of REST. This parameter must not be
     *            <code>null</code> and must be an instance of
     *            {@link EscidocException}, but this is not checked!.
     * @common
     */
    public LinkCollectionStaxHandler(final String elementPath,
        final String hrefBaseUri, final Class exceptionClass) {

        super(elementPath, hrefBaseUri, exceptionClass);
    }

    // CHECKSTYLE:JAVADOC-OFF
    
    /**
     * See Interface for functional description.
     * 
     * @param element
     * @return
     * @throws EscidocException
     * @see de.escidoc.core.common.util.xml.stax.handler.LinkStaxHandler
     *      #endLinkElement
     *      (de.escidoc.core.common.util.xml.stax.events.EndElement)
     * @um
     */
    @Override
    public EndElement endLinkElement(final EndElement element)
        throws EscidocException {

        hrefs.add(getHref());
        objids.add(getObjid());
        index++;
        
        return super.endLinkElement(element);
    }

    /**
     * @return the hrefs
     */
    public List<String> getHrefs() {
        return hrefs;
    }

    /**
     * @return the objids
     */
    public List<String> getObjids() {
        return objids;
    }

    /**
     * @return the index
     */
    public int getIndex() {
        return index;
    }
    
    // CHECKSTYLE:JAVADOC-ON
}
