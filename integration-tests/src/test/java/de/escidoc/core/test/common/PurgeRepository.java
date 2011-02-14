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

import de.escidoc.core.test.EscidocRestSoapTestBase;
import de.escidoc.core.test.common.fedora.Client;
import de.escidoc.core.test.common.fedora.TripleStoreTestBase;
import de.escidoc.core.test.common.logger.AppLogger;
import org.apache.xpath.XPathAPI;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.Iterator;
import java.util.Vector;

/**
 * Tool to purge all escidoc resources from Fedora Repository. Precondition is a
 * running TripleStore where Fedora has written the Triples and, of course, a
 * running Fedora.
 * 
 * @author SWA
 * 
 */
public class PurgeRepository {

    private static AppLogger log =
        new AppLogger(PurgeRepository.class.getName());

    /**
     * @param args
     *            Program arguments.
     */
    public static void main(final String[] args) {

        try {
            int numPurged = purgeAllEscidoc();
            log.info(numPurged + " Objects purged from repository.");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Purging all resources with escidoc prefix.
     * 
     * @return Number of purged objects.
     * @throws Exception
     *             Throws Exception if TripleStore of Fedora request failed.
     */
    private static int purgeAllEscidoc() throws Exception {

        int purgedObjects = 0;

        Client fedoraClient = new Client();

        Vector<String> objids =
            obtainObjidsFromTripleStore("<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>");

        Iterator<String> it = objids.iterator();
        int toRemove = objids.size();
        while (it.hasNext()) {

            String objid = it.next();

            // prevent purging of persistent objects and examples
            fedoraClient.purgeObject(objid, "purged through Test Project");

            log.debug(objid + " purged (" + --toRemove + " left)");
        }
        return purgedObjects;
    }

    /**
     * Obtains all objid which fit to the TripleStore predicate and cuts of the
     * objid.
     * 
     * @param trsPredicate
     *            TripleStore Predicate
     * @return Vector with all objids (Fedora PIDs) which fit to the Predicate.
     * @throws Exception
     */
    private static Vector<String> obtainObjidsFromTripleStore(
        final String trsPredicate) throws Exception {

        Vector<String> objids = new Vector<String>();

        // call value from TripleStore
        TripleStoreTestBase tripleStore = new TripleStoreTestBase();
        String result =
            tripleStore.requestMPT("* " + trsPredicate + " *", "RDF/XML");
        Document resultDoc = EscidocRestSoapTestBase.getDocument(result);

        // obtain objids
        NodeList nl =
            XPathAPI.selectNodeList(resultDoc, "/RDF/Description/@about");

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
