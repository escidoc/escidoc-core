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
import static org.junit.Assert.assertTrue;

import de.escidoc.core.test.EscidocAbstractTest;
import de.escidoc.core.test.EscidocTestBase;
import de.escidoc.core.test.common.resources.BinaryContent;
import de.escidoc.core.test.common.resources.PropertiesProvider;
import de.escidoc.core.test.om.item.contentTools.ContentTestBase;
import de.escidoc.core.test.om.item.contentTools.ImageProperties;
import de.escidoc.core.test.security.client.PWCallback;
import org.apache.http.HttpResponse;
import org.apache.http.Header;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

/**
 * Test the retrieve of binary content. These class tests especially the transformation of images with digilib.
 *
 * @author Steffen Wagner
 */
// DigiLib Tests sollen laut Matthias bis auf weiteres deaktiviert werden.
public class ItemRetrieveContentIT extends ContentTestBase {

    private static final Logger LOGGER = LoggerFactory.getLogger(ItemRetrieveContentIT.class);

    private static final int MAX_RETRIEVES = 30;

    private static final String TRANSFORM_SERVICE_DIGILIB = "digilib";

    /**
     * Test retrieving the binary content of an Item.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOmRtrEscidocCnt1() throws Exception {

        // pre computed
        final String contentSha1Checksum = "77b7a26c5446a46d8152eaa9c5ef5825e70a876b";

        // create Item with test image as content
        String xml = createItemWithImage();
        Document itemDoc = getDocument(xml);
        String itemId = getObjidValue(itemDoc);
        String componentId = getComponentObjidValue(itemDoc, 1);

        String contentType = "image/png";

        File temp = retrieveContentFromFramework(itemId, componentId, contentType);
        String sha1 = computeHashSum(temp);
        // check file with checksum -----------------------------------------------------
        assertEquals("File checksum failed", contentSha1Checksum, sha1);
        removeSilent(temp);

        temp = retrieveContentFromRepository(componentId, contentType);
        sha1 = computeHashSum(temp);

        assertEquals("File checksum failed", contentSha1Checksum, sha1);

        removeSilent(temp);
    }

    /**
     * Test retrieving the binary content of an item (multiple times).
     *
     * @throws Exception If anything fails.
     */
    @Test
    @Ignore
    public void testOmRtrEscidocCnt2() throws Exception {

        for (int i = 0; i < MAX_RETRIEVES; i++) {
            testOmRtrEscidocCnt1();
        }
    }

    /**
     * Test retrieving the binary content of an Item.
     *
     * @throws Exception If anything fails.
     */
    @Test
    @Ignore
    public void testOmRtrEscidocCnt3() throws Exception {

        String components = "";
        // HashMap<String, File> itemComponent = new HashMap<String, File>();

        // add content (multiple images) to item
        HashMap<URL, File> stagingContent = uploadDirAsContent("/src/java" + TEMPLATE_ITEM_PATH + "/content/", 5);

        Iterator<URL> it = stagingContent.keySet().iterator();
        while (it.hasNext()) {

            URL nextUrl = it.next();
            File nextFile = stagingContent.get(nextUrl);
            // TODO later if createComponent() is supported
            // String prepComponent = prepareComponent(nextFile, nextUrl);
            //
            // String componentXml = createComponent(itemId, prepComponent);
            // Document componentDoc =
            // EscidocAbstractTest.getDocument(componentXml);
            // String componentId = getObjidValue(componentDoc);
            // itemComponent.put(componentId, nextFile);
            components += prepareComponentAsItem(nextFile, nextUrl);
        }

        // create Item
        String xmlData =
            EscidocAbstractTest.getTemplateAsString(TEMPLATE_ITEM_PATH + "/rest", "escidoc_item_create_content.xml");
        Document itemDoc = EscidocAbstractTest.getDocument(xmlData);

        Document newItem = (Document) substitute(itemDoc, "/item/components", "######");
        xmlData = toString(newItem, false);
        xmlData = xmlData.replace("######", components);
        String itemXml = create(xmlData);
        itemDoc = EscidocAbstractTest.getDocument(itemXml);
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
                "<img src=\"" + getFrameworkUrl() + "/ir/item/" + itemId + "/components/component/" + compo
                    + "/content\" alt=\"" + compo + "\" border=\"1\" />\n";
        }
        page += "</body>\n";

