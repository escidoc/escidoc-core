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
package de.escidoc.core.test.om.contentRelation;

import static org.junit.Assert.fail;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.http.HttpResponse;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.escidoc.core.test.EscidocAbstractTest;
import de.escidoc.core.test.common.client.servlet.aa.UserAccountClient;
import de.escidoc.core.test.common.client.servlet.interfaces.ResourceHandlerClientInterface;
import de.escidoc.core.test.om.OmTestBase;

/**
 * Test the handler of the content relation resource.
 *
 * @author Steffen Wagner
 */
public class ContentRelationTestBase extends OmTestBase {

    protected static final String XPATH_CONTENT_RELATION = "/content-relation";

    protected static final String XPATH_CONTENT_RELATION_XLINK_HREF = XPATH_CONTENT_RELATION + PART_XLINK_HREF;

    /**
     * @return Returns the ContentRelationClient
     */
    @Override
    public ResourceHandlerClientInterface getClient() {

        return getContentRelationClient();
    }

    /**
     * Test retrieving the properties of an Content Relation.
     *
     * @param id The id of the Content Relation.
     * @return The retrieved properties.
     * @throws Exception If anything fails.
     */
    protected String retrieveProperties(final String id) throws Exception {

        return handleXmlResult(getContentRelationClient().retrieveProperties(id));
    }

    /**
     * Test retrieving the md-records of an Content Relation.
     *
     * @param id The id of the Content Relation.
     * @return The retrieved md-records.
     * @throws Exception If anything fails.
     */
    protected String retrieveMdRecords(final String id) throws Exception {

        return handleXmlResult(getContentRelationClient().retrieveMdRecords(id));
    }

    /**
     * Assign a objectPID to the Content Relation.
     *
     * @param id    The id of the Content Relation.
     * @param param The PID parameter.
     * @return The assignment method response.
     * @throws Exception Thrown if anything fails.
     */
    public String assignObjectPid(final String id, final String param) throws Exception {

        return handleXmlResult(getContentRelationClient().assignObjectPid(id, param));
    }

    /**
     * Test locking a content relation.
     *
     * @param id    The id of the content relation.
     * @param param The param indicating the last-modifiaction-date of the content relation.
     * @return result XML with (at least) last modification date of the resource.
     * @throws Exception If anything fails.
     */
    public String lock(final String id, final String param) throws Exception {

        Object result = getContentRelationClient().lock(id, param);
        if (result instanceof HttpResponse) {
            HttpResponse httpRes = (HttpResponse) result;
            assertHttpStatusOfMethod("", httpRes);
        }
        return handleXmlResult(result);
    }

    /**
     * Test unlocking a content relation in the mock framework.
     *
     * @param id    The id of the content relation.
     * @param param The param indicating the last-modifiaction-date of the content relation.
     * @return result XML with (at least) last modification date of the resource.
     * @throws Exception If anything fails.
     */
    public String unlock(final String id, final String param) throws Exception {

        Object result = getContentRelationClient().unlock(id, param);
        if (result instanceof HttpResponse) {
            HttpResponse httpRes = (HttpResponse) result;
            assertHttpStatusOfMethod("", httpRes);
        }
        return handleXmlResult(result);
    }

    /**
     * Test retrieving a md-record of an Content Relation with a provided name.
     *
     * @param id   The id of the Content Relation.
     * @param name The name of the md-record.
     * @return The retrieved md-record.
     * @throws Exception If anything fails.
     */
    protected String retrieveMdRecord(final String id, final String name) throws Exception {

        return handleXmlResult(getContentRelationClient().retrieveMdRecord(id, name));
    }

    /**
     * Test submitting of a Content Relation.
     *
     * @param id    The id of the Content Relation.
     * @param param param containing last mod date
     * @return modified last mod date after summit.
     * @throws Exception If anything fails.
     */
    protected String submit(final String id, final String param) throws Exception {
        return handleXmlResult(getContentRelationClient().submit(id, param));
    }

    /**
     * Test revising of a Content Relation.
     *
     * @param id    The id of the Content Relation.
     * @param param param containing last mod date
     * @return modified last mod date after revise.
     * @throws Exception If anything fails.
     */
    protected String revise(final String id, final String param) throws Exception {
        return handleXmlResult(getContentRelationClient().revise(id, param));
    }

