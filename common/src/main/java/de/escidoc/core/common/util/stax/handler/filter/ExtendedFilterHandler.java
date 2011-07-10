/*
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the Common Development and Distribution License, Version 1.0
 * only (the "License"). You may not use this file except in compliance with the License.
 *
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE or http://www.escidoc.de/license. See the License for
 * the specific language governing permissions and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each file and include the License file at
 * license/ESCIDOC.LICENSE. If applicable, add the following below this CDDL HEADER, with the fields enclosed by
 * brackets "[]" replaced with your own identifying information: Portions Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
 *
 * Copyright 2006-2011 Fachinformationszentrum Karlsruhe Gesellschaft fuer wissenschaftlich-technische Information mbH
 * and Max-Planck-Gesellschaft zur Foerderung der Wissenschaft e.V. All rights reserved. Use is subject to license
 * terms.
 */

package de.escidoc.core.common.util.stax.handler.filter;

import de.escidoc.core.common.business.Constants;
import de.escidoc.core.common.exceptions.application.invalid.InvalidContentException;
import de.escidoc.core.common.util.list.ListSorting;
import de.escidoc.core.common.util.stax.StaxParser;
import de.escidoc.core.common.util.string.StringUtility;
import de.escidoc.core.common.util.xml.XmlUtility;
import de.escidoc.core.common.util.xml.stax.events.EndElement;
import de.escidoc.core.common.util.xml.stax.events.StartElement;
import de.escidoc.core.common.util.xml.stax.handler.DefaultHandler;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Extracts filter-criteria out of filter-xml.
 *
 * @author Michael Hoppe
 */
public class ExtendedFilterHandler extends DefaultHandler {

    private static final String XPATH_ORDER_BY = '/' + XmlUtility.NAME_PARAM + '/' + XmlUtility.NAME_ORDER_BY;

    private static final String XPATH_FILTER = '/' + XmlUtility.NAME_PARAM + '/' + XmlUtility.NAME_FILTER;

    /**
     * The default offset used if no offset is defined in parsed data.
     */
    private static final int DEFAULT_OFFSET = 0;

    /**
     * The default limitation of the max. number of results used if no limitation is defined in parsed data.
     */
    private static final int DEFAULT_LIMIT = 1000;

    /**
     * The default sorting if no sorting is defined in the parsed data.
     */
    private static final ListSorting DEFAULT_SORTING = ListSorting.ASCENDING;

    /**
     * Pattern used to parse uris.
     */
    private static final Pattern URI_PATTERN = Pattern.compile("[^/]+:.*/.*");

    private static final Pattern NON_URI_PATTERN =
        Pattern.compile("user|role|top-level-organizational-units" + "|primary-affiliation|limited|granted|policyId|"
            + XmlUtility.NAME_USER_ID + '|' + XmlUtility.NAME_GROUP_ID + '|' + XmlUtility.NAME_ROLE_ID + '|'
            + XmlUtility.NAME_OBJECT_ID + '|' + XmlUtility.NAME_STATUS + '|' + XmlUtility.NAME_REVOCATION_DATE_FROM
            + '|' + XmlUtility.NAME_REVOCATION_DATE_TO + '|' + XmlUtility.NAME_GRANTED_DATE_FROM + '|'
            + XmlUtility.NAME_GRANTED_DATE_TO + '|' + XmlUtility.NAME_CREATOR_ID + '|' + XmlUtility.NAME_REVOKER_ID);

    private final StaxParser parser;

    private boolean inFilter;

    private boolean inObjectList;

    private final Map<String, Set<String>> rules;

    private final Set<String> objectsToFindIdList;

    private int offset = DEFAULT_OFFSET;

    private int limit = DEFAULT_LIMIT;

    private ListSorting sorting = DEFAULT_SORTING;

    private String orderBy;

    /**
     * Constructs a {@link FilterHandler} object. This constructor implicitly sets default values: <ul> <li>
     * DEFAULT_LIMIT used if no limit is defined in parsed data</li> <li>DEFAULT_OFFSET used if no offset is
     * defined in parsed data</li> <li>DEFAULT_SORTING used if no sorting is defined in parsed data</li> </ul>
     *
     * @param parser The {@link StaxParser} to use.
     */
    public ExtendedFilterHandler(final StaxParser parser) {

        this.parser = parser;
        this.rules = new HashMap<String, Set<String>>();
        this.objectsToFindIdList = new HashSet<String>();
    }