        // delete old file
        File f = File.createTempFile("test-content", ".html");

        BufferedWriter out = new BufferedWriter(new FileWriter(f));
        out.write(page);
        out.close();

        compIt = componentIds.iterator();
        while (compIt.hasNext()) {
            String componentId = compIt.next();
            // Retrieve content in random order
            File temp = retrieveContentFromFramework(itemId, componentId, contentType);
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
     * @throws Exception If anything fails.
     */
    @Test
    @Ignore
    public void testOmRtrEscidocDigilibCnt3() throws Exception {

        String transformParams = "ws=1.0&wy=0.8&wh=1.8&ww=0.3&wx=0.1&dw=600&dh=300";
        String itemId = "escidoc:ex5";
        String componentId = "escidoc:ex6";

        String tempFileName = "testBinaryData.img";
        String tempRefFileName = "testBinaryData-ref.img";

        BinaryContent ins = retrieveBinaryContent(itemId, componentId, "digilib", "?" + transformParams);

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
            PropertiesProvider.getInstance().getProperty(PropertiesProvider.FEDORA_URL) + "/get/" + componentId
                + "/content";

        String auth =
            PropertiesProvider.getInstance().getProperty(PropertiesProvider.FEDORA_USER) + ":"
                + PropertiesProvider.getInstance().getProperty(PropertiesProvider.FEDORA_PASSWORD);

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
        assertNotEquals("Width of images not differ ", imProp.getImageWidth(), imPropRef.getImageWidth());
        assertNotEquals("Height of images not differ ", imProp.getImageHeight(), imPropRef.getImageHeight());

        // cleanup -------------------------------------------------------------
        if (temp.exists() && !temp.delete()) {
            LOGGER.warn("Could not delete temporary file. " + temp.getPath());
        }

        if (tempRef.exists() && !tempRef.delete()) {
            LOGGER.warn("Could not delete temporary file. " + tempRef.getPath());
        }

        // compare with direct Scaler request ----------------------------------
        // String digilibServer =
        // p.getProperty(PropertiesProvider.DIGILIB_SCALER_URL);

        ins = null;
        conn.disconnect();
    }

    /**
     * Test retrieving the transformed binary content of an item (multiple times).
     *
     * @throws Exception If anything fails.
     */
    @Test
    @Ignore
    public void testOmRtrEscidocDigilibCnt4() throws Exception {

        for (int i = 0; i < MAX_RETRIEVES; i++) {
            testOmRtrEscidocDigilibCnt3();
        }
    }

    /**
     * Test the retrive of transformed binary content from the eSciDoc framework through another client implementation.
     *
     * @throws Exception Thrown if anythings failed.
     */
    @Test
    @Ignore
    public void testOmRtrCntJakarta01() throws Exception {

        String transformParams = "ws=1.0&wy=0.8&wh=1.8&ww=0.3&wx=0.1&dw=600&dh=300";
        String itemId = "escidoc:ex5";
        String componentId = "escidoc:ex6";

        String tempFileName = "testBinaryData.img";
        String tempRefFileName = "testBinaryData-ref.img";

        // BinaryContent ins =
        // retrieveBinaryContent(itemId, componentId, "digilib", "?"
        // + transformParams);
        String href =
            "http://" + PropertiesProvider.getInstance().getProperty("server.name") + ":"
                + PropertiesProvider.getInstance().getProperty("server.port") + "/" + itemId + "/components/component"
                + componentId + "/content/digilib?" + transformParams;

        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpGet get = new HttpGet(href);
        get.setHeader("Cookie", "escidocCookie=" + PWCallback.DEFAULT_HANDLE);
        HttpResponse httpRes = httpClient.execute(get);

        assertEquals("image/jpeg", get.getFirstHeader("Content-type"));

        // write out file
        File temp = File.createTempFile(tempFileName, "tmp");
        ByteArrayOutputStream barray = readBinaryContent(httpRes.getEntity().getContent());
        FileOutputStream fos = new FileOutputStream(temp);
        fos.write(barray.toByteArray());
        fos.flush();
        fos.close();

        // check file size -----------------------------------------------------
        ImageProperties imProp = new ImageProperties(temp);
        assertEquals("Image width ", 295, imProp.getImageWidth());
        assertEquals("Image heigth ", 19, imProp.getImageHeight());

        // compare it with direct request from Repository ----------------------
        String fedoraUrl =
            PropertiesProvider.getInstance().getProperty(PropertiesProvider.FEDORA_URL) + "/get/" + componentId
                + "/content";

        String auth =
            PropertiesProvider.getInstance().getProperty(PropertiesProvider.FEDORA_USER) + ":"
                + PropertiesProvider.getInstance().getProperty(PropertiesProvider.FEDORA_PASSWORD);

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
        assertNotEquals("Width of images not differ ", imProp.getImageWidth(), imPropRef.getImageWidth());
        assertNotEquals("Height of images not differ ", imProp.getImageHeight(), imPropRef.getImageHeight());

        // cleanup -------------------------------------------------------------
        if (temp.exists() && !temp.delete()) {
            LOGGER.warn("Could not delete temporary file. " + temp.getPath());
        }

        if (tempRef.exists() && !tempRef.delete()) {
            LOGGER.warn("Could not delete temporary file. " + tempRef.getPath());
        }

        // compare with direct Scaler request ----------------------------------
        // String digilibServer =
        // p.getProperty(PropertiesProvider.DIGILIB_SCALER_URL);

        conn.disconnect();

    }

    /**
     * Test retrieving content from digilib.
     *
     * @throws Exception If anything fails.
     */
    @Test
    @Ignore
    public void testDigilibRtrCnt1() throws Exception {

        String transformParams = "ws=1.0&wy=0.8&wh=1.8&ww=0.3&wx=0.1&dw=600&dh=300";
        String componentId = "escidoc:ex6";

        String tempFileName = "testBinaryData.img";

        // ---------------------------------------------------------------------
        URL url = getDigilibUrl(componentId, null, TRANSFORM_SERVICE_DIGILIB, transformParams);

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setUseCaches(false);

        if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
            String responseMessage =
                "Retrieving content from transformation service failed. " + conn.getResponseMessage();
            LOGGER.info(responseMessage);
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
            LOGGER.warn("Could not delete temporary file. " + temp.getPath());
        }

    }

