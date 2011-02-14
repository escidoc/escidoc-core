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
import de.escidoc.core.test.EscidocRestSoapTestBase;
import de.escidoc.core.test.security.client.PWCallback;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Test lifecycle of Content Relation.
 * 
 * @author ROF
 * 
 */
@RunWith(value = Parameterized.class)
public class ContentRelationLifecycleTest extends ContentRelationTestBase {

    private String relationXml = null;

    private String relationId = null;

    /**
     * @param transport
     *            The transport identifier.
     */
    public ContentRelationLifecycleTest(final int transport) {
        super(transport);
    }

    /**
     * Set up servlet test.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Override
    @Before
    public void setUp() throws Exception {

        String contentRelationXml =
            getExampleTemplate("content-relation-01.xml");
        relationXml = create(contentRelationXml);
        relationId = getObjidValue(relationXml);
    }

    /**
     * Clean up after servlet test.
     * 
     * @throws Exception
     *             If anything fails.
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
     * @test.name Submit Content relation - Pending
     * @test.id OM_SI_1
     * @test.input <ul>
     *             <li>id of an existing cr in state pending</li>
     *             <li>timestamp of the last modification of the cr</li>
     *             </ul>
     * @test.expected: No result, no exception, content relation has been
     *                 submitted, Last modification date of content relation has
     *                 been updated.
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testOmSi1() throws Exception {

        String paramXml = getTheLastModificationParam(false);
        final Document paramDocument =
            EscidocRestSoapTestBase.getDocument(paramXml);
        final String pendingLastModificationDate =
            getLastModificationDateValue(paramDocument);

        try {
            submit(this.relationId, paramXml);
        }
        catch (Exception e) {
            EscidocRestSoapTestBase.failException(
                "Submitting the pending Content relation failed. ", e);
        }

        String submittedXml = null;
        try {
            submittedXml = retrieve(this.relationId);
        }
        catch (Exception e) {
            EscidocRestSoapTestBase.failException(
                "Retrieving the submitted content relation failed. ", e);
        }
        final Document submittedDocument =
            EscidocRestSoapTestBase.getDocument(submittedXml);
        // assertEquals(pendingLastModificationDate,
        // getLastModificationDateValue(submittedDocument));
        assertDateBeforeAfter(pendingLastModificationDate,
            getLastModificationDateValue(submittedDocument));
        assertXmlEquals("Unexpected status. ", submittedDocument,
            XPATH_CONTENT_RELATION + "/properties/public-status",
            STATE_SUBMITTED);

    }

    /**
     * Test calling release after submit and afterwards submit again.
     * 
     * @throws Exception
     *             Thrown if submit or release fail or not exception is thrown
     *             if the second submit is called.
     */
    @Test
    public void testSubmitAfterRelease() throws Exception {

        submit(this.relationId, getTheLastModificationParam(false));
        release(this.relationId, getTheLastModificationParam(false));

        try {
            submit(this.relationId, getTheLastModificationParam(false));
            fail("No exception on submit of a content relation in "
                + "a state 'released'");
        }
        catch (Exception e) {
            Class<?> ec = InvalidStatusException.class;
            EscidocRestSoapTestBase.assertExceptionType(ec.getName()
                + " expected.", ec, e);
        }
    }

