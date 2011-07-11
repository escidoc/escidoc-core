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

import de.escidoc.core.common.exceptions.remote.application.violated.AlreadyActiveException;
import de.escidoc.core.common.exceptions.remote.application.violated.AlreadyDeactiveException;
import de.escidoc.core.test.EscidocAbstractTest;
import de.escidoc.core.test.common.client.servlet.Constants;
import de.escidoc.core.test.common.client.servlet.interfaces.ResourceHandlerClientInterface;
import de.escidoc.core.test.security.client.PWCallback;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * Base class for testing the AA.UserGroup component.
 *
 * @author Michael Hoppe
 */
public abstract class UserGroupTestBase extends AaTestBase {

    public static final String XPATH_USER_GROUP_XML_BASE = XPATH_USER_GROUP + PART_XML_BASE;

    public static final String XPATH_USER_GROUP_CURRENT_GRANTS_XLINK_TITLE =
        XPATH_USER_GROUP_CURRENT_GRANTS + PART_XLINK_TITLE;

    public static final String XPATH_USER_GROUP_CURRENT_GRANTS_XLINK_HREF =
        XPATH_USER_GROUP_CURRENT_GRANTS + PART_XLINK_HREF;

    /**
     * Test activating an UserGroup.
     *
     * @param id           The id of the UserGroup.
     * @param taskParamXml The task parameter in an XML structure.
     * @return userGroup-XML
     * @throws Exception If anything fails.
     */
    protected String activate(final String id, final String taskParamXml) throws Exception {

        Object result = getUserGroupClient().activate(id, taskParamXml);
        String xmlResult = null;
        if (result instanceof HttpResponse) {
            HttpResponse method = (HttpResponse) result;
            xmlResult = EntityUtils.toString(method.getEntity(), HTTP.UTF_8);
            assertHttpStatusOfMethod("", method);
            if (xmlResult.equals("")) {
                xmlResult = null;
            }

        }
        else if (result instanceof String) {
            xmlResult = (String) result;
        }
        return xmlResult;
    }

    /**
     * @return Returns the userGroupClient
     */
    @Override
    public ResourceHandlerClientInterface getClient() {

        return getUserGroupClient();
    }

    /**
     * Test deactivating an UserGroup.
     *
     * @param id           The id of the UserGroup.
     * @param taskParamXml The task parameter in an XML structure.
     * @return userGroup-XML
     * @throws Exception If anything fails.
     */
    protected String deactivate(final String id, final String taskParamXml) throws Exception {

        Object result = getUserGroupClient().deactivate(id, taskParamXml);
        String xmlResult = null;
        if (result instanceof HttpResponse) {
            HttpResponse method = (HttpResponse) result;
            xmlResult = EntityUtils.toString(method.getEntity(), HTTP.UTF_8);
            assertHttpStatusOfMethod("", method);

            if (xmlResult.equals("")) {
                xmlResult = null;
            }
        }
        else if (result instanceof String) {
            xmlResult = (String) result;
        }
        return xmlResult;
    }

    /**
     * Test retrieving an UserGroup.
     *
     * @param identifier An unique identifier (id, label) of the UserGroup.
     * @return The xml representation of the UserGroup.
     * @throws Exception If anything fails.
     */
    @Override
    public String retrieve(final String identifier) throws Exception {

        return super.retrieve(identifier);
    }

    /**
     * Test retrieving list of user Groups from the framework.
     *
     * @param filter The filter.
     * @return The retrieved resource.
     * @throws Exception If anything fails.
     */
    public String retrieveUserGroups(final Map<String, String[]> filter) throws Exception {
        return handleXmlResult(getUserGroupClient().retrieveUserGroups(filter));
    }

    /**
     * Test removing selectors from a user Group with provided id.
     *
     * @param id        The id of the user group.
     * @param taskParam xml structure with selectors to add
     * @return The modified user group representation.
     * @throws Exception If anything fails.
     */

    public String removeSelectors(final String id, final String taskParam) throws Exception {

        return handleXmlResult(getUserGroupClient().removeSelectors(id, taskParam));
    }

