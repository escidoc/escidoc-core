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

package org.escidoc.core.util.collections.internal;

import org.escidoc.core.util.collections.CollectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Set;

public final class ReflectionsCollectionFactory extends CollectionFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(CollectionFactory.class);

    private static final CollectionFactory COLLECTION_FACTORY = getCollectionFactory();

    /**
     * Package protected constructor to avoid instantion outside of this package.
     */
    public ReflectionsCollectionFactory() {
    }

    private static CollectionFactory getCollectionFactory() {
        if(isClassPresent("javolution.util.FastMap") && isClassPresent("javolution.util.FastSet") &&
                isClassPresent("javolution.util.FastTable")) {
            if(LOGGER.isInfoEnabled()) {
                LOGGER.info("Javolution collection classes are available.");
            }
            return new JavalutionCollectionFactory();
        } else { // use JDK collection classes by default
            return new JdkCollectionFactory();
        }
    }

    private static boolean isClassPresent(final String className) {
        try {
            Class.forName(className);
            return true;
        } catch(final ClassNotFoundException e) {
            return false;
        }
    }

    @Override
    public <Element> List<Element> createList() {
        return COLLECTION_FACTORY.createList();
    }

    @Override
    public <Element> List<Element> createList(final int initialCapacity) {
        return COLLECTION_FACTORY.createList(initialCapacity);
    }

    @Override
    public <Key, Value> Map<Key, Value> createMap() {
        return COLLECTION_FACTORY.createMap();
    }

    @Override
    public <Key, Value> Map<Key, Value> createMap(final int initialCapacity) {
        return COLLECTION_FACTORY.createMap(initialCapacity);
    }

    @Override
    public <Element> Set<Element> createSet() {
        return COLLECTION_FACTORY.createSet();
    }

    @Override
    public <Element> Set<Element> createSet(final int initialCapacity) {
        return COLLECTION_FACTORY.createSet(initialCapacity);
    }
}

