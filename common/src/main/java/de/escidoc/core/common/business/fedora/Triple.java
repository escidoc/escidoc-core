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

/**
 * Simple data structure for a Triple. Subject, Predicate and Object are simply handles as Strings.
 *
 * @author Steffen Wagner
 */
public class Triple {
    /*
     * org.nsdl.mptstore.rdf.Triple is a more complex and quite nice Triple
     * class (with currently not fits within the framework).
     */

    private String subject;

    private String predicate;

    private String object;

    /**
     * Triple.
     */
    public Triple() {

    }

    /**
     * Triple.
     *
     * @param subject   The subject.
     * @param predicate The predicate.
     * @param object    The object.
     */
    public Triple(final String subject, final String predicate, final String object) {

        this.setSubject(subject);
        this.setPredicate(predicate);
        this.setObject(object);
    }

    /**
     * @param subject the subject to set
     */
    public final void setSubject(final String subject) {
        this.subject = subject;
    }

    /**
     * @return the subject
     */
    public String getSubject() {
        return this.subject;
    }

    /**
     * @param predicate the predicate to set
     */
    public final void setPredicate(final String predicate) {
        this.predicate = predicate;
    }

    /**
     * @return the predicate
     */
    public String getPredicate() {
        return this.predicate;
    }

    /**
     * @param object the object to set
     */
    public final void setObject(final String object) {
        this.object = object;
    }

    /**
     * @return the object
     */
    public String getObject() {
        return this.object;
    }

    /**
     * Get a string representation of this object.
     *
     * @return string representation of this object
     */
    public String toString() {
        return "[subject=" + this.subject + ", predicate=" + this.predicate + ", object=" + this.object + ']';
    }
}
