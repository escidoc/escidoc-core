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

import de.escidoc.core.common.exceptions.remote.application.invalid.InvalidStatusException;
import de.escidoc.core.common.exceptions.remote.application.invalid.XmlCorruptedException;
import de.escidoc.core.common.exceptions.remote.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.remote.application.notfound.ContentRelationNotFoundException;
import de.escidoc.core.common.exceptions.remote.application.violated.OptimisticLockingException;
import de.escidoc.core.test.EscidocAbstractTest;
import de.escidoc.core.test.EscidocTestBase;
import de.escidoc.core.test.security.client.PWCallback;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Test lifecycle of Content Relation.
 *
 * @author Rozita Friedman
 */
public class ContentRelationLifecycleIT extends ContentRelationTestBase {

    private String relationXml = null;

    private String relationId = null;

    /**
     * Set up servlet test.
     *
     * @throws Exception If anything fails.
     */
    @Before
    public void setUp() throws Exception {
        String contentRelationXml = getExampleTemplate("content-relation-01.xml");
        relationXml = create(contentRelationXml);
        relationId = getObjidValue(relationXml);
    }

    /**
     * Clean up after servlet test.
     *
     * @throws Exception If anything fails.
     */
    @Override
    @After
    public void tearDown() throws Exception {

        super.tearDown();
        PWCallback.setHandle(PWCallback.DEFAULT_HANDLE);
    }

    /**
     * Test successful submitting a content relation in state "pending".
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOmSi1() throws Exception {

        DateTime t1 = getLastModificationDateValue2(getDocument(this.relationXml));

        String xml = submit(this.relationId, getStatusTaskParam(t1, null));
        DateTime t2 = getLastModificationDateValue2(getDocument(xml));

        assertTrue("Timestamp of submitted not after pending", t1.compareTo(t2) < 0);

        final Document submittedDocument = EscidocAbstractTest.getDocument(retrieve(this.relationId));
        DateTime t2a = getLastModificationDateValue2(submittedDocument);

        assertTrue("Timestamp not equal", t2.compareTo(t2a) == 0);

        assertXmlEquals("Unexpected status. ", submittedDocument, XPATH_CONTENT_RELATION + "/properties/public-status",
            STATE_SUBMITTED);
    }

    /**
     * Test calling release after submit and afterwards submit again.
     *
     * @throws Exception Thrown if submit or release fail or not exception is thrown if the second submit is called.
     */
    @Test
    public void testSubmitAfterRelease() throws Exception {

        String xml =
            submit(this.relationId, getStatusTaskParam(getLastModificationDateValue2(getDocument(this.relationXml)),
                null));
        xml = release(this.relationId, getStatusTaskParam(getLastModificationDateValue2(getDocument(xml)), null));

        try {
            submit(this.relationId, getStatusTaskParam(getLastModificationDateValue2(getDocument(xml)), null));
            fail("No exception on submit of a content relation in " + "a state 'released'");
        }
        catch (final Exception e) {
            Class<?> ec = InvalidStatusException.class;
            EscidocAbstractTest.assertExceptionType(ec.getName() + " expected.", ec, e);
        }
    }

    /**
     * Test successful submitting a content relation in state "in-revision".
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOmSi1_2() throws Exception {

        String param = getStatusTaskParam(getLastModificationDateValue2(getDocument(this.relationXml)), null);
        String xml = submit(this.relationId, param);

        param = getStatusTaskParam(getLastModificationDateValue2(getDocument(xml)), null);
        xml = revise(this.relationId, param);
        param = getStatusTaskParam(getLastModificationDateValue2(getDocument(xml)), null);

        final String revisedLastModificationDate = getLastModificationDateValue(getDocument(xml));

        try {
            submit(this.relationId, param);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException("Submitting the revised content relation failed. ", e);
        }

        String submittedXml = null;
        try {
            submittedXml = retrieve(this.relationId);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException("Retrieving the revised, submitted item failed. ", e);
        }
        final Document submittedDocument = EscidocAbstractTest.getDocument(submittedXml);
        assertDateBeforeAfter(revisedLastModificationDate, getLastModificationDateValue(submittedDocument));
        assertXmlEquals("Unexpected status. ", submittedDocument, XPATH_CONTENT_RELATION + "/properties/public-status",
            STATE_SUBMITTED);

    }

    /**
     * Test handling of non-ASCII character within submit comment.
     *
     * @throws Exception Thrown if escaping of non-ASCII character failed.
     */
    @Test
    public void testSubmitComment() throws Exception {

        String paramXml =
            getStatusTaskParam(getLastModificationDateValue2(getDocument(this.relationXml)),
                EscidocTestBase.ENTITY_REFERENCES);

        submit(this.relationId, paramXml);
        String submittedXml = retrieve(this.relationId);
        String commentString = null;
        Matcher m = Pattern.compile("comment[^>]*>([^<]*)</").matcher(submittedXml);
        if (m.find()) {
            commentString = m.group(1);
        }
        assertEquals(ENTITY_REFERENCES, commentString);
    }

