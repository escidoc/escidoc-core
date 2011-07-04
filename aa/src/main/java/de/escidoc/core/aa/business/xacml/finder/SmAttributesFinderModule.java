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

import com.sun.xacml.EvaluationCtx;
import com.sun.xacml.cond.EvaluationResult;
import de.escidoc.core.aa.business.authorisation.CustomEvaluationResultBuilder;
import de.escidoc.core.common.business.aa.authorisation.AttributeIds;
import de.escidoc.core.common.exceptions.EscidocException;
import de.escidoc.core.common.exceptions.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.application.notfound.AggregationDefinitionNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ReportDefinitionNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ScopeNotFoundException;
import de.escidoc.core.common.exceptions.application.security.SecurityException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.util.string.StringUtility;
import de.escidoc.core.common.util.xml.XmlUtility;
import de.escidoc.core.sm.service.interfaces.AggregationDefinitionHandlerInterface;
import de.escidoc.core.sm.service.interfaces.ReportDefinitionHandlerInterface;
import de.escidoc.core.sm.service.interfaces.ScopeHandlerInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Implementation of an XACML attribute finder module that is responsible for the attributes related to the resources of
 * the statistics manager.<br> As statistics data are not protected, these are the resources aggregation definition,
 * report definition and report. This finder module supports XACML resource attributes.<br> The attribute values are
 * fetched from the xml representations.
 * <p/>
 * Supported Attributes:<br> -info:escidoc/names:aa:1.0:resource:aggregation-definition:scope<br> the id of the scope of
 * the aggregation-definition, single value attribute -info:escidoc/names:aa:1.0:resource:report-definition:scope<br>
 * the id of the scope of the report-definition, single value attribute -info:escidoc/names:aa:1.0:resource:report-definition:allowed-roles:allowed-role<br>
 * the id of the role that is allowed to execute the report-definition, single value attribute containing roleIds
 * whitespace-separated -info:escidoc/names:aa:1.0:resource:report:scope<br> the id of the scope of the report, single
 * value attribute -info:escidoc/names:aa:1.0:resource:scope-id<br> the id of the scope, single value attribute
 *
 * @author Torsten Tetteroo
 */
@Service("eSciDoc.core.aa.SmAttributesFinderModule")
public class SmAttributesFinderModule extends AbstractAttributeFinderModule {

    private static final String ATTR_SCOPE = "scope";

    // private static final String ATTR_CREATED_BY = "created-by";
    //
    // private static final String ATTR_MODIFIED_BY = "modified-by";

    private static final String ATTR_ALLOWED_ROLE = "allowed-roles:allowed-role";

    private static final String RESOLVABLE_SM_ATTRS = ATTR_SCOPE + '|' + ATTR_ALLOWED_ROLE;

    private static final String VALID_SM_ATTRIBUTE_PREFIXES =
        AttributeIds.RESOURCE_AGGREGATION_DEFINITION_ATTR_PREFIX + ".*|" + AttributeIds.REPORT_DEFINITION_ATTR_PREFIX
            + ".*|" + AttributeIds.REPORT_ATTR_PREFIX + ".*";

    private static final Pattern PATTERN_PARSE_SM_ATTRIBUTE_ID =
        Pattern.compile("((" + VALID_SM_ATTRIBUTE_PREFIXES + ")(" + RESOLVABLE_SM_ATTRS + "))(-new){0,1}(:(.*)){0,1}"
            + '|' + AttributeIds.URN_STATISTIC_SCOPE_ID);

    private static final Pattern PATTERN_VALID_ATTRIBUTE_ID = Pattern.compile(VALID_SM_ATTRIBUTE_PREFIXES);

    private static final Pattern SCOPE_PATTERN =
        Pattern.compile(".*<[^>]*?scope[^>]*?objid=\"(.*?)\".*" + "|.*<[^>]*?scope[^>]*?href=\"[^\"]*/(.*?)\".*",
            Pattern.DOTALL + Pattern.MULTILINE);