    /**
     * Retrieve a grant from the specified user group.
     *
     * @param id      The user group id.
     * @param grantId The id of the grant.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    public String retrieveGrant(final String id, final String grantId) throws Exception {
        return handleXmlResult(getUserGroupClient().retrieveGrant(id, grantId));
    }

    /**
     * Retrieve the current grants of the specified user group.
     *
     * @param id The user group id.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */

    public Object retrieveCurrentGrants(final String id) throws Exception {

        return handleXmlResult(getUserGroupClient().retrieveCurrentGrants(id));
    }

    /**
     * Revoke a grant from the specified user group.
     *
     * @param id        The user group id.
     * @param filterXml The filter-criteria in an XML structure.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */

    public Object revokeGrants(final String id, final String filterXml) throws Exception {
        return handleXmlResult(getUserGroupClient().revokeGrants(id, filterXml));

    }

    /**
     * Add a provided grant to the user group with provided id.
     *
     * @param groupId  The id of the user group.
     * @param grantXml xml structure with selectors to add
     * @return The modified user group representation.
     * @throws Exception If anything fails.
     */
    public String createGrant(final String groupId, final String grantXml) throws Exception {

        return handleXmlResult(getUserGroupClient().createGrant(groupId, grantXml));
    }

    /**
     * Test adding selectors to a user Group with provided id.
     *
     * @param id        The id of the user group.
     * @param taskParam xml structure with selectors to add
     * @return The modified user group representation.
     * @throws Exception If anything fails.
     */
    public String addSelectors(final String id, final String taskParam) throws Exception {

        return handleXmlResult(getUserGroupClient().addSelectors(id, taskParam));
    }

    /**
     * Creates a new user group.
     *
     * @param templateName     name of the usergroup-template to use.
     * @param nameModification modified name of the user group.
     * @return The inserted user group.
     * @throws Exception If anything fails.
     */
    public Document createSuccessfully(final String templateName, final String nameModification) throws Exception {
        if (nameModification == null) {
            return createSuccessfully(templateName);

        }
        else {
            final Document toBeCreatedDocument =
                getTemplateAsFixedUserGroupDocument(TEMPLATE_USER_GROUP_PATH, templateName);
            final Node nameNode = selectSingleNode(toBeCreatedDocument, "/user-group/properties/name");
            String name = nameNode.getTextContent().trim();
            name += nameModification;

            nameNode.setTextContent(name);
            insertUniqueLabel(toBeCreatedDocument);
            final String toBeCreatedXml = toString(toBeCreatedDocument, false);
            assertXmlValidUserGroup(toBeCreatedXml);
            final String beforeCreationTimestamp = getNowAsTimestamp();

            String createdUserGroupXml = null;
            try {
                createdUserGroupXml = create(toBeCreatedXml);
            }
            catch (final Exception e) {
                EscidocAbstractTest.failException(e);
            }
            return assertActiveUserGroup(createdUserGroupXml, toBeCreatedXml, beforeCreationTimestamp,
                beforeCreationTimestamp, false);
        }
    }

    /**
     * Successfully creates an UserGroup.
     *
     * @param templateName The name of the template.
     * @return Returns the UserGroup document.
     * @throws Exception If anything fails
     */
    public Document createSuccessfully(final String templateName) throws Exception {

        final Document toBeCreatedDocument =
            getTemplateAsFixedUserGroupDocument(TEMPLATE_USER_GROUP_PATH, templateName);
        insertUniqueLabel(toBeCreatedDocument);

        final String toBeCreatedXml = toString(toBeCreatedDocument, false);
        assertXmlValidUserGroup(toBeCreatedXml);
        final String beforeCreationTimestamp = getNowAsTimestamp();

        String createdUserGroupXml = null;
        try {
            createdUserGroupXml = create(toBeCreatedXml);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException(e);
        }
        return assertActiveUserGroup(createdUserGroupXml, toBeCreatedXml, beforeCreationTimestamp,
            beforeCreationTimestamp, false);
    }

