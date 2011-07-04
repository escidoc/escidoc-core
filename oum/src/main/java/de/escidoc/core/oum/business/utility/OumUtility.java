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
package de.escidoc.core.oum.business.utility;

import de.escidoc.core.common.business.fedora.TripleStoreUtility;
import de.escidoc.core.common.exceptions.application.violated.OrganizationalUnitHierarchyViolationException;
import de.escidoc.core.common.exceptions.system.TripleStoreSystemException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Stack;

/**
 * Methods to check if there are cycles in an ous parent list..
 *
 * @author Michael Schneider
 */
@Configurable
public class OumUtility {

    @Autowired
    @Qualifier("business.TripleStoreUtility")
    private TripleStoreUtility tripleStoreUtility;

    private final Stack<String> open;

    private final Collection<String> closed;

    public OumUtility() {

        this.open = new Stack<String>();
        this.closed = new ArrayList<String>();
    }

    /**
     * Detect cycles in the parent ou hierarchy.
     *
     * @param organizationalUnitId The id of the child organizational unit.
     * @param parentIds            The list of ids of the parents of the child organizational unit.
     * @throws OrganizationalUnitHierarchyViolationException
     *          If there are cycles in the parent ou hierarchy.
     * @throws de.escidoc.core.common.exceptions.system.TripleStoreSystemException
     */
    public void detectCycles(final String organizationalUnitId, final Collection<String> parentIds)
        throws OrganizationalUnitHierarchyViolationException, TripleStoreSystemException {
        if (!parentIds.isEmpty()) {
            for (final String id : parentIds) {
                if (id.equals(organizationalUnitId)) {
                    throw new OrganizationalUnitHierarchyViolationException("Ou with id " + id
                        + " cannot be referenced as a parent of ou with id " + organizationalUnitId
                        + " because it is one of its subnodes");
                }
            }
            this.closed.add(organizationalUnitId);
            expand(organizationalUnitId);
            while (!this.open.empty()) {
                final String toClosedId = this.open.pop();
                for (final String id : parentIds) {
                    if (id.equals(toClosedId)) {
                        throw new OrganizationalUnitHierarchyViolationException("Ou with id " + id
                            + " cannot be referenced as a parent of " + "ou with id " + organizationalUnitId
                            + " because it is one of its subnodes");
                    }
                }
                this.closed.add(toClosedId);
                expand(toClosedId);
            }
        }

    }

    /**
     * Write the ids of the given ou's children to the global stack.
     *
     * @param currentOuId The parent ou.
     * @throws de.escidoc.core.common.exceptions.system.TripleStoreSystemException
     */
    private void expand(final String currentOuId) throws TripleStoreSystemException {

        final Collection<String> children = this.tripleStoreUtility.getChildren(currentOuId);
        if (children != null) {
            for (final String childId : children) {
                if (!this.closed.contains(childId)) {
                    this.open.push(childId);
                }
            }
        }

    }
}
