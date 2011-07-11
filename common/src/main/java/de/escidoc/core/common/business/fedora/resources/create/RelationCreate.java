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

/**
 * Relation.
 *
 * @author Steffen Wagner
 */
public class RelationCreate {

    private String predicate;

    private String target;

    private String predicateNs;

    /**
     * RelationCreate.
     */
    public RelationCreate() {

    }

    /**
     * RelationCreate.
     *
     * @param predicateNamespace Namespace of the relation.
     * @param predicate          Predicate of Relation.
     * @param target             Target (objid) for the relation.
     */
    public RelationCreate(final String predicateNamespace, final String predicate, final String target) {

        this.predicateNs = predicateNamespace;
        this.predicate = predicate;
        this.target = target;
    }

    /**
     * @param predicateValue the predicateValue to set
     */
    public void setPredicate(final String predicateValue) {
        this.predicate = predicateValue;
    }

    /**
     * @return the predicateValue
     */
    public String getPredicate() {
        return this.predicate;
    }

    /**
     * @param target the target to set
     */
    public void setTarget(final String target) {
        this.target = target;
    }

    /**
     * @return the target
     */
    public String getTarget() {
        return this.target;
    }

    /**
     * @param predicateNs the predicateNs to set
     */
    public void setPredicateNs(final String predicateNs) {
        this.predicateNs = predicateNs;
    }

    /**
     * @return the predicateNs
     */
    public String getPredicateNs() {
        return this.predicateNs;
    }

}
