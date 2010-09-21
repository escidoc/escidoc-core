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

import static org.junit.Assert.fail;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.escidoc.core.common.exceptions.remote.application.invalid.InvalidXmlException;
import de.escidoc.core.test.EscidocRestSoapTestsBase;
import de.escidoc.core.test.common.client.servlet.Constants;
import de.escidoc.core.test.common.client.servlet.HttpHelper;
import de.escidoc.core.test.security.client.PWCallback;

/**
 * Test the mock implementation of the item resource.
 * 
 * @author MSC
 * 
 */
@RunWith(value = Parameterized.class)
public class ContentStreamsTest extends ItemTestBase {

    /**
     * @param transport
     *            The transport identifier.
     */
    public ContentStreamsTest(final int transport) {
        super(transport);
    }

    /**
     * Test successfully creating item with three content streams.
     * 
     * @throws Exception
     */
    @Test
    public void testContentStreams() throws Exception {
        String createdItemId = null;
        try {
            String itemXml =
                create(EscidocRestSoapTestsBase.getTemplateAsString(
                    TEMPLATE_ITEM_PATH + "/" + getTransport(false),
                    "escidoc_item_198_for_create_3content-streams.xml"));

            createdItemId = getIdFromRootElement(itemXml);
            assertXmlValidItem(itemXml);
            // System.out.println(itemXml);

            Document itemDoc = getDocument(itemXml);
            assertContentStreamsOf_escidoc_item_198_for_create_3content_streams(
                createdItemId, itemDoc, false);

            String xml = retrieve(createdItemId);
            assertXmlValidItem(xml);

            itemDoc = getDocument(xml);
            assertContentStreamsOf_escidoc_item_198_for_create_3content_streams(
                createdItemId, itemDoc, false);

            // check content stream subresources
            String contentStreamXml =
                retrieveContentStream(createdItemId, "internal_xml");
            assertXmlValidItem(contentStreamXml);

            if (getTransport() == Constants.TRANSPORT_REST) {
                String content =
                    retrieveContentStreamContent(createdItemId, "internal_xml");
                assertNotNull(content);
            }

        }
        finally {
            if (createdItemId != null) {
                delete(createdItemId);
            }
        }
    }

    /**
     * Test successfully retrieving item with three content streams.
     * 
     * @throws Exception
     */
    @Test
    public void testRetrieveContentStreams() throws Exception {
        String createdItemId = null;
        try {
            String itemXml =
                create(EscidocRestSoapTestsBase.getTemplateAsString(
                    TEMPLATE_ITEM_PATH + "/" + getTransport(false),
                    "escidoc_item_198_for_create_3content-streams.xml"));

            createdItemId = getIdFromRootElement(itemXml);

            String contentStreamsXml = retrieveContentStreams(createdItemId);
            assertXmlValidItem(contentStreamsXml);
            Document itemDoc = getDocument(contentStreamsXml);
            assertContentStreamsOf_escidoc_item_198_for_create_3content_streams(
                createdItemId, itemDoc, true);
        }
        finally {
            if (createdItemId != null) {
                delete(createdItemId);
            }
        }
    }

    /**
     * Test successfully retrieving content of content streams.
     * 
     * @throws Exception
     */
    @Test
    public void testRetrieveContentFromContentStreams() throws Exception {
        String createdItemId = null;
        try {
            String itemXml =
                create(EscidocRestSoapTestsBase.getTemplateAsString(
                    TEMPLATE_ITEM_PATH + "/" + getTransport(false),
                    "escidoc_item_198_for_create_3content-streams.xml"));

            createdItemId = getIdFromRootElement(itemXml);

            String contentStreamsXml = retrieveContentStreams(createdItemId);
            NodeList nl =
                selectNodeList(getDocument(contentStreamsXml),
                    "//content-stream/@href");
            int c = nl.getLength();
            for (int i = 0; i < c; i++) {
                Node hrefNode = nl.item(i);
                String href = hrefNode.getNodeValue();
                if (href.startsWith("/")) {
                    href =
                        Constants.PROTOCOL + "://" + Constants.HOST_PORT + href;
                }
                // System.out.println(i + ": " + href);

                HttpClient httpClient = new DefaultHttpClient();
                HttpGet get = new HttpGet(href);
                get.setHeader("Cookie", "escidocCookie="
                    + PWCallback.DEFAULT_HANDLE);
                HttpResponse res  = httpClient.execute(get);
                byte[] result = EntityUtils.toByteArray(res.getEntity());

                assertHttpStatusOK(
                    "Retrieving content stream '" + href + "': ", res);

            }
        }
        finally {
            delete(createdItemId);
        }
    }