    /**
     * 
     * @throws Exception
     */
    @Test
    public void testReleaseBeforeSubmitContentRelation() throws Exception {

        String param = getStatusTaskParam(getLastModificationDateValue2(getDocument(this.relationXml)), null);

        try {
            release(this.relationId, param);
            fail("No exception occured on release befor submit.");
        }
        catch (final Exception e) {
            Class<?> ec = InvalidStatusException.class;
            EscidocAbstractTest.assertExceptionType(ec.getName() + " expected.", ec, e);
        }
    }

    /**
     * Test release a Content Relation.
     *
     * @throws Exception Thrown if release behavior is not as expected.
     */
    @Test
    public void testReleaseContentRelation() throws Exception {

        String param = getStatusTaskParam(getLastModificationDateValue2(getDocument(this.relationXml)), null);
        submit(this.relationId, param);

        // validate escidoc XML
        String submitXml = retrieve(this.relationId);
        assertXmlValidContentRelation(submitXml);

        // check values
        assertXmlExists("Properties status not submitted", submitXml, XPATH_CONTENT_RELATION_STATUS
            + "[text() = 'submitted']");
        assertXmlNotNull("Status comment missing", getDocument(submitXml), XPATH_CONTENT_RELATION_STATUS_COMMENT);

        param = getStatusTaskParam(getLastModificationDateValue2(getDocument(submitXml)), null);
        release(this.relationId, param);

        // validate escidoc XML
        String releasedXml = retrieve(this.relationId);
        assertXmlValidContentRelation(releasedXml);

        // check values
        assertXmlExists("Properties status not released", releasedXml, XPATH_CONTENT_RELATION_STATUS
            + "[text() = 'released']");
        assertXmlNotNull("Status comment missing", getDocument(submitXml), XPATH_CONTENT_RELATION_STATUS_COMMENT);
    }

