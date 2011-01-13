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
package de.escidoc.core.aa.business.xacml.finder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sun.xacml.EvaluationCtx;
import com.sun.xacml.attr.AttributeDesignator;
import com.sun.xacml.attr.AttributeValue;
import com.sun.xacml.attr.BagAttribute;
import com.sun.xacml.attr.StringAttribute;
import com.sun.xacml.cond.EvaluationResult;

import de.escidoc.core.aa.business.authorisation.Constants;
import de.escidoc.core.aa.business.authorisation.CustomEvaluationResultBuilder;
import de.escidoc.core.aa.business.authorisation.FinderModuleHelper;
import de.escidoc.core.aa.business.cache.PoliciesCacheProxy;
import de.escidoc.core.aa.business.cache.RequestAttributesCache;
import de.escidoc.core.aa.business.persistence.RoleGrant;
import de.escidoc.core.aa.business.persistence.UserAccount;
import de.escidoc.core.aa.business.persistence.UserAccountDaoInterface;
import de.escidoc.core.aa.business.persistence.UserAttribute;
import de.escidoc.core.aa.business.persistence.UserLoginData;
import de.escidoc.core.common.business.aa.authorisation.AttributeIds;
import de.escidoc.core.common.business.fedora.TripleStoreUtility;
import de.escidoc.core.common.exceptions.EscidocException;
import de.escidoc.core.common.exceptions.application.notfound.UserAccountNotFoundException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.util.configuration.EscidocConfiguration;
import de.escidoc.core.common.util.list.ListSorting;
import de.escidoc.core.common.util.logger.AppLogger;
import de.escidoc.core.common.util.service.UserContext;
import de.escidoc.core.common.util.string.StringUtility;
import de.escidoc.core.common.util.xml.XmlUtility;

/**
 * Implementation of an XACML attribute finder module that is responsible for
 * the attributes related to an user account.<br>
 * This finder module supports both XACML subject attributes and XACML resource
 * attributes.<br>
 * The attribute values are fetched from the xml representation of the user
 * account.
 * 
 * Supported Attributes:<br>
 * -info:escidoc/names:aa:1.0:resource:user-account:handle<br>
 * the handle of the user-account, single value attribute
 * -info:escidoc/names:aa:1.0:resource:user-account:login-name<br>
 * the login-name of the user-account, single value attribute
 * -info:escidoc/names:aa:1.0:resource:user-account:name<br>
 * the name of the user-account, single value attribute
 * -info:escidoc/names:aa:1.0:resource:user-account:created-by<br>
 * the user who created the user-account, single value attribute
 * -info:escidoc/names:aa:1.0:resource:user-account:modified-by<br>
 * the user who modified the user-account, single value attribute
 * -info:escidoc/names:aa:1.0:resource:user-account:organizational-unit<br>
 * the organizational-unit of the user-account, multi value attribute
 * -info:escidoc/names:aa:1.0:resource:user-account:organizational-unit-with-children<br>
 * the organizational-unit tree of the user-account, multi value attribute
 * -info:escidoc/names:aa:1.0:resource:user-account:group-membership<br>
 * the ids of the groups the user is member(hierarchical), multi value attribute
 * -info:escidoc/names:aa:1.0:resource:user-account:id<br>
 * the id of the user-account, single value attribute
 * -info:escidoc/names:aa:1.0:subject:handle<br>
 * the handle of the current user, single value attribute
 * -info:escidoc/names:aa:1.0:subject:login-name<br>
 * the login-name of the current user, single value attribute
 * -info:escidoc/names:aa:1.0:subject:name<br>
 * the name of the current user, single value attribute
 * -info:escidoc/names:aa:1.0:subject:created-by<br>
 * the user who created the current user, single value attribute
 * -info:escidoc/names:aa:1.0:subject:modified-by<br>
 * the user who modified the current user, single value attribute
 * -info:escidoc/names:aa:1.0:subject:organizational-unit<br>
 * the organizational-unit of the current user, multi value attribute
 * -info:escidoc/names:aa:1.0:subject:organizational-unit-with-children<br>
 * the organizational-unit tree of the current user, multi value attribute
 * -info:escidoc/names:aa:1.0:subject:group-membership<br>
 * the ids of the groups the current user is member(hierarchical), multi value attribute
 * -info:escidoc/names:aa:1.0:subject:role-grant:&lt;role-id&gt;:assigned-on<br>
 *  the id of the object the grant is assigned on (scope of the grant), multi value attribute
 * 
 * @spring.bean id="eSciDoc.core.aa.UserAccountAttributeFinderModule"
 * 
 * @author TTE
 * 
 * @aa
 */
