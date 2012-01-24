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
 * Copyright 2006-2012 Fachinformationszentrum Karlsruhe Gesellschaft
 * fuer wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Foerderung der Wissenschaft e.V.  
 * All rights reserved.  Use is subject to license terms.
 */
package de.escidoc.core.common.util.jaxrs;

import java.util.regex.Pattern;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import org.apache.cxf.jaxrs.ext.ResponseHandler;
import org.apache.cxf.jaxrs.model.OperationResourceInfo;
import org.apache.cxf.message.Message;

import de.escidoc.core.common.util.xml.XmlUtility;

/**
 * @author Michael Hoppe
 * 
 * Class inserts stylesheet-definition in xml response
 *
 */
@Provider
public class EscidocStylesheetResponseHandler implements ResponseHandler {

    private static final Pattern PATTERN_XML_HEADER = Pattern.compile("<\\?xml version=[^>]+\\?>");

    private static final Pattern XML_DOCUMENT_START_PATTERN = Pattern.compile("<?xml version=");

    private static final Pattern XML_DOCUMENT_START_XSLT_PATTERN =
        Pattern.compile("<?xml version=[^>]+<?xml-stylesheet ");

    /**
     * Insert stylesheet-definition if content-type=text/xml.
     *
     * @param outputMessage outputMessage.
     * @param invokedOperation invokedOperation.
     * @param response response.
     * @return Response replaced Response-Object.
     */
    public Response handleResponse(Message outputMessage, OperationResourceInfo invokedOperation, Response response) {
        String contentType = (String) outputMessage.get(Message.CONTENT_TYPE);
        String stylesheetDefinition = XmlUtility.getStylesheetDefinition();

        if (contentType.equals("text/xml") && stylesheetDefinition != null && !"".equals(stylesheetDefinition)) {
            String xml = (String) response.getEntity();
            if (!XML_DOCUMENT_START_PATTERN.matcher(xml).find()) {
                final StringBuilder ret = new StringBuilder(XmlUtility.DOCUMENT_START);
                ret.append(XmlUtility.getStylesheetDefinition());
                ret.append(xml);
                return Response.fromResponse(response).entity(ret.toString()).build();
            }
            else if (!XML_DOCUMENT_START_XSLT_PATTERN.matcher(xml).find()) {
                return Response.fromResponse(response).entity(
                    PATTERN_XML_HEADER.matcher(xml).replaceFirst(
                        XmlUtility.DOCUMENT_START + XmlUtility.getStylesheetDefinition())).build();
            }
        }
        return response;
    }
}
