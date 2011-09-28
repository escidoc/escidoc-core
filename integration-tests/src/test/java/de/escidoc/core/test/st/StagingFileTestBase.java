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
package de.escidoc.core.test.st;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

import de.escidoc.core.test.EscidocAbstractTest;
import de.escidoc.core.test.common.client.servlet.om.ItemClient;
import de.escidoc.core.test.common.client.servlet.st.StagingFileClient;
import de.escidoc.core.test.common.resources.ResourceProvider;

/**
 * Base class for testing the implementation of the StagingFile.
 *
 * @author Torsten Tetteroo
 */
public class StagingFileTestBase extends EscidocAbstractTest {

    private StagingFileClient stagingFileClient = null;

    private ItemClient itemClient = null;

    protected StagingFileTestBase() {
        this.stagingFileClient = new StagingFileClient();
        this.itemClient = new ItemClient();
    }

    /**
     * @return Returns the itemClient.
     */
    public StagingFileClient getStagingFileClient() {
        return stagingFileClient;
    }

    /**
     * @return Returns the itemClient.
     */
    public ItemClient getItemClient() {
        return itemClient;
    }

    /**
     * Test retrieving a StagingFile from the mock framework.
     *
     * @param id The id of the StagingFile.
     * @return The <code>HttpMethod</code> holding the binary content of the retrieved StagingFile.
     * @throws Exception If anything fails.
     */
    protected HttpResponse retrieveStagingFile(final String id) throws Exception {

        Object result = getStagingFileClient().retrieve(id);
        if (result instanceof HttpResponse) {
            HttpResponse httpRes = (HttpResponse) result;
            return httpRes;
        }
        else {
            fail("Unexpected result type [" + result.getClass().getName());
            return null;
        }
    }

    /**
     * Test creating a StagingFile in the framework.
     *
     * @param binaryContent The binary content of the staging file.
     * @param mimeType      The mime type of the data.
     * @param filename      The name of the file.
     * @return The <code>HttpMethod</code> object.
     * @throws Exception If anything fails.
     */
    protected HttpResponse create(final InputStream binaryContent, final String mimeType, final String filename)
        throws Exception {

        Object result = getStagingFileClient().create(binaryContent, mimeType, filename);
        if (result instanceof HttpResponse) {
            HttpResponse httpRes = (HttpResponse) result;
            return httpRes;
        }
        else {
            fail("Unsupported result type [" + result.getClass().getName() + "]");
            return null;
        }

    }

    /**
     * Test creating a StagingFile in the framework.
     *
     * @param binaryContent The binary content of the staging file.
     * @param mimeType      The mime type of the data.
     * @param filename      The name of the file.
     * @return The <code>HttpMethod</code> object.
     * @throws Exception If anything fails.
     */
    protected InputStream retrieveTestData(final String filename) throws Exception {

        Object result = getStagingFileClient().retrieveTestData(filename);
        if (result instanceof HttpResponse) {
            HttpResponse httpRes = (HttpResponse) result;
            return new ByteArrayInputStream(EntityUtils.toByteArray(httpRes.getEntity()));
        }
        else {
            fail("Unsupported result type [" + result.getClass().getName() + "]");
            return null;
        }

    }

    /**
     * Assert the content of the received response in givent http method object matches the conten of the last file
     * copied to staging area.
     *
     * @param httpRes The http method containing the result to check.
     * @param source  The name of the original source that shall be compared with the response's content.
     * @throws IOException If an i/O operation failed.
     */
    public static void assertResponseContentMatchesSourceFile(final HttpResponse httpRes, final InputStream origContent)
        throws IOException {

        InputStream responseContent = httpRes.getEntity().getContent();
        assertNotNull("GET failed! Response's content not found", responseContent);
        byte[] bufferR = new byte[1];
        byte[] bufferO = new byte[1];
        int i = 0;
        try {
            int lengthR = responseContent.read(bufferR);
            int lengthO = origContent.read(bufferO);
            assertEquals("GET failed! Lengths of response content" + " and original content do not match. Reading of "
                + (i + 1) + ". byte failed (expected = #bytes read from original source,"
                + " was = #bytes read from response content", lengthO, lengthR);
            while (lengthR != -1 && lengthO != -1) {
                assertEquals("GET failed! Response's content does not" + " match expected result. " + (i + 1)
                    + ". byte check failed,", bufferO[0], bufferR[0]);
                i++;
                lengthR = responseContent.read(bufferR);
                lengthO = origContent.read(bufferO);
                assertEquals("GET failed! Lengths of response content"
                    + " and original content do not match. Reading of " + (i + 1)
                    + ". byte failed (expected = #bytes read from "
                    + " original source, was = #bytes read from response" + " content)", lengthO, lengthR);
            }

        }
        finally {
            if (responseContent != null) {
                responseContent.close();
            }
            if (origContent != null) {
                origContent.close();
            }
        }
    }

}
