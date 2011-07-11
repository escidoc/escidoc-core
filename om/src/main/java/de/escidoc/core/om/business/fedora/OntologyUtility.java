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

import de.escidoc.core.common.exceptions.application.ApplicationException;
import de.escidoc.core.common.exceptions.system.EncodingSystemException;
import de.escidoc.core.common.exceptions.system.IntegritySystemException;
import de.escidoc.core.common.exceptions.system.TripleStoreSystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.exceptions.system.XmlParserSystemException;
import de.escidoc.core.common.util.configuration.EscidocConfiguration;
import de.escidoc.core.common.util.stax.StaxParser;
import de.escidoc.core.common.util.xml.XmlUtility;
import de.escidoc.core.om.business.stax.handler.OntologyHandler;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * @author Rozita Friedman
 */
public final class OntologyUtility {

    /**
     * Private constructor to avoid instantiation.
     */
    private OntologyUtility() {
    }

    /**
     * Check if content-relations ontologie contains predicate.
     *
     * @param predicateUriReference The predicate with uri reference
     * @return true if predicate is defined within ontologie, false otherwise.
     * @throws de.escidoc.core.common.exceptions.system.WebserverSystemException
     * @throws de.escidoc.core.common.exceptions.system.XmlParserSystemException
     * @throws de.escidoc.core.common.exceptions.system.EncodingSystemException
     */
    public static boolean checkPredicate(final String predicateUriReference) throws WebserverSystemException,
        EncodingSystemException, XmlParserSystemException {

        final InputStream in;
        try {
            final String ontologyLocation =
                EscidocConfiguration.getInstance().appendToSelfURL("/ontologies/mpdl-ontologies/content-relations.xml");
            final URLConnection conn = new URL(ontologyLocation).openConnection();
            in = conn.getInputStream();
        }
        catch (final IOException e) {
            throw new WebserverSystemException(e);
        }

        final StaxParser sp = new StaxParser();

        final OntologyHandler ontologyHandler = new OntologyHandler(sp, predicateUriReference);
        sp.addHandler(ontologyHandler);
        try {
            sp.parse(in);
        }
        catch (final ApplicationException e) {
            XmlUtility.handleUnexpectedStaxParserException("", e);
        }
        catch (final XMLStreamException e) {
            XmlUtility.handleUnexpectedStaxParserException("", e);
        }
        catch (final IntegritySystemException e) {
            XmlUtility.handleUnexpectedStaxParserException("", e);
        }
        catch (final TripleStoreSystemException e) {
            XmlUtility.handleUnexpectedStaxParserException("", e);
        }

        return ontologyHandler.isExist();
    }
}
