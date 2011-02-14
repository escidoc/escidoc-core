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
import de.escidoc.core.aa.business.ObjectAttributeResolver;
import de.escidoc.core.aa.business.authorisation.CustomEvaluationResultBuilder;
import de.escidoc.core.aa.business.authorisation.FinderModuleHelper;
import de.escidoc.core.aa.business.cache.RequestAttributesCache;
import de.escidoc.core.common.business.aa.authorisation.AttributeIds;
import de.escidoc.core.common.exceptions.EscidocException;
import de.escidoc.core.common.exceptions.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.application.notfound.ResourceNotFoundException;
import de.escidoc.core.common.exceptions.application.security.AuthenticationException;
import de.escidoc.core.common.exceptions.application.security.AuthorizationException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.util.logger.AppLogger;
import de.escidoc.core.common.util.string.StringUtility;

/**
 * Attribute finder module implementation that resolves the object type of the
 * resource addressed by the resource-id.<br>
 * If no resource-id is set, it is tried to resolve the attribute
 * object-type-new. If this fails, an exception is thrown.
 * 
 * Supported Attributes:<br>
 * -info:escidoc/names:aa:1.0:resource:object-type<br>
 *  the object-type of the resource, single value attribute
 * 
 * @spring.bean id="eSciDoc.core.aa.ObjectTypeAttributeFinderModule"
 * 
 * @author TTE
 * @aa
 */
public class ObjectTypeAttributeFinderModule
    extends AbstractAttributeFinderModule {

    private final AppLogger log =
        new AppLogger(ObjectTypeAttributeFinderModule.class.getName());

    private ObjectAttributeResolver objectAttributeResolver;

    /**
     * See Interface for functional description.
     * 
     * @param attributeIdValue attributeIdValue
     * @param ctx ctx
     * @param resourceId resourceId
     * @param resourceObjid resourceObjid
     * @param resourceVersionNumber resourceVersionNumber
     * @param designatorType designatorType
     * @return boolean
     * @throws EscidocException e
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

        return super.assertAttribute(attributeIdValue, ctx, resourceId,
            resourceObjid, resourceVersionNumber, designatorType)
            && AttributeIds.URN_OBJECT_TYPE.equals(attributeIdValue);
    }

    /**
     * See Interface for functional description.
     * 
     * @param attributeIdValue attributeIdValue
     * @param ctx ctx
     * @param resourceId resourceId
     * @param resourceObjid resourceObjid
     * @param resourceVersionNumber resourceVersionNumber
     * @return Object[]
     * @throws EscidocException e
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

        if (FinderModuleHelper.isNewResourceId(resourceId)) {
            // This is the case if a new resource shall be created and no
            // resource-id can exists. In this case, object-type can not exist,
            // but object-type-new should be specified in case of an access to
            // a resource.
            return resolveObjectTypeNew(attributeIdValue, ctx);
        }
        else {
            return resolveObjectType(attributeIdValue, resourceId,
                resourceObjid);
        }
    }

    /**
     * Resolves the object type in the case of an existing resource.
     * 
     * @param attributeIdValue
     *            The attribute id. This is used for caching.
     * @param resourceId
     *            The id of the resource.
     * @param resourceObjid
     *            The objid of the resource.
     * @return Returns an object array containing the {@link EvaluationResult}
     *         and the attribute id value.
     * @throws SystemException e
     * @throws MissingMethodParameterException e
     * @throws AuthenticationException e
     * @throws AuthorizationException e
     * @throws ResourceNotFoundException e
     */
    private Object[] resolveObjectType(
        final String attributeIdValue, final String resourceId,
        final String resourceObjid) throws SystemException,
        MissingMethodParameterException, AuthenticationException,
        AuthorizationException, ResourceNotFoundException {

        EvaluationResult result = null;
        String objectType = 
            objectAttributeResolver.resolveObjectType(resourceObjid);
        if (objectType != null) {
            result = CustomEvaluationResultBuilder
                .createSingleStringValueResult(objectType);
        }

        // if no object-type could be determined, throw a resource not found
        // exception.
        if (result == null) {
            String msg = StringUtility
            .format("Resource not found", resourceId)
            .toString();
            log.debug(msg);
            throw new ResourceNotFoundException(msg);
        }

        return new Object[] { result, attributeIdValue };
    }

    /**
     * Resolves the object-type-new attribute which is used as the value of
     * object-type in the case of a new resource that shall be created and no
     * object-type can exists.
     * 
     * @param attributeIdValue
     *            The attribute id. Used for caching
     * @param ctx
     *            The current evaluation context. Used for caching
     * @return Returns an object array containing the {@link EvaluationResult}
     *         and the attribute id.
     * @throws ResourceNotFoundException e
     */
    private Object[] resolveObjectTypeNew(
        final String attributeIdValue, final EvaluationCtx ctx)
        throws ResourceNotFoundException {

        EvaluationResult result =
            (EvaluationResult) RequestAttributesCache.get(ctx,
                AttributeIds.URN_OBJECT_TYPE_NEW);
        if (result != null) {
            return new Object[] { result, attributeIdValue };
        }

        String objectType;
        try {
            objectType =
                fetchSingleResourceAttribute(ctx,
                    AttributeIds.URN_OBJECT_TYPE_NEW);
        }
        catch (WebserverSystemException e) {
            // This can happen due to an internal error or because the
            // object-type-new attribute has not been provided, e.g. in case
            // of a task like logout or evaluate where neither resource-id
            // nor object-type-new exist
            return null;
        }
        result =
            CustomEvaluationResultBuilder
                .createSingleStringValueResult(objectType);
        RequestAttributesCache.put(ctx, AttributeIds.URN_OBJECT_TYPE_NEW,
            result);
        return new Object[] { result, attributeIdValue };
    }

    /**
     * Injects the object attribute resolver.
     * 
     * @param objectAttributeResolver
     *            The objectAttributeResolver.
     * 
     * @spring.property ref="eSciDoc.core.aa.ObjectAttributeResolver"
     */
    public void setObjectAttributeResolver(
            final ObjectAttributeResolver objectAttributeResolver) {
        this.objectAttributeResolver = objectAttributeResolver;
    }

}
