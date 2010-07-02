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
package de.escidoc.core.om.business.stax.handler.context;

import javax.naming.directory.NoSuchAttributeException;

import de.escidoc.core.common.exceptions.application.violated.ReadonlyAttributeViolationException;
import de.escidoc.core.common.exceptions.application.violated.ReadonlyElementViolationException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.util.service.UserContext;
import de.escidoc.core.common.util.stax.StaxParser;
import de.escidoc.core.common.util.xml.stax.events.EndElement;
import de.escidoc.core.common.util.xml.stax.events.StartElement;
import de.escidoc.core.common.util.xml.stax.handler.DefaultHandler;

public class ContextUpdateHandler extends DefaultHandler {

    private StaxParser parser = null;

    private String contextPath = null;

    private String contextId = null;

    private String objid = null;

    public ContextUpdateHandler(String contextId, StaxParser parser) {
        this.contextId = contextId;
        this.contextPath = "/context";
        this.parser = parser;
    }

    public StartElement startElement(StartElement element)
        throws ReadonlyElementViolationException,
        ReadonlyAttributeViolationException, SystemException {
        String curPath = parser.getCurPath();

        if (curPath.equals(contextPath)) {

            try {

                if (UserContext.isRestAccess()) {
                    String xlinkHref =
                        element.getAttribute(
                            de.escidoc.core.common.business.Constants.XLINK_URI,
                            "href").getValue();
                    if (xlinkHref == null
                        || !xlinkHref.equals("/ir/context/" + contextId)) {
                        throw new ReadonlyAttributeViolationException(
                            "Context href is not '/ir/context/" + contextId);
                    }
                    // objid =
                    // element.getAttribute(de.escidoc.core.common.business.Constants.XLINK_URI,
                    // "href").getValue();
                    // objid = objid.replace("/ir/context/", "");
                }
                else {
                    String objid =
                        element.getAttribute(null, "objid").getValue();
                    if (objid == null || !objid.equals(contextId)) {
                        throw new ReadonlyAttributeViolationException(
                            "objid is readonly");
                    }
                }

                // String xlinkTitle =
                // element.getAttribute(de.escidoc.core.common.business.Constants.XLINK_URI,
                // "title").getValue();
                //
                // if (xlinkTitle == null
                // ||
                // !xlinkTitle.equals(TripleStoreUtility.getInstance().getTitle(contextId)))
                // {
                // throw new ReadonlyAttributeViolationException(
                // "Properties title is not 'Properties'.");
                // }

            }
            catch (NoSuchAttributeException e) {
                throw new ReadonlyAttributeViolationException(e);
            }

        }

        return element;

    }

    public EndElement endElement(EndElement element) throws Exception {

        return element;
    }

    public String characters(String data, StartElement element)
        throws Exception {

        return data;
    }

}
