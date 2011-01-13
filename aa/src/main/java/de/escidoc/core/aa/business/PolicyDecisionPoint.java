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
package de.escidoc.core.aa.business;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
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

import org.springframework.beans.factory.InitializingBean;

import com.sun.xacml.BasicEvaluationCtx;
import com.sun.xacml.Indenter;
import com.sun.xacml.ParsingException;
import com.sun.xacml.attr.AttributeValue;
import com.sun.xacml.attr.BagAttribute;
import com.sun.xacml.attr.StringAttribute;
import com.sun.xacml.cond.EvaluationResult;
import com.sun.xacml.ctx.Attribute;
import com.sun.xacml.ctx.RequestCtx;
import com.sun.xacml.ctx.ResponseCtx;
import com.sun.xacml.ctx.Result;
import com.sun.xacml.ctx.Status;
import com.sun.xacml.ctx.Subject;
import com.sun.xacml.finder.AttributeFinder;

import de.escidoc.core.aa.business.authorisation.CustomPdp;
import de.escidoc.core.aa.business.authorisation.CustomPolicyBuilder;
import de.escidoc.core.aa.business.authorisation.FinderModuleHelper;
import de.escidoc.core.aa.business.cache.PoliciesCache;
import de.escidoc.core.aa.business.filter.AccessRights;
import de.escidoc.core.aa.business.interfaces.PolicyDecisionPointInterface;
import de.escidoc.core.aa.business.interfaces.UserAccountHandlerInterface;
import de.escidoc.core.aa.business.persistence.EscidocRole;
import de.escidoc.core.aa.business.persistence.EscidocRoleDaoInterface;
import de.escidoc.core.aa.business.persistence.RoleGrant;
import de.escidoc.core.aa.business.persistence.ScopeDef;
import de.escidoc.core.aa.business.xacml.finder.CheckProvidedAttributeFinderModule;
import de.escidoc.core.aa.business.xacml.finder.TripleStoreAttributeFinderModule;
import de.escidoc.core.aa.business.xacml.util.MapResult;
import de.escidoc.core.aa.convert.XacmlParser;
import de.escidoc.core.aa.security.cache.SecurityInterceptorCache;
import de.escidoc.core.aa.service.interfaces.RoleHandlerInterface;
import de.escidoc.core.aa.service.interfaces.UserGroupHandlerInterface;
import de.escidoc.core.cmm.service.interfaces.ContentModelHandlerInterface;
import de.escidoc.core.common.business.Constants;
import de.escidoc.core.common.business.aa.authorisation.AttributeIds;
import de.escidoc.core.common.business.fedora.TripleStoreUtility;
import de.escidoc.core.common.business.fedora.resources.ResourceType;
import de.escidoc.core.common.exceptions.application.invalid.XmlCorruptedException;
import de.escidoc.core.common.exceptions.application.invalid.XmlSchemaValidationException;
import de.escidoc.core.common.exceptions.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.application.notfound.ResourceNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.UserAccountNotFoundException;
import de.escidoc.core.common.exceptions.application.security.AuthenticationException;
import de.escidoc.core.common.exceptions.application.security.AuthorizationException;
import de.escidoc.core.common.exceptions.system.SqlDatabaseSystemException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.util.logger.AppLogger;
import de.escidoc.core.common.util.security.helper.InvocationParser;
import de.escidoc.core.common.util.security.persistence.MethodMapping;
import de.escidoc.core.common.util.security.persistence.MethodMappingList;
import de.escidoc.core.common.util.security.persistence.RequestMappingDaoInterface;
import de.escidoc.core.common.util.service.UserContext;
import de.escidoc.core.common.util.string.StringUtility;
import de.escidoc.core.common.util.xml.XmlUtility;
import de.escidoc.core.oai.service.interfaces.SetDefinitionHandlerInterface;
import de.escidoc.core.om.service.interfaces.ContainerHandlerInterface;
import de.escidoc.core.om.service.interfaces.ContentRelationHandlerInterface;
import de.escidoc.core.om.service.interfaces.ContextHandlerInterface;
import de.escidoc.core.om.service.interfaces.ItemHandlerInterface;
import de.escidoc.core.oum.service.interfaces.OrganizationalUnitHandlerInterface;
import de.escidoc.core.sm.service.interfaces.AggregationDefinitionHandlerInterface;
import de.escidoc.core.sm.service.interfaces.ReportDefinitionHandlerInterface;
import de.escidoc.core.sm.service.interfaces.ScopeHandlerInterface;

/**
 * Implementation for the AA Component.
 * 
 * @spring.bean id="business.PolicyDecisionPoint" init-method="init"
 * @author TTE, ROW
 */

