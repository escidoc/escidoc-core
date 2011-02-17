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
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FilterHandler extends DefaultHandler {

    private static final String XPATH_ORDER_BY = '/' + XmlUtility.NAME_PARAM
        + '/' + XmlUtility.NAME_ORDER_BY;

    private static final String XPATH_FILTER = '/' + XmlUtility.NAME_PARAM
        + '/' + XmlUtility.NAME_FILTER;

    /**
     * The default offset used if no offset is defined in parsed data.
     */
    public static final int DEFAULT_OFFSET = 0;

    /**
     * The default limitation of the max. number of results used if no
     * limitation is defined in parsed data.
     */
    public static final int DEFAULT_LIMIT = 1000;

    /**
     * The default sorting if no sorting is defined in the parsed data.
     */
    private static final ListSorting DEFAULT_SORTING = ListSorting.ASCENDING;

    /**
     * Pattern used to parse uris.
     */
    private static final Pattern URI_PATTERN = Pattern.compile("[^/]+:.*/.*");

    private static final Pattern NON_URI_PATTERN = Pattern
        .compile("user|role|top-level-organizational-units"
            + "|primary-affiliation|limited|granted|policyId|objectId"
            + "|userId|groupId|roleId|objectId|status|revocationDateFrom"
            + "|revocationDateTo|grantedDateFrom|grantedDateTo"
            + "|creatorId|revokerId");

    private StaxParser parser = null;

    private boolean inFilter = false;

    private boolean inObjectList = false;

    private Map<String, Object> rules = null;

    private Set<String> objectsToFindIdList = null;

    private int offset = DEFAULT_OFFSET;

    private int limit = DEFAULT_LIMIT;

    private ListSorting sorting = DEFAULT_SORTING;

    private String orderBy = null;

    /**
     * Constructs a {@link FilterHandler} object. This constructor implicitly
     * sets default values:
     * <ul>
     * <li>{@link DEFAULT_LIMIT} used if no limit is defined in parsed data</li>
     * <li>{@link DEFAULT_OFFSET} used if no offset is defined in parsed data</li>
     * <li>{@link DEFAULT_SORTING} used if no sorting is defined in parsed data</li>
     * </ul>
     * 
     * @param parser
     *            The {@link StaxParser} to use.
     * 
     * @common
     */
    public FilterHandler(final StaxParser parser) {

        this.parser = parser;
        this.rules = new HashMap<String, Object>();
        this.objectsToFindIdList = new HashSet<String>();
    }

    // CHECKSTYLE:JAVADOC-OFF
    /**
     * See Interface for functional description.
     * 
     * @param data
     * @param element
     * @return
     * @throws InvalidContentException
     * @see de.escidoc.core.common.util.xml.stax.handler.DefaultHandler#characters(java.lang.String,
     *      de.escidoc.core.common.util.xml.stax.events.StartElement)
     */
    @Override
    public String characters(final String data, final StartElement element)
        throws InvalidContentException {

        final String localName = element.getLocalName();
        if (inFilter) {
            if (localName.equals("id")) {
                if (!inObjectList) {
                    throw new InvalidContentException(
                        "Invalid id element in filter rule.");
                }
                objectsToFindIdList.add(data);
            }
            else if (localName.equals("filter")) {
                // filtername=objectsToFind has no character data
                int indexOfName = element.indexOfAttribute(null, "name");
                if (indexOfName >= 0) {
                    String filterName =
                        element.getAttribute(indexOfName).getValue();
                    // filter name MUST be a URI or "user", "role" or
                    // "top-level-organizational-units"
                    Matcher uriMatcher = URI_PATTERN.matcher(filterName);
                    Matcher nonUriMatcher = NON_URI_PATTERN.matcher(filterName);
                    if (!uriMatcher.matches() && !nonUriMatcher.matches()) {
                        throw new InvalidContentException(
                            StringUtility.format(
                                    "Filter is no URI.", filterName));
                    }
                    // filter name MUST NOT occur twice
                    if (rules.containsKey(filterName)) {
                        throw new InvalidContentException(
                            StringUtility.format(
                                "Filter name occurs twice in filter param.",
                                filterName));
                    }
                    rules.put(filterName, data);

                } else {
                    // TODO throw exception or ignore filter without name
                }
            }
        }
        else if (localName.equals(XmlUtility.NAME_OFFSET)) {
            offset = Integer.parseInt(data);
        }
        else if (localName.equals(XmlUtility.NAME_LIMIT)) {
            limit = Integer.parseInt(data);
        }
        else if (localName.equals(XmlUtility.NAME_ORDER_BY)) {
            orderBy = data;
        }

        return data;
    }

    /**
     * See Interface for functional description.
     * 
     * @param element
     * @return
     * @see de.escidoc.core.common.util.xml.stax.handler.DefaultHandler#startElement(de.escidoc.core.common.util.xml.stax.events.StartElement)
     */
    @Override
    public StartElement startElement(final StartElement element) {
        String curPath = parser.getCurPath();
        if (curPath.equals(XPATH_FILTER)) {
            inFilter = true;
            int indexOfName = element.indexOfAttribute(null, "name");
            if (indexOfName >= 0) {
                String filterName =
                    element.getAttribute(indexOfName).getValue();
                if (filterName.equals(Constants.DC_IDENTIFIER_URI)) {
                    inObjectList = true;
                }
            }
        }
        else if (curPath.equals(XPATH_ORDER_BY)) {
            int indexOfSorting =
                element.indexOfAttribute(null, XmlUtility.NAME_SORTING);
            if (indexOfSorting >= 0) {
                sorting =
                    ListSorting.valueOf(element
                        .getAttribute(indexOfSorting).getValue().toUpperCase());
            }
        }
        return element;
    }

    /**
     * See Interface for functional description.
     * 
     * @param element
     * @return
     * @see de.escidoc.core.common.util.xml.stax.handler.DefaultHandler#endElement(de.escidoc.core.common.util.xml.stax.events.EndElement)
     */
    @Override
    public EndElement endElement(final EndElement element) {
        String curPath = parser.getCurPath();
        if (curPath.equals(XPATH_FILTER)) {
            if (inObjectList) {
                inObjectList = false;
                rules.put(Constants.DC_IDENTIFIER_URI, objectsToFindIdList);
            }
            inFilter = false;
        }
        return element;
    }

    // CHECKSTYLE:JAVADOC-ON

    /**
     * Returns a Map with the filter names as keys for the filter values. A
     * special entry is the key <code>objectsToFind</code> for a Set of object
     * IDs.
     * 
     * @return Filter Map
     * @common
     */
    public Map<String, Object> getRules() {
        return rules;
    }

    /**
     * Puts a new Rule into rule-map.
     * 
     * @param ruleName
     *            name of rule
     * @param ruleValues
     *            values for rule
     * 
     * @common
     */
    @SuppressWarnings("unchecked")
    public void putRule(final String ruleName, final Set<String> ruleValues) {
        if (rules.get(ruleName) != null) {
            if (rules.get(ruleName) instanceof String) {
                ruleValues.add((String) rules.get(ruleName));
            }
            else if (rules.get(ruleName) instanceof Set) {
                ruleValues.addAll((Set) rules.get(ruleName));
            }
        }
        rules.put(ruleName, ruleValues);
    }

    /**
     * Removes a Rule from rule-map.
     * 
     * @param ruleName
     *            name of rule
     * 
     * @common
     */
    public void removeRule(final String ruleName) {
        rules.remove(ruleName);
    }

    /**
     * Gets the parsed offset.
     * 
     * @return Returns the parsed offset. If none has been found, the default
     *         value {@link FilterHandler.DEFAULT_OFFSET} is returned
     * @common
     */
    public int getOffset() {

        return offset;
    }

    /**
     * Gets the parsed limit.
     * 
     * @return Returns the parsed limit. If none has been found, the default
     *         value {@link FilterHandler.DEFAULT_LIMIT} is returned.
     * @common
     */
    public int getLimit() {

        return limit;
    }

    /**
     * Gets the parsed ordering information.
     * 
     * @return Returns the parsed ordering information. If none has been found,
     *         <code>null</code> is returned.
     * @common
     */
    public String getOrderBy() {
        return orderBy;
    }

    /**
     * Gets the parsed sorting information.
     * 
     * @return Returns the parsed ordering information. If none has been found,
     *         the default value {@link FilterHandler.DEFAULT_SORTING} is
     *         returned.
     * @common
     */
    public ListSorting getSorting() {
        return sorting;
    }

}
