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
package de.escidoc.core.om.business.stax.handler.filter;

import de.escidoc.core.common.business.Constants;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.util.logger.AppLogger;
import de.escidoc.core.om.business.fedora.OntologyUtility;

import javax.xml.namespace.QName;
import javax.xml.stream.EventFilter;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

public class RDFRegisteredOntologyFilter implements EventFilter {

    private static final AppLogger log =
        new AppLogger(RDFRegisteredOntologyFilter.class.getName());

    private boolean inFilteredEvent = false;

    private boolean workaroundForItemList = false;

    public boolean accept(XMLEvent event) {
        if (event instanceof StartElement) {
            StartElement element = event.asStartElement();
            return accept(element);
        }
        else if (event instanceof EndElement) {
            EndElement element = event.asEndElement();
            if (workaroundForItemList
                && element.getName().getLocalPart().equalsIgnoreCase("RDF")) {
                return false;
            }
        }

        if (inFilteredEvent) {
            if (event instanceof EndElement) {
                inFilteredEvent = false;
            }
            return false;
        }

        return true;
    }

    public boolean accept(StartElement element) {
        try {
            if (workaroundForItemList
                && element.getName().getLocalPart().equalsIgnoreCase("RDF")) {
                return false;
            }
            if (element.getName().getLocalPart().equalsIgnoreCase("RDF")
                || element.getName().getLocalPart().equalsIgnoreCase(
                    "description")) {
                return true;
            }
            else {
                QName name = element.getName();
                String predicate = name.getNamespaceURI() + name.getLocalPart();
                if (OntologyUtility.checkPredicate(predicate)) {
                    return true;
                }
                // workaround for item list is to allow dc and some specials
                else if (workaroundForItemList && predicate != null
                    && predicate.startsWith(Constants.DC_NS_URI)) {
                    // dc elements are allowed
                    return true;
                }
                else if (workaroundForItemList
                    && (name.getLocalPart().equals("created-by-title")
                        || name.getLocalPart().equals("context-title")
                        || name.getLocalPart().equals("latest-version.date")
                        || name.getLocalPart().equals("latest-version.status")
                        || name.getLocalPart().equals("public-status") || name
                        .getLocalPart().equals("hasComponent"))) {
                    // allowed is: created-by-title, context-title,
                    // latest-version.date, latest-version.status,
                    // public-status, hasComponent
                    return true;
                }
                else {
                    inFilteredEvent = true;
                    return false;
                }
            }
        }
        catch (SystemException e) {
            log.error("Unhandled exception", e);
        }
        return false;
    }

    /**
     * @param workaroundForItemList
     *            the workaroundForItemList to set
     */
    public void setWorkaroundForItemList(boolean workaroundForItemList) {
        this.workaroundForItemList = workaroundForItemList;
    }
}
