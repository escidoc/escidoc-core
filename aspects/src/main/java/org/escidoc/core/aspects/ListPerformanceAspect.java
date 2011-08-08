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


import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.escidoc.core.util.collections.CollectionFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

@Aspect
public final class ListPerformanceAspect {

    /*private static CollectionFactory collectionFactory = CollectionFactory.getInstance();

    /**
     * Package protected constructor to avoid instantion outside of this package.

    ListPerformanceAspect() {
    }

   @Around("call(public java.util.ArrayList.new())" + " && !within(org.escidoc.core.util.collections..*)" +
            "&& !within(org.escidoc.core.aspects.ListPerformanceAspect)" +
            "&& !within(org.slf4j..*)")
    public Object replaceArrayListWithDefaultContructor(final ProceedingJoinPoint joinPoint) throws Throwable {
        return new ArrayListWrapper(collectionFactory.createList());
    }

    @Around("call(public java.util.ArrayList.new(int))" + " && !within(org.escidoc.core.util.collections..*)" +
            "&& !within(org.escidoc.core.aspects.ListPerformanceAspect)" +
            "&& !within(org.slf4j.*)")
    public Object replaceArrayListWithCapacity(final JoinPoint joinPoint) throws Throwable {
        return new ArrayListWrapper(collectionFactory.createList((Integer) joinPoint.getArgs()[0]));
    }

    public static final class ArrayListWrapper<Element> extends ArrayList<Element> {

        private final List<Element> internalList;

        public ArrayListWrapper(List<Element> implementation) {
            this.internalList = implementation;
        }

        public boolean containsAll(Collection<?> c) {
            return internalList.containsAll(c);
        }

        public boolean addAll(final Collection<? extends Element> c) {
            return internalList.addAll(c);
        }

        public boolean addAll(final int index, final Collection<? extends Element> c) {
            return internalList.addAll(index, c);
        }

        public boolean removeAll(Collection<?> c) {
            return internalList.removeAll(c);
        }

        public boolean retainAll(Collection<?> c) {
            return internalList.retainAll(c);
        }

        public Iterator<Element> iterator() {
            return internalList.iterator();
        }

        public ListIterator<Element> listIterator() {
            return internalList.listIterator();
        }

        public ListIterator<Element> listIterator(int index) {
            return internalList.listIterator(index);
        }

        public List<Element> subList(int fromIndex, int toIndex) {
            return internalList.subList(fromIndex, toIndex);
        }

        public boolean equals(Object o) {
            return internalList.equals(o);
        }

        public int hashCode() {
            return internalList.hashCode();
        }

        public int size() {
            return internalList.size();
        }

        public boolean isEmpty() {
            return internalList.isEmpty();
        }

        public boolean contains(Object o) {
            return internalList.contains(o);
        }

        public int indexOf(Object o) {
            return internalList.indexOf(o);
        }

        public int lastIndexOf(Object o) {
            return internalList.lastIndexOf(o);
        }

        public Object[] toArray() {
            return internalList.toArray();
        }

        public <T> T[] toArray(T[] a) {
            return internalList.toArray(a);
        }

        public boolean add(final Element e) {
            return internalList.add(e);
        }

        public Element get(int index) {
            return internalList.get(index);
        }

        public Element set(final int index, final Element element) {
            return internalList.set(index, element);
        }

        public void add(final int index, final Element element) {
            internalList.add(index, element);
        }

        public Element remove(int index) {
            return internalList.remove(index);
        }

        public boolean remove(Object o) {
            return internalList.remove(o);
        }

        public void clear() {
            internalList.clear();
        }

        public String toString() {
            return internalList.toString();
        }

        public Object clone() {
            return null; // TODO
        }

        public void trimToSize() {
            // ignore this
        }

        public void ensureCapacity(int minCapacity) {
            // ignore this
        }
    }*/

}

