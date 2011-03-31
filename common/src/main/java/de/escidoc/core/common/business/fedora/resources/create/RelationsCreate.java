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

package de.escidoc.core.common.business.fedora.resources.create;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Relations.
 *
 * @author Steffen Wagner
 */
public class RelationsCreate {

    private List<RelationCreate> relations;

    /**
     * Add Content Relation to Relations collection.
     *
     * @param relation A ContentRelation (between Item/Container).
     */
    public void add(final RelationCreate relation) {

        if (this.relations == null) {
            this.relations = new ArrayList<RelationCreate>();
        }
        this.relations.add(relation);
    }

    /**
     * Remove a relation from Relations.
     *
     * @param relation The relation which is to remove. The values are compared by each properties.
     */
    public void remove(final RelationCreate relation) {

        if (this.relations != null) {
            final Iterator<RelationCreate> it = this.relations.iterator();

            while (it.hasNext()) {
                final RelationCreate rel = it.next();

                if (rel.equals(relation) || rel.getPredicate().equals(relation.getPredicate())
                    && rel.getPredicateNs().equals(relation.getPredicate())
                    && rel.getTarget().equals(relation.getTarget())) {

                    it.remove();
                    break;
                }
            }
        }
    }

    /**
     * Get a Iterator over Relations.
     *
     * @return Relations Iterator or null if no relation exist.
     */
    public Iterator<RelationCreate> iterator() {

        if (this.relations == null) {
            return null;
        }
        return this.relations.iterator();
    }
}