    /**
     * Successfully retrieves an UserGroup.
     *
     * @param id The id of the user group.
     * @return Returns the UserGroup document.
     * @throws Exception If anything fails
     */
    public Document retrieveSuccessfully(final String id) throws Exception {

        String retrievedXml = null;
        try {
            retrievedXml = retrieve(id);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException("Retrieving user group failed. ", e);
        }
        assertXmlValidUserGroup(retrievedXml);
        return EscidocAbstractTest.getDocument(retrievedXml);
    }

    /**
     * Tests updating a user-group.
     *
     * @param creatorHandle          The escidoc user handle that creates the user-group.
     * @param userHandle             The escidoc user handle.
     * @param templateName           templateName.
     * @param expectedExceptionClass expectedExceptionClass.
     * @throws Exception If anything fails.
     */
    protected void doTestUpdate(
        final String creatorHandle, final String userHandle, final String templateName,
        final Class<?> expectedExceptionClass) throws Exception {

        String userGroupXml = EscidocAbstractTest.getTemplateAsString(TEMPLATE_USER_GROUP_PATH, templateName);
        PWCallback.setHandle(creatorHandle);
        String createdUserGroupXml = handleResult(userGroupClient.create(userGroupXml));
        String userId = getObjidValue(createdUserGroupXml);
        createdUserGroupXml.replaceFirst("name>", "name>add");

        try {
            PWCallback.setHandle(userHandle);

            userGroupClient.update(userId, createdUserGroupXml);
            if (expectedExceptionClass != null) {
                EscidocAbstractTest.failMissingException(expectedExceptionClass);
            }
        }
        catch (final Exception e) {
            if (expectedExceptionClass == null) {
                EscidocAbstractTest.failException(e);
            }
            else {
                EscidocAbstractTest.assertExceptionType(expectedExceptionClass, e);
            }
        }
        finally {
            PWCallback.setHandle(PWCallback.DEFAULT_HANDLE);
        }
    }

    /**
     * Tests updating a user-Group.
     *
     * @param userHandle             The escidoc user handle.
     * @param groupId                id of Group to update.
     * @param expectedExceptionClass expectedExceptionClass.
     * @throws Exception If anything fails.
     */
    protected void doTestUpdate(final String userHandle, final String groupId, final Class<?> expectedExceptionClass)
        throws Exception {

        String userGroupXml = handleResult(userGroupClient.retrieve(groupId));
        userGroupXml.replaceFirst("name>", "name>add");

        try {
            PWCallback.setHandle(userHandle);

            userGroupClient.update(groupId, userGroupXml);
            if (expectedExceptionClass != null) {
                EscidocAbstractTest.failMissingException(expectedExceptionClass);
            }
        }
        catch (final Exception e) {
            if (expectedExceptionClass == null) {
                EscidocAbstractTest.failException(e);
            }
            else {
                EscidocAbstractTest.assertExceptionType(expectedExceptionClass, e);
            }
        }
        finally {
            PWCallback.setHandle(PWCallback.DEFAULT_HANDLE);
        }
    }

    /**
     * Tests deleting a user-Group.
     *
     * @param creatorHandle          The escidoc user handle that creates the user-Group.
     * @param userHandle             The escidoc user handle.
     * @param templateName           templateName.
     * @param expectedExceptionClass expectedExceptionClass.
     * @throws Exception If anything fails.
     */
    protected void doTestDelete(
        final String creatorHandle, final String userHandle, final String templateName,
        final Class<?> expectedExceptionClass) throws Exception {

        String userGroupXml = EscidocAbstractTest.getTemplateAsString(TEMPLATE_USER_GROUP_PATH, templateName);
        PWCallback.setHandle(creatorHandle);
        String createdUserGroupXml = handleResult(userGroupClient.create(userGroupXml));
        String userId = getObjidValue(createdUserGroupXml);

        try {
            PWCallback.setHandle(userHandle);

            userGroupClient.delete(userId);
            if (expectedExceptionClass != null) {
                EscidocAbstractTest.failMissingException(expectedExceptionClass);
            }
        }
        catch (final Exception e) {
            if (expectedExceptionClass == null) {
                EscidocAbstractTest.failException(e);
            }
            else {
                EscidocAbstractTest.assertExceptionType(expectedExceptionClass, e);
            }
        }
        finally {
            PWCallback.setHandle(PWCallback.DEFAULT_HANDLE);
        }
    }

