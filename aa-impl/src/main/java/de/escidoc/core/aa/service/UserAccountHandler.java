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
package de.escidoc.core.aa.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import de.escidoc.core.aa.service.interfaces.UserAccountHandlerInterface;
import de.escidoc.core.common.exceptions.application.invalid.InvalidScopeException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidSearchQueryException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidStatusException;
import de.escidoc.core.common.exceptions.application.invalid.XmlCorruptedException;
import de.escidoc.core.common.exceptions.application.invalid.XmlSchemaValidationException;
import de.escidoc.core.common.exceptions.application.missing.MissingAttributeValueException;
import de.escidoc.core.common.exceptions.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.application.notfound.GrantNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.OrganizationalUnitNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.PreferenceNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.RoleNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.UserAccountNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.UserAttributeNotFoundException;
import de.escidoc.core.common.exceptions.application.security.AuthenticationException;
import de.escidoc.core.common.exceptions.application.security.AuthorizationException;
import de.escidoc.core.common.exceptions.application.violated.AlreadyActiveException;
import de.escidoc.core.common.exceptions.application.violated.AlreadyDeactiveException;
import de.escidoc.core.common.exceptions.application.violated.AlreadyExistsException;
import de.escidoc.core.common.exceptions.application.violated.AlreadyRevokedException;
import de.escidoc.core.common.exceptions.application.violated.OptimisticLockingException;
import de.escidoc.core.common.exceptions.application.violated.ReadonlyElementViolationException;
import de.escidoc.core.common.exceptions.application.violated.UniqueConstraintViolationException;
import de.escidoc.core.common.exceptions.system.SystemException;

/**
 * User account handler implementation for the service layer of the AA component.
 *
 * @author Michael Schneider
 */
@Service("service.UserAccountHandler")
public class UserAccountHandler implements UserAccountHandlerInterface {

    @Autowired
    @Qualifier("business.UserAccountHandler")
    private de.escidoc.core.aa.business.interfaces.UserAccountHandlerInterface business;

    /**
     * See Interface for functional description.
     *
     * @param user userId
     * @return created userAccount as xml
     * @throws UniqueConstraintViolationException
     *                                 e
     * @throws InvalidStatusException  e
     * @throws OrganizationalUnitNotFoundException
     *                                 e
     * @throws MissingMethodParameterException
     *                                 e
     * @throws AuthenticationException e
     * @throws AuthorizationException  e
     * @throws SystemException         e
     * @see UserAccountHandlerInterface #create(java.lang.String)
     */
    @Override
    public String create(final String user) throws UniqueConstraintViolationException, XmlCorruptedException,
        XmlSchemaValidationException, OrganizationalUnitNotFoundException, MissingMethodParameterException,
        AuthenticationException, AuthorizationException, SystemException, InvalidStatusException {

        return business.create(user);
    }

    /**
     * See Interface for functional description.
     *
     * @param userId userId
     * @throws UserAccountNotFoundException e
     * @throws MissingMethodParameterException
     *                                      e
     * @throws AuthenticationException      e
     * @throws AuthorizationException       e
     * @throws SystemException              e
     * @see UserAccountHandlerInterface #delete(java.lang.String)
     */
    @Override
    public void delete(final String userId) throws UserAccountNotFoundException, MissingMethodParameterException,
        AuthenticationException, AuthorizationException, SystemException {

        business.delete(userId);
    }

    /**
     * See Interface for functional description.
     *
     * @param userId userId
     * @param user   updated user-xml
     * @return updated user-xml
     * @throws UserAccountNotFoundException   e
     * @throws UniqueConstraintViolationException
     *                                        e
     * @throws XmlCorruptedException          e
     * @throws XmlSchemaValidationException   , e
     * @throws InvalidStatusException         e
     * @throws MissingMethodParameterException
     *                                        e
     * @throws MissingAttributeValueException e
     * @throws OptimisticLockingException     e
     * @throws AuthenticationException        e
     * @throws AuthorizationException         e
     * @throws OrganizationalUnitNotFoundException
     *                                        e
     * @throws SystemException                e
     * @see UserAccountHandlerInterface #update(java.lang.String, java.lang.String)
     */
    @Override
    public String update(final String userId, final String user) throws UserAccountNotFoundException,
        UniqueConstraintViolationException, XmlCorruptedException, XmlSchemaValidationException,
        MissingMethodParameterException, MissingAttributeValueException, OptimisticLockingException,
        AuthenticationException, AuthorizationException, OrganizationalUnitNotFoundException, SystemException,
        InvalidStatusException {

        return business.update(userId, user);
    }