    // private static final Pattern CREATED_BY_PATTERN =
    // Pattern.compile(StringUtility.concatenateToString(
    // ".*<[^>]*?created-by[^>]*?objid=\"(.*?)\".*",
    // "|.*<[^>]*?created-by[^>]*?href=\"[^\"]*/(.*?)\".*"),
    // Pattern.DOTALL
    // + Pattern.MULTILINE);
    //
    // private static final Pattern MODIFIED_BY_PATTERN =
    // Pattern.compile(StringUtility.concatenateToString(
    // ".*<[^>]*?modified-by[^>]*?objid=\"(.*?)\".*",
    // "|.*<[^>]*?modified-by[^>]*?href=\"[^\"]*/(.*?)\".*"),
    // Pattern.DOTALL
    // + Pattern.MULTILINE);

    private static final Pattern ALLOWED_ROLE_PATTERN =
        Pattern.compile("<[^>]*?allowed-role[^>]*?objid=\"(.*?)\"" + "|<[^>]*?allowed-role[^>]*?href=\"[^\"]*/(.*?)\"",
            Pattern.DOTALL + Pattern.MULTILINE);

    @Autowired
    @Qualifier("service.AggregationDefinitionHandler")
    private AggregationDefinitionHandlerInterface aggregationDefinitionHandler;

    @Autowired
    @Qualifier("service.ReportDefinitionHandler")
    private ReportDefinitionHandlerInterface reportDefinitionHandler;

    @Autowired
    @Qualifier("service.ScopeHandler")
    private ScopeHandlerInterface scopeHandler;

    /**
     * See Interface for functional description.
     */
    @Override
    protected boolean assertAttribute(
        final String attributeIdValue, final EvaluationCtx ctx, final String resourceId, final String resourceObjid,
        final String resourceVersionNumber, final int designatorType) throws EscidocException {

        if (!super.assertAttribute(attributeIdValue, ctx, resourceId, resourceObjid, resourceVersionNumber,
            designatorType)) {

            return false;
        }

        // make sure attribute is in escidoc-internal format for aggregation
        // definition or report definition or scope
        return PATTERN_VALID_ATTRIBUTE_ID.matcher(attributeIdValue).find();

    }

    /**
     * See Interface for functional description.
     */
    @Override
    protected Object[] resolveLocalPart(
        final String attributeIdValue, final EvaluationCtx ctx, final String resourceId, final String resourceObjid,
        final String resourceVersionNumber) throws AggregationDefinitionNotFoundException, ScopeNotFoundException,
        WebserverSystemException, ReportDefinitionNotFoundException {

        final EvaluationResult result;
        String resolvedAttributeIdValue;

        final Matcher smAttributeMatcher = PATTERN_PARSE_SM_ATTRIBUTE_ID.matcher(attributeIdValue);
        if (smAttributeMatcher.find()) {
            resolvedAttributeIdValue = smAttributeMatcher.group(1);
            String attributePrefix = smAttributeMatcher.group(2);
            if (resolvedAttributeIdValue == null && attributePrefix == null) {
                resolvedAttributeIdValue = smAttributeMatcher.group(0);
                attributePrefix = smAttributeMatcher.group(0);
            }
            final String attributeId = smAttributeMatcher.group(3);

            final String resourceXml;
            if (attributePrefix.equals(AttributeIds.RESOURCE_AGGREGATION_DEFINITION_ATTR_PREFIX)) {
                resourceXml = retrieveAggregationDefinition(ctx, resourceId);
                result = evaluateResult(resourceXml, resolvedAttributeIdValue, attributeId);
            }
            else if (attributePrefix.equals(AttributeIds.REPORT_DEFINITION_ATTR_PREFIX)
                || attributePrefix.equals(AttributeIds.REPORT_ATTR_PREFIX)) {
                resourceXml = retrieveReportDefinition(ctx, resourceId);
                result = evaluateResult(resourceXml, resolvedAttributeIdValue, attributeId);

            }
            else if (attributePrefix.equals(AttributeIds.URN_STATISTIC_SCOPE_ID)) {
                retrieveScope(ctx, resourceId);
                result = CustomEvaluationResultBuilder.createSingleStringValueResult(resourceId);
            }
            else {
                return null;
            }
            if (result == null) {
                return null;
            }
        }
        else {
            return null;
        }

        return new Object[] { result, resolvedAttributeIdValue };
    }

