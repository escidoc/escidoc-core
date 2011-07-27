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
package de.escidoc.core.aa.service;

import de.escidoc.core.aa.service.interfaces.UserGroupHandlerInterface;
import de.escidoc.core.common.exceptions.application.invalid.InvalidContentException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidScopeException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidSearchQueryException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidXmlException;
import de.escidoc.core.common.exceptions.application.invalid.XmlCorruptedException;
import de.escidoc.core.common.exceptions.application.invalid.XmlSchemaValidationException;
import de.escidoc.core.common.exceptions.application.missing.MissingAttributeValueException;
import de.escidoc.core.common.exceptions.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.application.notfound.GrantNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.OrganizationalUnitNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ResourceNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.RoleNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.UserAccountNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.UserGroupNotFoundException;
import de.escidoc.core.common.exceptions.application.security.AuthenticationException;
import de.escidoc.core.common.exceptions.application.security.AuthorizationException;
import de.escidoc.core.common.exceptions.application.violated.AlreadyActiveException;
import de.escidoc.core.common.exceptions.application.violated.AlreadyDeactiveException;
import de.escidoc.core.common.exceptions.application.violated.AlreadyExistsException;
import de.escidoc.core.common.exceptions.application.violated.AlreadyRevokedException;
import de.escidoc.core.common.exceptions.application.violated.OptimisticLockingException;
import de.escidoc.core.common.exceptions.application.violated.UniqueConstraintViolationException;
import de.escidoc.core.common.exceptions.application.violated.UserGroupHierarchyViolationException;
import de.escidoc.core.common.exceptions.system.SystemException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * User group handler implementation for the service layer of the AA component.
 *
 * @author Michael Hoppe
 */
@Service("service.UserGroupHandler")
public class UserGroupHandler implements UserGroupHandlerInterface {

    @Autowired
    @Qualifier("business.UserGroupHandler")
    private de.escidoc.core.aa.business.interfaces.UserGroupHandlerInterface business;

    /**
     * Private constructor to prevent initialization.
     */
    protected UserGroupHandler() {
    }

    /**
     * See Interface for functional description.
     *
     * @param xmlData xmlData
     * @return String created userAccount
     * @throws UniqueConstraintViolationException
     *                                      e
     * @throws XmlCorruptedException        e
     * @throws XmlSchemaValidationException e
     * @throws MissingMethodParameterException
     *                                      e
     * @throws AuthenticationException      e
     * @throws AuthorizationException       e
     * @throws SystemException              e
     */
    @Override
    public String create(final String xmlData) throws UniqueConstraintViolationException, XmlCorruptedException,
        XmlSchemaValidationException, MissingMethodParameterException, AuthenticationException, AuthorizationException,
        SystemException {
        try {
            return business.create(xmlData);
        }
        catch (final InvalidXmlException e) {
            throw new XmlCorruptedException(e);
        }
    }

    /**
     * See Interface for functional description.
     *
     * @param groupId groupId
     * @throws UserGroupNotFoundException e
     * @throws MissingMethodParameterException
     *                                    e
     * @throws AuthenticationException    e
     * @throws AuthorizationException     e
     * @throws SystemException            e
     */
    @Override
    public void delete(final String groupId) throws UserGroupNotFoundException, MissingMethodParameterException,
        AuthenticationException, AuthorizationException, SystemException {
        try {
            business.delete(groupId);
        }
        catch (final ResourceNotFoundException e) {
            throw new UserGroupNotFoundException(e);
        }
    }

    /**
     * See Interface for functional description.
     *
     * @param groupId groupId
     * @return String userGroup as xml
     * @throws UserGroupNotFoundException e
     * @throws MissingMethodParameterException
     *                                    e
     * @throws AuthenticationException    e
     * @throws AuthorizationException     e
     * @throws SystemException            e
     */
    @Override
    public String retrieve(final String groupId) throws UserGroupNotFoundException, MissingMethodParameterException,
        AuthenticationException, AuthorizationException, SystemException {
        try {
            return business.retrieve(groupId);
        }
        catch (final ResourceNotFoundException e) {
            throw new UserGroupNotFoundException(e);
        }
    }

