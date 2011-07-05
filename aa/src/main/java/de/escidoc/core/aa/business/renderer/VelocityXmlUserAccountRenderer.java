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
import de.escidoc.core.aa.business.persistence.UserAttribute;
import de.escidoc.core.aa.business.persistence.UserPreference;
import de.escidoc.core.aa.business.renderer.interfaces.UserAccountRendererInterface;
import de.escidoc.core.common.business.Constants;
import de.escidoc.core.common.business.filter.RecordPacking;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.util.xml.XmlUtility;
import de.escidoc.core.common.util.xml.factory.UserAccountXmlProvider;
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
import java.util.Set;

/**
 * User account renderer implementation using the velocity template engine.
 *
 * @author Torsten Tetteroo
 */
@Service("eSciDoc.core.aa.business.renderer.VelocityXmlUserAccountRenderer")
public final class VelocityXmlUserAccountRenderer extends AbstractRenderer implements UserAccountRendererInterface {

    private static final String USER_ACCOUNT_ID = "userAccountId";

    /**
     * Private constructor to prevent initialization.
     */
    private VelocityXmlUserAccountRenderer() {
    }

    /**
     * See Interface for functional description.
     *
     * @see UserAccountRendererInterface# render(Map)
     */
    @Override
    public String render(final UserAccount userAccount) throws SystemException {

        final Map<String, Object> values = new HashMap<String, Object>();

        values.put("isRootUserAccount", XmlTemplateProviderConstants.TRUE);
        addCommonValues(values);

        addUserAccountValues(userAccount, values);
        return getUserAccountXmlProvider().getUserAccountXml(values);
    }

    /**
     * Adds the values of the {@link UserAccount} to the provided {@link Map}.
     *
     * @param userAccount The {@link UserAccount}.
     * @param values      The {@link Map} to add the values to.
     * @throws SystemException Thrown in case of an internal error.
     */
    private static void addUserAccountValues(final UserAccount userAccount, final Map<String, Object> values) {
        DateTime lmdDateTime = new DateTime(userAccount.getLastModificationDate());
        lmdDateTime = lmdDateTime.withZone(DateTimeZone.UTC);
        final String lmd = lmdDateTime.toString(Constants.TIMESTAMP_FORMAT);
        values.put("userAccountLastModificationDate", lmd);
        values.put("userAccountHref", userAccount.getHref());
        DateTime creationDateTime = new DateTime(userAccount.getCreationDate());
        creationDateTime = creationDateTime.withZone(DateTimeZone.UTC);
        final String creationDate = creationDateTime.toString(Constants.TIMESTAMP_FORMAT);
        values.put("userAccountCreationDate", creationDate);
        values.put("userAccountName", userAccount.getName());
        values.put("userAccountLoginName", userAccount.getLoginname());
        values.put(USER_ACCOUNT_ID, userAccount.getId());
        values.put("userAccountActive", userAccount.getActive());

        final UserAccount createdBy = userAccount.getUserAccountByCreatorId();
        values.put("userAccountCreatedByTitle", createdBy.getName());
        values.put("userAccountCreatedByHref", createdBy.getHref());
        values.put("userAccountCreatedById", createdBy.getId());

        final UserAccount modifiedBy = userAccount.getUserAccountByModifiedById();
        values.put("userAccountModifiedByTitle", modifiedBy.getName());
        values.put("userAccountModifiedByHref", modifiedBy.getHref());
        values.put("userAccountModifiedById", modifiedBy.getId());

        addResourcesValues(userAccount, values);
    }

    /**
     * See Interface for functional description.
     */
    @Override
    public String renderCurrentGrants(final UserAccount userAccount, final List<RoleGrant> currentGrants)
        throws WebserverSystemException {

        final Map<String, Object> values = new HashMap<String, Object>();
        values.put("isRootCurrentGrants", XmlTemplateProviderConstants.TRUE);
        values.put("grantNamespacePrefix", Constants.GRANTS_NS_PREFIX);
        values.put("grantNamespace", Constants.GRANTS_NS_URI);
        values.put(XmlTemplateProviderConstants.ESCIDOC_PROPERTIES_NS_PREFIX, Constants.PROPERTIES_NS_PREFIX);
        values.put(XmlTemplateProviderConstants.ESCIDOC_PROPERTIES_NS, Constants.PROPERTIES_NS_URI);
        values.put(XmlTemplateProviderConstants.ESCIDOC_SREL_NS_PREFIX, Constants.STRUCTURAL_RELATIONS_NS_PREFIX);
        values.put(XmlTemplateProviderConstants.ESCIDOC_SREL_NS, Constants.STRUCTURAL_RELATIONS_NS_URI);
        values.put("currentGrantsTitle", "Grants of " + userAccount.getLoginname());
        values.put("currentGrantsHref", XmlUtility.getCurrentGrantsHref(userAccount.getId()));
        if (currentGrants != null && !currentGrants.isEmpty()) {
            values.put("currentGrants", currentGrants);
        }
        DateTime lmdDateTime = new DateTime(userAccount.getLastModificationDate());
        lmdDateTime = lmdDateTime.withZone(DateTimeZone.UTC);
        final String lmd = lmdDateTime.toString(Constants.TIMESTAMP_FORMAT);
        values.put(XmlTemplateProviderConstants.VAR_LAST_MODIFICATION_DATE, lmd);
        addEscidocBaseUrl(values);
        return getUserAccountXmlProvider().getCurrentGrantsXml(values);
    }

