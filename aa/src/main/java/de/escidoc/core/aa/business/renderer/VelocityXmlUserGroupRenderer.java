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

import de.escidoc.core.aa.business.persistence.EscidocRole;
import de.escidoc.core.aa.business.persistence.RoleGrant;
import de.escidoc.core.aa.business.persistence.UserAccount;
import de.escidoc.core.aa.business.persistence.UserGroup;
import de.escidoc.core.aa.business.renderer.interfaces.UserGroupRendererInterface;
import de.escidoc.core.common.business.Constants;
import de.escidoc.core.common.business.filter.RecordPacking;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.util.xml.XmlUtility;
import de.escidoc.core.common.util.xml.factory.UserGroupXmlProvider;
import de.escidoc.core.common.util.xml.factory.XmlTemplateProviderConstants;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User group renderer implementation using the velocity template engine.
 *
 * @author André Schenk
 */
@Service("eSciDoc.core.aa.business.renderer.VelocityXmlUserGroupRenderer")
public final class VelocityXmlUserGroupRenderer extends AbstractRenderer implements UserGroupRendererInterface {

    /**
     * Private constructor to prevent initialization.
     */
    private VelocityXmlUserGroupRenderer() {
    }

    /**
     * See Interface for functional description.
     *
     */
    @Override
    public String render(final UserGroup userGroup) throws SystemException {
        final Map<String, Object> values = new HashMap<String, Object>();
        values.put("isRootUserGroup", XmlTemplateProviderConstants.TRUE);
        addCommonValues(values);
        addUserGroupValues(userGroup, values);
        return getUserGroupXmlProvider().getUserGroupXml(values);
    }

    /**
     * Adds the values of the {@link UserGroup} to the provided {@link Map}.
     *
     * @param userGroup The {@link UserGroup}.
     * @param values    The {@link Map} to add the values to.
     * @throws SystemException Thrown in case of an internal error.
     */
    private static void addUserGroupValues(final UserGroup userGroup, final Map<String, Object> values) {
        DateTime lmdDateTime = new DateTime(userGroup.getLastModificationDate());
        lmdDateTime = lmdDateTime.withZone(DateTimeZone.UTC);
        final String lmd = lmdDateTime.toString(Constants.TIMESTAMP_FORMAT);
        values.put("userGroupLastModificationDate", lmd);
        values.put("userGroupHref", userGroup.getHref());
        DateTime creationDateTime = new DateTime(userGroup.getCreationDate());
        creationDateTime = creationDateTime.withZone(DateTimeZone.UTC);
        final String creationDate = creationDateTime.toString(Constants.TIMESTAMP_FORMAT);
        values.put("userGroupCreationDate", creationDate);
        values.put("userGroupName", userGroup.getName());
        values.put("userGroupLabel", userGroup.getLabel());
        values.put("userGroupType", userGroup.getType());
        values.put("userGroupEmail", userGroup.getEmail());
        values.put("userGroupDescription", userGroup.getDescription());
        values.put("userGroupId", userGroup.getId());
        values.put("userGroupActive", userGroup.getActive());
        values.put("userGroupSelectors", userGroup.getMembers());

        final UserAccount createdBy = userGroup.getCreatorId();

        values.put("userGroupCreatedByTitle", createdBy.getName());
        values.put("userGroupCreatedByHref", createdBy.getHref());
        values.put("userGroupCreatedById", createdBy.getId());

        final UserAccount modifiedBy = userGroup.getModifiedById();

        values.put("userGroupModifiedByTitle", modifiedBy.getName());
        values.put("userGroupModifiedByHref", modifiedBy.getHref());
        values.put("userGroupModifiedById", modifiedBy.getId());

        addResourcesValues(userGroup, values);
    }

