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
import java.io.IOException;
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
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.escidoc.core.aa.business.cache.PoliciesCache;
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
import de.escidoc.core.common.util.logger.AppLogger;
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
 * @spring.bean id="business.UserGroupHandler"
 * @author sche
 * @aa
 */
public class UserGroupHandler implements UserGroupHandlerInterface {
    /**
     * The logger.
     */
    private static final AppLogger LOG = new AppLogger(
        UserGroupHandler.class.getName());

    private static final Pattern USER_FILTER_PATTERN = Pattern
        .compile("(?s)\"{0,1}(" + Constants.FILTER_USER + "|"
            + Constants.FILTER_PATH_USER_GROUP_USER_ID
            + ")(\"*\\s*([=<>]+)\\s*\"*|\"*\\s*(any)\\s*\"*"
            + "|\"*\\s*(cql.any)\\s*\"*)" + "([^\\s\"\\(\\)]*)\"{0,1}");

    private static final int MAX_FIELD_LENGTH = 245;

    private UserGroupRendererInterface renderer;

    private EscidocRoleDaoInterface roleDao = null;

    private TripleStoreUtility tsu = null;

    private ObjectAttributeResolver objectAttributeResolver;

    private UserAccountDaoInterface userAccountDao = null;

    private UserGroupDaoInterface userGroupDao = null;

    private PolicyDecisionPointInterface pdp;

    // CHECKSTYLE:JAVADOC-OFF

