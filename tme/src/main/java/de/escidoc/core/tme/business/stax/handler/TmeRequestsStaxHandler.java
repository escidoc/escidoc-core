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
import de.escidoc.core.common.exceptions.application.invalid.TmeException;
import de.escidoc.core.common.util.xml.stax.events.StartElement;
import de.escidoc.core.common.util.xml.stax.handler.DefaultHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.directory.NoSuchAttributeException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * Stax handler that handles the last modification attribute and checks the optimistic locking criteria.
 *
 * @author Torsten Tetteroo
 */
public class TmeRequestsStaxHandler extends DefaultHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(TmeRequestsStaxHandler.class);

    private final Collection<String> files;

    public TmeRequestsStaxHandler() {
        this.files = new ArrayList<String>();
    }

    /**
     * See Interface for functional description.
     *
     * @param element Start Element
     */
    @Override
    public StartElement startElement(final StartElement element) throws TmeException {

        if ("file".equals(element.getLocalName())) {
            try {
                final String uriString = element.getAttribute(Constants.XLINK_URI, "href").getValue();
                if ("".equals(uriString)) {
                    throw new TmeException("Link '" + uriString + "' to file is no Uri!");
                }
                try {
                    new URI(uriString);
                }
                catch (final URISyntaxException e) {
                    throw new TmeException("Link '" + uriString + "' to file is no Uri!", e);
                }
                files.add(uriString);
            }
            catch (final NoSuchAttributeException e) {
                // TODO what happens here?
                if (LOGGER.isWarnEnabled()) {
                    LOGGER.warn("Error on parsing last modification attribute.");
                }
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Error on parsing last modification attribute.", e);
                }
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
        final Iterator<String> iter = files.iterator();
        if (iter.hasNext()) {
            result = result.append(iter.next());
            while (iter.hasNext()) {
                result.append(',');
                result.append(iter.next());
            }
        }
        return result.toString();
    }

}
