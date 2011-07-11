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
package de.escidoc.core.aa.business;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.escidoc.core.aa.business.filter.UserGroupFilter;
import de.escidoc.core.aa.business.interfaces.PolicyDecisionPointInterface;
import de.escidoc.core.aa.business.interfaces.UserGroupHandlerInterface;
import de.escidoc.core.aa.business.persistence.EscidocRole;
import de.escidoc.core.aa.business.persistence.EscidocRoleDaoInterface;
import de.escidoc.core.aa.business.persistence.RoleGrant;
import de.escidoc.core.aa.business.persistence.ScopeDef;
import de.escidoc.core.aa.business.persistence.UserAccount;
import de.escidoc.core.aa.business.persistence.UserAccountDaoInterface;
import de.escidoc.core.aa.business.persistence.UserAttribute;
import de.escidoc.core.aa.business.persistence.UserGroup;
import de.escidoc.core.aa.business.persistence.UserGroupDaoInterface;
import de.escidoc.core.aa.business.persistence.UserGroupMember;
import de.escidoc.core.aa.business.renderer.interfaces.UserGroupRendererInterface;
import de.escidoc.core.aa.business.stax.handler.GrantStaxHandler;
import de.escidoc.core.aa.business.stax.handler.GroupCreateUpdateHandler;
import de.escidoc.core.aa.business.stax.handler.GroupSelectorsAddHandler;
import de.escidoc.core.aa.business.stax.handler.GroupSelectorsRemoveHandler;
import de.escidoc.core.aa.business.stax.handler.RevokeStaxHandler;
import de.escidoc.core.common.business.Constants;
import de.escidoc.core.common.business.fedora.TripleStoreUtility;
import de.escidoc.core.common.business.filter.DbRequestParameters;
import de.escidoc.core.common.business.filter.SRURequestParameters;
import de.escidoc.core.common.exceptions.application.invalid.InvalidContentException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidScopeException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidSearchQueryException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidXmlException;
import de.escidoc.core.common.exceptions.application.invalid.LastModificationDateMissingException;
import de.escidoc.core.common.exceptions.application.invalid.XmlCorruptedException;
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
import de.escidoc.core.common.exceptions.system.SqlDatabaseSystemException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.util.configuration.EscidocConfiguration;
import de.escidoc.core.common.util.stax.StaxParser;
import de.escidoc.core.common.util.stax.handler.TaskParamHandler;
import de.escidoc.core.common.util.stax.handler.filter.FilterHandler;
import de.escidoc.core.common.util.string.StringUtility;
import de.escidoc.core.common.util.xml.Elements;
import de.escidoc.core.common.util.xml.XmlUtility;
import de.escidoc.core.common.util.xml.factory.ExplainXmlProvider;
import de.escidoc.core.common.util.xml.stax.handler.LinkStaxHandler;
import de.escidoc.core.common.util.xml.stax.handler.OptimisticLockingStaxHandler;

/**
 * Implementation for the user group handler.
 *
 * @author André Schenk
 */
@Service("business.UserGroupHandler")
public class UserGroupHandler implements UserGroupHandlerInterface {

    /**
     * The logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(UserGroupHandler.class);

    private static final Pattern USER_FILTER_PATTERN =
        Pattern
            .compile("(?s)\"{0,1}(" + Constants.FILTER_USER + '|' + Constants.FILTER_PATH_USER_GROUP_USER_ID
                + ")(\"*\\s*([=<>]+)\\s*\"*|\"*\\s*(any)\\s*\"*" + "|\"*\\s*(cql.any)\\s*\"*)"
                + "([^\\s\"\\(\\)]*)\"{0,1}");

    private static final int MAX_FIELD_LENGTH = 245;

    @Autowired
    @Qualifier("eSciDoc.core.aa.business.renderer.VelocityXmlUserGroupRenderer")
    private UserGroupRendererInterface renderer;

    @Autowired
    @Qualifier("persistence.EscidocRoleDao")
    private EscidocRoleDaoInterface roleDao;

    @Autowired
    @Qualifier("business.TripleStoreUtility")
    private TripleStoreUtility tripleStoreUtility;

    @Autowired
    @Qualifier("eSciDoc.core.aa.ObjectAttributeResolver")
    private ObjectAttributeResolver objectAttributeResolver;

    @Autowired
    @Qualifier("persistence.UserAccountDao")
    private UserAccountDaoInterface userAccountDao;

    @Autowired
    @Qualifier("persistence.UserGroupDao")
    private UserGroupDaoInterface userGroupDao;

    @Autowired
    @Qualifier("business.PolicyDecisionPoint")
    private PolicyDecisionPointInterface pdp;

    @Autowired
    @Qualifier("security.SecurityHelper")
    private SecurityHelper securityHelper;

    private static final String MSG_UNEXPECTED_EXCEPTION = "Unexpected exception in ";

    /**
     * See Interface for functional description.
     *
     * @see de.escidoc.core.aa.service.interfaces.UserGroupHandlerInterface #create(java.lang.String)
     */
    @Override
    public String create(final String xmlData) throws UniqueConstraintViolationException, XmlCorruptedException,
        SystemException {
        final ByteArrayInputStream in = XmlUtility.convertToByteArrayInputStream(xmlData);
        final StaxParser sp = new StaxParser();
        final GroupCreateUpdateHandler groupHandler = new GroupCreateUpdateHandler(sp);

        sp.addHandler(groupHandler);
        try {
            sp.parse(in);
            sp.clearHandlerChain();
        }
        catch (final Exception e) {
            XmlUtility.handleUnexpectedStaxParserException("", e);
        }

        final UserGroup userGroup = new UserGroup();

        setModificationValues(userGroup, groupHandler.getGroupProperties());
        setCreationValues(userGroup);
        // A created user group is in state active
        userGroup.setActive(Boolean.TRUE);
        userGroupDao.save(userGroup);
        return renderer.render(userGroup);
    }

    /**
     * See Interface for functional description.
     *
     * @see de.escidoc.core.aa.service.interfaces.UserGroupHandlerInterface #delete(java.lang.String)
     */
    @Override
    public void delete(final String groupId) throws SqlDatabaseSystemException, WebserverSystemException,
        UserGroupNotFoundException {
        final UserGroup userGroup = userGroupDao.retrieveUserGroup(groupId);

        if (userGroup == null) {
            final String message = "User group with id " + groupId + " does not exist.";
            throw new UserGroupNotFoundException(message);
        }
        userGroupDao.delete(userGroup);
        sendUserGroupUpdateEvent(groupId);
        sendUserGroupMemberUpdateEvent(groupId);
    }

    /**
     * See Interface for functional description.
     *
     * @see de.escidoc.core.aa.service.interfaces.UserGroupHandlerInterface #retrieve(java.lang.String)
     */
    @Override
    public String retrieve(final String groupId) throws SystemException, UserGroupNotFoundException {
        final UserGroup userGroup = userGroupDao.retrieveUserGroup(groupId);

        if (userGroup == null) {
            throw new UserGroupNotFoundException(StringUtility.format(MSG_GROUP_NOT_FOUND_BY_ID, groupId));
        }
        return renderer.render(userGroup);
    }

