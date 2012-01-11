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
 * Copyright 2006-2011 Fachinformationszentrum Karlsruhe Gesellschaft
 * fuer wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Foerderung der Wissenschaft e.V.  
 * All rights reserved.  Use is subject to license terms.
 */
package de.escidoc.core.aa.internal;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.escidoc.core.domain.aa.CurrentGrantsTO;
import org.escidoc.core.domain.aa.GrantTO;
import org.escidoc.core.domain.aa.PermissionFilterTO;
import org.escidoc.core.domain.aa.UserAccountAttributeListTO;
import org.escidoc.core.domain.aa.UserAccountAttributeTO;
import org.escidoc.core.domain.aa.UserAccountPreferenceListTO;
import org.escidoc.core.domain.aa.UserAccountPreferenceTO;
import org.escidoc.core.domain.aa.UserAccountResourcesTO;
import org.escidoc.core.domain.aa.UserAccountTO;
import org.escidoc.core.domain.service.ServiceUtility;
import org.escidoc.core.domain.taskparam.OptimisticLockingTaskParamTO;
import org.escidoc.core.domain.taskparam.RevokeGrantTaskParamTO;
import org.escidoc.core.domain.taskparam.RevokeGrantsTaskParamTO;
import org.escidoc.core.domain.taskparam.UpdatePasswordTaskParamTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.escidoc.core.aa.UserAccountRestService;
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
 * @author Michael Hoppe
 *
 */
public class UserAccountRestServiceImpl implements UserAccountRestService {

    @Autowired
    @Qualifier("service.UserAccountHandler")
    private UserAccountHandlerInterface userAccountHandler;

    /**
     * 
     */
    public UserAccountRestServiceImpl() {
    }

    /* (non-Javadoc)
     * @see de.escidoc.core.aa.UserAccountRestService#create(org.escidoc.core.domain.aa.UserAccountTO)
     */
    @Override
    public UserAccountTO create(final UserAccountTO userAccountTO) throws UniqueConstraintViolationException,
        InvalidStatusException, XmlCorruptedException, XmlSchemaValidationException,
        OrganizationalUnitNotFoundException, MissingMethodParameterException, AuthenticationException,
        AuthorizationException, SystemException {
        return ServiceUtility.fromXML(UserAccountTO.class, this.userAccountHandler.create(ServiceUtility.toXML(userAccountTO)));
    }

    /* (non-Javadoc)
     * @see de.escidoc.core.aa.UserAccountRestService#retrieve(java.lang.String)
     */
    @Override
    public UserAccountTO retrieve(final String id) throws UserAccountNotFoundException, MissingMethodParameterException,
        AuthenticationException, AuthorizationException, SystemException {
        return ServiceUtility.fromXML(UserAccountTO.class, this.userAccountHandler.retrieve(id));
    }

    /* (non-Javadoc)
     * @see de.escidoc.core.aa.UserAccountRestService#update(java.lang.String, org.escidoc.core.domain.aa.UserAccountTO)
     */
    @Override
    public UserAccountTO update(final String id, final UserAccountTO userAccountTO) throws UserAccountNotFoundException,
        UniqueConstraintViolationException, InvalidStatusException, XmlCorruptedException,
        XmlSchemaValidationException, MissingMethodParameterException, MissingAttributeValueException,
        OptimisticLockingException, AuthenticationException, AuthorizationException,
        OrganizationalUnitNotFoundException, SystemException {
        return ServiceUtility.fromXML(UserAccountTO.class, this.userAccountHandler.update(id, ServiceUtility.toXML(userAccountTO)));
    }

    /* (non-Javadoc)
     * @see de.escidoc.core.aa.UserAccountRestService#delete(java.lang.String)
     */
    @Override
    public void delete(final String id) throws UserAccountNotFoundException, MissingMethodParameterException,
        AuthenticationException, AuthorizationException, SystemException {
        this.userAccountHandler.delete(id);
    }

    /* (non-Javadoc)
     * @see de.escidoc.core.aa.UserAccountRestService#retrieveCurrentUser()
     */
    @Override
    public UserAccountTO retrieveCurrentUser() throws UserAccountNotFoundException, AuthenticationException,
        AuthorizationException, SystemException {
        return ServiceUtility.fromXML(UserAccountTO.class, this.userAccountHandler.retrieveCurrentUser());
    }