    /**
     * Evaluate an attribute from an resource-xml.
     *
     * @param resourceXml         xml of resource
     * @param resolvableAttribute The attribute to resolve.
     * @param attributeId         The id of the attribute.
     * @return EvaluationResult result.
     */
    private static EvaluationResult evaluateResult(
        final CharSequence resourceXml, final String resolvableAttribute, final String attributeId) {
        EvaluationResult result = null;

        if (ATTR_SCOPE.equals(attributeId)) {
            final Matcher matcher = SCOPE_PATTERN.matcher(resourceXml);
            if (matcher.find()) {
                String objId = matcher.group(1);
                if (objId == null) {
                    objId = matcher.group(2);
                }
                result = CustomEvaluationResultBuilder.createSingleStringValueResult(objId.trim());
            }
            // } else if (ATTR_CREATED_BY.equals(attributeId)) {
            // Matcher matcher =
            // CREATED_BY_PATTERN.matcher(resourceXml);
            // if (matcher.find()) {
            // String objId = matcher.group(1);
            // if (objId == null) {
            // objId = matcher.group(2);
            // }
            // result =
            // CustomEvaluationResultBuilder
            // .createSingleStringValueResult(objId.trim());
            // }
            // } else if (ATTR_MODIFIED_BY.equals(attributeId)) {
            // Matcher matcher =
            // MODIFIED_BY_PATTERN.matcher(resourceXml);
            // if (matcher.find()) {
            // String objId = matcher.group(1);
            // if (objId == null) {
            // objId = matcher.group(2);
            // }
            // result =
            // CustomEvaluationResultBuilder
            // .createSingleStringValueResult(objId.trim());
            // }
        }
        else if (ATTR_ALLOWED_ROLE.equals(attributeId)) {
            final Matcher matcher = ALLOWED_ROLE_PATTERN.matcher(resourceXml);
            final StringBuilder roles = new StringBuilder("");
            while (matcher.find()) {
                String roleId = matcher.group(1);
                if (roleId == null) {
                    roleId = matcher.group(2);
                }
                roles.append(' ').append(roleId.trim());
            }
            result = CustomEvaluationResultBuilder.createSingleStringValueResult(roles.toString());
        }
        else {
            return null;
        }
        return result;
    }

    /**
     * Retrieve XML representation of an aggregation definition from the system.
     *
     * @param ctx                     The evaluation context, which will be used as key for the cache.
     * @param aggregationDefinitionId The aggregation definition id.
     * @return Returns the XML representation of the {@code AggregationDefinition} identified by the provided id.
     * @throws WebserverSystemException Thrown in case of an internal error.
     * @throws AggregationDefinitionNotFoundException
     *                                  Thrown if no aggregation definition with provided id exists.
     */
    private String retrieveAggregationDefinition(final EvaluationCtx ctx, final String aggregationDefinitionId)
        throws WebserverSystemException, AggregationDefinitionNotFoundException {

        final StringBuilder key = new StringBuilder(XmlUtility.NAME_ID).append(aggregationDefinitionId);
        String aggregationDefinitionXml =
            (String) getFromCache(XmlUtility.NAME_AGGREGATION_DEFINITION, null, null, key.toString(), ctx);
        if (aggregationDefinitionXml == null) {
            try {
                aggregationDefinitionXml = aggregationDefinitionHandler.retrieve(aggregationDefinitionId);
                if (aggregationDefinitionXml == null) {
                    throw new AggregationDefinitionNotFoundException(StringUtility.format(
                        "Aggregation definition not found", aggregationDefinitionId));
                }
                putInCache(XmlUtility.NAME_AGGREGATION_DEFINITION, null, null, key.toString(), ctx,
                    aggregationDefinitionXml);
            }
            catch (final MissingMethodParameterException e) {
                throw new WebserverSystemException(StringUtility.format(
                    "Exception during aggregation definition retrieval", e.getMessage()), e);
            }
            catch (final SecurityException e) {
                throw new WebserverSystemException("Security exception during "
                    + "AggregationDefinitionHandler.retrieve call.", e);
            }
            catch (final SystemException e) {
                throw new WebserverSystemException(StringUtility.format(
                    "Exception during aggregation definition retrieval", e.getMessage()), e);
            }
        }

        return aggregationDefinitionXml;
    }

