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
package de.escidoc.core.test.om.item;

import static org.junit.Assert.assertEquals;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.w3c.dom.Document;

import de.escidoc.core.test.EscidocRestSoapTestsBase;
import de.escidoc.core.test.common.resources.BinaryContent;
import de.escidoc.core.test.common.resources.PropertiesProvider;
import de.escidoc.core.test.om.item.contentTools.ContentTestBase;
import de.escidoc.core.test.om.item.contentTools.ImageProperties;
import de.escidoc.core.test.security.client.PWCallback;

/**
 * Test the retrieve of binary content. These class tests especially the
 * transformation of images with digilib.
 * 
 * @author SWA
 * 
 */
@RunWith(value = Parameterized.class)
public class ItemRetrieveContentTest extends ContentTestBase {

    private static final int MAX_RETRIEVES = 30;

    private static final String TRANSFORM_SERVICE_DIGILIB = "digilib";

    /**
     * @param transport
     *            The transport identifier.
     */
    public ItemRetrieveContentTest(final int transport) {
        super(transport);
    }

    /**
     * Test retrieving the binary content of an Item.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testOmRtrEscidocCnt1() throws Exception {
        String itemId = "escidoc:ex5";
        String componentId = "escidoc:ex6";
        String contentType = "image/jpeg";

        File temp =
            retrieveContentFromFramework(itemId, componentId, contentType);

        // check file size -----------------------------------------------------
        ImageProperties imProp = new ImageProperties(temp);
        assertEquals("Image width ", 982, imProp.getImageWidth());
        assertEquals("Image heigth ", 95, imProp.getImageHeight());

        File tempRef = retrieveContentFromRepository(componentId, contentType);

        // Asserts -------------------------------------------------------------
        ImageProperties imPropRef = new ImageProperties(tempRef);
        assertEquals("Width of images not differ ", imProp.getImageWidth(),
            imPropRef.getImageWidth());
        assertEquals("Height of images not differ ", imProp.getImageHeight(),
            imPropRef.getImageHeight());

        removeSilent(temp);
        removeSilent(tempRef);
    }

    /**
     * Test retrieving the binary content of an item (multiple times).
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testOmRtrEscidocCnt2() throws Exception {

        for (int i = 0; i < MAX_RETRIEVES; i++) {
            testOmRtrEscidocCnt1();
        }
    }

    /**
     * Test retrieving the binary content of an Item.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testOmRtrEscidocCnt3() throws Exception {

        String components = "";
        // HashMap<String, File> itemComponent = new HashMap<String, File>();

        // add content (multiple images) to item
        HashMap<URL, File> stagingContent =
            uploadDirAsContent("/src/java" + TEMPLATE_ITEM_PATH + "/content/",
                5);

        Iterator<URL> it = stagingContent.keySet().iterator();
        while (it.hasNext()) {

            URL nextUrl = it.next();
            File nextFile = stagingContent.get(nextUrl);
            // TODO later if createComponent() is supported
            // String prepComponent = prepareComponent(nextFile, nextUrl);
            //
            // String componentXml = createComponent(itemId, prepComponent);
            // Document componentDoc =
            // EscidocRestSoapTestsBase.getDocument(componentXml);
            // String componentId = getObjidValue(componentDoc);
            // itemComponent.put(componentId, nextFile);
            components += prepareComponentAsItem(nextFile, nextUrl);
        }

        // create Item
        String xmlData =
            EscidocRestSoapTestsBase.getTemplateAsString(TEMPLATE_ITEM_PATH
                + "/" + getTransport(false), "escidoc_item_create_content.xml");
        Document itemDoc = EscidocRestSoapTestsBase.getDocument(xmlData);

        Document newItem =
            (Document) substitute(itemDoc, "/item/components", "######");
        xmlData = toString(newItem, false);
        xmlData = xmlData.replace("######", components);
        String itemXml = create(xmlData);
        itemDoc = EscidocRestSoapTestsBase.getDocument(itemXml);
        String itemId = getObjidValue(itemDoc);

        submit(itemId, getTheLastModificationParam(false, itemId));
        releaseWithPid(itemId);

        Vector<String> componentIds = getAllComponents(itemId);
        String contentType = "image/jpeg";

        // Prepare webpage
        String page = "<html><head></head><body>\n";
        Iterator<String> compIt = componentIds.iterator();
        while (compIt.hasNext()) {
            String compo = compIt.next();
            page +=
                "<img src=\"http://localhost:8080/ir/item/" + itemId
                    + "/components/component/" + compo + "/content\" alt=\""
                    + compo + "\" border=\"1\" />\n";
        }
        page += "</body>\n";

        // delete old file
        File f = new File("test-content.html");
        if (f.exists()) {
            f.delete();
        }

        BufferedWriter out = new BufferedWriter(new FileWriter(f));
        out.write(page);
        out.close();

        compIt = componentIds.iterator();
        while (compIt.hasNext()) {
            String componentId = compIt.next();
            // Retrieve content in random order
            File temp =
                retrieveContentFromFramework(itemId, componentId, contentType);
            //
            // // check file size
            // -----------------------------------------------------
            // ImageProperties imProp = new ImageProperties(temp);
            // assertEquals("Image width ", 982, imProp.getImageWidth());
            // assertEquals("Image heigth ", 95, imProp.getImageHeight());
            //
            // File tempRef = retrieveContentFromRepository(componentId,
            // contentType);
            //
            // // Asserts
            // -------------------------------------------------------------
            // ImageProperties imPropRef = new ImageProperties(tempRef);
            // assertEquals("Width of images not differ ",
            // imProp.getImageWidth(),
            // imPropRef.getImageWidth());
            // assertEquals("Height of images not differ ",
            // imProp.getImageHeight(),
            // imPropRef.getImageHeight());
            //
            removeSilent(temp);
            // removeSilent(tempRef);
        }
    }

    /**
     * Test retrieving the transformed binary content of an Item.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testOmRtrEscidocDigilibCnt3() throws Exception {

        String transformParams =
            "ws=1.0&wy=0.8&wh=1.8&ww=0.3&wx=0.1&dw=600&dh=300";
        String itemId = "escidoc:ex5";
        String componentId = "escidoc:ex6";

        String tempFileName = "testBinaryData.img";
        String tempRefFileName = "testBinaryData-ref.img";

        BinaryContent ins =
            retrieveBinaryContent(itemId, componentId, "digilib", "?"
                + transformParams);

        assertEquals("image/jpeg", ins.getMimeType());

        // write out file
        File temp = File.createTempFile(tempFileName, "tmp");
        ByteArrayOutputStream barray = readBinaryContent(ins.getContent());
        FileOutputStream fos = new FileOutputStream(temp);
        fos.write(barray.toByteArray());
        fos.flush();
        fos.close();
        ins.getContent().close();

        // check file size -----------------------------------------------------
        ImageProperties imProp = new ImageProperties(temp);
        assertEquals("Image width ", 295, imProp.getImageWidth());
        assertEquals("Image heigth ", 19, imProp.getImageHeight());

        // compare it with direct request from Repository ----------------------
        String fedoraUrl =
            this.properties.getProperty(PropertiesProvider.FEDORA_URL)
                + "/get/" + componentId + "/content";

        String auth =
            this.properties.getProperty(PropertiesProvider.FEDORA_USER)
                + ":"
                + this.properties
                    .getProperty(PropertiesProvider.FEDORA_PASSWORD);

        URL url = new URL(fedoraUrl);

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setDoInput(true);
        conn.setRequestProperty("Authorization", userNamePasswordBase64(auth));
        conn.setUseCaches(false);
        conn.connect();

        barray = readBinaryContent(conn.getInputStream());
        File tempRef = File.createTempFile(tempRefFileName, "tmp");
        fos = new FileOutputStream(tempRef);
        fos.write(barray.toByteArray());
        fos.flush();
        fos.close();

        ImageProperties imPropRef = new ImageProperties(tempRef);

        // Asserts -------------------------------------------------------------
        assertNotEquals("Width of images not differ ", imProp.getImageWidth(),
            imPropRef.getImageWidth());
        assertNotEquals("Height of images not differ ",
            imProp.getImageHeight(), imPropRef.getImageHeight());

        // cleanup -------------------------------------------------------------
        if (temp.exists() && !temp.delete()) {
            log.warn("Could not delete temporary file. " + temp.getPath());
        }

        if (tempRef.exists() && !tempRef.delete()) {
            log.warn("Could not delete temporary file. " + tempRef.getPath());
        }

        // compare with direct Scaler request ----------------------------------
        // String digilibServer =
        // p.getProperty(PropertiesProvider.DIGILIB_SCALER_URL);

        ins = null;
        conn.disconnect();
    }

    /**
     * Test retrieving the transformed binary content of an item (multiple
     * times).
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testOmRtrEscidocDigilibCnt4() throws Exception {

        for (int i = 0; i < MAX_RETRIEVES; i++) {
            testOmRtrEscidocDigilibCnt3();
        }
    }

    /**
     * Test the retrive of transformed binary content from the eSciDoc framework
     * through another client implementation.
     * 
     * @throws Exception
     *             Thrown if anythings failed.
     */
    @Test
    public void testOmRtrCntJakarta01() throws Exception {

        String transformParams =
            "ws=1.0&wy=0.8&wh=1.8&ww=0.3&wx=0.1&dw=600&dh=300";
        String itemId = "escidoc:ex5";
        String componentId = "escidoc:ex6";

        String tempFileName = "testBinaryData.img";
        String tempRefFileName = "testBinaryData-ref.img";

        // BinaryContent ins =
        // retrieveBinaryContent(itemId, componentId, "digilib", "?"
        // + transformParams);
        String href =
            "http://" + this.properties.getProperty("server.name") + ":"
                + this.properties.getProperty("server.port") + "/" + itemId
                + "/components/component" + componentId + "/content/digilib?"
                + transformParams;

        HttpClient httpClient = new HttpClient();
        HttpMethod get = new GetMethod(href);
        get.setRequestHeader("Cookie", "escidocCookie="
            + PWCallback.DEFAULT_HANDLE);
        httpClient.executeMethod(get);

        assertEquals("image/jpeg", get.getResponseHeader("Content-type"));

        // write out file
        File temp = File.createTempFile(tempFileName, "tmp");
        ByteArrayOutputStream barray =
            readBinaryContent(get.getResponseBodyAsStream());
        FileOutputStream fos = new FileOutputStream(temp);
        fos.write(barray.toByteArray());
        fos.flush();
        fos.close();
        get.releaseConnection();

        // check file size -----------------------------------------------------
        ImageProperties imProp = new ImageProperties(temp);
        assertEquals("Image width ", 295, imProp.getImageWidth());
        assertEquals("Image heigth ", 19, imProp.getImageHeight());

        // compare it with direct request from Repository ----------------------
        String fedoraUrl =
            this.properties.getProperty(PropertiesProvider.FEDORA_URL)
                + "/get/" + componentId + "/content";

        String auth =
            this.properties.getProperty(PropertiesProvider.FEDORA_USER)
                + ":"
                + this.properties
                    .getProperty(PropertiesProvider.FEDORA_PASSWORD);

        URL url = new URL(fedoraUrl);

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setDoInput(true);
        conn.setRequestProperty("Authorization", userNamePasswordBase64(auth));
        conn.setUseCaches(false);
        conn.connect();

        barray = readBinaryContent(conn.getInputStream());
        File tempRef = File.createTempFile(tempRefFileName, "tmp");
        fos = new FileOutputStream(tempRef);
        fos.write(barray.toByteArray());
        fos.flush();
        fos.close();

        ImageProperties imPropRef = new ImageProperties(tempRef);

        // Asserts -------------------------------------------------------------
        assertNotEquals("Width of images not differ ", imProp.getImageWidth(),
            imPropRef.getImageWidth());
        assertNotEquals("Height of images not differ ",
            imProp.getImageHeight(), imPropRef.getImageHeight());

        // cleanup -------------------------------------------------------------
        if (temp.exists() && !temp.delete()) {
            log.warn("Could not delete temporary file. " + temp.getPath());
        }

        if (tempRef.exists() && !tempRef.delete()) {
            log.warn("Could not delete temporary file. " + tempRef.getPath());
        }

        // compare with direct Scaler request ----------------------------------
        // String digilibServer =
        // p.getProperty(PropertiesProvider.DIGILIB_SCALER_URL);

        conn.disconnect();

    }

