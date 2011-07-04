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

package de.escidoc.core.common.util.security.persistence;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Class holding the list of method mappings for a method.<br> The methosMappings are stored inernally in two lists, one
 * for the before-mappings and one for the after-mappings.<br> This class provides iterators for both lists and methods
 * to retrieve the number of before- and after-mappings and elements of the before- and after-appigs lists..
 *
 * @author Torsten Tetteroo
 */
public class MethodMappingList {

    private List<MethodMapping> methodMappingsBefore;

    private List<MethodMapping> methodMappingsAfter;

    /**
     * Default constructor.
     */
    public MethodMappingList() {

    }

    /**
     * Constructor.
     *
     * @param methodMappings A {@code Collection} of method mappings
     */
    public MethodMappingList(final Collection<MethodMapping> methodMappings) {

        setMethodMappings(methodMappings);
    }

    /**
     * Setter for method mappings.
     *
     * @param methodMappings Collection of method mappings.
     */
    public final void setMethodMappings(final Iterable<MethodMapping> methodMappings) {

        if (methodMappings == null) {
            this.methodMappingsBefore = new ArrayList<MethodMapping>();
            this.methodMappingsAfter = new ArrayList<MethodMapping>();
            return;
        }

        this.methodMappingsBefore = new ArrayList<MethodMapping>();
        this.methodMappingsAfter = new ArrayList<MethodMapping>();

        for (final MethodMapping methodMapping : methodMappings) {
            if (methodMapping.isExecBefore()) {
                methodMappingsBefore.add(methodMapping);
            }
            else {
                methodMappingsAfter.add(methodMapping);
            }
        }
    }

    /**
     * Gets the number of before-mappings.
     *
     * @return The number of mapping for before-mappings.
     */
    public int sizeBefore() {

        return methodMappingsBefore.size();
    }

    /**
     * Gets the number of after-mappings.
     *
     * @return The number of mapping for after-mappings.
     */
    public int sizeAfter() {

        return methodMappingsAfter.size();
    }

    /**
     * Gets the iterator pointing to before-mappings.
     *
     * @return Returns an {@code Iterator} over before-mappings.
     */
    public Iterator<MethodMapping> iteratorBefore() {

        return methodMappingsBefore.iterator();
    }

    /**
     * Gets the iterator pointing to after-mappings.
     *
     * @return Returns an {@code Iterator} over after-mappings.
     */
    public Iterator<MethodMapping> iteratorAfter() {

        return methodMappingsAfter.iterator();
    }

    /**
     * Gets a before-mapping.
     *
     * @param index The index of the element to return in the before-mappings list.
     * @return Returns the specified element.
     */
    public MethodMapping getBefore(final int index) {

        return methodMappingsBefore.get(index);
    }

    /**
     * Gets an after-mapping.
     *
     * @param index The index of the element to return in the after-mappings list.
     * @return Returns the specified element.
     */
    public MethodMapping getAfter(final int index) {

        return methodMappingsAfter.get(index);
    }

    /**
     * Get a string representation of this object.
     *
     * @return a string representation of this object
     */
    public String toString() {
        return "[methodMappingsBefore=" + this.methodMappingsBefore + ',' + "methodMappingsAfter="
            + this.methodMappingsAfter + ']';
    }
}
