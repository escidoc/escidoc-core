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
package de.escidoc.core.aa.business.authorisation;

import com.sun.xacml.TargetMatch;
import com.sun.xacml.UnknownIdentifierException;
import com.sun.xacml.attr.AttributeDesignator;
import com.sun.xacml.attr.AttributeValue;
import com.sun.xacml.attr.StringAttribute;
import com.sun.xacml.cond.Function;
import com.sun.xacml.cond.FunctionFactory;
import com.sun.xacml.cond.FunctionTypeException;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Helper class to support target creation.
 *
 * @author Roland Werner (Accenture)
 * @author Torsten Tetteroo
 */
public final class CustomTargetBuilder {

    /**
     * Standard Constructor.<br> Prevents instance creation.
     */
    private CustomTargetBuilder() {
    }

    /**
     * Generates a resource match for the provided values.
     *
     * @param matchId        The match id of the target match.
     * @param attributeValue The attribute value of the target match.
     * @param designatorId   The designator id of the target match.
     * @param designatorType The designator type of the target match.
     * @return Returns the generated {@code TargetMatch} object of type {@code TargetMatch.RESOURCE}
     * @throws URISyntaxException         Thrown if there is a problem with an URI.
     * @throws UnknownIdentifierException Thrown if there is a problem with an identifier
     * @throws FunctionTypeException      Thrown if there is a problem with the match id.
     */
    public static TargetMatch generateResourceMatch(
        final String matchId, final String attributeValue, final String designatorId, final String designatorType)
        throws URISyntaxException, UnknownIdentifierException, FunctionTypeException {
        final URI designatorTypeUri = new URI(designatorType);
        final URI designatorIdUri = new URI(designatorId);
        final AttributeDesignator designator =
            new AttributeDesignator(AttributeDesignator.RESOURCE_TARGET, designatorTypeUri, designatorIdUri, false);
        final StringAttribute value = new StringAttribute(attributeValue);
        return createTargetMatch(TargetMatch.RESOURCE, matchId, designator, value);
    }

    /**
     * Generates a subject match for the provided values.
     *
     * @param matchId        The match id of the target match.
     * @param attributeValue The attribute value of the target match.
     * @param designatorId   The designator id of the target match.
     * @param designatorType The designator type of the target match.
     * @return Returns the generated {@code TargetMatch} object of type {@code TargetMatch.SUBJECT}
     * @throws URISyntaxException         Thrown if there is a problem with an URI.
     * @throws UnknownIdentifierException Thrown if there is a problem with an identifier
     * @throws FunctionTypeException      Thrown if there is a problem with the match id.
     */
    public static TargetMatch generateSubjectMatch(
        final String matchId, final String attributeValue, final String designatorId, final String designatorType)
        throws URISyntaxException, UnknownIdentifierException, FunctionTypeException {

        final StringAttribute value = new StringAttribute(attributeValue);
        AttributeDesignator designator = null;
        if (designatorId != null && designatorType != null) {
            final URI designatorTypeUri = new URI(designatorType);
            final URI designatorIdUri = new URI(designatorId);
            designator =
                new AttributeDesignator(AttributeDesignator.SUBJECT_TARGET, designatorTypeUri, designatorIdUri, false);
        }

        return createTargetMatch(TargetMatch.SUBJECT, matchId, designator, value);
    }

    /**
     * Simple helper routine that creates a TargetMatch instance.
     *
     * @param type       the type of match
     * @param functionId the matching function identifier
     * @param designator the AttributeDesignator used in this match
     * @param value      the AttributeValue used in this match
     * @return the matching element
     * @throws FunctionTypeException      Thrown if there is a problem with the provided functionId.
     * @throws UnknownIdentifierException Thrown if there is a problem with the provided functionId.
     */
    public static TargetMatch createTargetMatch(
        final int type, final String functionId, final AttributeDesignator designator, final AttributeValue value)
        throws UnknownIdentifierException, FunctionTypeException {

        final FunctionFactory factory = FunctionFactory.getTargetInstance();
        final Function function = factory.createFunction(functionId);
        return new TargetMatch(type, function, designator, value);

    }

}
