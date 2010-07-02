package de.escidoc.core.common.persistence.interfaces;

import de.escidoc.core.common.exceptions.system.SystemException;

/**
 * Data access object to fetch resource ids from a storage back end.
 * 
 * @author tte
 */
public interface ResourceIdentifierDao {

    /**
     * Get next object identifiers.
     * 
     * @param noOfPids
     *            The number of ids to retrieve. (non negative integer)
     * @return An array of the requested next available object id(s).
     * @throws SystemException
     *             Thrown in case of an internal system error.
     * @common
     */
    String[] getNextPids(final int noOfPids) throws SystemException;
}
