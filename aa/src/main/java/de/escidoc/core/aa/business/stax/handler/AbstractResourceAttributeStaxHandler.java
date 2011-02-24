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
package de.escidoc.core.aa.business.stax.handler;

import com.sun.xacml.EvaluationCtx;
import com.sun.xacml.attr.BagAttribute;
import com.sun.xacml.attr.StringAttribute;
import com.sun.xacml.cond.EvaluationResult;
import de.escidoc.core.aa.business.authorisation.Constants;
import de.escidoc.core.aa.business.authorisation.CustomEvaluationResultBuilder;
import de.escidoc.core.aa.business.cache.RequestAttributesCache;
import de.escidoc.core.common.util.string.StringUtility;
import de.escidoc.core.common.util.xml.XmlUtility;
import de.escidoc.core.common.util.xml.stax.events.EndElement;
import de.escidoc.core.common.util.xml.stax.events.StartElement;
import de.escidoc.core.common.util.xml.stax.handler.DefaultHandler;

import java.util.Collection;

/**
 * Abstract stax handler that handles the attributes that have to be fetched
 * from a resource Xml representation.<br>
 * 
 * @author TTE
 * 
 */
public class AbstractResourceAttributeStaxHandler extends DefaultHandler {

    private final EvaluationCtx ctx;

    private final String resourceId;

    private final String urnModifiedBy;

    private final String urnStatus;

    private final String urnVersionStatus;

    private boolean statusFound = false;
    
    private boolean inMetadata = false;
    
    /**
     * The constructor.
     * 
     * @param ctx
     *            The <code>EvaluationCtx</code> for that the item xml
     *            representation shall be parsed. Found attributes are cached
     *            using this context as part of the key.
     * @param resourceId
     *            The id of the item resource. Used as part of the cache key.
     * @param urnModifiedBy
     *            The urn of the modified-by attribute.
     * @param urnStatus
     *            The urn of the status attribute.
     * @param urnVersionStatus
     *            The urn of the version-status attribute.
     * @aa
     */
    public AbstractResourceAttributeStaxHandler(final EvaluationCtx ctx,
        final String resourceId, final String urnModifiedBy,
        final String urnStatus, final String urnVersionStatus) {

        this.ctx = ctx;
        this.resourceId = resourceId;
        this.urnModifiedBy = urnModifiedBy;
        this.urnStatus = urnStatus;
        this.urnVersionStatus = urnVersionStatus;
    }



    /**
     * See Interface for functional description.
     * 
     * @param element
     * @return
     * @throws Exception
     * @see de.escidoc.core.common.util.xml.stax.handler.DefaultHandler
     *      #startElement
     *      (de.escidoc.core.common.util.xml.stax.events.StartElement)
     * @aa
     */
    @Override
    public StartElement startElement(final StartElement element)
        throws Exception {
        
        //Only parse elements that are not in metadata-section
        if (XmlUtility.NAME_MDRECORDS.equals(element.getLocalName())) {
            setInMetadata(true);
        }

        if (isNotReady() && !isInMetadata()) {
            final String localName = element.getLocalName();
            if (XmlUtility.NAME_MODIFIED_BY.equals(localName)) {
                cacheAttribute(urnModifiedBy, XmlUtility
                    .getIdFromStartElement(element));
            }
        }

        return element;
    }

    /**
     * This method handles an end element.<br>
     * Check if this element is the mdrecords end element.
     * 
     * @param element
     *            The {@link StartElementStartElement} to handle.
     * @return Returns an end element that shall be handled by further handlers
     *         in the chain, or <code>null</code> to stop the chain.
     * @throws Exception
     *             Thrown if anything fails. This depends on the implementation
     *             of the concrete class.
     * @aa
     */
    @Override
    public EndElement endElement(final EndElement element) throws Exception {

        //Only parse elements that are not in metadata-section
        if (XmlUtility.NAME_MDRECORDS.equals(element.getLocalName())) {
            setInMetadata(false);
        }
        
        return element;
    }