    /**
     * Test releasing of a Content Relation.
     *
     * @param id    The id of the Content Relation.
     * @param param param containing last mod date
     * @return modified last mod date after release.
     * @throws Exception If anything fails.
     */
    protected String release(final String id, final String param) throws Exception {
        return handleXmlResult(getContentRelationClient().release(id, param));
    }

    // /**
    // * Test retrieving the metadata records of an Content Relation.
    // *
    // * @param id
    // * The id of the Content Relation.
    // * @return The retrieved metadata records.
    // * @throws Exception
    // * If anything fails.
    // */
    // protected String retrieveMetadataRecords(final String id) throws
    // Exception {
    //
    // return handleXmlResult(getContentRelationClient()
    // .retrieveMetaDataRecords(id));
    // }
    //
    // /**
    // * Test retrieving the metadata records of an Content Relation.
    // *
    // * @param id
    // * The id of the Content Relation.
    // * @param mdRecordname
    // * The name of the md-record.
    // * @return The retrieved metadata record.
    // * @throws Exception
    // * If anything fails.
    // */
    // protected String retrieveMetadataRecord(
    // final String id, final String mdRecordname) throws Exception {
    //
    // return handleXmlResult(getContentRelationClient().retrieveMdRecord(id,
    // mdRecordname));
    // }
    //
    // /**
    // * Test updating a metadata record of an Item.
    // *
    // * @param id
    // * The id of the container.
    // * @param mdRecordName
    // * The name of the metadata record.
    // * @param mdRecord
    // * The updated metadata record.
    // * @return The created metadata record.
    // * @throws Exception
    // * If anything fails.
    // */
    // protected String updateMetadataRecord(
    // final String id, final String mdRecordName, final String mdRecord)
    // throws Exception {
    //
    // return handleXmlResult(getContentRelationClient().updateMetaDataRecord(
    // id, mdRecordName, mdRecord));
    // }

    /**
     * Test retrieving the list of content relations.
     *
     * @param filter CQL filter
     * @return The retrieved content relations.
     * @throws Exception If anything fails.
     */
    public String retrieveContentRelations(final Map<String, String[]> filter) throws Exception {

        return handleXmlResult(getContentRelationClient().retrieveContentRelations(filter));
    }

    /**
     * Test retrieving the resources of an Item.
     *
     * @param id The id of the container.
     * @return The retrieved resources.
     * @throws Exception If anything fails.
     */
    @Override
    public String retrieveResources(final String id) throws Exception {

        return handleXmlResult(getContentRelationClient().retrieveResources(id));
    }

    /**
     * Create an Content Relation from template.
     *
     * @param templateName The name of the Content Relation template (file).
     * @return objid of the Item.
     * @throws Exception Thrown if creation of Item or extraction of objid fails.
     */
    public String createContainerFromTemplate(final String templateName) throws Exception {

        // create an item and save the id
        String xmlData = getContainerTemplate(templateName);

        String theContainerXml = handleXmlResult(getContentRelationClient().create(xmlData));
        return getObjidValue(theContainerXml);
    }

    /**
     * Create an Item from template.
     *
     * @param templateName The name of the Item template (file).
     * @return objid of the Item.
     * @throws Exception Thrown if creation of Item or extraction of objid fails.
     */
    public String createItemFromTemplate(final String templateName) throws Exception {

        // create an item and save the id
        String xmlData = EscidocAbstractTest.getTemplateAsString(TEMPLATE_ITEM_PATH + "/rest", templateName);

        String theItemXml = handleXmlResult(getItemClient().create(xmlData));
        return getObjidValue(theItemXml);
    }

    /**
     * Get a Content Relation template. The template is pulled automatically from the rest/soap directory of the
     * container template basedir.
     *
     * @param templateName The name of the Content Relation template (file).
     * @return The String representation of the template.
     * @throws Exception Thrown if anything fails.
     */
    public String getContainerTemplate(final String templateName) throws Exception {

        return EscidocAbstractTest.getTemplateAsString(TEMPLATE_CONTAINER_PATH + "/rest", templateName);

    }

    /**
     * Get a Item template. The template is pulled automatically from the rest/soap directory of the Item template
     * basedir.
     *
     * @param templateName The name of the Item template (file).
     * @return The String representation of the template.
     * @throws Exception Thrown if anything fails.
     */
    public String getItemTemplate(final String templateName) throws Exception {

        return EscidocAbstractTest.getTemplateAsString(TEMPLATE_ITEM_PATH + "/rest", templateName);

    }

