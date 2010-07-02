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
package de.escidoc.core.common.business.fedora.resources;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.escidoc.core.common.business.Constants;
import de.escidoc.core.common.business.fedora.TripleStoreUtility;
import de.escidoc.core.common.business.fedora.resources.interfaces.FilterInterface;
import de.escidoc.core.common.exceptions.application.invalid.InvalidSearchQueryException;
import de.escidoc.core.common.util.configuration.EscidocConfiguration;
import de.escidoc.core.common.util.logger.AppLogger;

/**
 * This class parses an XML filter and translates it into a SQL WHERE clause.
 * The filter criteria are also available as key/value pairs in the map this
 * class is derived from. The keys are the names of the corresponding DB table
 * columns. The values are lists to handle multiple values.
 * 
 * @author SCHE
 */
public abstract class AbstractFilter extends HashMap<String, List<Object>>
    implements FilterInterface {
    private static final long serialVersionUID = 1532617232308307459L;

    /**
     * SQL constants for the sorting direction.
     */
    protected static final String DIRECTION_ASCENDING = "ASC";

    protected static final String DIRECTION_DESCENDING = "DESC";

    /**
     * Default search limit. May be overridden by config.
     */
    protected static final int DEFAULT_SEARCH_LIMIT = 1000;

    /**
     * Mapping between property URI and database column name.
     */
    protected static final Map<String, String> PROPERTY_MAP =
        new HashMap<String, String>();

    static {
        PROPERTY_MAP.put(TripleStoreUtility.PROP_DC_IDENTIFIER, "/id");
        PROPERTY_MAP.put(TripleStoreUtility.PROP_CONTENT_MODEL_ID,
            "/properties/content-model/id");
        PROPERTY_MAP.put(TripleStoreUtility.PROP_CONTENT_MODEL_TITLE,
            "/properties/content-model/title");
        PROPERTY_MAP.put(TripleStoreUtility.PROP_CONTEXT_ID,
            "/properties/context/id");
        PROPERTY_MAP.put(TripleStoreUtility.PROP_CONTEXT_TITLE,
            "/properties/context/title");
        PROPERTY_MAP.put(TripleStoreUtility.PROP_CREATED_BY_ID,
            "/properties/created-by/id");
        PROPERTY_MAP.put(TripleStoreUtility.PROP_CREATED_BY_TITLE,
            "/properties/created-by/title");
        PROPERTY_MAP.put(TripleStoreUtility.PROP_CREATION_DATE,
            "/properties/creation-date");
        PROPERTY_MAP.put(TripleStoreUtility.PROP_DC_DESCRIPTION,
            "/md-records/md-record/publication/description");
        PROPERTY_MAP.put(TripleStoreUtility.PROP_LAST_MODIFICATION_DATE,
            "/last-modification-date");
        PROPERTY_MAP.put(Constants.PROPERTIES_NS_URI + "lock-date",
            "/properties/lock-date");
        PROPERTY_MAP.put(Constants.STRUCTURAL_RELATIONS_NS_URI + "lock-owner",
            "/properties/lock-owner/id");
        PROPERTY_MAP.put(Constants.PROPERTIES_NS_URI + "lock-status",
            "/properties/lock-status");
        PROPERTY_MAP.put(TripleStoreUtility.PROP_MODIFIED_BY_ID,
            "/properties/version/modified-by/id");
        PROPERTY_MAP.put(TripleStoreUtility.PROP_MODIFIED_BY_TITLE,
            "/properties/version/modified-by/title");
        PROPERTY_MAP.put(TripleStoreUtility.PROP_ORGANIZATIONAL_UNIT,
            "/properties/organizational-units/organizational-unit/id");
        PROPERTY_MAP.put(TripleStoreUtility.PROP_OBJECT_PID, "/properties/pid");
        PROPERTY_MAP.put(TripleStoreUtility.PROP_PUBLIC_STATUS,
            "/properties/public-status");
        PROPERTY_MAP.put(TripleStoreUtility.PROP_PUBLIC_STATUS_COMMENT,
            "/properties/public-status-comment");
        PROPERTY_MAP.put(TripleStoreUtility.PROP_DC_TITLE, "/title");
        PROPERTY_MAP.put(TripleStoreUtility.PROP_CONTEXT_TYPE,
            "/properties/type");
        PROPERTY_MAP.put(TripleStoreUtility.PROP_LATEST_VERSION_NUMBER,
            "/properties/version/number");
        PROPERTY_MAP.put(TripleStoreUtility.PROP_LATEST_VERSION_STATUS,
            "/properties/version/status");
    }

    /**
     * Logging goes there.
     */
    private static AppLogger logger =
        new AppLogger(AbstractFilter.class.getName());

    protected String format = null;

    protected int limit = DEFAULT_SEARCH_LIMIT;

    protected String member = null;

    protected String objectType = null;

    protected int offset = 0;

    protected Collection<OrderBy> orderBy = new LinkedList<OrderBy>();

    protected String parent = null;

    protected String roleId = null;

    protected boolean topLevelOnly = false;

    protected String userId = null;

    /**
     * Read the search limit from properties.
     */
    protected void initLimit() {
        try {
            this.limit =
                Integer.parseInt(EscidocConfiguration.getInstance().get(
                    EscidocConfiguration.FILTER_DEFAULT_LIMIT,
                    "" + DEFAULT_SEARCH_LIMIT));
        }
        catch (NumberFormatException e) {
            if (logger.isWarnEnabled()) {
                String msg =
                    "Error setting default filter limit from config. "
                        + "Reset to standard default limit of "
                        + DEFAULT_SEARCH_LIMIT + ". " + e.getClass().getName();
                logger.warn(msg);
            }
            this.limit = DEFAULT_SEARCH_LIMIT;
        }
        catch (IOException e) {
            if (logger.isWarnEnabled()) {
                String msg =
                    "Error setting default filter limit from config. "
                        + "Reset to standard default limit of "
                        + DEFAULT_SEARCH_LIMIT + ". " + e.getClass().getName();
                logger.warn(msg);
            }
            this.limit = DEFAULT_SEARCH_LIMIT;
        }
    }

    // CHECKSTYLE:JAVADOC-OFF

    /**************************************************************************
     * start implementation of FilterInterface interface
     *************************************************************************/

    /**
     * See Interface for functional description.
     * 
     * @param name
     * @param value
     * @see de.escidoc.core.common.business.fedora.resources.interfaces
     *      .FilterInterface#addRestriction(java.lang.String, java.lang.String)
     */
    public void addRestriction(final String name, final String value) {
        if ((name != null) && (name.length() > 0) && (value != null)
            && (value.length() > 0)) {
            String columnName = PROPERTY_MAP.get(name);

            if ((columnName == null) || (columnName.length() == 0)) {
                // assume the filter name is a local path style filter
                columnName = name;
            }

            List<Object> filterList = (List<Object>) get(columnName);

            if (filterList == null) {
                filterList = new LinkedList<Object>();
            }
            filterList.add(value);
            put(columnName, filterList);
        }
    }

    /**
     * See Interface for functional description.
     * 
     * @return
     * @see de.escidoc.core.common.business.fedora.resources.interfaces
     *      .FilterInterface#getFormat()
     */
    public String getFormat() {
        return format;
    }

    /**
     * See Interface for functional description.
     * 
     * @return
     * @see de.escidoc.core.common.business.fedora.resources.interfaces
     *      .FilterInterface#getLimit()
     */
    public int getLimit() {
        return limit;
    }

    /**
     * See Interface for functional description.
     * 
     * @return
     * @see de.escidoc.core.common.business.fedora.resources.interfaces
     *      .FilterInterface#getMember()
     */
    public String getMember() {
        return member;
    }

    /**
     * See Interface for functional description.
     * 
     * @return
     * @see de.escidoc.core.common.business.fedora.resources.interfaces
     *      .FilterInterface#getObjectType()
     */
    public String getObjectType() {
        return objectType;
    }

    /**
     * See Interface for functional description.
     * 
     * @return
     * @see de.escidoc.core.common.business.fedora.resources.interfaces
     *      .FilterInterface#getOffset()
     */
    public int getOffset() {
        return offset;
    }

    /**
     * See Interface for functional description.
     * 
     * @return
     * @see de.escidoc.core.common.business.fedora.resources.interfaces
     *      .FilterInterface#getOrderBy()
     */
    public Collection<OrderBy> getOrderBy() {
        return orderBy;
    }

    /**
     * See Interface for functional description.
     * 
     * @return
     * @see de.escidoc.core.common.business.fedora.resources.interfaces
     *      .FilterInterface#getParent()
     */
    public String getParent() {
        return parent;
    }

    /**
     * See Interface for functional description.
     * 
     * @return
     * @see de.escidoc.core.common.business.fedora.resources.interfaces
     *      .FilterInterface#getRoleId()
     */
    public String getRoleId() {
        return roleId;
    }

    /**
     * See Interface for functional description.
     * 
     * @return
     * @see de.escidoc.core.common.business.fedora.resources.interfaces
     *      .FilterInterface#getUserId()
     */
    public String getUserId() {
        return userId;
    }

    /**
     * See Interface for functional description.
     * 
     * @param limit
     * @see de.escidoc.core.common.business.fedora.resources.interfaces
     *      .FilterInterface#setLimit(int)
     */
    public void setLimit(final int limit) {
        this.limit = limit;
    }

    /**
     * See Interface for functional description.
     * 
     * @param member
     * @see de.escidoc.core.common.business.fedora.resources.interfaces
     *      .FilterInterface#setMember(java.lang.String)
     */
    public void setMember(final String member) {
        this.member = member;
    }

    /**
     * See Interface for functional description.
     * 
     * @param objectType
     * @see de.escidoc.core.common.business.fedora.resources.interfaces
     *      .FilterInterface#setObjectType(String)
     */
    public void setObjectType(final String objectType) {
        this.objectType = objectType;
    }

    /**
     * See Interface for functional description.
     * 
     * @param offset
     * @see de.escidoc.core.common.business.fedora.resources.interfaces
     *      .FilterInterface#setOffset(int)
     */
    public void setOffset(final int offset) {
        this.offset = offset;
    }

    /**
     * See Interface for functional description.
     * 
     * @param parent
     * @see de.escidoc.core.common.business.fedora.resources.interfaces
     *      .FilterInterface#setParent(java.lang.String)
     */
    public void setParent(final String parent) {
        this.parent = parent;
    }

    /**
     * See Interface for functional description.
     * 
     * @return
     * @see de.escidoc.core.common.business.fedora.resources.interfaces
     *      .FilterInterface#toSqlString()
     */
    public abstract String toSqlString() throws InvalidSearchQueryException;

    /**************************************************************************
     * end implementation of FilterInterface interface
     *************************************************************************/

    // CHECKSTYLE:JAVADOC-ON

    /**
     * Add a sorting attribute.
     * 
     * @param attribute
     *            attribute name
     * @param direction
     *            sorting direction
     */
    protected void addOrderBy(final String attribute, final String direction) {
        orderBy.add(new OrderBy(attribute, direction));
    }

    /**
     * Get the top level OUs only flag.
     * 
     * @return top level OUs only flag
     */
    protected boolean getTopLevelOnly() {
        return topLevelOnly;
    }

    /**
     * Set all sorting attributes.
     * 
     * @param attributes
     *            order by attributes
     */
    protected void setOrderBy(final Collection<OrderBy> attributes) {
        orderBy.clear();
        orderBy.addAll(attributes);
    }

    /**
     * Set the user role id.
     * 
     * @param roleId
     *            user role id or null
     */
    protected void setRoleId(final String roleId) {
        this.roleId = roleId;
    }

    /**
     * Set the output format.
     * 
     * @param format
     *            output format
     */
    protected void setFormat(final String format) {
        this.format = format;
    }

    /**
     * Set the top level only flag.
     * 
     * @param topLevelOnly
     *            top level only flag
     */
    protected void setTopLevelOnly(final boolean topLevelOnly) {
        this.topLevelOnly = topLevelOnly;
    }

    /**
     * Set the user id.
     * 
     * @param userId
     *            user id or null
     */
    protected void setUserId(final String userId) {
        this.userId = userId;
    }

    /**
     * Get the string representation of this filter.
     * 
     * @return the string representation of this filter
     */
    public String toString() {
        StringBuffer result = new StringBuffer("filter=" + super.toString());

        result.append(", sqlQuery=");
        try {
            result.append(toSqlString());
        }
        catch (InvalidSearchQueryException e) {
            result.append(e.getMessage());
        }
        result.append(", limit=" + limit);
        result.append(", offset=" + offset);
        result.append(", order=" + orderBy);
        result.append(", member=" + member);
        result.append(", objectType=" + objectType);
        result.append(", parent=" + parent);
        result.append(", roleId=" + roleId);
        result.append(", topLevelOnly=" + topLevelOnly);
        result.append(", userId=" + userId);
        return result.toString();
    }
}
