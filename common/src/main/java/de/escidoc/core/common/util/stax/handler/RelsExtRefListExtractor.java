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

package de.escidoc.core.common.util.stax.handler;

import de.escidoc.core.common.exceptions.application.invalid.InvalidContentException;
import de.escidoc.core.common.util.xml.stax.events.StartElement;
import de.escidoc.core.common.util.xml.stax.handler.DefaultHandler;

import javax.naming.directory.NoSuchAttributeException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RelsExtRefListExtractor extends DefaultHandler {

    private final Map<String, List<String>> entries;

    private final List<String> predicates;

    public RelsExtRefListExtractor(final List<String> predicates) {
        this.predicates = predicates;
        this.entries = new HashMap<String, List<String>>();
        for (final String predicate : this.predicates) {
            entries.put(predicate, new ArrayList<String>());
        }
    }

    @Override
    public StartElement startElement(final StartElement element) throws InvalidContentException {
        // {"http://www.w3.org/1999/02/22-rdf-syntax-ns#"}resource="info:fedora/escidoc:12108"
        final String ns = element.getNamespace();
        final String ln = element.getLocalName();
        final String curPredicate = ns + ln;
        if (predicates.contains(curPredicate)) {
            final String resource;
            try {
                resource = element.getAttribute("http://www.w3.org/1999/02/22-rdf-syntax-ns#", "resource").getValue();
            }
            catch (final NoSuchAttributeException e) {
                throw new InvalidContentException(e);
            }
            entries.get(curPredicate).add(resource.substring(resource.indexOf('/') + 1));
        }

        return element;
    }

    public Map<String, List<String>> getEntries() {
        return this.entries;
    }

}