    /**
     * Test successfully updating content streams.
     * 
     * @throws Exception
     */
    @Test
    public void testUpdateContentStreamsValues() throws Exception {
        String createdItemId = null;
        try {
            String itemXml =
                create(EscidocRestSoapTestsBase.getTemplateAsString(
                    TEMPLATE_ITEM_PATH + "/" + getTransport(false),
                    "escidoc_item_198_for_create_3content-streams.xml"));

            createdItemId = getIdFromRootElement(itemXml);
            assertXmlValidItem(itemXml);
            // System.out.println(itemXml);

            Document itemDoc = getDocument(itemXml);
            // change updateable attributes
            // content-streams:
            // NOT persistent: xlink:title, xlink:href
            // removable: xlink:type
            String origTypeContentStreams = null;
            String origTitleContentStreams = null;
            String origHrefContentStreams = null;
            if (getTransport() == Constants.TRANSPORT_REST) {
                origTypeContentStreams =
                    selectSingleNode(itemDoc, "/item/content-streams/@type")
                        .getNodeValue();
                origTitleContentStreams =
                    selectSingleNode(itemDoc, "/item/content-streams/@title")
                        .getNodeValue();
                origHrefContentStreams =
                    selectSingleNode(itemDoc, "/item/content-streams/@href")
                        .getNodeValue();
                Node typeAttributeRemoveNode =
                    selectSingleNodeAsserted(itemDoc, "/item/content-streams");
                NamedNodeMap attMap = typeAttributeRemoveNode.getAttributes();
                attMap.removeNamedItem("xlink:type");
                substitute(itemDoc, "/item/content-streams/@title", "something");
                substitute(itemDoc, "/item/content-streams/@href", "something");
            }
            // content-stream:
            // NOT persistent: xlink:type, xlink:title, href (managed)
            // persistent: mime-type, href (external-url)
            Node typeAttributeRemoveNode =
                selectSingleNodeAsserted(itemDoc,
                    "/item/content-streams/content-stream[@storage = 'external-managed']");
            NamedNodeMap attMap = typeAttributeRemoveNode.getAttributes();
            attMap.removeNamedItem("xlink:type");
            String newTitle = "something";
            substitute(
                itemDoc,
                "/item/content-streams/content-stream[@storage = 'external-managed']/@title",
                newTitle);
            String newHref =
                Constants.PROTOCOL
                    + "://"
                    + Constants.HOST_PORT
                    + selectSingleNodeAsserted(itemDoc,
                        "/item/content-streams/content-stream[@name = 'internal_xml']/@href")
                        .getNodeValue();
            substitute(
                itemDoc,
                "/item/content-streams/content-stream[@storage = 'external-managed']/@href",
                newHref);
            String newMimeType = "text/xml";
            substitute(
                itemDoc,
                "/item/content-streams/content-stream[@storage = 'external-managed']/@mime-type",
                newMimeType);

            String updateXml = toString(itemDoc, false);
            String updatedXml = update(createdItemId, updateXml);
            Document updatedDoc = getDocument(updatedXml);

            assertXmlValidItem(updatedXml);
            // assertContentStreamsOf_escidoc_item_198_for_create_3content_streams(
            // createdItemId, updatedDoc, false);

            // content-streams: xlink:type, xlink:title, xlink:href must not be
            // changed
            if (getTransport() == Constants.TRANSPORT_REST) {
                selectSingleNodeAsserted(updatedDoc,
                    "/item/content-streams[@type = '" + origTypeContentStreams
                        + "']");
                selectSingleNodeAsserted(updatedDoc,
                    "/item/content-streams[@title = '"
                        + origTitleContentStreams + "']");
                selectSingleNodeAsserted(updatedDoc,
                    "/item/content-streams[@href = '" + origHrefContentStreams
                        + "']");
            }
            // content-stream: xlink:type, xlink:href must not be changed
            selectSingleNodeAsserted(
                updatedDoc,
                "/item/content-streams/content-stream[@storage = 'external-managed' and @type = 'simple']");
            selectSingleNodeAsserted(
                updatedDoc,
                "/item/content-streams/content-stream[@storage = 'external-managed' and @href = '/ir/item/"
                    + createdItemId
                    + "/content-streams/content-stream/external_image/content']");
            // new xlink:title and mime-type
            selectSingleNodeAsserted(
                updatedDoc,
                "/item/content-streams/content-stream[@storage = 'external-managed' and @title = '"
                    + newTitle + "']");
            selectSingleNodeAsserted(
                updatedDoc,
                "/item/content-streams/content-stream[@storage = 'external-managed' and @mime-type = '"
                    + newMimeType + "']");
        }
        finally {
            if (createdItemId != null) {
                delete(createdItemId);
            }
        }
    }

