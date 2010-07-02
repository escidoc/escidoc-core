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
package de.escidoc.core.om.business.stax.handler.item;

import javax.naming.directory.NoSuchAttributeException;

import de.escidoc.core.common.business.Constants;
import de.escidoc.core.common.business.fedora.Utility;
import de.escidoc.core.common.business.fedora.resources.create.RelationCreate;
import de.escidoc.core.common.exceptions.application.invalid.InvalidContentException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.util.logger.AppLogger;
import de.escidoc.core.common.util.service.UserContext;
import de.escidoc.core.common.util.stax.StaxParser;
import de.escidoc.core.common.util.xml.Elements;
import de.escidoc.core.common.util.xml.XmlUtility;
import de.escidoc.core.common.util.xml.stax.events.EndElement;
import de.escidoc.core.common.util.xml.stax.events.StartElement;
import de.escidoc.core.common.util.xml.stax.handler.DefaultHandler;

/**
 * Obtains Content Relations values from XML.
 * 
 * @author SWA
 * 
 */
public class RelationHandler2 extends DefaultHandler {

    private StaxParser parser;

    private String relationXPath = "//relation";

    private RelationCreate relation = null;

    private static AppLogger log =
        new AppLogger(RelationHandler2.class.getName());

    /**
     * 
     * @param parser
     *            StAX parser.
     */
    public RelationHandler2(final StaxParser parser) {

        this.parser = parser;
    }

    /**
     * 
     * @param parser
     *            StAX parser.
     * @param path
     *            XPath for relations
     */
    public RelationHandler2(final StaxParser parser, final String path) {

        this.parser = parser;
        this.relationXPath = path;
    }

    /**
     * @param element
     *            StAX StartElement.
     * @return StAX StartElement.
     * @throws InvalidContentException
     *             Thrown if the content relation attribute for predicate has
     *             wrong structure.
     * @throws WebserverSystemException
     *             Thrown if check of transport protocol failed.
     */
    @Override
    public StartElement startElement(final StartElement element)
        throws InvalidContentException, WebserverSystemException {

        if (this.relationXPath.equals(parser.getCurPath())
            && element
            .indexOfAttribute(null, "inherited") < 0) {

            String predicateNs = null;
            String predicate = null;

            try {
                String predicateUri =
                    element.getAttributeValue(null,
                        Elements.ATTRIBUTE_PREDICATE);

                if (predicateUri != null) {
                    String[] predicateAndTarget = predicateUri.split("#");
                    if (predicateAndTarget.length != 2) {
                        String msg = "Attribute has invalid predicate";
                        log.debug(msg + " '" + predicateUri + "'");
                        throw new InvalidContentException(msg);
                    }

                    predicateNs = predicateAndTarget[0];
                    predicate = predicateAndTarget[1];
                }

            }
            catch (NoSuchAttributeException e) {
                log.debug(e);
            }

            String id = null;
            try {
                // REST
                if (UserContext.isRestAccess()) {
                    String href =
                        element.getAttributeValue(Constants.XLINK_NS_URI,
                            Elements.ATTRIBUTE_XLINK_HREF);
                    id = Utility.getId(href);
                }
                // SOAP
                else {
                    id =
                        element.getAttributeValue(null,
                            Elements.ATTRIBUTE_XLINK_OBJID);

                }
            }
            catch (NoSuchAttributeException e) {
                log.debug(e);
            }

            // handle objid
            if (XmlUtility.getVersionNumberFromObjid(id) != null) {
                String message =
                    "A relation target may not be referenced by an "
                        + " identifier containing a version number. "
                        + "Use a floating identifier like 'escidoc:123' "
                        + "to reference a target";
                log.debug(message);
                throw new InvalidContentException(message);
            }

            this.relation = new RelationCreate(predicateNs, predicate, id);

        }
        return element;
    }

    /**
     * @param element
     *            StAX EndElement
     * @return StAX EndElement
     */
    @Override
    public EndElement endElement(final EndElement element) {

        return element;
    }

    /**
     * @param relation
     *            the relation to set
     */
    public void setRelation(final RelationCreate relation) {
        this.relation = relation;
    }

    /**
     * @return the relation
     */
    public RelationCreate getRelation() {
        return relation;
    }

}
