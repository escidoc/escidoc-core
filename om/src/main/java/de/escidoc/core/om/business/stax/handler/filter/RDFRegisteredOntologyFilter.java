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
import de.escidoc.core.om.business.fedora.OntologyUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.namespace.QName;
import javax.xml.stream.EventFilter;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

public class RDFRegisteredOntologyFilter implements EventFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(RDFRegisteredOntologyFilter.class);

    private boolean inFilteredEvent;

    private boolean workaroundForItemList;

    @Override
    public boolean accept(final XMLEvent event) {
        if (event instanceof StartElement) {
            final StartElement element = event.asStartElement();
            return accept(element);
        }
        else if (event instanceof EndElement) {
            final EndElement element = event.asEndElement();
            if (this.workaroundForItemList && "RDF".equalsIgnoreCase(element.getName().getLocalPart())) {
                return false;
            }
        }

        if (this.inFilteredEvent) {
            if (event instanceof EndElement) {
                this.inFilteredEvent = false;
            }
            return false;
        }

        return true;
    }

    public boolean accept(final StartElement element) {
        try {
            if (this.workaroundForItemList && "RDF".equalsIgnoreCase(element.getName().getLocalPart())) {
                return false;
            }
            if ("RDF".equalsIgnoreCase(element.getName().getLocalPart())
                || "description".equalsIgnoreCase(element.getName().getLocalPart())) {
                return true;
            }
            else {
                final QName name = element.getName();
                final String predicate = name.getNamespaceURI() + name.getLocalPart();
                if (OntologyUtility.checkPredicate(predicate)
                    || this.workaroundForItemList
                    && predicate.startsWith(Constants.DC_NS_URI)
                    || this.workaroundForItemList
                    && ("created-by-title".equals(name.getLocalPart()) || "context-title".equals(name.getLocalPart())
                        || "latest-version.date".equals(name.getLocalPart())
                        || "latest-version.status".equals(name.getLocalPart())
                        || "public-status".equals(name.getLocalPart()) || "hasComponent".equals(name.getLocalPart()))) {
                    return true;
                }
                // workaround for item list is to allow dc and some specials
                else {
                    this.inFilteredEvent = true;
                    return false;
                }
            }
        }
        catch (final SystemException e) {
            LOGGER.error("Unhandled exception", e);
        }
        return false;
    }

    /**
     * @param workaroundForItemList the workaroundForItemList to set
     */
    public void setWorkaroundForItemList(final boolean workaroundForItemList) {
        this.workaroundForItemList = workaroundForItemList;
    }
}