    /**
     * See Interface for functional description.
     *
     * @see de.escidoc.core.aa.service.interfaces.UserGroupHandlerInterface #update(java.lang.String, java.lang.String)
     */
    @Override
    public String update(final String groupId, final String xmlData) throws UniqueConstraintViolationException,
        XmlCorruptedException, MissingAttributeValueException, OptimisticLockingException, SystemException,
        UserGroupNotFoundException {

        final UserGroup userGroup = userGroupDao.retrieveUserGroup(groupId);

        if (userGroup == null) {
            final String message = "User group with id " + groupId + " does not exist.";
            throw new UserGroupNotFoundException(message);
        }

        final ByteArrayInputStream in = XmlUtility.convertToByteArrayInputStream(xmlData);
        final StaxParser sp = new StaxParser();

        final OptimisticLockingStaxHandler optimisticLockingHandler =
            new OptimisticLockingStaxHandler(userGroup.getLastModificationDate());
        sp.addHandler(optimisticLockingHandler);

        final GroupCreateUpdateHandler groupHandler = new GroupCreateUpdateHandler(sp);

        sp.addHandler(groupHandler);
        try {
            sp.parse(in);
            sp.clearHandlerChain();
        }
        catch (final OptimisticLockingException e) {
            throw e;
        }
        catch (final Exception e) {
            XmlUtility.handleUnexpectedStaxParserException("", e);
        }

        if (setModificationValues(userGroup, groupHandler.getGroupProperties())) {
            userGroupDao.save(userGroup);
        }
        return renderer.render(userGroup);
    }

