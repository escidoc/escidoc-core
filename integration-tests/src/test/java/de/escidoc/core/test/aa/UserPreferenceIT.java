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
import de.escidoc.core.common.exceptions.remote.application.missing.MissingAttributeValueException;
import de.escidoc.core.common.exceptions.remote.application.notfound.PreferenceNotFoundException;
import de.escidoc.core.common.exceptions.remote.application.violated.OptimisticLockingException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.transform.TransformerException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

/**
 * Test suite for the Preferences of an UserAccount.
 *
 * @author Frank Schwichtenberg
 */
public class UserPreferenceIT extends UserPreferenceTestBase {

    private static final String PREEXISTING_USER = "escidoc:testsystemadministrator";

    /**
     * Set up servlet test.
     *
     * @throws Exception If anything fails.
     */
    @Before
    public void initialize() throws Exception {
    }

    /**
     * Clean up after servlet test.
     *
     * @throws Exception If anything fails.
     */
    @After
    public void deinitialize() throws Exception {
    }

    @Test
    public void createUserPreference() throws Exception {
        String userId = PREEXISTING_USER;
        String key = "KeyForTestCreate-" + System.currentTimeMillis();
        String value = "ValueForTestCreate";

        Map<String, String> expectedPreferences = getMapFromPreferences(userId);
        expectedPreferences.put(key, value);

        String createdXml =
            createPreference(userId, "<preference xmlns=\"http://www.escidoc.de/schemas/preferences/0.1\"" + " name=\""
                + key + "\">" + value + "</preference>");
        assertValidUserPreferences(retrievePreferences(userId), userId, expectedPreferences);
    }

    @Test
    public void retrieveUserPreference() throws Exception {
        String userId = PREEXISTING_USER;
        String key = "KeyForTestCreate-" + System.currentTimeMillis();
        String value = "ValueForTestCreate";

        Map<String, String> expectedPreferences = getMapFromPreferences(userId);
        expectedPreferences.put(key, value);

        String createdXml =
            createPreference(userId, "<preference xmlns=\"http://www.escidoc.de/schemas/preferences/0.1\"" + " name=\""
                + key + "\">" + value + "</preference>");

        String retrievedXml = retrievePreference(userId, key);
        assertXmlEquals("Difference between the return value of create and a retrieve afterwards. ", createdXml,
            retrievedXml);
        assertValidUserPreferences(retrievedXml, null, null);
    }

    @Test
    public void deleteUserPreference() throws Exception {
        String userId = PREEXISTING_USER;
        String key = "KeyForTestCreate-" + System.currentTimeMillis();
        String value = "ValueForTestCreate";

        Map<String, String> expectedPreferences = getMapFromPreferences(userId);
        expectedPreferences.put(key, value);

        String createdXml =
            createPreference(userId, "<preference xmlns=\"http://www.escidoc.de/schemas/preferences/0.1\"" + " name=\""
                + key + "\">" + value + "</preference>");
        assertValidUserPreferences(retrievePreferences(userId), userId, expectedPreferences);

        deletePreference(userId, key);
        expectedPreferences.remove(key);
        assertValidUserPreferences(retrievePreferences(userId), userId, expectedPreferences);
    }

    @Test
    public void declineDeleteUserPreferenceNonexistingPreference() throws Exception {
        Class ec = PreferenceNotFoundException.class;

        String userId = PREEXISTING_USER;
        String key = "KeyForTestCreate-" + System.currentTimeMillis();
        String value = "ValueForTestCreate";

        Map<String, String> expectedPreferences = getMapFromPreferences(userId);
        expectedPreferences.put(key, value);

        String createdXml =
            createPreference(userId, "<preference xmlns=\"http://www.escidoc.de/schemas/preferences/0.1\"" + " name=\""
                + key + "\">" + value + "</preference>");
        assertValidUserPreferences(retrievePreferences(userId), userId, expectedPreferences);

        deletePreference(userId, key);
        try {
            deletePreference(userId, key);
            fail("Expected exception " + ec.getCanonicalName() + " not thrown.");
        }
        catch (final Exception e) {
            assertExceptionType(ec, e);
        }
    }