    /**
     * See Interface for functional description.
     *
     * @param userId    userId
     * @param taskParam taskParam
     * @throws UserAccountNotFoundException e
     * @throws InvalidStatusException       e
     * @throws XmlCorruptedException        e
     * @throws MissingMethodParameterException
     *                                      e
     * @throws OptimisticLockingException   e
     * @throws AuthenticationException      e
     * @throws AuthorizationException       e
     * @throws SystemException              e
     */
    @Override
    public void updatePassword(final String userId, final String taskParam) throws UserAccountNotFoundException,
        InvalidStatusException, XmlCorruptedException, MissingMethodParameterException, OptimisticLockingException,
        AuthenticationException, AuthorizationException, SystemException {

        business.updatePassword(userId, taskParam);
    }

    /**
     * See Interface for functional description.
     *
     * @param userId userId
     * @return user as xml
     * @throws UserAccountNotFoundException e
     * @throws MissingMethodParameterException
     *                                      e
     * @throws AuthenticationException      e
     * @throws AuthorizationException       e
     * @throws SystemException              e
     * @see UserAccountHandlerInterface #retrieve(java.lang.String)
     */
    @Override
    public String retrieve(final String userId) throws UserAccountNotFoundException, MissingMethodParameterException,
        AuthenticationException, AuthorizationException, SystemException {

        return business.retrieve(userId);
    }

    /**
     * See Interface for functional description.
     *
     * @return user as xml
     * @throws UserAccountNotFoundException e
     * @throws AuthenticationException      e
     * @throws AuthorizationException       e
     * @throws SystemException              e
     * @see UserAccountHandlerInterface #retrieveCurrentUser(java.lang.String)
     */
    @Override
    public String retrieveCurrentUser() throws UserAccountNotFoundException, AuthenticationException,
        AuthorizationException, SystemException {

        return business.retrieveCurrentUser();
    }

    @Override
    public String retrieveResources(final String userId) throws UserAccountNotFoundException,
        MissingMethodParameterException, AuthenticationException, AuthorizationException, SystemException {

        return business.retrieveResources(userId);
    }

    @Override
    public String retrieveCurrentGrants(final String userId) throws UserAccountNotFoundException,
        MissingMethodParameterException, AuthenticationException, AuthorizationException, SystemException {

        return business.retrieveCurrentGrants(userId);
    }

    /**
     * See Interface for functional description.
     *
     * @param userId  userId
     * @param grantId grantId
     * @return grant as xml
     * @throws UserAccountNotFoundException e
     * @throws GrantNotFoundException       e
     * @throws MissingMethodParameterException
     *                                      e
     * @throws AuthenticationException      e
     * @throws AuthorizationException       e
     * @throws SystemException              e
     * @see UserAccountHandlerInterface #retrieveGrant(java.lang.String, java.lang.String)
     */
    @Override
    public String retrieveGrant(final String userId, final String grantId) throws UserAccountNotFoundException,
        GrantNotFoundException, MissingMethodParameterException, AuthenticationException, AuthorizationException,
        SystemException {

        return business.retrieveGrant(userId, grantId);
    }

    /**
     * See Interface for functional description.
     *
     * @param filter filter as CQL query
     * @return String xml
     * @throws MissingMethodParameterException
     *                                     e
     * @throws InvalidSearchQueryException e
     * @throws AuthenticationException     e
     * @throws AuthorizationException      e
     * @throws SystemException             e
     * @see UserAccountHandlerInterface #retrieveGrants(java.lang.String)
     */
    @Override
    public String retrieveGrants(final Map<String, String[]> filter) throws MissingMethodParameterException,
        InvalidSearchQueryException, AuthenticationException, AuthorizationException, SystemException {

        return business.retrieveGrants(filter);
    }

    /**
     * See Interface for functional description.
     *
     * @param userId    userId
     * @param taskParam taskParam
     * @throws AlreadyActiveException         e
     * @throws UserAccountNotFoundException   e
     * @throws XmlCorruptedException          e
     * @throws MissingMethodParameterException
     *                                        e
     * @throws MissingAttributeValueException e
     * @throws OptimisticLockingException     e
     * @throws AuthenticationException        e
     * @throws AuthorizationException         e
     * @throws SystemException                e
     * @see UserAccountHandlerInterface #activate(java.lang.String, java.lang.String)
     */
    @Override
    public void activate(final String userId, final String taskParam) throws AlreadyActiveException,
        UserAccountNotFoundException, XmlCorruptedException, MissingMethodParameterException,
        MissingAttributeValueException, OptimisticLockingException, AuthenticationException, AuthorizationException,
        SystemException {

        business.activate(userId, taskParam);
    }

