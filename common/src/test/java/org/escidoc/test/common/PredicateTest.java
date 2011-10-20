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
package org.escidoc.test.common;

import static org.junit.Assert.assertEquals;

import java.net.URI;

import org.junit.Test;

import de.escidoc.core.common.business.fedora.Predicate;

/**
 * Test Predicate
 * 
 * @author SWA
 * 
 */
public class PredicateTest {

    /**
     * Test split of predicate with '#' as separator into name space an local name.
     * 
     * @throws Exception
     */
    @Test
    public void testSplitNamespaceAndLocalname1() throws Exception {

        final URI namespace = new URI("http://www.escidoc.org/ontologie/content-relations#");
        final String localname = "isPartOf";

        Predicate p = new Predicate(namespace + localname);
        assertEquals("Invalid local name", localname, p.getLocalname());
        assertEquals("Invalid name space", namespace, p.getNamespace());
    }

    /**
     * Test split of predicate with '/' as separator into name space an local name.
     * 
     * @throws Exception
     */
    @Test
    public void testSplitNamespaceAndLocalname2() throws Exception {

        final URI namespace = new URI("http://www.escidoc.org/ontologie/content-relations/");
        final String localname = "isPartOf";

        Predicate p = new Predicate(namespace + localname);
        assertEquals("Invalid local name", localname, p.getLocalname());
        assertEquals("Invalid name space", namespace, p.getNamespace());
    }

}