    /**
     * Test retrieving content from digilib.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testDigilibRtrCnt1() throws Exception {

        String transformParams =
            "ws=1.0&wy=0.8&wh=1.8&ww=0.3&wx=0.1&dw=600&dh=300";
        String componentId = "escidoc:ex6";

        String tempFileName = "testBinaryData.img";

        // ---------------------------------------------------------------------
        URL url =
            getDigilibUrl(componentId, null, TRANSFORM_SERVICE_DIGILIB,
                transformParams);

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setUseCaches(false);

        if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
            String responseMessage =
                "Retrieving content from transformation service failed. "
                    + conn.getResponseMessage();
            log.info(responseMessage);
            throw new Exception(responseMessage);
        }

        String mimeType = conn.getContentType();

        assertEquals("image/jpeg", mimeType);
        InputStream ins = conn.getInputStream();

        // write out file
        File temp = File.createTempFile(tempFileName, "tmp");
        ByteArrayOutputStream barray = readBinaryContent(ins);
        FileOutputStream fos = new FileOutputStream(temp);
        fos.write(barray.toByteArray());
        fos.flush();
        fos.close();
        ins.close();
        conn.disconnect();

        // check file size -----------------------------------------------------
        ImageProperties imProp = new ImageProperties(temp);
        assertEquals("Image width ", 295, imProp.getImageWidth());
        assertEquals("Image heigth ", 19, imProp.getImageHeight());

        // cleanup -------------------------------------------------------------
        temp = new File(tempFileName);
        if (temp.exists() && !temp.delete()) {
            log.warn("Could not delete temporary file. " + temp.getPath());
        }

    }

    /**
     * Test retrieving content from digilib (multiple times).
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testDigilibRtrCnt2() throws Exception {

        for (int i = 0; i < MAX_RETRIEVES; i++) {
            testDigilibRtrCnt1();
        }
    }

    /**
     * Test retrieving content from digilib.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testFedoraRtrCnt1() throws Exception {

        String componentId = "escidoc:ex6";
        String tempFileName = "testBinaryData.img";

        // ---------------------------------------------------------------------
        URL fedoraUrl = new URL(getFedoraUrl(componentId, null));
        String userinfo = fedoraUrl.getUserInfo();

        HttpURLConnection conn = (HttpURLConnection) fedoraUrl.openConnection();
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        conn.setRequestProperty("Authorization",
            userNamePasswordBase64(userinfo));
        conn.setUseCaches(false);
        conn.connect();

        if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
            String responseMessage =
                "Retrieving content from transformation service failed. "
                    + conn.getResponseMessage();
            log.info(responseMessage);
            throw new Exception(responseMessage);
        }

        String mimeType = conn.getContentType();
        assertEquals("image/jpeg", mimeType);
        InputStream ins = conn.getInputStream();

        // add the file extention
        // tempFileName += "." + getFileExtentionFromContentType(mimeType);

        // write out file
        File temp = File.createTempFile(tempFileName, "tmp");
        ByteArrayOutputStream barray = readBinaryContent(ins);
        FileOutputStream fos = new FileOutputStream(temp);
        fos.write(barray.toByteArray());
        fos.flush();
        fos.close();
        ins.close();
        conn.disconnect();

        // check file size -----------------------------------------------------
        ImageProperties imProp = new ImageProperties(temp);
        assertEquals("Image width ", 982, imProp.getImageWidth());
        assertEquals("Image heigth ", 95, imProp.getImageHeight());

        // cleanup -------------------------------------------------------------
        if (temp.exists() && !temp.delete()) {
            log.warn("Could not delete temporary file. " + temp.getPath());
        }

    }

    /**
     * Test retrieving the binary content from Fedora (multiple times).
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testFedoraRtrCnt2() throws Exception {

        for (int i = 0; i < MAX_RETRIEVES; i++) {
            testFedoraRtrCnt1();
        }
    }

}
