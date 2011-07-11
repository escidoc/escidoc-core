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
package de.escidoc.core.test.common;

import de.escidoc.core.test.EscidocAbstractTest;
import de.escidoc.core.test.common.fedora.TripleStoreTestBase;
import org.apache.xpath.XPathAPI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.Vector;

/**
 * Tool to count resource in Fedora Repository.
 *
 * @author Steffen Wagner
 */
public class CountRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(CountRepository.class);

    private static final String RDF_TYPE = "<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>";

    /**
     * @param args Program arguments.
     */
    public static void main(final String[] args) {

        try {
            int numOfResources = countResources();
            LOGGER.info(numOfResources + " objects in repository.");
            int numOfItems = countItems();
            LOGGER.info(numOfItems + " Items in repository.");
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Count all resources of the Fedora repository.
     *
     * @return Number of objects.
     * @throws Exception Throws Exception if TripleStore of Fedora request failed.
     */
    public static int countResources() throws Exception {

        Vector<String> objids = obtainObjidsFromTripleStore(RDF_TYPE);

        return objids.size();
    }

    /**
     * Count all resources from eSciDoc type Item of repository.
     *
     * @return Number of Items.
     * @throws Exception Throws Exception if TripleStore of Fedora request failed.
     */
    public static int countItems() throws Exception {

        Vector<String> objids = obtainObjidsOfItemsFromTripleStore();

        return objids.size();
    }

    /**
     * Obtains all objid which fit to the TripleStore predicate and cuts of the objid.
     *
     * @param trsPredicate TripleStore Predicate
     * @return Vector with all objids (Fedora PIDs) which fit to the Predicate.
     * @throws Exception Thrown if obtaining values from TripleStore and extracting failed.
     */
    public static Vector<String> obtainObjidsFromTripleStore(final String trsPredicate) throws Exception {

        Vector<String> objids = new Vector<String>();

        // call value from TripleStore
        TripleStoreTestBase tripleStore = new TripleStoreTestBase();
        String result = tripleStore.requestMPT("* " + trsPredicate + " *", "RDF/XML");
        Document resultDoc = EscidocAbstractTest.getDocument(result);

        // obtain objids
        NodeList nl = XPathAPI.selectNodeList(resultDoc, "/RDF/Description/@about");

        int nlSize = nl.getLength();
        for (int i = 0; i < nlSize; i++) {

            Node node = nl.item(i);
            // cutting of fedora:info/
            String id = node.getTextContent().substring(12);

            objids.add(id);
        }

        return objids;
    }

    /**
     * Obtains all objid of Items which fit to the TripleStore predicate and cuts of the objid.
     *
     * @return Vector with all objids (Fedora PIDs) which fit to the Predicate.
     * @throws Exception Thrown if obtaining values from TripleStore and extracting failed.
     */
    public static Vector<String> obtainObjidsOfItemsFromTripleStore() throws Exception {

        Vector<String> objids = new Vector<String>();

        // call value from TripleStore
        TripleStoreTestBase tripleStore = new TripleStoreTestBase();
        String result =
            tripleStore.requestMPT("* " + RDF_TYPE + " <http://escidoc.de/core/01/resources/Item>", "RDF/XML");
        Document resultDoc = EscidocAbstractTest.getDocument(result);

        // obtain objids
        NodeList nl = XPathAPI.selectNodeList(resultDoc, "/RDF/Description/@about");

        int nlSize = nl.getLength();
        for (int i = 0; i < nlSize; i++) {

            Node node = nl.item(i);
            // cutting of fedora:info/
            String id = node.getTextContent().substring(12);

            objids.add(id);
        }

        return objids;
    }

}
