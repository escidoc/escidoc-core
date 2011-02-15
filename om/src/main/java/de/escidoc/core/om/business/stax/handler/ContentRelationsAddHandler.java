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
import de.escidoc.core.common.exceptions.application.invalid.InvalidContentException;
import de.escidoc.core.common.exceptions.application.missing.MissingElementValueException;
import de.escidoc.core.common.exceptions.application.notfound.ReferencedResourceNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.RelationPredicateNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ResourceNotFoundException;
import de.escidoc.core.common.exceptions.application.violated.ReadonlyAttributeViolationException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.util.logger.AppLogger;
import de.escidoc.core.common.util.stax.StaxParser;
import de.escidoc.core.common.util.xml.XmlUtility;
import de.escidoc.core.common.util.xml.stax.events.EndElement;
import de.escidoc.core.common.util.xml.stax.events.StartElement;
import de.escidoc.core.common.util.xml.stax.handler.DefaultHandler;
import de.escidoc.core.om.business.fedora.OntologyUtility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class ContentRelationsAddHandler extends DefaultHandler {

    private StaxParser parser = null;

    private boolean inRelation = false;

    private String targetId = null;

    private String targetIdWithoutVersion = null;

    private String targetVersion = null;

    private String predicate = null;

    List<Map> relationsData = new ArrayList<Map>();

    private static AppLogger log =
        new AppLogger(ContentRelationsAddHandler.class.getName());

    public ContentRelationsAddHandler(StaxParser parser) {
        this.parser = parser;

    }

    public String characters(String data, StartElement element)
        throws MissingElementValueException {

        if (inRelation) {
            if (element.getLocalName().equals("targetId")) {
                if ((data == null) || (data.length() == 0)) {
                    String message =
                        "The value of the element " + element.getLocalName()
                            + " is missing.";
                    log.error(message);
                    throw new MissingElementValueException(message);
                }
                this.targetId = data;
            }
            else if (element.getLocalName().equals("predicate")) {
                if ((data == null) || (data.length() == 0)) {
                    String message =
                        "The value of the element " + element.getLocalName()
                            + " is missing.";
                    log.error(message);
                    throw new MissingElementValueException(message);
                }
                this.predicate = data;

            }
        }

        return data;
    }

    public StartElement startElement(StartElement element)
        throws InvalidContentException, ReadonlyAttributeViolationException {
        String curPath = parser.getCurPath();

        if (curPath.equals("/param/relation")) {
            inRelation = true;
            int indexOfObjId = element.indexOfAttribute(null, "objid");
            if (indexOfObjId != (-1)) {
                String message =
                    "Attribite objid of the element " + element.getLocalName()
                        + " may not exist while adding" + " a new relation";
                log.error(message);
                throw new ReadonlyAttributeViolationException(message);
            }

        }

        return element;
    }

    public EndElement endElement(EndElement element)
        throws ReferencedResourceNotFoundException,
        RelationPredicateNotFoundException, SystemException {
        if (inRelation) {
            if (element.getLocalName().equals("targetId")) {
                checkRefElement();
            }
            else if (element.getLocalName().equals("predicate")) {
                if (!OntologyUtility.checkPredicate(this.predicate)) {
                    String message =
                        "Predicate " + this.predicate + " is wrong. ";
                    log.error(message);
                    throw new RelationPredicateNotFoundException(message);

                }
            }
            else if (element.getLocalName().equals("relation")) {
                HashMap relationData = new HashMap();
                relationsData.add(relationData);
                relationData.put("predicate", predicate);
                relationData.put("target", targetIdWithoutVersion);
                relationData.put("targetVersion", targetVersion);
                targetId = null;
                targetIdWithoutVersion = null;
                targetVersion = null;
                predicate = null;
                inRelation = false;
            }
        }
        return element;
    }

    /**
     * Returns a Vector with relations data.
     * 
     * @return Relations Map
     */
    public List getRelations() {
        return relationsData;
    }

    /**
     * 
     * @throws SystemException
     * @throws ResourceNotFoundException
     */
    private void checkRefElement() throws ReferencedResourceNotFoundException,
        SystemException {

        targetIdWithoutVersion = XmlUtility.getObjidWithoutVersion(targetId);
        targetVersion = targetId.replaceFirst(targetIdWithoutVersion, "");
        if (targetVersion.length() > 0) {
            targetVersion = targetVersion.substring(1);
        }
        else {
            targetVersion = null;
        }

        if (!TripleStoreUtility.getInstance().exists(targetIdWithoutVersion)) {
            String message =
                "Referenced target resource with id " + targetIdWithoutVersion
                    + " does not exist.";
            log.error(message);
            throw new ReferencedResourceNotFoundException(message);
        }
        if (targetVersion != null) {
            String targetLatestVersion = 
                    TripleStoreUtility
                        .getInstance()
                        .getPropertiesElements(
                            targetIdWithoutVersion,
                            TripleStoreUtility.PROP_LATEST_VERSION_NUMBER);
            
            if (Integer.parseInt(targetVersion) > Integer
                .parseInt(targetLatestVersion)) {
                String message =
                    "Referenced target resource with id "
                        + targetIdWithoutVersion + ":" + targetVersion
                        + " does not exist.";
                log.error(message);
                throw new ReferencedResourceNotFoundException(message);
            }
        }
    }

}