    /**
     * Tests deleting a user-Group.
     *
     * @param userHandle             The escidoc user handle.
     * @param groupId                id of Group to delete.
     * @param expectedExceptionClass expectedExceptionClass.
     * @throws Exception If anything fails.
     */
    protected void doTestDelete(final String userHandle, final String groupId, final Class<?> expectedExceptionClass)
        throws Exception {

        try {
            PWCallback.setHandle(userHandle);

            userGroupClient.delete(groupId);
            if (expectedExceptionClass != null) {
                EscidocAbstractTest.failMissingException(expectedExceptionClass);
            }
        }
        catch (final Exception e) {
            if (expectedExceptionClass == null) {
                EscidocAbstractTest.failException(e);
            }
            else {
                EscidocAbstractTest.assertExceptionType(expectedExceptionClass, e);
            }
        }
        finally {
            PWCallback.setHandle(PWCallback.DEFAULT_HANDLE);
        }
    }

    /**
     * Tests activating a user-Group.
     *
     * @param creatorHandle          The escidoc user handle that creates the user-Group.
     * @param userHandle             The escidoc user handle.
     * @param templateName           templateName.
     * @param expectedExceptionClass expectedExceptionClass.
     * @throws Exception If anything fails.
     */
    protected void doTestActivate(
        final String creatorHandle, final String userHandle, final String templateName,
        final Class<?> expectedExceptionClass) throws Exception {

        String userGroupXml = EscidocAbstractTest.getTemplateAsString(TEMPLATE_USER_GROUP_PATH, templateName);
        PWCallback.setHandle(creatorHandle);
        String createdUserGroupXml = handleResult(userGroupClient.create(userGroupXml));
        String userId = getObjidValue(createdUserGroupXml);

        try {
            PWCallback.setHandle(userHandle);
            String lastModificationDate =
                getLastModificationDateValue(EscidocAbstractTest.getDocument(createdUserGroupXml));
            String taskParamXml = "<param last-modification-date=\"" + lastModificationDate + "\" />";
            userGroupClient.activate(userId, taskParamXml);
            if (expectedExceptionClass != null) {
                EscidocAbstractTest.failMissingException(expectedExceptionClass);
            }
        }
        catch (final Exception e) {
            if (expectedExceptionClass == null) {
                EscidocAbstractTest.failException(e);
            }
            else {
                EscidocAbstractTest.assertExceptionType(expectedExceptionClass, e);
            }
        }
        finally {
            PWCallback.setHandle(PWCallback.DEFAULT_HANDLE);
        }
    }

    /**
     * Tests activating a user-Group.
     *
     * @param userHandle             The escidoc user handle.
     * @param groupId                id of Group to activate.
     * @param expectedExceptionClass expectedExceptionClass.
     * @throws Exception If anything fails.
     */
    protected void doTestActivate(final String userHandle, final String groupId, final Class<?> expectedExceptionClass)
        throws Exception {
        try {
            String userGroupXml = handleResult(userGroupClient.retrieve(groupId));
            String lastModificationDate = getLastModificationDateValue(EscidocAbstractTest.getDocument(userGroupXml));
            String taskParamXml = "<param last-modification-date=\"" + lastModificationDate + "\" />";
            userGroupClient.deactivate(groupId, taskParamXml);
        }
        catch (final AlreadyDeactiveException e) {
        }

        String userGroupXml = handleResult(userGroupClient.retrieve(groupId));

        try {
            PWCallback.setHandle(userHandle);
            String lastModificationDate = getLastModificationDateValue(EscidocAbstractTest.getDocument(userGroupXml));
            String taskParamXml = "<param last-modification-date=\"" + lastModificationDate + "\" />";
            userGroupClient.activate(groupId, taskParamXml);
            if (expectedExceptionClass != null) {
                EscidocAbstractTest.failMissingException(expectedExceptionClass);
            }
        }
        catch (final Exception e) {
            if (expectedExceptionClass == null) {
                EscidocAbstractTest.failException(e);
            }
            else {
                EscidocAbstractTest.assertExceptionType(expectedExceptionClass, e);
            }
        }
        finally {
            PWCallback.setHandle(PWCallback.DEFAULT_HANDLE);
        }
    }

