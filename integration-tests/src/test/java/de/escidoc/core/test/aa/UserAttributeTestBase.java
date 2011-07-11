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
package de.escidoc.core.test.aa;

import de.escidoc.core.test.EscidocAbstractTest;
import de.escidoc.core.test.common.client.servlet.Constants;
import de.escidoc.core.test.common.client.servlet.aa.UserAccountClient;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.Assert.fail;

/**
 * Base Class for UserAttributeTests.
 *
 * @author Michael Hoppe
 */
public abstract class UserAttributeTestBase extends UserAccountTestBase {

    private UserAccountClient client = null;

    public UserAttributeTestBase() {
        client = (UserAccountClient) getClient();
    }

    /**
     * Test creating a user attribute.
     *
     * @param userId The id of the UserAccount.
     * @param xml    The xml representation of the attribute.
     * @return The xml representation of the created attribute.
     * @throws Exception If anything fails.
     */
    protected String createAttribute(final String userId, final String xml) throws Exception {
        Object result = client.createAttribute(userId, xml);
        String xmlResult = null;
        if (result instanceof HttpResponse) {
            HttpResponse httpRes = (HttpResponse) result;
            xmlResult = EntityUtils.toString(httpRes.getEntity(), HTTP.UTF_8);
            assertHttpStatusOfMethod("", httpRes);

        }
        else if (result instanceof String) {
            xmlResult = (String) result;
        }
        return xmlResult;
    }

    /**
     * Test retrieving user attributes.
     *
     * @param userId The id of the UserAccount.
     * @return The xml representation of the user attributes.
     * @throws Exception If anything fails.
     */
    protected String retrieveAttributes(final String userId) throws Exception {

        Object result = client.retrieveAttributes(userId);
        String xmlResult = null;
        if (result instanceof HttpResponse) {
            HttpResponse method = (HttpResponse) result;
            xmlResult = EntityUtils.toString(method.getEntity(), HTTP.UTF_8);
            assertHttpStatusOfMethod("", method);

        }
        else if (result instanceof String) {
            xmlResult = (String) result;
        }
        return xmlResult;
    }

    /**
     * Test retrieving user attributes with given name.
     *
     * @param userId The id of the UserAccount.
     * @param name   The name of the UserAttribute.
     * @return The xml representation of the user attributes.
     * @throws Exception If anything fails.
     */
    protected String retrieveNamedAttributes(final String userId, final String name) throws Exception {

        Object result = client.retrieveNamedAttributes(userId, name);
        String xmlResult = null;
        if (result instanceof HttpResponse) {
            HttpResponse method = (HttpResponse) result;
            xmlResult = EntityUtils.toString(method.getEntity(), HTTP.UTF_8);
            assertHttpStatusOfMethod("", method);

        }
        else if (result instanceof String) {
            xmlResult = (String) result;
        }
        return xmlResult;
    }

    /**
     * Test retrieving user attribute.
     *
     * @param userId      The id of the UserAccount.
     * @param attributeId The id of the UserAttributet.
     * @return The xml representation of the user attribute.
     * @throws Exception If anything fails.
     */
    protected String retrieveAttribute(final String userId, final String attributeId) throws Exception {

        Object result = client.retrieveAttribute(userId, attributeId);
        String xmlResult = null;
        if (result instanceof HttpResponse) {
            HttpResponse method = (HttpResponse) result;
            xmlResult = EntityUtils.toString(method.getEntity(), HTTP.UTF_8);
            assertHttpStatusOfMethod("", method);

        }
        else if (result instanceof String) {
            xmlResult = (String) result;
        }
        return xmlResult;
    }

    /**
     * Test updating single user attribute.
     *
     * @param userId      The id of the UserAccount.
     * @param attributeId The name of the attribute to update.
     * @param xml         The xml representation of the attribute.
     * @return The xml representation of the updated attribute.
     * @throws Exception If anything fails.
     */
    protected String updateAttribute(final String userId, final String attributeId, final String xml) throws Exception {
        Object result = client.updateAttribute(userId, attributeId, xml);
        String xmlResult = null;
        if (result instanceof HttpResponse) {
            HttpResponse method = (HttpResponse) result;
            xmlResult = EntityUtils.toString(method.getEntity(), HTTP.UTF_8);
            assertHttpStatusOfMethod("", method);

        }
        else if (result instanceof String) {
            xmlResult = (String) result;
        }
        return xmlResult;
    }

    /**
     * Test deleting single user attribute.
     *
     * @param userId      The id of the UserAccount.
     * @param attributeId The id of the attribute to delete.
     * @throws Exception If anything fails.
     */
    protected void deleteAttribute(final String userId, final String attributeId) throws Exception {
        client.deleteAttribute(userId, attributeId);
    }