    /**
     * Test successfully creating item with content stream with inline xml
     * content.
     * 
     * @throws Exception
     */
    @Test
    public void testCreateInlineContentContentStream() throws Exception {
        String createdItemId = null;
        try {
            String itemXml =
                create(EscidocRestSoapTestsBase.getTemplateAsString(
                    TEMPLATE_ITEM_PATH + "/" + getTransport(false),
                    "escidoc_item_198_for_create_inline_content-stream.xml"));

            createdItemId = getIdFromRootElement(itemXml);
            assertXmlValidItem(itemXml);
            // System.out.println(itemXml);

            Document itemDoc = getDocument(itemXml);
            selectSingleNodeAsserted(itemDoc,
                "/item/content-streams/content-stream[@name = 'toc']/"
                    + "toc[@ID = 'meins']/div[@ID = 'rootNode']/"
                    + "ptr[@ID = 'rootNodePtr']");
            // assertContentStreamsOf_escidoc_item_198_for_create_3content_streams(
            // createdItemId, itemDoc, false);

            String xml = retrieve(createdItemId);
            assertXmlValidItem(xml);

            itemDoc = getDocument(xml);
            selectSingleNodeAsserted(itemDoc,
                "/item/content-streams/content-stream[@name = 'toc']/"
                    + "toc[@ID = 'meins']/div[@ID = 'rootNode']/"
                    + "ptr[@ID = 'rootNodePtr']");
            // assertContentStreamsOf_escidoc_item_198_for_create_3content_streams(
            // createdItemId, itemDoc, false);
        }
        finally {
            if (createdItemId != null) {
                delete(createdItemId);
            }
        }
    }

    /**
     * Test successfully updating item with content stream with inline xml
     * content. After updating, which creates version 2, version 1 of content
     * stream content is retrieved and checked.
     * 
     * @throws Exception
     */
    @Test
    public void testUpdateInlineContentContentStream() throws Exception {
        String createdItemId = null;
        try {
            String itemXml =
                create(EscidocRestSoapTestsBase.getTemplateAsString(
                    TEMPLATE_ITEM_PATH + "/" + getTransport(false),
                    "escidoc_item_198_for_create_inline_content-stream.xml"));

            createdItemId = getIdFromRootElement(itemXml);
            assertXmlValidItem(itemXml);
            // System.out.println(itemXml);

            Document itemDoc = getDocument(itemXml);
            selectSingleNodeAsserted(itemDoc,
                "/item/content-streams/content-stream[@name = 'toc']/"
                    + "toc[@ID = 'meins']/div[@ID = 'rootNode']/"
                    + "ptr[@ID = 'rootNodePtr']");

            Node tocDiv =
                selectSingleNode(itemDoc,
                    "/item/content-streams/content-stream[@name = 'toc']/"
                        + "toc[@ID = 'meins']/div");
            tocDiv.getParentNode().removeChild(tocDiv);

            String updateItem = toString(itemDoc, false);
            String updatedItem = update(createdItemId, updateItem);
            assertXmlValidItem(updatedItem);
            itemDoc = getDocument(updatedItem);
            Node n =
                selectSingleNode(itemDoc,
                    "/item/content-streams/content-stream[@name = 'toc']/"
                        + "toc[@ID = 'meins']/div[@ID = 'rootNode']/"
                        + "ptr[@ID = 'rootNodePtr']");
            assertNull(n);
            n =
                selectSingleNode(itemDoc,
                    "/item/content-streams/content-stream[@name = 'toc']/"
                        + "toc[@ID = 'meins']/div");
            assertNull(n);
            selectSingleNodeAsserted(itemDoc,
                "/item/content-streams/content-stream[@name = 'toc']/"
                    + "toc[@ID = 'meins']");

            // retrieve old version of content

            String tocHref =
                HttpHelper.createUrl(Constants.PROTOCOL, Constants.HOST_PORT,
                    Constants.ITEM_BASE_URI, new String[] {
                        createdItemId + ":1",
                        "/content-streams/content-stream/toc/content" });
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(tocHref);
            httpGet.setHeader("Cookie", "escidocCookie="
                + PWCallback.DEFAULT_HANDLE);
            HttpResponse httpRes = httpClient.execute(httpGet);
            String oldInlineContent = EntityUtils.toString(httpRes.getEntity());

            assertHttpStatusOK("Retrieving content stream '" + tocHref + "': ",
                httpRes);

            selectSingleNodeAsserted(getDocument(oldInlineContent),
                "/toc[@ID = 'meins']/div[@ID = 'rootNode']/"
                    + "ptr[@ID = 'rootNodePtr']");

        }
        finally {
            if (createdItemId != null) {
                delete(createdItemId);
            }
        }
    }

