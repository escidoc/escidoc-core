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

package de.escidoc.core.common.business.fedora;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Simple data structure to handle Triples.
 * 
 * @author Steffen Wagner
 */
public class Triples {

    private List<Triple> triples;

    public Triples() {
        this(new LinkedList<Triple>());
    }

    /**
     * Triples.
     * 
     * @param triples
     *            The list of triples.
     */
    public Triples(final List<Triple> triples) {
        this.triples = triples;
    }

    /**
     * Get list of Triples.
     * 
     * @return triples
     */
    public List<Triple> getTriples() {

        return this.triples;
    }

    /**
     * Set list of Triples.
     * 
     * @param triples
     *            The new triple list.
     */
    public void setTriples(final List<Triple> triples) {

        this.triples = triples;
    }

    /**
     * Add triple to list. The triple is added even if the same triple is already in the list.
     * 
     * @param triple
     *            New triple.
     */
    public void add(final Triple triple) {

        this.triples.add(triple);
    }

    /**
     * Delete the first equal entry of the list.
     * 
     * @param triple
     *            The triple which is to delete.
     */
    public void del(final Triple triple) {

        delTriple(triple, false);
    }

    /**
     * Delete all equal entry of the list.
     * 
     * @param triple
     *            The triple which is to delete.
     */
    public void delTriples(final Triple triple) {

        delTriple(triple, true);
    }

    /**
     * Clean the list from redundant entries.
     */
    public void distinct() {
        final List<Triple> newTripleList = new LinkedList<Triple>();
        for (final Triple triple : this.triples) {
            newTripleList.add(triple);
            delTriples(triple);
        }
        this.triples = newTripleList;
    }

    /**
     * Delete a Triple from the list.
     * 
     * @param triple
     *            The triple which is to delete.
     * @param firstHit
     *            Set true if only the first Triple which compares to the parameter is removed. False to remove all
     *            equal entries from the list (expensive operation for long list).
     */
    private void delTriple(final Triple triple, final boolean firstHit) {

        final Iterator<Triple> it = this.triples.iterator();

        while (it.hasNext()) {
            final Triple t = it.next();
            if (t.equals(triple)) {
                it.remove();
                if (firstHit) {
                    break;
                }
            }
        }
    }

}
