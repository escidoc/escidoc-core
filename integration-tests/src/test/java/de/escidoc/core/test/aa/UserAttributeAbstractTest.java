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

import de.escidoc.core.common.exceptions.remote.application.invalid.XmlCorruptedException;
import de.escidoc.core.common.exceptions.remote.application.invalid.XmlSchemaValidationException;
import de.escidoc.core.common.exceptions.remote.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.remote.application.notfound.UserAccountNotFoundException;
import de.escidoc.core.common.exceptions.remote.application.notfound.UserAttributeNotFoundException;
import de.escidoc.core.common.exceptions.remote.application.violated.OptimisticLockingException;
import de.escidoc.core.test.EscidocRestSoapTestBase;
import de.escidoc.core.test.security.client.PWCallback;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.Assert.fail;

/**
 * Test suite for the Attributes of an UserAccount.
 * 
 * @author MIH
 * 
 */
public class UserAttributeAbstractTest extends UserAttributeTestBase {

    private static final String USER_TEST = 
        PWCallback.ID_PREFIX + PWCallback.TEST_HANDLE;
    
    private static final String USER_TEST1 = 
        PWCallback.ID_PREFIX + PWCallback.TEST_HANDLE1;
    
    /**
     * The constructor.
     * 
     * @param transport
     *            The transport identifier.
     * 
     * @throws Exception
     *             If anything fails.
     */
    public UserAttributeAbstractTest(final int transport) throws Exception {
        super(transport);
    }

