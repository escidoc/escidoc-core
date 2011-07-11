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
 * Copyright 2008 Fachinformationszentrum Karlsruhe Gesellschaft
 * fuer wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Foerderung der Wissenschaft e.V.  
 * All rights reserved.  Use is subject to license terms.
 */
package de.escidoc.core.test.migration;

import org.junit.Ignore;
import org.junit.Test;

import java.util.Collection;
import java.util.Map;
import java.util.Vector;

import static org.junit.Assert.assertEquals;

/**
 * @author Michael Schneider
 */
@Ignore("Klären was dieser Test macht. Test ist im Ant-Build nicht gelaufen")
// TODO: Klären was dieser Test macht. Test ist im Ant-Build nicht gelaufen.
public class MigrationIT extends MigrationTestBase {

    @Test
    public void testRetrieveObjects() throws Exception {

        Map<String, String> idsFromBuild159 = loadObjectIds();
        Collection<String> failing = new Vector<String>();
        Collection<String> failed = getValuesFromCsv(idsFromBuild159.get(MigrationTestBase.FAILING));

        Collection<String> containerIds =
            getValuesFromCsv(idsFromBuild159.get(MigrationTestBase.OBJECT_TYPE_FED_2_CONTAINER));
        Collection<String> failingContainerIds = checkContainers(containerIds, false);
        failing.addAll(failingContainerIds);
        containerIds.removeAll(failingContainerIds);
        idsFromBuild159.put(MigrationTestBase.OBJECT_TYPE_FED_2_CONTAINER, putValuesToCsv(containerIds));

        Collection<String> contextIds =
            getValuesFromCsv(idsFromBuild159.get(MigrationTestBase.OBJECT_TYPE_FED_2_CONTEXT));
        Collection<String> failingContextIds = checkContexts(contextIds, false);
        failing.addAll(failingContextIds);
        contextIds.removeAll(failingContextIds);
        idsFromBuild159.put(MigrationTestBase.OBJECT_TYPE_FED_2_CONTEXT, putValuesToCsv(contextIds));

        Collection<String> contentModelIds =
            getValuesFromCsv(idsFromBuild159.get(MigrationTestBase.OBJECT_TYPE_FED_2_CONTENT_MODEL));
        Collection<String> failingContentModelIds = checkContentModels(contentModelIds);
        failing.addAll(failingContentModelIds);
        // contentModelIds.removeAll(failingContentModelIds);
        idsFromBuild159.put(MigrationTestBase.OBJECT_TYPE_FED_2_CONTENT_MODEL, putValuesToCsv(contentModelIds));

        Collection<String> itemIds = getValuesFromCsv(idsFromBuild159.get(MigrationTestBase.OBJECT_TYPE_FED_2_ITEM));
        Collection<String> failingItemIds = checkItems(itemIds, false);
        failing.addAll(failingItemIds);
        itemIds.removeAll(failingItemIds);
        idsFromBuild159.put(MigrationTestBase.OBJECT_TYPE_FED_2_ITEM, putValuesToCsv(itemIds));

        Collection<String> organizationalUnitIds =
            getValuesFromCsv(idsFromBuild159.get(MigrationTestBase.OBJECT_TYPE_FED_2_ORGANIZATIONAL_UNIT));
        Collection<String> failingOrganizationalUnitIdsIds = checkOrganizationalUnits(organizationalUnitIds, false);
        failing.addAll(failingOrganizationalUnitIdsIds);
        organizationalUnitIds.removeAll(failingOrganizationalUnitIdsIds);
        idsFromBuild159.put(MigrationTestBase.OBJECT_TYPE_FED_2_ORGANIZATIONAL_UNIT,
            putValuesToCsv(organizationalUnitIds));

        Collection<String> roleIds = getValuesFromCsv(idsFromBuild159.get(MigrationTestBase.OBJECT_TYPE_ROLE));
        Collection<String> failingRoleIds = checkRoles(roleIds, false);
        failing.addAll(failingRoleIds);
        roleIds.removeAll(failingRoleIds);
        idsFromBuild159.put(MigrationTestBase.OBJECT_TYPE_ROLE, putValuesToCsv(roleIds));

        Collection<String> userAccountIds =
            getValuesFromCsv(idsFromBuild159.get(MigrationTestBase.OBJECT_TYPE_USER_ACCOUNT));
        Collection<String> failingUserAccountIds = checkUserAccounts(userAccountIds, false);
        failing.addAll(failingUserAccountIds);
        userAccountIds.removeAll(failingUserAccountIds);
        idsFromBuild159.put(MigrationTestBase.OBJECT_TYPE_USER_ACCOUNT, putValuesToCsv(userAccountIds));

        idsFromBuild159.put(MigrationTestBase.FAILING, putValuesToCsv(failing));
        saveCheckResult(idsFromBuild159, failed);
        assertEquals("[REST] Check of migrated Objects failed", 0, failing.size());

    }
}