    /**
     * See Interface for functional description.
     *
     * @param userId    userId
     * @param taskParam taskParam
     * @throws AlreadyDeactiveException       e
     * @throws UserAccountNotFoundException   e
     * @throws XmlCorruptedException          e
     * @throws MissingMethodParameterException
     *                                        e
     * @throws MissingAttributeValueException e
     * @throws OptimisticLockingException     e
     * @throws AuthenticationException        e
     * @throws AuthorizationException         e
     * @throws SystemException                e
     * @see UserAccountHandlerInterface #deactivate(java.lang.String, java.lang.String)
     */
    @Override
    public void deactivate(final String userId, final String taskParam) throws AlreadyDeactiveException,
        UserAccountNotFoundException, XmlCorruptedException, MissingMethodParameterException,
        MissingAttributeValueException, OptimisticLockingException, AuthenticationException, AuthorizationException,
        SystemException {

        business.deactivate(userId, taskParam);
    }

    /**
     * See Interface for functional description.
     *
     * @param userId   userId
     * @param grantXML grant as xml
     * @return created grant as xml
     * @throws AlreadyExistsException       e
     * @throws UserAccountNotFoundException e
     * @throws InvalidScopeException        e
     * @throws RoleNotFoundException        e
     * @throws XmlCorruptedException        e
     * @throws XmlSchemaValidationException e
     * @throws MissingMethodParameterException
     *                                      e
     * @throws AuthenticationException      e
     * @throws AuthorizationException       e
     * @throws SystemException              e
     * @see UserAccountHandlerInterface #createGrant(java.lang.String, java.lang.String)
     */
    @Override
    public String createGrant(final String userId, final String grantXML) throws AlreadyExistsException,
        UserAccountNotFoundException, InvalidScopeException, RoleNotFoundException, XmlCorruptedException,
        XmlSchemaValidationException, MissingMethodParameterException, AuthenticationException, AuthorizationException,
        SystemException {

        return business.createGrant(userId, grantXML);
    }

    /**
     * See Interface for functional description.
     *
     * @param userId    userId
     * @param grantId   grantId
     * @param taskParam taskParam
     * @throws UserAccountNotFoundException   e
     * @throws GrantNotFoundException         e
     * @throws AlreadyRevokedException        e
     * @throws XmlCorruptedException          e
     * @throws MissingAttributeValueException e
     * @throws MissingMethodParameterException
     *                                        e
     * @throws AuthenticationException        e
     * @throws AuthorizationException         e
     * @throws SystemException                e
     * @see UserAccountHandlerInterface #revokeGrant(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public void revokeGrant(final String userId, final String grantId, final String taskParam)
        throws UserAccountNotFoundException, GrantNotFoundException, AlreadyRevokedException, XmlCorruptedException,
        MissingAttributeValueException, MissingMethodParameterException, AuthenticationException,
        AuthorizationException, SystemException {

        business.revokeGrant(userId, grantId, taskParam);
    }

    /**
     * See Interface for functional description.
     *
     * @param userId    userId
     * @param taskParam taskParam
     * @throws UserAccountNotFoundException   e
     * @throws GrantNotFoundException         e
     * @throws AlreadyRevokedException        e
     * @throws XmlCorruptedException          e
     * @throws MissingAttributeValueException e
     * @throws MissingMethodParameterException
     *                                        e
     * @throws AuthenticationException        e
     * @throws AuthorizationException         e
     * @throws SystemException                e
     * @see UserAccountHandlerInterface #revokeGrants(java.lang.String, java.lang.String)
     */
    @Override
    public void revokeGrants(final String userId, final String taskParam) throws UserAccountNotFoundException,
        GrantNotFoundException, AlreadyRevokedException, XmlCorruptedException, MissingAttributeValueException,
        MissingMethodParameterException, AuthenticationException, AuthorizationException, SystemException {

        business.revokeGrants(userId, taskParam);
    }

    /**
     * See Interface for functional description.
     *
     * @param filter filterXml
     * @return filtered user-accounts as xml
     * @throws MissingMethodParameterException
     *                                     e
     * @throws InvalidSearchQueryException e
     * @throws AuthenticationException     e
     * @throws AuthorizationException      e
     * @throws SystemException             e
     * @see UserAccountHandlerInterface #retrieveUserAccounts(java.util.Map)
     */
    @Override
    public String retrieveUserAccounts(final Map<String, String[]> filter) throws MissingMethodParameterException,
        AuthenticationException, AuthorizationException, SystemException, InvalidSearchQueryException {

        return business.retrieveUserAccounts(filter);
    }