    /**
     * Test successful submitting a content relation in state "in-revision".
     * 
     * @test.name Submit a content relation - In Revision
     * @test.id OM_SI_1-2
     * @test.input <ul>
     *             <li>id of an existing a content relation in state in-revision
     *             </li>
     *             <li>timestamp of the last modification of the a content
     *             relation</li>
     *             </ul>
     * @test.expected: No result, no exception, a content relation has been
     *                 submitted, Last modification date of a content relation
     *                 has been updated.
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testOmSi1_2() throws Exception {

        String paramXml = getTheLastModificationParam(false, this.relationId);
        submit(this.relationId, paramXml);
        paramXml = getTheLastModificationParam(false, this.relationId);
        revise(this.relationId, paramXml);
        paramXml = getTheLastModificationParam(false, this.relationId);

        final Document paramDocument =
            EscidocRestSoapTestBase.getDocument(paramXml);
        final String revisedLastModificationDate =
            getLastModificationDateValue(paramDocument);

        try {
            submit(this.relationId, paramXml);
        }
        catch (Exception e) {
            EscidocRestSoapTestBase.failException(
                "Submitting the revised content relation failed. ", e);
        }

        String submittedXml = null;
        try {
            submittedXml = retrieve(this.relationId);
        }
        catch (Exception e) {
            EscidocRestSoapTestBase.failException(
                "Retrieving the revised, submitted item failed. ", e);
        }
        final Document submittedDocument =
            EscidocRestSoapTestBase.getDocument(submittedXml);
        assertDateBeforeAfter(revisedLastModificationDate,
            getLastModificationDateValue(submittedDocument));
        assertXmlEquals("Unexpected status. ", submittedDocument,
            XPATH_CONTENT_RELATION + "/properties/public-status",
            STATE_SUBMITTED);

    }

    /**
     * Test handling of non-ASCII character within submit comment.
     * 
     * @throws Exception
     *             Thrown if escaping of non-ASCII character failed.
     */
    @Test
    public void testSubmitComment() throws Exception {

        String paramXml = getTheLastModificationParam(ENTITY_REFERENCES);

        submit(this.relationId, paramXml);
        String submittedXml = retrieve(this.relationId);
        String commentString = null;
        Matcher m =
            Pattern.compile("comment[^>]*>([^<]*)</").matcher(submittedXml);
        if (m.find()) {
            commentString = m.group(1);
        }
        assertEquals(ENTITY_REFERENCES, commentString);
    }

   @Test
   public void testReleaseBeforeSubmitContentRelation() throws Exception {

        String param = getTheLastModificationParam(false);

        try {
            release(this.relationId, param);
            fail("No exception occured on release befor submit.");
        }
        catch (Exception e) {
            Class<?> ec = InvalidStatusException.class;
            EscidocRestSoapTestBase.assertExceptionType(ec.getName()
                + " expected.", ec, e);
        }
    }

    /**
     * Test release a Content Relation.
     * 
     * @throws Exception
     *             Thrown if release behavior is not as expected.
     */
   @Test
    public void testReleaseContentRelation() throws Exception {

        String param = getTheLastModificationParam(false);
        submit(this.relationId, param);

        // validate escidoc XML
        String submitXml = retrieve(this.relationId);
        assertXmlValidContentRelation(submitXml);

        // check values
        assertXmlExists("Properties status not submitted", submitXml,
            XPATH_CONTENT_RELATION_STATUS + "[text() = 'submitted']");
        assertXmlNotNull("Status comment missing", getDocument(submitXml),
            XPATH_CONTENT_RELATION_STATUS_COMMENT);

        param = getTheLastModificationParam(false);
        release(this.relationId, param);

        // validate escidoc XML
        String releasedXml = retrieve(this.relationId);
        assertXmlValidContentRelation(releasedXml);

        // check values
        assertXmlExists("Properties status not released", releasedXml,
            XPATH_CONTENT_RELATION_STATUS + "[text() = 'released']");
        assertXmlNotNull("Status comment missing", getDocument(submitXml),
            XPATH_CONTENT_RELATION_STATUS_COMMENT);
    }