    @Test
    public void declineInvalidXmlCreateUserPreference() throws Exception {
        String userId = PREEXISTING_USER;
        String key = "KeyForTestCreate-" + System.currentTimeMillis();
        String value = "ValueForTestCreate";

        Map<String, String> expectedPreferences = getMapFromPreferences(userId);
        expectedPreferences.put(key, value);

        Class ec = XmlCorruptedException.class;
        try {
            createPreference(userId, "<preferenc xmlns=\"http://www.escidoc.de/schemas/preferences/0.1\"" + " name=\""
                + key + "\">" + value + "</preference>");
        }
        catch (final Exception e) {
            assertExceptionType(ec, e);
        }
        try {
            createPreference(userId, "<preferenc xmlns=\"http://www.escidoc.de/schemas/preferences/0.1\"" + " name=\""
                + key + "\">");
        }
        catch (final Exception e) {
            assertExceptionType(ec, e);
        }

        ec = XmlSchemaValidationException.class;
        try {
            createPreference(userId, "<preference xmlns=\"http://www.escidoc.de/schemas/preferences/0.1\"" + " >"
                + value + "</preference>");
        }
        catch (final Exception e) {
            assertExceptionType(ec, e);
        }
        try {
            createPreference(userId, "<preference xmlns=\"http://www.escidoc.de\"" + " name=\"" + key + "\">" + value
                + "</preference>");
        }
        catch (final Exception e) {
            assertExceptionType(ec, e);
        }
    }

    @Test
    public void retrieveUserPreferences() throws Exception {
        String userId = PREEXISTING_USER;
        // FIXME dont use retrieve to test retrieve
        Map<String, String> expectedPreferences = getMapFromPreferences(userId);

        String preferences = retrievePreferences(userId);
        assertValidUserPreferences(preferences, userId, expectedPreferences);
    }

    @Test
    public void updateUserPreferences() throws Exception {
        String userId = PREEXISTING_USER;
        String key = "KeyForTestCreate-" + System.currentTimeMillis();
        String value = "ValueForTestCreate";

        String createdXml =
            createPreference(userId, "<preference xmlns=\"http://www.escidoc.de/schemas/preferences/0.1\"" + " name=\""
                + key + "\">" + value + "</preference>");
        String timestamp = getLastModificationDateValue(getDocument(createdXml));

        updatePreferences(userId, "<preferences xmlns=\"http://www.escidoc.de/schemas/preferences/0.1\" "
            + "last-modification-date=\"" + timestamp + "\"><preference" + " name=\"" + key
            + "\">updated</preference></preferences>");
        Map<String, String> expectedPreferences = new HashMap<String, String>();
        expectedPreferences.put(key, "updated");
        String tmp = retrievePreferences(userId);
        assertValidUserPreferences(tmp, userId, expectedPreferences);
    }

    @Test
    public void updateUserPreferencesWrongTimestamp() throws Exception {
        String userId = PREEXISTING_USER;
        String key = "KeyForTestCreate-" + System.currentTimeMillis();
        String value = "ValueForTestCreate";

        String createdXml =
            createPreference(userId, "<preference xmlns=\"http://www.escidoc.de/schemas/preferences/0.1\"" + " name=\""
                + key + "\">" + value + "</preference>");

        Class ec = OptimisticLockingException.class;
        try {
            updatePreferences(userId, "<preferences xmlns=\"http://www.escidoc.de/schemas/preferences/0.1\" "
                + "last-modification-date=\"1009-04-03T12:37:14.250Z\"><preference" + " name=\"" + key
                + "\">updated</preference></preferences>");
            fail(ec.getName() + " expected");
        }
        catch (final Exception e) {
            assertExceptionType(ec, e);
        }

    }

