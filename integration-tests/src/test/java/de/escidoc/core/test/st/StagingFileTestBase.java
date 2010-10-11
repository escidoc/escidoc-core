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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import junit.framework.TestCase;

import org.apache.http.HttpResponse;

import de.escidoc.core.test.EscidocTestBase;
import de.escidoc.core.test.common.client.servlet.st.StagingFileClient;
import de.escidoc.core.test.common.resources.ResourceProvider;

/**
 * Base class for testing the implementation of the StagingFile.
 * 
 * @author TTE
 * 
 */
public abstract class StagingFileTestBase extends EscidocTestBase {

    private StagingFileClient stagingFileClient = null;

    // Data for access to postgres database
    private static Connection dbConnetion = null;

    private static String dbDriver = "org.postgresql.Driver";

    private static String dbHost = "localhost";

    private static String dbPort = "5432";

    private static String dbName = "escidoc-core";

    private static String dbUser = "postgres";

    private static String dbPassword = "postgres";

    /**
     * @param transport
     *            The transport identifier.
     */
    protected StagingFileTestBase(final int transport) {
        super(transport);
        this.stagingFileClient = new StagingFileClient(transport);
    }

    /**
     * @return Returns the itemClient.
     */
    public StagingFileClient getStagingFileClient() {
        return stagingFileClient;
    }

    /**
     * Test retrieving a StagingFile from the mock framework.
     * 
     * @param id
     *            The id of the StagingFile.
     * @return The <code>HttpMethod</code> holding the binary content of the
     *         retrieved StagingFile.
     * @throws Exception
     *             If anything fails.
     */
    protected HttpResponse retrieveStagingFile(final String id) throws Exception {

        Object result = getStagingFileClient().retrieve(id);
        if (result instanceof HttpResponse) {
            HttpResponse httpRes = (HttpResponse) result;
            return httpRes;
        }
        else {
            TestCase.fail("Unexpected result type ["
                + result.getClass().getName());
            return null;
        }
    }

    /**
     * Test creating a StagingFile in the framework.
     * 
     * @param binaryContent
     *            The binary content of the staging file.
     * @param mimeType
     *            The mime type of the data.
     * @param filename
     *            The name of the file.
     * @return The <code>HttpMethod</code> object.
     * @throws Exception
     *             If anything fails.
     */
    protected HttpResponse create(
        final InputStream binaryContent, final String mimeType,
        final String filename) throws Exception {

        Object result =
            getStagingFileClient().create(binaryContent, mimeType, filename);
        if (result instanceof HttpResponse) {
            HttpResponse httpRes = (HttpResponse) result;
            return httpRes;
        }
        else {
            TestCase.fail("Unsupported result type ["
                + result.getClass().getName() + "]");
            return null;
        }

    }

    /**
     * Gets a database connection.
     * 
     * @return Returns a database connection.
     * @throws Exception
     *             Thrown if anything fails.
     */
    private static Connection getDbConnection() throws Exception {
        if (dbConnetion == null) {

            Class.forName(dbDriver);
            String jdbcUrl =
                "jdbc:postgresql://" + dbHost + ":" + dbPort + "/" + dbName;
            dbConnetion =
                DriverManager.getConnection(jdbcUrl, dbUser, dbPassword);

        }
        return dbConnetion;
    }

    /**
     * Get the staging file for token from the database.
     * 
     * @param token
     *            The token.
     * @return The staging file.
     * @throws Exception
     *             Thrown if anything fails.
     */
    public static StagingFile retrieveStagingFileFromDatabase(final String token)
        throws Exception {
        StagingFile result = null;
        Connection db = getDbConnection();
        if (db != null) {
            Statement sql = db.createStatement();
            String select =
                "select * from st.staging_file where token='" + token + "';";
            ResultSet resultSet;

            resultSet = sql.executeQuery(select);

            if ((resultSet != null) && (resultSet.next())) {
                result =
                    new StagingFile(resultSet.getLong("expiry_ts"), resultSet
                        .getString("reference"), resultSet
                        .getString("mime_type"), resultSet.getBoolean("upload"));
                result.setToken(token);
            }
            sql.close();
        }
        return result;
    }