    /**
     * See Interface for functional description.
     * 
     * @param data
     * @param element
     * @return
     * @throws Exception
     * @see de.escidoc.core.common.util.xml.stax.handler.DefaultHandler
     *      #characters(java.lang.String,
     *      de.escidoc.core.common.util.xml.stax.events.StartElement)
     * @aa
     */
    @Override
    public String characters(final String data, final StartElement element)
        throws Exception {

        if (isNotReady() && !isInMetadata()) {
            final String localName = element.getLocalName();
            if (!statusFound && XmlUtility.NAME_PUBLIC_STATUS.equals(localName)) {
                cacheAttribute(urnStatus, data);
                // in an item representation, status is contained in item
                // properties and in the component properties. We have to mark
                // the found item status to prevent overriding with component
                // status.
                statusFound = true;
            }
            else if (XmlUtility.NAME_STATUS.equals(localName)) {
                cacheAttribute(urnVersionStatus, data);
            }
        }

        return data;
    }



    /**
     * Creates and caches the result for the provided attribute values using the
     * evaluation context, the resource id and the provided attribute id as the
     * cache key.
     * 
     * @param attributeId
     *            The attribute id.
     * @param attributeValues
     *            The attribute values.
     * @aa
     */
    protected final void cacheAttribute(
            final String attributeId,
            final Collection<StringAttribute> attributeValues) {

        cacheAttribute(ctx, resourceId, attributeId, attributeValues);
    }

    /**
     * Creates and caches the result for the provided attribute value using the
     * evaluation context, the resource id and the provided attribute id as the
     * cache key.
     * 
     * @param attributeId
     *            The attribute id.
     * @param attributeValue
     *            The attribute value.
     * @aa
     */
    protected final void cacheAttribute(
            final String attributeId, final String attributeValue) {

        cacheAttribute(ctx, resourceId, attributeId, attributeValue);
    }

    /**
     * Creates and caches the result for the provided attribute values using the
     * evaluation context, the resource id and the provided attribute id as the
     * cache key.
     * 
     * @param ctx
     *            The <code>EvaluationCtx</code> object used as part of the
     *            key for caching.
     * @param resourceId
     *            The resource id used as part of the key for caching.
     * @param attributeId
     *            The attribute id used as part of the key for caching.
     * @param attributeValues
     *            The attribute values.
     * @aa
     */
    protected static void cacheAttribute(
        final EvaluationCtx ctx, final String resourceId,
        final String attributeId,
        final Collection<StringAttribute> attributeValues) {

        final String cacheKey =
            StringUtility.concatenateWithColonToString(resourceId, attributeId);
        RequestAttributesCache.put(ctx, cacheKey, new EvaluationResult(
            new BagAttribute(Constants.URI_XMLSCHEMA_STRING, attributeValues)));
    }

    /**
     * Creates and caches the result for the provided attribute value using the
     * evaluation context, the resource id and the provided attribute id as the
     * cache key.
     * 
     * @param ctx
     *            The <code>EvaluationCtx</code> object used as part of the
     *            key for caching.
     * @param resourceId
     *            The resource id used as part of the key for caching.
     * @param attributeId
     *            The attribute id used as part of the key for caching.
     * @param attributeValue
     *            The attribute value.
     * @aa
     */
    protected static void cacheAttribute(
        final EvaluationCtx ctx, final String resourceId,
        final String attributeId, final String attributeValue) {

        final String cacheKey =
            StringUtility.concatenateWithColonToString(resourceId, attributeId);
        RequestAttributesCache.put(ctx, cacheKey, CustomEvaluationResultBuilder
            .createSingleStringValueResult(attributeValue));
    }

    /**
     * @return the inMetadata
     */
    protected final boolean isInMetadata() {
        return inMetadata;
    }

    /**
     * @param inMetadata the inMetadata to set
     */
    protected final void setInMetadata(final boolean inMetadata) {
        this.inMetadata = inMetadata;
    }
}