public class UserAccountAttributeFinderModule
    extends AbstractAttributeFinderModule {

    /**
     * Pattern used to parse the attribute id and fetch the resolvable part, the
     * last part of the resolvable part and the tail.
     */
    private static final String USER_ACCOUNT_ATTRS =
        "(handle|login-name|name|" + "created-by|modified-by|"
            + "organizational-unit|organizational-unit-with-children|"
            + "group-membership|role-grant)";

    private static final String ROLE_GRANT_ATTRS = "(assigned-on)";

    private static final Pattern PATTERN_PARSE_ROLE_GRANT_ROLE =
        Pattern.compile("((" + AttributeIds.SUBJECT_ATTR_PREFIX + "|"
            + AttributeIds.USER_ACCOUNT_ATTR_PREFIX + ")" + USER_ACCOUNT_ATTRS
            + "):(.*?):" + ROLE_GRANT_ATTRS);

    private static final Pattern PATTERN_PARSE_USER_ACCOUNT_ATTRIBUTE_ID =
        Pattern.compile("((" + AttributeIds.SUBJECT_ATTR_PREFIX + "|"
            + AttributeIds.USER_ACCOUNT_ATTR_PREFIX + ")" + USER_ACCOUNT_ATTRS
            + ")(:.*){0,1}");

    private static final Pattern PATTERN_SUBJECT_ATTRIBUTE_PREFIX =
        Pattern.compile(AttributeIds.SUBJECT_ATTR_PREFIX);

    private static final Pattern PATTERN_USER_ACCOUNT_ATTRIBUTE_PREFIX =
        Pattern.compile(AttributeIds.USER_ACCOUNT_ATTR_PREFIX);

    private static final Pattern PATTERN_IS_SUBJECT_ATTRIBUTE_ID =
        Pattern.compile(AttributeIds.SUBJECT_ATTR_PREFIX + ".*");

    /** The logger. */
    private static AppLogger log =
        new AppLogger(UserAccountAttributeFinderModule.class.getName());

    /**
     * Attributes can have USER_ACCOUNT_ATTR_PREFIX
     * (info:escidoc/names:aa:1.0:resources:user-account:) or
     * SUBJECT_ATTR_PREFIX (info:escidoc/names:aa:1.0:subject:) if
     * USER_ACCOUNT_ATTR_PREFIX is used, userId is resolved from
     * resourceId-Attribute. if SUBJECT_ATTR_PREFIX is used, userId is resolved
     * from xacml-subject-attribute
     * 
     * Internally this class only uses USER_ACCOUNT_ATTR_PREFIX -->
     * SUBJECT_ATTR_PREFIX is changed to USER_ACCOUNT_ATTR_PREFIX before
     * resolving.
     */

    /**
     * This attribute matches the current eSciDoc user handles of the user
     * (identified by the resource-id).
     * 
     * @aa
     */
    public static final String ATTR_USER_HANDLE =
        AttributeIds.USER_ACCOUNT_ATTR_PREFIX + "handle";

    /**
     * This attribute matches the internal id of the user (identified by the
     * resource-id).
     * 
     * @aa
     */
    public static final String ATTR_USER_ID =
        AttributeIds.USER_ACCOUNT_ATTR_PREFIX + "id";

    /**
     * This attribute matches the login name of the user (identified by the
     * resource-id).
     * 
     * @aa
     */
    public static final String ATTR_USER_LOGIN_NAME =
        AttributeIds.USER_ACCOUNT_ATTR_PREFIX + "login-name";

    /**
     * This attribute matches the name of the user (identified by the
     * resource-id).
     * 
     * @aa
     */
    public static final String ATTR_USER_NAME =
        AttributeIds.USER_ACCOUNT_ATTR_PREFIX + "name";

    /**
     * This attribute matches the creator of the user (identified by the
     * resource-id).
     * 
     * @aa
     */
    public static final String ATTR_CREATED_BY =
        AttributeIds.USER_ACCOUNT_ATTR_PREFIX + "created-by";

    /**
     * This attribute matches the last modifier of the user (identified by the
     * resource-id).
     * 
     * @aa
     */
    public static final String ATTR_MODIFIED_BY =
        AttributeIds.USER_ACCOUNT_ATTR_PREFIX + "modified-by";

    /**
     * This attribute matches the group-membership of the user (identified by
     * the resource-id).
     * 
     * @aa
     */
    public static final String ATTR_USER_GROUP_MEMBERSHIP =
        AttributeIds.USER_ACCOUNT_ATTR_PREFIX + "group-membership";

    /**
     * This attribute matches the organizational unit of the user (identified by
     * the resource-id).
     * 
     * @aa
     */
    public static final String ATTR_USER_ORGANIZATIONAL_UNIT =
        AttributeIds.USER_ACCOUNT_ATTR_PREFIX
            + XmlUtility.NAME_ORGANIZATIONAL_UNIT;

    /**
     * This attribute matches the organizational unit of the user (identified by
     * the resource-id) (also children are resolved).
     * 
     * @aa
     */
    public static final String ATTR_USER_ORGANIZATIONAL_UNIT_WITH_CHILDREN =
        AttributeIds.USER_ACCOUNT_ATTR_PREFIX
            + XmlUtility.NAME_ORGANIZATIONAL_UNIT + "-with-children";

    /**
     * This attribute matches the scope of a role of the user.
     * 
     * @aa
     */
    public static final Pattern ATTR_USER_ROLE_SCOPE =
        Pattern.compile(AttributeIds.USER_ACCOUNT_ATTR_PREFIX
            + "role-grant:(.*?):assigned-on");

    private PoliciesCacheProxy policiesCacheProxy = null;

    private UserAccountDaoInterface userAccountDao;

    /**
     * Since this class will retrieve subject attributes and resource
     * attributes, it will return a set only containing two values, indicating
     * that it supports Designators of type SUBJECT_TARGET and RESOURCE_TARGET.
     * 
     * @return Set only containing the value SUBJECT_TARGET and RESOURCE_TARGET.
     * @see com.sun.xacml.finder.AttributeFinderModule
     *      #getSupportedDesignatorTypes()
     * @aa
     */
    @Override
    public Set getSupportedDesignatorTypes() {

        Set<Integer> set = new HashSet<Integer>();
        set.add(Integer.valueOf(AttributeDesignator.SUBJECT_TARGET));
        set.add(Integer.valueOf(AttributeDesignator.RESOURCE_TARGET));
        return set;
    }

    // CHECKSTYLE:JAVADOC-OFF

    /**
     * See Interface for functional description.
     * 
     * @param attributeIdValue
     * @param ctx
     * @param resourceId
     * @param resourceObjid
     * @param resourceVersionNumber
     * @param designatorType
     * @return
     * @throws EscidocException
     * @see de.escidoc.core.aa.business.xacml.finder.AbstractAttributeFinderModule#assertAttribute(java.lang.String,
     *      com.sun.xacml.EvaluationCtx, java.lang.String, java.lang.String,
     *      java.lang.String, int)
     * @aa
     */
    @Override
    protected boolean assertAttribute(
        final String attributeIdValue, final EvaluationCtx ctx,
        final String resourceId, final String resourceObjid,
        final String resourceVersionNumber, final int designatorType)
        throws EscidocException {

        // the super class method only supports RESOURCE attributes. We have to
        // override it, to support SUBJECT Attributes, too.

        // make sure this is an Subject or Resource attribute
        if (designatorType != AttributeDesignator.SUBJECT_TARGET
            && designatorType != AttributeDesignator.RESOURCE_TARGET) {

            return false;
        }

        // make sure attribute is in escidoc-internal format for
        // subject attribute or resource user-account attributes
        if (!PATTERN_PARSE_USER_ACCOUNT_ATTRIBUTE_ID
            .matcher(attributeIdValue).find()) {
            return false;
        }

        return true;
    }

    /**
     * See Interface for functional description.
     * 
     * @param attributeIdValue
     * @param ctx
     * @param resourceId
     * @param resourceObjid
     * @param resourceVersionNumber
     * @return
     * @throws EscidocException
     * @see de.escidoc.core.aa.business.xacml.finder.AbstractAttributeFinderModule#resolveLocalPart(java.lang.String,
     *      com.sun.xacml.EvaluationCtx, java.lang.String, java.lang.String,
     *      java.lang.String)
     * @aa
     */
    @Override
    protected Object[] resolveLocalPart(
        final String attributeIdValue, final EvaluationCtx ctx,
        final String resourceId, final String resourceObjid,
        final String resourceVersionNumber) throws EscidocException {

        EvaluationResult result = null;
        String resolvedAttributeIdValue = null;

        // determine the id of the user account and to simplify the further
        // work, replace INTERNAL_SUBJECT_ATTRIBUTE_PREFIX by
        // INTERNAL_RESOURCE_USER_ACCOUNT_ATTRIBUTE_PREFIX in case of an
        // subject attribute
        String internalAttributeIdValue;
        String userAccountId;
        boolean isSubjectAttribute = false;
        if (PATTERN_IS_SUBJECT_ATTRIBUTE_ID.matcher(attributeIdValue).find()) {
            isSubjectAttribute = true;
            userAccountId =
                FinderModuleHelper.retrieveSingleSubjectAttribute(ctx,
                    Constants.URI_SUBJECT_ID, true);
            if (userAccountId == null) {
                StringBuffer errorMsg =
                    new StringBuffer(
                        "The subject (user) of the request cannot be ");
                errorMsg.append("identified, the ");
                errorMsg.append(Constants.URI_SUBJECT_ID);
                errorMsg.append(" may not have been set.");
                throw new WebserverSystemException(errorMsg.toString());
            }
            Matcher matcher =
                PATTERN_SUBJECT_ATTRIBUTE_PREFIX.matcher(attributeIdValue);
            internalAttributeIdValue =
                matcher.replaceFirst(AttributeIds.USER_ACCOUNT_ATTR_PREFIX);
        }
        else {
            userAccountId = FinderModuleHelper.getResourceId(ctx);
            if (FinderModuleHelper.isNewResourceId(userAccountId)) {
                return null;
            }
            internalAttributeIdValue = attributeIdValue;
        }
        // ask cache for previously cached results
        result =
            getFromCache(resourceId, resourceObjid, resourceVersionNumber,
                internalAttributeIdValue, ctx);

        if (result == null) {
            // check if attributes of an anonymous user shall be retrieved
            if (UserContext.isIdOfAnonymousUser(userAccountId)) {
                // The anonymous user does not have an account, for each
                // attribute the value of the anonymous identifier is returned.
                result =
                    CustomEvaluationResultBuilder
                        .createSingleStringValueResult(UserContext.ANONYMOUS_IDENTIFIER);
                // the resolved id is set to the complete id, as no further
                // resolving is possible.
                resolvedAttributeIdValue = internalAttributeIdValue;
            }
            else {
                if (ATTR_USER_HANDLE.equals(internalAttributeIdValue)) {
                    Set userHandles;
                    userHandles =
                        retrieveUserHandle(ctx, userAccountId,
                            internalAttributeIdValue);
                    if (userHandles == null) {
                        return null;
                    }
                    result =
                        new EvaluationResult(new BagAttribute(
                            Constants.URI_XMLSCHEMA_STRING, userHandles));
                    resolvedAttributeIdValue = ATTR_USER_HANDLE;
                }
                else if (ATTR_USER_ROLE_SCOPE
                    .matcher(internalAttributeIdValue).matches()) {
                    result =
                        fetchRoleScopes(userAccountId, internalAttributeIdValue);
                    resolvedAttributeIdValue = internalAttributeIdValue;
                }
                else if (ATTR_USER_GROUP_MEMBERSHIP
                    .equals(internalAttributeIdValue)) {
                    result = fetchUserGroups(userAccountId);
                    resolvedAttributeIdValue = internalAttributeIdValue;
                }
                else {
                    // Fetch the user account and return the appropriate value
                    UserAccount userAccount = null;
                    try {
                        userAccount = retrieveUserAccount(ctx, userAccountId);
                    }
                    catch (UserAccountNotFoundException e) {
                        if (isSubjectAttribute) {
                            throw new UserAccountNotFoundException(
                                StringUtility.concatenateWithBracketsToString(
                                    "Account of subject not found.",
                                    userAccountId, e.getMessage()), e);
                        }
                        else {
                            throw e;
                        }
                    }
                    Pattern p = PATTERN_PARSE_USER_ACCOUNT_ATTRIBUTE_ID;
                    Matcher idMatcher = p.matcher(internalAttributeIdValue);
                    if (idMatcher.find()) {
                        resolvedAttributeIdValue = idMatcher.group(1);
                        String nextResourceId = null;
                        if (userAccount != null) {
                            if (ATTR_USER_ID.equals(resolvedAttributeIdValue)) {
                                nextResourceId = userAccount.getId();
                                result =
                                    CustomEvaluationResultBuilder
                                        .createSingleStringValueResult(nextResourceId);
                            }
                            else if (ATTR_USER_LOGIN_NAME
                                .equals(resolvedAttributeIdValue)) {
                                result =
                                    CustomEvaluationResultBuilder
                                        .createSingleStringValueResult(userAccount
                                            .getLoginname());
                            }
                            else if (ATTR_USER_NAME
                                .equals(resolvedAttributeIdValue)) {
                                result =
                                    CustomEvaluationResultBuilder
                                        .createSingleStringValueResult(userAccount
                                            .getName());
                            }
                            else if (ATTR_CREATED_BY
                                .equals(resolvedAttributeIdValue)) {
                                result =
                                    CustomEvaluationResultBuilder
                                        .createSingleStringValueResult(userAccount
                                            .getUserAccountByCreatorId()
                                            .getId());
                            }
                            else if (ATTR_MODIFIED_BY
                                .equals(resolvedAttributeIdValue)) {
                                result =
                                    CustomEvaluationResultBuilder
                                        .createSingleStringValueResult(userAccount
                                            .getUserAccountByModifiedById()
                                            .getId());
                            }
                            else if (ATTR_USER_ORGANIZATIONAL_UNIT
                                .equals(resolvedAttributeIdValue)) {
                                result =
                                    fetchUserAccountOus(userAccount, false);
                            }
                            else if (ATTR_USER_ORGANIZATIONAL_UNIT_WITH_CHILDREN
                                .equals(resolvedAttributeIdValue)) {
                                result = fetchUserAccountOus(userAccount, true);
                            }
                        }
                    }
                }
            }
        }
        if (result == null) {
            return null;
        }
        if (isSubjectAttribute) {
            // revert previously Subject -> Resource change
            Matcher matcher =
                PATTERN_USER_ACCOUNT_ATTRIBUTE_PREFIX
                    .matcher(resolvedAttributeIdValue);
            resolvedAttributeIdValue =
                matcher.replaceFirst(AttributeIds.SUBJECT_ATTR_PREFIX);
        }
        putInCache(resourceId, resourceObjid, resourceVersionNumber,
            attributeIdValue, ctx, result);
        return new Object[] { result, resolvedAttributeIdValue };
    }

    // CHECKSTYLE:JAVADOC-ON

    /**
     * Fetches the value of the attribute <code>ATTR_USER_OU</code> for the
     * provided user account.
     * 
     * @param userAccount
     *            The user account to fetch the value from.
     * @param getChildren
     *            if also children of userAccountous are to be fetched.
     * @return Returns the attribute value in an <code>EvaluationResult</code>.
     * @throws EscidocException
     *             e
     */
    private EvaluationResult fetchUserAccountOus(
        final UserAccount userAccount, final boolean getChildren)
        throws EscidocException {

        final EvaluationResult result;
        String ouAttributeName = null;
        try {
            ouAttributeName =
                EscidocConfiguration.getInstance().get(
                    EscidocConfiguration.ESCIDOC_CORE_AA_OU_ATTRIBUTE_NAME);
        }
        catch (IOException e) {
            throw new SystemException(e);
        }
        if (ouAttributeName == null || ouAttributeName.equals("")) {
            return CustomEvaluationResultBuilder.createEmptyEvaluationResult();
        }
        List<UserAttribute> attributes =
            userAccountDao.retrieveAttributes(userAccount, ouAttributeName);
        if (attributes == null || attributes.size() == 0) {
            result =
                CustomEvaluationResultBuilder.createEmptyEvaluationResult();
        }
        else {
            final List<StringAttribute> results =
                new Vector<StringAttribute>(attributes.size());
            Collection<String> ouIds = new ArrayList<String>();
            for (UserAttribute attribute : attributes) {
                results.add(new StringAttribute(attribute.getValue()));
                if (getChildren) {
                    ouIds.add(attribute.getValue());
                }
            }
            if (getChildren) {
                Vector<String> childOus =
                    TripleStoreUtility.getInstance().getChildrenPath(ouIds,
                        new Vector<String>());
                if (childOus != null) {
                    for (String childOu : childOus) {
                        results.add(new StringAttribute(childOu));
                    }
                }
            }

            result =
                new EvaluationResult(new BagAttribute(
                    Constants.URI_XMLSCHEMA_STRING, results));
        }
        return result;
    }

    /**
     * Fetches the groupIds where user is member for the provided user account.
     * 
     * @param userAccountId
     *            The id of the user account to fetch the value from.
     * @throws EscidocException
     *             e
     * @return Returns the attribute value in an <code>EvaluationResult</code>.
     */
    private EvaluationResult fetchUserGroups(final String userAccountId)
        throws EscidocException {

        final EvaluationResult result;

        Set<String> userGroups = policiesCacheProxy.getUserGroups(userAccountId);

        if (userGroups == null || userGroups.size() == 0) {
            result =
                CustomEvaluationResultBuilder.createEmptyEvaluationResult();
        }
        else {
            final Iterator<String> groupIdsIter = userGroups.iterator();
            final List<StringAttribute> results =
                new Vector<StringAttribute>(userGroups.size());
            while (groupIdsIter.hasNext()) {
                results.add(new StringAttribute(groupIdsIter.next()));
            }
            result =
                new EvaluationResult(new BagAttribute(
                    Constants.URI_XMLSCHEMA_STRING, results));
        }
        return result;
    }

    /**
     * Fetches the scopes of the role identified in the attribute for the
     * provided user account.
     * 
     * @param userAccountId
     *            The id of the user account to fetch the value from.
     * @param attributeId
     *            The name of the attribute.
     * @throws EscidocException
     *             e
     * @return Returns the attribute value in an <code>EvaluationResult</code>.
     */
    private EvaluationResult fetchRoleScopes(
        final String userAccountId, final String attributeId)
        throws EscidocException {

        final EvaluationResult result;
        // get role to fetch
        Matcher roleMatcher =
            PATTERN_PARSE_ROLE_GRANT_ROLE.matcher(attributeId);
        String roleName = null;
        if (roleMatcher.find()) {
            roleName = roleMatcher.group(4);
        }
        if (roleName == null || roleName.equals("")) {
            return CustomEvaluationResultBuilder.createEmptyEvaluationResult();
        }

        Set<String> userGroups = policiesCacheProxy.getUserGroups(userAccountId);
        Map<String, HashSet<String>> criterias =
            new HashMap<String, HashSet<String>>();
        HashSet<String> roles = new HashSet<String>();
        roles.add(roleName);
        HashSet<String> users = new HashSet<String>();
        users.add(userAccountId);
        criterias.put(de.escidoc.core.common.business.Constants.FILTER_PATH_USER_ID,
            users);
        criterias.put(de.escidoc.core.common.business.Constants.FILTER_PATH_ROLE_ID,
            roles);
        if (userGroups != null && !userGroups.isEmpty()) {
            criterias.put(
                de.escidoc.core.common.business.Constants.FILTER_PATH_GROUP_ID,
                (HashSet<String>) userGroups);
        }
        List<RoleGrant> roleGrants =
            userAccountDao.retrieveGrants(criterias, null,
                ListSorting.ASCENDING);
        if (roleGrants != null) {
            final List<StringAttribute> results = new Vector<StringAttribute>();
            for (RoleGrant roleGrant : roleGrants) {
                if (roleGrant.getRevocationDate() == null) {
                    results.add(new StringAttribute(roleGrant.getObjectId()));
                }
            }
            result =
                new EvaluationResult(new BagAttribute(
                    Constants.URI_XMLSCHEMA_STRING, results));
        }
        else {
            result =
                CustomEvaluationResultBuilder.createEmptyEvaluationResult();
        }
        return result;
    }

    /**
     * Retrieves the handles of the user.
     * 
     * @param ctx
     *            The evaluation context, which will be used as key for the
     *            cache.
     * @param userAccountId
     *            The id of the user account to get the handles for.
     * @param attributeIdValue
     *            The value of the attribute id that is currently resolved.
     * @return Returns the eSciDoc user handle of the subject (current user).
     * @throws WebserverSystemException
     *             Thrown in case of an internal error.
     * @throws UserAccountNotFoundException
     *             Thrown if no user account with provided id is found.
     * @aa
     */
    private Set retrieveUserHandle(
        final EvaluationCtx ctx, final String userAccountId,
        final String attributeIdValue) throws WebserverSystemException,
        UserAccountNotFoundException {

        final StringBuffer key =
            StringUtility.concatenateWithColon(XmlUtility.NAME_HANDLE,
                userAccountId);
        Set<AttributeValue> result =
            (Set<AttributeValue>) RequestAttributesCache.get(ctx, key
                .toString());
        if (result == null) {
            List userHandles = null;
            try {
                userHandles =
                    getUserAccountDao().retrieveUserLoginDataByUserId(
                        userAccountId);
                if (userHandles == null || userHandles.isEmpty()) {
                    assertUserAccount(userAccountId, getUserAccountDao()
                        .retrieveUserAccountById(userAccountId));
                }
            }
            catch (UserAccountNotFoundException e) {
                throw e;
            }
            catch (WebserverSystemException e) {
                throw e;
            }
            catch (Exception e) {
                final StringBuffer errorMsg =
                    StringUtility.concatenateWithBrackets(
                        "Retrieving of attribute failed", attributeIdValue);
                log.error(errorMsg.toString(), e);
                throw new WebserverSystemException(errorMsg.toString(), e);
            }
            result = new HashSet<AttributeValue>();
            if (userHandles != null && !userHandles.isEmpty()) {
                Iterator iter = userHandles.iterator();
                while (iter.hasNext()) {
                    UserLoginData userLoginData = (UserLoginData) iter.next();
                    result.add(new StringAttribute(userLoginData.getHandle()));
                }
            }
            RequestAttributesCache.put(ctx, key.toString(), result);
        }
        return result;
    }

    /**
     * Retrieve User Account from the system.
     * 
     * @param ctx
     *            The evaluation context, which will be used as key for the
     *            cache.
     * @param userAccountId
     *            The user account id.
     * @return Returns the <code>UserAccount</code> identified by the provided
     *         id.
     * @throws WebserverSystemException
     *             Thrown in case of an internal error.
     * @throws UserAccountNotFoundException
     *             Thrown if no user account with provided id exists.
     * @aa
     */
    private UserAccount retrieveUserAccount(
        final EvaluationCtx ctx, final String userAccountId)
        throws WebserverSystemException, UserAccountNotFoundException {

        final StringBuffer key =
            StringUtility.concatenateWithColon(XmlUtility.NAME_ID,
                userAccountId);
        UserAccount userAccount =
            (UserAccount) RequestAttributesCache.get(ctx, key.toString());
        if (userAccount == null) {
            try {
                userAccount =
                    getUserAccountDao().retrieveUserAccount(userAccountId);
            }
            catch (Exception e) {
                throw new WebserverSystemException(StringUtility
                    .concatenateWithBracketsToString(
                        "Exception during retrieval of the user account", e
                            .getMessage()), e);
            }
        }

        assertUserAccount(userAccountId, userAccount);

        RequestAttributesCache.put(ctx, key.toString(), userAccount);
        return userAccount;
    }

    /**
     * Asserts that the user account is provided, i.e. it is not
     * <code>null</code>.
     * 
     * @param userId
     *            The user id for which the account should be provided (should
     *            exist).
     * @param userAccount
     *            The user account to assert.
     * @throws UserAccountNotFoundException
     *             Thrown if assertion fails.
     * @aa
     */
    private void assertUserAccount(
        final String userId, final UserAccount userAccount)
        throws UserAccountNotFoundException {

        if (userAccount == null) {
            throw new UserAccountNotFoundException(StringUtility
                .concatenateWithBrackets(
                    "User with provided id does not exist", userId).toString());
        }
    }

    /**
     * Gets the user account dao.
     * 
     * @return Returns the user account dao bean.
     * @throws WebserverSystemException
     *             Thrown in case of an internal system error during bean
     *             initialization.
     */
    private UserAccountDaoInterface getUserAccountDao()
        throws WebserverSystemException {

        return userAccountDao;
    }

    /**
     * Injects the policies cache proxy.
     * 
     * @spring.property ref="resource.PoliciesCacheProxy"
     * @param policiesCacheProxy
     *            the {@link PoliciesCacheProxy} to inject.
     */
    public void setPoliciesCacheProxy(
        final PoliciesCacheProxy policiesCacheProxy) {
        this.policiesCacheProxy = policiesCacheProxy;
    }

    /**
     * Injects the user account data access object if "called" via Spring.
     * 
     * @param userAccountDao
     *            The user account dao.
     * @spring.property ref="persistence.UserAccountDao"
     */
    public void setUserAccountDao(final UserAccountDaoInterface userAccountDao) {
        this.userAccountDao = userAccountDao;
    }
}
