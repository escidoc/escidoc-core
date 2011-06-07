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
package de.escidoc.core.test.om.container;

import de.escidoc.core.test.EscidocAbstractTest;
import de.escidoc.core.test.common.client.servlet.HttpHelper;
import org.apache.http.HttpResponse;
import org.apache.http.client.protocol.RequestAddCookies;
import org.apache.http.client.protocol.ResponseProcessCookies;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.servlet.http.HttpServletResponse;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Test all references of a Container.
 *
 * @author Steffen Wagner
 */
public class ContainerReferenceIT extends ContainerTestBase {

    private String theContainerId;

    private String theContainerXml;

    private String theItemId;

    /**
     * Set up servlet test.
     *
     * @throws Exception If anything fails.
     */
    @Before
    public void setUp() throws Exception {

        this.theItemId = createItem();

        String xmlData = getContainerTemplate("create_container_v1.1-forItem.xml");

        theContainerXml = create(xmlData.replaceAll("##ITEMID##", this.theItemId));
        this.theContainerId = getObjidValue(this.theContainerXml);
    }

    /**
     * Check if all object references within the properties section are valid after createContainer.
     *
     * @throws Exception Thrown if an reference could not be solved.
     */
    @Test
    public void testReferenceProp1() throws Exception {

        String objid = getObjidValue(theContainerXml);
        checkRestPropertiesReferences(theContainerXml, objid);
        String objId = getObjidWithoutVersion(objid);
        String versionNumber = getVersionNumber(objid);
        if (versionNumber == null) {
            versionNumber = "1";
        }

        // now check this for further versions
        int verNo = Integer.valueOf(versionNumber);
        checkRestPropertiesReferences(theContainerXml, objId + VERSION_SUFFIX_SEPARATOR + verNo);

        for (int i = verNo; i < verNo + 4; i++) {

            theContainerXml = addCtsElement(theContainerXml);
            theContainerXml = update(theContainerId, theContainerXml);
            checkRestPropertiesReferences(theContainerXml, objId + VERSION_SUFFIX_SEPARATOR + (i + 1));
        }

    }

    /**
     * Check if all object references are available and retrievable after createContainer.
     *
     * @throws Exception Thrown if an reference could not be solved.
     */
    @Test
    public void testReferenceCr1() throws Exception {
        checkRestReferences(theContainerXml);
    }

    /**
     * Check if all object references are available and retrievable after a new Container cycle.
     *
     * @throws Exception Thrown if an reference could not be solved.
     */
    @Test
    public void testReferenceCr2() throws Exception {

        for (int i = 0; i < 4; i++) {
            theContainerXml = addCtsElement(theContainerXml);
            theContainerXml = update(theContainerId, theContainerXml);
            checkRestReferences(theContainerXml);
        }

        String ltstVrsnId = getLatestVersionId(EscidocAbstractTest.getDocument(theContainerXml));
        call("/ir/container/" + ltstVrsnId);
        int maxVersion = Integer.valueOf(ltstVrsnId.substring(ltstVrsnId.lastIndexOf(':') + 1));

        for (int i = 1; i <= maxVersion; i++) {
            String versionId = theContainerId + ":" + i;
            checkRestReferences(retrieve(versionId));
        }
    }

    /**
     * Check the references of an Container for the REST representation.
     *
     * @param containerXml The XML of the Container.
     * @throws Exception Thrown if retrieve or ID extracting failed.
     */
    private void checkRestReferences(final String containerXml) throws Exception {

        Document containerDoc = EscidocAbstractTest.getDocument(containerXml);

        NodeList hrefs = selectNodeList(containerDoc, "//@href");
        List<String> refList = nodeList2List(hrefs);

        // retrieve each single ref from framework
        // prevent duplicate href checking
        Vector<String> checkedRefs = new Vector<String>();

        Iterator<String> refIt = refList.iterator();

        while (refIt.hasNext()) {
            String ref = refIt.next();

            if (!checkedRefs.contains(ref) && !skipRefCheck(ref)) {
                call(ref);
                checkedRefs.add(ref);
            }
        }
    }

    /**
     * References that are to skip (or non valid GET refs).
     *
     * @param ref The reference (path).
     * @return True if the reference is to skip, false otherwise.
     */
    private boolean skipRefCheck(final String ref) {

        Vector<String> skipList = new Vector<String>();

        skipList.add("members/filter");
        skipList.add("members/filter/refs");

        for (int i = 0; i < skipList.size(); i++) {
            if (ref.endsWith(skipList.get(i))) {
                return (true);
            }
        }

        return false;
    }

    /**
     * Check the References within the Properties section. It is not checked if the reference is retrievable. Here is
     * the logic of the id checked.
     *
     * @param containerXml The XML of the Container.
     * @param id           The id which was used for the retrieve.
     * @throws Exception Thrown if the ids within the properties section doesn't fit to the retrieve Id.
     */
    private void checkRestPropertiesReferences(final String containerXml, final String id) throws Exception {

        Document containerDoc = EscidocAbstractTest.getDocument(containerXml);

        String versionSuffix = getVersionNumber(id);
        Node n = selectSingleNode(containerDoc, "/container/properties/version/@href");
        String currentVersion = getObjidFromHref(n.getNodeValue());

        n = selectSingleNode(containerDoc, "/container/properties/latest-version/@href");
        String latestVersion = getObjidFromHref(n.getNodeValue());

        assertNotNull("VersionSuffix is missing on version link", getVersionNumber(currentVersion));
        assertNotNull("Latest-VersionSuffix is missing on version link", getVersionNumber(latestVersion));

        if (versionSuffix != null) {
            assertEquals("This version link is wrong", id, currentVersion);
        }
        else {
            assertEquals("This version/latest-version link is wrong", latestVersion, currentVersion);
        }
    }

    /**
     * Retrieve the resource from the Framework via REST GET request.
     *
     * @param href The resource href.
     * @return The response object.
     * @throws Exception Thrown if the HTTP response value is != HTTP_OK (200)
     */
    private HttpResponse call(final String href) throws Exception {

        DefaultHttpClient httpClient = new DefaultHttpClient();
        httpClient.removeRequestInterceptorByClass(RequestAddCookies.class);
        httpClient.removeResponseInterceptorByClass(ResponseProcessCookies.class);

        String httpUrl = getFrameworkUrl() + href;

        HttpResponse httpRes = HttpHelper.doGet(httpClient, httpUrl, null);

        if (httpRes.getStatusLine().getStatusCode() != HttpServletResponse.SC_OK) {

            throw new Exception("Retrieve of " + href + " failed. " + httpRes.getStatusLine().getReasonPhrase() + " - "
                + EntityUtils.toString(httpRes.getEntity(), HTTP.UTF_8));
        }

        return httpRes;
    }

    /**
     * Create Item with the used interface method (REST/SOAP) for Container. The representation of an Item is different
     * from the used ingest method.
     *
     * @return id of the created Item.
     * @throws Exception Thrown if ingest failed.
     */
    private String createItem() throws Exception {

        String xmlData = getItemTemplate("escidoc_item_198_for_create.xml");
        String theItemXml = handleXmlResult(getItemClient().create(xmlData));
        String itemId = getObjidValue(theItemXml);
        return itemId;
    }
}