    /* (non-Javadoc)
     * @see de.escidoc.core.aa.UserAccountRestService#updatePassword(java.lang.String, java.lang.String)
     */
    @Override
    public void updatePassword(final String id, final UpdatePasswordTaskParamTO taskParam) throws UserAccountNotFoundException,
        InvalidStatusException, XmlCorruptedException, MissingMethodParameterException, OptimisticLockingException,
        AuthenticationException, AuthorizationException, SystemException {
        this.userAccountHandler.updatePassword(id, ServiceUtility.toXML(taskParam));
    }

    /* (non-Javadoc)
     * @see de.escidoc.core.aa.UserAccountRestService#updatePreferences(java.lang.String, org.escidoc.core.domain.aa.UserAccountPreferenceListTO)
     */
    @Override
    public UserAccountPreferenceListTO updatePreferences(final String id, final UserAccountPreferenceListTO userAccountPrefrencesTO)
        throws UserAccountNotFoundException, XmlCorruptedException, XmlSchemaValidationException,
        OptimisticLockingException, SystemException, AuthenticationException, AuthorizationException,
        MissingMethodParameterException, MissingAttributeValueException {
        return ServiceUtility.fromXML(UserAccountPreferenceListTO.class, this.userAccountHandler.updatePreferences(id, ServiceUtility.toXML(userAccountPrefrencesTO)));
    }

    /* (non-Javadoc)
     * @see de.escidoc.core.aa.UserAccountRestService#activate(java.lang.String, java.lang.String)
     */
    @Override
    public void activate(final String id, final OptimisticLockingTaskParamTO taskParam) throws AlreadyActiveException, UserAccountNotFoundException,
        XmlCorruptedException, MissingMethodParameterException, MissingAttributeValueException,
        OptimisticLockingException, AuthenticationException, AuthorizationException, SystemException {
        this.userAccountHandler.activate(id, ServiceUtility.toXML(taskParam));
    }

    /* (non-Javadoc)
     * @see de.escidoc.core.aa.UserAccountRestService#deactivate(java.lang.String, java.lang.String)
     */
    @Override
    public void deactivate(final String id, final OptimisticLockingTaskParamTO taskParam) throws AlreadyDeactiveException, UserAccountNotFoundException,
        XmlCorruptedException, MissingMethodParameterException, MissingAttributeValueException,
        OptimisticLockingException, AuthenticationException, AuthorizationException, SystemException {
        this.userAccountHandler.deactivate(id, ServiceUtility.toXML(taskParam));
    }

    /* (non-Javadoc)
     * @see de.escidoc.core.aa.UserAccountRestService#retrieveResources(java.lang.String)
     */
    @Override
    public UserAccountResourcesTO retrieveResources(final String id) throws UserAccountNotFoundException,
        MissingMethodParameterException, AuthenticationException, AuthorizationException, SystemException {
        return ServiceUtility.fromXML(UserAccountResourcesTO.class, this.userAccountHandler.retrieveResources(id));
    }

    /* (non-Javadoc)
     * @see de.escidoc.core.aa.UserAccountRestService#retrieveCurrentGrants(java.lang.String)
     */
    @Override
    public CurrentGrantsTO retrieveCurrentGrants(final String id) throws UserAccountNotFoundException,
        MissingMethodParameterException, AuthenticationException, AuthorizationException, SystemException {
        return ServiceUtility.fromXML(CurrentGrantsTO.class, this.userAccountHandler.retrieveCurrentGrants(id));
    }

    /* (non-Javadoc)
     * @see de.escidoc.core.aa.UserAccountRestService#createGrant(org.escidoc.core.domain.aa.GrantTO)
     */
    @Override
    public GrantTO createGrant(final String id, final GrantTO grantTo) throws AlreadyExistsException, UserAccountNotFoundException,
        InvalidScopeException, RoleNotFoundException, XmlCorruptedException, XmlSchemaValidationException,
        MissingMethodParameterException, AuthenticationException, AuthorizationException, SystemException {
        return ServiceUtility.fromXML(GrantTO.class, this.userAccountHandler.createGrant(id, ServiceUtility.toXML(grantTo)));
    }

    /* (non-Javadoc)
     * @see de.escidoc.core.aa.UserAccountRestService#retrieveGrant(java.lang.String, java.lang.String)
     */
    @Override
    public GrantTO retrieveGrant(final String id, final String grantId) throws UserAccountNotFoundException,
        GrantNotFoundException, MissingMethodParameterException, AuthenticationException, AuthorizationException,
        SystemException {
        return ServiceUtility.fromXML(GrantTO.class, this.userAccountHandler.retrieveGrant(id, grantId));
    }