    /**
     * See Interface for functional description.
     *
     * @param groupId groupId
     * @param xmlData xmlData
     * @return userGroup as xml
     * @throws UserGroupNotFoundException     e
     * @throws UniqueConstraintViolationException
     *                                        e
     * @throws XmlCorruptedException          e
     * @throws XmlSchemaValidationException   e
     * @throws MissingMethodParameterException
     *                                        e
     * @throws MissingAttributeValueException e
     * @throws OptimisticLockingException     e
     * @throws AuthenticationException        e
     * @throws AuthorizationException         e
     * @throws SystemException                e
     */
    @Override
    public String update(final String groupId, final String xmlData) throws UserGroupNotFoundException,
        UniqueConstraintViolationException, XmlCorruptedException, XmlSchemaValidationException,
        MissingMethodParameterException, MissingAttributeValueException, OptimisticLockingException,
        AuthenticationException, AuthorizationException, SystemException {
        try {
            return business.update(groupId, xmlData);
        }
        catch (final ResourceNotFoundException e) {
            throw new UserGroupNotFoundException(e);
        }
        catch (final InvalidXmlException e) {
            throw new XmlCorruptedException(e);
        }
    }

    /**
     * See Interface for functional description.
     *
     * @param groupId   groupId
     * @param taskParam taskParam
     * @throws AlreadyActiveException         e
     * @throws UserGroupNotFoundException     e
     * @throws XmlCorruptedException          e
     * @throws MissingMethodParameterException
     *                                        e
     * @throws MissingAttributeValueException e
     * @throws OptimisticLockingException     e
     * @throws AuthenticationException        e
     * @throws AuthorizationException         e
     * @throws SystemException                e
     */
    @Override
    public void activate(final String groupId, final String taskParam) throws AlreadyActiveException,
        UserGroupNotFoundException, XmlCorruptedException, MissingMethodParameterException,
        MissingAttributeValueException, OptimisticLockingException, AuthenticationException, AuthorizationException,
        SystemException {
        try {
            business.activate(groupId, taskParam);
        }
        catch (final ResourceNotFoundException e) {
            throw new UserGroupNotFoundException(e);
        }
    }

    /**
     * See Interface for functional description.
     *
     * @param groupId   groupId
     * @param taskParam taskParam
     * @throws AlreadyDeactiveException       e
     * @throws UserGroupNotFoundException     e
     * @throws XmlCorruptedException          e
     * @throws MissingMethodParameterException
     *                                        e
     * @throws MissingAttributeValueException e
     * @throws OptimisticLockingException     e
     * @throws AuthenticationException        e
     * @throws AuthorizationException         e
     * @throws SystemException                e
     */
    @Override
    public void deactivate(final String groupId, final String taskParam) throws AlreadyDeactiveException,
        UserGroupNotFoundException, XmlCorruptedException, MissingMethodParameterException,
        MissingAttributeValueException, OptimisticLockingException, AuthenticationException, AuthorizationException,
        SystemException {
        try {
            business.deactivate(groupId, taskParam);
        }
        catch (final ResourceNotFoundException e) {
            throw new UserGroupNotFoundException(e);
        }
    }

    /**
     * See Interface for functional description.
     *
     * @param userGroupId groupId
     * @return currentGrants as xml
     * @throws UserGroupNotFoundException e
     * @throws MissingMethodParameterException
     *                                    e
     * @throws AuthenticationException    e
     * @throws AuthorizationException     e
     * @throws SystemException            e
     * @see UserGroupHandlerInterface #retrieveCurrentGrants(java.lang.String)
     */
    @Override
    public String retrieveCurrentGrants(final String userGroupId) throws UserGroupNotFoundException,
        MissingMethodParameterException, AuthenticationException, AuthorizationException, SystemException {
        try {
            return business.retrieveCurrentGrants(userGroupId);
        }
        catch (final ResourceNotFoundException e) {

            throw new UserGroupNotFoundException(e);

        }
    }