    /**
     * Retrieve XML representation of a report definition from the system.
     *
     * @param ctx                The evaluation context, which will be used as key for the cache.
     * @param reportDefinitionId The aggregation definition id.
     * @return Returns the XML representation of the {@code ReportDefinition} identified by the provided id.
     * @throws WebserverSystemException Thrown in case of an internal error.
     * @throws ReportDefinitionNotFoundException
     *                                  Thrown if no report definition with provided id exists.
     */
    private String retrieveReportDefinition(final EvaluationCtx ctx, final String reportDefinitionId)
        throws WebserverSystemException, ReportDefinitionNotFoundException {

        final StringBuilder key = new StringBuilder(XmlUtility.NAME_ID).append(reportDefinitionId);
        String reportDefinitionXml =
            (String) getFromCache(XmlUtility.NAME_REPORT_DEFINITION, null, null, key.toString(), ctx);
        if (reportDefinitionXml == null) {
            try {
                reportDefinitionXml = reportDefinitionHandler.retrieve(reportDefinitionId);
                if (reportDefinitionXml == null) {
                    throw new ReportDefinitionNotFoundException(StringUtility.format("Report definition not found",
                        reportDefinitionId));
                }
                putInCache(XmlUtility.NAME_REPORT_DEFINITION, null, null, key.toString(), ctx, reportDefinitionXml);
            }
            catch (final MissingMethodParameterException e) {
                throw new WebserverSystemException(StringUtility.format("Exception during report definition retrieval",
                    e.getMessage()), e);
            }
            catch (final SecurityException e) {
                throw new WebserverSystemException("Security exception during "
                    + "ReportDefinitionHandler.retrieve call.", e);
            }
            catch (final SystemException e) {
                throw new WebserverSystemException(StringUtility.format("Exception during report definition retrieval",
                    e.getMessage()), e);
            }
        }

        return reportDefinitionXml;
    }

    /**
     * Retrieve Scope from the system.
     *
     * @param ctx     The evaluation context, which will be used as key for the cache.
     * @param scopeId The container id.
     * @return Returns the Xml representation of the scope identified by the provided id.
     * @throws WebserverSystemException Thrown in case of an internal error.
     * @throws ScopeNotFoundException   Thrown if no scope with provided id exists.
     */
    private void retrieveScope(final EvaluationCtx ctx, final String scopeId) throws WebserverSystemException,
        ScopeNotFoundException {

        final StringBuilder key = new StringBuilder(XmlUtility.NAME_ID).append(scopeId);
        String scopeXml = (String) getFromCache(XmlUtility.NAME_SCOPE, null, null, key.toString(), ctx);
        if (scopeXml == null) {
            try {
                scopeXml = scopeHandler.retrieve(scopeId);
                if (scopeXml == null) {
                    throw new ScopeNotFoundException(StringUtility.format("Scope not found", scopeId));
                }
                putInCache(XmlUtility.NAME_SCOPE, null, null, key.toString(), ctx, scopeXml);
            }
            catch (final MissingMethodParameterException e) {
                throw new WebserverSystemException(StringUtility.format("Exception during retrieval of the scope", e
                    .getMessage()), e);
            }
            catch (final SecurityException e) {
                throw new WebserverSystemException("Security exception during ScopeHandler.retrieve call.", e);
            }
            catch (final SystemException e) {
                throw new WebserverSystemException(StringUtility.format("Exception during retrieval of the scope", e
                    .getMessage()), e);
            }
        }
    }

}