    @Test
    public void updateUserPreferencesWithOldTimestamp() throws Exception {
        String userId = PREEXISTING_USER;
        String key = "KeyForTestCreate-" + System.currentTimeMillis();
        String value = "ValueForTestCreate";

        String createdXml =
            createPreference(userId, "<preference xmlns=\"http://www.escidoc.de/schemas/preferences/0.1\"" + " name=\""
                + key + "\">" + value + "</preference>");
        String timestamp = getLastModificationDateValue(getDocument(createdXml));

        updatePreferences(userId, "<preferences xmlns=\"http://www.escidoc.de/schemas/preferences/0.1\" "
            + "last-modification-date=\"" + timestamp + "\"><preference" + " name=\"" + key
            + "\">updated1</preference></preferences>");

        Class ec = OptimisticLockingException.class;
        try {
            updatePreferences(userId, "<preferences xmlns=\"http://www.escidoc.de/schemas/preferences/0.1\" "
                + "last-modification-date=\"" + timestamp + "\"><preference" + " name=\"" + key
                + "\">updated2</preference></preferences>");
            fail(ec.getName() + " expected");
        }
        catch (final Exception e) {
            assertExceptionType(ec, e);
        }

    }

    @Test
    public void updateUserPreferencesWithoutTimestamp() throws Exception {
        String userId = PREEXISTING_USER;
        String key = "KeyForTestCreate-" + System.currentTimeMillis();
        String value = "ValueForTestCreate";

        String createdXml =
            createPreference(userId, "<preference " + "xmlns=\"http://www.escidoc.de/schemas/preferences/0.1\""
                + " name=\"" + key + "\">" + value + "</preference>");

        Class ec = MissingAttributeValueException.class;
        try {
            updatePreferences(userId, "<preferences " + "xmlns=\"http://www.escidoc.de/schemas/preferences/0.1\">"
                + "<preference name=\"" + key + "\">updated</preference></preferences>");
        }
        catch (final Exception e) {
            assertExceptionType(ec, e);
        }

    }

    @Test
    public void updateUserPreference() throws Exception {
        String userId = PREEXISTING_USER;
        String key = "KeyForTestCreate-" + System.currentTimeMillis();
        String value = "ValueForTestCreate";

        Map<String, String> expectedPreferences = getMapFromPreferences(userId);

        String createdXml =
            createPreference(userId, "<preference xmlns=\"http://www.escidoc.de/schemas/preferences/0.1\"" + " name=\""
                + key + "\">" + value + "</preference>");
        String timestamp = getLastModificationDateValue(getDocument(createdXml));

        updatePreference(userId, key, "<preference xmlns=\"http://www.escidoc.de/schemas/preferences/0.1\""
            + " last-modification-date=\"" + timestamp + "\"" + " name=\"" + key + "\">single updated</preference>");
        expectedPreferences.put(key, "single updated");
        assertValidUserPreferences(retrievePreference(userId, key), null, null);
        assertValidUserPreferences(retrievePreferences(userId), userId, expectedPreferences);
    }

    @Test
    public void updateUserPreferenceWrongTimestamp() throws Exception {
        String userId = PREEXISTING_USER;
        String key = "KeyForTestCreate-" + System.currentTimeMillis();
        String value = "ValueForTestCreate";

        Map<String, String> expectedPreferences = getMapFromPreferences(userId);

        String createdXml =
            createPreference(userId, "<preference xmlns=\"http://www.escidoc.de/schemas/preferences/0.1\"" + " name=\""
                + key + "\">" + value + "</preference>");

        Class ec = OptimisticLockingException.class;
        try {
            updatePreference(userId, key, "<preference xmlns=\"http://www.escidoc.de/schemas/preferences/0.1\""
                + " last-modification-date=\"1009-04-03T12:37:14.250Z\"" + " name=\"" + key
                + "\">single updated</preference>");
            fail(ec.getName() + " expected");
        }
        catch (final Exception e) {
            assertExceptionType(ec, e);
        }
    }