    /**
     * Test successful revising a content relation.
     * 
     * @test.name Revise content relation - Submitted
     * @test.id OM_RVI-1
     * @test.input <ul>
     *             <li>existing content relation id of an content relation in
     *             state submitted</li>
     *             <li>timestamp of the last modification of the item</li>
     *             </ul>
     * @test.expected: No result, no exception, content relation has been
     *                 revised, Last modification date of content relation has
     *                 been updated.
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
   @Test
    public void testOMRvi1() throws Exception {

        String paramXml = getTheLastModificationParam(false);
        submit(this.relationId, paramXml);
        paramXml = getTheLastModificationParam(false);
        final Document paramDocument =
            EscidocRestSoapTestBase.getDocument(paramXml);
        final String submittedLastModificationDate =
            getLastModificationDateValue(paramDocument);

        try {
            revise(this.relationId, paramXml);
        }
        catch (Exception e) {
            EscidocRestSoapTestBase.failException(
                "Revising the submitted item failed", e);
        }

        final String revisedXml = retrieve(this.relationId);
        final Document revisedDocument =
            EscidocRestSoapTestBase.getDocument(revisedXml);
        assertDateBeforeAfter(submittedLastModificationDate,
            getLastModificationDateValue(revisedDocument));
        assertXmlEquals("Unexpected status. ", revisedDocument,
            XPATH_CONTENT_RELATION + "/properties/public-status",
            STATE_IN_REVISION);

    }

    /**
     * Test declining revising a content relation in state pending.
     * 
     * @test.name Revise content relation - Pending
     * @test.id OM_RVI-2
     * @test.input <ul>
     *             <li>existing content relation id of an content relation in
     *             state pending</li>
     *             <li>timestamp of the last modification of the content
     *             relation</li>
     *             </ul>
     * @test.expected: InvalidStatusException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
   @Test
    public void testOMRvi2() throws Exception {

        String param = getTheLastModificationParam(false);

        try {
            revise(this.relationId, param);
            EscidocRestSoapTestBase
                .failMissingException(InvalidStatusException.class);
        }
        catch (Exception e) {
            EscidocRestSoapTestBase.assertExceptionType(
                "Revising failed with unexpected exception. ",
                InvalidStatusException.class, e);
        }
    }

    /**
     * Test declining revising a content relation in state released.
     * 
     * @test.name Revise content relation - Released
     * @test.id OM_RVI-3
     * @test.input <ul>
     *             <li>existing content relation id of an content relation in
     *             state released</li>
     *             <li>timestamp of the last modification of the content
     *             relation</li>
     *             </ul>
     * @test.expected: InvalidStatusException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
   @Test
    public void testOMRvi3() throws Exception {

        String param = getTheLastModificationParam(false);
        submit(this.relationId, param);

        param = getTheLastModificationParam(false);
        release(this.relationId, param);
        param = getTheLastModificationParam(false);

        try {
            revise(this.relationId, param);
            EscidocRestSoapTestBase
                .failMissingException(InvalidStatusException.class);
        }
        catch (Exception e) {
            EscidocRestSoapTestBase.assertExceptionType(
                "Revising failed with unexpected exception. ",
                InvalidStatusException.class, e);
        }
    }

    /**
     * Test declining revising an unknown content relation.
     * 
     * @test.name Revise Item - Unknown content relation
     * @test.id OM_RVI-5
     * @test.input <ul>
     *             <li>id for that no content relation exists</li>
     *             <li>timestamp of the last modification of the content
     *             relation</li>
     *             </ul>
     * @test.expected: ContentRelationNotFoundException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
   @Test
    public void testOMRvi5() throws Exception {

        String param = getTheLastModificationParam(false);

        try {
            revise(UNKNOWN_ID, param);
            EscidocRestSoapTestBase
                .failMissingException(ContentRelationNotFoundException.class);
        }
        catch (Exception e) {
            EscidocRestSoapTestBase.assertExceptionType(
                "Revising unknown content relation failed "
                    + "with unexpected exception. ",
                ContentRelationNotFoundException.class, e);
        }
    }

    /**
     * Test declining revising an content relation without providing an item id.
     * 
     * @test.name Revise Item - Missing content relation id
     * @test.id OM_RVI-6
     * @test.input <ul>
     *             <li>no content relation id is provided</li>
     *             <li>timestamp of the last modification of the content
     *             relation</li>
     *             </ul>
     * @test.expected: MissingMethodParameterException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
   @Test
    public void testOMRvi6() throws Exception {

        String param = getTheLastModificationParam(false);

        try {
            revise(null, param);
            EscidocRestSoapTestBase
                .failMissingException(MissingMethodParameterException.class);
        }
        catch (Exception e) {
            EscidocRestSoapTestBase.assertExceptionType(
                "Revising without id failed with unexpected exception. ",
                MissingMethodParameterException.class, e);
        }
    }

    /**
     * Test declining revising an content relation without providing task param.
     * 
     * @test.name Revise content relation - Missing task param
     * @test.id OM_RVI-7
     * @test.input <ul>
     *             <li>existing content relation id of an content relation in
     *             state submitted</li>
     *             <li>No task param is provided</li>
     *             </ul>
     * @test.expected: MissingMethodParameterException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
   @Test
    public void testOMRvi7() throws Exception {

        String param = getTheLastModificationParam(false);
        submit(this.relationId, param);
        param = getTheLastModificationParam(false);

        try {
            revise(this.relationId, null);
            EscidocRestSoapTestBase
                .failMissingException(MissingMethodParameterException.class);
        }
        catch (Exception e) {
            EscidocRestSoapTestBase.assertExceptionType(
                "Revising without id failed with unexpected exception. ",
                MissingMethodParameterException.class, e);
        }
    }

    /**
     * Test declining revising a content relation without providing last
     * modification date in task param.
     * 
     * @test.name Revise Item - Missing last modification date
     * @test.id OM_RVI-8
     * @test.input <ul>
     *             <li>existing content relation id of an content relation in
     *             state submitted</li>
     *             <li>No last modification date is provided in task param</li>
     *             </ul>
     * @test.expected: MissingAttributeValueException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
   @Test
    public void testOMRvi8() throws Exception {

        String param = getTheLastModificationParam(false);
        submit(this.relationId, param);
        param = "<param />";

        try {
            revise(this.relationId, null);
            EscidocRestSoapTestBase
                .failMissingException(MissingMethodParameterException.class);
        }
        catch (Exception e) {
            EscidocRestSoapTestBase.assertExceptionType(
                "Revising without last modification date failed "
                    + "with unexpected exception. ",
                MissingMethodParameterException.class, e);
        }
    }

    /**
     * Test declining revising an content relation with corrupted task param.
     * 
     * @test.name Revise content relation - Corrupted task param
     * @test.id OM_RVI-9
     * @test.input <ul>
     *             <li>existing content relation id of an content relation in
     *             state submitted</li>
     *             <li>Corrupted task param is provided</li>
     *             </ul>
     * @test.expected: XmlCorruptedException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
   @Test
    public void testOMRvi9() throws Exception {

        String param = getTheLastModificationParam(false);
        submit(this.relationId, param);
        param = "<param";

        try {
            revise(this.relationId, param);
            EscidocRestSoapTestBase
                .failMissingException(XmlCorruptedException.class);
        }
        catch (Exception e) {
            EscidocRestSoapTestBase.assertExceptionType(
                "Revising without last modification date failed "
                    + "with unexpected exception. ",
                XmlCorruptedException.class, e);
        }
    }

    /**
     * Test declining revising an content relation with providing outdated last
     * modification date in task param.
     * 
     * @test.name Revise content relation - Corrupted task param
     * @test.id OM_RVI-10
     * @test.input <ul>
     *             <li>existing content relation id of an content relation in
     *             state submitted</li>
     *             <li>task param is provided that contains an outdated last
     *             modificaton date</li>
     *             </ul>
     * @test.expected: XmlCorruptedException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
   @Test
    public void testOMRvi10() throws Exception {

        String param = getTheLastModificationParam(false);
        submit(this.relationId, param);

        try {
            revise(this.relationId, param);
            EscidocRestSoapTestBase
                .failMissingException(OptimisticLockingException.class);
        }
        catch (Exception e) {
            EscidocRestSoapTestBase.assertExceptionType(
                "Revising with outdated last modification "
                    + "date failed with unexpected exception. ",
                OptimisticLockingException.class, e);
        }
    }

    /**
     * Test successful revising an content relation (created by a user with
     * createContentRelation permission) by an administrator.
     * 
     * @test.name Revise content relation - Administrator
     * @test.id OM_RVI-11
     * @test.input <ul>
     *             <li>existing content relation id of an content relation in
     *             state submitted</li>
     *             <li>timestamp of the last modification of the content
     *             relation</li>
     *             <li>revise method executed by an administrator</li>
     *             </ul>
     * @test.expected: No result, no exception, content relation has been
     *                 revised and is retrievable, updateable and submittable by
     *                 the depositor that had created the content relation.
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
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
            String contentRelationXml =
                getExampleTemplate("content-relation-01.xml");
            relationXml = create(contentRelationXml);
            relationId = getObjidValue(relationXml);
            String param = getTheLastModificationParam(false);
            submit(relationId, param);
            param = getTheLastModificationParam(false);

            // revise the content relation by an administrator
            PWCallback.setHandle(PWCallback.DEFAULT_HANDLE);

            try {
                revise(relationId, param);
            }
            catch (Exception e) {
                EscidocRestSoapTestBase.failException(
                    "Revising the submitted content relation failed", e);
            }

            // retrieve, update and submit the item by the depositor
            PWCallback.setHandle(PWCallback.DEPOSITOR_HANDLE);
            try {
                this.relationXml = retrieve(this.relationId);
            }
            catch (Exception e) {
                EscidocRestSoapTestBase.failException(
                    "Retrieving the revised content relation by the "
                        + "depositor failed with exception. ", e);
            }
            this.relationXml.replaceFirst("", "");
            try {
                this.relationXml = update(this.relationId, this.relationXml);
            }
            catch (Exception e) {
                EscidocRestSoapTestBase.failException(
                    "Updating the revised content relation by the "
                        + "depositor failed with exception. ", e);
            }
            param = getTheLastModificationParam(false);
            try {
                submit(this.relationId, param);
            }
            catch (Exception e) {
                EscidocRestSoapTestBase.failException(
                    "Submitting the revised, updated content relation "
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
        String param = getTheLastModificationParam(false);
        submit(this.relationId, param);
        param = getTheLastModificationParam(false);
        release(this.relationId, param);

        String xml = retrieve(this.relationId);
        assertXmlExists("Properties status released", xml,
            XPATH_CONTENT_RELATION + "/properties/public-status"
                + "[text() = 'released']");
        Document relation = getDocument(xml);
        Node description =
            selectSingleNode(relation,
                "/content-relation/properties/description");
        String updatedDescription = "updated description";
        description.setTextContent(updatedDescription);
        String relationToUdate = toString(relation, false);
        try {
            update(this.relationId, relationToUdate);
            EscidocRestSoapTestBase
                .failMissingException(InvalidStatusException.class);
        }
        catch (Exception e) {
            EscidocRestSoapTestBase.assertExceptionType(
                "Update after release failed with unexpected exception. ",
                InvalidStatusException.class, e);
        }

    }

    /**
     * 
     * @param includeWithdrawComment
     * @return
     * @throws Exception
     */
    private String getTheLastModificationParam(boolean includeWithdrawComment)
        throws Exception {
        return getTheLastModificationParam(includeWithdrawComment,
            this.relationId);
    }

    /**
     * 
     * @param comment
     * @return
     * @throws Exception
     */
    private String getTheLastModificationParam(final String comment)
        throws Exception {
        return getTheLastModificationParam(true, this.relationId, comment);
    }

}