public class PolicyDecisionPoint
    implements PolicyDecisionPointInterface, InitializingBean {

    /**
     * The logger.
     */
    private static final AppLogger LOG = new AppLogger(
        PolicyDecisionPoint.class.getName());

    /**
     * Pattern to detect an action id.
     */
    private static final Pattern PATTERN_ACTION_ID = Pattern
        .compile(AttributeIds.URN_ACTION_ID);

    /**
     * Pattern to detect a subject id.
     */
    private static final Pattern PATTERN_SUBJECT_ID = Pattern
        .compile(AttributeIds.URN_SUBJECT_ID);

    private static final String UNSUPPORTED_SCOPE_DEF = "Filtering with user"
        + " role currently is not supported for scope definitions that need"
        + " recursive fetching of attribute values from tripple store";

    private static final String INVALID_ATTRIBUTE_URI_IN_SCOPE_DEF =
        "Invalid attribute URI in scope definition";

    private static final String ERROR_MORE_THAN_ONE_RESULT =
        "Error in XACML engine: More than one result returned!";

    private static final String ERROR_OBLIGATIONS_ARE_NOT_SUPPORTED =
        "Error in XACML engine: Found obligations, but obligations are not"
            + " supported.";

    /**
     * The regexp pattern used to remove prefixes from the provided requests xml
     * data.
     */
    private static final Pattern PREFIX_PATTERN = Pattern
        .compile("(</{0,1})[^: >]+?:");

    /**
     * The regexp pattern used to split the provided requests xml data into its
     * contained xacml Requests and to extract the corresponding index value.
     */
    private static final Pattern SPLIT_PATTERN = Pattern.compile(
        "(<Request.*?</Request *>)", Pattern.MULTILINE | Pattern.DOTALL);

    /** The regexp pattern used to trim the provided requests xml data. */
    private static final Pattern TRIM_PATTERN = Pattern
        .compile("[\\n\\r\\s]*([<>]+)[\\n\\r\\s]*");

    private AccessRights accessRights;

    private CustomPdp customPdp;

    private SecurityInterceptorCache cache;

    private RequestMappingDaoInterface requestMappingDao;

    private EscidocRoleDaoInterface roleDao;

    private InvocationParser invocationParser;

    private UserAccountHandlerInterface userAccountHandler;

    private TripleStoreAttributeFinderModule tripleStoreAttributeFinderModule;

    private TripleStoreUtility tsu;

    private XacmlParser xacmlParser;

    private final Map<String, String> handlerClassNames =
        new HashMap<String, String>();

    private final Map<String, URI> uriCache = new HashMap<String, URI>();

    /**
     * Default constructor.
     * 
     * 
     */
    public PolicyDecisionPoint() {

        initializeHandlerClassNames();

    }

    /**
     * Initialize the PDP.
     * 
     * To synchronize AA with the resource cache all roles are fetched and
     * converted into SQL statements.
     * 
     * @throws SqlDatabaseSystemException
     *             thrown if an error occurred when accessing the database
     * @throws WebserverSystemException
     *             thrown in case of an internal error
     */
    public void init() throws SqlDatabaseSystemException,
        WebserverSystemException {
        accessRights.deleteAccessRights();

        Map<String, Object> filter = new HashMap<String, Object>();

        filter.put(Constants.FILTER_PATH_NAME, "%");
        List<EscidocRole> roles =
            roleDao.retrieveRoles(filter, 0, 0, null, null);

        roles.add(roleDao.retrieveRole(EscidocRole.DEFAULT_USER_ROLE_ID));
        for (EscidocRole role : roles) {
            xacmlParser.parse(role);
            for (ResourceType resourceType : ResourceType.values()) {
                try {
                    String scopeRules = xacmlParser.getScopeRules(resourceType);
                    String policyRules =
                        xacmlParser.getPolicyRules(resourceType);

                    LOG.info("create access right (" + role.getId() + ","
                        + resourceType + "," + scopeRules + "," + policyRules
                        + ")");
                    accessRights.putAccessRight(resourceType, role.getId(),
                        scopeRules, policyRules);
                }
                catch (Exception e) {
                    String message =
                        "The translation from XACML to SQL failed. Please try to "
                            + "paraphrase your policy or contact the developer team "
                            + "to extend the conversion rules accordingly.";

                    LOG.error(message);
                    LOG.error("error message: " + e.getMessage());
                    throw new WebserverSystemException(message, e);
                }
            }
        }
    }

    // CHECKSTYLE:JAVADOC-OFF

    /**
     * See Interface for functional description.
     * 
     * @param requests
     * @return
     * @throws ResourceNotFoundException
     * @throws MissingMethodParameterException
     * @throws AuthenticationException
     * @throws AuthorizationException
     * @throws SystemException
     * @see de.escidoc.core.aa.service.interfaces.PolicyDecisionPointInterface
     *      #evaluate(java.lang.String)
     */
    public boolean[] evaluateRequestList(
        final List<Map<String, String>> requests)
        throws ResourceNotFoundException, MissingMethodParameterException,
        AuthenticationException, AuthorizationException, SystemException {

        boolean[] allowedObjects = new boolean[requests.size()];
        int i = 0;

        Iterator<Map<String, String>> requestsIter = requests.iterator();
        while (requestsIter.hasNext()) {
            final Map<String, String> attributeMap = requestsIter.next();
            Iterator<String> attributeUriIter =
                attributeMap.keySet().iterator();
            Set<Subject> subjects = null;
            Set<Attribute> actions = null;
            Set<Attribute> resourceAttributes = new HashSet<Attribute>();
            while (attributeUriIter.hasNext()) {
                final String uriString = attributeUriIter.next();
                URI uri = uriCache.get(uriString);
                if (uri == null) {
                    try {
                        uri = new URI(uriString);
                    }
                    catch (URISyntaxException e) {
                        // FIXME: Other exception?
                        throw new SystemException(e.getMessage(), e);
                    }
                    uriCache.put(uriString, uri);
                }
                final String value = attributeMap.get(uriString);
                final StringAttribute attributeValue =
                    new StringAttribute(value);
                final Attribute attribute =
                    new Attribute(uri, null, null, attributeValue);

                if (PATTERN_SUBJECT_ID.matcher(uriString).find()) {
                    if (subjects != null) {
                        // FIXME: other exception?
                        throw new SystemException(
                            "Duplicate definition of subject id");
                    }
                    Set<Attribute> subjectAttributes =
                        new HashSet<Attribute>(1);
                    subjectAttributes.add(attribute);
                    subjects = new HashSet<Subject>(1);
                    subjects.add(new Subject(subjectAttributes));
                }
                else if (PATTERN_ACTION_ID.matcher(uriString).find()) {
                    if (actions != null) {
                        // FIXME: other exception?
                        throw new SystemException(
                            "Duplicate definition of action id");
                    }
                    actions = new HashSet<Attribute>(1);
                    actions.add(attribute);
                }
                else {
                    resourceAttributes.add(attribute);
                }
            }

            // Provide attributes provided in the evaluation request for
            // checking during attribute resolving.
            Iterator<Attribute> iter = resourceAttributes.iterator();
            Set<Attribute> uris =
                new HashSet<Attribute>(resourceAttributes.size());
            while (iter.hasNext()) {
                Attribute attr = iter.next();
                uris.add(new Attribute(CheckProvidedAttributeFinderModule
                    .getAttributeId(), null, null, new StringAttribute(attr
                    .getId().toString())));
            }
            RequestCtx requestCtx =
                new RequestCtx(subjects, resourceAttributes, actions, uris);

            ResponseCtx responseCtx = doEvaluate(requestCtx);
            Result result = extractSingleResultWithoutObligations(responseCtx);
            allowedObjects[i] = handleResult(result);
            i++;
        }

        return allowedObjects;
    }

    /**
     * See Interface for functional description.
     * 
     * @param requestsXml
     * @return
     * @throws ResourceNotFoundException
     * @throws XmlSchemaValidationException
     * @throws SystemException
     * @see de.escidoc.core.aa.business.interfaces.PolicyDecisionPointInterface
     *      #checkUserPrivilegeOnListofObjects(java.lang.String)
     */
    public boolean[] checkUserPrivilegeOnListofObjects(final String requestsXml)
        throws ResourceNotFoundException, XmlSchemaValidationException,
        SystemException {

        List<ResponseCtx> responseCtxs = doEvaluate(requestsXml);

        boolean[] allowedObjects = new boolean[responseCtxs.size()];
        int i = 0;
        Iterator<ResponseCtx> iter = responseCtxs.iterator();
        while (iter.hasNext()) {
            ResponseCtx responseCtx = iter.next();
            Result result = extractSingleResultWithoutObligations(responseCtx);

            allowedObjects[i] = handleResult(result);
            i++;
        }
        return allowedObjects;
    }

    /**
     * See Interface for functional description.
     * 
     * @param requestsXml
     * @return
     * @throws ResourceNotFoundException
     * @throws XmlSchemaValidationException
     * @throws SystemException
     * @see de.escidoc.core.aa.service.interfaces.PolicyDecisionPointInterface
     *      #evaluate(java.lang.String)
     */
    public String evaluate(final String requestsXml)
        throws ResourceNotFoundException, XmlSchemaValidationException,
        XmlCorruptedException, SystemException {

        XmlUtility.validate(requestsXml,
            XmlUtility.getPdpRequestsSchemaLocation());

        List<ResponseCtx> responseCtxs = doEvaluate(requestsXml);

        StringBuffer buf = new StringBuffer("<results xmlns=\"");
        buf.append(Constants.RESULTS_NS_URI);
        buf.append("\" xmlns:xacml-context=\"");
        buf.append(Constants.XACML_CONTEXT_NS_URI);
        buf.append("\">");

        Iterator<ResponseCtx> iter = responseCtxs.iterator();
        while (iter.hasNext()) {
            ResponseCtx responseCtx = iter.next();
            Result result = extractSingleResultWithoutObligations(responseCtx);

            String decision;
            if (result.getDecision() == Result.DECISION_PERMIT) {
                decision = "permit";
            }
            else {
                decision = "deny";
            }

            buf.append("<result decision=\"");
            buf.append(decision);
            buf.append("\">");

            buf.append(CustomPolicyBuilder.encode(responseCtx));

            buf.append("</result>");

        }

        buf.append("</results>");

        return buf.toString();
    }

    /**
     * See Interface for functional description.
     * 
     * @param resourceName
     * @param methodName
     * @param argumentList
     * @return List, marked with allowed or denied
     * @see de.escidoc.core.aa.service.interfaces.AaInterface
     *      #evaluateMethodForList(java.lang.String, java.lang.String, List)
     */
    public List<Object[]> evaluateMethodForList(
        final String resourceName, final String methodName,
        final List<Object[]> argumentList) throws ResourceNotFoundException,
        SystemException {

        // convert the resourceName if provided in triple store format
        final String convertedResourceName =
            FinderModuleHelper.convertObjectType(resourceName, false);

        final String className =
            handlerClassNames.get(convertedResourceName.toLowerCase());

        final List<Object[]> ret = new ArrayList<Object[]>();

        try {
            final MethodMappingList methodMappings =
                cache.getMethodMappings(className, methodName);
            for (Object[] arguments : argumentList) {
                boolean allowed = true;
                final Iterator<MethodMapping> iter =
                    methodMappings.iteratorBefore();
                if (iter != null) {
                    while (allowed && iter.hasNext()) {
                        final List<Map<String, String>> requests =
                            invocationParser.buildRequestsList(arguments,
                                iter.next());
                        boolean[] accessAllowedArray =
                            evaluateRequestList(requests);
                        for (int j = 0; j < accessAllowedArray.length; j++) {
                            if (!accessAllowedArray[j]) {
                                allowed = false;
                                break;
                            }
                        }
                    }
                }
                if (allowed) {
                    ret.add(arguments);
                }
            }
        }
        catch (ResourceNotFoundException e) {
            throw e;
        }
        catch (SystemException e) {
            throw e;
        }
        catch (Exception e) {
            throw new WebserverSystemException(
                "Unhandled exception in evaluateRetrieve. ", e);
        }

        return ret;
    }

    /**
     * See Interface for functional description.
     * 
     * @param resourceName
     * @param ids
     * @return
     * @see de.escidoc.core.aa.service.interfaces.AaInterface
     *      #evaluateRetrieve(java.lang.String, List)
     */
    public List<String> evaluateRetrieve(
        final String resourceName, final List<String> ids)
        throws ResourceNotFoundException, SystemException {

        // convert the resourceName if provided in triple store format
        final String convertedResourceName =
            FinderModuleHelper.convertObjectType(resourceName, false);

        final String className =
            handlerClassNames.get(convertedResourceName.toLowerCase());

        final List<String> ret = new ArrayList<String>();

        try {
            final MethodMappingList methodMappings =
                cache.getMethodMappings(className, "retrieve");
            final Iterator<String> idIter = ids.iterator();
            while (idIter.hasNext()) {
                String id = idIter.next();
                boolean allowed = true;
                final Iterator<MethodMapping> iter =
                    methodMappings.iteratorBefore();
                if (iter != null) {
                    while (allowed && iter.hasNext()) {
                        final List<Map<String, String>> requests =
                            invocationParser.buildRequestsList(id, iter.next());
                        allowed = evaluateRequestList(requests)[0];
                    }
                }
                if (allowed) {
                    ret.add(id);
                }
            }
        }
        catch (ResourceNotFoundException e) {
            throw e;
        }
        catch (SystemException e) {
            throw e;
        }
        catch (Exception e) {
            throw new WebserverSystemException(
                "Unhandled exception in evaluateRetrieve. ", e);
        }

        return ret;
    }

    /**
     * See Interface for functional description.
     * 
     * @param resourceName
     * @param userId
     * @param role
     * @param objectIds
     * @return
     * @throws UserAccountNotFoundException
     * @throws ResourceNotFoundException
     * @throws MissingMethodParameterException
     * @throws SystemException
     * @see de.escidoc.core.aa.service.interfaces.PolicyDecisionPointInterface
     *      #evaluateRoles(java.lang.String, java.lang.String, java.lang.String,
     *      java.util.List)
     */
    public List<String> evaluateRoles(
        final String resourceName, final String userId, final String role,
        final List<String> objectIds) throws ResourceNotFoundException,
        MissingMethodParameterException, SystemException {

        final StringBuffer whereClause =
            getRoleUserWhereClause(resourceName, userId, role);
        if (whereClause == null) {
            return new ArrayList<String>(0);
        }
        if (whereClause.length() == 0) {
            return objectIds;
        }
        else {
            Set<String> ret = new HashSet<String>();
            ret.addAll(FinderModuleHelper.retrieveFromTripleStore(true,
                whereClause, null, null, tsu));

            final List<String> retList = new ArrayList<String>();
            final Iterator<String> iter = objectIds.iterator();
            while (iter.hasNext()) {
                String objid = iter.next();
                if (ret.contains(objid)) {
                    retList.add(objid);
                }
            }

            return retList;
        }
    }

    /**
     * See Interface for functional description.
     * 
     * @param resourceName
     * @param userId
     * @param role
     * @return
     * @throws MissingMethodParameterException
     * @throws SystemException
     * @see de.escidoc.core.aa.service.interfaces.PolicyDecisionPointInterface
     *      #getRoleUserWhereClause(java.lang.String, java.lang.String,
     *      java.lang.String)
     */
    public StringBuffer getRoleUserWhereClause(
        final String resourceName, final String userId, final String role)
        throws MissingMethodParameterException, SystemException {
        // Get id for role
        String roleId = null;
        if (role != null) {
            EscidocRole roleDo = roleDao.retrieveRole(role);
            if (roleDo != null) {
                roleId = roleDo.getId();
            }
        }

        if (resourceName == null) {
            throw new MissingMethodParameterException(
                "Resource name must be specified.");
        }
        if (userId == null) {
            throw new MissingMethodParameterException(
                "User id must be specified");
        }

        // convert the resourceName if provided in triple store format
        final String convertedResourceName =
            FinderModuleHelper.convertObjectType(resourceName, false);

        Map currentGrants = null;
        try {
            currentGrants =
                userAccountHandler.retrieveCurrentGrantsAsMap(userId);
        }
        catch (UserAccountNotFoundException e) {
            return null;
        }
        catch (Exception e) {
            throw new WebserverSystemException(
                "Unhandled exception in evaluateRoles.", e);
        }

        final Collection<String> roleIds;
        if (roleId == null) {
            roleIds = currentGrants.keySet();
            if (roleIds == null || roleIds.isEmpty()) {
                return null;
            }
        }
        else {
            roleIds = new HashSet<String>();
            roleIds.add(roleId);
        }

        final Collection<EscidocRole> roles = new HashSet<EscidocRole>();
        final Iterator<String> roleIdIter = roleIds.iterator();
        while (roleIdIter.hasNext()) {
            final String id = roleIdIter.next();
            EscidocRole escidocRole = PoliciesCache.getRole(id);
            if (escidocRole == null) {
                escidocRole = roleDao.retrieveRole(id);
                PoliciesCache.putRole(escidocRole.getId(), escidocRole);
            }
            if (escidocRole == null) {
                return null;
            }
            if (escidocRole.isLimited()) {
                roles.add(escidocRole);
            }
            else {
                // role is unlimited and therefore true for all objects. No
                // additional where clause needed, empty string buffer is
                // returned.
                return new StringBuffer();
            }
        }

        final StringBuffer whereClause = new StringBuffer();
        Iterator<EscidocRole> roleIter = roles.iterator();
        while (roleIter.hasNext()) {
            final EscidocRole escidocRole = roleIter.next();
            final Iterator<ScopeDef> scopeDefIter =
                escidocRole.getScopeDefs().iterator();
            while (scopeDefIter.hasNext()) {
                final ScopeDef scopeDef = scopeDefIter.next();
                // TODO FRS: try to add "member"
                if (!scopeDef.getObjectType().equals(convertedResourceName)
                    && !convertedResourceName.equals("member")) {
                    continue;
                }
                else if (convertedResourceName.equals("member")
                    && !(scopeDef.getObjectType().equals("item") || !scopeDef
                        .getObjectType().equals("container"))) {
                    continue;
                }

                final String attributeId = scopeDef.getAttributeId();

                final MapResult mapResult =
                    tripleStoreAttributeFinderModule.mapIt(attributeId);

                if (mapResult == null) {
                    throw new WebserverSystemException(StringUtility
                        .concatenateWithBrackets(
                            INVALID_ATTRIBUTE_URI_IN_SCOPE_DEF,
                            scopeDef.getEscidocRole().getRoleName(),
                            scopeDef.getObjectType()).toString());
                }

                if (mapResult.hasNext()) {
                    throw new WebserverSystemException(StringUtility
                        .concatenateWithBrackets(UNSUPPORTED_SCOPE_DEF,
                            scopeDef.getEscidocRole().getRoleName(),
                            scopeDef.getObjectType(), attributeId).toString());
                }

                final Map<String, RoleGrant> grantsOfRole =
                    (Map<String, RoleGrant>) currentGrants.get(escidocRole
                        .getId());
                if (grantsOfRole != null) {
                    final Iterator<String> idIter =
                        grantsOfRole.keySet().iterator();
                    while (idIter.hasNext()) {
                        // we perform an inverse lookup here, therefore, the
                        // inverse flag of the map result must be switched
                        // (inverse -> forward, forward -> inverse)

                        // here, the original resource name has to be used to
                        // find any value
                        final String id = idIter.next();
                        if (whereClause.length() != 0) {
                            whereClause.append(" or ");
                        }
                        whereClause.append(mapResult
                            .getResolveCurrentInverseWhereClause(id,
                                resourceName, tsu));
                    }
                }
            }
        }
        return whereClause;
    }

    /**
     * See Interface for functional description.
     * 
     * @param attributeType
     * @param attributeId
     * @param issuer
     * @param subjectCategory
     * @param context
     * @param designatorType
     * @return
     * @throws SystemException
     * @see de.escidoc.core.aa.service.interfaces.PolicyDecisionPointInterface
     *      #findAttribute(java.net.URI, java.net.URI, java.net.URI,
     *      java.net.URI, com.sun.xacml.EvaluationCtx, int)
     */
    public String[] findAttribute(
        final URI attributeType, final URI attributeId, final URI issuer,
        final URI subjectCategory, final String context,
        final int designatorType) throws SystemException {

        BasicEvaluationCtx ctx;
        try {
            ByteArrayInputStream is =
                new ByteArrayInputStream(
                    context.getBytes(XmlUtility.CHARACTER_ENCODING));
            final AttributeFinder finder =
                customPdp.getPdpConfig().getAttributeFinder();
            ctx = new BasicEvaluationCtx(RequestCtx.getInstance(is), finder);

            final EvaluationResult attribute =
                finder.findAttribute(attributeType, attributeId, issuer,
                    subjectCategory, ctx, designatorType);

            if (attribute.getStatus() != null) {
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                attribute.getStatus().encode(os);
                return new String[] { os.toString() };
            }
            else {
                final AttributeValue attributeValue =
                    attribute.getAttributeValue();
                if (attributeValue.isBag()) {
                    final BagAttribute bagAttribute =
                        (BagAttribute) attributeValue;
                    Iterator<StringAttribute> iter = bagAttribute.iterator();
                    String[] ret = new String[bagAttribute.size()];
                    int i = 0;
                    while (iter.hasNext()) {
                        ret[i++] = (iter.next()).getValue();
                    }
                    return ret;
                }
                else {
                    return new String[] { ((StringAttribute) attributeValue)
                        .getValue() };
                }
            }

        }
        catch (Exception e) {
            throw new SystemException(e.getMessage(), e);
        }
    }

    // CHECKSTYLE:JAVADOC-ON

    /**
     * Handles the provided result.<br>
     * This method examines the provided result. In case of an error result, it
     * is checked if a <code>ResourceNotFoundException</code> has to be thrown
     * or if another error occurred that is reported as a
     * <code>WebserverSystemException</code>. If no error occurred, either
     * 
     * @param result
     *            The result of evaluating the request.
     * @throws WebserverSystemException
     *             Thrown in case of any error except a resource not found
     *             error.
     * @return Returns <code>true</code>, if no error occurred and the provided
     *         result contains a permit decision. Returns <code>false</code>, if
     *         no error occurred and the result does not contain a permit
     *         decision.
     * @throws ResourceNotFoundException
     *             Thrown in case of a resource not found error.
     */
    private boolean handleResult(final Result result)
        throws WebserverSystemException, ResourceNotFoundException {

        // any decision other than permit means denial, throw exception
        // From XACML spec: A PEP SHALL allow access to the resource only if a
        // valid XACML response of "Permit" is returned by the PDP. The PEP
        // SHALL deny access to the resource in all other cases.An XACML
        // response of "Permit" SHALL be considered valid only if the PEP
        // understands all of the obligations contained in the response.
        if (result.getDecision() != Result.DECISION_PERMIT) {
            final Status status = result.getStatus();
            final String statusCode = (String) status.getCode().get(0);
            if (!statusCode.equals(Status.STATUS_OK)) {

                if (statusCode.startsWith(AttributeIds.STATUS_PREFIX)) {

                    ResourceNotFoundException e = null;
                    try {
                        final Class clazz =
                            Class
                                .forName(statusCode
                                    .substring(AttributeIds.STATUS_PREFIX
                                        .length()));
                        if (ResourceNotFoundException.class
                            .isAssignableFrom(clazz)) {

                            String statusMessage = status.getMessage();
                            e =
                                (ResourceNotFoundException) clazz
                                    .getConstructor(
                                        new Class[] { String.class })
                                    .newInstance(new Object[] { statusMessage });
                        }
                    }
                    catch (Exception e1) {
                        e = null;
                    }
                    if (e != null) {
                        throw e;
                    }
                    else {
                        throw new WebserverSystemException(
                            StringUtility.concatenateWithBracketsToString(
                                "Error reported during policy evaluation ",
                                result.getResource(), encode(status)));
                    }

                }

                throw new WebserverSystemException(
                    StringUtility.concatenateWithBracketsToString(
                        "XACML error reported during policy evaluation ",
                        result.getResource(), encode(status)));
            }

            if (LOG.isDebugEnabled()) {
                StringBuffer msg =
                    new StringBuffer(
                        "Access not granted. Reason from XACML engine: ");
                msg.append(Result.DECISIONS[result.getDecision()]);
                LOG.debug(msg.toString());
            }
            return false;
        }
        else {
            return true;
        }
    }

    /**
     * Encodes the provided XACML status.
     * 
     * @param status
     *            The XACML status to encode.
     * @return Returns the encoded status.
     */
    private static String encode(final Status status) 
                            throws WebserverSystemException {
        String ret = null;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        status.encode(out, new Indenter());
        try {
            ret = out.toString(XmlUtility.CHARACTER_ENCODING);
        } catch (UnsupportedEncodingException e) {
            throw new WebserverSystemException(e.getMessage(), e);
        }
        try {
            out.close();
        }
        catch (IOException e1) {
            out = null;
        }
        return ret;
    }

    /**
     * Extracts the expected one and only result without any obligations from
     * the provided xacml response object.
     * 
     * @param responseCtx
     *            The xacml response object
     * @return Returns the extracted {@link com.sun.xacml.ctx.Result} object.
     * @throws WebserverSystemException
     *             Thrown if there is not exactly one result in the provided
     *             xacml response object, or the result has obligations.
     */
    private Result extractSingleResultWithoutObligations(
        final ResponseCtx responseCtx) throws WebserverSystemException {

        if (responseCtx.getResults().size() != 1) {
            throw new WebserverSystemException(ERROR_MORE_THAN_ONE_RESULT);
        }
        Result result = (Result) responseCtx.getResults().iterator().next();
        if (!result.getObligations().isEmpty()) {
            throw new WebserverSystemException(
                ERROR_OBLIGATIONS_ARE_NOT_SUPPORTED);
        }
        return result;
    }

    /**
     * Helper method to evaluate the xacml requests in the provided xml
     * representation of authorization requests.
     * 
     * @param requestsXml
     *            The xml representation of authorization requests
     * @return Returns the list of the xacml response objects holding the
     *         evaluation results of the provided requests (in the same order).
     * @throws XmlSchemaValidationException
     *             Thrown in case of a schema validation error or a corrupted
     *             xml.
     * @throws SystemException
     *             Thrown in case of an internal error.
     */
    private List<ResponseCtx> doEvaluate(final String requestsXml)
        throws XmlSchemaValidationException, SystemException {

        // trim white spaces and newlines around < and >
        Matcher matcher = TRIM_PATTERN.matcher(requestsXml);
        String xml = matcher.replaceAll("$1");

        // remove all prefixes to enable later parsing of xacml requests
        matcher = PREFIX_PATTERN.matcher(xml);
        xml = matcher.replaceAll("$1");

        StringBuffer buf = new StringBuffer("<results xmlns=\"");
        buf.append(Constants.RESULTS_NS_URI);
        buf.append("\" xmlns:xacml-context=\"");
        buf.append(Constants.XACML_CONTEXT_NS_URI);
        buf.append("\">");

        matcher = SPLIT_PATTERN.matcher(xml);
        int i = 0;
        Vector<RequestCtx> requestCtxs = new Vector<RequestCtx>();
        while (matcher.find(i)) {
            try {
                RequestCtx requestCtx =
                    RequestCtx.getInstance(new ByteArrayInputStream(matcher
                        .group(1).getBytes(XmlUtility.CHARACTER_ENCODING)));
                Set<Attribute> resources = requestCtx.getResource();
                Set<Attribute> uris = new HashSet<Attribute>(resources.size());
                for (Attribute attribute : resources) {
                    uris.add(new Attribute(CheckProvidedAttributeFinderModule
                        .getAttributeId(), null, null, new StringAttribute(
                        attribute.getId().toString())));
                }
                for (Attribute attribute : (Set<Attribute>) requestCtx
                    .getEnvironmentAttributes()) {
                    uris.add(attribute);
                }
                requestCtx =
                    new RequestCtx(requestCtx.getSubjects(),
                        requestCtx.getResource(), requestCtx.getAction(), uris);
                requestCtxs.add(requestCtx);
            }
            catch (UnsupportedEncodingException e) {
                throw new WebserverSystemException(e.getMessage(), e);
            }
            catch (ParsingException e) {
                throw new XmlSchemaValidationException(e.getMessage(), e);
            }
            i = matcher.end();
        }

        return doEvaluate(requestCtxs);
    }

    /**
     * Helper method to evaluate the provided xacml requests.
     * 
     * @param requestCtxs
     *            The list of xacml request to evaluate.
     * @return Returns the list of the xacml response objects holding the
     *         evaluation results of the provided requests (in the same order).
     * @throws WebserverSystemException
     *             Thrown in case of an internal error.
     */
    private List<ResponseCtx> doEvaluate(final List<RequestCtx> requestCtxs)
        throws WebserverSystemException {

        Iterator<RequestCtx> iter = requestCtxs.iterator();
        List<ResponseCtx> responsCtxs =
            new Vector<ResponseCtx>(requestCtxs.size());
        while (iter.hasNext()) {
            responsCtxs.add(doEvaluate(iter.next()));
        }
        return responsCtxs;
    }

    /**
     * Helper method to evaluate the provided xacml request.
     * 
     * @param requestCtx
     *            The xacml request to evaluate
     * @return Returns the xacml response object holding the evaluation results.
     * 
     * @throws WebserverSystemException
     *             Thrown in case of an internal error.
     */
    private ResponseCtx doEvaluate(final RequestCtx requestCtx)
        throws WebserverSystemException {

        if (LOG.isDebugEnabled()) {
            LOG.debug("Request: ");
            ByteArrayOutputStream writer = new ByteArrayOutputStream();
            requestCtx.encode(writer, new Indenter());
            LOG.debug(writer.toString());
            try {
                writer.close();
            }
            catch (IOException e) {
                writer = null;
            }
        }

        // Checks the generated request against the XACML engine
        ResponseCtx response;
        boolean wasExternalBefore = false;
        try {
            // The evaluation has to be run as authorization user
            wasExternalBefore = UserContext.runAsInternalUser();
            response = customPdp.evaluate(requestCtx);
        }
        catch (RuntimeException e) {
            throw new WebserverSystemException(e.getMessage(), e);
        }
        finally {
            if (wasExternalBefore) {
                UserContext.runAsExternalUser();
            }
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("Response: ");
            ByteArrayOutputStream writer = new ByteArrayOutputStream();
            response.encode(writer, new Indenter());
            LOG.debug(writer.toString());
            try {
                writer.close();
            }
            catch (IOException e) {
                writer = null;
            }
        }

        return response;
    }

    /**
     * Initializes the handler class name map.
     */
    private void initializeHandlerClassNames() {

        handlerClassNames.put("container",
            ContainerHandlerInterface.class.getName());
        handlerClassNames.put("context",
            ContextHandlerInterface.class.getName());
        handlerClassNames.put("item", ItemHandlerInterface.class.getName());
        handlerClassNames.put("content-relation",
            ContentRelationHandlerInterface.class.getName());
        handlerClassNames.put("content-model",
            ContentModelHandlerInterface.class.getName());
        handlerClassNames.put("organizational-unit",
            OrganizationalUnitHandlerInterface.class.getName());
        handlerClassNames
            .put(
                "user-account",
                de.escidoc.core.aa.service.interfaces.UserAccountHandlerInterface.class
                    .getName());
        handlerClassNames.put("user-group",
            UserGroupHandlerInterface.class.getName());
        handlerClassNames.put("role", RoleHandlerInterface.class.getName());

        // OAI
        handlerClassNames.put("set-definition",
            SetDefinitionHandlerInterface.class.getName());

        // Statistic Manager
        handlerClassNames.put("scope", ScopeHandlerInterface.class.getName());
        handlerClassNames.put("aggregation-definition",
            AggregationDefinitionHandlerInterface.class.getName());
        handlerClassNames.put("report-definition",
            ReportDefinitionHandlerInterface.class.getName());
    }

    /**
     * @return Returns the customPdp.
     */
    public CustomPdp getCustomPdp() {
        return customPdp;
    }

    /**
     * Injects the access rights object.
     * 
     * @spring.property ref="resource.DbAccessRights"
     * @param accessRights
     *            access rights from Spring
     */
    public void setAccessRights(final AccessRights accessRights) {
        this.accessRights = accessRights;
    }

    /**
     * @param customPdp
     *            The customPdp to set.
     * @spring.property ref="authorisation.CustomPdp"
     */
    public void setCustomPdp(final CustomPdp customPdp) {
        this.customPdp = customPdp;
    }

    /**
     * @return Returns the requestMappingLoader.
     */
    public RequestMappingDaoInterface getRequestMappingDao() {
        return requestMappingDao;
    }

    /**
     * @param requestMappingDao
     *            The requestMappingDao to set.
     * @spring.property ref="persistence.HibernateRequestMappingDao"
     */
    public void setRequestMappingDao(
        final RequestMappingDaoInterface requestMappingDao) {
        this.requestMappingDao = requestMappingDao;
    }

    /**
     * @param roleDao
     *            The role data access object to set.
     * @spring.property ref="persistence.EscidocRoleDao"
     */
    public void setRoleDao(final EscidocRoleDaoInterface roleDao) {

        this.roleDao = roleDao;
    }

    /**
     * @param invocationParser
     *            The invocation parser to set.
     * @spring.property ref="eSciDoc.core.common.helper.InvocationParser"
     */
    public void setInvocationParser(final InvocationParser invocationParser) {

        this.invocationParser = invocationParser;
    }

    /**
     * Injects the UserAccountHandler.
     * 
     * @spring.property ref="business.UserAccountHandler"
     * 
     * @param userAccountHandler
     *            The user account handler.
     * @aa
     */
    public void setUserAccountHandler(
        final UserAccountHandlerInterface userAccountHandler) {

        this.userAccountHandler = userAccountHandler;
    }

    /**
     * Injects the {@link TripleStoreAttributeFinderModule}.
     * 
     * @param tripleStoreAttributeFinderModule
     *            The {@link TripleStoreAttributeFinderModule} to inject.
     * 
     * @spring.property ref="eSciDoc.core.aa.TripleStoreAttributeFinderModule"
     * @aa
     */
    public void setTripleStoreAttributeFinderModule(
        final TripleStoreAttributeFinderModule tripleStoreAttributeFinderModule) {

        this.tripleStoreAttributeFinderModule =
            tripleStoreAttributeFinderModule;
    }

    public TripleStoreAttributeFinderModule getTripleStoreAttributeFinderModule() {

        return this.tripleStoreAttributeFinderModule;
    }

    /**
     * Injects the {@link TripleStoreUtility}.
     * 
     * @param tsu
     *            the {@link TripleStoreUtility} to inject.
     * 
     * @spring.property ref="business.TripleStoreUtility"
     * @aa
     */
    public void setTsu(final TripleStoreUtility tsu) {

        this.tsu = tsu;
    }

    /**
     * Injects the {@link XacmlParser}.
     * 
     * @param xacmlParser
     *            the {@link XacmlParser} to inject.
     * 
     * @spring.property ref="convert.XacmlParser"
     * @aa
     */
    public void setXacmlParser(final XacmlParser xacmlParser) {

        this.xacmlParser = xacmlParser;
    }

    /**
     * Injects the {@link SecurityInterceptorCache}.
     * 
     * @param cache
     *            The {@link SecurityInterceptorCache} to be injected.
     * @spring.property ref="eSciDoc.core.common.SecurityInterceptorCache"
     * @common
     */
    public void setCache(final SecurityInterceptorCache cache) {

        this.cache = cache;
    }

    // CHECKSTYLE:JAVADOC-OFF

    /**
     * See Interface for functional description.
     * 
     * @throws Exception
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     * @aa
     */
    public void afterPropertiesSet() throws Exception {

        LOG.debug("Properties set");
    }

    public void touch() {
        // do nothing
    }

    // CHECKSTYLE:JAVADOC-ON

}
