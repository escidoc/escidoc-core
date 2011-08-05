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

import javolution.util.FastMap;
import javolution.util.FastSet;
import javolution.util.FastTable;
import org.escidoc.core.util.collections.CollectionFactory;

import java.util.List;
import java.util.Map;
import java.util.Set;

final class JavalutionCollectionFactory extends CollectionFactory {

    /**
     * Package protected constructor to avoid instantion outside of this package.
     */
    JavalutionCollectionFactory() {
    }

    public <Element> List<Element> createList() {
        return new FastTable<Element>();
    }

    public <Element> List<Element> createList(final int initialCapacity) {
        return new FastTable<Element>(initialCapacity);
    }

    public <Key, Value> Map<Key, Value> createMap() {
        return new FastMap<Key, Value>();
    }

    public <Key, Value> Map<Key, Value> createMap(final int initialCapacity) {
        return new FastMap<Key, Value>(initialCapacity);
    }

    public <Element> Set<Element> createSet() {
        return new FastSet<Element>();
    }

    public <Element> Set<Element> createSet(final int initialCapacity) {
        return new FastSet<Element>(initialCapacity);
    }
}
