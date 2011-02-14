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
package de.escidoc.core.aa.business.authorisation;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This is a error handler class used when parsing a policy using SAX.
 * <p/>
 * 
 * This class is used in class DatabasePolicyFinderModule when creating a policy
 * via loadPolicy, which "loads" the policy from an XML string. This is an
 * alternative implementation of the Policy Loading mechanism that is currently
 * not in use.
 * 
 * @author ROW (Accenture)
 */
public class CustomErrorHandler implements ErrorHandler {

    // the logger we'll use for all messages
    private static final Logger LOGGER =
        Logger.getLogger(CustomErrorHandler.class.getName());

    /**
     * Standard handler routine for the XML parsing.
     * 
     * @param exception
     *            information on what caused the problem
     * @throws SAXException
     *             exception
     */
    public void warning(final SAXParseException exception) throws SAXException {
        if (LOGGER.isLoggable(Level.WARNING)) {
            LOGGER.warning("Warning on line " + exception.getLineNumber()
                + ": " + exception.getMessage());
        }
    }

    /**
     * Standard handler routine for the XML parsing.
     * 
     * @param exception
     *            information on what caused the problem
     * 
     * @throws SAXException
     *             always to halt parsing on errors
     */
    public void error(final SAXParseException exception) throws SAXException {
        if (LOGGER.isLoggable(Level.WARNING)) {
            LOGGER.warning("Error on line " + exception.getLineNumber() + ": "
                + exception.getMessage() + " ... "
                + "Policy will not be available");
        }

        throw new SAXException("error parsing policy");
    }

    /**
     * Standard handler routine for the XML parsing.
     * 
     * @param exception
     *            information on what caused the problem
     * 
     * @throws SAXException
     *             always to halt parsing on errors
     */
    public void fatalError(final SAXParseException exception)
        throws SAXException {
        if (LOGGER.isLoggable(Level.WARNING)) {
            LOGGER.warning("Fatal error on line " + exception.getLineNumber()
                + ": " + exception.getMessage() + " ... "
                + "Policy will not be available");
        }

        throw new SAXException("fatal error parsing policy");
    }

}
