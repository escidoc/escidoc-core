/*
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the Common Development and Distribution License, Version 1.0
 * only (the "License"). You may not use this file except in compliance with the License.
 *
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE or http://www.escidoc.de/license. See the License for
 * the specific language governing permissions and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each file and include the License file at
 * license/ESCIDOC.LICENSE. If applicable, add the following below this CDDL HEADER, with the fields enclosed by
 * brackets "[]" replaced with your own identifying information: Portions Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
 *
 * Copyright 2006-2011 Fachinformationszentrum Karlsruhe Gesellschaft fuer wissenschaftlich-technische Information mbH
 * and Max-Planck-Gesellschaft zur Foerderung der Wissenschaft e.V. All rights reserved. Use is subject to license
 * terms.
 */
package de.escidoc.core.common.business.fedora;

import java.net.URI;
import java.net.URISyntaxException;

import de.escidoc.core.common.exceptions.application.invalid.InvalidContentException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Content Relation Predicate.
 * 
 * @author SWA
 * 
 */
public class Predicate {

    private static final Logger LOGGER = LoggerFactory.getLogger(Predicate.class);

    private URI namespace;

    private String localname;

    /**
     * Content Relation Predicate.
     * 
     * Predicate: http://www.escidoc.org/ontologie/content-relations#isPartOf = Namespace:
     * http://www.escidoc.org/ontologie/content-relations# and Local name: isPartOf
     * 
     * Predicate: http://www.escidoc.org/ontologie/content-relations/isPartOf = Namespace:
     * http://www.escidoc.org/ontologie/content-relations/ and Local name: isPartOf
     * 
     * @param predicate
     */
    public Predicate(final String predicate) throws InvalidContentException {

        splitNamespaceAndLocalname(predicate);
    }

    /**
     * 
     * @return
     */
    public URI getNamespace() {
        return this.namespace;
    }

    /**
     * 
     * @return
     */
    public String getLocalname() {
        return this.localname;
    }

    /**
     * Split Predicate in name space and local name. Values are stored into class attributes.
     * 
     * @param predicate
     * @throws URISyntaxException
     *             Thrown if name space is not a valid URI
     */
    private void splitNamespaceAndLocalname(final String predicate) throws InvalidContentException {

        // compute split position -------------
        int pos = predicate.lastIndexOf('#');
        // contains # (e.g. http://www.escidoc.org/ontologie/content-relations#isPartOf)

        if (pos <= 0) {
            // contains # (e.g. http://www.escidoc.org/ontologie/content-relations/isPartOf)
            pos = predicate.lastIndexOf('/');
            if (pos == predicate.length()) {
                LOGGER.debug("Predicate '" + predicate + "' has no valid local name.");
                throw new InvalidContentException("Predicate '" + predicate + "' has no valid local name.");
            }
        }

        // split -----------------
        try {
            this.namespace = new URI(predicate.substring(0, pos + 1));
        }
        catch (URISyntaxException e) {
            throw new InvalidContentException("Predicate '" + predicate + "' has invalid namespace.", e);
        }
        this.localname = predicate.substring(pos + 1, predicate.length());

    }
}
