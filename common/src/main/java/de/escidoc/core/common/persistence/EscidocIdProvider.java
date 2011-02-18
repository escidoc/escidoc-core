package de.escidoc.core.common.persistence;

import de.escidoc.core.common.business.fedora.FedoraResourceIdentifierDao;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.persistence.interfaces.ResourceIdentifierDao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Provider for new eSciDoc object ids.<br>
 * This class prefetches a number of ids and provides ids on request. If no id
 * is available, a set of ids is fetched from a back end.<br>
 * Currently, {@link FedoraResourceIdentifierDao} is used to fetch the ids from
 * a fedora repository.
 * 
 * @spring.bean id="escidoc.core.business.EscidocIdProvider"
 * @author tte
 */
public class EscidocIdProvider {

    public static final String SPRING_BEAN_ID =
        "escidoc.core.business.EscidocIdProvider";

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
     * @throws SystemException
     *             Thrown in case of an internal system error.
     * @common
     */
    public final String getNextPid() throws SystemException {

        return getNextPids(1)[0];
    }

    /**
     * Get next object identifiers.
     * 
     * @param noOfPids
     *            The number of pids to retrieve. (non negative integer)
     * @return An array of the requested next available object id(s).
     * @throws SystemException
     *             Thrown in case of an internal system error.
     * @common
     */
    final synchronized String[] getNextPids(final int noOfPids)
        throws SystemException {

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
     * Fetches a number of id from the back end. The maximum number of the
     * provided number of needed ids and the configured number of ids to
     * prefetch are fetched from the back end.
     * 
     * @param numberNeededIds
     *            The number of ids that are at least needed to handle the
     *            current request for ids.
     * @throws SystemException
     *             Thrown if fetched identifier contain capital letters or in
     *             case of an internal system error.
     * @common
     */
    private void fetchIds(final int numberNeededIds) throws SystemException {

        int number = Math.max(numberNeededIds, numberPrefetchedIds);
        List<String> idArryList =
            Arrays.asList(resourceIdentifierDao.getNextPids(number));

        // check if prefixes have lower characters
        // I assume that its enough to check the first retrieved pid
        String id = idArryList.get(0);

        if (!id.equals(id.toLowerCase())) {
            throw new SystemException(
                "Invalid identifier prefix configured in Fedora. "
                    + "Capital letters are forbidden in eSciDoc as prefix.");
        }
        this.storedIds = idArryList.iterator();
    }

    /**
     * Injects the number of ids that shall be prefetched at one time.
     * 
     * @param numberPrefetchedIds
     *            the number to inject.
     * 
     * @spring.property value="${escidoc-core.identifier.numberPrefetched}"
     * @common
     */
    public void setNumberPrefetchedIds(final int numberPrefetchedIds) {
        this.numberPrefetchedIds = numberPrefetchedIds;
    }

    /**
     * Injects the data access object used to retrieve resource identifiers.
     * 
     * @param resourceIdentifierDao
     *            the resourceIdentifierDao to inject.
     * 
     * @spring.property ref="escidoc.core.business.FedoraResourceIdentifierDao"
     * @common
     */
    public void setResourceIdentifierDao(
        final ResourceIdentifierDao resourceIdentifierDao) {
        this.resourceIdentifierDao = resourceIdentifierDao;
    }

}