    /**
     * See Interface for functional description.
     *
     * @param groupId  groupId
     * @param grantXML grantXml
     * @return reated grant as xml
     * @throws AlreadyExistsException       e
     * @throws UserGroupNotFoundException   e
     * @throws InvalidScopeException        e
     * @throws RoleNotFoundException        e
     * @throws XmlCorruptedException        e
     * @throws XmlSchemaValidationException e
     * @throws MissingMethodParameterException
     *                                      e
     * @throws AuthenticationException      e
     * @throws AuthorizationException       e
     * @throws SystemException              e
     */
    @Override
    public String createGrant(final String groupId, final String grantXML) throws AlreadyExistsException,
        UserGroupNotFoundException, InvalidScopeException, RoleNotFoundException, XmlCorruptedException,
        XmlSchemaValidationException, MissingMethodParameterException, AuthenticationException, AuthorizationException,
        SystemException {
        try {
            return business.createGrant(groupId, grantXML);
        }
        catch (final ResourceNotFoundException e) {
            if (e instanceof RoleNotFoundException) {
                throw new RoleNotFoundException(e);
            }
            else {
                throw new UserGroupNotFoundException(e);
            }
        }
    }

    /**
     * See Interface for functional description.
     *
     * @param groupId   groupId
     * @param grantId   grantId
     * @param taskParam taskParam
     * @throws UserGroupNotFoundException     e
     * @throws GrantNotFoundException         e
     * @throws AlreadyRevokedException        e
     * @throws XmlCorruptedException          e
     * @throws MissingAttributeValueException e
     * @throws MissingMethodParameterException
     *                                        e
     * @throws AuthenticationException        e
     * @throws AuthorizationException         e
     * @throws SystemException                e
     */
    @Override
    public void revokeGrant(final String groupId, final String grantId, final String taskParam)
        throws UserGroupNotFoundException, GrantNotFoundException, AlreadyRevokedException, XmlCorruptedException,
        MissingAttributeValueException, MissingMethodParameterException, AuthenticationException,
        AuthorizationException, SystemException {
        try {
            business.revokeGrant(groupId, grantId, taskParam);
        }
        catch (final ResourceNotFoundException e) {
            if (e instanceof GrantNotFoundException) {
                throw new GrantNotFoundException(e);
            }
            else {
                throw new UserGroupNotFoundException(e);
            }
        }
    }

    /**
     * See Interface for functional description.
     *
     * @param groupId groupId
     * @param grantId grantId
     * @return grant as xml
     * @throws UserGroupNotFoundException e
     * @throws GrantNotFoundException     e
     * @throws MissingMethodParameterException
     *                                    e
     * @throws AuthenticationException    e
     * @throws AuthorizationException     e
     * @throws SystemException            e
     * @see UserAccountHandlerInterface #retrieveGrant(java.lang.String, java.lang.String)
     */
    @Override
    public String retrieveGrant(final String groupId, final String grantId) throws UserGroupNotFoundException,
        GrantNotFoundException, MissingMethodParameterException, AuthenticationException, AuthorizationException,
        SystemException {
        try {
            return business.retrieveGrant(groupId, grantId);
        }
        catch (final ResourceNotFoundException e) {
            if (e instanceof UserGroupNotFoundException) {
                throw new UserGroupNotFoundException(e);
            }
            else {
                throw new GrantNotFoundException(e);
            }

        }
    }

    /**
     * See Interface for functional description.
     *
     * @param groupId   groupId
     * @param taskParam taskParam
     * @throws UserGroupNotFoundException     e
     * @throws GrantNotFoundException         e
     * @throws AlreadyRevokedException        e
     * @throws XmlCorruptedException          e
     * @throws MissingAttributeValueException e
     * @throws MissingMethodParameterException
     *                                        e
     * @throws AuthenticationException        e
     * @throws AuthorizationException         e
     * @throws SystemException                e
     */
    @Override
    public void revokeGrants(final String groupId, final String taskParam) throws UserGroupNotFoundException,
        GrantNotFoundException, AlreadyRevokedException, XmlCorruptedException, MissingAttributeValueException,
        MissingMethodParameterException, AuthenticationException, AuthorizationException, SystemException {
        try {
            business.revokeGrants(groupId, taskParam);
        }
        catch (final ResourceNotFoundException e) {

            if (e instanceof GrantNotFoundException) {
                throw new GrantNotFoundException(e);

            }
            else {
                throw new UserGroupNotFoundException(e);
            }
        }
    }

