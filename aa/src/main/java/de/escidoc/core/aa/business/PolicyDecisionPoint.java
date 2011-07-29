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
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;

import org.esidoc.core.utils.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.sun.xacml.Indenter;
import com.sun.xacml.ParsingException;
import com.sun.xacml.attr.StringAttribute;
import com.sun.xacml.ctx.Attribute;
import com.sun.xacml.ctx.RequestCtx;
import com.sun.xacml.ctx.ResponseCtx;
import com.sun.xacml.ctx.Result;
import com.sun.xacml.ctx.Status;
import com.sun.xacml.ctx.Subject;

import de.escidoc.core.aa.business.authorisation.CustomPdp;
import de.escidoc.core.aa.business.authorisation.CustomPolicyBuilder;
import de.escidoc.core.aa.business.authorisation.FinderModuleHelper;
import de.escidoc.core.aa.business.filter.AccessRights;
import de.escidoc.core.aa.business.interfaces.PolicyDecisionPointInterface;
import de.escidoc.core.aa.business.persistence.EscidocRole;
import de.escidoc.core.aa.business.persistence.EscidocRoleDaoInterface;
import de.escidoc.core.aa.business.xacml.finder.CheckProvidedAttributeFinderModule;
import de.escidoc.core.aa.convert.XacmlParser;
import de.escidoc.core.aa.security.cache.SecurityInterceptorCache;
import de.escidoc.core.aa.service.interfaces.RoleHandlerInterface;
import de.escidoc.core.aa.service.interfaces.UserAccountHandlerInterface;
import de.escidoc.core.aa.service.interfaces.UserGroupHandlerInterface;
import de.escidoc.core.cmm.service.interfaces.ContentModelHandlerInterface;
import de.escidoc.core.common.business.Constants;
import de.escidoc.core.common.business.aa.authorisation.AttributeIds;
import de.escidoc.core.common.business.fedora.resources.ResourceType;
import de.escidoc.core.common.exceptions.application.invalid.XmlCorruptedException;
import de.escidoc.core.common.exceptions.application.invalid.XmlSchemaValidationException;
import de.escidoc.core.common.exceptions.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.application.notfound.ResourceNotFoundException;
import de.escidoc.core.common.exceptions.application.security.AuthenticationException;
import de.escidoc.core.common.exceptions.application.security.AuthorizationException;
import de.escidoc.core.common.exceptions.system.SqlDatabaseSystemException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.util.security.helper.InvocationParser;
import de.escidoc.core.common.util.security.persistence.MethodMapping;
import de.escidoc.core.common.util.security.persistence.MethodMappingList;
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
 * @author Torsten Tetteroo, Roland Werner
 */
@Service("business.PolicyDecisionPoint")
public class PolicyDecisionPoint implements PolicyDecisionPointInterface {

    /**
     * The logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(PolicyDecisionPoint.class);

    /**
     * Pattern to detect an action id.
     */
    private static final Pattern PATTERN_ACTION_ID = Pattern.compile(AttributeIds.URN_ACTION_ID);

    /**
     * Pattern to detect a subject id.
     */
    private static final Pattern PATTERN_SUBJECT_ID = Pattern.compile(AttributeIds.URN_SUBJECT_ID);

    private static final String ERROR_MORE_THAN_ONE_RESULT = "Error in XACML engine: More than one result returned!";

    private static final String ERROR_OBLIGATIONS_ARE_NOT_SUPPORTED =
        "Error in XACML engine: Found obligations, but obligations are not" + " supported.";

    /**
     * The regexp pattern used to remove prefixes from the provided requests xml data.
     */
    private static final Pattern PREFIX_PATTERN = Pattern.compile("(</{0,1})[^: >]+?:");

    /**
     * The regexp pattern used to split the provided requests xml data into its contained xacml Requests and to extract
     * the corresponding index value.
     */
    private static final Pattern SPLIT_PATTERN =
        Pattern.compile("(<Request.*?</Request *>)", Pattern.MULTILINE | Pattern.DOTALL);

    /**
     * The regexp pattern used to trim the provided requests xml data.
     */
    private static final Pattern TRIM_PATTERN = Pattern.compile("[\\n\\r\\s]*([<>]+)[\\n\\r\\s]*");

    @Autowired
    @Qualifier("resource.AccessRights")
    private AccessRights accessRights;

    @Autowired
    @Qualifier("authorisation.CustomPdp")
    private CustomPdp customPdp;