    /**
     * Tests deactivating a user-Group.
     *
     * @param creatorHandle          The escidoc user handle that creates the user-Group.
     * @param userHandle             The escidoc user handle.
     * @param templateName           templateName.
     * @param expectedExceptionClass expectedExceptionClass.
     * @throws Exception If anything fails.
     */
    protected void doTestDeactivate(
        final String creatorHandle, final String userHandle, final String templateName,
        final Class<?> expectedExceptionClass) throws Exception {

        String userGroupXml = EscidocAbstractTest.getTemplateAsString(TEMPLATE_USER_GROUP_PATH, templateName);
        PWCallback.setHandle(creatorHandle);
        String createdUserGroupXml = handleResult(userGroupClient.create(userGroupXml));
        String userId = getObjidValue(createdUserGroupXml);

        try {
            PWCallback.setHandle(userHandle);
            String lastModificationDate =
                getLastModificationDateValue(EscidocAbstractTest.getDocument(createdUserGroupXml));
            String taskParamXml = "<param last-modification-date=\"" + lastModificationDate + "\" />";
            userGroupClient.deactivate(userId, taskParamXml);
            if (expectedExceptionClass != null) {
                EscidocAbstractTest.failMissingException(expectedExceptionClass);
            }
        }
        catch (final Exception e) {
            if (expectedExceptionClass == null) {
                EscidocAbstractTest.failException(e);
            }
            else {
                EscidocAbstractTest.assertExceptionType(expectedExceptionClass, e);
            }
        }
        finally {
            PWCallback.setHandle(PWCallback.DEFAULT_HANDLE);
        }
    }

    /**
     * Tests deactivating a user-Group.
     *
     * @param userHandle             The escidoc user handle.
     * @param groupId                id of Group to deactivate.
     * @param expectedExceptionClass expectedExceptionClass.
     * @throws Exception If anything fails.
     */
    protected void doTestDeactivate(final String userHandle, final String groupId, final Class<?> expectedExceptionClass)
        throws Exception {

        try {
            String userGroupXml = handleResult(userGroupClient.retrieve(groupId));
            String lastModificationDate = getLastModificationDateValue(EscidocAbstractTest.getDocument(userGroupXml));
            String taskParamXml = "<param last-modification-date=\"" + lastModificationDate + "\" />";
            userGroupClient.activate(groupId, taskParamXml);
        }
        catch (final AlreadyActiveException e) {
        }

        String userGroupXml = handleResult(userGroupClient.retrieve(groupId));

        try {
            PWCallback.setHandle(userHandle);
            String lastModificationDate = getLastModificationDateValue(EscidocAbstractTest.getDocument(userGroupXml));
            String taskParamXml = "<param last-modification-date=\"" + lastModificationDate + "\" />";
            userGroupClient.deactivate(groupId, taskParamXml);
            if (expectedExceptionClass != null) {
                EscidocAbstractTest.failMissingException(expectedExceptionClass);
            }
        }
        catch (final Exception e) {
            if (expectedExceptionClass == null) {
                EscidocAbstractTest.failException(e);
            }
            else {
                EscidocAbstractTest.assertExceptionType(expectedExceptionClass, e);
            }
        }
        finally {
            PWCallback.setHandle(PWCallback.DEFAULT_HANDLE);
        }
    }

