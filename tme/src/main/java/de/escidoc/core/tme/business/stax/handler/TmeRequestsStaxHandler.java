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
package de.escidoc.core.tme.business.stax.handler;

import de.escidoc.core.common.business.Constants;
import de.escidoc.core.common.exceptions.EscidocException;
import de.escidoc.core.common.exceptions.application.invalid.TmeException;
import de.escidoc.core.common.util.stax.StaxParser;
import de.escidoc.core.common.util.string.StringUtility;
import de.escidoc.core.common.util.xml.stax.events.StartElement;
import de.escidoc.core.common.util.xml.stax.handler.DefaultHandler;

import javax.naming.directory.NoSuchAttributeException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

/**
 * Stax handler that handles the last modification attribute and checks the
 * optimistic locking criteria.
 * 
 * @author TTE
 * @common
 */
public class TmeRequestsStaxHandler extends DefaultHandler {

    private final StaxParser parser;

    private final Collection<String> files;

    /**
     * The constructor.
     * 
     * @param parser
     *            The stax parser.
     */
    public TmeRequestsStaxHandler(final StaxParser parser) {
        files = new Vector<String>();
        this.parser = parser;
    }

    /**
     * See Interface for functional description.
     * 
     * @param element
     *            Start Element
     * @return
     * @throws EscidocException
     *             TODO
     */
    @Override
    public StartElement startElement(final StartElement element)
        throws EscidocException {

        if ("file".equals(element.getLocalName())) {
            try {
                String uriString =
                    element
                        .getAttribute(Constants.XLINK_URI, "href").getValue();
                if ("".equals(uriString)) {
                    throw new TmeException("Link '" + uriString
                        + "' to file is no Uri!");
                }
                try {
                    new URI(uriString);
                }
                catch (URISyntaxException e) {
                    throw new TmeException("Link '" + uriString
                        + "' to file is no Uri!", e);
                }
                files.add(uriString);
            }
            catch (NoSuchAttributeException e) {
                // TODO what happens here?
            }
        }
        return element;
    }

    /**
     * @return the files
     */
    public String[] getFiles() {

        return files.toArray(new String[files.size()]);
    }

    /**
     * @return the files
     */
    public String getFilesAsCsv() {

        StringBuffer result = new StringBuffer();
        Iterator<String> iter = files.iterator();
        if (iter.hasNext()) {
            result = result.append(iter.next());
            while (iter.hasNext()) {
                result = StringUtility.concatenate(result, ",", iter.next());
            }
        }
        return result.toString();
    }

}