    /**
     * See Interface for functional description.
     *
     * @see de.escidoc.core.aa.service.interfaces.UserGroupHandlerInterface #activate(java.lang.String,
     *      java.lang.String)
     */
    @Override
    public void activate(final String groupId, final String taskParam) throws AlreadyActiveException,
        XmlCorruptedException, MissingAttributeValueException, OptimisticLockingException, SystemException,
        UserGroupNotFoundException {
        final UserGroup userGroup = userGroupDao.retrieveUserGroup(groupId);

        if (userGroup == null) {
            throw new UserGroupNotFoundException(StringUtility.format(MSG_GROUP_NOT_FOUND_BY_ID, groupId));
        }

        // TODO: validation missing, check if needed or if it shall be skipped
        final de.escidoc.core.common.util.xml.stax.StaxParser sp =
            new de.escidoc.core.common.util.xml.stax.StaxParser(XmlUtility.NAME_PARAM);
        final OptimisticLockingStaxHandler optimisticLockingHandler =
            new OptimisticLockingStaxHandler(userGroup.getLastModificationDate());

        sp.addHandler(optimisticLockingHandler);
        try {
            sp.parse(XmlUtility.convertToByteArrayInputStream(taskParam));
        }
        catch (final InvalidXmlException e) {
            throw new XmlCorruptedException(e);
        }
        catch (LastModificationDateMissingException e) {
            throw new XmlCorruptedException(e);
        }
        catch (final OptimisticLockingException e) {
            throw e;
        }
        catch (final MissingAttributeValueException e) {
            throw e;
        }
        catch (final SystemException e) {
            throw e;
        }
        catch (final Exception e) {
            final String msg = MSG_UNEXPECTED_EXCEPTION + getClass().getName() + ".activate: " + e.getClass().getName();
            throw new SystemException(msg, e);
        }

        // check active flag and change value
        if (userGroup.getActive()) {
            throw new AlreadyActiveException("User group already active.");
        }
        try {
            setModificationValues(userGroup, null);
        }
        catch (final UniqueConstraintViolationException e) {
            if (LOGGER.isWarnEnabled()) {
                LOGGER.warn("Error on getting users for group.");
            }
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Error on setting modification values.", e);
            }
        }
        userGroup.setActive(Boolean.TRUE);
        userGroupDao.update(userGroup);
        sendUserGroupUpdateEvent(groupId);
        sendUserGroupMemberUpdateEvent(groupId);
    }

    /**
     * See Interface for functional description.
     *
     * @see de.escidoc.core.aa.service.interfaces.UserGroupHandlerInterface #deactivate(java.lang.String,
     *      java.lang.String)
     */
    @Override
    public void deactivate(final String groupId, final String taskParam) throws AlreadyDeactiveException,
        XmlCorruptedException, MissingAttributeValueException, OptimisticLockingException, SystemException,
        UserGroupNotFoundException {
        final UserGroup userGroup = userGroupDao.retrieveUserGroup(groupId);

        if (userGroup == null) {
            throw new UserGroupNotFoundException(StringUtility.format(MSG_GROUP_NOT_FOUND_BY_ID, groupId));
        }

        // TODO: validation missing, check if needed or if it shall be skipped
        final de.escidoc.core.common.util.xml.stax.StaxParser sp =
            new de.escidoc.core.common.util.xml.stax.StaxParser(XmlUtility.NAME_PARAM);
        final OptimisticLockingStaxHandler optimisticLockingHandler =
            new OptimisticLockingStaxHandler(userGroup.getLastModificationDate());

        sp.addHandler(optimisticLockingHandler);
        try {
            sp.parse(XmlUtility.convertToByteArrayInputStream(taskParam));
        }
        catch (final InvalidXmlException e) {
            throw new XmlCorruptedException(e);
        }
        catch (LastModificationDateMissingException e) {
            throw new XmlCorruptedException(e);
        }
        catch (final OptimisticLockingException e) {
            throw e;
        }
        catch (final MissingAttributeValueException e) {
            throw e;
        }
        catch (final SystemException e) {
            throw e;
        }
        catch (final Exception e) {
            final String msg = MSG_UNEXPECTED_EXCEPTION + getClass().getName() + ".activate: " + e.getClass().getName();
            throw new SystemException(msg, e);
        }

        // check active flag and change value
        if (!userGroup.getActive()) {
            throw new AlreadyDeactiveException("User group already deactive.");
        }
        try {
            setModificationValues(userGroup, null);
        }
        catch (final UniqueConstraintViolationException e) {
            if (LOGGER.isWarnEnabled()) {
                LOGGER.warn("Error on setting modification values.");
            }
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Error on setting modification values.", e);
            }
        }
        userGroup.setActive(Boolean.FALSE);
        userGroupDao.update(userGroup);
        sendUserGroupUpdateEvent(groupId);
        sendUserGroupMemberUpdateEvent(groupId);
    }

    /**
     * See Interface for functional description.
     *
     * @see de.escidoc.core.aa.service.interfaces.UserGroupHandlerInterface #createGrant(java.lang.String,
     *      java.lang.String)
     */
    @Override
    public String createGrant(final String groupId, final String grantXML) throws AlreadyExistsException,
        AuthenticationException, AuthorizationException, RoleNotFoundException, InvalidScopeException,
        UserGroupNotFoundException, XmlCorruptedException, SystemException {
        final UserGroup userGroup = userGroupDao.retrieveUserGroup(groupId);

        if (userGroup == null) {
            throw new UserGroupNotFoundException(StringUtility.format(MSG_GROUP_NOT_FOUND_BY_ID, groupId));
        }

        final RoleGrant grant = new RoleGrant();
        final ByteArrayInputStream in = XmlUtility.convertToByteArrayInputStream(grantXML);
        final de.escidoc.core.common.util.xml.stax.StaxParser sp =
            new de.escidoc.core.common.util.xml.stax.StaxParser(XmlUtility.NAME_GRANT);
        final LinkStaxHandler roleLinkHandler =
            new LinkStaxHandler(XPATH_GRANT_ROLE, XmlUtility.BASE_ROLE, RoleNotFoundException.class);

        sp.addHandler(roleLinkHandler);

        final LinkStaxHandler objectLinkHandler = new LinkStaxHandler(XPATH_GRANT_ASSIGNED_ON);

        sp.addHandler(objectLinkHandler);

        final GrantStaxHandler grantHandler = new GrantStaxHandler(grant);

        sp.addHandler(grantHandler);

        try {
            sp.parse(in);
        }
        catch (final InvalidXmlException e) {
            throw new XmlCorruptedException(e);
        }
        catch (final RoleNotFoundException e) {
            throw e;
        }
        catch (final SystemException e) {
            throw e;
        }
        catch (final Exception e) {
            final String msg =
                MSG_UNEXPECTED_EXCEPTION + getClass().getName() + ".createGrant: " + e.getClass().getName();
            throw new SystemException(msg, e);
        }

        final Date creationDate = new Date();

        grant.setCreationDate(creationDate);
        grant.setUserAccountByCreatorId(UserAccountHandler.getAuthenticatedUser(this.userAccountDao));

        final String roleId = roleLinkHandler.getObjid();
        final EscidocRole role = roleDao.retrieveRole(roleId);

        if (role == null) {
            throw new RoleNotFoundException(StringUtility.format("Role with provided id not found", roleId));
        }
        grant.setEscidocRole(role);

        final String objectId = objectLinkHandler.getObjid();

        // check if referenced object exists and determine its object type
        // it is assumed, that each resource stored in fedora has an object
        // type stored in the triple store.
        if (objectId != null) {
            final Map<String, String> objectAttributes;
            try {
                objectAttributes = objectAttributeResolver.resolveObjectAttributes(objectId);
            }
            catch (final Exception e) {
                throw new SystemException(e);
            }

            if (objectAttributes == null) {
                throw new XmlCorruptedException(StringUtility.format(MSG_GRANT_RESTRICTION_VIOLATED, objectId));
            }
            final String objectType = objectAttributes.get(ObjectAttributeResolver.ATTR_OBJECT_TYPE);
            String objectTitle = objectAttributes.get(ObjectAttributeResolver.ATTR_OBJECT_TITLE);

            // check if objectType may be scope
            boolean checkOk = false;
            if (role.getScopeDefs() != null && objectType != null) {
                for (final ScopeDef scopeDef : role.getScopeDefs()) {
                    if (scopeDef.getAttributeObjectType() != null
                        && scopeDef.getAttributeObjectType().equals(objectType)) {
                        checkOk = true;
                        break;
                    }
                }
            }
            if (!checkOk) {
                throw new InvalidScopeException("objectId " + objectId + " has objectType " + objectType
                    + " and may not be scope for role " + role.getRoleName());
            }

            // see issue 358. The title of an object stored in fedora may
            // not be explicitly stored in the triple store.
            // Therefore, a default title will be set, if it is null, here.
            if (objectTitle == null) {
                objectTitle = StringUtility.convertToUpperCaseLetterFormat(objectType) + " " + objectId;
            }
            else if (objectTitle.length() > MAX_FIELD_LENGTH) {
                objectTitle = objectTitle.substring(0, MAX_FIELD_LENGTH);
            }

            // get the href of the object.
            final String objectHref = tripleStoreUtility.getHref(objectType, objectId);

            // In case of REST it has to be checked if the provided href points
            // to the correct href.
            if (objectLinkHandler.getHref() != null && !objectLinkHandler.getHref().equals(objectHref)) {
                // FIXME: exception should be a resource not found exception
                // but this changes the interface. To prevent problems on
                // application side, currently an XmlCorruptedException is
                // thrown.
                throw new XmlCorruptedException(StringUtility.format(MSG_WRONG_HREF, objectLinkHandler.getHref(),
                    objectType));
            }

            // check if grant already exists
            if (userGroupDao.retrieveCurrentGrant(userGroup, role, objectId) != null) {
                throw new AlreadyExistsException(StringUtility.format("Grant already exists", groupId, role.getId(),
                    objectId));
            }

            // set object values in grant
            grant.setObjectId(objectId);
            grant.setObjectTitle(objectTitle);
            grant.setObjectHref(objectHref);
        }

        grant.setUserGroupByGroupId(userGroup);
        userGroupDao.save(grant);
        sendUserGroupUpdateEvent(groupId);
        return renderer.renderGrant(grant);
    }

    /**
     * See Interface for functional description.
     */
    @Override
    @Transactional(rollbackFor = { SystemException.class, RuntimeException.class })
    public String addSelectors(final String groupId, final String taskParam)
        throws OrganizationalUnitNotFoundException, UserAccountNotFoundException, UserGroupNotFoundException,
        InvalidContentException, MissingMethodParameterException, SystemException, AuthenticationException,
        AuthorizationException, OptimisticLockingException, XmlCorruptedException, UserGroupHierarchyViolationException {

        final UserGroup userGroup = userGroupDao.retrieveUserGroup(groupId);
        if (userGroup == null) {
            final String message = "User group with id " + groupId + " does not exist.";
            throw new UserGroupNotFoundException(message);
        }
        final ByteArrayInputStream in = XmlUtility.convertToByteArrayInputStream(taskParam);
        final StaxParser sp = new StaxParser();

        final OptimisticLockingStaxHandler optimisticLockingHandler =
            new OptimisticLockingStaxHandler(userGroup.getLastModificationDate());
        sp.addHandler(optimisticLockingHandler);

        final GroupSelectorsAddHandler groupHandler = new GroupSelectorsAddHandler(sp);
        sp.addHandler(groupHandler);
        try {
            sp.parse(in);
            sp.clearHandlerChain();
        }
        catch (final InvalidXmlException e) {
            throw new XmlCorruptedException(e);
        }
        catch (final Exception e) {
            XmlUtility.handleUnexpectedStaxParserException("", e);
        }
        final List<String[]> selectors = groupHandler.getGroupSelectors();
        final Set<UserGroupMember> existingMembers = userGroup.getMembers();
        final Collection<UserGroupMember> newMembers = new HashSet<UserGroupMember>();

        for (final String[] selector : selectors) {
            final UserGroupMember member = new UserGroupMember(userGroup);
            final String name = selector[0];
            final String type = selector[1];
            final String value = selector[2];
            if ("internal".equals(type)) {
                if (name.equals(XmlUtility.NAME_USER_ACCOUNT)) {
                    final UserAccount referencedUser = this.userAccountDao.retrieveUserAccountById(value);
                    if (referencedUser == null) {
                        final String message = StringUtility.format(MSG_USER_NOT_FOUND_BY_ID, value);
                        throw new UserAccountNotFoundException(message);
                    }
                }
                else if (name.equals(XmlUtility.NAME_USER_GROUP)) {
                    final UserGroup referencedUserGroup = userGroupDao.retrieveUserGroup(value);
                    if (referencedUserGroup == null) {
                        final String message = "Referenced user group with id " + value + " does not exist.";
                        throw new UserGroupNotFoundException(message);
                    }
                    if (!isCycleFree(groupId, value)) {
                        final String message =
                            "User group with id " + value + " can not become a member of the user group with id "
                                + groupId + "  because user group with id " + value
                                + " is already ranked higher in the group hierarchy.";
                        throw new UserGroupHierarchyViolationException(message);
                    }
                }
                else {
                    final String message = MSG_GROUP_INVALID_SELECTOR_NAME;
                    throw new XmlCorruptedException(message);
                }
            }
            boolean alreadyExist = false;
            for (final UserGroupMember existingMember : existingMembers) {
                if (name.equals(existingMember.getName()) && type.equals(existingMember.getType())
                    && value.equals(existingMember.getValue())) {
                    alreadyExist = true;
                    break;
                }
            }

            if (!alreadyExist) {
                member.setName(name);
                member.setType(type);
                member.setValue(value);
                newMembers.add(member);
            }
        }

        // save new Members
        for (final UserGroupMember newMember : newMembers) {
            userGroupDao.save(newMember);
            existingMembers.add(newMember);
        }
        sendUserGroupMemberUpdateEvent(groupId);
        return renderer.render(userGroup);
    }

    /**
     * See Interface for functional description.
     */
    @Override
    @Transactional(rollbackFor = { SystemException.class, RuntimeException.class })
    public String removeSelectors(final String groupId, final String taskParam) throws XmlCorruptedException,
        AuthenticationException, AuthorizationException, SystemException, UserGroupNotFoundException,
        OptimisticLockingException, MissingMethodParameterException, UserAccountNotFoundException,
        OrganizationalUnitNotFoundException {
        final UserGroup userGroup = userGroupDao.retrieveUserGroup(groupId);
        if (userGroup == null) {
            final String message = "User group with id " + groupId + " does not exist.";
            throw new UserGroupNotFoundException(message);
        }
        final ByteArrayInputStream in = XmlUtility.convertToByteArrayInputStream(taskParam);
        final StaxParser sp = new StaxParser();

        final OptimisticLockingStaxHandler optimisticLockingHandler =
            new OptimisticLockingStaxHandler(userGroup.getLastModificationDate());
        sp.addHandler(optimisticLockingHandler);

        final GroupSelectorsRemoveHandler groupHandler = new GroupSelectorsRemoveHandler();
        sp.addHandler(groupHandler);
        try {
            sp.parse(in);
            sp.clearHandlerChain();
        }
        catch (final InvalidXmlException e) {
            throw new XmlCorruptedException(e);
        }
        catch (final Exception e) {
            XmlUtility.handleUnexpectedStaxParserException("", e);
        }
        final List<String> membersToRemove = groupHandler.getMemberIdsToRemove();
        final Set<UserGroupMember> existingMembers = userGroup.getMembers();

        for (final String memberId : membersToRemove) {
            final Iterator<UserGroupMember> membersIterator = existingMembers.iterator();

            while (membersIterator.hasNext()) {
                final UserGroupMember existingMember = membersIterator.next();
                if (memberId.equals(existingMember.getId())) {
                    membersIterator.remove();
                    break;
                }
            }
        }
        sendUserGroupMemberUpdateEvent(groupId);
        return renderer.render(userGroup);

    }

    /**
     * See Interface for functional description.
     *
     * @param filter userGroupFilter
     * @return list of filtered user groups
     * @see de.escidoc.core.aa.service.interfaces.UserGroupHandlerInterface #retrieveUserGroups(java.util.Map)
     */
    @Override
    public String retrieveUserGroups(final Map<String, String[]> filter) throws InvalidSearchQueryException,
        SystemException {

        Map<String, String[]> castedFilter = filter;

        // check if filter for userId is provided
        // if yes, get groups for user and add ids to filter
        // then remove userId from filter
        castedFilter = fixCqlUserFilter(castedFilter);

        final SRURequestParameters parameters = new DbRequestParameters(castedFilter);

        final String query = parameters.getQuery();
        final int limit = parameters.getMaximumRecords();
        final int offset = parameters.getStartRecord();
        final boolean explain = parameters.isExplain();

        final String result;
        if (explain) {
            final Map<String, Object> values = new HashMap<String, Object>();

            values.put("PROPERTY_NAMES", new UserGroupFilter(null).getPropertyNames());
            result = ExplainXmlProvider.getInstance().getExplainUserGroupXml(values);
        }
        else if (limit == 0) {
            result = renderer.renderUserGroups(new ArrayList<UserGroup>(0), parameters.getRecordPacking());
        }
        else {
            final int currentLimit = offset + limit;
            int currentOffset = 0;
            final List<UserGroup> permittedUserGroups = new ArrayList<UserGroup>();
            final int size = permittedUserGroups.size();

            while (size <= currentLimit) {

                final List<UserGroup> tmpUserGroups =
                    userGroupDao.retrieveUserGroups(query, currentOffset, currentLimit);
                if (tmpUserGroups == null || tmpUserGroups.isEmpty()) {
                    break;
                }
                final List<String> ids = new ArrayList<String>(tmpUserGroups.size());
                for (final UserGroup userGroup : tmpUserGroups) {
                    ids.add(userGroup.getId());
                }

                try {
                    final List<String> tmpPermitted = pdp.evaluateRetrieve(XmlUtility.NAME_USER_GROUP, ids);
                    final int numberPermitted = tmpPermitted.size();
                    if (numberPermitted == 0) {
                        break;
                    }
                    else {
                        int permittedIndex = 0;
                        String currentPermittedId = tmpPermitted.get(permittedIndex);
                        for (final UserGroup userGroup : tmpUserGroups) {
                            if (currentPermittedId.equals(userGroup.getId())) {
                                permittedUserGroups.add(userGroup);
                                ++permittedIndex;
                                if (permittedIndex < numberPermitted) {
                                    currentPermittedId = tmpPermitted.get(permittedIndex);
                                }
                                else {
                                    break;
                                }
                            }
                        }
                    }
                }
                catch (final MissingMethodParameterException e) {
                    throw new SystemException("Unexpected exception during evaluating access " + "rights.", e);
                }
                catch (final ResourceNotFoundException e) {
                    throw new SystemException("Unexpected exception during evaluating access " + "rights.", e);
                }
                currentOffset += currentLimit;
            }

            final List<UserGroup> offsetUserGroups;
            final int numberPermitted = permittedUserGroups.size();
            if (offset < numberPermitted) {
                offsetUserGroups = new ArrayList<UserGroup>(limit);
                for (int i = offset; i < numberPermitted && i < currentLimit; i++) {
                    offsetUserGroups.add(permittedUserGroups.get(i));
                }
            }
            else {
                offsetUserGroups = new ArrayList<UserGroup>(0);
            }
            result = renderer.renderUserGroups(offsetUserGroups, parameters.getRecordPacking());
        }
        return result;
    }

    /**
     * replaces user-id-filter with resolved groupIds.
     *
     * @param filter cql-filter
     * @return Map with replaced cql-query (userId replaced with groupIds)
     * @throws InvalidSearchQueryException e
     * @throws SystemException             e
     */
    private Map<String, String[]> fixCqlUserFilter(final Map<String, String[]> filter)
        throws InvalidSearchQueryException, SystemException {
        Map<String, String[]> returnFilter = filter;
        final Object[] queryPartsObject = filter.get(Constants.SRU_PARAMETER_QUERY);
        if (queryPartsObject != null) {
            final String[] queryParts = new String[queryPartsObject.length];
            for (int i = 0; i < queryPartsObject.length; i++) {
                if (queryPartsObject[i] != null) {
                    queryParts[i] = queryPartsObject[i].toString();
                }
            }
            boolean userFilterFound = false;
            for (int i = 0; i < queryParts.length; i++) {
                final Matcher matcher = USER_FILTER_PATTERN.matcher(queryParts[i]);
                if (matcher.find()) {
                    userFilterFound = true;
                    final Matcher userFilterMatcher = USER_FILTER_PATTERN.matcher(queryParts[i]);
                    final StringBuffer result = new StringBuffer("");
                    while (userFilterMatcher.find()) {
                        if (userFilterMatcher.group(6).matches(".*?%.*")) {
                            throw new InvalidSearchQueryException("Wildcards not allowed in user-filter");
                        }
                        if (userFilterMatcher.group(3) != null && userFilterMatcher.group(3).matches(">|<|<=|>=|<>")
                            || userFilterMatcher.group(4) != null || userFilterMatcher.group(5) != null) {
                            throw new InvalidSearchQueryException("non-supported relation in user-filter");
                        }
                        final StringBuilder replacement = new StringBuilder(" (");
                        try {
                            // get groups for user
                            final Set<String> groupIds = retrieveGroupsForUser(userFilterMatcher.group(6));

                            // write group-cql-query
                            // and replace user-expression with it.
                            if (groupIds != null && !groupIds.isEmpty()) {
                                for (final String groupId : groupIds) {
                                    if (replacement.length() > 2) {
                                        replacement.append(" or ");
                                    }
                                    replacement.append('\"');
                                    replacement.append(Constants.FILTER_PATH_ID);
                                    replacement.append("\"=").append(groupId).append(' ');
                                }
                            }
                            else {
                                throw new UserAccountNotFoundException("");
                            }
                        }
                        catch (final UserAccountNotFoundException e) {
                            if (LOGGER.isWarnEnabled()) {
                                LOGGER.warn("Error on getting user account.");
                            }
                            if (LOGGER.isDebugEnabled()) {
                                LOGGER.debug("Error on getting user account.", e);
                            }
                            // if user has no groups or user not found,
                            // write nonexisting group in query
                            replacement.append('\"');
                            replacement.append(Constants.FILTER_PATH_ID);
                            replacement.append("\"=").append("nonexistinggroup").append(' ');
                        }

                        replacement.append(") ");
                        userFilterMatcher.appendReplacement(result, replacement.toString());
                    }
                    userFilterMatcher.appendTail(result);
                    queryParts[i] = result.toString();
                }
            }
            if (userFilterFound) {
                final Map<String, String[]> filter1 = new HashMap<String, String[]>();
                for (final Entry<String, String[]> entry : filter.entrySet()) {
                    if (entry.getValue() != null) {
                        // noinspection RedundantCast
                        filter1.put(entry.getKey(), new String[((Object[]) entry.getValue()).length]);
                        // noinspection RedundantCast
                        for (int j = 0; j < ((Object[]) entry.getValue()).length; j++) {
                            // noinspection RedundantCast
                            filter1.get(entry.getKey())[j] = ((Object[]) entry.getValue())[j].toString();
                        }
                    }
                    else {
                        filter1.put(entry.getKey(), null);
                    }
                }
                filter1.put(Constants.SRU_PARAMETER_QUERY, queryParts);
                returnFilter = filter1;
            }
        }
        return returnFilter;
    }

    /**
     * Checks if one user group can become a member of another userGroup without creating a cycle in the user group
     * hierarchy.
     *
     * @param userGroupId       id of the user group, the member should be added to
     * @param memberCandidateId id of the member candidate user group
     * @return true if the user group hierarchy will not be violated false if the user group hierarchy will be violated
     * @throws de.escidoc.core.common.exceptions.system.SqlDatabaseSystemException
     */
    private boolean isCycleFree(final String userGroupId, final String memberCandidateId)
        throws SqlDatabaseSystemException {
        if (userGroupId != null && userGroupId.equals(memberCandidateId)) {
            return false;
        }
        final Map<String, Object> criteria = new HashMap<String, Object>();
        criteria.put(Constants.FILTER_PATH_TYPE, "internal");
        criteria.put(Constants.FILTER_PATH_NAME, XmlUtility.NAME_USER_GROUP);
        final Set<String> filterPathValue = new HashSet<String>();
        filterPathValue.add(userGroupId);
        criteria.put(Constants.FILTER_PATH_VALUE, filterPathValue);
        boolean proceed = true;
        while (proceed) {
            final Collection<String> superMembers = new HashSet<String>();
            final List<UserGroupMember> userGroupMembers = userGroupDao.retrieveUserGroupMembers(criteria);
            if (userGroupMembers != null && !userGroupMembers.isEmpty()) {
                for (final UserGroupMember userGroupMember : userGroupMembers) {
                    final String id = userGroupMember.getUserGroup().getId();
                    if (memberCandidateId.equals(id)) {
                        return false;
                    }
                    else {
                        superMembers.add(userGroupMember.getUserGroup().getId());
                    }
                }
                criteria.put(Constants.FILTER_PATH_VALUE, superMembers);

            }
            else {
                proceed = false;
            }
        }
        return true;
    }

    /**
     * See Interface for functional description.
     *
     * @return set of groupIds (hierarchy)
     * @see de.escidoc.core.aa.service.interfaces.UserGroupHandlerInterface #retrieveGroupsForUser(java.lang.String)
     */
    @Override
    public Set<String> retrieveGroupsForUser(final String userId) throws UserAccountNotFoundException, SystemException {

        return retrieveGroupsForUser(userId, false);

    }

    /**
     * See Interface for functional description.
     *
     * @return set of groupIds (hierarchy)
     * @see de.escidoc.core.aa.service.interfaces.UserGroupHandlerInterface #retrieveGroupsForUser(java.lang.String,
     *      boolean)
     */
    @Override
    public Set<String> retrieveGroupsForUser(final String userId, final boolean activeOnly)
        throws UserAccountNotFoundException, SystemException {
        // may not return null, so return empty list!!
        Set<String> userGroups = new HashSet<String>();
        // Try getting the userAccount
        final UserAccount userAccount = userAccountDao.retrieveUserAccount(userId);
        if (userAccount == null) {
            throw new UserAccountNotFoundException(StringUtility.format(MSG_USER_NOT_FOUND_BY_ID, userId));
        }

        // Get groups the user is integrated via his userId
        final Collection<String> userIds = new HashSet<String>();
        userIds.add(userId);
        userGroups.addAll(retrieveGroupsByUserIds(userIds, activeOnly));

        // Get groups the user is integrated via one of his user attributes
        // check if attribute is ou-attribute. Then resolve path-list
        final String ouAttributeName =
            EscidocConfiguration.getInstance().get(EscidocConfiguration.ESCIDOC_CORE_AA_OU_ATTRIBUTE_NAME);

        final Set<UserAttribute> attributes = userAccount.getUserAttributesByUserId();

        final Map<String, Set<UserGroupMember>> groupMemberMap = retrieveGroupMembersWithAttributeSelector();

        if (attributes != null && groupMemberMap != null) {
            // iterate attributes of user
            for (final UserAttribute attribute : attributes) {
                // check for groupMembers with name=user-attribute-name
                final Set<UserGroupMember> groupMembers = groupMemberMap.get(attribute.getName());

                if (groupMembers != null) {
                    List<String> pathList = null;
                    if (ouAttributeName != null && ouAttributeName.length() != 0
                        && attribute.getName().equals(ouAttributeName)) {
                        final List<String> initialList = new ArrayList<String>();
                        initialList.add(attribute.getValue());
                        pathList = getOrgUnitPathList(attribute.getValue(), initialList);
                    }
                    for (final UserGroupMember groupMember : groupMembers) {
                        if ((attribute.getValue().equals(groupMember.getValue()) || pathList != null
                            && pathList.contains(groupMember.getValue()))
                            && (!activeOnly || Boolean.TRUE.equals(groupMember.getUserGroup().getActive()))) {
                            userGroups.add(groupMember.getUserGroup().getId());
                        }
                    }
                }
            }
        }

        // Get groups the user is integrated via integrated groups
        userGroups = retrieveGroupsByGroupIds(userGroups, activeOnly);

        return userGroups;
    }

    /**
     * See Interface for functional description.
     *
     * @return set of groupIds (hierarchy)
     * @see de.escidoc.core.aa.service.interfaces.UserGroupHandlerInterface #retrieveGroupsForGroup(java.lang.String)
     */
    @Override
    public Set<String> retrieveGroupsForGroup(final String groupId) throws SqlDatabaseSystemException {

        Set<String> userGroups = new HashSet<String>();
        if (groupId != null) {
            userGroups.add(groupId);
            // Get groups the group is integrated (hierarchically)
            userGroups = retrieveGroupsByGroupIds(userGroups, false);
        }

        return userGroups;
    }

    /**
     * Retrieves a list of userGroupIds by querying for userId-selector.
     *
     * @param userIds
     * @param activeOnly if true, only return active groups
     * @return HashSet userGroupIds
     * @throws UserAccountNotFoundException e
     * @throws SqlDatabaseSystemException   e
     */
    private Collection<String> retrieveGroupsByUserIds(final Collection<String> userIds, final boolean activeOnly)
        throws SqlDatabaseSystemException {
        final Set<String> userGroupIds = new HashSet<String>();

        if (userIds != null && !userIds.isEmpty()) {
            // retrieve all groupMembers that are of type
            // user-account
            // and have one of the userIds as value
            final Map<String, Object> criteria = new HashMap<String, Object>();
            criteria.put(Constants.FILTER_PATH_TYPE, "internal");
            criteria.put(Constants.FILTER_PATH_NAME, "user-account");
            criteria.put(Constants.FILTER_PATH_VALUE, userIds);
            final List<UserGroupMember> userGroupMembers = userGroupDao.retrieveUserGroupMembers(criteria);
            if (userGroupMembers != null) {
                for (final UserGroupMember userGroupMember : userGroupMembers) {
                    if ((!activeOnly || Boolean.TRUE.equals(userGroupMember.getUserGroup().getActive()))
                        && !userGroupIds.contains(userGroupMember.getUserGroup().getId())) {
                        userGroupIds.add(userGroupMember.getUserGroup().getId());
                    }
                }
            }
        }

        return userGroupIds;
    }

    /**
     * Retrieves a list of user group members by querying for attribute-selector.
     *
     * @return Map key=attribute name, value=list of user group members
     * @throws SqlDatabaseSystemException e
     */
    private Map<String, Set<UserGroupMember>> retrieveGroupMembersWithAttributeSelector()
        throws SqlDatabaseSystemException {
        final Map<String, Set<UserGroupMember>> result = new HashMap<String, Set<UserGroupMember>>();
        final Map<String, Object> criteria = new HashMap<String, Object>();

        criteria.put(Constants.FILTER_PATH_TYPE, "user-attribute");

        final List<UserGroupMember> userGroupMembers = userGroupDao.retrieveUserGroupMembers(criteria);

        if (userGroupMembers != null) {
            for (final UserGroupMember userGroupMember : userGroupMembers) {
                final String attributeName = userGroupMember.getName();
                Set<UserGroupMember> userGroupMemberSet = result.get(attributeName);

                if (userGroupMemberSet == null) {
                    userGroupMemberSet = new HashSet<UserGroupMember>();
                    result.put(attributeName, userGroupMemberSet);
                }
                userGroupMemberSet.add(userGroupMember);
            }
        }
        return result;
    }

    /**
     * Retrieves a list of userGroupIds by querying for groupId-selector.
     *
     * @param userGroupIds
     * @param activeOnly if true, only retrun active groups.
     * @return HashSet userGroupIds
     * @throws SqlDatabaseSystemException e
     */
    private Set<String> retrieveGroupsByGroupIds(final Set<String> userGroupIds, final boolean activeOnly)
        throws SqlDatabaseSystemException {
        if (userGroupIds != null && !userGroupIds.isEmpty()) {
            // retrieve all groupMembers that are of type
            // user-group and have one of the groupIds as value
            // plus: resolve complete group-hierarchy
            final Map<String, Object> criteria = new HashMap<String, Object>();
            criteria.put(Constants.FILTER_PATH_TYPE, "internal");
            criteria.put(Constants.FILTER_PATH_NAME, "user-group");
            criteria.put(Constants.FILTER_PATH_VALUE, userGroupIds);
            boolean proceed = true;
            while (proceed) {
                final List<UserGroupMember> userGroupMembers = userGroupDao.retrieveUserGroupMembers(criteria);
                final Collection<String> superMembers = new HashSet<String>();
                if (userGroupMembers != null && !userGroupMembers.isEmpty()) {
                    for (final UserGroupMember userGroupMember : userGroupMembers) {
                        if (!activeOnly || Boolean.TRUE.equals(userGroupMember.getUserGroup().getActive())) {
                            superMembers.add(userGroupMember.getUserGroup().getId());
                            if (!userGroupIds.contains(userGroupMember.getUserGroup().getId())) {
                                userGroupIds.add(userGroupMember.getUserGroup().getId());
                            }
                        }
                    }
                }
                if (superMembers.isEmpty()) {
                    proceed = false;
                }
                else {
                    criteria.put(Constants.FILTER_PATH_VALUE, superMembers);
                }
            }
        }

        return userGroupIds;
    }

    /**
     * Compute the pathes of the actual organizaional unit.
     *
     * @param orgUnitId the orgUnitId where pathList has to get retrieved.
     * @param totalList total list of all Parents.
     * @return List of ancestor-orgUnits
     * @throws SystemException If anything fails while computing the pathes.
     */
    private List<String> getOrgUnitPathList(final String orgUnitId, final List<String> totalList)
        throws SystemException {

        List<String> addableList = totalList;
        final List<String> orgUnitIds = this.tripleStoreUtility.getParents(orgUnitId);
        if (orgUnitIds != null && !orgUnitIds.isEmpty()) {
            addableList.addAll(orgUnitIds);
            for (final String parentOrgUnitId : orgUnitIds) {
                addableList = getOrgUnitPathList(parentOrgUnitId, addableList);
            }
        }
        return addableList;
    }

    /**
     * See Interface for functional description.
     *
     * @see de.escidoc.core.aa.service.interfaces.UserGroupHandlerInterface #retrieveCurrentGrantsAsMap(java.lang.String)
     */
    @Override
    public Map<String, Map<String, List<RoleGrant>>> retrieveCurrentGrantsAsMap(final String groupId)
        throws UserGroupNotFoundException, SqlDatabaseSystemException {

        final UserGroup userGroup = userGroupDao.retrieveUserGroup(groupId);
        if (userGroup == null) {
            throw new UserGroupNotFoundException(StringUtility.format(MSG_GROUP_NOT_FOUND_BY_ID, groupId));
        }
        final List<RoleGrant> currentGrants = fetchCurrentGrants(groupId);
        if (currentGrants == null || currentGrants.isEmpty()) {
            return null;
        }

        final Map<String, Map<String, List<RoleGrant>>> ret = new HashMap<String, Map<String, List<RoleGrant>>>();
        for (final RoleGrant grant : currentGrants) {
            final String roleId = grant.getEscidocRole().getId();
            Map<String, List<RoleGrant>> grantsOfRole = ret.get(roleId);
            if (grantsOfRole == null) {
                grantsOfRole = new HashMap<String, List<RoleGrant>>();
                ret.put(roleId, grantsOfRole);
            }
            String key = grant.getObjectId();
            if (key == null) {
                // For grants of unlimited roles an empty string is used
                // as the key.
                key = "";
            }
            List<RoleGrant> grantsOfObject = grantsOfRole.get(key);
            if (grantsOfObject == null) {
                grantsOfObject = new ArrayList<RoleGrant>();
                grantsOfRole.put(key, grantsOfObject);
            }
            grantsOfObject.add(grant);
        }
        return ret;
    }

    /**
     * See Interface for functional description.
     *
     * @return List with maps for groupGrants
     * @see de.escidoc.core.aa.service.interfaces.UserGroupHandlerInterface #retrieveCurrentGrantsAsMap(List)
     */
    @Override
    public Map<String, Map<String, Map<String, List<RoleGrant>>>> retrieveManyCurrentGrantsAsMap(
        final List<String> groupIds) throws SqlDatabaseSystemException {

        final Map<String, List<RoleGrant>> currentGrantsForGroups = userGroupDao.retrieveCurrentGrants(groupIds);
        if (currentGrantsForGroups == null || currentGrantsForGroups.isEmpty()) {
            return null;
        }

        final Map<String, Map<String, Map<String, List<RoleGrant>>>> ret =
            new HashMap<String, Map<String, Map<String, List<RoleGrant>>>>();
        for (final Entry<String, List<RoleGrant>> entry : currentGrantsForGroups.entrySet()) {
            if (entry.getValue() == null) {
                continue;
            }
            final Map<String, Map<String, List<RoleGrant>>> currentGrantsForOneGroup =
                new HashMap<String, Map<String, List<RoleGrant>>>();
            for (final RoleGrant grant : entry.getValue()) {
                final String roleId = grant.getRoleId();
                Map<String, List<RoleGrant>> grantsOfRole = currentGrantsForOneGroup.get(roleId);
                if (grantsOfRole == null) {
                    grantsOfRole = new HashMap<String, List<RoleGrant>>();
                    currentGrantsForOneGroup.put(roleId, grantsOfRole);
                }
                String key = grant.getObjectId();
                if (key == null) {
                    // For grants of unlimited roles an empty string is used
                    // as the key.
                    key = "";
                }
                List<RoleGrant> grantsOfObject = grantsOfRole.get(key);
                if (grantsOfObject == null) {
                    grantsOfObject = new ArrayList<RoleGrant>();
                    grantsOfRole.put(key, grantsOfObject);
                }
                grantsOfObject.add(grant);
            }
            ret.put(entry.getKey(), currentGrantsForOneGroup);
        }

        return ret;
    }

    /**
     * See Interface for functional description.
     *
     * @see de.escidoc.core.aa.service.interfaces.UserGroupHandlerInterface #retrieveCurrentGrants(java.lang.String)
     */
    @Override
    public String retrieveCurrentGrants(final String groupId) throws UserGroupNotFoundException, SystemException {

        final UserGroup userGroup = userGroupDao.retrieveUserGroup(groupId);
        if (userGroup == null) {
            throw new UserGroupNotFoundException(StringUtility.format(MSG_GROUP_NOT_FOUND_BY_ID, groupId));
        }
        final List<RoleGrant> currentGrants = fetchCurrentGrants(groupId);
        final HashMap<String, RoleGrant> grantsMap = new HashMap<String, RoleGrant>();
        final List<Object[]> argumentList = new ArrayList<Object[]>();
        final List<RoleGrant> filteredCurrentGrants = new ArrayList<RoleGrant>();

        // AA-filter
        for (final RoleGrant roleGrant : currentGrants) {
            grantsMap.put(roleGrant.getId(), roleGrant);
            final Object[] args = { groupId, roleGrant.getId() };
            argumentList.add(args);
        }
        try {
            final List<Object[]> returnList = pdp.evaluateMethodForList("user-group", "retrieveGrant", argumentList);
            for (final Object[] obj : returnList) {
                filteredCurrentGrants.add(grantsMap.get(obj[1]));
            }
        }
        catch (final MissingMethodParameterException e) {
            throw new SystemException("Unexpected exception " + "during evaluating access rights.", e);
        }
        catch (final ResourceNotFoundException e) {
            throw new SystemException("Unexpected exception " + "during evaluating access rights.", e);
        }

        return renderer.renderCurrentGrants(userGroup, filteredCurrentGrants);
    }

    /**
     * See Interface for functional description.
     *
     * @see de.escidoc.core.aa.service.interfaces.UserGroupHandlerInterface #retrieveCurrentGrants(java.lang.String)
     */
    @Override
    public String retrieveGrant(final String groupId, final String grantId) throws UserGroupNotFoundException,
        GrantNotFoundException, MissingMethodParameterException, AuthenticationException, AuthorizationException,
        SystemException {
        final UserGroup userGroup = userGroupDao.retrieveUserGroup(groupId);

        if (userGroup == null) {
            throw new UserGroupNotFoundException(StringUtility.format(MSG_GROUP_NOT_FOUND_BY_ID, groupId));
        }
        boolean isOwned = false;
        final List<RoleGrant> grants = userGroupDao.retrieveGrants(groupId);
        RoleGrant grantToRetrieve = null;
        for (final RoleGrant grant : grants) {
            if (grant.getId().equals(grantId)) {
                grantToRetrieve = grant;
                isOwned = true;
                break;

            }
        }
        if (!isOwned) {
            throw new GrantNotFoundException("Grant with id " + grantId + " is no grant of user group " + groupId);
        }

        return renderer.renderGrant(grantToRetrieve);
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
        return renderer.renderResources(userGroupDao.retrieveUserGroup(groupId));
    }

    /**
     * See Interface for functional description.
     *
     * @param grantId   grantId
     * @param taskParam taskParam
     * @see de.escidoc.core.aa.service.interfaces.UserGroupHandlerInterface #revokeGrant(java.lang.String,
     *      java.lang.String)
     */
    @Override
    public void revokeGrant(final String groupId, final String grantId, final String taskParam)
        throws UserGroupNotFoundException, GrantNotFoundException, AlreadyRevokedException, XmlCorruptedException,
        MissingAttributeValueException, MissingMethodParameterException, AuthenticationException,
        AuthorizationException, SystemException {

        final UserGroup userGroup = userGroupDao.retrieveUserGroup(groupId);

        if (userGroup == null) {
            throw new UserGroupNotFoundException(StringUtility.format(MSG_GROUP_NOT_FOUND_BY_ID, groupId));
        }
        boolean isOwned = false;
        final List<RoleGrant> grants = userGroupDao.retrieveGrants(groupId);
        RoleGrant grantToRevoke = null;
        for (final RoleGrant grant : grants) {
            if (grant.getId().equals(grantId)) {
                grantToRevoke = grant;
                isOwned = true;
                break;

            }
        }
        if (!isOwned) {
            throw new GrantNotFoundException("Grant with id " + grantId + " is no current grant of user group "
                + groupId);
        }

        final de.escidoc.core.common.util.xml.stax.StaxParser sp =
            new de.escidoc.core.common.util.xml.stax.StaxParser(XmlUtility.NAME_PARAM);
        final GrantStaxHandler grantHandler = new GrantStaxHandler(grantToRevoke);

        sp.addHandler(grantHandler);

        final RevokeStaxHandler revokeStaxHandler = new RevokeStaxHandler(grantToRevoke, this.userAccountDao);

        sp.addHandler(revokeStaxHandler);

        try {
            sp.parse(XmlUtility.convertToByteArrayInputStream(taskParam));
        }
        catch (final InvalidXmlException e) {
            throw new XmlCorruptedException(e);
        }
        catch (final MissingAttributeValueException e) {
            throw e;
        }
        catch (final SystemException e) {
            throw e;
        }
        catch (final Exception e) {
            final String msg = MSG_UNEXPECTED_EXCEPTION + getClass().getName() + ".parse: " + e.getClass().getName();
            throw new SystemException(msg, e);
        }
        userGroupDao.update(grantToRevoke);
        sendUserGroupUpdateEvent(groupId);
    }

    /**
     * See Interface for functional description.
     *
     * @see de.escidoc.core.aa.service.interfaces.UserGroupHandlerInterface #revokeGrants(java.lang.String,
     *      java.lang.String)
     */
    @Override
    @Transactional(rollbackFor = { SystemException.class, RuntimeException.class })
    @edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "WMI_WRONG_MAP_ITERATOR")
    public void revokeGrants(final String groupId, final String filterXML) throws UserGroupNotFoundException,
        GrantNotFoundException, AlreadyRevokedException, XmlCorruptedException, MissingAttributeValueException,
        SystemException, AuthorizationException {
        // check if user group exists
        final UserGroup userGroup = userGroupDao.retrieveUserGroup(groupId);
        if (userGroup == null) {
            throw new UserGroupNotFoundException(StringUtility.format(MSG_GROUP_NOT_FOUND_BY_ID, groupId));
        }
        // get all current grants of user group
        final List<RoleGrant> grants = fetchCurrentGrants(groupId);
        // build HashMap with grantId
        final HashMap<String, RoleGrant> grantsHash = new HashMap<String, RoleGrant>();

        for (final RoleGrant grant : grants) {
            grantsHash.put(grant.getId(), grant);
        }

        // Parse taskParam
        final StaxParser fp = new StaxParser();
        final TaskParamHandler tph = new TaskParamHandler(fp);

        tph.setCheckLastModificationDate(false);
        fp.addHandler(tph);

        final FilterHandler fh = new FilterHandler(fp);

        fp.addHandler(fh);
        try {
            fp.parse(new ByteArrayInputStream(filterXML.getBytes(XmlUtility.CHARACTER_ENCODING)));
        }
        catch (final InvalidContentException e) {
            throw new XmlCorruptedException(e);
        }
        catch (final Exception e) {
            XmlUtility.handleUnexpectedStaxParserException("", e);
        }

        final Map<String, Object> filters = fh.getRules();
        final Collection<String> grantIds;

        if (filters.isEmpty()) {
            // if no filters are provided, remove all current grants
            grantIds = new HashSet<String>();
            for (final String grantId : grantsHash.keySet()) {
                grantIds.add(grantId);
            }
        }
        else {
            // get ids of grants to revoke
            grantIds = (Collection<String>) filters.get(Constants.DC_IDENTIFIER_URI);
        }

        if (grantIds == null || grantIds.isEmpty()) {
            return;
        }

        // check if all grants that shall get revoked are currentGrants
        for (final String grantId : grantIds) {
            if (!grantsHash.containsKey(grantId)) {
                throw new GrantNotFoundException("Grant with id " + grantId + " is no current grant of user group "
                    + groupId);
            }
        }

        // AA-filter grants to revoke
        final List<Object[]> argumentList = new ArrayList<Object[]>();
        for (final String grantId : grantIds) {
            final Object[] args = { groupId, grantId };
            argumentList.add(args);
        }
        try {
            final List<Object[]> returnList = pdp.evaluateMethodForList("user-group", "revokeGrant", argumentList);
            if (returnList.size() < grantIds.size()) {
                // user is not allowed to revoke at least one of the grants
                // so throw AuthorizationException
                throw new AuthorizationException("You are not allowed to revoke at least "
                    + "one of the specified grants");
            }
        }
        catch (final MissingMethodParameterException e) {
            throw new SystemException("Unexpected exception " + "during evaluating access rights.", e);
        }
        catch (final ResourceNotFoundException e) {
            throw new SystemException("Unexpected exception " + "during evaluating access rights.", e);
        }

        final UserAccount authenticateUser = UserAccountHandler.getAuthenticatedUser(this.userAccountDao);

        for (final String grantId : grantIds) {
            final RoleGrant roleGrant = grantsHash.get(grantId);
            // set revoke-date, -user and -remark
            roleGrant.setUserAccountByRevokerId(authenticateUser);
            roleGrant.setRevocationDate(new Date());
            roleGrant.setRevocationRemark(tph.getRevokationRemark());
            // update grant
            userGroupDao.update(roleGrant);
        }
        sendUserGroupUpdateEvent(groupId);
    }

    /**
     * Fetches the current grants of the user group identified by the provided id.
     *
     * @param groupId id of the user group
     * @return Returns a {@code List} containing the grants of the user group that are currently valid. If the user
     *         group does not have a grant, an empty {@code List} is returned.
     * @throws SqlDatabaseSystemException Thrown in case of an internal database error.
     */
    private List<RoleGrant> fetchCurrentGrants(final String groupId) throws SqlDatabaseSystemException {
        final List<RoleGrant> grants = userGroupDao.retrieveGrants(groupId);
        final List<RoleGrant> result = new ArrayList<RoleGrant>(grants.size());

        for (final RoleGrant grant : grants) {
            if (grant.getRevocationDate() == null) {
                result.add(grant);
            }
        }
        return result;
    }

    /**
     * Sends userGroupUpdateEvent to AA.
     *
     * @param groupId The id of the updated user group.
     * @throws WebserverSystemException Thrown in case of an internal error.
     */
    private void sendUserGroupUpdateEvent(final String groupId) {

        securityHelper.clearGroupPoliciesCaches(groupId);
    }

    /**
     * Sends userGroupMemberUpdateEvent to AA.
     *
     * @param groupId The id of the updated user group.
     * @throws WebserverSystemException Thrown in case of an internal error.
     */
    private void sendUserGroupMemberUpdateEvent(final String groupId) {

        securityHelper.clearUserGroups();
    }

    /**
     * Sets the creation date and the created-by user in the provided {@code UserGroup} object.<br/> The values are
     * set with the values of modification date and modifying user of the provided user group.<br/> Before calling this
     * method, the last modification date and the modifying user must be set.
     *
     * @param userGroup The {@code UserGroup} object to modify.
     * @throws SystemException Thrown in case of an internal error.
     */
    private static void setCreationValues(final UserGroup userGroup) {

        // initialize creation-date value
        userGroup.setCreationDate(userGroup.getLastModificationDate());

        // initialize created-by values
        userGroup.setCreatorId(userGroup.getModifiedById());
    }

    /**
     * Sets the last modification date, the modified-by user and all values from the given properties map in the
     * provided {@code UserGroup} object. <br/> The last modification date is set to the current time, and the
     * modified by user to the user account of the current, authenticated user.
     *
     * @param userGroup       The {@code UserGroup} object to modify.
     * @param groupProperties map which contains all properties of the user group
     * @return true if the modification values were changed
     * @throws UniqueConstraintViolationException
     *          The label of the given user group has already been used.
     * @throws de.escidoc.core.common.exceptions.system.WebserverSystemException
     * @throws de.escidoc.core.common.exceptions.system.SqlDatabaseSystemException
     */
    private boolean setModificationValues(final UserGroup userGroup, final Map<String, String> groupProperties)
        throws UniqueConstraintViolationException, SqlDatabaseSystemException, WebserverSystemException {
        boolean changed = false;
        if (groupProperties != null) {
            final String description = groupProperties.get(Elements.ELEMENT_DESCRIPTION);
            if (description != null
                && (userGroup.getDescription() != null && description.equals(userGroup.getDescription()) || userGroup
                    .getDescription() == null)) {
                userGroup.setDescription(description);
                changed = true;
            }

            final String oldLabel = userGroup.getLabel();
            final String label = groupProperties.get("label");
            if (checkLabelUnique(label)) {
                changed = true;
                userGroup.setLabel(label);
            }
            else {
                if (oldLabel == null || !oldLabel.equals(label)) {
                    final String message = "The provided user group label is not unique.";
                    throw new UniqueConstraintViolationException(message);
                }
            }

            final String name = groupProperties.get(Elements.ELEMENT_NAME);
            if (userGroup.getName() == null || userGroup.getName() != null && !name.equals(userGroup.getName())) {
                userGroup.setName(name);
                changed = true;
            }

            final String email = groupProperties.get("email");
            if (email != null
                && (userGroup.getEmail() != null && email.equals(userGroup.getEmail()) || userGroup.getEmail() == null)) {
                userGroup.setEmail(email);
                changed = true;
            }

            final String type = groupProperties.get(Elements.ELEMENT_TYPE);
            if (type != null
                && (userGroup.getType() != null && type.equals(userGroup.getType()) || userGroup.getType() == null)) {
                userGroup.setType(type);
                changed = true;
            }

            if (changed) {
                userGroup.setLastModificationDate(new Date(System.currentTimeMillis()));
                userGroup.setModifiedById(UserAccountHandler.getAuthenticatedUser(this.userAccountDao));
            }
        }
        else {
            // caller is activate() or deactivate()
            userGroup.setLastModificationDate(new Date(System.currentTimeMillis()));
            userGroup.setModifiedById(UserAccountHandler.getAuthenticatedUser(this.userAccountDao));
        }
        return changed;
    }

    /**
     * Check if the given label is already used as user group name in the database.
     *
     * @param label user group name
     * @return true if the label is still unused
     * @throws SqlDatabaseSystemException Thrown in case of an internal database error.
     */
    private boolean checkLabelUnique(final String label) throws SqlDatabaseSystemException {
        return userGroupDao.findUsergroupByLabel(label) == null;
    }

}