    /**
     * retrieves an UserGroup.
     *
     * @param handle                 The handle of the user that shall call the method.
     * @param id                     The id of the user group.
     * @param expectedExceptionClass expectedExceptionClass.
     * @return Returns the UserGroup-xml.
     * @throws Exception If anything fails
     */
    public String doTestRetrieve(final String handle, final String id, final Class expectedExceptionClass)
        throws Exception {

        String replacedHandle = handle;
        if (replacedHandle == null) {
            replacedHandle = PWCallback.DEFAULT_HANDLE;
        }

        PWCallback.setHandle(replacedHandle);
        String retrievedXml = null;
        try {
            retrievedXml = retrieve(id);
            if (expectedExceptionClass != null) {
                EscidocAbstractTest.failMissingException(expectedExceptionClass);
            }
        }
        catch (final Exception e) {
            if (expectedExceptionClass == null) {
                EscidocAbstractTest.failException(e);
            }
            else {
                EscidocAbstractTest.assertExceptionType(expectedExceptionClass, e);
            }
        }
        finally {
            PWCallback.setHandle(PWCallback.DEFAULT_HANDLE);
        }
        return retrievedXml;
    }

    /**
     * Adds selectors to a UserGroup.
     *
     * @param handle                 The handle of the user that shall call the method.
     * @param id                     The id of the user group.
     * @param taskParam              The taskParam.
     * @param expectedExceptionClass expectedExceptionClass.
     * @return Returns the UserGroup-xml.
     * @throws Exception If anything fails
     */
    public String doTestAddSelectors(
        final String handle, final String id, final String taskParam, final Class expectedExceptionClass)
        throws Exception {

        String replacedHandle = handle;
        if (replacedHandle == null) {
            replacedHandle = PWCallback.DEFAULT_HANDLE;
        }

        PWCallback.setHandle(replacedHandle);
        String retrievedXml = null;
        try {
            retrievedXml = addSelectors(id, taskParam);
            if (expectedExceptionClass != null) {
                EscidocAbstractTest.failMissingException(expectedExceptionClass);
            }
        }
        catch (final Exception e) {
            if (expectedExceptionClass == null) {
                EscidocAbstractTest.failException(e);
            }
            else {
                EscidocAbstractTest.assertExceptionType(expectedExceptionClass, e);
            }
        }
        finally {
            PWCallback.setHandle(PWCallback.DEFAULT_HANDLE);
        }
        return retrievedXml;
    }

    /**
     * Removes selectors from a UserGroup.
     *
     * @param handle                 The handle of the user that shall call the method.
     * @param id                     The id of the user group.
     * @param taskParam              The taskParam.
     * @param expectedExceptionClass expectedExceptionClass.
     * @return Returns the UserGroup-xml.
     * @throws Exception If anything fails
     */
    public String doTestRemoveSelectors(
        final String handle, final String id, final String taskParam, final Class expectedExceptionClass)
        throws Exception {

        String replacedHandle = handle;
        if (replacedHandle == null) {
            replacedHandle = PWCallback.DEFAULT_HANDLE;
        }

        PWCallback.setHandle(replacedHandle);
        String retrievedXml = null;
        try {
            retrievedXml = removeSelectors(id, taskParam);
            if (expectedExceptionClass != null) {
                EscidocAbstractTest.failMissingException(expectedExceptionClass);
            }
        }
        catch (final Exception e) {
            if (expectedExceptionClass == null) {
                EscidocAbstractTest.failException(e);
            }
            else {
                EscidocAbstractTest.assertExceptionType(expectedExceptionClass, e);
            }
        }
        finally {
            PWCallback.setHandle(PWCallback.DEFAULT_HANDLE);
        }
        return retrievedXml;
    }

    /**
     * Adds selectors as task-params.
     *
     * @param selectors            list of selectors to add.
     * @param lastModificationDate The timestamp of the resource.
     * @return Returns the created task param xml.
     * @throws Exception Thrown if anything fails.
     */
    protected String getAddSelectorsTaskParam(final List<String[]> selectors, final String lastModificationDate)
        throws Exception {

        String param = "<param last-modification-date=\"" + lastModificationDate + "\" ";
        param += ">";
        Iterator<String[]> iterator = selectors.iterator();
        while (iterator.hasNext()) {
            String[] selector = iterator.next();
            String name = selector[0];
            String type = selector[1];
            String value = selector[2];
            param += "<selector name=\"" + name + "\" type=\"" + type + "\">" + value + "</selector>";
        }
        param += "</param>";

        return param;
    }

