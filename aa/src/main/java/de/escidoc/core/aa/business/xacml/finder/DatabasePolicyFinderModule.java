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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.sun.xacml.AbstractPolicy;
import com.sun.xacml.EvaluationCtx;
import com.sun.xacml.PolicyReference;
import com.sun.xacml.UnknownIdentifierException;
import com.sun.xacml.ctx.Status;
import com.sun.xacml.finder.PolicyFinder;
import com.sun.xacml.finder.PolicyFinderModule;
import com.sun.xacml.finder.PolicyFinderResult;

import de.escidoc.core.aa.business.authorisation.Constants;
import de.escidoc.core.aa.business.authorisation.CustomPolicyBuilder;
import de.escidoc.core.aa.business.authorisation.CustomStatusBuilder;
import de.escidoc.core.aa.business.authorisation.FinderModuleHelper;
import de.escidoc.core.aa.business.SecurityHelper;
import de.escidoc.core.aa.business.xacml.XacmlPolicySet;
import de.escidoc.core.common.exceptions.EscidocException;
import de.escidoc.core.common.exceptions.application.notfound.UserAccountNotFoundException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.util.service.UserContext;
import de.escidoc.core.common.util.string.StringUtility;

/**
 * Custom implementation of a PolicyFinderModule.
 * <p/>
 *
 * @author Roland Werner (Accenture)
 */
@Service("eSciDoc.core.aa.DatabasePolicyFinderModule")
public class DatabasePolicyFinderModule extends PolicyFinderModule {

    /**
     * The logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(DatabasePolicyFinderModule.class);

    private static final Pattern SPLIT_PATTERN = Pattern.compile("/");

    /**
     * The property which is used to specify the schema file to validate against (if any).
     */
    public static final String POLICY_SCHEMA_PROPERTY = "com.sun.xacml.PolicySchema";

    public static final String JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";

    public static final String W3C_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";

    public static final String JAXP_SCHEMA_SOURCE = "http://java.sun.com/xml/jaxp/properties/schemaSource";

    @Autowired
    @Qualifier("security.SecurityHelper")
    private SecurityHelper securityHelper;

    private PolicyFinder policyFinder;

    /**
     * Protected constructor to prevent instantiation outside of the Spring-context.
     */
    protected DatabasePolicyFinderModule() {
    }

    /**
     * Indicates whether this module supports finding policies based on a request (target matching). Since this module
     * does support finding policies based on requests, it returns true.
     *
     * @return true, since finding policies based on requests is supported
     */
    @Override
    public boolean isRequestSupported() {
        return true;
    }

    /**
     * Indicates whether this module supports finding policies based on referencing. Since this module does not support
     * finding policies based on referencing, it returns false.
     *
     * @return false, since finding policies based on referencing is not supported
     */
    @Override
    public boolean isIdReferenceSupported() {
        return true;
    }

    /**
     * Initializes the {@code DatabasePolicyFinderModule} by setting the specified {@code PolicyFinder} to
     * help in instantiating PolicySets.
     *
     * @param finder a PolicyFinder used to help in instantiating PolicySets
     */
    @Override
    public void init(final PolicyFinder finder) {

    }

    /**
     * See Interface for functional description.
     */
    @Override
    public void invalidateCache() {
        securityHelper.clear();
    }

    /**
     * See Interface for functional description.
     */
    @Override
    public PolicyFinderResult findPolicy(final URI idReference, final int type) {

        if (type != PolicyReference.POLICY_REFERENCE && type != PolicyReference.POLICYSET_REFERENCE) {
            throw new IllegalArgumentException(StringUtility.format("Illegal type", type));
        }
        if (type != PolicyReference.POLICYSET_REFERENCE) {
            return new PolicyFinderResult();
        }

        // The policyId is concatenated String
        // containing <roleName>/<user or group>/<userOrGroupId>
        final String[] parts = SPLIT_PATTERN.split(idReference.toString());
        final StringBuilder roleIdentifier = new StringBuilder("");
        if (parts.length > 2) {
            for (int i = 0; i < parts.length - 2; i++) {
                roleIdentifier.append(parts[i]);
            }
        }
        else {
            roleIdentifier.append(idReference);
        }
        final URI roleIdentifierUri;
        try {
            roleIdentifierUri = new URI(roleIdentifier.toString());
        }
        catch (final URISyntaxException e1) {
            return createProcessingError("Error during resolving policy reference. ", e1);
        }

        XacmlPolicySet result;
        try {
            result = securityHelper.getRolePolicySet(roleIdentifierUri);
        }
        catch (final WebserverSystemException e) {
            return createProcessingError("Error during resolving policy reference. ", e);
        }

        try {
            result = CustomPolicyBuilder.regeneratePolicySet(result, idReference.toString());
        }
        catch (final Exception e) {
            return createProcessingError("Error during resolving policy reference. ", e);
        }
        return new PolicyFinderResult(result);
    }