    /**
     * Test retrieving content from digilib (multiple times).
     *
     * @throws Exception If anything fails.
     */
    @Test
    @Ignore
    public void testDigilibRtrCnt2() throws Exception {

        for (int i = 0; i < MAX_RETRIEVES; i++) {
            testDigilibRtrCnt1();
        }
    }

    /**
     * Test retrieving content from digilib.
     *
     * @throws Exception If anything fails.
     */
    @Test
    @Ignore
    public void testFedoraRtrCnt1() throws Exception {

        String componentId = "escidoc:ex6";
        String tempFileName = "testBinaryData.img";

        // ---------------------------------------------------------------------
        URL fedoraUrl = new URL(getFedoraUrl(componentId, null));
        String userinfo = fedoraUrl.getUserInfo();

        HttpURLConnection conn = (HttpURLConnection) fedoraUrl.openConnection();
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        conn.setRequestProperty("Authorization", userNamePasswordBase64(userinfo));
        conn.setUseCaches(false);
        conn.connect();

        if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
            String responseMessage =
                "Retrieving content from transformation service failed. " + conn.getResponseMessage();
            LOGGER.info(responseMessage);
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
            LOGGER.warn("Could not delete temporary file. " + temp.getPath());
        }

    }

    /**
     * Test retrieving the binary content from Fedora (multiple times).
     *
     * @throws Exception If anything fails.
     */
    @Test
    @Ignore
    public void testFedoraRtrCnt2() throws Exception {

        for (int i = 0; i < MAX_RETRIEVES; i++) {
            testFedoraRtrCnt1();
        }
    }

    /**
     * Test if filename and mime-type are well set.
     * 
     * @throws Exception If anything is not at expected
     */
    @Test
    public void contentFilenameAndMimeType() throws Exception {

        // create Item with an image as content and set mime-type and filename 
        String itemXml =
            EscidocAbstractTest.getTemplateAsString(TEMPLATE_ITEM_PATH + "/rest", "item_component_metadata.xml");
        Document itemDoc = EscidocAbstractTest.getDocument(create(itemXml));

        // release Item to avoid authentication
        String itemId = getObjidValue(itemDoc);
        submit(itemId, getTheLastModificationParam(false, itemId));
        assignObjectPid(itemId, getPidParam(itemId, "http://localhost/" + itemId));
        assignVersionPid(itemId, getPidParam(itemId, "http://localhost/" + itemId));
        release(itemId, getTheLastModificationParam(false, itemId));

        // get URL to content
        String hrefContent = selectSingleNode(itemDoc, "/item/components/component/content/@href").getNodeValue();

        // get md-record title
        String mdRecordTitle =
            selectSingleNode(itemDoc, "/item/components/component/md-records/md-record/metadata/title")
                .getTextContent();

        // get filename
        String filename = selectSingleNode(itemDoc, "/item/components/component/properties/file-name").getTextContent();

        // get mime-type
        String mimeType = selectSingleNode(itemDoc, "/item/components/component/properties/mime-type").getTextContent();

        String baseUrl = selectSingleNode(itemDoc, "/item/@base").getNodeValue();

        // get HTTP Header
        HttpClient httpclient = new DefaultHttpClient();
        HttpGet httpget = new HttpGet(baseUrl + hrefContent);
        HttpResponse response = httpclient.execute(httpget);

        Header[] headerCType = response.getHeaders("Content-Type");
        assertTrue(headerCType.length == 1);
        assertEquals(mimeType, headerCType[0].getValue());

        Header[] headerDisp = response.getHeaders("Content-Disposition");
        assertTrue(headerDisp.length == 1);
        assertEquals("inline;filename=\"" + filename + "\"", headerDisp[0].getValue());
    }

    /**
     * Create an Item with an image as content (and two dots in file name, see issue INFR-1355).
     * 
     * @return objid of Item
     */
    private String createItemWithImage() throws Exception {

        // create an item an replace the value of the public-status element
        String itemXml = getExampleTemplate("item-minimal-for-create-03.xml");

        Document item = EscidocAbstractTest.getDocument(itemXml);
        substitute(item, "/item/components/component/content/@href", PropertiesProvider.getInstance().getProperty(
            PropertiesProvider.TESTDATA_URL)
            + "/testDocuments/images/head-v0.1.png");
        substitute(item, "/item/components/component/properties/mime-type", "image/png");
        String xmlTmp = toString(item, false);

        return create(xmlTmp);
    }

    /**
     * Compute SHA1 Hash.
     * 
     * @return SHA1
     */
    private String computeHashSum(final File datafile) throws Exception {

        MessageDigest md = MessageDigest.getInstance("SHA1");
        FileInputStream fis = new FileInputStream(datafile);
        byte[] dataBytes = new byte[1024];

        int nread = 0;

        while ((nread = fis.read(dataBytes)) != -1) {
            md.update(dataBytes, 0, nread);
        }
        ;

        byte[] mdbytes = md.digest();

        //convert the byte to hex format
        StringBuffer sb = new StringBuffer("");
        for (int i = 0; i < mdbytes.length; i++) {
            sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));
        }

        return sb.toString();
    }

}