    @Autowired
    @Qualifier("eSciDoc.core.common.SecurityInterceptorCache")
    private SecurityInterceptorCache cache;

    @Autowired
    @Qualifier("persistence.EscidocRoleDao")
    private EscidocRoleDaoInterface roleDao;

    @Autowired
    @Qualifier("eSciDoc.core.common.helper.InvocationParser")
    private InvocationParser invocationParser;

    @Autowired
    @Qualifier("convert.XacmlParser")
    private XacmlParser xacmlParser;

    @Autowired
    @Qualifier("common.xml.XmlUtility")
    private XmlUtility xmlUtility;

    private final Map<String, String> handlerClassNames = new HashMap<String, String>();

    private final Map<String, URI> uriCache = new HashMap<String, URI>();

    /**
     * Protected constructor to prevent instantiation outside of the Spring-context.
     */
    protected PolicyDecisionPoint() {
        initializeHandlerClassNames();
    }

    /**
     * Initialize the PDP.
     * <p/>
     * To synchronize AA with the resource cache all roles are fetched and converted into SQL statements.
     *
     * @throws SqlDatabaseSystemException thrown if an error occurred when accessing the database
     * @throws WebserverSystemException   thrown in case of an internal error
     */
    @PostConstruct
    public void init() throws SqlDatabaseSystemException, WebserverSystemException {
        accessRights.deleteAccessRights();

        final Map<String, Object> filter = new HashMap<String, Object>();

        filter.put(Constants.FILTER_PATH_NAME, "%");
        final List<EscidocRole> roles = roleDao.retrieveRoles(filter, 0, 0, null, null);

        roles.add(roleDao.retrieveRole(EscidocRole.DEFAULT_USER_ROLE_ID));
        for (final EscidocRole role : roles) {
            xacmlParser.parse(role);
            for (final ResourceType resourceType : ResourceType.values()) {
                try {
                    final String scopeRules = xacmlParser.getScopeRules(resourceType);
                    final String policyRules = xacmlParser.getPolicyRules(resourceType);
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("create access right (" + role.getId() + ',' + resourceType + ',' + scopeRules
                            + ',' + policyRules + ')');
                    }
                    accessRights.putAccessRight(resourceType, role.getId(), scopeRules, policyRules);
                }
                catch (final Exception e) {
                    final String message =
                        "The translation from XACML to SQL failed. Please try to "
                            + "paraphrase your policy or contact the developer team "
                            + "to extend the conversion rules accordingly.";
                    throw new WebserverSystemException(message, e);
                }
            }
        }
    }

    /**
     * See Interface for functional description.
     *
     * @see de.escidoc.core.aa.service.interfaces.PolicyDecisionPointInterface #evaluate(java.lang.String)
     */
    @Override
    public boolean[] evaluateRequestList(final List<Map<String, String>> requests) throws ResourceNotFoundException,
        MissingMethodParameterException, AuthenticationException, AuthorizationException, SystemException {

        final boolean[] allowedObjects = new boolean[requests.size()];
        int i = 0;

        for (final Map<String, String> attributeMap : requests) {
            final Iterator<Entry<String, String>> attributeUriIter = attributeMap.entrySet().iterator();
            Set<Subject> subjects = null;
            Set<Attribute> actions = null;
            final Set<Attribute> resourceAttributes = new HashSet<Attribute>();
            while (attributeUriIter.hasNext()) {
                final Entry<String, String> mapEntry = attributeUriIter.next();
                final String uriString = mapEntry.getKey();
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
                final String value = mapEntry.getValue();
                final StringAttribute attributeValue = new StringAttribute(value);
                final Attribute attribute = new Attribute(uri, null, null, attributeValue);

                if (PATTERN_SUBJECT_ID.matcher(uriString).find()) {
                    if (subjects != null) {
                        // FIXME: other exception?
                        throw new SystemException("Duplicate definition of subject id");
                    }
                    final Set<Attribute> subjectAttributes = new HashSet<Attribute>(1);
                    subjectAttributes.add(attribute);
                    subjects = new HashSet<Subject>(1);
                    subjects.add(new Subject(subjectAttributes));
                }
                else if (PATTERN_ACTION_ID.matcher(uriString).find()) {
                    if (actions != null) {
                        // FIXME: other exception?
                        throw new SystemException("Duplicate definition of action id");
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
            final Iterator<Attribute> iter = resourceAttributes.iterator();
            final Set<Attribute> uris = new HashSet<Attribute>(resourceAttributes.size());
            while (iter.hasNext()) {
                final Attribute attr = iter.next();
                uris.add(new Attribute(CheckProvidedAttributeFinderModule.getAttributeId(), null, null,
                    new StringAttribute(attr.getId().toString())));
            }
            final RequestCtx requestCtx = new RequestCtx(subjects, resourceAttributes, actions, uris);

            final ResponseCtx responseCtx = doEvaluate(requestCtx);
            final Result result = extractSingleResultWithoutObligations(responseCtx);
            allowedObjects[i] = handleResult(result);
            i++;
        }

        return allowedObjects;
    }

    /**
     * See Interface for functional description.
     *
     * @see de.escidoc.core.aa.service.interfaces.PolicyDecisionPointInterface #evaluate(java.lang.String)
     */
    @Override
    public String evaluate(final String requestsXml) throws ResourceNotFoundException, XmlSchemaValidationException,
        XmlCorruptedException, SystemException {

        xmlUtility.validate(requestsXml, XmlUtility.getPdpRequestsSchemaLocation());

        final List<ResponseCtx> responseCtxs = doEvaluate(requestsXml);

        final StringBuilder buf = new StringBuilder("<results xmlns=\"");
        buf.append(Constants.RESULTS_NS_URI);
        buf.append("\" xmlns:xacml-context=\"");
        buf.append(Constants.XACML_CONTEXT_NS_URI);
        buf.append("\">");

        for (final ResponseCtx responseCtx : responseCtxs) {
            final Result result = extractSingleResultWithoutObligations(responseCtx);
            final String decision = result.getDecision() == Result.DECISION_PERMIT ? "permit" : "deny";

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
     * @return List, marked with allowed or denied
     * @see de.escidoc.core.aa.service.interfaces.PolicyDecisionPointInterface#evaluateMethodForList(java.lang.String,
     *      java.lang.String, List)
     */
    @Override
    public List<Object[]> evaluateMethodForList(
        final String resourceName, final String methodName, final List<Object[]> argumentList)
        throws ResourceNotFoundException, SystemException {

        // convert the resourceName if provided in triple store format
        final String convertedResourceName = FinderModuleHelper.convertObjectType(resourceName, false);

        final String className = handlerClassNames.get(convertedResourceName.toLowerCase(Locale.ENGLISH));

        final List<Object[]> ret = new ArrayList<Object[]>();

        try {
            final MethodMappingList methodMappings = cache.getMethodMappings(className, methodName);
            for (final Object[] arguments : argumentList) {
                boolean allowed = true;
                final Iterator<MethodMapping> iter = methodMappings.iteratorBefore();
                if (iter != null) {
                    while (allowed && iter.hasNext()) {
                        final List<Map<String, String>> requests =
                            invocationParser.buildRequestsList(arguments, iter.next());
                        final boolean[] accessAllowedArray = evaluateRequestList(requests);
                        for (final boolean anAccessAllowedArray : accessAllowedArray) {
                            if (!anAccessAllowedArray) {
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
            throw new WebserverSystemException("Unhandled exception in evaluateRetrieve. ", e);
        }

        return ret;
    }

    /**
     * See Interface for functional description.
     *
     * @see de.escidoc.core.aa.service.interfaces.PolicyDecisionPointInterface#evaluateRetrieve(java.lang.String, List)
     */
    @Override
    public List<String> evaluateRetrieve(final String resourceName, final List<String> ids)
        throws ResourceNotFoundException, SystemException {

        // convert the resourceName if provided in triple store format
        final String convertedResourceName = FinderModuleHelper.convertObjectType(resourceName, false);

        final String className = handlerClassNames.get(convertedResourceName.toLowerCase(Locale.ENGLISH));

        final List<String> ret = new ArrayList<String>();

        try {
            final MethodMappingList methodMappings = cache.getMethodMappings(className, "retrieve");
            for (final String id : ids) {
                boolean allowed = true;
                final Iterator<MethodMapping> iter = methodMappings.iteratorBefore();
                if (iter != null) {
                    while (allowed && iter.hasNext()) {
                        final List<Map<String, String>> requests = invocationParser.buildRequestsList(id, iter.next());
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
            throw new WebserverSystemException("Unhandled exception in evaluateRetrieve. ", e);
        }

        return ret;
    }

    /**
     * Handles the provided result.<br> This method examines the provided result. In case of an error result, it is
     * checked if a {@code ResourceNotFoundException} has to be thrown or if another error occurred that is
     * reported as a {@code WebserverSystemException}. If no error occurred, either
     *
     * @param result The result of evaluating the request.
     * @return Returns {@code true}, if no error occurred and the provided result contains a permit decision.
     *         Returns {@code false}, if no error occurred and the result does not contain a permit decision.
     * @throws WebserverSystemException  Thrown in case of any error except a resource not found error.
     * @throws ResourceNotFoundException Thrown in case of a resource not found error.
     */
    private static boolean handleResult(final Result result) throws WebserverSystemException, ResourceNotFoundException {

        // any decision other than permit means denial, throw exception
        // From XACML spec: A PEP SHALL allow access to the resource only if a
        // valid XACML response of "Permit" is returned by the PDP. The PEP
        // SHALL deny access to the resource in all other cases.An XACML
        // response of "Permit" SHALL be considered valid only if the PEP
        // understands all of the obligations contained in the response.
        if (result.getDecision() == Result.DECISION_PERMIT) {
            return true;
        }
        else {
            final Status status = result.getStatus();
            final String statusCode = (String) status.getCode().get(0);
            if (!statusCode.equals(Status.STATUS_OK)) {

                if (statusCode.startsWith(AttributeIds.STATUS_PREFIX)) {

                    ResourceNotFoundException e = null;
                    try {
                        final Class clazz = Class.forName(statusCode.substring(AttributeIds.STATUS_PREFIX.length()));
                        if (ResourceNotFoundException.class.isAssignableFrom(clazz)) {

                            final String statusMessage = status.getMessage();
                            e =
                                (ResourceNotFoundException) clazz
                                    .getConstructor(new Class[] { String.class }).newInstance(statusMessage);
                        }
                    }
                    catch (Exception e1) {
                        if (LOGGER.isWarnEnabled()) {
                            LOGGER.warn(StringUtility.format("Error reported during policy evaluation ", result
                                .getResource(), encode(status)));
                        }
                        if (LOGGER.isDebugEnabled()) {
                            LOGGER.debug(StringUtility.format("Error reported during policy evaluation ", result
                                .getResource(), encode(status)), e);
                        }
                        e = null;
                    }
                    if (e != null) {
                        throw e;
                    }
                    else {
                        throw new WebserverSystemException(StringUtility.format(
                            "Error reported during policy evaluation ", result.getResource(), encode(status)));
                    }

                }

                throw new WebserverSystemException(StringUtility.format(
                    "XACML error reported during policy evaluation ", result.getResource(), encode(status)));
            }
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(StringUtility.format("Access not granted. Reason from XACML engine: ",
                    Result.DECISIONS[result.getDecision()]));
            }
            return false;
        }
    }

    /**
     * Encodes the provided XACML status.
     *
     * @param status The XACML status to encode.
     * @return Returns the encoded status.
     * @throws de.escidoc.core.common.exceptions.system.WebserverSystemException
     */
    private static String encode(final Status status) throws WebserverSystemException {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final String ret;
        try {
            status.encode(out, new Indenter());
            ret = out.toString(XmlUtility.CHARACTER_ENCODING);
        }
        catch (UnsupportedEncodingException e) {
            throw new WebserverSystemException("Error on encoding policy.", e);
        }
        finally {
            IOUtils.closeStream(out);
        }
        return ret;
    }

    /**
     * Extracts the expected one and only result without any obligations from the provided xacml response object.
     *
     * @param responseCtx The xacml response object
     * @return Returns the extracted {@link Result} object.
     * @throws WebserverSystemException Thrown if there is not exactly one result in the provided xacml response object,
     *                                  or the result has obligations.
     */
    private static Result extractSingleResultWithoutObligations(final ResponseCtx responseCtx)
        throws WebserverSystemException {

        if (responseCtx.getResults().size() != 1) {
            throw new WebserverSystemException(ERROR_MORE_THAN_ONE_RESULT);
        }
        final Result result = (Result) responseCtx.getResults().iterator().next();
        if (!result.getObligations().isEmpty()) {
            throw new WebserverSystemException(ERROR_OBLIGATIONS_ARE_NOT_SUPPORTED);
        }
        return result;
    }

    /**
     * Helper method to evaluate the xacml requests in the provided xml representation of authorization requests.
     *
     * @param requestsXml The xml representation of authorization requests
     * @return Returns the list of the xacml response objects holding the evaluation results of the provided requests
     *         (in the same order).
     * @throws XmlSchemaValidationException Thrown in case of a schema validation error or a corrupted xml.
     * @throws SystemException              Thrown in case of an internal error.
     */
    private List<ResponseCtx> doEvaluate(final CharSequence requestsXml) throws XmlSchemaValidationException,
        SystemException {

        // trim white spaces and newlines around < and >
        Matcher matcher = TRIM_PATTERN.matcher(requestsXml);
        String xml = matcher.replaceAll("$1");

        // remove all prefixes to enable later parsing of xacml requests
        matcher = PREFIX_PATTERN.matcher(xml);
        xml = matcher.replaceAll("$1");

        matcher = SPLIT_PATTERN.matcher(xml);
        int i = 0;
        final List<RequestCtx> requestCtxs = new ArrayList<RequestCtx>();
        while (matcher.find(i)) {
            try {
                RequestCtx requestCtx =
                    RequestCtx.getInstance(new ByteArrayInputStream(matcher.group(1).getBytes(
                        XmlUtility.CHARACTER_ENCODING)));
                final Set<Attribute> resources = requestCtx.getResource();
                final Set<Attribute> uris = new HashSet<Attribute>(resources.size());
                for (final Attribute attribute : resources) {
                    uris.add(new Attribute(CheckProvidedAttributeFinderModule.getAttributeId(), null, null,
                        new StringAttribute(attribute.getId().toString())));
                }
                for (final Attribute attribute : (Set<Attribute>) requestCtx.getEnvironmentAttributes()) {
                    uris.add(attribute);
                }
                requestCtx =
                    new RequestCtx(requestCtx.getSubjects(), requestCtx.getResource(), requestCtx.getAction(), uris);
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
     * @param requestCtxs The list of xacml request to evaluate.
     * @return Returns the list of the xacml response objects holding the evaluation results of the provided requests
     *         (in the same order).
     * @throws WebserverSystemException Thrown in case of an internal error.
     */
    private List<ResponseCtx> doEvaluate(final List<RequestCtx> requestCtxs) throws WebserverSystemException {

        final Iterator<RequestCtx> iter = requestCtxs.iterator();
        final List<ResponseCtx> responsCtxs = new ArrayList<ResponseCtx>(requestCtxs.size());
        while (iter.hasNext()) {
            responsCtxs.add(doEvaluate(iter.next()));
        }
        return responsCtxs;
    }

    /**
     * Helper method to evaluate the provided xacml request.
     *
     * @param requestCtx The xacml request to evaluate
     * @return Returns the xacml response object holding the evaluation results.
     * @throws WebserverSystemException Thrown in case of an internal error.
     */
    private ResponseCtx doEvaluate(final RequestCtx requestCtx) throws WebserverSystemException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Request: ");
            final ByteArrayOutputStream os = new ByteArrayOutputStream();
            try {
                requestCtx.encode(os, new Indenter());
                LOGGER.debug(os.toString());
            }
            finally {
                IOUtils.closeStream(os);
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

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Response: ");
            final ByteArrayOutputStream os = new ByteArrayOutputStream();
            try {
                response.encode(os, new Indenter());
                LOGGER.debug(os.toString());
            }
            finally {
                IOUtils.closeStream(os);
            }
        }
        return response;
    }

    /**
     * Initializes the handler class name map.
     */
    private void initializeHandlerClassNames() {

        handlerClassNames.put("container", ContainerHandlerInterface.class.getName());
        handlerClassNames.put("context", ContextHandlerInterface.class.getName());
        handlerClassNames.put("item", ItemHandlerInterface.class.getName());
        handlerClassNames.put("content-relation", ContentRelationHandlerInterface.class.getName());
        handlerClassNames.put("content-model", ContentModelHandlerInterface.class.getName());
        handlerClassNames.put("organizational-unit", OrganizationalUnitHandlerInterface.class.getName());
        handlerClassNames.put("user-account", UserAccountHandlerInterface.class.getName());
        handlerClassNames.put("user-group", UserGroupHandlerInterface.class.getName());
        handlerClassNames.put("role", RoleHandlerInterface.class.getName());

        // OAI
        handlerClassNames.put("set-definition", SetDefinitionHandlerInterface.class.getName());

        // Statistic Manager
        handlerClassNames.put("scope", ScopeHandlerInterface.class.getName());
        handlerClassNames.put("aggregation-definition", AggregationDefinitionHandlerInterface.class.getName());
        handlerClassNames.put("report-definition", ReportDefinitionHandlerInterface.class.getName());
    }

    /**
     * @return Returns the customPdp.
     */
    public CustomPdp getCustomPdp() {
        return this.customPdp;
    }

    @Override
    public void touch() {
        // do nothing
    }

}