    /**
     * Convert a NodeList to a List.
     *
     * @param nl The NodeList.
     * @return List
     */
    public List<String> nodeList2List(final NodeList nl) {

        List<String> list = new Vector<String>();
        for (int i = nl.getLength() - 1; i >= 0; i--) {
            list.add(nl.item(i).getNodeValue());
        }
        return list;
    }

    /**
     * Compares if the content of two lists equals.
     * <p/>
     * WARNING: side effect, elements are removed from the second list
     *
     * @param msg  Message for the exception.
     * @param arg0 List one.
     * @param arg1 List two.
     */
    public void assertListContentEqual(final String msg, final List<String> arg0, final List<String> arg1) {

        Iterator<String> it = arg0.iterator();
        while (it.hasNext()) {
            String member = (String) it.next();
            if (arg1.contains(member)) {
                arg1.remove(member);
            }
            else {
                fail(msg + " (" + member + ")");
            }
        }
        if (!arg1.isEmpty()) {
            fail(msg);
        }
    }

    /**
     * Add certain grants to userid.
     *
     * @param userId Userid
     */
    public void addContentRelationManagerGrant(final String userId) throws Exception {

        UserAccountClient uac = new UserAccountClient();
        // check if user has this role already
        String userXml = handleXmlResult(uac.retrieveCurrentGrants(userId));
        if (userXml.contains("escidoc:role-content-relation-manager")) {
            return;
        }

        // set content-relation-manager grant to user
        String grantXml =
            EscidocAbstractTest.getTemplateAsString(TEMPLATE_BASE_PATH + "/om/template/aa/user-account/rest",
                "create_content_relation_manager_grant.xml");

        try {
            handleXmlResult(uac.createGrant(userId, grantXml));
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException(e);
        }
    }

    /**
     * Add certain grants to userid.
     *
     * @param userId Userid
     */
    public void addContentRelationModifierGrant(final String userId) throws Exception {

        UserAccountClient uac = new UserAccountClient();
        // check if user has this role already
        String userXml = handleXmlResult(uac.retrieveCurrentGrants(userId));
        if (userXml.contains("escidoc:role-content-relation-modifier")) {
            return;
        }

        // set content-relation-manager grant to user
        String grantXml =
            EscidocAbstractTest.getTemplateAsString(TEMPLATE_BASE_PATH + "/om/template/aa/user-account/rest",
                "create_content_relation_modifier_grant.xml");
        // set objid/href of user to template
        grantXml = grantXml.replace("###USER_ID###", userId);

        try {
            handleXmlResult(uac.createGrant(userId, grantXml));
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException(e);
        }
    }

    /**
     * Successfully creates an UserAccount.
     *
     * @param templateName The name of the template.
     * @return Returns the UserAccount document.
     * @throws Exception If anything fails
     */
    public String[] createUserWithContentRelationRole(final String templateName) throws Exception {

        final Document toBeCreatedDocument =
            EscidocAbstractTest.getTemplateAsDocument(TEMPLATE_USER_ACCOUNT_PATH, templateName);
        String loginName = insertUniqueLoginName(toBeCreatedDocument);

        final String toBeCreatedXml = toString(toBeCreatedDocument, false);

        String createdUserAccountXml = null;
        UserAccountClient uac = new UserAccountClient();
        try {
            createdUserAccountXml = handleXmlResult(uac.create(toBeCreatedXml));
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException(e);
        }

        String userId = getObjidValue(createdUserAccountXml);

        return new String[] { userId, loginName };
    }

    /**
     * Inserts a unique loginname into the provided document by adding the current timestamp to the contained
     * loginname.
     *
     * @param document The document.
     * @return The inserted login name.
     * @throws Exception If anything fails.
     */
    protected String insertUniqueLoginName(final Document document) throws Exception {

        assertXmlExists("No login-name found in template data. ", document, "/user-account/properties/login-name");
        final Node loginNameNode = selectSingleNode(document, "/user-account/properties/login-name");
        String loginname = loginNameNode.getTextContent().trim();
        loginname += System.currentTimeMillis();

        loginNameNode.setTextContent(loginname);

        return loginname;
    }

}
