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
package org.escidoc.core.service.interceptors;

import java.util.Locale;
import java.util.regex.Pattern;

import javax.ws.rs.core.Response;

import org.apache.cxf.message.Message;
import org.apache.cxf.message.MessageContentsList;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;

import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.util.configuration.EscidocConfiguration;

/**
 * @author Michael Hoppe
 *
 */
public class StylesheetOutInterceptor extends AbstractPhaseInterceptor<Message> {

    private static final Pattern PATTERN_XML_HEADER = Pattern.compile("<\\?xml version=[^>]+\\?>");

    private static final Pattern XML_DOCUMENT_START_PATTERN = Pattern.compile("<?xml version=");

    private static final Pattern XML_DOCUMENT_START_XSLT_PATTERN =
        Pattern.compile("<?xml version=[^>]+<?xml-stylesheet ");
    
    public static final String CHARACTER_ENCODING = "UTF-8";

    private static final String XML_VERSION = "1.0";

    public static final String DOCUMENT_START =
        "<?xml version=\"" + XML_VERSION + "\" encoding=\"" + CHARACTER_ENCODING + "\"?>\n";

    private static String stylesheetDefinition;

    public StylesheetOutInterceptor() {
        super(Phase.MARSHAL);
    }

    public void handleMessage(Message message) {
        String contentType = (String) message.get(Message.CONTENT_TYPE);
        if (contentType != null && contentType.toLowerCase(Locale.ENGLISH).indexOf("text/xml") != -1) {
            MessageContentsList objs = MessageContentsList.getContentsList(message);
            if (objs != null && objs.size() == 1) {
                Response response;
                try {
                    response = (Response)objs.remove(0);
                }
                catch (ClassCastException e) {
                    return;
                }
                String xml = (String) response.getEntity();
                if (!XML_DOCUMENT_START_PATTERN.matcher(xml).find()) {
                    final StringBuilder ret = new StringBuilder(DOCUMENT_START);
                    ret.append(getStylesheetDefinition());
                    ret.append(xml);
                    response = Response.fromResponse(response).entity(ret.toString()).build();
                }
                else if (!XML_DOCUMENT_START_XSLT_PATTERN.matcher(xml).find()) {
                    response = Response.fromResponse(response).entity(
                        PATTERN_XML_HEADER.matcher(xml).replaceFirst(
                            DOCUMENT_START + getStylesheetDefinition())).build();
                }
                objs.add(response);
            }
        }
    }

    /**
     * Gets the stylesheet definition.
     *
     * @return Returns the stylesheet definition. This may be an empty string, if the xslt has not been defined with the
     *         eSciDoc configuration property escidoc.xslt.std.
     * @throws WebserverSystemException In case of an error.
     */
    private String getStylesheetDefinition() {
        if (stylesheetDefinition == null) {
            String xslt = EscidocConfiguration.getInstance().get(EscidocConfiguration.ESCIDOC_CORE_XSLT_STD);

            if (xslt == null || xslt.isEmpty()) {

                // FIXME a non-existing values should be null and not an empty string
                stylesheetDefinition = "";
            }
            else {
                // add baseurl, if xslt is not http or https protocol
                /*
                 * taking base url from configuration is actually an hacking, because the servlet context is missing in the
                 * deep of this methods.
                 */
                if (!(xslt.startsWith("http://") || xslt.startsWith("https://"))) {
                    String baseurl = EscidocConfiguration.getInstance().get(EscidocConfiguration.ESCIDOC_CORE_BASEURL);
                    if (!baseurl.endsWith("/")) {
                        baseurl += "/";
                    }

                    if (xslt.startsWith("./")) {
                        xslt = baseurl + xslt.substring(2, xslt.length());
                    }
                    else if (xslt.startsWith("/")) {
                        xslt = baseurl + xslt.substring(1, xslt.length());
                    }

                    stylesheetDefinition = "<?xml-stylesheet type=\"text/xsl\" href=\"" + xslt + "\"?>\n";
                }
                else {
                    stylesheetDefinition = xslt;
                }
            }
        }
        return stylesheetDefinition;
    }

}