    /**
     * See Interface for functional description.
     *
     * @param recordPacking A string to determine how the record should be escaped in the response. Defined values are
     *                      'string' and 'xml'. The default is 'xml'.
     * @see UserAccountRendererInterface#renderCurrentGrants (de.escidoc.core.aa.business.UserAccount, java.util.List)
     */
    @Override
    public String renderGrants(
        final List<RoleGrant> grants, final String numberOfHits, final String offset, final String limit,
        final RecordPacking recordPacking) throws WebserverSystemException {

        final Map<String, Object> values = new HashMap<String, Object>();
        values.put("isRootGrants", XmlTemplateProviderConstants.TRUE);
        values.put("grantNamespacePrefix", Constants.GRANTS_NS_PREFIX);
        values.put("grantNamespace", Constants.GRANTS_NS_URI);
        values.put(XmlTemplateProviderConstants.ESCIDOC_PROPERTIES_NS_PREFIX, Constants.PROPERTIES_NS_PREFIX);
        values.put(XmlTemplateProviderConstants.ESCIDOC_PROPERTIES_NS, Constants.PROPERTIES_NS_URI);
        values.put(XmlTemplateProviderConstants.ESCIDOC_SREL_NS_PREFIX, Constants.STRUCTURAL_RELATIONS_NS_PREFIX);
        values.put(XmlTemplateProviderConstants.ESCIDOC_SREL_NS, Constants.STRUCTURAL_RELATIONS_NS_URI);
        values.put("searchResultNamespace", Constants.SEARCH_RESULT_NS_URI);
        values.put("recordPacking", recordPacking);
        values.put("numberOfHits", numberOfHits);
        values.put("offset", offset);
        values.put("limit", limit);
        if (grants != null && !grants.isEmpty()) {
            values.put("grants", grants);
        }

        addEscidocBaseUrl(values);
        return getUserAccountXmlProvider().getGrantsXml(values);
    }

    /**
     * See Interface for functional description.
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
            DateTime revokationDateTime = new DateTime(revocationDate);
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
        return getUserAccountXmlProvider().getGrantXml(values);
    }

    /**
     * See Interface for functional description.
     *
     * @see UserAccountRendererInterface #renderResources(de.escidoc.core.aa.business.UserAccount)
     */
    @Override
    public String renderResources(final UserAccount userAccount) throws WebserverSystemException {

        final Map<String, Object> values = new HashMap<String, Object>();
        values.put("isRootResources", XmlTemplateProviderConstants.TRUE);
        addResourcesValues(userAccount, values);
        addCommonValues(values);
        DateTime lmdDateTime = new DateTime(userAccount.getLastModificationDate());
        lmdDateTime = lmdDateTime.withZone(DateTimeZone.UTC);
        final String lmd = lmdDateTime.toString(Constants.TIMESTAMP_FORMAT);
        values.put("userAccountLastModificationDate", lmd);
        return getUserAccountXmlProvider().getResourcesXml(values);
    }

    /**
     * See Interface for functional description.
     *
     * @param recordPacking A string to determine how the record should be escaped in the response. Defined values are
     *                      'string' and 'xml'. The default is 'xml'.
     * @see UserAccountRendererInterface #renderUserAccounts(de.escidoc.core.aa.business.UserAccount)
     */
    @Override
    public String renderUserAccounts(final List<UserAccount> userAccounts, final RecordPacking recordPacking)
        throws SystemException {

        final Map<String, Object> values = new HashMap<String, Object>();
        values.put("isRootUserAccount", XmlTemplateProviderConstants.TRUE);
        values.put("recordPacking", recordPacking);
        addCommonValues(values);
        addUserAccountListValues(values);

        final Collection<Map<String, Object>> userAccountsValues =
            new ArrayList<Map<String, Object>>(userAccounts.size());
        for (final UserAccount userAccount : userAccounts) {
            final Map<String, Object> userAccountValues = new HashMap<String, Object>();
            addUserAccountValues(userAccount, userAccountValues);
            userAccountsValues.add(userAccountValues);
        }
        values.put("userAccounts", userAccountsValues);
        return getUserAccountXmlProvider().getUserAccountsXml(values);
    }

