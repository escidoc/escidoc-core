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
package de.escidoc.core.aa.business.xacml;

import com.sun.xacml.Indenter;
import com.sun.xacml.Target;
import com.sun.xacml.TargetMatch;
import com.sun.xacml.attr.AttributeDesignator;
import com.sun.xacml.attr.StringAttribute;
import com.sun.xacml.cond.Function;
import com.sun.xacml.cond.FunctionFactory;
import de.escidoc.core.aa.business.persistence.Action;
import de.escidoc.core.aa.business.xacml.function.XacmlFunctionContains;
import org.esidoc.core.utils.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * This class implements the XACML Target used in the framework. <br> In the escidoc framework, the action part of the
 * target is a restricted: <ul> <li>the action match id equals to urn:oasis:names:tc:xacml:1.0:function:string-is-in</li>
 * <li>the action DESIGNATOR type equals to http://www.w3.org/2001/XMLSchema#string, and</li> <li>the action DESIGNATOR
 * id equals to urn:oasis:names:tc:xacml:1.0:action:action-id</li> </ul>
 *
 * @author Torsten Tetteroo
 */
public class XacmlTarget extends Target {

    private static final Logger LOGGER = LoggerFactory.getLogger(XacmlTarget.class);

    private static final String URN_ACTION_DESIGNATOR_ID = "urn:oasis:names:tc:xacml:1.0:action:action-id";

    private static final String URN_ACTION_DESIGNATOR_TYPE = "http://www.w3.org/2001/XMLSchema#string";

    @SuppressWarnings( { "CanBeFinal" })
    private static AttributeDesignator designator; // Ignore FindBugs

    static {
        try {
            designator =
                new AttributeDesignator(AttributeDesignator.ACTION_TARGET, new URI(URN_ACTION_DESIGNATOR_TYPE),
                    new URI(URN_ACTION_DESIGNATOR_ID), false);
        }
        catch (final URISyntaxException e) {
            // Dont do anything because null-query is given.
            if (LOGGER.isWarnEnabled()) {
                LOGGER.warn("Error on initialising designator.");
            }
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Error on initialising designator.", e);
            }
        }
    }

    private static Function function;

    /**
     * @param subjects  The subjects of the target.
     * @param resources The resources of the target.
     * @param actions   The {@code Action}data objects of the actions of the target.
     */
    public XacmlTarget(final List subjects, final List resources, final Collection<Action> actions) {

        super(subjects, resources, buildActionMatches(actions));
    }

    /**
     * Builds the list of action matches of this target using the provided Set of action data objects holding the action
     * ids.
     *
     * @param actions The {@code Collection} of the data objects of the actions defining this target.
     * @return Returns the built {@code List} of action matches.
     */
    private static List<List<TargetMatch>> buildActionMatches(final Collection<Action> actions) {

        List<List<TargetMatch>> actionsList = null;

        if (actions != null && !actions.isEmpty()) {
            actionsList = new ArrayList<List<TargetMatch>>();
            final List<TargetMatch> action = new ArrayList<TargetMatch>();
            final Iterator<Action> iter = actions.iterator();
            final StringBuilder values = new StringBuilder();
            while (iter.hasNext()) {
                values.append(iter.next().getName());
                values.append(' ');
            }
            action.add(createTargetActionMatch(new StringAttribute(values.toString().trim())));
            actionsList.add(action);
        }
        return actionsList;
    }

    /**
     * Simple helper routine that creates a TargetMatch instance.
     *
     * @param value the AttributeValue used in this match
     * @return the matching element
     */
    private static TargetMatch createTargetActionMatch(final StringAttribute value) {

        try {
            // get the factory that handles Target functions and get an
            // instance of the right function
            if (function == null) {
                final FunctionFactory factory = FunctionFactory.getTargetInstance();
                function = factory.createFunction(XacmlFunctionContains.NAME);
            }

            // create the TargetMatch
            return new TargetMatch(TargetMatch.ACTION, function, designator, value);
        }
        catch (final Exception e) {
            if (LOGGER.isWarnEnabled()) {
                LOGGER.warn("Error on creating target action.");
            }
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Error on creating target action.", e);
            }
            return null;
        }

    }

    /**
     * See Interface for functional description.
     *
     * @see Object#toString()
     */
    public String toString() {
        final ByteArrayOutputStream os = new ByteArrayOutputStream();
        String returnValue;
        try {
            encode(os, new Indenter());
            returnValue = os.toString();
        }
        finally {
            IOUtils.closeStream(os);
        }
        return returnValue;
    }

}
