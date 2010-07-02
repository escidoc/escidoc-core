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

import java.util.regex.Pattern;

import com.sun.xacml.EvaluationCtx;
import com.sun.xacml.cond.EvaluationResult;

import de.escidoc.core.aa.business.authorisation.CustomEvaluationResultBuilder;
import de.escidoc.core.common.business.LockHandler;
import de.escidoc.core.common.business.aa.authorisation.AttributeIds;
import de.escidoc.core.common.exceptions.EscidocException;
import de.escidoc.core.common.util.logger.AppLogger;
import de.escidoc.core.common.util.string.StringUtility;

/**
 * Implementation of an XACML attribute finder module that is responsible for
 * the lock-owner attribute.<br>
 * 
 * Supported Attributes:<br>
 * -info:escidoc/names:aa:1.0:resource:container:lock-owner<br>
 *  the id of the user who locked the container, single value attribute
 * -info:escidoc/names:aa:1.0:resource:item:lock-owner<br>
 *  the id of the user who locked the item, single value attribute
 * 
 * @spring.bean id="eSciDoc.core.aa.LockOwnerAttributeFinderModule"
 * 
 * @author MIH
 * 
 * @aa
 */
public class LockOwnerAttributeFinderModule
    extends AbstractAttributeFinderModule {

    /**
     * The logger.
     */
    private static final AppLogger LOG =
        new AppLogger(LockOwnerAttributeFinderModule.class.getName());

    private static final Pattern PATTERN_VALID_ATTRIBUTE_ID =
        Pattern.compile(StringUtility.concatenateToString(
            AttributeIds.CONTAINER_ATTR_PREFIX, "lock-owner|",
            AttributeIds.ITEM_ATTR_PREFIX, "lock-owner"));

    private LockHandler lockHandler;

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

        if (!super.assertAttribute(attributeIdValue, ctx, resourceId,
            resourceObjid, resourceVersionNumber, designatorType)) {

            return false;
        }

        // make sure attribute is in escidoc-internal format for lock-owner
        if (!PATTERN_VALID_ATTRIBUTE_ID.matcher(attributeIdValue).find()) {
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

        String lockOwner = lockHandler.getLockOwner(resourceId);
        result =
            CustomEvaluationResultBuilder
                .createSingleStringValueResult(lockOwner);
        return new Object[] { result, attributeIdValue };

    }

    /**
     * Injects the lock handler if "called" via Spring.
     * 
     * @param lockHandler
     *            The lock handler.
     * @spring.property ref="business.LockHandler"
     */
    public void setLockHandler(final LockHandler lockHandler) {

        LOG.debug("setLockHandler");
        this.lockHandler = lockHandler;
    }

}