    /**
     * Test successful revising a content relation.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOMRvi1() throws Exception {

        String paramXml = getStatusTaskParam(getLastModificationDateValue2(getDocument(this.relationXml)), null);
        String response = submit(this.relationId, paramXml);

        final Document responseDoc = getDocument(response);
        final String submittedLastModificationDate = getLastModificationDateValue(responseDoc);
        paramXml = getStatusTaskParam(getLastModificationDateValue2(responseDoc), null);

        try {
            revise(this.relationId, paramXml);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException("Revising the submitted item failed", e);
        }

        final String revisedXml = retrieve(this.relationId);
        final Document revisedDocument = EscidocAbstractTest.getDocument(revisedXml);
        assertDateBeforeAfter(submittedLastModificationDate, getLastModificationDateValue(revisedDocument));
        assertXmlEquals("Unexpected status. ", revisedDocument, XPATH_CONTENT_RELATION + "/properties/public-status",
            STATE_IN_REVISION);

    }

    /**
     * Test declining revising a content relation in state pending.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOMRvi2() throws Exception {

        String param = getStatusTaskParam(getLastModificationDateValue2(getDocument(this.relationXml)), null);

        try {
            revise(this.relationId, param);
            EscidocAbstractTest.failMissingException(InvalidStatusException.class);
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType("Revising failed with unexpected exception. ",
                InvalidStatusException.class, e);
        }
    }

    /**
     * Test declining revising a content relation in state released.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOMRvi3() throws Exception {

        String param = getStatusTaskParam(getLastModificationDateValue2(getDocument(this.relationXml)), null);
        String xml = submit(this.relationId, param);

        param = getStatusTaskParam(getLastModificationDateValue2(getDocument(xml)), null);
        release(this.relationId, param);
        param = getStatusTaskParam(getLastModificationDateValue2(getDocument(xml)), null);

        try {
            revise(this.relationId, param);
            EscidocAbstractTest.failMissingException(InvalidStatusException.class);
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType("Revising failed with unexpected exception. ",
                InvalidStatusException.class, e);
        }
    }

    /**
     * Test declining revising an unknown content relation.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOMRvi5() throws Exception {

        String param = getStatusTaskParam(getLastModificationDateValue2(getDocument(this.relationXml)), null);

        try {
            revise(UNKNOWN_ID, param);
            EscidocAbstractTest.failMissingException(ContentRelationNotFoundException.class);
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType("Revising unknown content relation failed "
                + "with unexpected exception. ", ContentRelationNotFoundException.class, e);
        }
    }

    /**
     * Test declining revising an content relation without providing an item id.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOMRvi6() throws Exception {

        String param = getTheLastModificationParam(false, this.relationId);

        try {
            revise(null, param);
            EscidocAbstractTest.failMissingException(MissingMethodParameterException.class);
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType("Revising without id failed with unexpected exception. ",
                MissingMethodParameterException.class, e);
        }
    }

    /**
     * Test declining revising an content relation without providing task param.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOMRvi7() throws Exception {

        String param = getStatusTaskParam(getLastModificationDateValue2(getDocument(this.relationXml)), null);
        submit(this.relationId, param);

        try {
            revise(this.relationId, null);
            EscidocAbstractTest.failMissingException(MissingMethodParameterException.class);
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType("Revising without id failed with unexpected exception. ",
                MissingMethodParameterException.class, e);
        }
    }

    /**
     * Test declining revising a content relation without providing last modification date in task param.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOMRvi8() throws Exception {

        String param = getStatusTaskParam(getLastModificationDateValue2(getDocument(this.relationXml)), null);
        submit(this.relationId, param);
        param = "<param />";

        try {
            revise(this.relationId, null);
            EscidocAbstractTest.failMissingException(MissingMethodParameterException.class);
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType("Revising without last modification date failed "
                + "with unexpected exception. ", MissingMethodParameterException.class, e);
        }
    }

    /**
     * Test declining revising an content relation with corrupted task param.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOMRvi9() throws Exception {

        String param = getStatusTaskParam(getLastModificationDateValue2(getDocument(this.relationXml)), null);
        submit(this.relationId, param);
        param = "<param";

        try {
            revise(this.relationId, param);
            EscidocAbstractTest.failMissingException(XmlCorruptedException.class);
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType("Revising without last modification date failed "
                + "with unexpected exception. ", XmlCorruptedException.class, e);
        }
    }

    /**
     * Test declining revising an content relation with providing outdated last modification date in task param.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOMRvi10() throws Exception {

        String param = getStatusTaskParam(getLastModificationDateValue2(getDocument(this.relationXml)), null);
        submit(this.relationId, param);

        try {
            revise(this.relationId, param);
            EscidocAbstractTest.failMissingException(OptimisticLockingException.class);
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType("Revising with outdated last modification "
                + "date failed with unexpected exception. ", OptimisticLockingException.class, e);
        }
    }

    /**
     * Test successful revising an content relation (created by a user with createContentRelation permission) by an
     * administrator.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOMRvi11() throws Exception {

        // create use with permission to create contentRelations
        // String userId =
        createUserWithContentRelationRole("escidoc_useraccount_for_create.xml");

        // add content-relation-manager grants to depositor
        addContentRelationManagerGrant("escidoc:testdepositor");
        addContentRelationManagerGrant("escidoc:testadministrator");

        try {
            // create and submit item by a depositor
            PWCallback.setHandle(PWCallback.DEPOSITOR_HANDLE);
            String contentRelationXml = getExampleTemplate("content-relation-01.xml");
            relationXml = create(contentRelationXml);
            relationId = getObjidValue(relationXml);
            String param = getTheLastModificationParam(false, this.relationId);
            submit(relationId, param);
            param = getTheLastModificationParam(false, this.relationId);

            // revise the content relation by an administrator
            PWCallback.setHandle(PWCallback.DEFAULT_HANDLE);

            try {
                revise(relationId, param);
            }
            catch (final Exception e) {
                EscidocAbstractTest.failException("Revising the submitted content relation failed", e);
            }

            // retrieve, update and submit the item by the depositor
            PWCallback.setHandle(PWCallback.DEPOSITOR_HANDLE);
            try {
                this.relationXml = retrieve(this.relationId);
            }
            catch (final Exception e) {
                EscidocAbstractTest.failException("Retrieving the revised content relation by the "
                    + "depositor failed with exception. ", e);
            }
            this.relationXml.replaceFirst("", "");
            try {
                this.relationXml = update(this.relationId, this.relationXml);
            }
            catch (final Exception e) {
                EscidocAbstractTest.failException("Updating the revised content relation by the "
                    + "depositor failed with exception. ", e);
            }
            param = getTheLastModificationParam(false, this.relationId);
            try {
                submit(this.relationId, param);
            }
            catch (final Exception e) {
                EscidocAbstractTest.failException("Submitting the revised, updated content relation "
                    + "by the depositor failed with exception. ", e);
            }

        }
        finally {
            PWCallback.resetHandle();
        }
    }

    /**
     *
     * @throws Exception
     */
    @Test
    public void testUpdateAfterReleaseItem() throws Exception {

        String param = getStatusTaskParam(getLastModificationDateValue2(getDocument(this.relationXml)), null);
        String xml = submit(this.relationId, param);
        param = getStatusTaskParam(getLastModificationDateValue2(getDocument(xml)), null);
        release(this.relationId, param);

        xml = retrieve(this.relationId);
        assertXmlExists("Properties status released", xml, XPATH_CONTENT_RELATION + "/properties/public-status"
            + "[text() = 'released']");
        Document relation = getDocument(xml);
        Node description = selectSingleNode(relation, "/content-relation/properties/description");
        String updatedDescription = "updated description";
        description.setTextContent(updatedDescription);
        String relationToUdate = toString(relation, false);
        try {
            update(this.relationId, relationToUdate);
            EscidocAbstractTest.failMissingException(InvalidStatusException.class);
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType("Update after release failed with unexpected exception. ",
                InvalidStatusException.class, e);
        }

    }
}
