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

import org.escidoc.core.domain.aa.AddSelectorsTO;
import org.escidoc.core.domain.aa.CurrentGrantsTO;
import org.escidoc.core.domain.aa.GrantTO;
import org.escidoc.core.domain.aa.RemoveSelectorsTO;
import org.escidoc.core.domain.aa.UserGroupResourcesTO;
import org.escidoc.core.domain.aa.UserGroupSelectorsTO;
import org.escidoc.core.domain.aa.UserGroupTO;
import org.escidoc.core.domain.service.ServiceUtility;
import org.escidoc.core.domain.taskparam.OptimisticLockingTaskParamTO;
import org.escidoc.core.domain.taskparam.RevokeGrantTaskParamTO;
import org.escidoc.core.domain.taskparam.RevokeGrantsTaskParamTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.escidoc.core.aa.UserGroupRestService;
import de.escidoc.core.aa.service.interfaces.UserGroupHandlerInterface;
import de.escidoc.core.common.exceptions.application.invalid.InvalidContentException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidScopeException;
import de.escidoc.core.common.exceptions.application.invalid.XmlCorruptedException;
import de.escidoc.core.common.exceptions.application.invalid.XmlSchemaValidationException;
import de.escidoc.core.common.exceptions.application.missing.MissingAttributeValueException;
import de.escidoc.core.common.exceptions.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.application.notfound.GrantNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.OrganizationalUnitNotFoundException;
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

/**
 * @author Michael Hoppe
 *
 */
public class UserGroupRestServiceImpl implements UserGroupRestService {

    @Autowired
    @Qualifier("service.UserGroupHandler")
    private UserGroupHandlerInterface userGroupHandler;

    /**
     * 
     */
    public UserGroupRestServiceImpl() {
    }

    /* (non-Javadoc)
     * @see de.escidoc.core.aa.UserGroupRestService#create(org.escidoc.core.domain.aa.UserGroupTO)
     */
    @Override
    public UserGroupTO create(final UserGroupTO userGroupTO) throws UniqueConstraintViolationException,
        XmlCorruptedException, XmlSchemaValidationException, MissingMethodParameterException, AuthenticationException,
        AuthorizationException, SystemException {
        return ServiceUtility.fromXML(UserGroupTO.class, this.userGroupHandler.create(ServiceUtility.toXML(userGroupTO)));
    }

    /* (non-Javadoc)
     * @see de.escidoc.core.aa.UserGroupRestService#delete(java.lang.String)
     */
    @Override
    public void delete(final String id) throws UserGroupNotFoundException, MissingMethodParameterException,
        AuthenticationException, AuthorizationException, SystemException {
        this.userGroupHandler.delete(id);
    }

    /* (non-Javadoc)
     * @see de.escidoc.core.aa.UserGroupRestService#retrieve(java.lang.String)
     */
    @Override
    public UserGroupTO retrieve(final String id) throws UserGroupNotFoundException, MissingMethodParameterException,
        AuthenticationException, AuthorizationException, SystemException {
        return ServiceUtility.fromXML(UserGroupTO.class, this.userGroupHandler.retrieve(id));
    }

    /* (non-Javadoc)
     * @see de.escidoc.core.aa.UserGroupRestService#update(java.lang.String, org.escidoc.core.domain.aa.UserGroupTO)
     */
    @Override
    public UserGroupTO update(final String id, final UserGroupTO userGroupTO) throws UserGroupNotFoundException,
        UniqueConstraintViolationException, XmlCorruptedException, XmlSchemaValidationException,
        MissingMethodParameterException, MissingAttributeValueException, OptimisticLockingException,
        AuthenticationException, AuthorizationException, SystemException {
        return ServiceUtility.fromXML(UserGroupTO.class, this.userGroupHandler.update(id, ServiceUtility.toXML(userGroupTO)));
    }

    /* (non-Javadoc)
     * @see de.escidoc.core.aa.UserGroupRestService#activate(java.lang.String, java.lang.String)
     */
    @Override
    public void activate(final String id, final OptimisticLockingTaskParamTO taskParam) throws AlreadyActiveException, UserGroupNotFoundException,
        XmlCorruptedException, MissingMethodParameterException, MissingAttributeValueException,
        OptimisticLockingException, AuthenticationException, AuthorizationException, SystemException {
        this.userGroupHandler.activate(id, ServiceUtility.toXML(taskParam));
    }

    /* (non-Javadoc)
     * @see de.escidoc.core.aa.UserGroupRestService#deactivate(java.lang.String, java.lang.String)
     */
    @Override
    public void deactivate(final String id, final OptimisticLockingTaskParamTO taskParam) throws AlreadyDeactiveException, UserGroupNotFoundException,
        XmlCorruptedException, MissingMethodParameterException, MissingAttributeValueException,
        OptimisticLockingException, AuthenticationException, AuthorizationException, SystemException {
        this.userGroupHandler.deactivate(id, ServiceUtility.toXML(taskParam));
    }