    /**
     * Get the XML taskParam for the method removeSelectors.
     *
     * @param lastModificationDate The timestamp of the resource.
     * @return Returns the created task param xml.
     * @throws Exception Thrown if anything fails.
     */
    public String getRemoveSelectorsTaskParam(final Vector<String> selectorIds, final String lastModificationDate)
        throws Exception {

        String param = "<param last-modification-date=\"" + lastModificationDate + "\" ";
        param += " xmlns:user-group=\"http://www.escidoc.de/schemas/usergroup/0.6\" >";
        Iterator<String> iterator = selectorIds.iterator();
        while (iterator.hasNext()) {
            String selectorId = iterator.next();

            param += "<id>" + selectorId + "</id>";

        }
        param += "</param>";
        return param;
    }

    /**
     * Inserts the given values into the provided document .
     *
     * @param document    The document.
     * @param email       The email.
     * @param name        The name.
     * @param label       The label.
     * @param description The description.
     * @throws Exception If anything fails.
     */
    protected void insertUserGroupValues(
        final Document document, final String email, final String name, final String label, final String description)
        throws Exception {

        assertXmlExists("No email found in template data. ", document, "/user-group/properties/email");
        assertXmlExists("No name found in template data. ", document, "/user-group/properties/name");
        assertXmlExists("No label found in template data. ", document, "/user-group/properties/label");
        assertXmlExists("No description found in template data. ", document, "/user-group/properties/description");

        if (email != null) {
            final Node emailNode = selectSingleNode(document, "/user-group/properties/email");
            emailNode.setTextContent(email);
        }

        if (name != null) {
            final Node nameNode = selectSingleNode(document, "/user-group/properties/name");
            nameNode.setTextContent(name);
        }

        if (label != null) {
            final Node labelNode = selectSingleNode(document, "/user-group/properties/label");
            labelNode.setTextContent(label);
        }

        if (description != null) {
            final Node descriptionNode = selectSingleNode(document, "/user-group/properties/description");
            descriptionNode.setTextContent(description);
        }

    }

    /**
     * Assert that the user group is active and has all required elements.<br/> This method delegates to
     * assertUserGroup. Additionally, it is asserted that the active flag is set to true.
     *
     * @param toBeAssertedXml         The created/updated user group.
     * @param originalXml             The template used to create/update the user group this parameter is
     *                                <code>null</code>, no check with the original data is performed.
     * @param timestampBeforeCreation A timestamp before the creation has been started. This is used to check the
     *                                creation date.
     * @param timestampBeforeLastModification
     *                                A timestamp before the last modification has been started. This is used to check
     *                                the last modification date.
     * @param assertCreationDate      Flag to indicate if the creation-date and created-by values shall be asserted
     *                                (<code>true</code>) or not (<code>false</code>).
     * @return Returns the document representing the provided xml data.
     * @throws Exception If anything fails.
     */
    public Document assertActiveUserGroup(
        final String toBeAssertedXml, final String originalXml, final String timestampBeforeCreation,
        final String timestampBeforeLastModification, final boolean assertCreationDate) throws Exception {

        final Document toBeAssertedDocument =
            assertUserGroup(toBeAssertedXml, originalXml, timestampBeforeCreation, timestampBeforeLastModification,
                assertCreationDate);

        assertXmlEquals("User group is not active. ", toBeAssertedDocument, XPATH_USER_GROUP_ACTIVE, "true");

        return toBeAssertedDocument;
    }