    /**
     * Finds a policy based on a request's context.
     * <p/>
     * <p/>
     * The method executes the following steps in order to fetch suitable policies: <ul> <li>Retrieve
     * {@code subject-id} and {@code action-id} from the provided context object, which is used to narrow down
     * the search to only policies that have this {@code subject-id} and {@code action-id} in their target
     * part. This is done via the method {@code retrieveSingleAttribute}.</li>
     * <p/>
     * <li>The retrieved policies are checked for matching against the current {@code EvaluationCtx}.</li>
     * <p/>
     * <li>If a matching policy is found, it is returned.</li> </ul>
     *
     * @param context the representation of the request data
     * @return the result of trying to find an applicable policy
     * @see FinderModuleHelper#retrieveSingleResourceAttribute
     */
    @Override
    public PolicyFinderResult findPolicy(final EvaluationCtx context) {
        try {
            final List<AbstractPolicy> policies = new ArrayList<AbstractPolicy>();

            // first get the user id and action from the request
            final String userId =
                FinderModuleHelper.retrieveSingleSubjectAttribute(context, Constants.URI_SUBJECT_ID, true);

            // get policySet for policies attached to the user
            final XacmlPolicySet userPolicySet = getUserPolicies(userId);
            policies.add(userPolicySet);

            // get policySet for policies attached via groups the user belongs
            // to
            if (!UserContext.isIdOfAnonymousUser(userId)) {
                final XacmlPolicySet userGroupsPolicySet = getUserGroupPolicies(userId);
                if (userGroupsPolicySet != null) {
                    policies.add(userGroupsPolicySet);
                }
            }

            final XacmlPolicySet result =
                new XacmlPolicySet("UserGroupPolicies-" + userId,
                    XacmlPolicySet.URN_POLICY_COMBINING_ALGORITHM_ORDERED_PERMIT_OVERRIDES, null, policies);

            return new PolicyFinderResult(result);

        }
        catch (final Exception e) {
            return createProcessingError("Exception happened while searching for policies: ", e);
        }
    }

    /**
     * Creates a {@code XacmlPolicySet} object with all policies directly attached to the user.
     *
     * @param userId The userId.
     * @return Returns the created {@code XacmlPolicySet} object for the user.
     * @throws de.escidoc.core.common.exceptions.system.WebserverSystemException
     * @throws java.net.URISyntaxException
     * @throws com.sun.xacml.UnknownIdentifierException
     */
    private XacmlPolicySet getUserPolicies(final String userId) throws UnknownIdentifierException, URISyntaxException,
        WebserverSystemException {
        return securityHelper.getUserPolicies(userId, this.policyFinder);
    }

    /**
     * Creates a {@code XacmlPolicySet} object with all policies attached to the user via the groups he belongs
     * to.
     *
     * @param userId The userId.
     * @return Returns the created {@code XacmlPolicySet} object for the user.
     * @throws java.net.URISyntaxException
     * @throws com.sun.xacml.UnknownIdentifierException
     * @throws de.escidoc.core.common.exceptions.system.SystemException
     */
    private XacmlPolicySet getUserGroupPolicies(final String userId) throws UnknownIdentifierException,
        URISyntaxException, SystemException {

        final List<AbstractPolicy> policies = new ArrayList<AbstractPolicy>();
        // get groups the user belongs to
        Set<String> userGroups = null;
        try {
            userGroups = securityHelper.getUserGroups(userId);
        }
        catch (UserAccountNotFoundException e) {
            // The caller doesn't expect to get an exception from here if
            // the user doesn't exist.
            if (LOGGER.isWarnEnabled()) {
                LOGGER.warn("Error on retrieving user-groups.");
            }
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Error on retrieving ser-groups.", e);
            }
        }
        if (userGroups != null && !userGroups.isEmpty()) {
            for (final String groupId : userGroups) {
                final XacmlPolicySet groupPolicySet = securityHelper.getGroupPolicies(groupId, this.policyFinder);
                if (groupPolicySet.getChildren() != null && !groupPolicySet.getChildren().isEmpty()) {
                    policies.add(groupPolicySet);
                }
            }
        }
        if (!policies.isEmpty()) {
            return new XacmlPolicySet("GroupPolicies-" + userId,
                XacmlPolicySet.URN_POLICY_COMBINING_ALGORITHM_ORDERED_PERMIT_OVERRIDES, null, policies);
        }
        return null;

    }

    /**
     * Creates a {@code PolicyFinderResult} object holding a processing error status and the provided exception.
     *
     * @param msg The error message.
     * @param e   The exception causing the error.
     * @return Returns the created {@code PolicyFinderResult} object.
     */
    private static PolicyFinderResult createProcessingError(final String msg, final Exception e) {

        LOGGER.error(msg, e);
        final Exception ex = e instanceof EscidocException ? e : new WebserverSystemException(e);
        return new PolicyFinderResult(CustomStatusBuilder.createErrorStatus(Status.STATUS_PROCESSING_ERROR, ex));
    }

    /**
     * Sets the policy finder.
     *
     * @param policyFinder The {@code PolicyFinder} object to set.
     */
    public void setPolicyFinder(final PolicyFinder policyFinder) {

        if (policyFinder == null) {
            throw new IllegalArgumentException("Policy finder must be provided.");
        }
        this.policyFinder = policyFinder;
    }

}