    @Override
    public UserDetails retrieveUserDetails(final String handle) throws MissingMethodParameterException,
        AuthenticationException, AuthorizationException, UserAccountNotFoundException, SystemException {
        return business.retrieveUserDetails(handle);
    }

    /**
     * Setter for the business object.
     *
     * @param business business object.
     */
    public void setBusiness(final de.escidoc.core.aa.business.interfaces.UserAccountHandlerInterface business) {
        this.business = business;
    }

    /**
     * See Interface for functional description.
     *
     * @param userId userId
     * @return preferences as xml
     * @throws UserAccountNotFoundException e
     * @throws MissingMethodParameterException
     *                                      e
     * @throws AuthenticationException      e
     * @throws AuthorizationException       e
     * @throws SystemException              e
     * @see UserAccountHandlerInterface #retrieveCurrentGrants(java.lang.String)
     */
    @Override
    public String retrievePreferences(final String userId) throws UserAccountNotFoundException,
        MissingMethodParameterException, AuthenticationException, AuthorizationException, SystemException {

        return business.retrievePreferences(userId);
    }

    /**
     * See Interface for functional description.
     *
     * @param userId        The userId.
     * @param preferenceXML The preferencesXML.
     * @return created preferenceXML
     * @throws AlreadyExistsException       If
     * @throws UserAccountNotFoundException If
     * @throws PreferenceNotFoundException  If
     * @throws XmlCorruptedException        If
     * @throws XmlSchemaValidationException If
     * @throws SystemException              If
     * @throws MissingMethodParameterException
     *                                      Thrown if no user id or no task parameters are provided.
     * @throws AuthenticationException      Thrown if the authentication fails due to an invalid provided
     *                                      eSciDocUserHandle.
     * @throws AuthorizationException       Thrown if the authorization fails.
     * @see UserAccountHandlerInterface #createPreference(java.lang.String, java.lang.String)
     */
    @Override
    public String createPreference(final String userId, final String preferenceXML) throws AlreadyExistsException,
        UserAccountNotFoundException, PreferenceNotFoundException, XmlCorruptedException, XmlSchemaValidationException,
        MissingMethodParameterException, AuthenticationException, AuthorizationException, SystemException {
        return business.createPreference(userId, preferenceXML);
    }

    /**
     * See Interface for functional description.
     *
     * @param userId         The userId.
     * @param preferencesXML The preferencesXML.
     * @return updated preferenceXML
     * @throws UserAccountNotFoundException   If
     * @throws XmlCorruptedException          If
     * @throws XmlSchemaValidationException   If
     * @throws MissingAttributeValueException If
     * @throws SystemException                If
     * @throws OptimisticLockingException     Thrown in case of an optimistic locking error.
     * @throws MissingMethodParameterException
     *                                        Thrown if no user id or no task parameters are provided.
     * @throws AuthenticationException        Thrown if the authentication fails due to an invalid provided
     *                                        eSciDocUserHandle.
     * @throws AuthorizationException         Thrown if the authorization fails.
     * @see UserAccountHandlerInterface #updatePreferences(java.lang.String, java.lang.String)
     */
    @Override
    public String updatePreferences(final String userId, final String preferencesXML)
        throws UserAccountNotFoundException, XmlCorruptedException, XmlSchemaValidationException,
        OptimisticLockingException, MissingMethodParameterException, AuthenticationException, AuthorizationException,
        SystemException, MissingAttributeValueException {
        return business.updatePreferences(userId, preferencesXML);
    }

