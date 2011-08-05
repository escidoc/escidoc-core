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
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Aspect
public final class MapPerformanceAspect {

    /*private CollectionFactory collectionFactory = CollectionFactory.getInstance();

    /**
     * Protected constructor to avoid instantion outside of this package.

    MapPerformanceAspect() {
    }

    @Around("call(public java.util.HashMap.new())" + " && !within(org.escidoc.core.util.collections..*)" +
            "&& !within(org.escidoc.core.aspects.MapPerformanceAspect)")
    public Object replaceHashMap(final ProceedingJoinPoint joinPoint) throws Throwable {
        return new HashMapWrapper(collectionFactory.createMap());
    }

    public final class HashMapWrapper<Key, Value> extends HashMap<Key, Value> {

        private final Map<Key, Value> internalMap;

        public HashMapWrapper(Map<Key, Value> implementation) {
            internalMap = implementation;
        }

        public boolean equals(Object o) {
            return internalMap.equals(o);
        }

        public int hashCode() {
            return internalMap.hashCode();
        }

        public int size() {
            return internalMap.size();
        }

        public boolean isEmpty() {
            return internalMap.isEmpty();
        }

        public Value get(Object key) {
            return internalMap.get(key);
        }

        public Value put(final Key key, final Value value) {
            return internalMap.put(key, value);
        }

        public boolean containsKey(Object key) {
            return internalMap.containsKey(key);
        }

        public Value remove(Object key) {
            return internalMap.remove(key);
        }

        public void putAll(final Map<? extends Key, ? extends Value> m) {
            internalMap.putAll(m);
        }

        public void clear() {
            internalMap.clear();
        }

        public boolean containsValue(Object value) {
            return internalMap.containsValue(value);
        }

        public Set<Key> keySet() {
            return internalMap.keySet();
        }

        public Collection<Value> values() {
            return internalMap.values();
        }

        public Set<Map.Entry<Key, Value>> entrySet() {
            return internalMap.entrySet();
        }

        public String toString() {
            return internalMap.toString();
        }
    }*/

}