    @Test
    public void updateUserPreferenceWithoutTimestamp() throws Exception {
        String userId = PREEXISTING_USER;
        String key = "KeyForTestCreate-" + System.currentTimeMillis();
        String value = "ValueForTestCreate";

        Map<String, String> expectedPreferences = getMapFromPreferences(userId);

        String createdXml =
            createPreference(userId, "<preference xmlns=\"http://www.escidoc.de/schemas/preferences/0.1\"" + " name=\""
                + key + "\">" + value + "</preference>");

        Class ec = MissingAttributeValueException.class;
        try {
            updatePreference(userId, key, "<preference xmlns=\"http://www.escidoc.de/schemas/preferences/0.1\""
                + " name=\"" + key + "\">single updated</preference>");
            fail(ec.getName() + " expected");
        }
        catch (final Exception e) {
            assertExceptionType(ec, e);
        }
    }

    public void assertValidUserPreferences(String preferencesXml, String userId, Map<String, String> preferenceMap)
        throws Exception {

        assertXmlValidPreferences(preferencesXml);
        if (userId == null) {
            return;
        }

        String href = "/aa/user-account/" + userId + "/resources/preferences";
        Document preferencesXmlDocument = getDocument(preferencesXml);

        try {
            selectSingleNodeAsserted(preferencesXmlDocument, "/preferences/@base");
            selectSingleNodeAsserted(preferencesXmlDocument, "/preferences[@href = '" + href + "']");
            int count = preferenceMap.size();
            if (count > 0) {
                selectSingleNodeAsserted(preferencesXmlDocument, "/preferences/preference[" + count + "]");
            }
            else {
                assertNull("Unexpected element /preferences/preference", selectSingleNode(preferencesXmlDocument,
                    "/preferences/preference[1]"));
            }

            // check if every entry from given map is in the document
            HashSet<String> hashes = new HashSet<String>();
            Iterator<Entry<String, String>> it = preferenceMap.entrySet().iterator();
            while (it.hasNext()) {
                Entry<String, String> e = it.next();
                hashes.add(e.getKey() + e.getValue());
            }
            NodeList preferenceElements = selectNodeList(preferencesXmlDocument, "/preferences/preference");
            int elementCount = preferenceElements.getLength();
            assertEquals("Cardinality of preference elements is not in sync with given map.", count, elementCount);
            // iterate elements of the xml document
            for (int i = 0; i < elementCount; i++) {
                // check if key value pair is in given map
                String preferenceName = preferenceElements.item(i).getAttributes().getNamedItem("name").getNodeValue();
                String preferenceValue = preferenceElements.item(i).getTextContent();
                boolean wasInHashesOutOfMap = hashes.remove(preferenceName + preferenceValue);
                if (!wasInHashesOutOfMap) {
                    fail("Found unexpected preference [" + preferenceName + "=" + preferenceValue + "].");
                }
            }
            // all entries should be removed from hashes(-out-of-map), now
            if (!hashes.isEmpty()) {
                fail("Expected preferences not found. [" + hashes.toString() + "]");
            }
        }
        catch (final AssertionError e) {
            throw e;
        }
        finally {
        }
    }

    private Map<String, String> getMapFromPreferences(String userId) throws TransformerException, Exception {
        NodeList list = selectNodeList(getDocument(retrievePreferences(userId)), "/preferences/preference");
        Map<String, String> map = new HashMap<String, String>();

        int elementCount = list.getLength();
        // iterate elements of the xml document
        for (int i = 0; i < elementCount; i++) {
            String preferenceName = list.item(i).getAttributes().getNamedItem("name").getNodeValue();
            String preferenceValue = list.item(i).getTextContent();
            map.put(preferenceName, preferenceValue);
        }

        return map;
    }
}