    /**
     * Set up servlet test.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Before
    public void initialize() throws Exception {
    }

    /**
     * Clean up after servlet test.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @After
    public void deinitialize() throws Exception {
    }

    /**
     * Test successfull creation of User-Attribute.
     * 
     * @test.name Create UserAttribute - Xml
     * @test.id createUserAttribute
     * @test.input UserAttribute XML representation
     * @test.inputDescription: UserAttribute XML representation for
     *                         creating a new UserAttribute is provided.
     * @test.expected: OK
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void createUserAttribute() throws Exception {
        for (int i = 0; i < 2; i++) {
            String key = "KeyForTestCreate";
            String value = "ValueForTestCreate-" + System.currentTimeMillis();

            Collection<String> expectedAttributes = 
                    getCollectionFromAttributes(USER_TEST);
            expectedAttributes.add(key + value + "true");

            String createdXml =
                createAttribute(USER_TEST,
                    "<attribute xmlns="
                    + "\"http://www.escidoc.de/schemas/attributes/0.1\""
                        + " name=\"" + key + "\">" + value + "</attribute>");
            assertValidUserAttributes(retrieveAttributes(USER_TEST), USER_TEST,
                expectedAttributes);
        }
    }

    /**
     * Test declining creation of User-Attribute.
     * 
     * @test.name Create UserAttribute - Corrupted Xml
     * @test.id declineInvalidXmlCreateUserAttribute
     * @test.input UserAttribute XML representation
     * @test.inputDescription: UserAttribute corrupted XML representation for
     *                         creating a new UserAttribute is provided.
     * @test.expected: XmlCorruptedException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void declineInvalidXmlCreateUserAttribute() throws Exception {
        String key = "KeyForTestCreate-" + System.currentTimeMillis();
        String value = "ValueForTestCreate";

        try {
            createAttribute(USER_TEST,
                "<attribut xmlns=\"http://www.escidoc.de/schemas/attributes/0.1\""
                    + " name=\"" + key + "\">" + value + "</attribute>");
            EscidocRestSoapTestBase
                .failMissingException(XmlCorruptedException.class);
        }
        catch (final Exception e) {
            assertExceptionType(XmlCorruptedException.class, e);
        }
        try {
            createAttribute(USER_TEST,
                "<attribut xmlns=\"http://www.escidoc.de/schemas/attributes/0.1\""
                    + " name=\"" + key + "\">");
            EscidocRestSoapTestBase
                .failMissingException(XmlCorruptedException.class);
        }
        catch (final Exception e) {
            assertExceptionType(XmlCorruptedException.class, e);
        }

        try {
            createAttribute(USER_TEST,
                "<attribute xmlns=\"http://www.escidoc.de/schemas/attributes/0.1\""
                    + " >" + value + "</attribute>");
            EscidocRestSoapTestBase
                .failMissingException(XmlSchemaValidationException.class);
        }
        catch (final Exception e) {
            assertExceptionType(XmlSchemaValidationException.class, e);
        }
        try {
            createAttribute(USER_TEST,
                "<attribute xmlns=\"http://www.escidoc.de\"" + " name=\""
                    + key + "\">" + value + "</attribute>");
            EscidocRestSoapTestBase
                .failMissingException(XmlSchemaValidationException.class);
        }
        catch (final Exception e) {
            assertExceptionType(XmlSchemaValidationException.class, e);
        }
    }

    /**
     * Test declining creation of UserAttribute without providing XML data.
     * 
     * @test.name Create UserAttribute - xml not provided
     * @test.id declineNoXmlCreateUserAttribute
     * @test.input None
     * @test.inputDescription: No UserAttribute XML representation for creating a
     *                         new UserAttribute is provided.
     * @test.expected: MissingMethodParameterException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void declineNoXmlCreateUserAttribute() throws Exception {

        try {
            createAttribute(USER_TEST, null);
            EscidocRestSoapTestBase
                .failMissingException(MissingMethodParameterException.class);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.assertExceptionType(
                MissingMethodParameterException.class, e);
        }
    }

    /**
     * Test retrieving User-Attributes for userId.
     * 
     * @test.name Retrieve UserAttributes
     * @test.id retrieveUserAttributes
     * @test.input UserId
     * @test.inputDescription: id of the user id provided.
     * @test.expected: Xml with all attributes of the user
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void retrieveUserAttributes() throws Exception {
        Collection<String> expectedAttributes = 
                    getCollectionFromAttributes(USER_TEST);

        String attributes = retrieveAttributes(USER_TEST);
        assertValidUserAttributes(attributes, USER_TEST, expectedAttributes);
    }

    /**
     * Test declining retrieving User-Attributes for nonexisting userId.
     * 
     * @test.name Retrieve UserAttributes with nonexisting userId
     * @test.id retrieveUserAttributesNonexistingUser
     * @test.input nonexisting UserId
     * @test.inputDescription: null.
     * @test.expected: UserAccountNotFoundException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void retrieveUserAttributesNonexistingUser() throws Exception {
        try {
            retrieveAttributes("nonexisting:user");
            EscidocRestSoapTestBase
                .failMissingException(UserAccountNotFoundException.class);
        } catch (final Exception e) {
            EscidocRestSoapTestBase.assertExceptionType(
                    UserAccountNotFoundException.class, e);
        }
    }

    /**
     * Test declining retrieving User-Attributes for null userId.
     * 
     * @test.name Retrieve UserAttributes with null userId
     * @test.id retrieveUserAttributesNullUser
     * @test.input null UserId
     * @test.inputDescription: null.
     * @test.expected: MissingParameterException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void retrieveUserAttributesNullUser() throws Exception {
        try {
            retrieveAttributes(null);
            EscidocRestSoapTestBase
                .failMissingException(MissingMethodParameterException.class);
        } catch (final Exception e) {
            EscidocRestSoapTestBase.assertExceptionType(
                    MissingMethodParameterException.class, e);
        }
    }

    /**
     * Test retrieving named User-Attributes with name for userId.
     * 
     * @test.name Retrieve UserAttribute with name
     * @test.id retrieveUserNamedAttributes
     * @test.input UserId and name
     * @test.inputDescription: id of the user and name of attribute provided.
     * @test.expected: Xml with attributes of the user having given name
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void retrieveUserNamedAttributes() throws Exception {
        String key = "retrieveTest";
        Collection<String> attributeIds = 
            getCollectionFromAttributesIds(USER_TEST, key);
        for (String attributeId : attributeIds) {
            deleteAttribute(USER_TEST, attributeId);
        }
        Collection<String> expectedAttributes = new ArrayList<String>();
        for (int i = 0; i < 2; i++) {
            long value =  System.currentTimeMillis();
            createAttribute(USER_TEST,
                    "<attribute xmlns="
                    + "\"http://www.escidoc.de/schemas/attributes/0.1\""
                        + " name=\"" + key + "\">" 
                        + value + "</attribute>");
            expectedAttributes.add(key + value + "true");
        }

        
        String attributes = retrieveNamedAttributes(USER_TEST, key);
        assertValidUserAttributes(attributes, USER_TEST, expectedAttributes);
    }

    /**
     * Test declining retrieving named User-Attributes for nonexisting userId.
     * 
     * @test.name Retrieve UserAttribute with nonexisting userId
     * @test.id retrieveUserNamedAttributesNonexistingUser
     * @test.input nonexisting UserId, nonexisting AttributeName
     * @test.inputDescription: id of nonexisting user 
     *      and name of nonexisting attribute provided.
     * @test.expected: UserAccountNotFoundException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void retrieveUserNamedAttributesNonexistingUser() throws Exception {
        try {
            retrieveNamedAttributes("nonexisting:user", "nonexisting");
            EscidocRestSoapTestBase
                .failMissingException(UserAccountNotFoundException.class);
        } catch (final Exception e) {
            EscidocRestSoapTestBase.assertExceptionType(
                    UserAccountNotFoundException.class, e);
        }
    }

    /**
     * Test retrieving named User-Attributes for nonexisting attributeName.
     * 
     * @test.name Retrieve UserAttribute with nonexisting attributeName
     * @test.id retrieveUserNamedAttributesNonexistingAttribute
     * @test.input UserId, nonexisting AttributeId
     * @test.inputDescription: id of the user 
     *      and name of nonexisting attribute provided.
     * @test.expected: empty list
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void retrieveUserNamedAttributesNonexistingAttribute() 
                                                throws Exception {
        Collection<String> expectedAttributes = new ArrayList<String>();
        String attributes = retrieveNamedAttributes(USER_TEST, "nonexisting");
        assertValidUserAttributes(attributes, USER_TEST, expectedAttributes);
    }

    /**
     * Test declining retrieving named User-Attributes for null userId.
     * 
     * @test.name Retrieve UserAttribute with null userId
     * @test.id retrieveUserNamedAttributesNullUser
     * @test.input null UserId, existing attributeName
     * @test.inputDescription: null and name of attribute provided.
     * @test.expected: MissingParameterException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void retrieveUserNamedAttributesNullUser() throws Exception {
        try {
            retrieveNamedAttributes(null, "attribute");
            EscidocRestSoapTestBase
                .failMissingException(MissingMethodParameterException.class);
        } catch (final Exception e) {
            EscidocRestSoapTestBase.assertExceptionType(
                    MissingMethodParameterException.class, e);
        }
    }

    /**
     * Test declining retrieving named User-Attributes for null attributeName.
     * 
     * @test.name Retrieve UserAttribute with null attributeName
     * @test.id retrieveUserNamedAttributesNullAttribute
     * @test.input UserId, null
     * @test.inputDescription: id of the user and null.
     * @test.expected: MissingParameterException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void retrieveUserNamedAttributesNullAttribute() throws Exception {
        try {
            retrieveNamedAttributes(USER_TEST, null);
            EscidocRestSoapTestBase
                .failMissingException(MissingMethodParameterException.class);
        } catch (final Exception e) {
            EscidocRestSoapTestBase.assertExceptionType(
                    MissingMethodParameterException.class, e);
        }
    }

    /**
     * Test retrieving User-Attribute with id for userId.
     * 
     * @test.name Retrieve UserAttribute with id
     * @test.id retrieveUserAttribute with id
     * @test.input UserId and attributeId
     * @test.inputDescription: id of the user and id of attribute provided.
     * @test.expected: Xml with attribute of the user having given id
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void retrieveUserAttribute() throws Exception {
        String name = "retrieveTest";
        long value =  System.currentTimeMillis();
        String attributeXml = createAttribute(USER_TEST,
            "<attribute xmlns="
            + "\"http://www.escidoc.de/schemas/attributes/0.1\""
            + " name=\"" + name + "\">" 
            + value + "</attribute>");
        String id = getObjidValue(attributeXml);
        String attribute = retrieveAttribute(USER_TEST, id);
        assertUserAttribute(USER_TEST, attribute, attributeXml);
    }

    /**
     * Test declining retrieving User-Attribute for nonexisting userId.
     * 
     * @test.name Retrieve UserAttribute with nonexisting userId
     * @test.id retrieveUserAttributeNonexistingUser
     * @test.input nonexisting UserId, nonexisting AttributeId
     * @test.inputDescription: id of nonexisting user 
     *      and id of nonexisting attribute provided.
     * @test.expected: UserAccountNotFoundException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void retrieveUserAttributeNonexistingUser() throws Exception {
        try {
            retrieveAttribute("nonexisting:user", "nonexisting");
            EscidocRestSoapTestBase
                .failMissingException(UserAccountNotFoundException.class);
        } catch (final Exception e) {
            EscidocRestSoapTestBase.assertExceptionType(
                    UserAccountNotFoundException.class, e);
        }
    }

    /**
     * Test retrieving User-Attribute for nonexisting attributeId.
     * 
     * @test.name Retrieve UserAttribute with nonexisting attributeId
     * @test.id retrieveUserAttributeNonexistingAttributeName
     * @test.input UserId, nonexisting AttributeId
     * @test.inputDescription: id of the user 
     *      and id of nonexisting attribute provided.
     * @test.expected: empty list
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void retrieveUserAttributeNonexistingAttribute() 
                                                throws Exception {
        Class< ? > ec = UserAttributeNotFoundException.class;

        try {
            retrieveAttribute(USER_TEST, "nonexisting");
            fail("Expected exception " + ec.getCanonicalName() + " not thrown.");
        }
        catch (final Exception e) {
            assertExceptionType(ec, e);
        }
    }

    /**
     * Test declining retrieving User-Attribute for null userId.
     * 
     * @test.name Retrieve UserAttribute with null userId
     * @test.id retrieveUserAttributeNullUser
     * @test.input null UserId, existing attributeId
     * @test.inputDescription: null and id of attribute provided.
     * @test.expected: MissingParameterException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void retrieveUserAttributeNullUser() throws Exception {
        try {
            retrieveAttribute(null, "attribute");
            EscidocRestSoapTestBase
                .failMissingException(MissingMethodParameterException.class);
        } catch (final Exception e) {
            EscidocRestSoapTestBase.assertExceptionType(
                    MissingMethodParameterException.class, e);
        }
    }

    /**
     * Test declining retrieving User-Attribute for null attributeId.
     * 
     * @test.name Retrieve UserAttribute with null attributeId
     * @test.id retrieveUserAttributeNullAttribute
     * @test.input UserId, null
     * @test.inputDescription: id of the user and null.
     * @test.expected: MissingParameterException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void retrieveUserAttributeNullAttribute() throws Exception {
        try {
            retrieveAttribute(USER_TEST, null);
            EscidocRestSoapTestBase
                .failMissingException(MissingMethodParameterException.class);
        } catch (final Exception e) {
            EscidocRestSoapTestBase.assertExceptionType(
                    MissingMethodParameterException.class, e);
        }
    }

    /**
     * Test updating a User-Attribute.
     * 
     * @test.name Update UserAttribute
     * @test.id updateUserAttribute
     * @test.input existing UserId, existing attributeName
     * @test.inputDescription: id of the user and name of attribute provided..
     * @test.expected: MissingParameterException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void updateUserAttribute() throws Exception {
        String key = "KeyForTestCreate";
        String value = "ValueForTestCreate" + System.currentTimeMillis();

        Collection<String> expectedAttributes = 
                getCollectionFromAttributes(USER_TEST);
        String attributeXml = createAttribute(USER_TEST,
            "<attribute xmlns=\"http://www.escidoc.de/schemas/attributes/0.1\""
                + " name=\"" + key + "\">" + value + "</attribute>");
        String id = getObjidValue(attributeXml);
        String timestamp =
            getLastModificationDateValue(getDocument(attributeXml));

        updateAttribute(USER_TEST, id,
            "<attribute xmlns=\"http://www.escidoc.de/schemas/attributes/0.1\""
                + " last-modification-date=\"" + timestamp + "\"" + " name=\""
                + key + "\">updated</attribute>");
        expectedAttributes.add(key + "updatedtrue");
        assertValidUserAttributes(retrieveAttributes(USER_TEST), USER_TEST,
            expectedAttributes);
    }

    /**
     * Test updating a User-Attribute with a wrong last-modification-date.
     * 
     * @test.name Update UserAttribute
     * @test.id updateUserAttribute
     * @test.input existing UserId, existing attributeName
     * @test.inputDescription: id of the user and name of attribute provided..
     * @test.expected: MissingParameterException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void updateUserAttributeWrongLastModificationDate() throws Exception {
        String key = "KeyForTestCreate";
        String value = "ValueForTestCreate" + System.currentTimeMillis();
        String attributeXml = createAttribute(USER_TEST,
            "<attribute xmlns=\"http://www.escidoc.de/schemas/attributes/0.1\""
                + " name=\"" + key + "\">" + value + "</attribute>");
        String id = getObjidValue(attributeXml);

        updateAttribute(USER_TEST, id, attributeXml);
        try {
            updateAttribute(USER_TEST, id, attributeXml);
            EscidocRestSoapTestBase
                .failMissingException(OptimisticLockingException.class);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.assertExceptionType(
                OptimisticLockingException.class, e);
        }
    }
    
    /**
     * Test deleting a User-Attribute.
     * 
     * @test.name Delete UserAttribute
     * @test.id deleteUserAttribute
     * @test.input existing UserId, existing attributeId
     * @test.inputDescription: id of the user and id of attribute provided..
     * @test.expected: 
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void deleteUserAttribute() throws Exception {
        String key = "KeyForTestCreate";
        String value = "ValueForTestCreate" + System.currentTimeMillis();
        String attributeXml = createAttribute(USER_TEST,
            "<attribute xmlns=\"http://www.escidoc.de/schemas/attributes/0.1\""
                + " name=\"" + key + "\">" + value + "</attribute>");
        String id = getObjidValue(attributeXml);
        retrieveAttribute(USER_TEST, id);
        deleteAttribute(USER_TEST, id);
        try {
            retrieveAttribute(USER_TEST, id);
            EscidocRestSoapTestBase
                .failMissingException(UserAttributeNotFoundException.class);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.assertExceptionType(
                    UserAttributeNotFoundException.class, e);
        }
    }
    
    /**
     * Test deleting a User-Attribute with nonexisting attributeId.
     * 
     * @test.name Delete UserAttribute NonexistingAttribute
     * @test.id deleteUserAttributeNonexistingAttribute
     * @test.input existing UserId, nonexisting attributeId
     * @test.inputDescription: id of the user and id of nonexisting attribute provided..
     * @test.expected: UserAttributeNotFoundException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void deleteUserAttributeNonexistingAttribute() throws Exception {
        try {
            deleteAttribute(USER_TEST, "nonexisting");
            EscidocRestSoapTestBase
                .failMissingException(UserAttributeNotFoundException.class);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.assertExceptionType(
                    UserAttributeNotFoundException.class, e);
        }
    }
    
    /**
     * Test deleting a User-Attribute with nonexisting userId.
     * 
     * @test.name Delete UserAttribute NonexistingUser
     * @test.id deleteUserAttributeNonexistingUser
     * @test.input nonexisting UserId, existing attributeId
     * @test.inputDescription: id of the nonexisting user and id of attribute provided..
     * @test.expected: UserAccountNotFoundException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void deleteUserAttributeNonexistingUser() throws Exception {
        String key = "KeyForTestCreate";
        String value = "ValueForTestCreate" + System.currentTimeMillis();
        String attributeXml = createAttribute(USER_TEST,
            "<attribute xmlns=\"http://www.escidoc.de/schemas/attributes/0.1\""
                + " name=\"" + key + "\">" + value + "</attribute>");
        String id = getObjidValue(attributeXml);
        try {
            deleteAttribute("nonexisting", id);
            EscidocRestSoapTestBase
                .failMissingException(UserAccountNotFoundException.class);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.assertExceptionType(
                    UserAccountNotFoundException.class, e);
        }
    }
    
    /**
     * Test deleting a User-Attribute with wrong attributeId.
     * 
     * @test.name Delete UserAttribute wrong attributeId
     * @test.id deleteUserAttributeWrongAttributeId
     * @test.input existing UserId, existing attributeId
     * @test.inputDescription: id of the user and id of attribute provided..
     * @test.expected: UserAttributeNotFoundException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void deleteUserAttributeWrongAttributeId() throws Exception {
        String key = "KeyForTestCreate";
        String value = "ValueForTestCreate" + System.currentTimeMillis();
        String attributeXml = createAttribute(USER_TEST1,
            "<attribute xmlns=\"http://www.escidoc.de/schemas/attributes/0.1\""
                + " name=\"" + key + "\">" + value + "</attribute>");
        String id = getObjidValue(attributeXml);
        try {
            deleteAttribute(USER_TEST, id);
            EscidocRestSoapTestBase
                .failMissingException(UserAttributeNotFoundException.class);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.assertExceptionType(
                    UserAttributeNotFoundException.class, e);
        } finally {
            deleteAttribute(USER_TEST1, id);
        }
    }
    
}
