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
package de.escidoc.core.aa.business.authorisation;

import com.sun.xacml.ctx.Status;
import de.escidoc.core.common.business.aa.authorisation.AttributeIds;
import de.escidoc.core.common.exceptions.EscidocException;
import de.escidoc.core.common.exceptions.application.notfound.ResourceNotFoundException;
import de.escidoc.core.common.util.xml.XmlUtility;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class to create XACML statuses used in policy results or evaluation
 * results.
 * 
 * @author TTE
 * 
 */
public final class CustomStatusBuilder {

    private static final Pattern PATTERN_CDATA_END =
        Pattern.compile(XmlUtility.CDATA_END);

    /**
     * Private constructor to prevent instantiation.
     * 
     * @aa
     */
    private CustomStatusBuilder() {
    }

    /**
     * Creates an <code>Status</code> object holding information about an
     * error.<br>
     * This can be used to tunnel an exception through the XACML engine.<br>
     * If the provided exception is not an <code>EscidocException</code>, it
     * is wrapped by a <code>WebserverSystemException .</code><br>
     * The created error status holds a status code with the eSciDoc status code
     * prefix and the name of the exception.
     * 
     * @param e
     *            The <code>Exception</code> that caused this error result.
     * @return Returns the created <code>EvaluationResult</code> object.
     * @aa
     */
    public static Status createErrorStatus(final Exception e) {

        return createErrorStatus(
            AttributeIds.STATUS_PREFIX
                + e.getClass().getName(), e);
    }

    /**
     * Creates an <code>Status</code> object holding information about an
     * error.<br>
     * This can be used to tunnel an exception through the XACML engine.<br>
     * If the provided exception is not an <code>EscidocException</code>, it
     * is wrapped by a <code>WebserverSystemException .</code>
     * 
     * @param status
     *            The status code, one of the codes defined in the class
     *            <code>com.sun.xacml.ctx.Status</code>.
     * @param e
     *            The <code>Exception</code> that caused this error result.
     * @return Returns the created <code>EvaluationResult</code> object.
     * @aa
     */
    public static Status createErrorStatus(
        final String status, final Exception e) {

        List<String> codeList = new ArrayList<String>();
        codeList.add(status);
        String message = e.getMessage();
        if (message == null) {
            message = e.getClass().getName();
        }
        if (e instanceof EscidocException) {
            try {
                StringBuilder errorMsg = new StringBuilder(message);
                errorMsg.append('\n');
                errorMsg.append(XmlUtility.CDATA_START);
                errorMsg
                    .append(quoteCdata(((EscidocException) e).toXmlString()));
                errorMsg.append(XmlUtility.CDATA_END);
                return new Status(codeList, errorMsg.toString());
            }
            catch (Exception e1) {
                StringBuilder errorMsg = new StringBuilder(message);
                errorMsg
                    .append(quoteCdata(((EscidocException) e).toXmlString()));
                errorMsg.append("\n\nException deserializing failed due to ");
                errorMsg.append(e1);
                return new Status(codeList, errorMsg.toString());
            }
        }
        else {
            try {
                StringBuilder errorMsg = new StringBuilder(message);
                errorMsg.append("\n<exception>");
                errorMsg
                    .append(quoteCdata(EscidocException.getStackTraceXml(e)));
                errorMsg.append("</exception>");
                return new Status(codeList, errorMsg.toString());
            }
            catch (Exception e1) {
                StringBuilder errorMsg = new StringBuilder(message);
                errorMsg.append('\n');
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                e.printStackTrace(pw);
                errorMsg.append(sw.toString());
                errorMsg.append("\n\nException deserializing failed due to ");
                errorMsg.append(e1);
                try {
                    sw.close();
                }
                catch (IOException e2) {
                    // Ignore exceptions
                }
                return new Status(codeList, errorMsg.toString());
            }
        }
    }

    /**
     * Quotes the CDATA end "]]>" in the provided string.
     * 
     * @param str
     *            The string to quote
     * @return Returns the provided string with "]]>" replaced by "]]&gt;"
     * @aa
     */
    private static String quoteCdata(final CharSequence str) {

        Pattern pattern = PATTERN_CDATA_END;
        Matcher matcher = pattern.matcher(str);
        return matcher.replaceAll(XmlUtility.CDATA_END_QUOTED);
    }

    /**
     * Gets the status code for the provided exception class.
     * 
     * @param exception
     *            The resource not found exception. If tis is <code>null</code>,
     *            the <code>ResourceNotFoundException</code> is used.
     * @return Returns the error status code in case of a resource not found
     *         exception.
     */
    public static String getResourceNotFoundStatusCode(
        final ResourceNotFoundException exception) {

        if (exception == null) {
            return AttributeIds.STATUS_PREFIX
                + ResourceNotFoundException.class.getName();
        }
        else {
            return AttributeIds.STATUS_PREFIX
                + exception.getClass().getName();
        }
    }
}