    /**
     * See Interface for functional description.
     *
     */
    @Override
    public String renderCurrentGrants(final UserGroup userGroup, final List<RoleGrant> currentGrants)
        throws WebserverSystemException {
        final Map<String, Object> values = new HashMap<String, Object>();

        values.put("isRootCurrentGrants", XmlTemplateProviderConstants.TRUE);
        values.put("grantNamespacePrefix", Constants.GRANTS_NS_PREFIX);
        values.put("grantNamespace", Constants.GRANTS_NS_URI);
        values.put(XmlTemplateProviderConstants.ESCIDOC_PROPERTIES_NS_PREFIX, Constants.PROPERTIES_NS_PREFIX);
        values.put(XmlTemplateProviderConstants.ESCIDOC_PROPERTIES_NS, Constants.PROPERTIES_NS_URI);
        values.put(XmlTemplateProviderConstants.ESCIDOC_SREL_NS_PREFIX, Constants.STRUCTURAL_RELATIONS_NS_PREFIX);
        values.put(XmlTemplateProviderConstants.ESCIDOC_SREL_NS, Constants.STRUCTURAL_RELATIONS_NS_URI);
        values.put("currentGrantsTitle", "Grants of " + userGroup.getLabel());
        values.put("currentGrantsHref", XmlUtility.getUserGroupCurrentGrantsHref(userGroup.getId()));
        if (currentGrants != null && !currentGrants.isEmpty()) {
            values.put("currentGrants", currentGrants);
        }
        DateTime lmdDateTime = new DateTime(userGroup.getLastModificationDate());
        lmdDateTime = lmdDateTime.withZone(DateTimeZone.UTC);
        final String lmd = lmdDateTime.toString(Constants.TIMESTAMP_FORMAT);
        values.put(XmlTemplateProviderConstants.VAR_LAST_MODIFICATION_DATE, lmd);

        addEscidocBaseUrl(values);
        return getUserGroupXmlProvider().getCurrentGrantsXml(values);
    }