    /**
     * Update the given staging file in the database.
     * 
     * @param stagingFile
     *            The staging file.
     * @throws Exception
     *             Thrown if anything fails.
     */
    public static void updateStagingFile(final StagingFile stagingFile)
        throws Exception {
        Connection db = getDbConnection();
        if (db != null) {
            db.setAutoCommit(true);
            Statement sql = db.createStatement();
            String uploadValue = "true";
            if (!stagingFile.isUpload()) {
                uploadValue = "false";
            }
            String update =
                "update st.staging_file set expiry_ts="
                    + stagingFile.getExpiryTs() + ", reference='"
                    + stagingFile.getReference() + "', mime_type='"
                    + stagingFile.getMimeType() + "', upload=" + uploadValue
                    + " where token='" + stagingFile.getToken() + "';";

            sql.executeUpdate(update);

            sql.close();
        }
    }

    /**
     * Delete the staging file from the database.
     * 
     * @param stagingFile
     *            The staging file.
     * @throws Exception
     *             Thrown if anything fails.
     */
    public static void deleteStagingFile(final StagingFile stagingFile)
        throws Exception {
        deleteStagingFile(stagingFile.getToken());
    }

    /**
     * Delete the staging file for token from the database.
     * 
     * @param token
     *            The token.
     * @throws Exception
     *             Thrown if anything fails.
     */
    public static void deleteStagingFile(final String token) throws Exception {
        Connection db = getDbConnection();
        if (db != null) {
            db.setAutoCommit(true);
            Statement sql = db.createStatement();
            String delete =
                "delete from st.staging_file where token='" + token + "';";
            sql.executeUpdate(delete);
            sql.close();
        }
    }

    /**
     * Delete the file identified by the token from the file system without
     * updating persistent information.<br>
     * This method can be used to simulate manually deleting a staging area
     * file, e.g. by an administrator.
     * 
     * @param token
     *            The token identifying the file.
     * @throws Exception
     *             Thrown if anything fails.
     */
    public static void deletePhysicalFile(final String token) throws Exception {

        StagingFile stagingFile = retrieveStagingFileFromDatabase(token);
        if (stagingFile != null) {
            if (stagingFile.getReference() != null) {
                File file = new File(stagingFile.getReference());
                file.delete();
            }
        }

    }

    /**
     * Set token as expired by changing its expiry timestamp to a time previous
     * to the current time.
     * 
     * @param token
     *            The token to set as expired.
     * @throws Exception
     *             Thrown if anything fails.
     */
    public static void setExpired(final String token) throws Exception {

        StagingFile stagingFile = retrieveStagingFileFromDatabase(token);
        if (stagingFile != null) {
            stagingFile.setExpiryTs(System.currentTimeMillis() - 1);
            updateStagingFile(stagingFile);
        }
    }

    /**
     * Gets a file input stream for a source file identified by given file name.
     * 
     * @param filename
     *            The file name of the source, e.g. UploadTest.zip
     * @return Returns a file input stream to the source.
     * @throws IOException
     *             Thrown if the resource cannot be found.
     */
    public static InputStream getFileInputStream(final String filename)
        throws IOException {

        InputStream result = ResourceProvider.getFileInputStream(filename);
        return result;
    }

    /**
     * Assert the content of the received response in givent http method object
     * matches the conten of the last file copied to staging area.
     * 
     * @param method
     *            The http method containing the result to check.
     * @param source
     *            The name of the original source that shall be compared with
     *            the response's content.
     * @throws IOException
     *             If an i/O operation failed.
     */
    public static void assertResponseContentMatchesSourceFile(
        final HttpResponse httpRes, final String source) throws IOException {

        InputStream responseContent = httpRes.getEntity().getContent();
        TestCase.assertNotNull("GET failed! Response's content not found",
            responseContent);
        InputStream origContent =
            StagingFileTestBase.getFileInputStream(source);
        TestCase.assertNotNull("Source not found! [" + source + "]",
            responseContent);
        byte[] bufferR = new byte[1];
        byte[] bufferO = new byte[1];
        int i = 0;
        try {
            int lengthR = responseContent.read(bufferR);
            int lengthO = origContent.read(bufferO);
            TestCase.assertEquals("GET failed! Lengths of response content"
                + " and original content do not match. Reading of " + (i + 1)
                + ". byte failed (expected = #bytes read from original source,"
                + " was = #bytes read from response content", lengthO, lengthR);
            while (lengthR != -1 && lengthO != -1) {
                TestCase.assertEquals("GET failed! Response's content does not"
                    + " match expected result. " + (i + 1)
                    + ". byte check failed,", bufferO[0], bufferR[0]);
                i++;
                lengthR = responseContent.read(bufferR);
                lengthO = origContent.read(bufferO);
                TestCase.assertEquals("GET failed! Lengths of response content"
                    + " and original content do not match. Reading of "
                    + (i + 1) + ". byte failed (expected = #bytes read from "
                    + " original source, was = #bytes read from response"
                    + " content)", lengthO, lengthR);
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