    /* (non-Javadoc)
     * @see de.escidoc.core.aa.UserAccountRestService#revokeGrant(java.lang.String, java.lang.String)
     */
    @Override
    public void revokeGrant(final String id, final String grantId, final RevokeGrantTaskParamTO taskParam) throws UserAccountNotFoundException, GrantNotFoundException,
        AlreadyRevokedException, XmlCorruptedException, MissingAttributeValueException,
        MissingMethodParameterException, AuthenticationException, AuthorizationException, SystemException {
        this.userAccountHandler.revokeGrant(id, grantId, ServiceUtility.toXML(taskParam));
    }

    /* (non-Javadoc)
     * @see de.escidoc.core.aa.UserAccountRestService#revokeGrants(java.lang.String, java.lang.String)
     */
    @Override
    public void revokeGrants(final String id, final RevokeGrantsTaskParamTO taskParam) throws UserAccountNotFoundException, GrantNotFoundException,
        AlreadyRevokedException, XmlCorruptedException, MissingAttributeValueException,
        MissingMethodParameterException, AuthenticationException, AuthorizationException, SystemException {
        this.userAccountHandler.revokeGrants(id, ServiceUtility.toXML(taskParam));
    }

    /* (non-Javadoc)
     * @see de.escidoc.core.aa.UserAccountRestService#retrievePreference(java.lang.String, java.lang.String)
     */
    @Override
    public UserAccountPreferenceTO retrievePreference(final String id, final String name) throws UserAccountNotFoundException,
        PreferenceNotFoundException, MissingMethodParameterException, AuthenticationException, AuthorizationException,
        SystemException {
        return ServiceUtility.fromXML(UserAccountPreferenceTO.class, this.userAccountHandler.retrievePreference(id, name));
    }

    /* (non-Javadoc)
     * @see de.escidoc.core.aa.UserAccountRestService#retrievePreferences(java.lang.String)
     */
    @Override
    public UserAccountPreferenceListTO retrievePreferences(final String id) throws UserAccountNotFoundException,
        MissingMethodParameterException, AuthenticationException, AuthorizationException, SystemException {
        return ServiceUtility.fromXML(UserAccountPreferenceListTO.class, this.userAccountHandler.retrievePreferences(id));
    }

    /* (non-Javadoc)
     * @see de.escidoc.core.aa.UserAccountRestService#createPreference(java.lang.String, org.escidoc.core.domain.aa.UserAccountPreferenceTO)
     */
    @Override
    public UserAccountPreferenceTO createPreference(final String id, final UserAccountPreferenceTO userAccountPreferenceTO)
        throws AlreadyExistsException, UserAccountNotFoundException, XmlCorruptedException,
        XmlSchemaValidationException, MissingMethodParameterException, AuthenticationException, AuthorizationException,
        SystemException, PreferenceNotFoundException {
        return ServiceUtility.fromXML(UserAccountPreferenceTO.class, this.userAccountHandler.createPreference(id, ServiceUtility.toXML(userAccountPreferenceTO)));
    }

    /* (non-Javadoc)
     * @see de.escidoc.core.aa.UserAccountRestService#updatePreference(java.lang.String, java.lang.String, org.escidoc.core.domain.aa.UserAccountPreferenceTO)
     */
    @Override
    public UserAccountPreferenceTO updatePreference(
        final String id, final String preferenceName, final UserAccountPreferenceTO userAccountPreferenceTO)
        throws AlreadyExistsException, UserAccountNotFoundException, XmlCorruptedException,
        XmlSchemaValidationException, MissingMethodParameterException, AuthenticationException, AuthorizationException,
        SystemException, PreferenceNotFoundException, OptimisticLockingException, MissingAttributeValueException {
        return ServiceUtility.fromXML(UserAccountPreferenceTO.class, this.userAccountHandler.updatePreference(id, preferenceName, ServiceUtility.toXML(userAccountPreferenceTO)));
    }

    /* (non-Javadoc)
     * @see de.escidoc.core.aa.UserAccountRestService#deletePreference(java.lang.String, java.lang.String)
     */
    @Override
    public void deletePreference(final String id, final String preferenceName) throws UserAccountNotFoundException,
        PreferenceNotFoundException, MissingMethodParameterException, AuthenticationException, AuthorizationException,
        SystemException {
        this.userAccountHandler.deletePreference(id, preferenceName);
    }

