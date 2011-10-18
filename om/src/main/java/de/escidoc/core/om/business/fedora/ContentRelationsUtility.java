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
import de.escidoc.core.common.util.stax.StaxParser;
import de.escidoc.core.common.util.xml.XmlUtility;
import de.escidoc.core.om.business.stax.handler.ContentRelationsOntologyHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * Handle content relations for framework.
 *
 * @author Rozita Friedman, Steffen Wagner
 */
public final class ContentRelationsUtility {

    private static final Logger LOGGER = LoggerFactory.getLogger(ContentRelationsUtility.class);

    private static final List<String> PREDICATES = new ArrayList<String>();

    static {
        try {
            loadOntology();
        }
        catch (Exception e) {
            LOGGER.error("Error on loading ontology: " + e.getMessage());
        }
    }

    /**
     * Private constructor to avoid instantiation.
     */
    private ContentRelationsUtility() {
    }

    /**
     * Check if a predicate is valid. Valid mean that it is a registered predicate. Predicated are registered through
     * the predicate list.
     *
     * @param predicateUriReference predicate URI
     * @return true if predicate is registered, false otherwise
     */
    public static boolean validPredicate(final URI predicateUriReference) {
        return validPredicate(predicateUriReference.toString());
    }

    /**
     * Check if a predicate is valid. Valid mean that it is a registered predicate. Predicated are registered through
     * the predicate list.
     *
     * @param predicateUriReference predicate URI
     * @return true if predicate is registered, false otherwise
     */
    public static boolean validPredicate(final String predicateUriReference) {
        return PREDICATES.contains(predicateUriReference);
    }

    /**
     * Load a file with ontology .
     *
     * @throws XmlCorruptedException    Thrown if XML is invalid
     * @throws InvalidContentException  Thrown if content is not as expected
     * @throws XmlParserSystemException Thrown if an unexpected parser exception occurs
     * @throws WebserverSystemException Thrown if load of list or parsing failed.
     */
    private static void loadOntology() throws XmlCorruptedException, WebserverSystemException,
        XmlParserSystemException, InvalidContentException {

        PREDICATES.clear();
        final String[] locations = getLocations();

        for (final String location : locations) {
            final InputStream in = getInputStream(location);
            PREDICATES.addAll(parseOntology(in));
            try {
                in.close();
            }
            catch (IOException e) {
                if (LOGGER.isWarnEnabled()) {
                    LOGGER.warn("Could not close stream.");
                }
                else if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Could not close stream.", e);
                }
            }
        }
    }

    /**
     * Get location of ontology/predicate list for content relations.
     *
     * @return location of file with PREDICATES
     * @throws WebserverSystemException Thrown if loading escidoc configuration failed.
     */
    private static String[] getLocations() {

        String[] locations;
        String location = EscidocConfiguration.getInstance().get(EscidocConfiguration.CONTENT_RELATIONS_URL);

        // default location
        // FIXME use a more qualified place for default configurations
        if (location == null) {
            locations =
                new String[] { EscidocConfiguration.getInstance().appendToSelfURL(
                    "/ontologies/mpdl-ontologies/content-relations.xml") };
        }
        else {
            locations = location.split("\\s+");

            // expand local paths with selfUrl 
            for (int i = 0; i < locations.length; i++) {
                if (!locations[i].startsWith("http://")) {
                    locations[i] = EscidocConfiguration.getInstance().appendToSelfURL(locations[i]);
                }
            }
        }

        return locations;
    }

    /**
     * Get InputStream from location.
     *
     * @param location file location
     * @return InputStream from location
     * @throws WebserverSystemException Thrown if open of InputStream failed.
     */
    private static InputStream getInputStream(final String location) throws WebserverSystemException {

        final URLConnection conn;
        try {
            conn = new URL(location).openConnection();
        }
        catch (final MalformedURLException e) {
            if (LOGGER.isWarnEnabled()) {
                LOGGER.warn("Problem while loading resource '" + location + "'.");
            }
            else if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Problem while loading resource '" + location + "'.", e);
            }
            throw new WebserverSystemException(e);
        }
        catch (final IOException e) {
            if (LOGGER.isWarnEnabled()) {
                LOGGER.warn("Problem while loading resource '" + location + "'.");
            }
            else if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Problem while loading resource '" + location + "'.", e);
            }
            throw new WebserverSystemException(e);
        }
        final InputStream in;
        try {
            in = conn.getInputStream();
        }
        catch (final IOException e) {
            if (LOGGER.isWarnEnabled()) {
                LOGGER.warn("Problem while loading resource '" + location + "'.");
            }
            else if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Problem while loading resource '" + location + "'.", e);
            }
            throw new WebserverSystemException(e);
        }
        return in;
    }

    /**
     * Parse an InputStream to extract PREDICATES.
     *
     * @param in InputStream
     * @return vector with PREDICATES
     * @throws XmlCorruptedException    Thrown if XML is invalid
     * @throws InvalidContentException  Thrown if content is not as expected
     * @throws XmlParserSystemException Thrown if an unexpected parser exception occurs
     */
    private static List<String> parseOntology(final InputStream in) throws XmlCorruptedException,
        InvalidContentException, XmlParserSystemException {
        final StaxParser sp = new StaxParser();
        final ContentRelationsOntologyHandler handler = new ContentRelationsOntologyHandler(sp);
        sp.addHandler(handler);
        try {
            sp.parse(in);
        }
        catch (final XmlCorruptedException e) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Problem while parsing.", e);
            }
            throw new XmlCorruptedException(e);
        }
        catch (final InvalidContentException e) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Problem while parsing.", e);
            }
            throw new InvalidContentException(e);
        }
        catch (final XMLStreamException e) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Problem while parsing.", e);
            }
            throw new XmlParserSystemException(e);
        }
        catch (final Exception e) {
            XmlUtility.handleUnexpectedStaxParserException("", e);
        }

        return handler.getPredicates();
    }

    /**
     * Get a vector of registered PREDICATES.
     *
     * @return vector with PREDICATES
     */
    public static List<String> getPredicates() {
        return PREDICATES;
    }
}
