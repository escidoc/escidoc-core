/*
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the Common Development and Distribution License, Version 1.0
 * only (the "License"). You may not use this file except in compliance with the License.
 *
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE or http://www.escidoc.de/license. See the License
 * for the specific language governing permissions and limitations under the License.
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

package org.escidoc.core.aspects;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.escidoc.core.util.collections.CollectionFactory;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

@Aspect
public final class SetPerformanceAspect {

    /*private CollectionFactory collectionFactory = CollectionFactory.getInstance();

    /**
     * Package protected constructor to avoid instantion outside of this package.

    SetPerformanceAspect() {
    }

    @Around("call(public java.util.HashSet.new())" + " && !within(org.escidoc.core.util.collections..*)" +
            "&& !within(org.escidoc.core.aspects.SetPerformanceAspect)")
    public Object replaceHashSet(final ProceedingJoinPoint joinPoint) throws Throwable {
        return new HashSetWrapper(collectionFactory.createSet());
    }

    public static final class HashSetWrapper<Element> extends HashSet<Element> {

        private final Set<Element> internalSet;

        public HashSetWrapper(Set<Element> implementation) {
            internalSet = implementation;
        }

        public Object[] toArray() {
            return internalSet.toArray();
        }

        public <T> T[] toArray(final T[] a) {
            return internalSet.toArray(a);
        }

        public boolean add(final Element o) {
            return internalSet.add(o);
        }

        public boolean remove(final Object o) {
            return internalSet.remove(o);
        }

        public boolean containsAll(Collection<?> c) {
            return internalSet.containsAll(c);
        }

        public boolean addAll(final Collection c) {
            return internalSet.addAll(c);
        }

        public boolean retainAll(Collection<?> c) {
            return internalSet.retainAll(c);
        }

        public boolean equals(Object o) {
            return internalSet.equals(o);
        }

        public int hashCode() {
            return internalSet.hashCode();
        }

        public boolean removeAll(Collection<?> c) {
            return internalSet.removeAll(c);
        }

        public Iterator<Element> iterator() {
            return internalSet.iterator();
        }

        public int size() {
            return internalSet.size();
        }

        public boolean isEmpty() {
            return internalSet.isEmpty();
        }

        public boolean contains(final Object o) {
            return internalSet.contains(o);
        }

        public void clear() {
            internalSet.clear();
        }

        public String toString() {
            return internalSet.toString();
        }
    }*/

}