    /**
     * Assert that the user group is deactive and has all required elements.<br/> This method delegates to
     * assertUserGroup. Additionally, it is asserted that the active flag is set to false.
     *
     * @param toBeAssertedXml         The created/updated user group.
     * @param originalXml             The template used to create/update the user group. If this parameter is
     *                                <code>null</code>, no check with the original data is performed.
     * @param timestampBeforeCreation A timestamp before the creation has been started. This is used to check the
     *                                creation date.
     * @param timestampBeforeLastModification
     *                                A timestamp before the last modification has been started. This is used to check
     *                                the last modification date.
     * @param assertCreationDate      Flag to indicate if the creation-date and created-by values shall be asserted
     *                                (<code>true</code>) or not (<code>false</code>).
     * @return Returns the document representing the provided xml data.
     * @throws Exception If anything fails.
     */
    public Document assertDeactiveUserGroup(
        final String toBeAssertedXml, final String originalXml, final String timestampBeforeCreation,
        final String timestampBeforeLastModification, final boolean assertCreationDate) throws Exception {

        final Document toBeAssertedDocument =
            assertUserGroup(toBeAssertedXml, originalXml, timestampBeforeCreation, timestampBeforeLastModification,
                assertCreationDate);

        assertXmlEquals("User group is not deactive. ", toBeAssertedDocument, XPATH_USER_GROUP_ACTIVE, "false");

        return toBeAssertedDocument;
    }

    /**
     * Assert that the user group has all required elements.<br/>
     * <p/>
     * <ul> <li>It is asserted that the active flag exists, but the value of this flag is not checked.</li> </ul>
     *
     * @param toBeAssertedXml         The created/updated user group.
     * @param originalXml             The template used to create/update the user group. If this parameter is
     *                                <code>null</code>, no check with the original data is performed.
     * @param timestampBeforeCreation A timestamp before the creation has been started. This is used to check the
     *                                creation date.
     * @param timestampBeforeLastModification
     *                                A timestamp before the last modification has been started. This is used to check
     *                                the last modification date.
     * @param assertCreationDate      Flag to indicate if the creation-date and created-by values shall be asserted
     *                                (<code>true</code>) or not (<code>false</code>).
     * @return Returns the document representing the provided xml data.
     * @throws Exception If anything fails.
     */
    public Document assertUserGroup(
        final String toBeAssertedXml, final String originalXml, final String timestampBeforeCreation,
        final String timestampBeforeLastModification, final boolean assertCreationDate) throws Exception {

        assertXmlValidUserGroup(toBeAssertedXml);
        Document toBeAssertedDocument = EscidocAbstractTest.getDocument(toBeAssertedXml);

        String[] rootValues =
            assertRootElement("Invalid root element. ", toBeAssertedDocument, XPATH_USER_GROUP,
                Constants.USER_GROUP_BASE_URI, timestampBeforeLastModification);
        final String id = rootValues[0];

        assertReferencingElement("Assert of resources failed. ", toBeAssertedDocument, XPATH_USER_GROUP_RESOURCES, null);
        assertReferencingElement("Assert of resource current-grants failed. ", toBeAssertedDocument,
            XPATH_USER_GROUP_RESOURCES + "/" + "current-grants[@href=\"/aa/user-group/" + id
                + "/resources/current-grants\"]", null);

        // assert properties
        assertPropertiesElementUnversioned("Asserting user group properties failed. ", toBeAssertedDocument,
            XPATH_USER_GROUP_PROPERTIES, timestampBeforeCreation);

        // assert active flag
        assertXmlExists("Missing active flag. ", toBeAssertedDocument, XPATH_USER_GROUP_ACTIVE);

        if (originalXml != null) {
            final Document originalDocument = EscidocAbstractTest.getDocument(originalXml);

            if (assertCreationDate) {
                final String expectedCreationDate = getCreationDateValue(originalDocument);
                if (expectedCreationDate != null) {

                    // creation-date
                    assertXmlEquals("Creation date mismatch, ", toBeAssertedDocument, XPATH_USER_GROUP_CREATION_DATE,
                        expectedCreationDate);

                    // created-by
                    assertCreatedBy("Created-by invalid", originalDocument, toBeAssertedDocument);
                }
            }

            // label
            assertXmlEquals("Label mismatch, ", originalDocument, toBeAssertedDocument, XPATH_USER_GROUP_LABEL);

            // selectors
            //TODO: check for selectors
        }

        return toBeAssertedDocument;
    }

}
