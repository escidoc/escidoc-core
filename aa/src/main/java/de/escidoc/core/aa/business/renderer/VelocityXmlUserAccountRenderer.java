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
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

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
import de.escidoc.core.common.util.xml.factory.XmlTemplateProvider;

/**
 * User account renderer implementation using the velocity template engine.
 * 
 * @author TTE
 * @spring.bean 
 *              id="eSciDoc.core.aa.business.renderer.VelocityXmlUserAccountRenderer"
 * @aa
 */
public final class VelocityXmlUserAccountRenderer extends AbstractRenderer
    implements UserAccountRendererInterface {

    /**
     * Private constructor to prevent initialization.
     */
    private VelocityXmlUserAccountRenderer() {
    }

    // CHECKSTYLE:JAVADOC-OFF

    /**
     * See Interface for functional description.
     * 
     * @param userAccount
     * @return
     * @throws SystemException
     * @see de.escidoc.core.aa.business.renderer.interfaces.UserAccountRendererInterface#
     *      render(Map)
     * @aa
     */
    @Override
    public String render(final UserAccount userAccount) throws SystemException {

        // long start = System.nanoTime();

        // start = System.nanoTime();
        Map<String, Object> values = new HashMap<String, Object>();

        values.put("isRootUserAccount", "true");
        addCommonValues(values);

        addUserAccountValues(userAccount, values);

        final String ret =
            getUserAccountXmlProvider().getUserAccountXml(values);

        // if (LOG.isDebugEnabled()) {
        // long runtime = System.nanoTime() - start;
        // LOG.debug(StringUtility.concatenateToString("Built XML in ", Long
        // .valueOf(runtime), "ns."));
        // }

        return ret;
    }

    /**
     * Adds the values of the {@link UserAccount} to the provided {@link Map}.
     * 
     * @param userAccount
     *            The {@link UserAccount}.
     * @param values
     *            The {@link Map} to add the values to.
     * @throws SystemException
     *             Thrown in case of an internal error.
     */
    private void addUserAccountValues(
        final UserAccount userAccount, final Map<String, Object> values)
        throws SystemException {
        DateTime lmdDateTime =
            new DateTime(userAccount.getLastModificationDate());
        lmdDateTime = lmdDateTime.withZone(DateTimeZone.UTC);
        String lmd = lmdDateTime.toString(Constants.TIMESTAMP_FORMAT);
        values.put("userAccountLastModificationDate", lmd);
        values.put("userAccountHref", userAccount.getHref());
        DateTime creationDateTime = new DateTime(userAccount.getCreationDate());
        creationDateTime = creationDateTime.withZone(DateTimeZone.UTC);
        String creationDate =
            creationDateTime.toString(Constants.TIMESTAMP_FORMAT);
        values.put("userAccountCreationDate", creationDate);
        values.put("userAccountName", userAccount.getName());
        values.put("userAccountLoginName", userAccount.getLoginname());
        values.put("userAccountId", userAccount.getId());
        values.put("userAccountActive", userAccount.getActive());

        UserAccount createdBy = userAccount.getUserAccountByCreatorId();
        values.put("userAccountCreatedByTitle", createdBy.getName());
        values.put("userAccountCreatedByHref", createdBy.getHref());
        values.put("userAccountCreatedById", createdBy.getId());

        UserAccount modifiedBy = userAccount.getUserAccountByModifiedById();
        values.put("userAccountModifiedByTitle", modifiedBy.getName());
        values.put("userAccountModifiedByHref", modifiedBy.getHref());
        values.put("userAccountModifiedById", modifiedBy.getId());

        addResourcesValues(userAccount, values);
    }

    /**
     * See Interface for functional description.
     * 
     * @param userAccount
     * @param currentGrants
     * @return
     * @throws WebserverSystemException
     * @see de.escidoc.core.aa.business.renderer.interfaces.UserAccountRendererInterface#renderCurrentGrants
     *      (de.escidoc.core.aa.business.UserAccount, java.util.List)
     * @aa
     */
    @Override
    public String renderCurrentGrants(
        final UserAccount userAccount, final List<RoleGrant> currentGrants)
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
        values.put("currentGrantsTitle",
            "Grants of " + userAccount.getLoginname());
        values.put("currentGrantsHref",
            XmlUtility.getCurrentGrantsHref(userAccount.getId()));
        if (currentGrants != null && !currentGrants.isEmpty()) {
            values.put("currentGrants", currentGrants);
        }
        DateTime lmdDateTime =
            new DateTime(userAccount.getLastModificationDate());
        lmdDateTime = lmdDateTime.withZone(DateTimeZone.UTC);
        String lmd = lmdDateTime.toString(Constants.TIMESTAMP_FORMAT);
        values.put(XmlTemplateProvider.VAR_LAST_MODIFICATION_DATE, lmd);

        addEscidocBaseUrl(values);

        final String ret =
            getUserAccountXmlProvider().getCurrentGrantsXml(values);

        return ret;
    }

    /**
     * See Interface for functional description.
     * 
     * @param grants
     * @param numberOfHits
     * @param offset
     * @param limit
     * @param recordPacking
     *            A string to determine how the record should be escaped in the
     *            response. Defined values are 'string' and 'xml'. The default
     *            is 'xml'.
     * 
     * @return
     * @throws WebserverSystemException
     * @see de.escidoc.core.aa.business.renderer.interfaces.UserAccountRendererInterface#renderCurrentGrants
     *      (de.escidoc.core.aa.business.UserAccount, java.util.List)
     */
    @Override
    public String renderGrants(
        final List<RoleGrant> grants, final String numberOfHits,
        final String offset, final String limit,
        final RecordPacking recordPacking) throws WebserverSystemException {

        Map<String, Object> values = new HashMap<String, Object>();
        values.put("isRootGrants", "true");
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
        values.put("searchResultNamespace", Constants.SEARCH_RESULT_NS_URI);
        values.put("numberOfHits", numberOfHits);
        values.put("offset", offset);
        values.put("limit", limit);
        if (grants != null && !grants.isEmpty()) {
            values.put("grants", grants);
        }

        addEscidocBaseUrl(values);
        return getUserAccountXmlProvider().getGrantsSrwXml(values);
    }

    /**
     * See Interface for functional description.
     * 
     * @param grant
     * @return
     * @throws WebserverSystemException
     * @see de.escidoc.core.aa.business.renderer.interfaces.UserAccountRendererInterface#renderGrant
     *      (de.escidoc.core.aa.business.persistence.RoleGrant)
     * @aa
     */
    @Override
    public String renderGrant(final RoleGrant grant)
        throws WebserverSystemException {

        Map<String, Object> values = new HashMap<String, Object>();

        values.put("isRootGrant", "true");
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
            DateTime revokationDateTime = new DateTime(revocationDate);
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

        final String ret = getUserAccountXmlProvider().getGrantXml(values);

        return ret;
    }

    /**
     * See Interface for functional description.
     * 
     * @param userAccount
     * @return
     * @throws WebserverSystemException
     * @see de.escidoc.core.aa.business.renderer.interfaces.UserAccountRendererInterface
     *      #renderResources(de.escidoc.core.aa.business.UserAccount)
     * @aa
     */
    @Override
    public String renderResources(final UserAccount userAccount)
        throws WebserverSystemException {

        Map<String, Object> values = new HashMap<String, Object>();
        values.put("isRootResources", "true");
        addResourcesValues(userAccount, values);
        addCommonValues(values);
        DateTime lmdDateTime =
            new DateTime(userAccount.getLastModificationDate());
        lmdDateTime = lmdDateTime.withZone(DateTimeZone.UTC);
        String lmd = lmdDateTime.toString(Constants.TIMESTAMP_FORMAT);
        values.put("userAccountLastModificationDate", lmd);
        return getUserAccountXmlProvider().getResourcesXml(values);
    }

    /**
     * See Interface for functional description.
     * 
     * @param userAccounts
     * @param recordPacking
     *            A string to determine how the record should be escaped in the
     *            response. Defined values are 'string' and 'xml'. The default
     *            is 'xml'.
     * 
     * @return
     * @throws WebserverSystemException
     * @see de.escidoc.core.aa.business.renderer.interfaces.UserAccountRendererInterface
     *      #renderUserAccounts(de.escidoc.core.aa.business.UserAccount)
     */
    @Override
    public String renderUserAccounts(
        final List<UserAccount> userAccounts, final RecordPacking recordPacking)
        throws SystemException {

        Map<String, Object> values = new HashMap<String, Object>();
        values.put("isRootUserAccount", XmlTemplateProvider.TRUE);
        addCommonValues(values);
        addUserAccountListValues(values);

        final Collection<Map<String, Object>> userAccountsValues =
            new ArrayList<Map<String, Object>>(userAccounts.size());
        for (UserAccount userAccount1 : userAccounts) {
            UserAccount userAccount = userAccount1;
            Map<String, Object> userAccountValues =
                new HashMap<String, Object>();
            addUserAccountValues(userAccount, userAccountValues);
            userAccountsValues.add(userAccountValues);
        }
        values.put("userAccounts", userAccountsValues);
        return getUserAccountXmlProvider().getUserAccountsXml(values,
                recordPacking);
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

        addUserAccountNamespaceValues(values);
        addPropertiesNamespaceValues(values);
        addStructuralRelationNamespaceValues(values);
        addEscidocBaseUrl(values);
    }

    /**
     * Adds the user account name space values.
     * 
     * @param values
     *            The {@link Map} to that the values shall be added.
     * @aa
     */
    private static void addUserAccountNamespaceValues(
        final Map<String, Object> values) {
        values.put("userAccountNamespacePrefix",
            Constants.USER_ACCOUNT_NS_PREFIX);
        values.put("userAccountNamespace", Constants.USER_ACCOUNT_NS_URI);
    }

    /**
     * Adds the user account list values to the provided map.
     * 
     * @param values
     *            The map to add values to.
     * @aa
     */
    private void addUserAccountListValues(final Map<String, Object> values) {

        addUserAccountsNamespaceValues(values);
        values.put("userAccountListTitle", "User Account List");
    }

    /**
     * Adds the values related to the user accounts name space to the provided
     * {@link Map}.
     * 
     * @param values
     *            The MAP to add the values to.
     * @aa
     */
    private static void addUserAccountsNamespaceValues(
        final Map<String, Object> values) {

        values.put("userAccountListNamespacePrefix",
            Constants.USER_ACCOUNT_LIST_NS_PREFIX);
        values.put("userAccountListNamespace",
            Constants.USER_ACCOUNT_LIST_NS_URI);
        values.put("searchResultNamespace", Constants.SEARCH_RESULT_NS_URI);
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
    private static void addEscidocBaseUrl(final Map<String, Object> values)
        throws WebserverSystemException {

        values.put(XmlTemplateProvider.VAR_ESCIDOC_BASE_URL,
            XmlUtility.getEscidocBaseUrl());
    }

    /**
     * Adds the resources specific values to the provided map.
     * 
     * @param userAccount
     *            The user account for that data shall be created.
     * @param values
     *            The map to add values to.
     * @aa
     */
    private static void addResourcesValues(
        final UserAccount userAccount, final Map<String, Object> values) {

        values.put("resourcesHref",
            XmlUtility.getUserAccountResourcesHref(userAccount.getId()));
        values.put("currentGrantsHref",
            XmlUtility.getCurrentGrantsHref(userAccount.getId()));
        values.put("preferencesHref",
            XmlUtility.getPreferencesHref(userAccount.getId()));
        values.put("attributesHref",
            XmlUtility.getAttributesHref(userAccount.getId()));
    }

    /**
     * Gets the <code>UserAccountXmlProvider</code> object.
     * 
     * @return Returns the <code>UserAccountXmlProvider</code> object.
     * @throws WebserverSystemException
     *             Thrown in case of an internal error.
     * @aa
     */
    private static UserAccountXmlProvider getUserAccountXmlProvider()
        throws WebserverSystemException {

        return UserAccountXmlProvider.getInstance();
    }

    @Override
    public String renderPreference(
        final UserAccount userAccount, final UserPreference preference)
        throws SystemException {
        return renderPreference(userAccount, preference, true);
    }

    private String renderPreference(
        final UserAccount userAccount, final UserPreference preference,
        final boolean isRootPreference) throws WebserverSystemException {

        Map<String, Object> values = new HashMap<String, Object>();

        addPreferencesCommonValues(values);
        if (isRootPreference) {
            values.put("isRootPreference", XmlTemplateProvider.TRUE);
            DateTime lmdDateTime =
                new DateTime(userAccount.getLastModificationDate());
            lmdDateTime = lmdDateTime.withZone(DateTimeZone.UTC);
            String lmd = lmdDateTime.toString(Constants.TIMESTAMP_FORMAT);
            values.put("userAccountLastModificationDate", lmd);
            values.put("userAccountId", userAccount.getId());
            addEscidocBaseUrl(values);
        }

        values.put("userAccountPreferenceName", preference.getName());
        values.put("userAccountPreferenceValue", preference.getValue());

        final String ret = getUserAccountXmlProvider().getPreferenceXml(values);

        return ret;
    }

    @Override
    public String renderPreferences(
        final UserAccount userAccount, final Set<UserPreference> preferences)
        throws SystemException {

        Map<String, Object> values = new HashMap<String, Object>();

        values.put("isRootPreference", false);
        addPreferencesCommonValues(values);
        DateTime lmdDateTime =
            new DateTime(userAccount.getLastModificationDate());
        lmdDateTime = lmdDateTime.withZone(DateTimeZone.UTC);
        String lmd = lmdDateTime.toString(Constants.TIMESTAMP_FORMAT);
        values.put("userAccountLastModificationDate", lmd);
        values.put("userAccountId", userAccount.getId());
        addEscidocBaseUrl(values);

        Collection<Map<String, String>> userAccountPreferencesValues =
            new ArrayList<Map<String, String>>();
        for (UserPreference preference : preferences) {
            UserPreference p = preference;
            Map<String, String> m = new HashMap<String, String>();
            m.put("userAccountPreferenceName", p.getName());
            m.put("userAccountPreferenceValue", p.getValue());
            userAccountPreferencesValues.add(m);
        }
        values.put("userAccountPreferences", userAccountPreferencesValues);

        final String ret =
            getUserAccountXmlProvider().getPreferencesXml(values);

        return ret;
    }

    private static void addPreferencesCommonValues(
        final Map<String, Object> values) {
        values.put("preferencesNamespacePrefix",
            Constants.USER_PREFERENCES_NS_PREFIX);
        values.put("preferencesNamespace", Constants.USER_PREFERENCES_NS_URI);
    }

    /**
     * See Interface for functional description.
     * 
     * @param attribute
     *            attribute
     * @return String rendered attribute
     * @throws SystemException
     *             e
     * @see de.escidoc.core.aa.business.renderer.interfaces.UserAccountRendererInterface#renderAttribute
     *      (de.escidoc.core.aa.business.persistence.UserAttribute)
     * @aa
     */
    @Override
    public String renderAttribute(final UserAttribute attribute)
        throws SystemException {
        return renderAttribute(attribute, XmlTemplateProvider.TRUE);
    }

    /**
     * renders one user-attribute.
     * 
     * @param attribute
     *            attribute
     * @param isRootAttribute
     *            if root attribute
     * @return String rendered attribute
     * @throws WebserverSystemException
     *             e
     * @aa
     */
    private String renderAttribute(
        final UserAttribute attribute, final String isRootAttribute)
        throws WebserverSystemException {

        Map<String, Object> values = new HashMap<String, Object>();

        values.put("isRootAttribute", isRootAttribute);
        addAttributesCommonValues(values);
        if (XmlTemplateProvider.TRUE.equals(isRootAttribute)) {
            DateTime lmdDateTime =
                new DateTime(attribute
                    .getUserAccountByUserId().getLastModificationDate());
            lmdDateTime = lmdDateTime.withZone(DateTimeZone.UTC);
            String lmd = lmdDateTime.toString(Constants.TIMESTAMP_FORMAT);
            values.put(XmlTemplateProvider.VAR_LAST_MODIFICATION_DATE, lmd);
            values.put("userAccountId", attribute
                .getUserAccountByUserId().getId());
        }
        values.put("userAccountAttributeId", attribute.getId());
        values.put("userAccountAttributeName", attribute.getName());
        values.put("userAccountAttributeValue", attribute.getValue());
        values.put("userAccountAttributeIsInternal", attribute.getInternal());

        final String ret = getUserAccountXmlProvider().getAttributeXml(values);

        return ret;
    }

    /**
     * See Interface for functional description.
     * 
     * @param userAccount
     *            user-account-do
     * @param attributes
     *            set containing the attributes
     * @return String rendered attributes
     * @throws SystemException
     *             e
     * @see de.escidoc.core.aa.business.renderer.interfaces.UserAccountRendererInterface#renderAttributes
     *      (de.escidoc.core.aa.business.persistence.UserAccount, Set)
     * @aa
     */
    @Override
    public String renderAttributes(
        final UserAccount userAccount, final Set<UserAttribute> attributes)
        throws SystemException {

        Map<String, Object> values = new HashMap<String, Object>();

        addAttributesCommonValues(values);
        DateTime lmdDateTime =
            new DateTime(userAccount.getLastModificationDate());
        lmdDateTime = lmdDateTime.withZone(DateTimeZone.UTC);
        String lmd = lmdDateTime.toString(Constants.TIMESTAMP_FORMAT);
        values.put(XmlTemplateProvider.VAR_LAST_MODIFICATION_DATE, lmd);
        values.put("userAccountId", userAccount.getId());
        addEscidocBaseUrl(values);

        Collection<Map<String, Object>> userAccountAttributesValues =
            new ArrayList<Map<String, Object>>();
        for (UserAttribute attribute : attributes) {
            UserAttribute p = attribute;
            Map<String, Object> m = new HashMap<String, Object>();
            m.put("userAccountId", userAccount.getId());
            m.put("userAccountAttributeId", p.getId());
            m.put("userAccountAttributeName", p.getName());
            m.put("userAccountAttributeValue", p.getValue());
            m.put("userAccountAttributeIsInternal", p.getInternal());
            userAccountAttributesValues.add(m);
        }
        values.put("userAccountAttributes", userAccountAttributesValues);

        final String ret = getUserAccountXmlProvider().getAttributesXml(values);

        return ret;
    }

    /**
     * adds common values to user-attribute.
     * 
     * @param values
     *            map with values
     * @throws WebserverSystemException
     *             e
     * @aa
     */
    private void addAttributesCommonValues(final Map<String, Object> values)
        throws WebserverSystemException {
        values.put("attributesNamespacePrefix",
            Constants.USER_ATTRIBUTES_NS_PREFIX);
        values.put("attributesNamespace", Constants.USER_ATTRIBUTES_NS_URI);
        addEscidocBaseUrl(values);
    }

}
