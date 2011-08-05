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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

final class JdkCollectionFactory extends CollectionFactory {

    /**
     * Package protected constructor to avoid instantion outside of this package.
     */
    JdkCollectionFactory() {
    }

    public <Element> List<Element> createList() {
        return new ArrayList<Element>();
    }

    public <Element> List<Element> createList(final int initialCapacity) {
        return new ArrayList<Element>(initialCapacity);
    }

    public <Key, Value> Map<Key, Value> createMap() {
        return new LinkedHashMap<Key, Value>();
    }

    public <Key, Value> Map<Key, Value> createMap(final int initialCapacity) {
        return new LinkedHashMap<Key, Value>(initialCapacity);
    }

    public <Element> Set<Element> createSet() {
        return new LinkedHashSet<Element>();
    }

    public <Element> Set<Element> createSet(final int initialCapacity) {
        return new LinkedHashSet<Element>(initialCapacity);
    }
}
