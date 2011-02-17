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
package de.escidoc.core.aa.business.renderer;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import de.escidoc.core.aa.business.persistence.EscidocRole;
import de.escidoc.core.aa.business.persistence.RoleGrant;
import de.escidoc.core.aa.business.persistence.UserAccount;
import de.escidoc.core.aa.business.persistence.UserGroup;
import de.escidoc.core.aa.business.renderer.interfaces.UserGroupRendererInterface;
import de.escidoc.core.common.business.Constants;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.util.xml.XmlUtility;
import de.escidoc.core.common.util.xml.factory.UserGroupXmlProvider;
import de.escidoc.core.common.util.xml.factory.XmlTemplateProvider;

/**
 * User group renderer implementation using the velocity template engine.
 * 
 * @author sche
 * @spring.bean 
 *              id="eSciDoc.core.aa.business.renderer.VelocityXmlUserGroupRenderer"
 * @aa
 */
public final class VelocityXmlUserGroupRenderer extends AbstractRenderer
    implements UserGroupRendererInterface {

    /**
     * Private constructor to prevent initialization.
     */
    private VelocityXmlUserGroupRenderer() {
    }

    // CHECKSTYLE:JAVADOC-OFF

    /**
     * See Interface for functional description.
     * 
     * @param userGroup
     * 
     * @return
     * @throws SystemException
     * @see de.escidoc.core.aa.business.renderer.interfaces.
     *      UserGroupRendererInterface#render(Map)
     * @aa
     */
    public String render(final UserGroup userGroup) throws SystemException {
        Map<String, Object> values = new HashMap<String, Object>();

        values.put("isRootUserGroup", "true");
        addCommonValues(values);
        addUserGroupValues(userGroup, values);

        final String ret = getUserGroupXmlProvider().getUserGroupXml(values);

        return ret;
    }

    /**
     * Adds the values of the {@link UserGroup} to the provided {@link Map}.
     * 
     * @param userGroup
     *            The {@link UserGroup}.
     * @param values
     *            The {@link Map} to add the values to.
     * 
     * @throws SystemException
     *             Thrown in case of an internal error.
     */
    private void addUserGroupValues(
        final UserGroup userGroup, final Map<String, Object> values)
        throws SystemException {
        DateTime lmdDateTime =
            new DateTime(userGroup.getLastModificationDate());
        lmdDateTime = lmdDateTime.withZone(DateTimeZone.UTC);
        String lmd = lmdDateTime.toString(Constants.TIMESTAMP_FORMAT);
        values.put("userGroupLastModificationDate", lmd);
        values.put("userGroupHref", userGroup.getHref());
        DateTime creationDateTime = new DateTime(userGroup.getCreationDate());
        creationDateTime = creationDateTime.withZone(DateTimeZone.UTC);
        String creationDate =
            creationDateTime.toString(Constants.TIMESTAMP_FORMAT);
        values.put("userGroupCreationDate", creationDate);
        values.put("userGroupName", userGroup.getName());
        values.put("userGroupLabel", userGroup.getLabel());
        values.put("userGroupType", userGroup.getType());
        values.put("userGroupEmail", userGroup.getEmail());
        values.put("userGroupDescription", userGroup.getDescription());
        values.put("userGroupId", userGroup.getId());
        values.put("userGroupActive", userGroup.getActive());
        values.put("userGroupSelectors", userGroup.getMembers());

        UserAccount createdBy = userGroup.getCreatorId();

        values.put("userGroupCreatedByTitle", createdBy.getName());
        values.put("userGroupCreatedByHref", createdBy.getHref());
        values.put("userGroupCreatedById", createdBy.getId());

        UserAccount modifiedBy = userGroup.getModifiedById();

        values.put("userGroupModifiedByTitle", modifiedBy.getName());
        values.put("userGroupModifiedByHref", modifiedBy.getHref());
        values.put("userGroupModifiedById", modifiedBy.getId());

        addResourcesValues(userGroup, values);
    }

    /**
     * See Interface for functional description.
     * 
     * @param userGroup
     * @param currentGrants
     * @return
     * @throws WebserverSystemException
     * @see de.escidoc.core.aa.business.renderer.interfaces.
     *      UserGroupRendererInterface#renderCurrentGrants
     *      (de.escidoc.core.aa.business.UserGroup, java.util.List)
     * @aa
     */
    public String renderCurrentGrants(
        final UserGroup userGroup, final List<RoleGrant> currentGrants)
        throws WebserverSystemException {
        Map<String, Object> values = new HashMap<String, Object>();

        values.put("isRootCurrentGrants", "true");
        values.put("grantNamespacePrefix", Constants.GRANTS_NS_PREFIX);
        values.put("grantNamespace", Constants.GRANTS_NS_URI);
        values.put(XmlTemplateProvider.ESCIDOC_PROPERTIES_NS_PREFIX,
            Constants.PROPERTIES_NS_PREFIX);
        values.put(XmlTemplateProvider.ESCIDOC_PROPERTIES_NS,
            Constants.PROPERTIES_NS_URI);
        values.put(XmlTemplateProvider.ESCIDOC_SREL_NS_PREFIX,
            Constants.STRUCTURAL_RELATIONS_NS_PREFIX);
        values.put(XmlTemplateProvider.ESCIDOC_SREL_NS,
            Constants.STRUCTURAL_RELATIONS_NS_URI);
        values.put("currentGrantsTitle", "Grants of " + userGroup.getLabel());
        values.put("currentGrantsHref",
            XmlUtility.getUserGroupCurrentGrantsHref(userGroup.getId()));
        if (currentGrants != null && !currentGrants.isEmpty()) {
            values.put("currentGrants", currentGrants);
        }
        DateTime lmdDateTime =
            new DateTime(userGroup.getLastModificationDate());
        lmdDateTime = lmdDateTime.withZone(DateTimeZone.UTC);
        String lmd = lmdDateTime.toString(Constants.TIMESTAMP_FORMAT);
        values.put(XmlTemplateProvider.VAR_LAST_MODIFICATION_DATE, lmd);

        addEscidocBaseUrl(values);
        return getUserGroupXmlProvider().getCurrentGrantsXml(values);
    }

    /**
     * See Interface for functional description.
     * 
     * @param grant
     * @return
     * @throws WebserverSystemException
     * @see de.escidoc.core.aa.business.renderer.interfaces.
     *      UserGroupRendererInterface#renderGrant
     *      (de.escidoc.core.aa.business.persistence.RoleGrant)
     * @aa
     */
    public String renderGrant(final RoleGrant grant)
        throws WebserverSystemException {

        Map<String, Object> values = new HashMap<String, Object>();

        values.put("isRootGrant", XmlTemplateProvider.TRUE);
        values.put("grantNamespacePrefix", Constants.GRANTS_NS_PREFIX);
        values.put("grantNamespace", Constants.GRANTS_NS_URI);
        values.put(XmlTemplateProvider.ESCIDOC_PROPERTIES_NS_PREFIX,
            Constants.PROPERTIES_NS_PREFIX);
        values.put(XmlTemplateProvider.ESCIDOC_PROPERTIES_NS,
            Constants.PROPERTIES_NS_URI);
        values.put(XmlTemplateProvider.ESCIDOC_SREL_NS_PREFIX,
            Constants.STRUCTURAL_RELATIONS_NS_PREFIX);
        values.put(XmlTemplateProvider.ESCIDOC_SREL_NS,
            Constants.STRUCTURAL_RELATIONS_NS_URI);
        values.put("grantTitle", grant.getTitle());
        values.put("grantHref", grant.getHref());
        values.put("grantId", grant.getId());
        if (grant.getUserAccountByUserId() != null) {
            values.put("grantUserTitle", grant
                .getUserAccountByUserId().getName());
            values.put("grantUserHref", grant
                .getUserAccountByUserId().getHref());
            values.put("grantUserId", grant.getUserAccountByUserId().getId());
        }
        if (grant.getUserGroupByGroupId() != null) {
            values.put("grantGroupTitle", grant
                .getUserGroupByGroupId().getName());
            values.put("grantGroupHref", grant
                .getUserGroupByGroupId().getHref());
            values.put("grantGroupId", grant.getUserGroupByGroupId().getId());
        }
        final EscidocRole escidocRole = grant.getEscidocRole();
        values.put("grantRoleTitle", escidocRole.getRoleName());
        final String roleId = escidocRole.getId();
        values.put("grantRoleHref", XmlUtility.getRoleHref(roleId));
        values.put("grantRoleId", roleId);
        values.put("grantObjectRefHref", grant.getObjectHref());
        values.put("grantObjectRefTitle", grant.getObjectTitle());
        values.put("grantObjectRefId", grant.getObjectId());
        DateTime creationDateTime = new DateTime(grant.getCreationDate());
        creationDateTime = creationDateTime.withZone(DateTimeZone.UTC);
        String creationDate =
            creationDateTime.toString(Constants.TIMESTAMP_FORMAT);
        values.put("grantCreationDate", creationDate);
        values.put("grantCreatedByTitle", grant
            .getUserAccountByCreatorId().getName());
        values.put("grantCreatedByHref", grant
            .getUserAccountByCreatorId().getHref());
        values.put("grantCreatedById", grant
            .getUserAccountByCreatorId().getId());
        values.put("grantRemark", grant.getGrantRemark());

        final Date revocationDate = grant.getRevocationDate();
        if (revocationDate != null) {
            DateTime revokationDateTime =
                new DateTime(grant.getRevocationDate());
            revokationDateTime = revokationDateTime.withZone(DateTimeZone.UTC);
            String revokationDate =
                revokationDateTime.toString(Constants.TIMESTAMP_FORMAT);
            values.put("grantRevocationDate", revokationDate);
            final UserAccount revokedBy = grant.getUserAccountByRevokerId();
            values.put("grantRevokedByHref", revokedBy.getHref());
            values.put("grantRevokedById", revokedBy.getId());
            values.put("grantRevokedByTitle", revokedBy.getName());
            values.put("grantRevocationRemark", grant.getRevocationRemark());
        }

        addEscidocBaseUrl(values);
        DateTime lmdDateTime = new DateTime(grant.getLastModificationDate());
        lmdDateTime = lmdDateTime.withZone(DateTimeZone.UTC);
        String lmd = lmdDateTime.toString(Constants.TIMESTAMP_FORMAT);
        values.put("grantLastModificationDate", lmd);

        return getUserGroupXmlProvider().getGrantXml(values);
    }

    /**
     * See Interface for functional description.
     * 
     * @param userGroup
     * @return
     * @throws WebserverSystemException
     * @see de.escidoc.core.aa.business.renderer.interfaces.
     *      UserGroupRendererInterface
     *      #renderResources(de.escidoc.core.aa.business.UserGroup)
     * @aa
     */
    public String renderResources(final UserGroup userGroup)
        throws WebserverSystemException {
        Map<String, Object> values = new HashMap<String, Object>();

        values.put("isRootResources", XmlTemplateProvider.TRUE);
        addResourcesValues(userGroup, values);
        addCommonValues(values);
        DateTime lmdDateTime =
            new DateTime(userGroup.getLastModificationDate());
        lmdDateTime = lmdDateTime.withZone(DateTimeZone.UTC);
        String lmd = lmdDateTime.toString(Constants.TIMESTAMP_FORMAT);
        values.put("userGroupLastModificationDate", lmd);

        return getUserGroupXmlProvider().getResourcesXml(values);
    }

    /**
     * See Interface for functional description.
     * 
     * @param userGroups
     * @param recordPacking
     *            A string to determine how the record should be escaped in the
     *            response. Defined values are 'string' and 'xml'. The default
     *            is 'xml'.
     * 
     * @return
     * @throws WebserverSystemException
     * @see de.escidoc.core.aa.business.renderer.interfaces.
     *      UserGroupRendererInterface
     *      #renderUserGroups(de.escidoc.core.aa.business.UserGroup)
     */
    public String renderUserGroups(
        final List<UserGroup> userGroups, final String recordPacking)
        throws SystemException {
        Map<String, Object> values = new HashMap<String, Object>();

        values.put("isRootUserGroup", XmlTemplateProvider.TRUE);
        addCommonValues(values);
        addUserGroupListValues(values);

        final List<Map<String, Object>> userGroupsValues =
            new ArrayList<Map<String, Object>>(userGroups.size());

        for (UserGroup userGroup1 : userGroups) {
            UserGroup userGroup = userGroup1;
            Map<String, Object> userGroupValues = new HashMap<String, Object>();

            addUserGroupValues(userGroup, userGroupValues);
            userGroupsValues.add(userGroupValues);
        }
        values.put("userGroups", userGroupsValues);
        return getUserGroupXmlProvider().getUserGroupsSrwXml(values);
    }

    // CHECKSTYLE:JAVADOC-ON

    /**
     * Adds the common values to the provided map.
     * 
     * @param values
     *            The map to add values to.
     * 
     * @throws WebserverSystemException
     *             Thrown in case of an internal error.
     * @aa
     */
    private void addCommonValues(final Map<String, Object> values)
        throws WebserverSystemException {

        addUserGroupNamespaceValues(values);
        addPropertiesNamespaceValues(values);
        addStructuralRelationNamespaceValues(values);
        addEscidocBaseUrl(values);
    }

    /**
     * Adds the user group name space values.
     * 
     * @param values
     *            The {@link Map} to that the values shall be added.
     * @aa
     */
    private void addUserGroupNamespaceValues(final Map<String, Object> values) {
        values.put("userGroupNamespacePrefix", Constants.USER_GROUP_NS_PREFIX);
        values.put("userGroupNamespace", Constants.USER_GROUP_NS_URI);
    }

    /**
     * Adds the user group list values to the provided map.
     * 
     * @param values
     *            The map to add values to.
     * @aa
     */
    private void addUserGroupListValues(final Map<String, Object> values) {

        addUserGroupsNamespaceValues(values);
        values.put("searchResultNamespace", Constants.SEARCH_RESULT_NS_URI);
        values.put("userGroupListTitle", "User Group List");
    }

    /**
     * Adds the values related to the user groups name space to the provided
     * {@link Map}.
     * 
     * @param values
     *            The MAP to add the values to.
     * @aa
     */
    private void addUserGroupsNamespaceValues(final Map<String, Object> values) {

        values.put("userGroupListNamespacePrefix",
            Constants.USER_GROUP_LIST_NS_PREFIX);
        values.put("userGroupListNamespace", Constants.USER_GROUP_LIST_NS_URI);
    }

    /**
     * Adds the escidoc base URL to the provided map.
     * 
     * @param values
     *            The map to add values to.
     * @throws WebserverSystemException
     *             Thrown in case of an internal error.
     * @aa
     */
    private void addEscidocBaseUrl(final Map<String, Object> values)
        throws WebserverSystemException {

        values.put(XmlTemplateProvider.VAR_ESCIDOC_BASE_URL,
            XmlUtility.getEscidocBaseUrl());
    }

    /**
     * Adds the resources specific values to the provided map.
     * 
     * @param userGroup
     *            The user group for that data shall be created.
     * @param values
     *            The map to add values to.
     * @aa
     */
    private void addResourcesValues(
        final UserGroup userGroup, final Map<String, Object> values) {

        values.put("resourcesHref",
            XmlUtility.getUserGroupResourcesHref(userGroup.getId()));
        values.put("currentGrantsHref",
            XmlUtility.getUserGroupCurrentGrantsHref(userGroup.getId()));
    }

    /**
     * Gets the <code>UserGroupXmlProvider</code> object.
     * 
     * @return Returns the <code>UserGroupXmlProvider</code> object.
     * @throws WebserverSystemException
     *             Thrown in case of an internal error.
     * @aa
     */
    private UserGroupXmlProvider getUserGroupXmlProvider()
        throws WebserverSystemException {

        return UserGroupXmlProvider.getInstance();
    }
}
