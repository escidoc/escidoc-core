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
package de.escidoc.core.test.common.client.servlet.om.interfaces;

/**
 * Interface for submit, release, withdraw methods.
 */
public interface SubmitReleaseReviseWithdrawClientInterface {

    /**
     * Release an Item.
     *
     * @param itemId The id of the item.
     * @param param  The timestamp of the last modification of the Item.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    Object release(final String itemId, final String param) throws Exception;

    /**
     * Release an Item with indirect PID assignment.
     *
     * @param itemId            The id of the item.
     * @param creatorUserHandle The user Handle of the creator to get authenticated access.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    Object releaseWithPid(final String itemId, final String creatorUserHandle) throws Exception;

    /**
     * Submit an Item.
     *
     * @param itemId The id of the item.
     * @param param  The timestamp of the last modification of the Item.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    Object submit(final String itemId, final String param) throws Exception;

    /**
     * Withdraw an Item.
     *
     * @param itemId The id of the item.
     * @param param  The timestamp of the last modification of the Item.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    Object withdraw(final String itemId, final String param) throws Exception;

}
