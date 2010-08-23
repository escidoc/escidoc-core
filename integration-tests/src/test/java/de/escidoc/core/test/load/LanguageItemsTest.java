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
package de.escidoc.core.test.load;

import java.util.List;
import java.util.Vector;

import de.escidoc.core.test.EscidocRestSoapTestsBase;
import de.escidoc.core.test.load.statistics.Collector;

public class LanguageItemsTest extends LoadTestBase {

    private static int NO_OF_LANGUAGE_ITEMS = 2550;

    private static final int MIN_NO_OF_LANGUAGE_ITEMS = 0;

    private static final int MAX_NO_OF_LANGUAGE_ITEMS = 2560;

    private static final String EXPECTED_STATE = STATUS_RELEASED;

    private boolean clearLogFile = true;

    private int exceptionCount = 0;

    private List exceptions = null;

    /**
     * @param transport
     *            The transport identifier.
     */
    public LanguageItemsTest(final int transport) {
        super(transport);
    }

    /**
     * Set up test.
     * 
     * @throws Exception
     *             If anything fails.
     */
    public void setUp() throws Exception {

        super.setUp();
        initLogFile(clearLogFile);
        exceptionCount = 0;
        exceptions = new Vector();
        if (NO_OF_LANGUAGE_ITEMS < MIN_NO_OF_LANGUAGE_ITEMS) {
            NO_OF_LANGUAGE_ITEMS = MIN_NO_OF_LANGUAGE_ITEMS;
        }
        if (NO_OF_LANGUAGE_ITEMS > MAX_NO_OF_LANGUAGE_ITEMS) {
            NO_OF_LANGUAGE_ITEMS = MAX_NO_OF_LANGUAGE_ITEMS;
        }
    }

    /**
     * Clean up after test.
     * 
     * @throws Exception
     *             If anything fails.
     */
    public void tearDown() throws Exception {

        super.tearDown();
    }

    /**
     * Successful creation of a Context.
     * 
     * @throws Exception
     *             If anything fails.
     */
    public void testLanguageItems() throws Exception {
        log("Creation of " + NO_OF_LANGUAGE_ITEMS
            + " languange items started. " + CRLF);
        for (int i = 1; i <= NO_OF_LANGUAGE_ITEMS; ++i) {
            try {
                createLanguageItem(i, EXPECTED_STATE);
            }
            catch (Exception e) {
                logException(e);
                exceptionCount += 1;
                exceptions.add(e);
            }
        }
        log("Creation of " + NO_OF_LANGUAGE_ITEMS
            + " languange items  finished with " + exceptionCount
            + " exceptions. " + CRLF);
        log(Collector.getMessage());
        log(CRLF + " ==========================================" + CRLF);
    }

    private String createLanguageItem(final int no, final String state)
        throws Exception {

        String result = null;
        String message =
            " language item from template '" + getLanguageItemTemplateName(no)
                + "' in state '";
        log("Create" + message + state + "'");
        result =
            createItem(EscidocRestSoapTestsBase.getTemplateAsString(TEMPLATE_LANGUAGE_ITEMS_PATH,
                getLanguageItemTemplateName(no)));
        if (STATUS_SUBMITTED.equals(state)) {
            submitItem(result);
        }
        else if (STATUS_RELEASED.equals(state)) {
            submitItem(result);
            releaseItem(result);
        }
        else if (STATUS_WITHDRAWN.equals(state)) {
            submitItem(result);
            releaseItem(result);
            withdrawItem(result);
        }

        log("  ");
        return result;
    }

    private String retrieveItem(final String id) throws Exception {
        Collector.setStart("retrieve");
        String result = null;
        try {
            result = retrieve(id);
        }
        catch (Exception e) {
            throw e;
        }
        finally {
            Collector.setEnd("retrieve");
            log("  Retrieved item '" + id + "'");
        }
        return result;
    }

    private String createItem(final String xml) throws Exception {

        Collector.setStart("create");
        String result = null;
        try {
            result = create(xml);
        }
        catch (Exception e) {
            throw e;
        }
        finally {
            Collector.setEnd("create");
            result = getObjidValue(EscidocRestSoapTestsBase.getDocument(result));
            log("  Created   item '" + result + "'");
        }
        return result;
    }

    private void submitItem(final String id) throws Exception {

        String taskParam =
            getTaskParam(getLastModificationDateValue(EscidocRestSoapTestsBase
                .getDocument(retrieveItem(id))));
        Collector.setStart("submit");
        try {
            submit(id, taskParam);
        }
        catch (Exception e) {
            throw e;
        }
        finally {
            Collector.setEnd("submit");
            log("  Submitted item '" + id + "'");
        }
    }

    private void releaseItem(final String id) throws Exception {

        String taskParam =
            getTaskParam(getLastModificationDateValue(EscidocRestSoapTestsBase
                .getDocument(retrieveItem(id))));
        Collector.setStart("release");
        try {
            release(id, taskParam);
        }
        catch (Exception e) {
            throw e;
        }
        finally {
            Collector.setEnd("release");
            log("  Released  item '" + id + "'");
        }
    }

    private void withdrawItem(final String id) throws Exception {

        String taskParam =
            getTaskParam(getLastModificationDateValue(EscidocRestSoapTestsBase
                .getDocument(retrieveItem(id))));
        Collector.setStart("withdraw");
        try {
            withdraw(id, taskParam);
        }
        catch (Exception e) {
            throw e;
        }
        finally {
            Collector.setEnd("withdraw");
            log("  Withdrawed item '" + id + "'");
        }
    }

    private String getLanguageItemTemplateName(final int no) throws Exception {
        return "item_" + no + ".xml";
    }

}