    /**
     * Retrieve the resources section of a user group.
     *
     * @param groupId id of the user group
     * @return the resources of the user group as XML structure
     * @throws SystemException            Thrown in case of an internal error.
     * @throws UserGroupNotFoundException Thrown if a user group with the provided id does not exist in the framework.
     */
    @Override
    public String retrieveResources(final String groupId) throws UserGroupNotFoundException, SystemException {
        return business.retrieveResources(groupId);
    }

    /**
     * See Interface for functional description.
     *
     * @param filter CQL query
     * @return filtered userGroups as xml
     * @throws MissingMethodParameterException
     *                                     e
     * @throws AuthenticationException     e
     * @throws AuthorizationException      e
     * @throws InvalidSearchQueryException e
     * @throws SystemException             e
     */
    @Override
    public String retrieveUserGroups(final Map<String, String[]> filter) throws MissingMethodParameterException,
        AuthenticationException, AuthorizationException, InvalidSearchQueryException, SystemException {
        return business.retrieveUserGroups(filter);
    }

    /**
     * See Interface for functional description.
     *
     * @param groupId   groupId
     * @param taskParam taskParam
     * @return group with updated selectors as xml
     * @throws OrganizationalUnitNotFoundException
     *                                      e
     * @throws UserAccountNotFoundException e
     * @throws UserGroupNotFoundException   e
     * @throws InvalidContentException      e
     * @throws MissingMethodParameterException
     *                                      e
     * @throws SystemException              e
     * @throws AuthenticationException      e
     * @throws AuthorizationException       e
     * @throws OptimisticLockingException   e
     * @throws XmlCorruptedException        e
     * @throws XmlSchemaValidationException e
     * @throws UserGroupHierarchyViolationException
     *                                      e
     */
    @Override
    public String addSelectors(final String groupId, final String taskParam)
        throws OrganizationalUnitNotFoundException, UserAccountNotFoundException, UserGroupNotFoundException,
        InvalidContentException, MissingMethodParameterException, SystemException, AuthenticationException,
        AuthorizationException, OptimisticLockingException, XmlCorruptedException, XmlSchemaValidationException,
        UserGroupHierarchyViolationException {
        return business.addSelectors(groupId, taskParam);
    }

    /**
     * See Interface for functional description.
     *
     * @param groupId   groupId
     * @param taskParam taskParam
     * @return group with updated selectors as xml
     * @throws XmlCorruptedException        e
     * @throws XmlSchemaValidationException e
     * @throws AuthenticationException      e
     * @throws AuthorizationException       e
     * @throws SystemException              e
     * @throws UserGroupNotFoundException   e
     * @throws OptimisticLockingException   e
     * @throws MissingMethodParameterException
     *                                      e
     * @throws UserAccountNotFoundException e
     * @throws OrganizationalUnitNotFoundException
     *                                      e
     */
    @Override
    public String removeSelectors(final String groupId, final String taskParam) throws XmlCorruptedException,
        XmlSchemaValidationException, AuthenticationException, AuthorizationException, SystemException,
        UserGroupNotFoundException, OptimisticLockingException, MissingMethodParameterException,
        UserAccountNotFoundException, OrganizationalUnitNotFoundException {
        return business.removeSelectors(groupId, taskParam);
    }

    /**
     * Setter for the business object.
     *
     * @param business business object.
     */
    public void setBusiness(final de.escidoc.core.aa.business.interfaces.UserGroupHandlerInterface business) {
        this.business = business;
    }
}
