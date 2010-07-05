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
 * Copyright 2006-2009 Fachinformationszentrum Karlsruhe Gesellschaft
 * fuer wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Foerderung der Wissenschaft e.V.
 * All rights reserved.  Use is subject to license terms.
 */
package de.escidoc.core.common.business.fedora.resources.interfaces;

import java.util.Collection;

import de.escidoc.core.common.business.fedora.resources.ResourceType;
import de.escidoc.core.common.exceptions.application.invalid.InvalidSearchQueryException;

/**
 * This is a common interface for all filter classes which are able to transform
 * some kind of a filter language into an SQL fragment which can later be used
 * in the resource cache.
 * 
 * @author Andr&eacute; Schenk
 */
public interface FilterInterface {

    /**
     * Add a key=value pair to the filter list which will be ANDed.
     * 
     * @param name
     *            property name (URI)
     * @param value
     *            property value
     */
    void addRestriction(final String name, final String value);

    /**
     * Get the output format.
     * 
     * @return output format
     */
    String getFormat();

    /**
     * Get the search limit.
     * 
     * @return search limit or 0 for no limit
     */
    int getLimit();

    /**
     * Get the member id.
     * 
     * @return member id or null
     */
    String getMember();

    /**
     * Get the object type.
     * 
     * @return object type to restrict the search for members or null.
     */
    ResourceType getObjectType();

    /**
     * Get the search offset.
     * 
     * @return search offset or 0 to start from the beginning
     */
    int getOffset();

    /**
     * Get all sorting attributes with key=attribute name, value=sorting
     * direction.
     * 
     * @return list of attribute names and their sorting directions
     */
    Collection<OrderBy> getOrderBy();

    /**
     * Get the parent id.
     * 
     * @return parent id or null
     */
    String getParent();

    /**
     * Get the user role id.
     * 
     * @return user role id or null
     */
    String getRoleId();

    /**
     * Get the user id.
     * 
     * @return user id or null
     */
    String getUserId();

    /**
     * Set the search limit.
     * 
     * @param limit
     *            search limit or 0 for no limit
     */
    void setLimit(final int limit);

    /**
     * Set the member id.
     * 
     * @param member
     *            member id
     */
    void setMember(final String member);

    /**
     * Set the object type.
     * 
     * @param objectType
     *            object type to restrict the search for members.
     */
    void setObjectType(final ResourceType objectType);

    /**
     * Set the search offset.
     * 
     * @param offset
     *            search offset or 0 to start from the beginning
     */
    void setOffset(final int offset);

    /**
     * Set the parent id.
     * 
     * @param parent
     *            parent id
     */
    void setParent(final String parent);

    /**
     * Convert the XML filter into a SQL WHERE clause.
     * 
     * @return SQL WHERE clause representing this filter
     * @throws InvalidSearchQueryException
     *             thrown if the given search query could not be translated into
     *             a SQL query
     */
    String toSqlString() throws InvalidSearchQueryException;

    /**
     * Hold all values of an order by criteria.
     */
    public class OrderBy {
        public final String attribute;

        public final String direction;

        /**
         * Construct a new OrderBy object.
         * 
         * @param attribute
         *            search attribute
         * @param direction
         *            search direction
         */
        public OrderBy(final String attribute, final String direction) {
            this.attribute = attribute;
            this.direction = direction;
        }

        /**
         * Get a string representation of this object.
         * 
         * @return string representation of this object
         */
        public String toString() {
            return attribute + " " + direction;
        }
    }
}