    /**
     * See Interface for functional description.
     *
     * @param userId         The userId.
     * @param preferenceName The preferenceName.
     * @param preferenceXML  The preferenceXML.
     * @return updated preferenceXML
     * @throws UserAccountNotFoundException   If
     * @throws AlreadyExistsException         If
     * @throws UserAccountNotFoundException   If
     * @throws PreferenceNotFoundException    If
     * @throws OptimisticLockingException     If
     * @throws XmlCorruptedException          If
     * @throws XmlSchemaValidationException   If
     * @throws MissingAttributeValueException If
     * @throws SystemException                If
     * @throws MissingMethodParameterException
     *                                        Thrown if no user id or no task parameters are provided.
     * @throws AuthenticationException        Thrown if the authentication fails due to an invalid provided
     *                                        eSciDocUserHandle.
     * @throws AuthorizationException         Thrown if the authorization fails.
     * @see UserAccountHandlerInterface #updatePreference(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public String updatePreference(final String userId, final String preferenceName, final String preferenceXML)
        throws AlreadyExistsException, UserAccountNotFoundException, XmlCorruptedException,
        XmlSchemaValidationException, MissingMethodParameterException, AuthenticationException, AuthorizationException,
        SystemException, PreferenceNotFoundException, OptimisticLockingException, MissingAttributeValueException {
        return business.updatePreference(userId, preferenceName, preferenceXML);
    }

    /**
     * See Interface for functional description.
     *
     * @param userId         The userId.
     * @param preferenceName The preferenceName.
     * @return preferenceXML
     * @throws UserAccountNotFoundException If
     * @throws UserAccountNotFoundException If
     * @throws PreferenceNotFoundException  If
     * @throws SystemException              If
     * @throws MissingMethodParameterException
     *                                      Thrown if no user id or no task parameters are provided.
     * @throws AuthenticationException      Thrown if the authentication fails due to an invalid provided
     *                                      eSciDocUserHandle.
     * @throws AuthorizationException       Thrown if the authorization fails.
     * @see UserAccountHandlerInterface #retrievePreference(java.lang.String, java.lang.String)
     */
    @Override
    public String retrievePreference(final String userId, final String preferenceName)
        throws UserAccountNotFoundException, PreferenceNotFoundException, MissingMethodParameterException,
        AuthenticationException, AuthorizationException, SystemException {
        return business.retrievePreference(userId, preferenceName);
    }

    /**
     * See Interface for functional description.
     *
     * @param userId         The userId.
     * @param preferenceName The preferenceName.
     * @throws UserAccountNotFoundException If
     * @throws PreferenceNotFoundException  If
     * @throws SystemException              If
     * @throws MissingMethodParameterException
     *                                      Thrown if no user id or no task parameters are provided.
     * @throws AuthenticationException      Thrown if the authentication fails due to an invalid provided
     *                                      eSciDocUserHandle.
     * @throws AuthorizationException       Thrown if the authorization fails.
     * @see UserAccountHandlerInterface #deletePreference(java.lang.String, java.lang.String)
     */
    @Override
    public void deletePreference(final String userId, final String preferenceName) throws UserAccountNotFoundException,
        PreferenceNotFoundException, MissingMethodParameterException, AuthenticationException, AuthorizationException,
        SystemException {
        business.deletePreference(userId, preferenceName);
    }

    /**
     * See Interface for functional description.
     *
     * @param userId       The userId.
     * @param attributeXml The attributeXml.
     * @return created attributeXml
     * @throws AlreadyExistsException       If
     * @throws UserAccountNotFoundException If
     * @throws XmlCorruptedException        If
     * @throws XmlSchemaValidationException If
     * @throws SystemException              If
     * @throws MissingMethodParameterException
     *                                      Thrown if no user id or no task parameters are provided.
     * @throws AuthenticationException      Thrown if the authentication fails due to an invalid provided
     *                                      eSciDocUserHandle.
     * @throws AuthorizationException       Thrown if the authorization fails.
     * @see UserAccountHandlerInterface #createAttribute(java.lang.String, java.lang.String)
     */
    @Override
    public String createAttribute(final String userId, final String attributeXml) throws AlreadyExistsException,
        UserAccountNotFoundException, XmlCorruptedException, XmlSchemaValidationException,
        MissingMethodParameterException, AuthenticationException, AuthorizationException, SystemException {
        return business.createAttribute(userId, attributeXml);
    }

    /**
     * See Interface for functional description.
     *
     * @param userId id of user
     * @return String attributes as xml
     * @throws UserAccountNotFoundException e
     * @throws SystemException              e
     * @throws MissingMethodParameterException
     *                                      Thrown if no user id or no task parameters are provided.
     * @throws AuthenticationException      Thrown if the authentication fails due to an invalid provided
     *                                      eSciDocUserHandle.
     * @throws AuthorizationException       Thrown if the authorization fails.
     * @see UserAccountHandlerInterface #retrieveAttributes(java.lang.String)
     */
    @Override
    public String retrieveAttributes(final String userId) throws UserAccountNotFoundException,
        MissingMethodParameterException, AuthenticationException, AuthorizationException, SystemException {
        return business.retrieveAttributes(userId);
    }

