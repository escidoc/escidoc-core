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
 * Copyright 2006-2009 Fachinformationszentrum Karlsruhe Gesellschaft
 * fuer wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Foerderung der Wissenschaft e.V.
 * All rights reserved.  Use is subject to license terms.
 */
package de.escidoc.core.adm.service;

import de.escidoc.core.adm.service.interfaces.AdminHandlerInterface;
import de.escidoc.core.common.exceptions.application.invalid.InvalidSearchQueryException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidXmlException;
import de.escidoc.core.common.exceptions.application.invalid.XmlCorruptedException;
import de.escidoc.core.common.exceptions.application.security.AuthenticationException;
import de.escidoc.core.common.exceptions.application.security.AuthorizationException;
import de.escidoc.core.common.exceptions.system.EncodingSystemException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.exceptions.system.TripleStoreSystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * Administration tool that rebuilds the search index, rebuilds the resource cache and deletes objects physically from
 * the repository.
 *
 * @author André Schenk
 */
@Service("service.AdminHandler")
public class AdminHandler implements AdminHandlerInterface {

    @Autowired
    @Qualifier("business.AdminHandler")
    private de.escidoc.core.adm.business.admin.AdminHandler business;

    /**
     * Protected constructor to prevent instantiation outside of the Spring-context.
     */
    protected AdminHandler() {
    }

    /**
     * Delete a list of objects given by their object id's from Fedora. In case of items this method will also delete
     * all depending components of the given items. The deletion runs synchronously and returns some useful information
     * for the user, e.g. the total number of objects deleted.
     *
     * @param taskParam list of object id's to be deleted boolean value to signal if the search index and the resource
     *                  cache have to be kept in sync. If this value is set to false then the re-indexing and re-caching
     *                  should be run manually afterwards.
     * @return total number of objects deleted, ...
     * @throws InvalidXmlException     thrown if the taskParam has an invalid structure
     * @throws SystemException         thrown in case of an internal error
     * @throws AuthenticationException Thrown if the authentication fails due to an invalid provided eSciDoc user
     *                                 handle.
     * @throws AuthorizationException  Thrown if authorization fails.
     */
    @Override
    public String deleteObjects(final String taskParam) throws SystemException, AuthenticationException,
        AuthorizationException, XmlCorruptedException {
        return business.deleteObjects(taskParam);
    }

    /**
     * Get the current status of the running/finished purging process.
     *
     * @return current status (how many objects are still in the queue)
     * @throws SystemException         thrown in case of an internal error
     * @throws AuthenticationException Thrown if the authentication fails due to an invalid provided eSciDoc user
     *                                 handle.
     * @throws AuthorizationException  Thrown if authorization fails.
     */
    @Override
    public String getPurgeStatus() throws SystemException, AuthenticationException, AuthorizationException {
        return business.getPurgeStatus();
    }

    /**
     * Get the current status of the running/finished reindexing process.
     *
     * @return current status (how many objects are still in the queue)
     * @throws SystemException         thrown in case of an internal error
     * @throws AuthenticationException Thrown if the authentication fails due to an invalid provided eSciDoc user
     *                                 handle.
     * @throws AuthorizationException  Thrown if authorization fails.
     */
    @Override
    public String getReindexStatus() throws SystemException, AuthenticationException, AuthorizationException {
        return business.getReindexStatus();
    }

    /**
     * decrease the type of the current status of the running reindexing process by 1.
     *
     * @param objectTypeXml object-type to decrease
     * @throws InvalidXmlException     thrown if given xml is invalid
     * @throws SystemException         thrown in case of an internal error
     * @throws AuthenticationException Thrown if the authentication fails due to an invalid provided eSciDoc user
     *                                 handle.
     * @throws AuthorizationException  Thrown if authorization fails.
     */
    @Override
    public void decreaseReindexStatus(final String objectTypeXml) throws InvalidXmlException, SystemException,
        AuthenticationException, AuthorizationException {
        business.decreaseReindexStatus(objectTypeXml);
    }

    /**
     * Reinitialize the search index. The initialization runs synchronously and returns some useful information for the
     * user, e.g. the total number of objects found.
     *
     * @param clearIndex      clear the index before adding objects to it
     * @param indexNamePrefix name of the index (may be null for "all indexes")
     * @return total number of objects found, ...
     * @throws SystemException             Thrown if a framework internal error occurs.
     * @throws InvalidSearchQueryException thrown if a given search query could not be translated into a SQL query
     * @throws AuthenticationException     Thrown if the authentication fails due to an invalid provided eSciDoc user
     *                                     handle.
     * @throws AuthorizationException      Thrown if authorization fails.
     */
    @Override
    public String reindex(final String clearIndex, final String indexNamePrefix) throws SystemException,
        InvalidSearchQueryException, AuthenticationException, AuthorizationException {
        return business.reindex(Boolean.valueOf(clearIndex), indexNamePrefix);
    }

    /**
     * Ingest the AdminHandler business object.
     *
     * @param business AdminHandler business object to be ingested
     */
    public void setBusiness(final de.escidoc.core.adm.business.admin.AdminHandler business) {
        this.business = business;
    }

    /**
     * Provides a xml structure containing the index-configuration.
     *
     * @return xml structure with index configuration
     * @throws AuthenticationException Thrown if the authentication fails due to an invalid provided eSciDoc user
     *                                 handle.
     * @throws AuthorizationException  Thrown if authorization fails.
     */
    @Override
    public String getIndexConfiguration() throws AuthenticationException, AuthorizationException,
        TripleStoreSystemException, EncodingSystemException, WebserverSystemException {
        return this.business.getIndexConfiguration();
    }

    /**
     * Provides a xml structure containing public configuration properties of escidoc-core framework and the earliest
     * creation date of Escidoc repository objects.
     *
     * @return xml structure with escidoc configuration properties
     * @throws AuthenticationException Thrown if the authentication fails due to an invalid provided eSciDoc user
     *                                 handle.
     * @throws AuthorizationException  Thrown if authorization fails.
     */
    @Override
    public String getRepositoryInfo() throws AuthenticationException, AuthorizationException,
        TripleStoreSystemException, EncodingSystemException, WebserverSystemException {
        return this.business.getRepositoryInfo();
    }

    /**
     * Loads an set of examples objects into the framework.
     *
     * @param type Specifies the type of example set which is to load.
     * @return some useful information
     * @throws SystemException             Thrown if a framework internal error occurs.
     * @throws InvalidSearchQueryException thrown if a given search query could not be translated into a SQL query
     * @throws AuthenticationException     Thrown if the authentication fails due to an invalid provided eSciDoc user
     *                                     handle.
     * @throws AuthorizationException      Thrown if authorization fails.
     */
    @Override
    public String loadExamples(final String type) throws InvalidSearchQueryException, SystemException,
        AuthenticationException, AuthorizationException {
        return this.business.loadExamples(type);
    }
}
