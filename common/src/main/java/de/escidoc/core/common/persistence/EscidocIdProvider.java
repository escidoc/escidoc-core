/*
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the Common Development and Distribution License, Version 1.0
 * only (the "License"). You may not use this file except in compliance with the License.
 *
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE or http://www.escidoc.de/license. See the License for
 * the specific language governing permissions and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each file and include the License file at
 * license/ESCIDOC.LICENSE. If applicable, add the following below this CDDL HEADER, with the fields enclosed by
 * brackets "[]" replaced with your own identifying information: Portions Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
 *
 * Copyright 2006-2011 Fachinformationszentrum Karlsruhe Gesellschaft fuer wissenschaftlich-technische Information mbH
 * and Max-Planck-Gesellschaft zur Foerderung der Wissenschaft e.V. All rights reserved. Use is subject to license
 * terms.
 */

package de.escidoc.core.common.persistence;

import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.persistence.interfaces.ResourceIdentifierDao;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

/**
 * Provider for new eSciDoc object ids.<br> This class prefetches a number of ids and provides ids on request. If no id
 * is available, a set of ids is fetched from a back end.<br> Currently, {@link ResourceIdentifierDao} is used to fetch
 * the ids from a fedora repository.
 *
 * @author Torsten Tetteroo
 */
public class EscidocIdProvider {

    private int numberPrefetchedIds;

    private ResourceIdentifierDao resourceIdentifierDao;

    /**
     * The {@link Iterator} holding the prefetched object ids.
     */
    private Iterator<String> storedIds = new ArrayList<String>(0).iterator();

    /**
     * Get next object identifier.
     *
     * @return Returns the requested next available object id.
     * @throws SystemException Thrown in case of an internal system error.
     */
    public String getNextPid() throws SystemException {

        return getNextPids(1)[0];
    }

    /**
     * Get next object identifiers.
     *
     * @param noOfPids The number of pids to retrieve. (non negative integer)
     * @return An array of the requested next available object id(s).
     * @throws SystemException Thrown in case of an internal system error.
     */
    public synchronized String[] getNextPids(final int noOfPids) throws SystemException {

        final String[] ret = new String[noOfPids];
        for (int index = 0; index < noOfPids; index++) {
            if (!storedIds.hasNext()) {
                fetchIds(noOfPids - index);
            }
            ret[index] = storedIds.next();

        }
        return ret;
    }

    /**
     * Fetches a number of id from the back end. The maximum number of the provided number of needed ids and the
     * configured number of ids to prefetch are fetched from the back end.
     *
     * @param numberNeededIds The number of ids that are at least needed to handle the current request for ids.
     * @throws SystemException Thrown if fetched identifier contain capital letters or in case of an internal system
     *                         error.
     */
    private void fetchIds(final int numberNeededIds) throws SystemException {

        final int number = Math.max(numberNeededIds, this.numberPrefetchedIds);
        final List<String> idArryList = resourceIdentifierDao.getNextPids(number);

        // check if prefixes have lower characters
        // I assume that its enough to check the first retrieved pid
        final String id = idArryList.get(0);

        if (!id.equals(id.toLowerCase(Locale.ENGLISH))) {
            throw new SystemException("Invalid identifier prefix configured in Fedora. "
                + "Capital letters are forbidden in eSciDoc as prefix.");
        }
        this.storedIds = idArryList.iterator();
    }

    /**
     * Injects the number of ids that shall be prefetched at one time.
     *
     * @param numberPrefetchedIds the number to inject.
     */
    public void setNumberPrefetchedIds(final int numberPrefetchedIds) {
        this.numberPrefetchedIds = numberPrefetchedIds;
    }

    /**
     * Injects the data access object used to retrieve resource identifiers.
     *
     * @param resourceIdentifierDao the resourceIdentifierDao to inject.
     */
    public void setResourceIdentifierDao(final ResourceIdentifierDao resourceIdentifierDao) {
        this.resourceIdentifierDao = resourceIdentifierDao;
    }

}
