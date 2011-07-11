/*
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the
 * Common Development and Distribution License, Version 1.0 only
 * (the "License").  You may not use this file except in compliance
 * with the License.
 *
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE
 * or http://www.escidoc.de/license.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each
 * file and include the License file at license/ESCIDOC.LICENSE.
 * If applicable, add the following below this CDDL HEADER, with the
 * fields enclosed by brackets "[]" replaced with your own identifying
 * information: Portions Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
 */

/*
 * Copyright 2006-2008 Fachinformationszentrum Karlsruhe Gesellschaft
 * fuer wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Foerderung der Wissenschaft e.V.  
 * All rights reserved.  Use is subject to license terms.
 */
package de.escidoc.core.om.business.stax.handler;

import de.escidoc.core.common.business.fedora.TripleStoreUtility;
import de.escidoc.core.common.exceptions.application.missing.MissingElementValueException;
import de.escidoc.core.common.exceptions.application.notfound.ContentRelationNotFoundException;
import de.escidoc.core.common.exceptions.system.TripleStoreSystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.util.stax.StaxParser;
import de.escidoc.core.common.util.xml.XmlUtility;
import de.escidoc.core.common.util.xml.stax.events.EndElement;
import de.escidoc.core.common.util.xml.stax.events.StartElement;
import de.escidoc.core.common.util.xml.stax.handler.DefaultHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configurable
public class ContentRelationsRemoveHandler2Edition extends DefaultHandler {

    @Autowired
    @Qualifier("business.TripleStoreUtility")
    private TripleStoreUtility tripleStoreUtility;

    private final StaxParser parser;

    private boolean inRelation;

    private final String sourceId;

    private String targetId;

    private String predicate;

    private final List<Map<String, String>> relationsData = new ArrayList<Map<String, String>>();

    public ContentRelationsRemoveHandler2Edition(final StaxParser parser, final String id) {
        this.parser = parser;
        this.sourceId = id;

    }

    /**
     * Attention!
     *
     * @param data is implemented with side affects!
     */
    @Override
    public String characters(String data, final StartElement element) throws MissingElementValueException {

        if (this.inRelation) {
            if ("targetId".equals(element.getLocalName())) {
                if (data == null || data.length() == 0) {
                    throw new MissingElementValueException("The value of the element " + element.getLocalName()
                        + " is missing.");
                }
                data = XmlUtility.getObjidWithoutVersion(data);
                this.targetId = data;
            }
            else if ("predicate".equals(element.getLocalName())) {
                if (data == null || data.length() == 0) {
                    throw new MissingElementValueException("The value of the element " + element.getLocalName()
                        + " is missing.");
                }
                this.predicate = data;
            }
        }
        return data;
    }

    @Override
    public StartElement startElement(final StartElement element) {
        final String curPath = parser.getCurPath();

        if ("/param/relation".equals(curPath)) {
            this.inRelation = true;

        }

        return element;
    }

    @Override
    public EndElement endElement(final EndElement element) throws ContentRelationNotFoundException,
        TripleStoreSystemException, WebserverSystemException {
        if (this.inRelation && "relation".equals(element.getLocalName())) {
            final String[] splittedPredicate = splitPredicate(this.predicate);
            final String predicateNs = splittedPredicate[0];
            final String predicateValue = splittedPredicate[1];
            final String existRelationTarget = this.tripleStoreUtility.getRelation(this.sourceId, this.predicate);

            if (existRelationTarget == null) {
                throw new ContentRelationNotFoundException("A relation with predicate " + this.predicate
                    + " between resources with ids " + this.sourceId + " and " + this.targetId + " does not exist.");

            }

            final Map<String, String> relationData = new HashMap<String, String>();
            relationsData.add(relationData);
            relationData.put("predicateNs", predicateNs);
            relationData.put("predicateValue", predicateValue);
            relationData.put("target", this.targetId);
            this.targetId = null;
            this.predicate = null;
            this.inRelation = false;
        }
        return element;
    }

    /**
     * Returns a Vector with relations data.
     *
     * @return Relations Map
     */
    public List<Map<String, String>> getRelations() {
        return this.relationsData;
    }

    private static String[] splitPredicate(final String predicate) {
        int index = predicate.lastIndexOf('#');
        if (index < 0) {
            index = predicate.lastIndexOf('/');
        }
        final String[] result = new String[2];
        result[0] = predicate.substring(0, index + 1);
        result[1] = predicate.substring(index + 1);
        return result;
    }

}
