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

import java.io.InputStream;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpMethod;
import org.w3c.dom.Document;

import de.escidoc.core.common.exceptions.remote.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.remote.application.notfound.StagingFileNotFoundException;
import de.escidoc.core.test.EscidocRestSoapTestsBase;
import de.escidoc.core.test.common.client.servlet.Constants;
import de.escidoc.core.test.common.client.servlet.HttpHelper;
import de.escidoc.core.test.security.client.PWCallback;

/**
 * Test suite for the StagingFile.
 * 
 * @author TTE
 * 
 */
public abstract class StagingFileTest extends StagingFileTestBase {

    private final String testUploadFile = "UploadTest.zip";

    private final String testUploadFileMimeType = "application/zip";

    /**
     * @param transport
     *            The transport identifier.
     * 
     * @throws Exception
     *             If anything fails.
     */
    protected StagingFileTest(final int transport) throws Exception {

        super(transport);
        if (transport != Constants.TRANSPORT_REST) {
            throw new Exception("Provided Transport not supported ["
                + transport + "]");
        }
    }

    /**
     * Set up servlet test.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Override
    protected void setUp() throws Exception {

        super.setUp();
    }

    /**
     * Clean up after servlet test.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Override
    protected void tearDown() throws Exception {

        super.tearDown();
    }

    /**
     * Test successfully creating a StagingFile.
     * 
     * @test.name Successful Creation of StagingFile.
     * @test.id ST_Csf_1
     * @test.input Binary Content
     * @test.inputDescription: Body contains data, data's mime type set in
     *                         header
     * @test.expected: Http status OK (200),XML representation of a StagingFile
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    public void testSTCsf1() throws Exception {

        InputStream fileInputStream =
            StagingFileTestBase.getFileInputStream(testUploadFile);

        HttpMethod httpMethod = null;
        try {
            httpMethod =
                create(fileInputStream, testUploadFileMimeType, testUploadFile);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.failException(e);
        }
        assertNotNull("No HTTPMethod. ", httpMethod);
        assertHttpStatusOfMethod("Create failed", httpMethod);
        final String stagingFileXml = getResponseBodyAsUTF8(httpMethod);
        httpMethod.releaseConnection();
        EscidocRestSoapTestsBase.assertXmlValidStagingFile(stagingFileXml);
        Document document =
            EscidocRestSoapTestsBase.getDocument(stagingFileXml);
        assertXmlExists("No xlink type", document, "/staging-file/@type");
        assertXmlExists("No xlink href", document, "/staging-file/@href");
        assertXmlExists("No last modification date", document,
            "/staging-file/@last-modification-date");
    }

    /**
     * Test declining the creation of a StagingFile without binary content.
     * 
     * @test.name Successful Missing Binary Content in Create.
     * @test.id ST_Csf_2
     * @test.input Binary Content
     * @test.inputDescription: Body contains no data, data's mime type set in
     *                         header
     * @test.expected: Http status 417
     * @test.status To Be Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    public void testSTCsf2() throws Exception {

        try {
            create(null, testUploadFileMimeType, testUploadFile);
            EscidocRestSoapTestsBase
                .failMissingException(MissingMethodParameterException.class);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.assertExceptionType("",
                MissingMethodParameterException.class, e);
        }
    }

    /**
     * Test successfully retrieving staging file.
     * 
     * @test.name Successful Retrieval of StagingFile
     * @test.id ST_RSF_1
     * @test.input Token
     * @test.inputDescription: Valid upload token as parameter for that a
     *                         StagingFile has been created in the staging area.
     *                         Mime type of file has been specified during
     *                         creation.
     * 
     * @test.expected: Http status OK (200), File content in http body, Mime
     *                 type of body in http header.
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    public void testSTRsf1() throws Exception {

        InputStream fileInputStream =
            StagingFileTestBase.getFileInputStream(testUploadFile);
        HttpMethod httpMethod = null;
        try {
            httpMethod =
                create(fileInputStream, testUploadFileMimeType, testUploadFile);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.failException(e);
        }
        assertNotNull("No HTTPMethod. ", httpMethod);
        assertHttpStatusOfMethod("Create failed", httpMethod);
        Document document =
            EscidocRestSoapTestsBase
                .getDocument(getResponseBodyAsUTF8(httpMethod));
        httpMethod.releaseConnection();
        String objidValue = getIdFromRootElementHref(document);

        try {
            PWCallback.setHandle("");
            httpMethod = retrieveStagingFile(objidValue);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.failException(e);
        }
        finally {
            PWCallback.resetHandle();
        }
        assertNotNull("Got no HTTP method object", httpMethod);
        assertHttpStatusOfMethod("Retrieve failed", httpMethod);
        final Header contentTypeHeader =
            httpMethod.getResponseHeader(HttpHelper.HTTP_HEADER_CONTENT_TYPE);
        assertNotNull("Retrieve failed! No returned mime type found",
            contentTypeHeader);
        assertEquals("Retrieve failed! The returned mime type is wrong,",
            testUploadFileMimeType, contentTypeHeader.getValue());
        StagingFileTestBase.assertResponseContentMatchesSourceFile(httpMethod,
            testUploadFile);
        httpMethod.releaseConnection();
    }

    /**
     * Test declining the retrieval of a StagingFile with missing parameter
     * token.
     * 
     * @test.name: Missing Token in Retrieve
     * @test.id: ST_Rsf_2
     * @test.input: Token
     * @test.inputDescription: No token is sent as parameter.
     * @test.expected: MissingMethodParameterException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    public void testSTRsf2() throws Exception {

        try {
            retrieveStagingFile(null);
            EscidocRestSoapTestsBase
                .failMissingException(MissingMethodParameterException.class);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.assertExceptionType(
                "Unexpected exception, ",
                MissingMethodParameterException.class, e);
        }
    }

    /**
     * Test declining the retrieval of a StagingFile with unknown token.
     * 
     * @test.name: Retrieve Staging File - Unknown Token
     * @test.id: ST_Rsf_4
     * @test.input: Token
     * @test.inputDescription: Token that is unknown to the system.
     * @test.expected: Http status NOT_FOUND (404)
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    public void testSTRsf4() throws Exception {

        try {
            retrieveStagingFile(UNKNOWN_ID);
            EscidocRestSoapTestsBase
                .failMissingException(StagingFileNotFoundException.class);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase
                .assertExceptionType("Unexpected exception, ",
                    StagingFileNotFoundException.class, e);
        }
    }

    /**
     * Test declining the retrieval of a StagingFile with providing the id of an
     * existing resource of another type.
     * 
     * @test.name: Retrieve Staging File - Wrong Token
     * @test.id: ST_Rsf_4
     * @test.input: Existing context id instead of token.
     * @test.expected: Http status NOT_FOUND (404)
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    public void testSTRsf4_2() throws Exception {

        try {
            retrieveStagingFile(CONTEXT_ID);
            EscidocRestSoapTestsBase
                .failMissingException(StagingFileNotFoundException.class);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase
                .assertExceptionType("Unexpected exception, ",
                    StagingFileNotFoundException.class, e);
        }
    }

    /**
     * Test declining the retrieval of an expired StagingFile.
     * 
     * @test.name: Expired Token in Retrieve
     * @test.id: ST_RSF_5
     * @test.input: Token
     * @test.inputDescription: Token that is expired.
     * @test.expected: Http status NOT_FOUND (404)
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    public void testSTRsf5() throws Exception {

        InputStream fileInputStream =
            StagingFileTestBase.getFileInputStream(testUploadFile);
        HttpMethod httpMethod = null;
        try {
            httpMethod =
                create(fileInputStream, testUploadFileMimeType, testUploadFile);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.failException(e);
        }
        assertNotNull("No HTTPMethod. ", httpMethod);
        assertHttpStatusOfMethod("Create failed", httpMethod);
        Document document =
            EscidocRestSoapTestsBase
                .getDocument(getResponseBodyAsUTF8(httpMethod));
        httpMethod.releaseConnection();

        String objidValue = getIdFromRootElementHref(document);
        StagingFileTestBase.setExpired(objidValue);

        try {
            httpMethod = retrieveStagingFile(objidValue);
            EscidocRestSoapTestsBase
                .failMissingException(StagingFileNotFoundException.class);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.assertExceptionType(
                StagingFileNotFoundException.class, e);
        }
        finally {
            httpMethod.releaseConnection();
        }
    }

    /**
     * Test declining the retrieval of an StagingFile for that the file had been
     * manually (e.g. by a administrator) removed from the staging area.
     * 
     * @test.name: StagingFile with removed file
     * @test.id: ST_Rsf_7
     * @test.input: Token
     * @test.inputDescription: Valid token for that a StangingFile had been
     *                         created but its file has been manually removed
     *                         from the file system (e.g. by the administrator).
     * @test.expected: Http status NOT_FOUND (404)
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    public void testSTRsf7() throws Exception {

        InputStream fileInputStream =
            StagingFileTestBase.getFileInputStream(testUploadFile);
        HttpMethod httpMethod = null;
        try {
            httpMethod =
                create(fileInputStream, testUploadFileMimeType, testUploadFile);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.failException(e);
        }
        assertNotNull("No HTTPMethod. ", httpMethod);
        assertHttpStatusOfMethod("Create failed", httpMethod);
        Document document =
            EscidocRestSoapTestsBase
                .getDocument(getResponseBodyAsUTF8(httpMethod));
        httpMethod.releaseConnection();
        String objidValue = getIdFromRootElementHref(document);
        StagingFileTestBase.deletePhysicalFile(objidValue);

        try {
            httpMethod = retrieveStagingFile(objidValue);
            EscidocRestSoapTestsBase
                .failMissingException(
                    "Upload Servlet's get method did not decline"
                        + " getting an object for which a file had been uploaded but"
                        + " has been manually removed, ",
                    StagingFileNotFoundException.class);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase
                .assertExceptionType(
                    "Upload Servlet's get method did not decline"
                        + " getting an object for which a file had been uploaded but"
                        + " has been manually removed, correctly, ",
                    StagingFileNotFoundException.class, e);
        }
        finally {
            httpMethod.releaseConnection();
        }
    }

    /**
     * Test declining the retrieval of a staging file that has been previously
     * retrieved.
     * 
     * @test.name Repeated Retrieval of Staging File
     * @test.id ST_RSF_8
     * @test.input Token
     * @test.inputDescription: Valid upload token as parameter for that a
     *                         StagingFile has been created in the staging area,
     *                         and the staging file has been previously
     *                         retrieved.
     * 
     * @test.expected: StagingFileNotFoundException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    public void testSTRsf8() throws Exception {

        InputStream fileInputStream =
            StagingFileTestBase.getFileInputStream(testUploadFile);
        HttpMethod httpMethod = null;
        try {
            httpMethod =
                create(fileInputStream, testUploadFileMimeType, testUploadFile);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.failException(e);
        }
        assertNotNull("No HTTPMethod. ", httpMethod);
        assertHttpStatusOfMethod("Create failed", httpMethod);
        Document document =
            EscidocRestSoapTestsBase
                .getDocument(getResponseBodyAsUTF8(httpMethod));
        httpMethod.releaseConnection();
        String objidValue = getIdFromRootElementHref(document);

        try {
            httpMethod = retrieveStagingFile(objidValue);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.failException(e);
        }

        try {
            httpMethod = retrieveStagingFile(objidValue);
            EscidocRestSoapTestsBase.failMissingException(
                "Upload Servlet's get method did not decline"
                    + " repeated retrieval of a staging file, ",
                StagingFileNotFoundException.class);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.assertExceptionType(
                "Upload Servlet's get method did not decline"
                    + " repeated retrieval of a staging file, correctly, ",
                StagingFileNotFoundException.class, e);
        }
        finally {
            httpMethod.releaseConnection();
        }
    }

}
