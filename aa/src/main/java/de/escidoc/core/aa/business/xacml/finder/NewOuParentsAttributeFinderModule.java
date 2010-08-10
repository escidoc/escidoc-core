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
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import com.sun.xacml.EvaluationCtx;
import com.sun.xacml.attr.BagAttribute;
import com.sun.xacml.attr.StringAttribute;
import com.sun.xacml.cond.EvaluationResult;

import de.escidoc.core.aa.business.authorisation.Constants;
import de.escidoc.core.aa.business.authorisation.FinderModuleHelper;
import de.escidoc.core.aa.business.xacml.util.MapResult;
import de.escidoc.core.common.business.aa.authorisation.AttributeIds;
import de.escidoc.core.common.business.fedora.TripleStoreUtility;
import de.escidoc.core.common.exceptions.EscidocException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.util.logger.AppLogger;

/**
 * Attribute finder module implementation that resolves the hierarchical parents
 * of a new organizational-unit.<br>
 * 
 * Supported Attributes:<br>
 * -info:escidoc/names:aa:1.0:resource:organizational-unit:hierarchical-parents-
 * new<br>
 * the parents of the new organizational-unit (hierarchical), multi value
 * attribute
 * 
 * @spring.bean id="eSciDoc.core.aa.NewOuParentsAttributeFinderModule"
 * 
 * @author MIH
 * @aa
 */
public class NewOuParentsAttributeFinderModule
    extends AbstractAttributeFinderModule {

    private final AppLogger log = new AppLogger(
        NewOuParentsAttributeFinderModule.class.getName());

    private static final String ATTR_HIERARCHICAL_PARENTS_NEW =
        AttributeIds.ORGANIZATIONAL_UNIT_ATTR_PREFIX
            + "hierarchical-parents-new";

    private static final String ATTR_PARENT_NEW =
        AttributeIds.ORGANIZATIONAL_UNIT_ATTR_PREFIX + "parent-new";

    private static final Pattern PATTERN_VALID_ATTRIBUTE_ID = Pattern
        .compile(ATTR_HIERARCHICAL_PARENTS_NEW);

    private TripleStoreAttributeFinderModule tripleStoreAttributeFinderModule;

    private final MapResult hierarchicalParentMapResult = new MapResult(
        TripleStoreUtility.PROP_PARENT, false, true, true);

    /**
     * See Interface for functional description.
     * 
     * @param attributeIdValue
     *            attributeIdValue
     * @param ctx
     *            ctx
     * @param resourceId
     *            resourceId
     * @param resourceObjid
     *            resourceObjid
     * @param resourceVersionNumber
     *            resourceVersionNumber
     * @param designatorType
     *            designatorType
     * @return boolean
     * @throws EscidocException
     *             e
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

        if (!super.assertAttribute(attributeIdValue, ctx, resourceId,
            resourceObjid, resourceVersionNumber, designatorType)) {

            return false;
        }

        if (!PATTERN_VALID_ATTRIBUTE_ID.matcher(attributeIdValue).find()) {
            return false;
        }

        return true;
    }

    /**
     * See Interface for functional description.
     * 
     * @param attributeIdValue
     *            attributeIdValue
     * @param ctx
     *            ctx
     * @param resourceId
     *            resourceId
     * @param resourceObjid
     *            resourceObjid
     * @param resourceVersionNumber
     *            resourceVersionNumber
     * @return Object[]
     * @throws EscidocException
     *             e
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

        try {
            EvaluationResult result;
            if (attributeIdValue.equals(ATTR_HIERARCHICAL_PARENTS_NEW)) {
                List<String> parentIds =
                    new ArrayList<String>(
                        FinderModuleHelper.retrieveMultiResourceAttribute(ctx,
                            new URI(ATTR_PARENT_NEW), false));
                List<String> expandedParentIds = new ArrayList<String>();
                if (parentIds != null) {
                    for (String parentId : parentIds) {
                        String[] expandedParentArr = parentId.split("\\s+");
                        if (expandedParentArr != null) {
                            for (int i = 0; i < expandedParentArr.length; i++) {
                                expandedParentIds.add(expandedParentArr[i]);
                            }
                        }
                    }
                }
                else {
                    return null;
                }
                if (expandedParentIds.isEmpty()) {
                    return null;
                }
                List<String> cachedAttribute = new ArrayList<String>();
                if (hierarchicalParentMapResult.isIncludeHierarchyBase()) {
                    cachedAttribute.addAll(expandedParentIds);
                }
                cachedAttribute =
                    tripleStoreAttributeFinderModule
                        .getHierarchicalCachedAttributes(expandedParentIds,
                            cachedAttribute, hierarchicalParentMapResult);

                if (StringUtils.isNotEmpty(resourceId)) {
                    cachedAttribute.add(resourceId);
                }
                List<StringAttribute> stringAttributes =
                    new ArrayList<StringAttribute>();

                for (String stringAttribute : cachedAttribute) {
                    stringAttributes.add(new StringAttribute(stringAttribute));
                }

                result =
                    new EvaluationResult(new BagAttribute(
                        Constants.URI_XMLSCHEMA_STRING, stringAttributes));

                return new Object[] { result, attributeIdValue };
            }
            else {
                return null;
            }
        }
        catch (final Exception e) {
            throw new SystemException(e);
        }

    }

    /**
     * Injects the triple store utility bean.
     * 
     * @param tripleStoreAttributeFinderModule
     *            The {@link TripleStoreAttributeFinderModule}.
     * @spring.property ref="eSciDoc.core.aa.TripleStoreAttributeFinderModule"
     * @aa
     */
    public void setTripleStoreAttributeFinderModule(
        final TripleStoreAttributeFinderModule tripleStoreAttributeFinderModule) {
        this.tripleStoreAttributeFinderModule =
            tripleStoreAttributeFinderModule;
    }
}
