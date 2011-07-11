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

import de.escidoc.core.test.common.resources.PropertiesProvider;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.StringReader;
import java.util.Properties;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Test suite for the GetRepositoryInfo method of the admin tool.
 *
 * @author André Schenk
 */
public class GetRepositoryInfoIT extends AdminToolTestBase {

    /**
     * Get some information about the repository.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testGetRepositoryInfo() throws Exception {
        // remove style sheet from XML
        BufferedReader reader = new BufferedReader(new StringReader(getRepositoryInfo()));
        StringBuffer output = new StringBuffer();
        String line = null;

        while ((line = reader.readLine()) != null) {
            if (!line.startsWith("<?xml-stylesheet")) {
                output.append(line);
            }
        }

        Properties repositoryInfo = new Properties();

        repositoryInfo.loadFromXML(new ByteArrayInputStream(output.toString().getBytes()));

        // Check Property escidoc-core.build that comes from
        // internal configuration file escidoc-core.constant.properties
        assertNotNull("Property " + PropertiesProvider.ESCIDOC_VERSION + " is null", repositoryInfo
            .getProperty(PropertiesProvider.ESCIDOC_VERSION));

        // Check Property escidoc-core.repository-name that comes from
        // external configuration file escidoc-core.properties
        assertNotNull("Property escidoc-core.repository-name is null", repositoryInfo
            .getProperty("escidoc-core.repository-name"));

        assertTrue("current database structure differs from the internally stored structure", Boolean
            .valueOf(repositoryInfo.getProperty("escidoc-core.database.consistent")));
    }

    /**
     * Get some information about the repository.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testGetIndexConfiguration() throws Exception {
        BufferedReader reader = new BufferedReader(new StringReader(getIndexConfiguration()));
        StringBuffer output = new StringBuffer();
        String line = null;

        while ((line = reader.readLine()) != null) {
            output.append(line);
        }

        assertXmlValidIndexConfiguration(output.toString());
    }
}
