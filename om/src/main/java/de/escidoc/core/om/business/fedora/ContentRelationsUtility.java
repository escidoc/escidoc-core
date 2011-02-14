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
package de.escidoc.core.om.business.fedora;

import de.escidoc.core.common.exceptions.application.invalid.InvalidContentException;
import de.escidoc.core.common.exceptions.application.invalid.XmlCorruptedException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.exceptions.system.XmlParserSystemException;
import de.escidoc.core.common.util.configuration.EscidocConfiguration;
import de.escidoc.core.common.util.logger.AppLogger;
import de.escidoc.core.common.util.stax.StaxParser;
import de.escidoc.core.common.util.xml.XmlUtility;
import de.escidoc.core.om.business.stax.handler.ContentRelationsOntologyHandler;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.Vector;

/**
 * Handle content relations for framework.
 * 
 * @author ROF, SWA
 * 
 */
public final class ContentRelationsUtility {
    private static AppLogger log =
        new AppLogger(ContentRelationsUtility.class.getName());

    private static Vector<String> PREDICATES = new Vector<String>();

    static {
        try {
            loadOntology();
        } catch(Exception e) {
            log.error("Error on loading ontology: " + e.getMessage());
        }
    }

    /**
     * Check if a predicate is valid. Valid mean that it is a registered
     * predicate. Predicated are registered through the predicate list.
     * 
     * @param predicateUriReference
     *            predicate URI
     * @return true if predicate is registered, false otherwise
     */
    public static boolean validPredicate(final URI predicateUriReference) {
        return validPredicate(predicateUriReference.toString());
    }

    /**
     * Check if a predicate is valid. Valid mean that it is a registered
     * predicate. Predicated are registered through the predicate list.
     * 
     * @param predicateUriReference
     *            predicate URI
     * @return true if predicate is registered, false otherwise
     */
    public static boolean validPredicate(final String predicateUriReference) {
        if (PREDICATES.contains(predicateUriReference)) {
            return true;
        }
        return false;
    }

    /**
     * Load a file with ontology .
     * 
     * @throws XmlCorruptedException
     *             Thrown if XML is invalid
     * @throws InvalidContentException
     *             Thrown if content is not as expected
     * @throws XmlParserSystemException
     *             Thrown if an unexpected parser exception occurs
     * @throws WebserverSystemException
     *             Thrown if load of list or parsing failed.
     */
    private static void loadOntology() throws XmlCorruptedException,
        WebserverSystemException, XmlParserSystemException,
        InvalidContentException {

        String location = getLocation();
        InputStream in = getInputStream(location);

        PREDICATES = parseOntology(in);
    }

    /**
     * Get location of ontology/predicate list for content relations.
     * 
     * @return location of file with PREDICATES
     * @throws WebserverSystemException
     *             Thrown if loading escidoc configuration failed.
     */
    private static String getLocation() throws WebserverSystemException {
        String location;
        try {
            location =
                EscidocConfiguration.getInstance().get(
                    EscidocConfiguration.CONTENT_RELATIONS_URL);
            if (location == null) {
                location =
                    EscidocConfiguration.getInstance().appendToSelfURL(
                        "/ontologies/mpdl-ontologies/content-relations.xml");
            }
        } catch (IOException ioe) {
            throw new WebserverSystemException(ioe);
        }
        return location;
    }

    /**
     * Get InputStream from location.
     * 
     * @param location
     *            file location
     * @return InputStream from location
     * @throws WebserverSystemException
     *             Thrown if open of InputStream failed.
     */
    private static InputStream getInputStream(final String location)
        throws WebserverSystemException {

        URLConnection conn = null;
        try {
            conn = new URL(location).openConnection();
        }
        catch (MalformedURLException e) {
            throw new WebserverSystemException(e);
        }
        catch (IOException e) {
            throw new WebserverSystemException(e);
        }
        InputStream in = null;
        try {
            in = conn.getInputStream();
        }
        catch (IOException e) {
            throw new WebserverSystemException(e);
        }
        return in;
    }

    /**
     * Parse an InputStream to extract PREDICATES.
     * 
     * @param in
     *            InputStream
     * @return vector with PREDICATES
     * 
     * @throws XmlCorruptedException
     *             Thrown if XML is invalid
     * @throws InvalidContentException
     *             Thrown if content is not as expected
     * @throws XmlParserSystemException
     *             Thrown if an unexpected parser exception occurs
     */
    private static Vector<String> parseOntology(final InputStream in)
        throws XmlCorruptedException, InvalidContentException,
        XmlParserSystemException {
        StaxParser sp = new StaxParser();
        ContentRelationsOntologyHandler handler =
            new ContentRelationsOntologyHandler(sp);
        sp.addHandler(handler);
        try {
            sp.parse(in);
        }
        catch (XmlCorruptedException e) {
            log.debug(e);
            throw new XmlCorruptedException(e);
        }
        catch (InvalidContentException e) {
            log.debug(e);
            throw new InvalidContentException(e);
        }
        catch (XMLStreamException e) {
            log.debug(e);
            throw new XmlParserSystemException(e);
        }
        catch (Exception e) {
            XmlUtility.handleUnexpectedStaxParserException("", e);
        }

        return handler.getPredicates();
    }

    /**
     * Get a vector of registered PREDICATES.
     * 
     * @return vector with PREDICATES
     */
    public static Vector<String> getPredicates() {
        return PREDICATES;
    }
}
