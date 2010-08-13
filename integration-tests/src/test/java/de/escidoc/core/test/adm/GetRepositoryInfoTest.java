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
package de.escidoc.core.test.adm;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.StringReader;
import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Test suite for the GetRepositoryInfo method of the admin tool.
 * 
 * @author SCHE
 * 
 */
public class GetRepositoryInfoTest extends AdminToolTestBase {

    /**
     * The constructor.
     * 
     * @param transport
     *            The transport identifier.
     * 
     * @throws Exception
     *             If anything fails.
     */
    public GetRepositoryInfoTest(final int transport) throws Exception {
        super(transport);
    }

    /**
     * Set up test.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Before
    public void initialize() throws Exception {
    }

    /**
     * Clean up after test.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @After
    public void deinitialize() throws Exception {
    }

    /**
     * Get some information about the repository.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testGetRepositoryInfo() throws Exception {
        // remove style sheet from XML
        BufferedReader reader =
            new BufferedReader(new StringReader(getRepositoryInfo()));
        StringBuffer output = new StringBuffer();
        String line = null;

        while ((line = reader.readLine()) != null) {
            if (!line.startsWith("<?xml-stylesheet")) {
                output.append(line);
            }
        }

        Properties repositoryInfo = new Properties();

        repositoryInfo.loadFromXML(new ByteArrayInputStream(output
            .toString().getBytes()));
        assertTrue("current database structure differs from the internally stored structure", Boolean
            .valueOf(repositoryInfo
                .getProperty("escidoc-core.database.consistent")));
    }
    
    /**
     * Get some information about the repository.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testGetIndexConfiguration() throws Exception {
        BufferedReader reader =
            new BufferedReader(new StringReader(getIndexConfiguration()));
        StringBuffer output = new StringBuffer();
        String line = null;

        while ((line = reader.readLine()) != null) {
            output.append(line);
        }

        assertXmlValidIndexConfiguration(output.toString());
    }
}