    /**
     * See Interface for functional description.
     *
     */
    @Override
    public String renderGrant(final RoleGrant grant) throws WebserverSystemException {

        final Map<String, Object> values = new HashMap<String, Object>();

        values.put("isRootGrant", XmlTemplateProviderConstants.TRUE);
        values.put("grantNamespacePrefix", Constants.GRANTS_NS_PREFIX);
        values.put("grantNamespace", Constants.GRANTS_NS_URI);
        values.put(XmlTemplateProviderConstants.ESCIDOC_PROPERTIES_NS_PREFIX, Constants.PROPERTIES_NS_PREFIX);
        values.put(XmlTemplateProviderConstants.ESCIDOC_PROPERTIES_NS, Constants.PROPERTIES_NS_URI);
        values.put(XmlTemplateProviderConstants.ESCIDOC_SREL_NS_PREFIX, Constants.STRUCTURAL_RELATIONS_NS_PREFIX);
        values.put(XmlTemplateProviderConstants.ESCIDOC_SREL_NS, Constants.STRUCTURAL_RELATIONS_NS_URI);
        values.put("grantTitle", grant.getTitle());
        values.put("grantHref", grant.getHref());
        values.put("grantId", grant.getId());
        if (grant.getUserAccountByUserId() != null) {
            values.put("grantUserTitle", grant.getUserAccountByUserId().getName());
            values.put("grantUserHref", grant.getUserAccountByUserId().getHref());
            values.put("grantUserId", grant.getUserAccountByUserId().getId());
        }
        if (grant.getUserGroupByGroupId() != null) {
            values.put("grantGroupTitle", grant.getUserGroupByGroupId().getName());
            values.put("grantGroupHref", grant.getUserGroupByGroupId().getHref());
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
        final String creationDate = creationDateTime.toString(Constants.TIMESTAMP_FORMAT);
        values.put("grantCreationDate", creationDate);
        values.put("grantCreatedByTitle", grant.getUserAccountByCreatorId().getName());
        values.put("grantCreatedByHref", grant.getUserAccountByCreatorId().getHref());
        values.put("grantCreatedById", grant.getUserAccountByCreatorId().getId());
        values.put("grantRemark", grant.getGrantRemark());

        final Date revocationDate = grant.getRevocationDate();
        if (revocationDate != null) {
            DateTime revokationDateTime = new DateTime(grant.getRevocationDate());
            revokationDateTime = revokationDateTime.withZone(DateTimeZone.UTC);
            final String revokationDate = revokationDateTime.toString(Constants.TIMESTAMP_FORMAT);
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
        final String lmd = lmdDateTime.toString(Constants.TIMESTAMP_FORMAT);
        values.put("grantLastModificationDate", lmd);

        return getUserGroupXmlProvider().getGrantXml(values);
    }

    /**
     * See Interface for functional description.
     *
     * @see de.escidoc.core.aa.business.renderer.interfaces.UserGroupRendererInterface
     *      #renderResources(de.escidoc.core.aa.business.UserGroup)
     */
    @Override
    public String renderResources(final UserGroup userGroup) throws WebserverSystemException {
        final Map<String, Object> values = new HashMap<String, Object>();

        values.put("isRootResources", XmlTemplateProviderConstants.TRUE);
        addResourcesValues(userGroup, values);
        addCommonValues(values);
        DateTime lmdDateTime = new DateTime(userGroup.getLastModificationDate());
        lmdDateTime = lmdDateTime.withZone(DateTimeZone.UTC);
        final String lmd = lmdDateTime.toString(Constants.TIMESTAMP_FORMAT);
        values.put("userGroupLastModificationDate", lmd);

        return getUserGroupXmlProvider().getResourcesXml(values);
    }

    /**
     * See Interface for functional description.
     *
     * @param recordPacking A string to determine how the record should be escaped in the response. Defined values are
     *                      'string' and 'xml'. The default is 'xml'.
     * @see de.escidoc.core.aa.business.renderer.interfaces.UserGroupRendererInterface
     *      #renderUserGroups(de.escidoc.core.aa.business.UserGroup)
     */
    @Override
    public String renderUserGroups(final List<UserGroup> userGroups, final RecordPacking recordPacking)
        throws SystemException {
        final Map<String, Object> values = new HashMap<String, Object>();

        values.put("isRootUserGroup", XmlTemplateProviderConstants.TRUE);
        values.put("recordPacking", recordPacking);
        addCommonValues(values);
        addUserGroupListValues(values);
        final Collection<Map<String, Object>> userGroupsValues = new ArrayList<Map<String, Object>>(userGroups.size());
        for (final UserGroup userGroup : userGroups) {
            final Map<String, Object> userGroupValues = new HashMap<String, Object>();
            addUserGroupValues(userGroup, userGroupValues);
            userGroupsValues.add(userGroupValues);
        }
        values.put("userGroups", userGroupsValues);
        return getUserGroupXmlProvider().getUserGroupsSrwXml(values);
    }

    /**
     * Adds the common values to the provided map.
     *
     * @param values The map to add values to.
     * @throws WebserverSystemException Thrown in case of an internal error.
     */
    private void addCommonValues(final Map<String, Object> values) {

        addUserGroupNamespaceValues(values);
        addPropertiesNamespaceValues(values);
        addStructuralRelationNamespaceValues(values);
        addEscidocBaseUrl(values);
    }

    /**
     * Adds the user group name space values.
     *
     * @param values The {@link Map} to that the values shall be added.
     */
    private static void addUserGroupNamespaceValues(final Map<String, Object> values) {
        values.put("userGroupNamespacePrefix", Constants.USER_GROUP_NS_PREFIX);
        values.put("userGroupNamespace", Constants.USER_GROUP_NS_URI);
    }

    /**
     * Adds the user group list values to the provided map.
     *
     * @param values The map to add values to.
     */
    private static void addUserGroupListValues(final Map<String, Object> values) {

        addUserGroupsNamespaceValues(values);
        values.put("searchResultNamespace", Constants.SEARCH_RESULT_NS_URI);
        values.put("userGroupListTitle", "User Group List");
    }

    /**
     * Adds the values related to the user groups name space to the provided {@link Map}.
     *
     * @param values The MAP to add the values to.
     */
    private static void addUserGroupsNamespaceValues(final Map<String, Object> values) {

        values.put("userGroupListNamespacePrefix", Constants.USER_GROUP_LIST_NS_PREFIX);
        values.put("userGroupListNamespace", Constants.USER_GROUP_LIST_NS_URI);
    }

    /**
     * Adds the escidoc base URL to the provided map.
     *
     * @param values The map to add values to.
     * @throws WebserverSystemException Thrown in case of an internal error.
     */
    private static void addEscidocBaseUrl(final Map<String, Object> values) {

        values.put(XmlTemplateProviderConstants.VAR_ESCIDOC_BASE_URL, XmlUtility.getEscidocBaseUrl());
    }

    /**
     * Adds the resources specific values to the provided map.
     *
     * @param userGroup The user group for that data shall be created.
     * @param values    The map to add values to.
     */
    private static void addResourcesValues(final UserGroup userGroup, final Map<String, Object> values) {

        values.put("resourcesHref", XmlUtility.getUserGroupResourcesHref(userGroup.getId()));
        values.put("currentGrantsHref", XmlUtility.getUserGroupCurrentGrantsHref(userGroup.getId()));
    }

    /**
     * Gets the {@code UserGroupXmlProvider} object.
     *
     * @return Returns the {@code UserGroupXmlProvider} object.
     * @throws WebserverSystemException Thrown in case of an internal error.
     */
    private static UserGroupXmlProvider getUserGroupXmlProvider() {

        return UserGroupXmlProvider.getInstance();
    }
}
