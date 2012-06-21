package de.escidoc.core.test.common.client.servlet.adm.interfaces;

/**
 * Interface for the Admin Handler Client.
 *
 * @author Steffen Wagner
 */
public interface AdminClientInterface {

    /**
     * Get Information about the repository.
     *
     * @param key property-key
     * @return information about the repository.
     * @throws Exception Thrown if retrieving failed.
     */
    Object getRepositoryInfo(final String key) throws Exception;
}
