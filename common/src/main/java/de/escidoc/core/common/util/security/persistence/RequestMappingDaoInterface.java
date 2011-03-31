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

import java.util.List;

/**
 * Interface of a data access object for request mappings.
 *
 * @author Torsten Tetteroo
 */
public interface RequestMappingDaoInterface {

    /**
     * Returns the mappings to the XACML business actions from the provided className and methodName.<p/> This method is
     * thread-safe.
     *
     * @param className  The class name to map.
     * @param methodName The method name to map.
     * @return The method mapping as an array of MethodMapping objects. If the method provided by input parameter
     *         methodName returns an array of of objects, the array will contain two MethodMapping objects, one for the
     *         authentication check (before the invocation) and one for the filtering (after the invocation).
     */
    List<MethodMapping> retrieveMethodMappings(final String className, final String methodName);

}