    /**
     * See Interface for functional description.
     *
     * @param userId id of user
     * @param name   name of attribute
     * @return String xml with user-attribute
     * @throws UserAccountNotFoundException   e
     * @throws UserAttributeNotFoundException e
     * @throws MissingMethodParameterException
     *                                        Thrown if no user id or no task parameters are provided.
     * @throws AuthenticationException        Thrown if the authentication fails due to an invalid provided
     *                                        eSciDocUserHandle.
     * @throws AuthorizationException         Thrown if the authorization fails.
     * @throws SystemException                e
     * @see UserAccountHandlerInterface #retrieveAttribute(java.lang.String, java.lang.String)
     */
    @Override
    public String retrieveNamedAttributes(final String userId, final String name) throws UserAccountNotFoundException,
        UserAttributeNotFoundException, MissingMethodParameterException, AuthenticationException,
        AuthorizationException, SystemException {
        return business.retrieveNamedAttributes(userId, name);
    }

    /**
     * See Interface for functional description.
     *
     * @param userId      id of user
     * @param attributeId id of attribute
     * @return String xml with user-attribute
     * @throws UserAccountNotFoundException   e
     * @throws UserAttributeNotFoundException e
     * @throws MissingMethodParameterException
     *                                        Thrown if no user id or no task parameters are provided.
     * @throws AuthenticationException        Thrown if the authentication fails due to an invalid provided
     *                                        eSciDocUserHandle.
     * @throws AuthorizationException         Thrown if the authorization fails.
     * @throws SystemException                e
     * @see UserAccountHandlerInterface #retrieveAttribute(java.lang.String, java.lang.String)
     */
    @Override
    public String retrieveAttribute(final String userId, final String attributeId) throws UserAccountNotFoundException,
        UserAttributeNotFoundException, MissingMethodParameterException, AuthenticationException,
        AuthorizationException, SystemException {
        return business.retrieveAttribute(userId, attributeId);
    }

    /**
     * See Interface for functional description.
     *
     * @param userId       id of user
     * @param attributeId  name of attribute
     * @param attributeXml xml with attribute
     * @return String xml with updated attribute
     * @throws UserAccountNotFoundException   e
     * @throws UserAttributeNotFoundException e
     * @throws XmlCorruptedException          If
     * @throws XmlSchemaValidationException   If
     * @throws ReadonlyElementViolationException
     *                                        e
     * @throws OptimisticLockingException     e
     * @throws AuthenticationException        e
     * @throws AuthorizationException         e
     * @throws MissingMethodParameterException
     *                                        e
     * @throws SystemException                e
     * @see UserAccountHandlerInterface #updateAttribute(java.lang.String, java.lang.String)
     */
    @Override
    public String updateAttribute(final String userId, final String attributeId, final String attributeXml)
        throws UserAccountNotFoundException, OptimisticLockingException, UserAttributeNotFoundException,
        ReadonlyElementViolationException, XmlCorruptedException, XmlSchemaValidationException,
        MissingMethodParameterException, AuthenticationException, AuthorizationException, SystemException {
        return business.updateAttribute(userId, attributeId, attributeXml);
    }

    /**
     * See Interface for functional description.
     *
     * @param userId      The userId.
     * @param attributeId The attributeId.
     * @throws UserAccountNotFoundException   e
     * @throws UserAttributeNotFoundException e
     * @throws ReadonlyElementViolationException
     *                                        e
     * @throws AuthenticationException        e
     * @throws AuthorizationException         e
     * @throws MissingMethodParameterException
     *                                        e
     * @throws SystemException                e
     * @see UserAccountHandlerInterface #deleteAttribute(java.lang.String, java.lang.String)
     */
    @Override
    public void deleteAttribute(final String userId, final String attributeId) throws UserAccountNotFoundException,
        UserAttributeNotFoundException, ReadonlyElementViolationException, MissingMethodParameterException,
        AuthenticationException, AuthorizationException, SystemException {
        business.deleteAttribute(userId, attributeId);
    }

    /**
     * See Interface for functional description.
     *
     * @param parameters parameter map
     * @return filter sub query with permission rules
     * @throws SystemException             e
     * @throws InvalidSearchQueryException e
     * @throws AuthenticationException     e
     * @throws AuthorizationException      e
     */
    @Override
    public String retrievePermissionFilterQuery(final Map<String, String[]> parameters) throws SystemException,
        InvalidSearchQueryException, AuthenticationException, AuthorizationException {
        return business.retrievePermissionFilterQuery(parameters);
    }
}
