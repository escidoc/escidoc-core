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
package de.escidoc.core.test.common.client.servlet.st;

import de.escidoc.core.test.common.client.servlet.ClientBase;
import de.escidoc.core.test.common.client.servlet.Constants;

import java.io.InputStream;

/**
 * This client offers access methods to the escidoc REST interface of the StagingFile resource.
 *
 * @author Torsten Tetteroo
 */
public class StagingFileClient extends ClientBase {

    /**
     * Create a StagingFile in the escidoc framework.
     *
     * @param binaryContent The binary content of the staging file.
     * @param mimeType      The mime type of the data.
     * @param filename      The name of the file.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    public Object create(final InputStream binaryContent, final String mimeType, final String filename)
        throws Exception {

        return callEsciDocWithBinaryContent("StagingFile.create", METHOD_CREATE, Constants.HTTP_METHOD_PUT,
            Constants.STAGING_FILE_BASE_URI, new String[] {}, binaryContent, mimeType, filename);
    }

    /**
     * Retrieve the binary content of a StagingFile.
     *
     * @param id The id of the StagingFile.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    public Object retrieve(final String id) throws Exception {

        return callEsciDoc("StagingFile.retrieve", METHOD_RETRIEVE, Constants.HTTP_METHOD_GET,
            Constants.STAGING_FILE_BASE_URI, new String[] { id }, null);
    }

    /**
     * Retrieve the binary content of a StagingFile.
     *
     * @param id The id of the StagingFile.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    public Object retrieveTestData(final String filename) throws Exception {

        return callEsciDoc("StagingFile.retrieve", METHOD_RETRIEVE, Constants.HTTP_METHOD_GET,
            Constants.TEST_DATA_BASE_URI, new String[] { filename }, null);
    }

}