    /**
     * Test decline update unchangable values in content streams.
     * 
     * @throws Exception
     */
    // FIXME decide if storage, mime-type is ignored on update
    @Ignore
    @Test
    public void NOtestUpdateContentStreamsUnchangableValues() throws Exception {
        String createdItemId = null;
        try {
            String itemXml =
                create(EscidocRestSoapTestsBase.getTemplateAsString(
                    TEMPLATE_ITEM_PATH + "/" + getTransport(false),
                    "escidoc_item_198_for_create_3content-streams.xml"));

            createdItemId = getIdFromRootElement(itemXml);
            assertXmlValidItem(itemXml);
            // System.out.println(itemXml);

            Document itemDoc = getDocument(itemXml);
            Document updateDoc;
            // change unchangable attributes
            // content-streams:
            boolean rootIsContentStreams = false;
            if (rootIsContentStreams) {
                // @last-modification-date
                updateDoc = itemDoc;
                substitute(updateDoc,
                    "/item/content-streams/@last-modification-date", "");
            }

            // change unchangable attributes
            // content-stream:
            // @mime-type, @storage
            updateDoc = itemDoc;
            substitute(
                updateDoc,
                "/item/content-streams/content-stream[@storage='external-url']/@storage",
                "internal-managed");
            try {
                update(createdItemId, toString(updateDoc, false));
                fail("No exception if storage changed.");
            }
            catch (Exception e) {
                Class ec = InvalidXmlException.class;
                assertExceptionType("Wrong exception if storage changed.", ec,
                    e);
            }

            updateDoc = getDocument(retrieve(createdItemId));
            substitute(updateDoc,
                "/item/content-streams/content-stream[1]/@mime-type",
                "test/escidoc");
            try {
                update(createdItemId, toString(updateDoc, false));
                fail("No exception if mime-type changed.");
            }
            catch (Exception e) {
                Class ec = InvalidXmlException.class;
                assertExceptionType("Wrong exception if mime-type changed.",
                    ec, e);
            }

        }
        catch (Exception e) {
            throw e;
        }
    }

    /**
     * Test successfully creating a minimal Item with three content streams.
     * 
     * @throws Exception
     *             Thrown if content-streams differ from expectations.
     */
    @Test
    public void testContentStreams02() throws Exception {

        String createdItemId = null;
        try {
            String itemXml =
                create(EscidocRestSoapTestsBase.getTemplateAsString(
                    TEMPLATE_ITEM_PATH + "/" + getTransport(false),
                    "escidoc_item_for_create_3content-streams.xml"));

            createdItemId = getIdFromRootElement(itemXml);
            assertXmlValidItem(itemXml);

            String xml = retrieve(createdItemId);
            assertXmlValidItem(xml);

            // check content stream subresources
            String contentStreamXml =
                retrieveContentStream(createdItemId, "internal_xml");
            assertXmlValidItem(contentStreamXml);

            if (getTransport() == Constants.TRANSPORT_REST) {
                String content =
                    retrieveContentStreamContent(createdItemId, "internal_xml");
                assertNotNull(content);
            }

        }
        finally {
            if (createdItemId != null) {
                delete(createdItemId);
            }
        }
    }

}
