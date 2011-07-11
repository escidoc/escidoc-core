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
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Attribute finder module implementation that resolves the hierarchical parents of a new organizational-unit.<br>
 * <p/>
 * Supported Attributes:<br> -info:escidoc/names:aa:1.0:resource:organizational-unit:hierarchical-parents- new<br> the
 * parents of the new organizational-unit (hierarchical), multi value attribute
 *
 * @author Michael Hoppe
 */
@Service("eSciDoc.core.aa.NewOuParentsAttributeFinderModule")
public class NewOuParentsAttributeFinderModule extends AbstractAttributeFinderModule {

    private static final Pattern SPLIT_PATTERN = Pattern.compile("\\s+");

    private static final String ATTR_HIERARCHICAL_PARENTS_NEW =
        AttributeIds.ORGANIZATIONAL_UNIT_ATTR_PREFIX + "hierarchical-parents-new";

    private static final String ATTR_PARENT_NEW = AttributeIds.ORGANIZATIONAL_UNIT_ATTR_PREFIX + "parent-new";

    private static final Pattern PATTERN_VALID_ATTRIBUTE_ID = Pattern.compile(ATTR_HIERARCHICAL_PARENTS_NEW);

    @Autowired
    @Qualifier("eSciDoc.core.aa.TripleStoreAttributeFinderModule")
    private TripleStoreAttributeFinderModule tripleStoreAttributeFinderModule;

    private final MapResult hierarchicalParentMapResult =
        new MapResult(TripleStoreUtility.PROP_PARENT, false, true, true);

    /**
     * See Interface for functional description.
     *
     * @param attributeIdValue      attributeIdValue
     * @param ctx                   ctx
     * @param resourceId            resourceId
     * @param resourceObjid         resourceObjid
     * @param resourceVersionNumber resourceVersionNumber
     * @param designatorType        designatorType
     * @return boolean
     * @throws EscidocException e
     */
    @Override
    protected boolean assertAttribute(
        final String attributeIdValue, final EvaluationCtx ctx, final String resourceId, final String resourceObjid,
        final String resourceVersionNumber, final int designatorType) throws EscidocException {

        if (!super.assertAttribute(attributeIdValue, ctx, resourceId, resourceObjid, resourceVersionNumber,
            designatorType)) {

            return false;
        }

        return PATTERN_VALID_ATTRIBUTE_ID.matcher(attributeIdValue).find();

    }

    /**
     * See Interface for functional description.
     *
     * @param attributeIdValue      attributeIdValue
     * @param ctx                   ctx
     * @param resourceId            resourceId
     * @param resourceObjid         resourceObjid
     * @param resourceVersionNumber resourceVersionNumber
     * @return Object[]
     */
    @Override
    protected Object[] resolveLocalPart(
        final String attributeIdValue, final EvaluationCtx ctx, final String resourceId, final String resourceObjid,
        final String resourceVersionNumber) throws SystemException {

        try {
            if (attributeIdValue.equals(ATTR_HIERARCHICAL_PARENTS_NEW)) {
                final Iterable<String> parentIds =
                    new ArrayList<String>(FinderModuleHelper.retrieveMultiResourceAttribute(ctx, new URI(
                        ATTR_PARENT_NEW), false));
                final List<String> expandedParentIds = new ArrayList<String>();
                for (final String parentId : parentIds) {
                    final String[] expandedParentArr = SPLIT_PATTERN.split(parentId);
                    if (expandedParentArr != null) {
                        expandedParentIds.addAll(Arrays.asList(expandedParentArr));
                    }
                }
                if (expandedParentIds.isEmpty()) {
                    return null;
                }
                List<String> cachedAttribute = new ArrayList<String>();
                if (hierarchicalParentMapResult.isIncludeHierarchyBase()) {
                    cachedAttribute.addAll(expandedParentIds);
                }
                cachedAttribute =
                    tripleStoreAttributeFinderModule.getHierarchicalCachedAttributes(expandedParentIds,
                        cachedAttribute, this.hierarchicalParentMapResult);

                if (StringUtils.isNotEmpty(resourceId)) {
                    cachedAttribute.add(resourceId);
                }
                final List<StringAttribute> stringAttributes = new ArrayList<StringAttribute>();

                for (final String stringAttribute : cachedAttribute) {
                    stringAttributes.add(new StringAttribute(stringAttribute));
                }

                final EvaluationResult result =
                    new EvaluationResult(new BagAttribute(Constants.URI_XMLSCHEMA_STRING, stringAttributes));

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
}