    /**
     * See Interface for functional description.
     * 
     * @param xmlData
     * @return
     * @throws UniqueConstraintViolationException
     * @throws XmlCorruptedException
     * @throws SystemException
     * @see de.escidoc.core.aa.service.interfaces.UserGroupHandlerInterface
     *      #create(java.lang.String)
     * @aa
     */
    public String create(final String xmlData)
        throws UniqueConstraintViolationException, XmlCorruptedException,
        SystemException {
        ByteArrayInputStream in =
            XmlUtility.convertToByteArrayInputStream(xmlData);
        StaxParser sp = new StaxParser();
        GroupCreateUpdateHandler groupHandler =
            new GroupCreateUpdateHandler(sp);

        sp.addHandler(groupHandler);
        try {
            sp.parse(in);
            sp.clearHandlerChain();
        }
        catch (Exception e) {
            XmlUtility.handleUnexpectedStaxParserException("", e);
        }

        UserGroup userGroup = new UserGroup();

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
     * @param groupId
     * 
     * @throws ResourceNotFoundException
     * @throws SystemException
     * @see de.escidoc.core.aa.service.interfaces.UserGroupHandlerInterface
     *      #delete(java.lang.String)
     * @aa
     */
    public void delete(final String groupId) throws ResourceNotFoundException,
        SystemException {
        UserGroup userGroup = userGroupDao.retrieveUserGroup(groupId);

        if (userGroup == null) {
            String message =
                "User group with id " + groupId + " does not exist.";
            LOG.error(message);
            throw new UserGroupNotFoundException(message);
        }
        userGroupDao.delete(userGroup);
        sendUserGroupUpdateEvent(groupId);
        sendUserGroupMemberUpdateEvent(groupId);
    }

    /**
     * See Interface for functional description.
     * 
     * @param groupId
     * 
     * @return
     * @throws ResourceNotFoundException
     * @throws SystemException
     * @see de.escidoc.core.aa.service.interfaces.UserGroupHandlerInterface
     *      #retrieve(java.lang.String)
     * @aa
     */
    public String retrieve(final String groupId)
        throws ResourceNotFoundException, SystemException {
        UserGroup userGroup = userGroupDao.retrieveUserGroup(groupId);

        if (userGroup == null) {
            throw new UserGroupNotFoundException(StringUtility
                .format(MSG_GROUP_NOT_FOUND_BY_ID, groupId)
                .toString());
        }
        return renderer.render(userGroup);
    }

    /**
     * See Interface for functional description.
     * 
     * @param groupId
     * @param xmlData
     * 
     * @return
     * @throws ResourceNotFoundException
     * @throws UniqueConstraintViolationException
     * @throws XmlCorruptedException
     * @throws MissingAttributeValueException
     * @throws OptimisticLockingException
     * @throws SystemException
     * @see de.escidoc.core.aa.service.interfaces.UserGroupHandlerInterface
     *      #update(java.lang.String, java.lang.String)
     * @aa
     */
    public String update(final String groupId, final String xmlData)
        throws ResourceNotFoundException, UniqueConstraintViolationException,
        XmlCorruptedException, MissingAttributeValueException,
        OptimisticLockingException, SystemException {

        UserGroup userGroup = userGroupDao.retrieveUserGroup(groupId);

        if (userGroup == null) {
            String message =
                "User group with id " + groupId + " does not exist.";
            LOG.error(message);
            throw new UserGroupNotFoundException(message);
        }

        ByteArrayInputStream in =
            XmlUtility.convertToByteArrayInputStream(xmlData);
        StaxParser sp = new StaxParser();

        OptimisticLockingStaxHandler optimisticLockingHandler =
            new OptimisticLockingStaxHandler(
                userGroup.getLastModificationDate());
        sp.addHandler(optimisticLockingHandler);

        GroupCreateUpdateHandler groupHandler =
            new GroupCreateUpdateHandler(sp);

        sp.addHandler(groupHandler);
        try {
            sp.parse(in);
            sp.clearHandlerChain();
        }
        catch (OptimisticLockingException e) {
            throw e;
        }
        catch (Exception e) {
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
     * @param groupId
     * @param taskParam
     * 
     * @throws AlreadyActiveException
     * @throws ResourceNotFoundException
     * @throws XmlCorruptedException
     * @throws MissingAttributeValueException
     * @throws OptimisticLockingException
     * @throws SystemException
     * @see de.escidoc.core.aa.service.interfaces.UserGroupHandlerInterface
     *      #activate(java.lang.String, java.lang.String)
     * @aa
     */
    public void activate(final String groupId, final String taskParam)
        throws AlreadyActiveException, ResourceNotFoundException,
        XmlCorruptedException, MissingAttributeValueException,
        OptimisticLockingException, SystemException {
        UserGroup userGroup = userGroupDao.retrieveUserGroup(groupId);

        if (userGroup == null) {
            throw new UserGroupNotFoundException(StringUtility
                .format(MSG_GROUP_NOT_FOUND_BY_ID, groupId)
                .toString());
        }

        // TODO: validation missing, check if needed or if it shall be skipped
        de.escidoc.core.common.util.xml.stax.StaxParser sp =
            new de.escidoc.core.common.util.xml.stax.StaxParser(
                XmlUtility.NAME_PARAM);
        OptimisticLockingStaxHandler optimisticLockingHandler =
            new OptimisticLockingStaxHandler(
                userGroup.getLastModificationDate());

        sp.addHandler(optimisticLockingHandler);
        try {
            sp.parse(XmlUtility.convertToByteArrayInputStream(taskParam));
        }
        catch (InvalidXmlException e) {
            throw new XmlCorruptedException(e);
        }
        catch (OptimisticLockingException e) {
            throw e;
        }
        catch (MissingAttributeValueException e) {
            throw e;
        }
        catch (SystemException e) {
            throw e;
        }
        catch (Exception e) {
            final StringBuffer msg =
                StringUtility
                    .concatenate("Unexpected exception in ", getClass()
                        .getName(), ".activate: ", e.getClass().getName());
            LOG.error(msg.toString(), e);
            throw new SystemException(msg.toString(), e);
        }

        // check active flag and change value
        if (userGroup.getActive().booleanValue()) {
            throw new AlreadyActiveException("User group already active.");
        }
        try {
            setModificationValues(userGroup, null);
        }
        catch (UniqueConstraintViolationException e) {
            // can not occur
        }
        userGroup.setActive(Boolean.TRUE);
        userGroupDao.update(userGroup);
        sendUserGroupUpdateEvent(groupId);
        sendUserGroupMemberUpdateEvent(groupId);
    }

    /**
     * See Interface for functional description.
     * 
     * @param groupId
     * @param taskParam
     * 
     * @throws AlreadyDectiveException
     * @throws ResourceNotFoundException
     * @throws XmlCorruptedException
     * @throws MissingAttributeValueException
     * @throws OptimisticLockingException
     * @throws SystemException
     * @see de.escidoc.core.aa.service.interfaces.UserGroupHandlerInterface
     *      #deactivate(java.lang.String, java.lang.String)
     * @aa
     */
    public void deactivate(final String groupId, final String taskParam)
        throws AlreadyDeactiveException, ResourceNotFoundException,
        XmlCorruptedException, MissingAttributeValueException,
        OptimisticLockingException, SystemException {
        UserGroup userGroup = userGroupDao.retrieveUserGroup(groupId);

        if (userGroup == null) {
            throw new UserGroupNotFoundException(StringUtility
                .format(MSG_GROUP_NOT_FOUND_BY_ID, groupId)
                .toString());
        }

        // TODO: validation missing, check if needed or if it shall be skipped
        de.escidoc.core.common.util.xml.stax.StaxParser sp =
            new de.escidoc.core.common.util.xml.stax.StaxParser(
                XmlUtility.NAME_PARAM);
        OptimisticLockingStaxHandler optimisticLockingHandler =
            new OptimisticLockingStaxHandler(
                userGroup.getLastModificationDate());

        sp.addHandler(optimisticLockingHandler);
        try {
            sp.parse(XmlUtility.convertToByteArrayInputStream(taskParam));
        }
        catch (InvalidXmlException e) {
            throw new XmlCorruptedException(e);
        }
        catch (OptimisticLockingException e) {
            throw e;
        }
        catch (MissingAttributeValueException e) {
            throw e;
        }
        catch (SystemException e) {
            throw e;
        }
        catch (Exception e) {
            final StringBuffer msg =
                StringUtility
                    .concatenate("Unexpected exception in ", getClass()
                        .getName(), ".activate: ", e.getClass().getName());
            LOG.error(msg.toString(), e);
            throw new SystemException(msg.toString(), e);
        }

        // check active flag and change value
        if (!userGroup.getActive().booleanValue()) {
            throw new AlreadyDeactiveException("User group already deactive.");
        }
        try {
            setModificationValues(userGroup, null);
        }
        catch (UniqueConstraintViolationException e) {
            // can not occur
        }
        userGroup.setActive(Boolean.FALSE);
        userGroupDao.update(userGroup);
        sendUserGroupUpdateEvent(groupId);
        sendUserGroupMemberUpdateEvent(groupId);
    }

    /**
     * See Interface for functional description.
     * 
     * @param groupId
     * @param grantXML
     * 
     * @return
     * @throws AlreadyExistsException
     * @throws AuthenticationException
     * @throws AuthorizationException
     * @throws ResourceNotFoundException
     * @throws XmlCorruptedException
     * @throws InvalidScopeException
     * @throws SystemException
     * @see de.escidoc.core.aa.service.interfaces.UserGroupHandlerInterface
     *      #createGrant(java.lang.String, java.lang.String)
     * @aa
     */
    public String createGrant(final String groupId, final String grantXML)
        throws AlreadyExistsException, AuthenticationException,
        AuthorizationException, RoleNotFoundException, InvalidScopeException,
        UserGroupNotFoundException, XmlCorruptedException, SystemException {
        UserGroup userGroup = userGroupDao.retrieveUserGroup(groupId);

        if (userGroup == null) {
            throw new UserGroupNotFoundException(StringUtility
                .format(MSG_GROUP_NOT_FOUND_BY_ID, groupId)
                .toString());
        }

        final RoleGrant grant = new RoleGrant();
        ByteArrayInputStream in =
            XmlUtility.convertToByteArrayInputStream(grantXML);
        de.escidoc.core.common.util.xml.stax.StaxParser sp =
            new de.escidoc.core.common.util.xml.stax.StaxParser(
                XmlUtility.NAME_GRANT);
        LinkStaxHandler roleLinkHandler =
            new LinkStaxHandler(XPATH_GRANT_ROLE, XmlUtility.BASE_ROLE,
                RoleNotFoundException.class);

        sp.addHandler(roleLinkHandler);

        LinkStaxHandler objectLinkHandler =
            new LinkStaxHandler(XPATH_GRANT_ASSIGNED_ON);

        sp.addHandler(objectLinkHandler);

        GrantStaxHandler grantHandler = new GrantStaxHandler(grant);

        sp.addHandler(grantHandler);

        try {
            sp.parse(in);
        }
        catch (InvalidXmlException e) {
            throw new XmlCorruptedException(e);
        }
        catch (RoleNotFoundException e) {
            throw e;
        }
        catch (SystemException e) {
            throw e;
        }
        catch (Exception e) {
            final StringBuffer msg =
                StringUtility.concatenate("Unexpected exception in ",
                    getClass().getName(), ".createGrant: ", e
                        .getClass().getName());

            LOG.error(msg.toString(), e);
            throw new SystemException(msg.toString(), e);
        }

        final Date creationDate = new Date(System.currentTimeMillis());

        grant.setCreationDate(creationDate);
        grant.setUserAccountByCreatorId(UserAccountHandler
            .getAuthenticatedUser(userAccountDao));

        final String roleId = roleLinkHandler.getObjid();
        final EscidocRole role = roleDao.retrieveRole(roleId);

        if (role == null) {
            throw new RoleNotFoundException(
                StringUtility.format(
                    "Role with provided id not found", roleId));
        }
        grant.setEscidocRole(role);

        final String objectId = objectLinkHandler.getObjid();

        // check if referenced object exists and determine its object type
        // it is assumed, that each resource stored in fedora has an object
        // type stored in the triple store.
        if (objectId != null) {
            String objectType = null;
            String objectTitle = null;
            String objectHref = null;
            HashMap<String, String> objectAttributes = null;
            try {
                objectAttributes =
                    objectAttributeResolver.resolveObjectAttributes(objectId);
            }
            catch (Exception e) {
                throw new SystemException(e);
            }

            if (objectAttributes == null) {
                throw new XmlCorruptedException(
                    StringUtility.format(
                            MSG_GRANT_RESTRICTION_VIOLATED, objectId));
            }
            objectType =
                objectAttributes.get(ObjectAttributeResolver.ATTR_OBJECT_TYPE);
            objectTitle =
                objectAttributes.get(ObjectAttributeResolver.ATTR_OBJECT_TITLE);

            // check if objectType may be scope
            boolean checkOk = false;
            if (role.getScopeDefs() != null && objectType != null) {
                for (ScopeDef scopeDef : (Collection<ScopeDef>) role
                    .getScopeDefs()) {
                    if (scopeDef.getAttributeObjectType() != null
                        && scopeDef.getAttributeObjectType().equals(objectType)) {
                        checkOk = true;
                        break;
                    }
                }
            }
            if (!checkOk) {
                throw new InvalidScopeException("objectId " + objectId
                    + " has objectType " + objectType
                    + " and may not be scope for role " + role.getRoleName());
            }

            // see issue 358. The title of an object stored in fedora may
            // not be explicitly stored in the triple store.
            // Therefore, a default title will be set, if it is null, here.
            if (objectTitle == null) {
                objectTitle =
                    StringUtility.convertToUpperCaseLetterFormat(objectType)
                        + " " + objectId;
            }
            else if (objectTitle.length() > MAX_FIELD_LENGTH) {
                objectTitle = objectTitle.substring(0, MAX_FIELD_LENGTH);
            }

            // get the href of the object.
            objectHref = XmlUtility.getHref(objectType, objectId);

            // In case of REST it has to be checked if the provided href points
            // to the correct href.
            if (objectLinkHandler.getHref() != null
                && !objectLinkHandler.getHref().equals(objectHref)) {
                // FIXME: exception should be a resource not found exception
                // but this changes the interface. To prevent problems on
                // application side, currently an XmlCorruptedException is
                // thrown.
                throw new XmlCorruptedException(
                    StringUtility.format(
                            MSG_WRONG_HREF, objectLinkHandler.getHref(), objectType));
            }

            // check if grant already exists
            if (userGroupDao.retrieveCurrentGrant(userGroup, role, objectId) != null) {
                throw new AlreadyExistsException(
                    StringUtility.format(
                            "Grant already exists", groupId, role.getId(), objectId));
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
     * 
     * @param groupId
     * @param taskParam
     * 
     * @return
     * @throws OrganizationalUnitNotFoundException
     * @throws UserAccountNotFoundException
     * @throws UserGroupNotFoundException
     * @throws InvalidContentException
     * @throws MissingMethodParameterException
     * @throws SystemException
     * @throws AuthenticationException
     * @throws AuthorizationException
     * @throws OptimisticLockingException
     * @throws XmlCorruptedException
     * 
     * @tx
     */
    public String addSelectors(final String groupId, final String taskParam)
        throws OrganizationalUnitNotFoundException,
        UserAccountNotFoundException, UserGroupNotFoundException,
        InvalidContentException, MissingMethodParameterException,
        SystemException, AuthenticationException, AuthorizationException,
        OptimisticLockingException, XmlCorruptedException,
        UserGroupHierarchyViolationException {

        UserGroup userGroup = userGroupDao.retrieveUserGroup(groupId);
        if (userGroup == null) {
            String message =
                "User group with id " + groupId + " does not exist.";
            LOG.error(message);
            throw new UserGroupNotFoundException(message);
        }
        ByteArrayInputStream in =
            XmlUtility.convertToByteArrayInputStream(taskParam);
        StaxParser sp = new StaxParser();

        OptimisticLockingStaxHandler optimisticLockingHandler =
            new OptimisticLockingStaxHandler(
                userGroup.getLastModificationDate());
        sp.addHandler(optimisticLockingHandler);

        GroupSelectorsAddHandler groupHandler =
            new GroupSelectorsAddHandler(sp);
        sp.addHandler(groupHandler);
        try {
            sp.parse(in);
            sp.clearHandlerChain();
        }
        catch (InvalidXmlException e) {
            throw new XmlCorruptedException(e);
        }
        catch (Exception e) {
            XmlUtility.handleUnexpectedStaxParserException("", e);
        }
        Vector<String[]> selectors = groupHandler.getGroupSelectors();
        Set<UserGroupMember> existingMembers = userGroup.getMembers();
        Set<UserGroupMember> newMembers = new HashSet<UserGroupMember>();

        for (String[] selector : selectors) {
            UserGroupMember member = new UserGroupMember(userGroup);
            String name = selector[0];
            String type = selector[1];
            String value = selector[2];
            if (type.equals("internal")) {
                if (name.equals(XmlUtility.NAME_USER_ACCOUNT)) {
                    UserAccount referencedUser =
                        this.userAccountDao.retrieveUserAccountById(value);
                    if (referencedUser == null) {
                        String message =
                            StringUtility.format(
                                    MSG_USER_NOT_FOUND_BY_ID, value).toString();
                        LOG.error(message);
                        throw new UserAccountNotFoundException(message);
                    }
                }
                else if (name.equals(XmlUtility.NAME_USER_GROUP)) {
                    UserGroup referencedUserGroup =
                        userGroupDao.retrieveUserGroup(value);
                    if (referencedUserGroup == null) {
                        String message =
                            "Referenced user group with id " + value
                                + " does not exist.";
                        LOG.error(message);
                        throw new UserGroupNotFoundException(message);
                    }
                    if (!isCycleFree(groupId, value)) {
                        String message =
                            "User group with id "
                                + value
                                + " can not become a member of the user group with id "
                                + groupId
                                + "  because user group with id "
                                + value
                                + " is already ranked higher in the group hierarchy.";
                        LOG.error(message);
                        throw new UserGroupHierarchyViolationException(message);
                    }
                }
                else {
                    String message = MSG_GROUP_INVALID_SELECTOR_NAME;
                    LOG.error(message);
                    throw new XmlCorruptedException(message);
                }
            }
            boolean alreadyExist = false;
            for (UserGroupMember existingMember : existingMembers) {
                if (name.equals(existingMember.getName())
                    && type.equals(existingMember.getType())
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
        for (UserGroupMember newMember : newMembers) {
            userGroupDao.save(newMember);
            existingMembers.add(newMember);
        }
        sendUserGroupMemberUpdateEvent(groupId);
        return renderer.render(userGroup);
    }

    /**
     * See Interface for functional description.
     * 
     * @param groupId
     * @param taskParam
     * 
     * @return
     * @throws UserGroupNotFoundException
     * @throws MissingMethodParameterException
     * @throws SystemException
     * @throws AuthenticationException
     * @throws AuthorizationException
     * @throws MissingAttributeValueException
     * @throws OptimisticLockingException
     * @throws XmlCorruptedException
     * @tx
     */
    public String removeSelectors(final String groupId, final String taskParam)
        throws XmlCorruptedException, AuthenticationException,
        AuthorizationException, SystemException, UserGroupNotFoundException,
        OptimisticLockingException, MissingMethodParameterException,
        UserAccountNotFoundException, OrganizationalUnitNotFoundException {
        UserGroup userGroup = userGroupDao.retrieveUserGroup(groupId);
        if (userGroup == null) {
            String message =
                "User group with id " + groupId + " does not exist.";
            LOG.error(message);
            throw new UserGroupNotFoundException(message);
        }
        ByteArrayInputStream in =
            XmlUtility.convertToByteArrayInputStream(taskParam);
        StaxParser sp = new StaxParser();

        OptimisticLockingStaxHandler optimisticLockingHandler =
            new OptimisticLockingStaxHandler(
                userGroup.getLastModificationDate());
        sp.addHandler(optimisticLockingHandler);

        GroupSelectorsRemoveHandler groupHandler =
            new GroupSelectorsRemoveHandler();
        sp.addHandler(groupHandler);
        try {
            sp.parse(in);
            sp.clearHandlerChain();
        }
        catch (InvalidXmlException e) {
            throw new XmlCorruptedException(e);
        }
        catch (Exception e) {
            XmlUtility.handleUnexpectedStaxParserException("", e);
        }
        Vector<String> membersToRemove = groupHandler.getMemberIdsToRemove();
        Set<UserGroupMember> existingMembers = userGroup.getMembers();

        for (String memberId : membersToRemove) {
            Iterator<UserGroupMember> membersIterator =
                existingMembers.iterator();

            while (membersIterator.hasNext()) {
                UserGroupMember existingMember = membersIterator.next();
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
     * @param filter
     *            userGroupFilter
     * @return list of filtered user groups
     * @throws InvalidSearchQueryException
     * @throws SystemException
     * @see de.escidoc.core.aa.service.interfaces.UserGroupHandlerInterface
     *      #retrieveUserGroups(java.util.Map)
     */
    public String retrieveUserGroups(final Map<String, String[]> filter)
        throws InvalidSearchQueryException, SystemException {
        String result = null;
        String query = null;
        int offset = FilterHandler.DEFAULT_OFFSET;
        int limit = FilterHandler.DEFAULT_LIMIT;
        boolean explain = false;

        Map<String, String[]> castedFilter = (Map<String, String[]>) filter;

        // check if filter for userId is provided
        // if yes, get groups for user and add ids to filter
        // then remove userId from filter
        castedFilter = fixCqlUserFilter(castedFilter);

        SRURequestParameters parameters = new DbRequestParameters(castedFilter);

        query = parameters.query;
        limit = parameters.limit;
        offset = parameters.offset;
        explain = parameters.explain;

        if (explain) {
            Map<String, Object> values = new HashMap<String, Object>();

            values.put("PROPERTY_NAMES",
                new UserGroupFilter(null).getPropertyNames());
            result =
                ExplainXmlProvider.getInstance().getExplainUserGroupXml(values);
        }
        else {
            int needed = offset + limit;
            int currentLimit = needed;
            int currentOffset = 0;
            final List<UserGroup> permittedUserGroups =
                new ArrayList<UserGroup>();
            final int size = permittedUserGroups.size();

            while (size <= needed) {
                List<UserGroup> tmpUserGroups = null;

                tmpUserGroups =
                    userGroupDao.retrieveUserGroups(query, currentOffset,
                        currentLimit);
                if (tmpUserGroups == null || tmpUserGroups.isEmpty()) {
                    break;
                }
                else {
                    final List<String> ids =
                        new ArrayList<String>(tmpUserGroups.size());
                    for (UserGroup userGroup : tmpUserGroups) {
                        ids.add(userGroup.getId());
                    }

                    try {
                        final List<String> tmpPermitted =
                            pdp.evaluateRetrieve(XmlUtility.NAME_USER_GROUP,
                                ids);
                        final int numberPermitted = tmpPermitted.size();
                        if (numberPermitted == 0) {
                            break;
                        }
                        else {
                            int permittedIndex = 0;
                            String currentPermittedId =
                                tmpPermitted.get(permittedIndex);
                            for (UserGroup userGroup : tmpUserGroups) {
                                if (currentPermittedId
                                    .equals(userGroup.getId())) {
                                    permittedUserGroups.add(userGroup);
                                    ++permittedIndex;
                                    if (permittedIndex < numberPermitted) {
                                        currentPermittedId =
                                            tmpPermitted.get(permittedIndex);
                                    }
                                    else {
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    catch (MissingMethodParameterException e) {
                        throw new SystemException(
                            "Unexpected exception during evaluating access "
                                + "rights.", e);
                    }
                    catch (ResourceNotFoundException e) {
                        throw new SystemException(
                            "Unexpected exception during evaluating access "
                                + "rights.", e);
                    }
                }
                currentOffset += currentLimit;
            }

            final List<UserGroup> offsetUserGroups;
            final int numberPermitted = permittedUserGroups.size();
            if (offset < numberPermitted) {
                offsetUserGroups = new ArrayList<UserGroup>(limit);
                for (int i = offset; i < numberPermitted && i < needed; i++) {
                    offsetUserGroups.add(permittedUserGroups.get(i));
                }
            }
            else {
                offsetUserGroups = new ArrayList<UserGroup>(0);
            }
            result = renderer.renderUserGroups(offsetUserGroups);
        }
        return result;
    }

    /**
     * replaces user-id-filter with resolved groupIds.
     * 
     * @param filter
     *            cql-filter
     * @throws InvalidSearchQueryException
     *             e
     * @throws SystemException
     *             e
     * @return Map with replaced cql-query (userId replaced with groupIds)
     */
    private Map<String, String[]> fixCqlUserFilter(
        final Map<String, String[]> filter) throws InvalidSearchQueryException,
        SystemException {
        Map<String, String[]> returnFilter = filter;
        Object[] queryPartsObject = filter.get(Constants.SRU_PARAMETER_QUERY);
        if (queryPartsObject != null) {
            String[] queryParts = new String[queryPartsObject.length];
            for (int i = 0; i < queryPartsObject.length; i++) {
                if (queryPartsObject[i] != null) {
                    queryParts[i] = queryPartsObject[i].toString();
                }
            }
            boolean userFilterFound = false;
            if (queryParts != null) {
                for (int i = 0; i < queryParts.length; i++) {
                    Matcher matcher =
                        USER_FILTER_PATTERN.matcher(queryParts[i]);
                    if (matcher.find()) {
                        userFilterFound = true;
                        Matcher userFilterMatcher =
                            USER_FILTER_PATTERN.matcher(queryParts[i]);
                        StringBuffer result = new StringBuffer("");
                        while (userFilterMatcher.find()) {
                            if (userFilterMatcher.group(6).matches(".*?%.*")) {
                                throw new InvalidSearchQueryException(
                                    "Wildcards not allowed in user-filter");
                            }
                            if ((userFilterMatcher.group(3) != null && userFilterMatcher
                                .group(3).matches(">|<|<=|>=|<>"))
                                || userFilterMatcher.group(4) != null
                                || userFilterMatcher.group(5) != null) {
                                throw new InvalidSearchQueryException(
                                    "non-supported relation in user-filter");
                            }
                            Set<String> groupIds = null;
                            StringBuffer replacement = new StringBuffer(" (");
                            try {
                                // get groups for user
                                groupIds =
                                    retrieveGroupsForUser(userFilterMatcher
                                        .group(6));

                                // write group-cql-query
                                // and replace user-expression with it.
                                if (groupIds != null && !groupIds.isEmpty()) {
                                    for (String groupId : groupIds) {
                                        if (replacement.length() > 2) {
                                            replacement.append(" or ");
                                        }
                                        replacement.append("\"");
                                        replacement
                                            .append(Constants.FILTER_PATH_ID);
                                        replacement
                                            .append("\"=").append(groupId)
                                            .append(" ");
                                    }
                                }
                                else {
                                    throw new UserAccountNotFoundException("");
                                }
                            }
                            catch (UserAccountNotFoundException e) {
                                // if user has no groups or user not found,
                                // write nonexisting group in query
                                replacement.append("\"");
                                replacement.append(Constants.FILTER_PATH_ID);
                                replacement
                                    .append("\"=").append("nonexistinggroup")
                                    .append(" ");
                            }

                            replacement.append(") ");
                            userFilterMatcher.appendReplacement(result,
                                replacement.toString());
                        }
                        userFilterMatcher.appendTail(result);
                        queryParts[i] = result.toString();
                    }
                }
                if (userFilterFound) {
                    Map<String, String[]> filter1 =
                        new HashMap<String, String[]>();
                    for (Entry<String, String[]> entry : filter.entrySet()) {
                        if (entry.getValue() != null) {
                            filter1
                                .put(
                                    entry.getKey(),
                                    new String[((Object[]) entry.getValue()).length]);
                            for (int j = 0; j < ((Object[]) entry.getValue()).length; j++) {
                                filter1.get(entry.getKey())[j] =
                                    ((Object[]) entry.getValue())[j].toString();
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
        }
        return returnFilter;
    }

    /**
     * Checks if one user group can become a member of another userGroup without
     * creating a cycle in the user group hierarchy.
     * 
     * @param userGroupId
     *            id of the user group, the member should be added to
     * @param memberCandidateId
     *            id of the member candidate user group
     * @return true if the user group hierarchy will not be violated false if
     *         the user group hierarchy will be violated
     * @throws SystemException
     */
    private boolean isCycleFree(
        final String userGroupId, final String memberCandidateId)
        throws SystemException {
        if (userGroupId != null && userGroupId.equals(memberCandidateId)) {
            return false;
        }
        Map<String, Object> criteria = new HashMap<String, Object>();
        criteria.put(Constants.FILTER_PATH_TYPE, "internal");
        criteria.put(Constants.FILTER_PATH_NAME, XmlUtility.NAME_USER_GROUP);
        criteria.put(Constants.FILTER_PATH_VALUE, new HashSet<String>() {
            private static final long serialVersionUID = -2207807626629819089L;

            {
                add(userGroupId);
            }
        });
        List<UserGroupMember> userGroupMembers;
        HashSet<String> superMembers;
        boolean proceed = true;
        while (proceed) {
            superMembers = new HashSet<String>();
            userGroupMembers = userGroupDao.retrieveUserGroupMembers(criteria);
            if (userGroupMembers != null && !userGroupMembers.isEmpty()) {
                for (UserGroupMember userGroupMember : userGroupMembers) {
                    String id = userGroupMember.getUserGroup().getId();
                    if (memberCandidateId.equals(id)) {
                        return false;
                    }
                    else {
                        superMembers
                            .add(userGroupMember.getUserGroup().getId());
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
     * @param userId
     * 
     * @return set of groupIds (hierarchy)
     * @throws UserAccountNotFoundException
     * @throws SystemException
     * @see de.escidoc.core.aa.service.interfaces.UserGroupHandlerInterface
     *      #retrieveGroupsForUser(java.lang.String)
     * @aa
     */
    public Set<String> retrieveGroupsForUser(final String userId)
        throws UserAccountNotFoundException, SystemException {

        return retrieveGroupsForUser(userId, false);

    }

    /**
     * See Interface for functional description.
     * 
     * @param userId
     * @param activeOnly
     * 
     * @return set of groupIds (hierarchy)
     * @throws UserAccountNotFoundException
     * @throws SystemException
     * @see de.escidoc.core.aa.service.interfaces.UserGroupHandlerInterface
     *      #retrieveGroupsForUser(java.lang.String, boolean)
     * @aa
     */
    public Set<String> retrieveGroupsForUser(
        final String userId, final boolean activeOnly)
        throws UserAccountNotFoundException, SystemException {
        // may not return null, so return empty list!!
        HashSet<String> userGroups = new HashSet<String>();
        // Try getting the userAccount
        UserAccount userAccount = userAccountDao.retrieveUserAccount(userId);
        if (userAccount == null) {
            throw new UserAccountNotFoundException(StringUtility
                .format(MSG_USER_NOT_FOUND_BY_ID, userId)
                .toString());
        }

        // Get groups the user is integrated via his userId
        userGroups.addAll(retrieveGroupsByUserIds(new HashSet<String>() {
            private static final long serialVersionUID = 3629642185855440792L;

            {
                add(userId);
            }
        }, activeOnly));

        // Get groups the user is integrated via one of his user attributes
        // check if attribute is ou-attribute. Then resolve path-list
        String ouAttributeName = null;
        try {
            ouAttributeName =
                EscidocConfiguration.getInstance().get(
                    EscidocConfiguration.ESCIDOC_CORE_AA_OU_ATTRIBUTE_NAME);
        }
        catch (IOException e) {
            throw new SystemException(e);
        }
        Set<UserAttribute> attributes = userAccount.getUserAttributesByUserId();

        Map<String, Set<UserGroupMember>> groupMemberMap =
            retrieveGroupMembersWithAttributeSelector();

        if ((attributes != null) && (groupMemberMap != null)) {
            // iterate attributes of user
            for (UserAttribute attribute : attributes) {
                // check for groupMembers with name=user-attribute-name
                Set<UserGroupMember> groupMembers =
                    groupMemberMap.get(attribute.getName());

                if (groupMembers != null) {
                    List<String> pathList = null;
                    if (ouAttributeName != null && !ouAttributeName.equals("")
                        && attribute.getName().equals(ouAttributeName)) {
                        List<String> initialList = new ArrayList<String>();
                        initialList.add(attribute.getValue());
                        pathList =
                            getOrgUnitPathList(attribute.getValue(),
                                initialList);
                    }
                    for (UserGroupMember groupMember : groupMembers) {
                        if (attribute.getValue().equals(groupMember.getValue())
                            || (pathList != null && pathList
                                .contains(groupMember.getValue()))) {
                            if (!activeOnly
                                || Boolean.TRUE.equals(groupMember
                                    .getUserGroup().getActive())) {
                                userGroups.add(groupMember
                                    .getUserGroup().getId());
                            }
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
     * @param groupId
     * 
     * @return set of groupIds (hierarchy)
     * @throws SystemException
     * @see de.escidoc.core.aa.service.interfaces.UserGroupHandlerInterface
     *      #retrieveGroupsForGroup(java.lang.String)
     * @aa
     */
    public Set<String> retrieveGroupsForGroup(final String groupId)
        throws SystemException {

        HashSet<String> userGroups = new HashSet<String>();
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
     * @param activeOnly
     *            if true, only return active groups
     * 
     * @return HashSet userGroupIds
     * @throws UserAccountNotFoundException
     *             e
     * @throws SqlDatabaseSystemException
     *             e
     * @aa
     */
    private HashSet<String> retrieveGroupsByUserIds(
        final HashSet<String> userIds, final boolean activeOnly)
        throws UserAccountNotFoundException, SqlDatabaseSystemException {
        HashSet<String> userGroupIds = new HashSet<String>();

        if (userIds != null && !userIds.isEmpty()) {
            // retrieve all groupMembers that are of type
            // user-account
            // and have one of the userIds as value
            Map<String, Object> criteria = new HashMap<String, Object>();
            criteria.put(Constants.FILTER_PATH_TYPE, "internal");
            criteria.put(Constants.FILTER_PATH_NAME, "user-account");
            criteria.put(Constants.FILTER_PATH_VALUE, userIds);
            List<UserGroupMember> userGroupMembers =
                userGroupDao.retrieveUserGroupMembers(criteria);
            if (userGroupMembers != null) {
                for (UserGroupMember userGroupMember : userGroupMembers) {
                    if (!activeOnly
                        || Boolean.TRUE.equals(userGroupMember
                            .getUserGroup().getActive())) {
                        if (!userGroupIds.contains(userGroupMember
                            .getUserGroup().getId())) {
                            userGroupIds.add(userGroupMember
                                .getUserGroup().getId());
                        }
                    }
                }
            }
        }

        return userGroupIds;
    }

    /**
     * Retrieves a list of user group members by querying for
     * attribute-selector.
     * 
     * @return Map key=attribute name, value=list of user group members
     * @throws SqlDatabaseSystemException
     *             e
     */
    private Map<String, Set<UserGroupMember>> retrieveGroupMembersWithAttributeSelector()
        throws SqlDatabaseSystemException {
        Map<String, Set<UserGroupMember>> result =
            new HashMap<String, Set<UserGroupMember>>();
        Map<String, Object> criteria = new HashMap<String, Object>();

        criteria.put(Constants.FILTER_PATH_TYPE, "user-attribute");

        List<UserGroupMember> userGroupMembers =
            userGroupDao.retrieveUserGroupMembers(criteria);

        if (userGroupMembers != null) {
            for (UserGroupMember userGroupMember : userGroupMembers) {
                final String attributeName = userGroupMember.getName();
                Set<UserGroupMember> userGroupMemberSet =
                    result.get(attributeName);

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
     * @param groupIds
     * @param activeOnly
     *            if true, only retrun active groups.
     * 
     * @return HashSet userGroupIds
     * @throws SqlDatabaseSystemException
     *             e
     * @aa
     */
    private HashSet<String> retrieveGroupsByGroupIds(
        final HashSet<String> groupIds, final boolean activeOnly)
        throws SqlDatabaseSystemException {
        HashSet<String> userGroupIds = groupIds;

        if (userGroupIds != null && !userGroupIds.isEmpty()) {
            // retrieve all groupMembers that are of type
            // user-group and have one of the groupIds as value
            // plus: resolve complete group-hierarchy
            Map<String, Object> criteria = new HashMap<String, Object>();
            criteria.put(Constants.FILTER_PATH_TYPE, "internal");
            criteria.put(Constants.FILTER_PATH_NAME, "user-group");
            criteria.put(Constants.FILTER_PATH_VALUE, userGroupIds);
            HashSet<String> superMembers;
            boolean proceed = true;
            while (proceed) {
                List<UserGroupMember> userGroupMembers =
                    userGroupDao.retrieveUserGroupMembers(criteria);
                superMembers = new HashSet<String>();
                if (userGroupMembers != null && !userGroupMembers.isEmpty()) {
                    for (UserGroupMember userGroupMember : userGroupMembers) {
                        if (!activeOnly
                            || Boolean.TRUE.equals(userGroupMember
                                .getUserGroup().getActive())) {
                            superMembers.add(userGroupMember
                                .getUserGroup().getId());
                            if (!userGroupIds.contains(userGroupMember
                                .getUserGroup().getId())) {
                                userGroupIds.add(userGroupMember
                                    .getUserGroup().getId());
                            }
                        }
                    }
                }
                if (!superMembers.isEmpty()) {
                    criteria.put(Constants.FILTER_PATH_VALUE, superMembers);
                }
                else {
                    proceed = false;
                }
            }
        }

        return userGroupIds;
    }

    /**
     * Compute the pathes of the actual organizaional unit.
     * 
     * @param orgUnitId
     *            the orgUnitId where pathList has to get retrieved.
     * @param totalList
     *            total list of all Parents.
     * @return List of ancestor-orgUnits
     * @throws SystemException
     *             If anything fails while computing the pathes.
     */
    private List<String> getOrgUnitPathList(
        final String orgUnitId, final List<String> totalList)
        throws SystemException {

        List<String> addableList = totalList;
        List<String> orgUnitIds = tsu.getParents(orgUnitId);
        if (orgUnitIds != null && !orgUnitIds.isEmpty()) {
            addableList.addAll(orgUnitIds);
            for (String parentOrgUnitId : orgUnitIds) {
                addableList = getOrgUnitPathList(parentOrgUnitId, addableList);
            }
        }
        return addableList;
    }

    /**
     * See Interface for functional description.
     * 
     * @param groupId
     * 
     * @return
     * @throws UserGroupNotFoundException
     * @throws SystemException
     * @see de.escidoc.core.aa.service.interfaces.UserGroupHandlerInterface
     *      #retrieveCurrentGrantsAsMap(java.lang.String)
     * @aa
     */
    public Map<String, Map<String, List<RoleGrant>>> retrieveCurrentGrantsAsMap(
        final String groupId) throws UserGroupNotFoundException,
        SystemException {

        UserGroup userGroup = userGroupDao.retrieveUserGroup(groupId);
        if (userGroup == null) {
            throw new UserGroupNotFoundException(StringUtility
                .format(MSG_GROUP_NOT_FOUND_BY_ID, groupId)
                .toString());
        }
        List<RoleGrant> currentGrants = fetchCurrentGrants(groupId);
        if (currentGrants == null || currentGrants.isEmpty()) {
            return null;
        }

        Map<String, Map<String, List<RoleGrant>>> ret =
            new HashMap<String, Map<String, List<RoleGrant>>>();
        for (RoleGrant grant : currentGrants) {
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
     * @param groupIds
     * 
     * @return List with maps for groupGrants
     * @throws SystemException
     * @see de.escidoc.core.aa.service.interfaces.UserGroupHandlerInterface
     *      #retrieveCurrentGrantsAsMap(List)
     * @aa
     */
    public HashMap<String, Map<String, Map<String, List<RoleGrant>>>> retrieveManyCurrentGrantsAsMap(
        final List<String> groupIds) throws SystemException {

        HashMap<String, List<RoleGrant>> currentGrantsForGroups =
            userGroupDao.retrieveCurrentGrants(groupIds);
        if (currentGrantsForGroups == null || currentGrantsForGroups.isEmpty()) {
            return null;
        }

        HashMap<String, Map<String, Map<String, List<RoleGrant>>>> ret =
            new HashMap<String, Map<String, Map<String, List<RoleGrant>>>>();
        for (Entry<String, List<RoleGrant>> entry 
        		            : currentGrantsForGroups.entrySet()) {
            if (entry.getValue() == null) {
                continue;
            }
            Map<String, Map<String, List<RoleGrant>>> currentGrantsForOneGroup =
                new HashMap<String, Map<String, List<RoleGrant>>>();
            for (RoleGrant grant : entry.getValue()) {
                final String roleId = grant.getRoleId();
                Map<String, List<RoleGrant>> grantsOfRole =
                    currentGrantsForOneGroup.get(roleId);
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
     * @param groupId
     * @return
     * @throws UserGroupNotFoundException
     * @throws SystemException
     * @see de.escidoc.core.aa.service.interfaces.UserGroupHandlerInterface
     *      #retrieveCurrentGrants(java.lang.String)
     * @aa
     */
    public String retrieveCurrentGrants(final String groupId)
        throws UserGroupNotFoundException, SystemException {

        UserGroup userGroup = userGroupDao.retrieveUserGroup(groupId);
        if (userGroup == null) {
            throw new UserGroupNotFoundException(StringUtility
                .format(MSG_GROUP_NOT_FOUND_BY_ID, groupId)
                .toString());
        }
        List<RoleGrant> currentGrants = fetchCurrentGrants(groupId);
        HashMap<String, RoleGrant> grantsMap = new HashMap<String, RoleGrant>();
        List<Object[]> argumentList = new ArrayList<Object[]>();
        List<RoleGrant> filteredCurrentGrants = new ArrayList<RoleGrant>();

        // AA-filter
        for (RoleGrant roleGrant : currentGrants) {
            grantsMap.put(roleGrant.getId(), roleGrant);
            Object[] args = new Object[] { groupId, roleGrant.getId() };
            argumentList.add(args);
        }
        try {
            List<Object[]> returnList =
                pdp.evaluateMethodForList("user-group", "retrieveGrant",
                    argumentList);
            for (Object[] obj : returnList) {
                filteredCurrentGrants.add(grantsMap.get(obj[1]));
            }
        }
        catch (MissingMethodParameterException e) {
            throw new SystemException("Unexpected exception "
                + "during evaluating access rights.", e);
        }
        catch (ResourceNotFoundException e) {
            throw new SystemException("Unexpected exception "
                + "during evaluating access rights.", e);
        }

        return renderer.renderCurrentGrants(userGroup, currentGrants);
    }

    /**
     * See Interface for functional description.
     * 
     * @param groupId
     * @param grantId
     * @return
     * @throws UserGroupNotFoundException
     * @throws GrantNotFoundException
     * @throws MissingMethodParameterException
     * @throws AuthenticationException
     * @throws AuthenticationException
     * @throws AuthorizationException
     * @throws SystemException
     * @see de.escidoc.core.aa.service.interfaces.UserGroupHandlerInterface
     *      #retrieveCurrentGrants(java.lang.String)
     * @aa
     */
    public String retrieveGrant(final String groupId, final String grantId)
        throws UserGroupNotFoundException, GrantNotFoundException,
        MissingMethodParameterException, AuthenticationException,
        AuthorizationException, SystemException {
        UserGroup userGroup = userGroupDao.retrieveUserGroup(groupId);

        if (userGroup == null) {
            throw new UserGroupNotFoundException(StringUtility
                .format(MSG_GROUP_NOT_FOUND_BY_ID, groupId)
                .toString());
        }
        boolean isOwned = false;
        List<RoleGrant> grants = userGroupDao.retrieveGrants(groupId);
        RoleGrant grantToRetrieve = null;
        for (RoleGrant grant : grants) {
            if (grant.getId().equals(grantId)) {
                grantToRetrieve = grant;
                isOwned = true;
                break;

            }
        }
        if (!isOwned) {
            throw new GrantNotFoundException("Grant with id " + grantId
                + " is no grant of user group " + groupId);
        }

        return renderer.renderGrant(grantToRetrieve);
    }

    /**
     * Retrieve the resources section of a user group.
     * 
     * @param groupId
     *            id of the user group
     * 
     * @return the resources of the user group as XML structure
     * @throws SystemException
     *             Thrown in case of an internal error.
     * @throws UserGroupNotFoundException
     *             Thrown if a user group with the provided id does not exist in
     *             the framework.
     */
    public String retrieveResources(final String groupId)
        throws UserGroupNotFoundException, SystemException {
        return renderer
            .renderResources(userGroupDao.retrieveUserGroup(groupId));
    }

    /**
     * See Interface for functional description.
     * 
     * @param grantId
     *            grantId
     * @param taskParam
     *            taskParam
     * 
     * @throws ResourceNotFoundException
     * @throws AlreadyRevokedException
     * @throws XmlCorruptedException
     * @throws MissingAttributeValueException
     * @throws OptimisticLockingException
     * @throws SystemException
     * @see de.escidoc.core.aa.service.interfaces.UserGroupHandlerInterface
     *      #revokeGrant(java.lang.String, java.lang.String)
     * @aa
     */
    public void revokeGrant(
        final String groupId, final String grantId, final String taskParam)
        throws UserGroupNotFoundException, GrantNotFoundException,
        AlreadyRevokedException, XmlCorruptedException,
        MissingAttributeValueException, MissingMethodParameterException,
        AuthenticationException, AuthorizationException, SystemException {

        UserGroup userGroup = userGroupDao.retrieveUserGroup(groupId);

        if (userGroup == null) {
            throw new UserGroupNotFoundException(StringUtility
                .format(MSG_GROUP_NOT_FOUND_BY_ID, groupId)
                .toString());
        }
        boolean isOwned = false;
        List<RoleGrant> grants = userGroupDao.retrieveGrants(groupId);
        RoleGrant grantToRevoke = null;
        for (RoleGrant grant : grants) {
            if (grant.getId().equals(grantId)) {
                grantToRevoke = grant;
                isOwned = true;
                break;

            }
        }
        if (!isOwned) {
            throw new GrantNotFoundException("Grant with id " + grantId
                + " is no current grant of user group " + groupId);
        }

        de.escidoc.core.common.util.xml.stax.StaxParser sp =
            new de.escidoc.core.common.util.xml.stax.StaxParser(
                XmlUtility.NAME_PARAM);
        GrantStaxHandler grantHandler = new GrantStaxHandler(grantToRevoke);

        sp.addHandler(grantHandler);

        RevokeStaxHandler revokeStaxHandler =
            new RevokeStaxHandler(grantToRevoke, userAccountDao);

        sp.addHandler(revokeStaxHandler);

        try {
            sp.parse(XmlUtility.convertToByteArrayInputStream(taskParam));
        }
        catch (InvalidXmlException e) {
            throw new XmlCorruptedException(e);
        }
        catch (MissingAttributeValueException e) {
            throw e;
        }
        catch (SystemException e) {
            throw e;
        }
        catch (Exception e) {
            final StringBuffer msg =
                StringUtility.concatenate("Unexpected exception in ",
                    getClass().getName(), ".parse: ", e.getClass().getName());
            LOG.error(msg.toString(), e);
            throw new SystemException(msg.toString(), e);
        }
        userGroupDao.update(grantToRevoke);
        sendUserGroupUpdateEvent(groupId);
    }

    /**
     * See Interface for functional description.
     * 
     * @param groupId
     * @param filterXML
     * 
     * @throws ResourceNotFoundException
     * @throws AlreadyRevokedException
     * @throws XmlCorruptedException
     * @throws MissingAttributeValueException
     * @throws SystemException
     * @throws AuthorizationException
     * @see de.escidoc.core.aa.service.interfaces.UserGroupHandlerInterface
     *      #revokeGrants(java.lang.String, java.lang.String)
     * @tx
     * @aa
     */
    public void revokeGrants(final String groupId, final String filterXML)
        throws UserGroupNotFoundException, GrantNotFoundException,
        AlreadyRevokedException, XmlCorruptedException,
        MissingAttributeValueException, SystemException, AuthorizationException {
        // check if user group exists
        UserGroup userGroup = userGroupDao.retrieveUserGroup(groupId);
        if (userGroup == null) {
            throw new UserGroupNotFoundException(StringUtility
                .format(MSG_GROUP_NOT_FOUND_BY_ID, groupId)
                .toString());
        }
        // get all current grants of user group
        List<RoleGrant> grants = fetchCurrentGrants(groupId);
        // build HashMap with grantId
        HashMap<String, RoleGrant> grantsHash =
            new HashMap<String, RoleGrant>();

        for (RoleGrant grant : grants) {
            grantsHash.put(grant.getId(), grant);
        }

        // Parse taskParam
        StaxParser fp = new StaxParser();
        TaskParamHandler tph = new TaskParamHandler(fp);

        tph.setCheckLastModificationDate(false);
        fp.addHandler(tph);

        FilterHandler fh = new FilterHandler(fp);

        fp.addHandler(fh);
        try {
            fp.parse(new ByteArrayInputStream(filterXML
                .getBytes(XmlUtility.CHARACTER_ENCODING)));
        }
        catch (InvalidContentException e) {
            throw new XmlCorruptedException(e);
        }
        catch (Exception e) {
            XmlUtility.handleUnexpectedStaxParserException("", e);
        }

        Map<String, Object> filters = fh.getRules();
        HashSet<String> grantIds = null;

        if (filters.isEmpty()) {
            // if no filters are provided, remove all current grants
            grantIds = new HashSet<String>();
            for (String grantId : grantsHash.keySet()) {
                grantIds.add(grantId);
            }
        }
        else {
            // get ids of grants to revoke
            grantIds =
                (HashSet<String>) filters.get(Constants.DC_IDENTIFIER_URI);
        }

        if ((grantIds == null) || grantIds.isEmpty()) {
            return;
        }

        // check if all grants that shall get revoked are currentGrants
        for (String grantId : grantIds) {
            if (!grantsHash.containsKey(grantId)) {
                throw new GrantNotFoundException("Grant with id " + grantId
                    + " is no current grant of user group " + groupId);
            }
        }

        // AA-filter grants to revoke
        List<Object[]> argumentList = new ArrayList<Object[]>();
        for (String grantId : grantIds) {
            Object[] args = new Object[] { groupId, grantId };
            argumentList.add(args);
        }
        try {
            List<Object[]> returnList =
                pdp.evaluateMethodForList("user-group", "revokeGrant",
                    argumentList);
            if (returnList.size() < grantIds.size()) {
                // user is not allowed to revoke at least one of the grants
                // so throw AuthorizationException
                throw new AuthorizationException(
                    "You are not allowed to revoke at least "
                        + "one of the specified grants");
            }
        }
        catch (MissingMethodParameterException e) {
            throw new SystemException("Unexpected exception "
                + "during evaluating access rights.", e);
        }
        catch (ResourceNotFoundException e) {
            throw new SystemException("Unexpected exception "
                + "during evaluating access rights.", e);
        }

        UserAccount authenticateUser =
            UserAccountHandler.getAuthenticatedUser(userAccountDao);

        for (String grantId : grantIds) {
            // set revoke-date, -user and -remark
            grantsHash.get(grantId).setUserAccountByRevokerId(authenticateUser);
            grantsHash.get(grantId).setRevocationDate(
                new Date(System.currentTimeMillis()));
            grantsHash.get(grantId).setRevocationRemark(
                tph.getRevokationRemark());

            // update grant
            userGroupDao.update(grantsHash.get(grantId));
        }
        sendUserGroupUpdateEvent(groupId);
    }

    // CHECKSTYLE:JAVADOC-ON

    /**
     * Fetches the current grants of the user group identified by the provided
     * id.
     * 
     * @param groupId
     *            id of the user group
     * 
     * @return Returns a <code>List</code> containing the grants of the user
     *         group that are currently valid. If the user group does not have a
     *         grant, an empty <code>List</code> is returned.
     * @throws SqlDatabaseSystemException
     *             Thrown in case of an internal database error.
     * @aa
     */
    private List<RoleGrant> fetchCurrentGrants(final String groupId)
        throws SqlDatabaseSystemException {
        List<RoleGrant> grants = userGroupDao.retrieveGrants(groupId);
        List<RoleGrant> result = new ArrayList<RoleGrant>(grants.size());

        for (RoleGrant grant : grants) {
            if (grant.getRevocationDate() == null) {
                result.add(grant);
            }
        }
        return result;
    }

    /**
     * Sends userGroupUpdateEvent to AA.
     * 
     * @param groupId
     *            The id of the updated user group.
     * 
     * @throws WebserverSystemException
     *             Thrown in case of an internal error.
     * @aa
     */
    private void sendUserGroupUpdateEvent(final String groupId)
        throws WebserverSystemException {

        PoliciesCache.clearGroupPolicies(groupId);
    }

    /**
     * Sends userGroupMemberUpdateEvent to AA.
     * 
     * @param groupId
     *            The id of the updated user group.
     * 
     * @throws WebserverSystemException
     *             Thrown in case of an internal error.
     * @aa
     */
    private void sendUserGroupMemberUpdateEvent(final String groupId)
        throws WebserverSystemException {

        PoliciesCache.clearUserGroups();
    }

    /**
     * Sets the creation date and the created-by user in the provided
     * <code>UserGroup</code> object.<br/>
     * The values are set with the values of modification date and modifying
     * user of the provided user group.<br/>
     * Before calling this method, the last modification date and the modifying
     * user must be set.
     * 
     * @param userGroup
     *            The <code>UserGroup</code> object to modify.
     * @throws SystemException
     *             Thrown in case of an internal error.
     */
    private void setCreationValues(final UserGroup userGroup)
        throws SystemException {

        // initialize creation-date value
        userGroup.setCreationDate(userGroup.getLastModificationDate());

        // initialize created-by values
        userGroup.setCreatorId(userGroup.getModifiedById());
    }

    /**
     * Sets the last modification date, the modified-by user and all values from
     * the given properties map in the provided <code>UserGroup</code> object. <br/>
     * The last modification date is set to the current time, and the modified
     * by user to the user account of the current, authenticated user.
     * 
     * @param userGroup
     *            The <code>UserGroup</code> object to modify.
     * @param groupProperties
     *            map which contains all properties of the user group
     * 
     * @return true if the modification values were changed
     * @throws SystemException
     *             Thrown in case of an internal error.
     * @throws UniqueConstraintViolationException
     *             The label of the given user group has already been used.
     */
    private boolean setModificationValues(
        final UserGroup userGroup, final Map<String, String> groupProperties)
        throws SystemException, UniqueConstraintViolationException {
        boolean changed = false;
        if (groupProperties != null) {
            String description =
                groupProperties.get(Elements.ELEMENT_DESCRIPTION);
            if (description != null
                && ((userGroup.getDescription() != null
                    && description.equals(userGroup.getDescription()) || userGroup
                    .getDescription() == null))) {
                userGroup.setDescription(description);
                changed = true;
            }

            String oldLabel = userGroup.getLabel();
            String label = groupProperties.get("label");
            if (!checkLabelUnique(label)) {
                if (oldLabel == null
                    || !oldLabel.equals(label)) {
                    String message =
                        "The provided user group label is not unique.";
                    LOG.error(message);
                    throw new UniqueConstraintViolationException(message);
                }
            }
            else {
                changed = true;
                userGroup.setLabel(label);
            }

            String name = groupProperties.get(Elements.ELEMENT_NAME);
            if (userGroup.getName() == null
                || ((userGroup.getName() != null) && !name.equals(userGroup
                    .getName()))) {
                userGroup.setName(name);
                changed = true;
            }

            String email = groupProperties.get("email");
            if (email != null
                && ((userGroup.getEmail() != null
                    && email.equals(userGroup.getEmail()) || userGroup
                    .getEmail() == null))) {
                userGroup.setEmail(email);
                changed = true;
            }

            String type = groupProperties.get(Elements.ELEMENT_TYPE);
            if (type != null
                && ((userGroup.getType() != null
                    && type.equals(userGroup.getType()) || userGroup.getType() == null))) {
                userGroup.setType(type);
                changed = true;
            }

            if (changed) {
                userGroup.setLastModificationDate(new Date(System
                    .currentTimeMillis()));
                userGroup.setModifiedById(UserAccountHandler
                    .getAuthenticatedUser(userAccountDao));
            }
        }
        else {
            // caller is activate() or deactivate()
            userGroup.setLastModificationDate(new Date(System
                .currentTimeMillis()));
            userGroup.setModifiedById(UserAccountHandler
                .getAuthenticatedUser(userAccountDao));
        }
        return changed;
    }

    /**
     * Injects the user group renderer.
     * 
     * @param renderer
     *            The user group renderer to inject.
     * 
     * @spring.property 
     *                  ref="eSciDoc.core.aa.business.renderer.VelocityXmlUserGroupRenderer"
     * @aa
     */
    public void setRenderer(final UserGroupRendererInterface renderer) {
        if (LOG.isDebugEnabled()) {
            LOG.debug(StringUtility.format(
                    "setRenderer", renderer));
        }
        this.renderer = renderer;
    }

    /**
     * Injects the role data access object.
     * 
     * @param roleDao
     *            The role data access object.
     * 
     * @spring.property ref="persistence.EscidocRoleDao"
     * @aa
     */
    public void setRoleDao(final EscidocRoleDaoInterface roleDao) {
        if (LOG.isDebugEnabled()) {
            LOG.debug(StringUtility.format(
                    "setRoleDao", roleDao));
        }
        this.roleDao = roleDao;
    }

    /**
     * Injects the triple store utility bean.
     * 
     * @param tsu
     *            The {@link TripleStoreUtility}.
     * 
     * @spring.property ref="business.TripleStoreUtility"
     * @aa
     */
    public void setTsu(final TripleStoreUtility tsu) {
        if (LOG.isDebugEnabled()) {
            LOG.debug(StringUtility.format("setTsu",
                    tsu));
        }
        this.tsu = tsu;
    }

    /**
     * Injects the user data access object.
     * 
     * @param userAccountDao
     *            The data access object.
     * 
     * @spring.property ref="persistence.UserAccountDao"
     * @aa
     */
    public void setUserAccountDao(final UserAccountDaoInterface userAccountDao) {
        if (LOG.isDebugEnabled()) {
            LOG.debug(StringUtility.format(
                    "setUserAccountDao", userAccountDao));
        }
        this.userAccountDao = userAccountDao;
    }

    /**
     * Injects the user group data access object.
     * 
     * @param userGroupDao
     *            The data access object.
     * 
     * @spring.property ref="persistence.UserGroupDao"
     * @aa
     */
    public void setUserGroupDao(final UserGroupDaoInterface userGroupDao) {
        if (LOG.isDebugEnabled()) {
            LOG.debug(StringUtility.format(
                    "setUserGroupDao", userGroupDao));
        }
        this.userGroupDao = userGroupDao;
    }

    /**
     * Check if the given label is already used as user group name in the
     * database.
     * 
     * @param label
     *            user group name
     * 
     * @return true if the label is still unused
     * @throws SqlDatabaseSystemException
     *             Thrown in case of an internal database error.
     */
    private boolean checkLabelUnique(final String label)
        throws SqlDatabaseSystemException {
        return userGroupDao.findUsergroupByLabel(label) == null;
    }

    /**
     * Injects the policy decision point bean.
     * 
     * @param pdp
     *            The {@link PolicyDecisionPoint}.
     * @spring.property ref="business.PolicyDecisionPoint"
     * @aa
     */
    public void setPdp(final PolicyDecisionPointInterface pdp) {

        LOG.debug("setPdp");

        this.pdp = pdp;
    }

    /**
     * Injects the user group data access object.
     * 
     * @param objectAttributeResolver
     *            The objectAttributeResolver.
     * 
     * @spring.property ref="eSciDoc.core.aa.ObjectAttributeResolver"
     * @aa
     */
    public void setObjectAttributeResolver(
        final ObjectAttributeResolver objectAttributeResolver) {

        if (LOG.isDebugEnabled()) {
            LOG.debug(StringUtility.format(
                "setObjectAttributeResolver", objectAttributeResolver));
        }

        this.objectAttributeResolver = objectAttributeResolver;
    }

}