    /* (non-Javadoc)
     * @see de.escidoc.core.aa.UserAccountRestService#createAttribute(java.lang.String, org.escidoc.core.domain.aa.UserAccountAttributeTO)
     */
    @Override
    public UserAccountAttributeTO createAttribute(final String id, final UserAccountAttributeTO userAccountAttributeTO)
        throws AlreadyExistsException, UserAccountNotFoundException, XmlCorruptedException,
        XmlSchemaValidationException, MissingMethodParameterException, AuthenticationException, AuthorizationException,
        SystemException {
        return ServiceUtility.fromXML(UserAccountAttributeTO.class, this.userAccountHandler.createAttribute(id, ServiceUtility.toXML(userAccountAttributeTO)));
    }

    /* (non-Javadoc)
     * @see de.escidoc.core.aa.UserAccountRestService#retrieveAttributes(java.lang.String)
     */
    @Override
    public UserAccountAttributeListTO retrieveAttributes(final String id) throws UserAccountNotFoundException,
        MissingMethodParameterException, AuthenticationException, AuthorizationException, SystemException {
        return ServiceUtility.fromXML(UserAccountAttributeListTO.class, this.userAccountHandler.retrieveAttributes(id));
    }

    /* (non-Javadoc)
     * @see de.escidoc.core.aa.UserAccountRestService#retrieveNamedAttributes(java.lang.String, java.lang.String)
     */
    @Override
    public UserAccountAttributeListTO retrieveNamedAttributes(final String id, final String name)
        throws UserAccountNotFoundException, UserAttributeNotFoundException, MissingMethodParameterException,
        AuthenticationException, AuthorizationException, SystemException {
        return ServiceUtility.fromXML(UserAccountAttributeListTO.class, this.userAccountHandler.retrieveNamedAttributes(id, name));
    }

    /* (non-Javadoc)
     * @see de.escidoc.core.aa.UserAccountRestService#retrieveAttribute(java.lang.String, java.lang.String)
     */
    @Override
    public UserAccountAttributeTO retrieveAttribute(final String id, final String attId) throws UserAccountNotFoundException,
        UserAttributeNotFoundException, MissingMethodParameterException, AuthenticationException,
        AuthorizationException, SystemException {
        return ServiceUtility.fromXML(UserAccountAttributeTO.class, this.userAccountHandler.retrieveAttribute(id, attId));
    }

    /* (non-Javadoc)
     * @see de.escidoc.core.aa.UserAccountRestService#updateAttribute(java.lang.String, java.lang.String, org.escidoc.core.domain.aa.UserAccountAttributeTO)
     */
    @Override
    public UserAccountAttributeTO updateAttribute(final String id, final String attId, final UserAccountAttributeTO userAccountAttributeTO)
        throws UserAccountNotFoundException, OptimisticLockingException, UserAttributeNotFoundException,
        ReadonlyElementViolationException, XmlCorruptedException, XmlSchemaValidationException,
        MissingMethodParameterException, AuthenticationException, AuthorizationException, SystemException {
        return ServiceUtility.fromXML(UserAccountAttributeTO.class, this.userAccountHandler.updateAttribute(id, attId, ServiceUtility.toXML(userAccountAttributeTO)));
    }

    /* (non-Javadoc)
     * @see de.escidoc.core.aa.UserAccountRestService#deleteAttribute(java.lang.String, java.lang.String)
     */
    @Override
    public void deleteAttribute(final String id, final String attId) throws UserAccountNotFoundException,
        UserAttributeNotFoundException, ReadonlyElementViolationException, MissingMethodParameterException,
        AuthenticationException, AuthorizationException, SystemException {
        this.userAccountHandler.deleteAttribute(id, attId);
    }

    /* (non-Javadoc)
     * @see de.escidoc.core.aa.UserAccountRestService#retrievePermissionFilterQuery(java.util.Set, java.util.Set, java.util.Set)
     */
    @Override
    public PermissionFilterTO retrievePermissionFilterQuery(final Set<String> index, final Set<String> user, final Set<String> role) throws SystemException,
        InvalidSearchQueryException, AuthenticationException, AuthorizationException {
        Map<String, String[]> parameters = new HashMap<String, String[]>();
        if (index != null && index.size() > 0) {
            parameters.put("index", (String[])index.toArray());
        }
        if (user != null && user.size() > 0) {
            parameters.put("user", (String[])user.toArray());
        }
        if (role != null && role.size() > 0) {
            parameters.put("role", (String[])role.toArray());
        }
        return ServiceUtility.fromXML(PermissionFilterTO.class, this.userAccountHandler.retrievePermissionFilterQuery(parameters));
    }

}
