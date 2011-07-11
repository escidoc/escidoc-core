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

import de.escidoc.core.common.business.Constants;
import de.escidoc.core.common.business.fedora.TripleStoreUtility;
import de.escidoc.core.common.exceptions.application.invalid.InvalidContentException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidXmlException;
import de.escidoc.core.common.exceptions.application.missing.MissingElementValueException;
import de.escidoc.core.common.exceptions.application.notfound.ReferencedResourceNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.RelationPredicateNotFoundException;
import de.escidoc.core.common.exceptions.application.violated.AlreadyExistsException;
import de.escidoc.core.common.exceptions.system.EncodingSystemException;
import de.escidoc.core.common.exceptions.system.TripleStoreSystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.exceptions.system.XmlParserSystemException;
import de.escidoc.core.common.util.stax.StaxParser;
import de.escidoc.core.common.util.xml.XmlUtility;
import de.escidoc.core.common.util.xml.stax.events.EndElement;
import de.escidoc.core.common.util.xml.stax.events.StartElement;
import de.escidoc.core.common.util.xml.stax.handler.DefaultHandler;
import de.escidoc.core.om.business.fedora.ContentRelationsUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configurable
public class ContentRelationsAddHandler2Edition extends DefaultHandler {

    @Autowired
    @Qualifier("business.TripleStoreUtility")
    private TripleStoreUtility tripleStoreUtility;

    private final StaxParser parser;

    private boolean inRelation;

    private final String sourceId;

    private String targetId;

    private String predicate;

    private final List<Map<String, String>> relationsData = new ArrayList<Map<String, String>>();

    private final Collection<String> relationsDataCheck = new ArrayList<String>();

    public ContentRelationsAddHandler2Edition(final StaxParser parser, final String id) {
        this.parser = parser;
        this.sourceId = id;

    }

    @Override
    public String characters(final String data, final StartElement element) throws MissingElementValueException,
        ReferencedResourceNotFoundException, RelationPredicateNotFoundException, InvalidContentException,
        TripleStoreSystemException, WebserverSystemException, XmlParserSystemException, EncodingSystemException,
        InvalidXmlException {

        if (this.inRelation) {
            if ("targetId".equals(element.getLocalName())) {
                if (data == null || data.length() == 0) {
                    throw new MissingElementValueException("The value of the element " + element.getLocalName()
                        + " is missing.");
                }

                this.targetId = data;
                final String targetIdWithoutVersion = XmlUtility.getObjidWithoutVersion(this.targetId);
                String targetVersion = targetId.replaceFirst(targetIdWithoutVersion, "");
                if (targetVersion.length() > 0) {
                    targetVersion = targetVersion.substring(1);
                    throw new InvalidContentException("A relation target may not be referenced by an "
                        + " identifier containing a version number. Use a floating "
                        + "identifier like 'escidoc:123' to reference a target");
                }
                if (!this.tripleStoreUtility.exists(this.targetId)) {
                    throw new ReferencedResourceNotFoundException("Referenced target resource with id " + this.targetId
                        + " does not exist.");
                }
                final String targetObjectType = this.tripleStoreUtility.getObjectType(this.targetId);

                if (!Constants.ITEM_OBJECT_TYPE.equals(targetObjectType)
                    && !Constants.CONTAINER_OBJECT_TYPE.equals(targetObjectType)) {
                    throw new InvalidContentException("A related resource has to be either 'item' or 'container'. "
                        + "A object with id " + this.targetId + " is neither 'item' nor 'container' ");
                }
            }
            else if ("predicate".equals(element.getLocalName())) {
                if (data == null || data.length() == 0) {
                    throw new MissingElementValueException("The value of the element " + element.getLocalName()
                        + " is missing.");
                }
                this.predicate = data;
                if (!ContentRelationsUtility.validPredicate(data)) {
                    throw new RelationPredicateNotFoundException("Predicate " + data + " is wrong. ");
                }
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
    public EndElement endElement(final EndElement element) throws AlreadyExistsException, TripleStoreSystemException,
        WebserverSystemException {
        if (this.inRelation && "relation".equals(element.getLocalName())) {
            final String[] splittedPredicate = splitPredicate(this.predicate);
            final String predicateNs = splittedPredicate[0];
            final String predicateValue = splittedPredicate[1];
            final String existRelationTarget = this.tripleStoreUtility.getRelation(this.sourceId, this.predicate);
            if (existRelationTarget != null && existRelationTarget.equals(this.targetId)) {
                throw new AlreadyExistsException("A relation with predicate " + this.predicate
                    + " between resources with ids " + this.sourceId + " and " + this.targetId + " already exists.");
            }
            final String relationDataCheck = this.predicate + "###" + this.targetId;
            if (!relationsDataCheck.contains(relationDataCheck)) {
                relationsDataCheck.add(relationDataCheck);

                final Map<String, String> relationData = new HashMap<String, String>();
                relationsData.add(relationData);
                relationData.put("predicateNs", predicateNs);
                relationData.put("predicateValue", predicateValue);
                relationData.put("target", this.targetId);
                this.targetId = null;
                this.predicate = null;
                this.inRelation = false;
            }
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
