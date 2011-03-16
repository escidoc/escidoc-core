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
package de.escidoc.core.test.om.item.rest;

import de.escidoc.core.common.exceptions.remote.application.security.AuthorizationException;
import de.escidoc.core.test.EscidocRestSoapTestBase;
import de.escidoc.core.test.common.client.servlet.Constants;
import de.escidoc.core.test.om.item.ItemTestBase;
import de.escidoc.core.test.security.client.PWCallback;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import static org.junit.Assert.fail;

/**
 * Item tests with REST transport.
 * 
 * @author MSC
 * 
 */
public class ItemLifecycleRestTest extends ItemTestBase {

    /**
     * Constructor.
     * 
     */
    public ItemLifecycleRestTest() {
        super(Constants.TRANSPORT_REST);
    }

    /**
     * Test declining retrieving of released item with component visibility
     * "private".
     * 
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testOMRContentVisibilityPrivate() throws Exception {
        PWCallback.setHandle(PWCallback.DEFAULT_HANDLE);
        Document item =
            EscidocRestSoapTestBase.getTemplateAsDocument(TEMPLATE_ITEM_PATH
                + "/" + getTransport(false), "escidoc_item_198_for_create.xml");
        Node itemChanged =
            substitute(item,
                "/item/components/component/properties/visibility", "private");
        String itemXml = toString(itemChanged, false);
        String cretaedItem = create(itemXml);
        Document itemDocument = getDocument(cretaedItem);
        String componentId = null;
        if (getTransport(true).equals("REST")) {
            componentId =
                selectSingleNode(
                    itemDocument,
                    "/item/components/component"
                        + "[properties/visibility='private']/@href")
                    .getNodeValue();
            componentId = getIdFromHrefValue(componentId);
        }
        else {
            componentId =
                selectSingleNode(
                    itemDocument,
                    "/item/components/component"
                        + "[properties/visibility='private']/@objid")
                    .getNodeValue();
        }
        String itemId = getObjidValue(cretaedItem);
        String param = getTheLastModificationParam(false, itemId);
        submit(itemId, param);
        String pidParam;
        if (getItemClient().getPidConfig(
            "cmm.Item.objectPid.setPidBeforeRelease", "true")
            && !getItemClient().getPidConfig(
                "cmm.Item.objectPid.releaseWithoutPid", "false")) {
            pidParam = getPidParam(itemId, "http://somewhere" + itemId);
            assignObjectPid(itemId, pidParam);
        }
        if (getItemClient().getPidConfig(
            "cmm.Item.versionPid.setPidBeforeRelease", "true")
            && !getItemClient().getPidConfig(
                "cmm.Item.versionPid.releaseWithoutPid", "false")) {
            String latestVersion = getLatestVersionObjidValue(cretaedItem);
            pidParam =
                getPidParam(latestVersion, "http://somewhere" + latestVersion);
            assignVersionPid(latestVersion, pidParam);
        }

        param = getTheLastModificationParam(false, itemId);

        release(itemId, param);

        // PWCallback.setHandle(PWCallback.DEFAULT_HANDLE);
        PWCallback.setHandle("");
        try {
            retrieveContent(itemId, componentId);
            fail("No AuthorizationException retrieving "
                + "item with component visibility 'private'.");
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.assertExceptionType(
                AuthorizationException.class, e);
        }

    }
}