    /* (non-Javadoc)
     * @see de.escidoc.core.aa.UserGroupRestService#retrieveGrant(java.lang.String, java.lang.String)
     */
    @Override
    public GrantTO retrieveGrant(final String id, final String grantId) throws UserGroupNotFoundException, GrantNotFoundException,
        MissingMethodParameterException, AuthenticationException, AuthorizationException, SystemException {
        return ServiceUtility.fromXML(GrantTO.class, this.userGroupHandler.retrieveGrant(id, grantId));
    }

    /* (non-Javadoc)
     * @see de.escidoc.core.aa.UserGroupRestService#retrieveCurrentGrants(java.lang.String)
     */
    @Override
    public CurrentGrantsTO retrieveCurrentGrants(final String id) throws UserGroupNotFoundException,
        MissingMethodParameterException, AuthenticationException, AuthorizationException, SystemException {
        return ServiceUtility.fromXML(CurrentGrantsTO.class, this.userGroupHandler.retrieveCurrentGrants(id));
    }

    /* (non-Javadoc)
     * @see de.escidoc.core.aa.UserGroupRestService#createGrant(java.lang.String, org.escidoc.core.domain.aa.GrantTO)
     */
    @Override
    public GrantTO createGrant(final String id, final GrantTO grantTo) throws AlreadyExistsException, UserGroupNotFoundException,
        InvalidScopeException, RoleNotFoundException, XmlCorruptedException, XmlSchemaValidationException,
        MissingMethodParameterException, AuthenticationException, AuthorizationException, SystemException {
        return ServiceUtility.fromXML(GrantTO.class, this.userGroupHandler.createGrant(id, ServiceUtility.toXML(grantTo)));
    }

    /* (non-Javadoc)
     * @see de.escidoc.core.aa.UserGroupRestService#revokeGrant(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public void revokeGrant(final String id, final String grantId, final RevokeGrantTaskParamTO taskParam) throws UserGroupNotFoundException,
        GrantNotFoundException, AlreadyRevokedException, XmlCorruptedException, MissingAttributeValueException,
        MissingMethodParameterException, AuthenticationException, AuthorizationException, SystemException {
        this.userGroupHandler.revokeGrant(id, grantId, ServiceUtility.toXML(taskParam));
    }

    /* (non-Javadoc)
     * @see de.escidoc.core.aa.UserGroupRestService#revokeGrants(java.lang.String, java.lang.String)
     */
    @Override
    public void revokeGrants(final String id, final RevokeGrantsTaskParamTO taskParam) throws UserGroupNotFoundException, GrantNotFoundException,
        AlreadyRevokedException, XmlCorruptedException, MissingAttributeValueException,
        MissingMethodParameterException, AuthenticationException, AuthorizationException, SystemException {
        this.userGroupHandler.revokeGrants(id, ServiceUtility.toXML(taskParam));
    }

    /* (non-Javadoc)
     * @see de.escidoc.core.aa.UserGroupRestService#retrieveResources(java.lang.String)
     */
    @Override
    public UserGroupResourcesTO retrieveResources(final String id) throws UserGroupNotFoundException, SystemException {
        return ServiceUtility.fromXML(UserGroupResourcesTO.class, this.userGroupHandler.retrieveResources(id));
    }

    /* (non-Javadoc)
     * @see de.escidoc.core.aa.UserGroupRestService#addSelectors(java.lang.String, java.lang.String)
     */
    @Override
    public UserGroupSelectorsTO addSelectors(final String id, final AddSelectorsTO taskParam) throws OrganizationalUnitNotFoundException,
        UserAccountNotFoundException, UserGroupNotFoundException, InvalidContentException,
        MissingMethodParameterException, SystemException, AuthenticationException, AuthorizationException,
        OptimisticLockingException, XmlCorruptedException, XmlSchemaValidationException,
        UserGroupHierarchyViolationException {
        return ServiceUtility.fromXML(UserGroupSelectorsTO.class, this.userGroupHandler.addSelectors(id, ServiceUtility.toXML(taskParam)));
    }

    /* (non-Javadoc)
     * @see de.escidoc.core.aa.UserGroupRestService#removeSelectors(java.lang.String, java.lang.String)
     */
    @Override
    public UserGroupSelectorsTO removeSelectors(final String id, final RemoveSelectorsTO taskParam) throws XmlCorruptedException,
        XmlSchemaValidationException, AuthenticationException, AuthorizationException, SystemException,
        UserGroupNotFoundException, OptimisticLockingException, MissingMethodParameterException,
        OrganizationalUnitNotFoundException, UserAccountNotFoundException {
        return ServiceUtility.fromXML(UserGroupSelectorsTO.class, this.userGroupHandler.removeSelectors(id, ServiceUtility.toXML(taskParam)));
    }

}