    /**
     * Adds the common values to the provided map.
     *
     * @param values The map to add values to.
     * @throws WebserverSystemException Thrown in case of an internal error.
     */
    private void addCommonValues(final Map<String, Object> values) {

        addUserAccountNamespaceValues(values);
        addPropertiesNamespaceValues(values);
        addStructuralRelationNamespaceValues(values);
        addEscidocBaseUrl(values);
    }

    /**
     * Adds the user account name space values.
     *
     * @param values The {@link Map} to that the values shall be added.
     */
    private static void addUserAccountNamespaceValues(final Map<String, Object> values) {
        values.put("userAccountNamespacePrefix", Constants.USER_ACCOUNT_NS_PREFIX);
        values.put("userAccountNamespace", Constants.USER_ACCOUNT_NS_URI);
    }

    /**
     * Adds the user account list values to the provided map.
     *
     * @param values The map to add values to.
     */
    private static void addUserAccountListValues(final Map<String, Object> values) {

        addUserAccountsNamespaceValues(values);
        values.put("userAccountListTitle", "User Account List");
    }

    /**
     * Adds the values related to the user accounts name space to the provided {@link Map}.
     *
     * @param values The MAP to add the values to.
     */
    private static void addUserAccountsNamespaceValues(final Map<String, Object> values) {

        values.put("userAccountListNamespacePrefix", Constants.USER_ACCOUNT_LIST_NS_PREFIX);
        values.put("userAccountListNamespace", Constants.USER_ACCOUNT_LIST_NS_URI);
        values.put("searchResultNamespace", Constants.SEARCH_RESULT_NS_URI);
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
     * @param userAccount The user account for that data shall be created.
     * @param values      The map to add values to.
     */
    private static void addResourcesValues(final UserAccount userAccount, final Map<String, Object> values) {

        values.put("resourcesHref", XmlUtility.getUserAccountResourcesHref(userAccount.getId()));
        values.put("currentGrantsHref", XmlUtility.getCurrentGrantsHref(userAccount.getId()));
        values.put("preferencesHref", XmlUtility.getPreferencesHref(userAccount.getId()));
        values.put("attributesHref", XmlUtility.getAttributesHref(userAccount.getId()));
    }

    /**
     * Gets the {@code UserAccountXmlProvider} object.
     *
     * @return Returns the {@code UserAccountXmlProvider} object.
     * @throws WebserverSystemException Thrown in case of an internal error.
     */
    private static UserAccountXmlProvider getUserAccountXmlProvider() {

        return UserAccountXmlProvider.getInstance();
    }

    @Override
    public String renderPreference(final UserAccount userAccount, final UserPreference preference)
        throws WebserverSystemException {
        return renderPreference(userAccount, preference, true);
    }

    private static String renderPreference(
        final UserAccount userAccount, final UserPreference preference, final boolean isRootPreference)
        throws WebserverSystemException {

        final Map<String, Object> values = new HashMap<String, Object>();

        addPreferencesCommonValues(values);
        if (isRootPreference) {
            values.put("isRootPreference", XmlTemplateProviderConstants.TRUE);
            DateTime lmdDateTime = new DateTime(userAccount.getLastModificationDate());
            lmdDateTime = lmdDateTime.withZone(DateTimeZone.UTC);
            final String lmd = lmdDateTime.toString(Constants.TIMESTAMP_FORMAT);
            values.put("userAccountLastModificationDate", lmd);
            values.put(USER_ACCOUNT_ID, userAccount.getId());
            addEscidocBaseUrl(values);
        }

        values.put("userAccountPreferenceName", preference.getName());
        values.put("userAccountPreferenceValue", preference.getValue());
        return getUserAccountXmlProvider().getPreferenceXml(values);
    }

    @Override
    public String renderPreferences(final UserAccount userAccount, final Set<UserPreference> preferences)
        throws WebserverSystemException {

        final Map<String, Object> values = new HashMap<String, Object>();

        values.put("isRootPreference", false);
        addPreferencesCommonValues(values);
        DateTime lmdDateTime = new DateTime(userAccount.getLastModificationDate());
        lmdDateTime = lmdDateTime.withZone(DateTimeZone.UTC);
        final String lmd = lmdDateTime.toString(Constants.TIMESTAMP_FORMAT);
        values.put("userAccountLastModificationDate", lmd);
        values.put(USER_ACCOUNT_ID, userAccount.getId());
        addEscidocBaseUrl(values);

        final Collection<Map<String, String>> userAccountPreferencesValues = new ArrayList<Map<String, String>>();
        for (final UserPreference preference : preferences) {
            final Map<String, String> m = new HashMap<String, String>();
            m.put("userAccountPreferenceName", preference.getName());
            m.put("userAccountPreferenceValue", preference.getValue());
            userAccountPreferencesValues.add(m);
        }
        values.put("userAccountPreferences", userAccountPreferencesValues);
        return getUserAccountXmlProvider().getPreferencesXml(values);
    }

    private static void addPreferencesCommonValues(final Map<String, Object> values) {
        values.put("preferencesNamespacePrefix", Constants.USER_PREFERENCES_NS_PREFIX);
        values.put("preferencesNamespace", Constants.USER_PREFERENCES_NS_URI);
    }

    /**
     * See Interface for functional description.
     *
     * @param attribute attribute
     * @return String rendered attribute
     */
    @Override
    public String renderAttribute(final UserAttribute attribute) throws WebserverSystemException {
        return renderAttribute(attribute, XmlTemplateProviderConstants.TRUE);
    }

    /**
     * renders one user-attribute.
     *
     * @param attribute       attribute
     * @param isRootAttribute if root attribute
     * @return String rendered attribute
     * @throws WebserverSystemException e
     */
    private String renderAttribute(final UserAttribute attribute, final String isRootAttribute)
        throws WebserverSystemException {

        final Map<String, Object> values = new HashMap<String, Object>();

        values.put("isRootAttribute", isRootAttribute);
        addAttributesCommonValues(values);
        if (XmlTemplateProviderConstants.TRUE.equals(isRootAttribute)) {
            DateTime lmdDateTime = new DateTime(attribute.getUserAccountByUserId().getLastModificationDate());
            lmdDateTime = lmdDateTime.withZone(DateTimeZone.UTC);
            final String lmd = lmdDateTime.toString(Constants.TIMESTAMP_FORMAT);
            values.put(XmlTemplateProviderConstants.VAR_LAST_MODIFICATION_DATE, lmd);
            values.put(USER_ACCOUNT_ID, attribute.getUserAccountByUserId().getId());
        }
        values.put("userAccountAttributeId", attribute.getId());
        values.put("userAccountAttributeName", attribute.getName());
        values.put("userAccountAttributeValue", attribute.getValue());
        values.put("userAccountAttributeIsInternal", attribute.getInternal());
        return getUserAccountXmlProvider().getAttributeXml(values);
    }

    /**
     * See Interface for functional description.
     *
     * @param userAccount user-account-do
     * @param attributes  set containing the attributes
     * @return String rendered attributes
     */
    @Override
    public String renderAttributes(final UserAccount userAccount, final Set<UserAttribute> attributes)
        throws WebserverSystemException {

        final Map<String, Object> values = new HashMap<String, Object>();

        addAttributesCommonValues(values);
        DateTime lmdDateTime = new DateTime(userAccount.getLastModificationDate());
        lmdDateTime = lmdDateTime.withZone(DateTimeZone.UTC);
        final String lmd = lmdDateTime.toString(Constants.TIMESTAMP_FORMAT);
        values.put(XmlTemplateProviderConstants.VAR_LAST_MODIFICATION_DATE, lmd);
        values.put(USER_ACCOUNT_ID, userAccount.getId());
        addEscidocBaseUrl(values);

        final Collection<Map<String, Object>> userAccountAttributesValues = new ArrayList<Map<String, Object>>();
        for (final UserAttribute attribute : attributes) {
            final Map<String, Object> m = new HashMap<String, Object>();
            m.put(USER_ACCOUNT_ID, userAccount.getId());
            m.put("userAccountAttributeId", attribute.getId());
            m.put("userAccountAttributeName", attribute.getName());
            m.put("userAccountAttributeValue", attribute.getValue());
            m.put("userAccountAttributeIsInternal", attribute.getInternal());
            userAccountAttributesValues.add(m);
        }
        values.put("userAccountAttributes", userAccountAttributesValues);
        return getUserAccountXmlProvider().getAttributesXml(values);
    }

    /**
     * adds common values to user-attribute.
     *
     * @param values map with values
     * @throws WebserverSystemException e
     */
    private static void addAttributesCommonValues(final Map<String, Object> values) {
        values.put("attributesNamespacePrefix", Constants.USER_ATTRIBUTES_NS_PREFIX);
        values.put("attributesNamespace", Constants.USER_ATTRIBUTES_NS_URI);
        addEscidocBaseUrl(values);
    }

}
