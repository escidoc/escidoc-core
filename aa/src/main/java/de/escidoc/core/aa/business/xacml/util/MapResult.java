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
 * Copyright 2010 Fachinformationszentrum Karlsruhe Gesellschaft
 * fuer wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Foerderung der Wissenschaft e.V.
 * All rights reserved.  Use is subject to license terms.
 */

package de.escidoc.core.aa.business.xacml.util;

import de.escidoc.core.common.business.fedora.TripleStoreUtility;
import de.escidoc.core.common.exceptions.system.TripleStoreSystemException;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Class encapsulating the mapping from aa attribute id to the identified used in the triple store.
 *
 * @author Torsten Tetteroo
 */
public class MapResult {

    private final String cacheId;

    private String resolvableAttributeId;

    private String nextAttributeId;

    private String contentTypePredicateId;

    private String contentTypeTitle;

    private final boolean inverse;

    private boolean hierarchical;

    private boolean includeHierarchyBase;

    /**
     * The constructor.
     *
     * @param cacheId The id used in the triple store
     * @param inverse The flag indicating if inverse lookup is needed to resolve the value of the needed attribute.
     */
    public MapResult(final String cacheId, final boolean inverse) {

        this.cacheId = cacheId;
        this.inverse = inverse;
    }

    /**
     * The constructor.
     *
     * @param cacheId              The id used in the triple store
     * @param inverse              The flag indicating if inverse lookup is needed to resolve the value of the needed
     *                             attribute.
     * @param hierarchical         The flag indicating resolving hierarchies is needed to resolve the value of the
     *                             needed attribute.
     * @param includeHierarchyBase The flag indicating if base of hierarchy should also be included in result.
     */
    public MapResult(final String cacheId, final boolean inverse, final boolean hierarchical,
        final boolean includeHierarchyBase) {

        this.cacheId = cacheId;
        this.inverse = inverse;
        this.hierarchical = hierarchical;
        this.includeHierarchyBase = includeHierarchyBase;
    }

    /**
     * @return Returns the contentTypePredicateId.
     */
    public String getContentTypePredicateId() {
        return this.contentTypePredicateId;
    }

    /**
     * @return Returns the contentTypeTitle.
     */
    public String getContentTypeTitle() {
        return this.contentTypeTitle;
    }

    /**
     * @return Returns the longestMatch.
     */
    public String getCacheId() {
        return this.cacheId;
    }

    /**
     * @return Returns the tail.
     */
    public String getNextAttributeId() {
        return this.nextAttributeId;
    }

    /**
     * @return Returns the inverse.
     */
    public boolean isInverse() {
        return this.inverse;
    }

    /**
     * @return Returns the hierarchical.
     */
    public boolean isHierarchical() {
        return this.hierarchical;
    }

    /**
     * @return Returns the includeHierarchyBase.
     */
    public boolean isIncludeHierarchyBase() {
        return this.includeHierarchyBase;
    }

    /**
     * @return Returns {@code true} if further attribute resolving is needed.
     */
    public boolean hasNext() {

        return this.nextAttributeId != null;
    }

    /**
     * See Interface for functional description.
     */
    @Override
    public String toString() {

        final ToStringBuilder toStringBuilder = new ToStringBuilder(this);
        toStringBuilder.append(super.toString());
        toStringBuilder.append("cacheId", getCacheId());
        toStringBuilder.append("resolvableId", getresolvableAttributeId());
        toStringBuilder.append("nextAttributeId", getNextAttributeId());
        toStringBuilder.append("contentTypeId", getContentTypePredicateId());
        toStringBuilder.append("contentTypeTitle", getContentTypeTitle());

        return toStringBuilder.toString();
    }

    /**
     * @param contentTypePredicateId the contentTypePredicateId to set
     */
    public void setContentTypePredicateId(final String contentTypePredicateId) {
        this.contentTypePredicateId = contentTypePredicateId;
    }

    /**
     * @param contentTypeTitle the contentTypeTitle to set
     */
    public void setContentTypeTitle(final String contentTypeTitle) {
        this.contentTypeTitle = contentTypeTitle;
    }

    /**
     * @param nextAttributeId the nextAttributeId to set
     */
    public void setNextAttributeId(final String nextAttributeId) {
        this.nextAttributeId = nextAttributeId;
    }

    /**
     * @return Returns the attribute id that is resolvable by this MapResult.
     */
    public String getresolvableAttributeId() {
        return this.resolvableAttributeId;
    }

    /**
     * Sets the attribute id that is resolvable by this MapResult.
     *
     * @param resolvableAttributeId The resolvable attribute id.
     */
    public void setResolvableAttributeId(final String resolvableAttributeId) {
        this.resolvableAttributeId = resolvableAttributeId;
    }

    /**
     * Gets the where clause to resolve the current part of the path expression.
     *
     * @param objectId The id of the resource (without version information!) for that the current part shall be
     *                 resolved.
     * @param tsu      The {@link TripleStoreUtility} to use.
     * @return Returns the where clause.
     * @throws TripleStoreSystemException e
     */
    public StringBuffer getResolveCurrentWhereClause(final String objectId, final TripleStoreUtility tsu)
        throws TripleStoreSystemException {

        // Currently three attributes exist, that needs 'inverse' lookup:
        // item:container and container:container and are stored as a
        // relationship from parent to child. Additionally, object
        // identifier need inverse lookup to get the correct dc identifier.
        // Therefore, these attributes have to be handled in a special
        // way.
        return isInverse() ? tsu.getRetrieveWhereClause(true, getCacheId(), objectId, null,
            getContentTypePredicateId(), getContentTypeTitle()) : tsu.getRetrieveWhereClause(false, getCacheId(),
            objectId, null, null, null);
    }

    /**
     * Gets the where clause to inverse resolve the current part of the path expression.
     *
     * @param objectId     The id of the resource (without the version information!) for that the current part shall be
     *                     resolved.
     * @param resourceType This parameter allows filtering the object list for a specified resource type. This parameter
     *                     may be {@code null}.
     * @param tsu          The {@link TripleStoreUtility} to use.
     * @return Returns the where clause.
     * @throws TripleStoreSystemException e
     */
    public StringBuffer getResolveCurrentInverseWhereClause(
        final String objectId, final String resourceType, final TripleStoreUtility tsu)
        throws TripleStoreSystemException {

        // Currently three attributes exist, that needs 'inverse' lookup:
        // item:container and container:container and are stored as a
        // relationship from parent to child. Additionally, object
        // identifier need inverse lookup to get the correct dc identifier.
        // Therefore, these attributes have to be handled in a special
        // way.

        // we perform an inverse lookup here, therefore, the
        // inverse flag of the map result must be switched
        // (inverse -> forward, forward -> inverse)
        return isInverse() ? tsu.getRetrieveWhereClause(false, getCacheId(), objectId, null, null, null) : tsu
            .getRetrieveWhereClause(true, getCacheId(), objectId, resourceType, getContentTypePredicateId(),
                getContentTypeTitle());
    }
}