    /**
     * See Interface for functional description.
     */
    @Override
    public String characters(final String data, final StartElement element) throws InvalidContentException {

        final String localName = element.getLocalName();
        if (this.inFilter) {
            if ("id".equals(localName)) {
                if (!this.inObjectList) {
                    throw new InvalidContentException("Invalid id element in filter rule.");
                }
                objectsToFindIdList.add(data);
            }
            else if ("filter".equals(localName)) {
                // filtername=objectsToFind has no character data
                final int indexOfName = element.indexOfAttribute(null, "name");
                if (indexOfName >= 0) {
                    String filterName = element.getAttribute(indexOfName).getValue();

                    // filter name MUST be a URI or match the NON_URI_PATTERN
                    final Matcher uriMatcher = URI_PATTERN.matcher(filterName);
                    final Matcher nonUriMatcher = NON_URI_PATTERN.matcher(filterName);
                    if (!uriMatcher.matches() && !nonUriMatcher.matches()) {
                        throw new InvalidContentException(StringUtility.format("Filter is no URI.", filterName));
                    }

                    // temporary because filter-names for retreiveGrants
                    // changed to URIs
                    if (nonUriMatcher.matches()) {
                        filterName = transformFilterName(filterName);
                    }

                    if (!rules.containsKey(filterName)) {
                        rules.put(filterName, new HashSet<String>());
                    }
                    rules.get(filterName).add(data);
                }
            }
        }
        else if (localName.equals(XmlUtility.NAME_OFFSET)) {
            this.offset = Integer.parseInt(data);
        }
        else if (localName.equals(XmlUtility.NAME_LIMIT)) {
            this.limit = Integer.parseInt(data);
        }
        else if (localName.equals(XmlUtility.NAME_ORDER_BY)) {
            this.orderBy = transformFilterName(data);
        }

        return data;
    }

    /**
     * See Interface for functional description.
     */
    @Override
    public StartElement startElement(final StartElement element) {
        final String curPath = parser.getCurPath();

        if (curPath.equals(XPATH_FILTER)) {
            this.inFilter = true;
            final int indexOfName = element.indexOfAttribute(null, "name");
            if (indexOfName >= 0) {
                final String filterName = element.getAttribute(indexOfName).getValue();
                if (filterName.equals(Constants.DC_IDENTIFIER_URI)) {
                    this.inObjectList = true;
                }
            }
        }
        else if (curPath.equals(XPATH_ORDER_BY)) {
            final int indexOfSorting = element.indexOfAttribute(null, XmlUtility.NAME_SORTING);
            if (indexOfSorting >= 0) {
                this.sorting =
                    ListSorting.valueOf(element.getAttribute(indexOfSorting).getValue().toUpperCase(Locale.ENGLISH));
            }
        }
        return element;
    }

    /**
     * See Interface for functional description.
     */
    @Override
    public EndElement endElement(final EndElement element) {
        final String curPath = parser.getCurPath();

        if (curPath.equals(XPATH_FILTER)) {
            if (this.inObjectList) {
                this.inObjectList = false;
                rules.put(Constants.DC_IDENTIFIER_URI, this.objectsToFindIdList);
            }
            this.inFilter = false;
        }
        return element;
    }

    /**
     * Temporary Method to transform filter-names for method retreiveGrants into URIs.
     *
     * @param filterName original filterName
     * @return String transformed filterName
     */
    private static String transformFilterName(final String filterName) {
        String transformed = filterName;
        if (filterName.equals(XmlUtility.NAME_USER_ID)) {
            transformed = Constants.FILTER_USER;
        }
        else if (filterName.equals(XmlUtility.NAME_GROUP_ID)) {
            transformed = Constants.FILTER_GROUP;
        }
        else if (filterName.equals(XmlUtility.NAME_ROLE_ID)) {
            transformed = Constants.FILTER_ROLE;
        }
        else if (filterName.equals(XmlUtility.NAME_OBJECT_ID)) {
            transformed = Constants.FILTER_ASSIGNED_ON;
        }
        else if (filterName.equals(XmlUtility.NAME_CREATOR_ID)) {
            transformed = Constants.FILTER_CREATED_BY;
        }
        else if (filterName.equals(XmlUtility.NAME_REVOKER_ID)) {
            transformed = Constants.FILTER_REVOKED_BY;
        }
        return transformed;
    }

    /**
     * Returns a Map with the filter names as keys for the filter values. A special entry is the key
     * {@code objectsToFind} for a Set of object IDs.
     *
     * @return Filter Map
     */
    public Map<String, Set<String>> getRules() {

        return this.rules;
    }

    /**
     * Gets the parsed offset.
     *
     * @return Returns the parsed offset. If none has been found, the default value DEFAULT_OFFSET is returned
     */
    public int getOffset() {

        return this.offset;
    }

    /**
     * Gets the parsed limit.
     *
     * @return Returns the parsed limit. If none has been found, the default value DEFAULT_LIMIT is returned.
     */
    public int getLimit() {

        return this.limit;
    }

    /**
     * Gets the parsed ordering information.
     *
     * @return Returns the parsed ordering information. If none has been found, {@code null} is returned.
     */
    public String getOrderBy() {
        return this.orderBy;
    }

    /**
     * Gets the parsed sorting information.
     *
     * @return Returns the parsed ordering information. If none has been found, the default value DEFAULT_SORTING is
     * returned.
     */
    public ListSorting getSorting() {
        return this.sorting;
    }

}