    /**
     * Assert that the user attribute has all required elements.<br/>
     *
     * @param userId          userId
     * @param toBeAssertedXml The created/updated user attribute.
     * @param originalXml     The template used to create/update the user attribute. If this parameter is
     *                        <code>null</code>, no check with the original data is performed.
     * @return Returns the document representing the provided xml data.
     * @throws Exception If anything fails.
     */
    public Document assertUserAttribute(final String userId, final String toBeAssertedXml, final String originalXml)
        throws Exception {

        assertXmlValidUserAttribute(toBeAssertedXml);
        Document toBeAssertedDocument = EscidocAbstractTest.getDocument(toBeAssertedXml);
        assertRootElement("Invalid root element. ", toBeAssertedDocument, XPATH_ATTRIBUTE,
            Constants.USER_ACCOUNT_BASE_URI + "/" + userId + "/resources/attributes/attribute", null);

        if (originalXml != null) {
            final Document originalDocument = EscidocAbstractTest.getDocument(originalXml);

            // name
            assertXmlEquals("Name mismatch, ", originalDocument, toBeAssertedDocument, XPATH_USER_ACCOUNT_NAME);

            // internal
            assertXmlEquals("Internal flag mismatch, ", originalDocument, toBeAssertedDocument, XPATH_USER_ACCOUNT_NAME);
        }
        return toBeAssertedDocument;
    }

    /**
     * Check if xml is valid and contains all attributes that are in given attributeList and only these attributes.
     *
     * @param attributesXml xml with user-attributes
     * @param userId        userId
     * @param attributeList list of expected user-attributes
     * @throws Exception If anything fails.
     */
    //CHECKSTYLE:OFF
    protected void assertValidUserAttributes(
        final String attributesXml, final String userId, Collection<String> attributeList) throws Exception {
        //CHECKSTYLE:ON

        assertXmlValidAttributes(attributesXml);

        String href = "/aa/user-account/" + userId + "/resources/attributes";
        Document attributesXmlDocument = getDocument(attributesXml);
        selectSingleNodeAsserted(attributesXmlDocument, "/attributes/@base");
        selectSingleNodeAsserted(attributesXmlDocument, "/attributes[@href = '" + href + "']");
        int count = attributeList.size();
        if (count > 0) {
            selectSingleNodeAsserted(attributesXmlDocument, "/attributes/attribute[" + count + "]");
        }

        // check if every entry from given collection is in the document
        NodeList attributeElements = selectNodeList(attributesXmlDocument, "/attributes/attribute");
        int elementCount = attributeElements.getLength();
        // iterate elements of the xml document
        for (int i = 0; i < elementCount; i++) {
            // check if key value pair is in given map
            String attributeName = attributeElements.item(i).getAttributes().getNamedItem("name").getNodeValue();
            String attributeValue = attributeElements.item(i).getTextContent();
            String isInternal = attributeElements.item(i).getAttributes().getNamedItem("internal").getNodeValue();
            if (!attributeList.contains(attributeName + attributeValue + isInternal)) {
                fail("Unexpected attribute found. [" + attributeName + attributeValue + isInternal + "]");
            }
            attributeList.remove(attributeName + attributeValue + isInternal);
        }
        // all entries should be removed from hashes(-out-of-map), now
        if (!attributeList.isEmpty()) {
            fail("Expected attributes not found. [" + attributeList.toString() + "]");
        }
    }

    /**
     * Retrieve attributes for given user. Make Collection with concatenated name-value-internal values from list of
     * user-attributes.
     *
     * @param userId userId
     * @return Collection with concatenated attributes
     * @throws Exception If anything fails.
     */
    protected Collection<String> getCollectionFromAttributes(final String userId) throws Exception {
        NodeList list = selectNodeList(getDocument(retrieveAttributes(userId)), "/attributes/attribute");
        Collection<String> col = new ArrayList<String>();

        int elementCount = list.getLength();
        // iterate elements of the xml document
        for (int i = 0; i < elementCount; i++) {
            String attributeName = list.item(i).getAttributes().getNamedItem("name").getNodeValue();
            String attributeValue = list.item(i).getTextContent();
            String isInternal = list.item(i).getAttributes().getNamedItem("internal").getNodeValue();
            col.add(attributeName + attributeValue + isInternal);
        }

        return col;
    }

    /**
     * Retrieve attributeIds for given user. Make Collection with attributeIds from list of user-attributes.
     *
     * @param userId        userId
     * @param attributeName attributeName to filter
     * @return Collection with attributeIds
     * @throws Exception If anything fails.
     */
    protected Collection<String> getCollectionFromAttributesIds(final String userId, final String attributeName)
        throws Exception {
        NodeList list = selectNodeList(getDocument(retrieveAttributes(userId)), "/attributes/attribute");
        Collection<String> col = new ArrayList<String>();

        int elementCount = list.getLength();
        // iterate elements of the xml document
        for (int i = 0; i < elementCount; i++) {
            if (attributeName == null
                || list.item(i).getAttributes().getNamedItem("name").getNodeValue().equals(attributeName)) {
                String attributeId = getObjidValue(list.item(i), null);
                col.add(attributeId);
            }
        }

        return col;
    }

}
