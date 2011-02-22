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
package de.escidoc.core.common.exceptions;

import de.escidoc.core.common.util.xml.XmlEscaper;

import javax.servlet.http.HttpServletResponse;

/**
 * EscidocExeption contains a HttpStatusCode and a HttpStatusMessage which
 * should be used in the REST interface. returned httpStatusCode is 500
 * (Internal Server Error)
 * 
 * @author FRS
 * 
 */
public abstract class EscidocException extends Exception {

    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = -4848570917974401296L;

    public static final int ESCIDOC_HTTP_SC_INVALID = 450;

    public static final int ESCIDOC_HTTP_SC_MISSING = 451;

    public static final int ESCIDOC_HTTP_SC_NOT_FOUND =
        HttpServletResponse.SC_NOT_FOUND;

    public static final int ESCIDOC_HTTP_SC_SECURITY =
        HttpServletResponse.SC_MOVED_TEMPORARILY;

    public static final int ESCIDOC_HTTP_SC_VIOLATED =
        HttpServletResponse.SC_CONFLICT;

    public static final int ESCIDOC_HTTP_SC_BAD_REQUEST =
        HttpServletResponse.SC_BAD_REQUEST;

    public static final int ESCIDOC_HTTP_SC_INTERNAL_SERVER_ERROR =
        HttpServletResponse.SC_INTERNAL_SERVER_ERROR;

    private int httpStatusCode = ESCIDOC_HTTP_SC_INTERNAL_SERVER_ERROR;

    private String httpStatusMsg = "Internal eSciDoc Error";

    /**
     * Returns the HttpStatusCode associated with this Exception.
     * 
     * @return The HttpStatusCode associated with this Exception.
     * @common
     */
    public int getHttpStatusCode() {
        return httpStatusCode;
    }

    /**
     * Returns a short message describing the HttpStatusCode associated with
     * this Exception.
     * 
     * @return The HttpStatusMessage associated with this Exception.
     * @common
     */
    public String getHttpStatusMsg() {
        return httpStatusMsg;
    }

    // CHECKSTYLE:JAVADOC-OFF

    /**
     * See Interface for functional description.
     * 
     * @return
     * @see java.lang.Throwable#toString()
     */
    public String toString() {
        return toXmlString();
    }

    // CHECKSTYLE:JAVADOC-ON

    /**
     * Returns a xml-representation of this Exception.
     * 
     * @return A xml-representation of this Exception.
     * @common
     */
    public String toXmlString() {

        return getXml(this);
    }

    /**
     * 
     * @return The HTTP status line.
     */
    public String getHttpStatusLine() {
        return String.valueOf(httpStatusCode) + ' ' + httpStatusMsg;
    }

    /**
     * Default constructor.
     * 
     * @common
     */
    public EscidocException() {
        super();
    }

    /**
     * Constructor used to create a new Exception with the specified detail
     * message and a mapping to an initial exception.
     * 
     * @param message -
     *            the detail message.
     * @param cause
     *            Throwable
     * @common
     */
    public EscidocException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new exception with the specified detail message.
     * 
     * @param message -
     *            the detail message.
     * @common
     */
    public EscidocException(final String message) {
        super(message);
    }

    /**
     * Constructor used to map an initial exception.
     * 
     * @param cause
     *            Throwable
     * @common
     */
    public EscidocException(final Throwable cause) {
        // FIXME: better solution insrtead of empty string needed
        super("", cause);
    }

    /**
     * Default constructor.
     * 
     * @param httpStatusCode
     *            the http status code
     * @param httpStatusMsg
     *            the http status message
     * @common
     */
    public EscidocException(final int httpStatusCode, final String httpStatusMsg) {
        super();
        this.httpStatusCode = httpStatusCode;
        this.httpStatusMsg = httpStatusMsg;
    }

    /**
     * Constructor used to create a new Exception with the specified detail
     * message and a mapping to an initial exception.
     * 
     * @param message
     *            the detail message.
     * @param cause
     *            Throwable
     * @param httpStatusCode
     *            the http status code
     * @param httpStatusMsg
     *            the http status message
     * @common
     */
    public EscidocException(final String message, final Throwable cause,
        final int httpStatusCode, final String httpStatusMsg) {
        super(message, cause);
        this.httpStatusCode = httpStatusCode;
        this.httpStatusMsg = httpStatusMsg;
    }

    /**
     * Constructs a new exception with the specified detail message.
     * 
     * @param message
     *            the detail message.
     * @param httpStatusCode
     *            the http status code
     * @param httpStatusMsg
     *            the http status message
     * @common
     */
    public EscidocException(final String message, final int httpStatusCode,
        final String httpStatusMsg) {
        super(message);
        this.httpStatusCode = httpStatusCode;
        this.httpStatusMsg = httpStatusMsg;
    }

    /**
     * Constructor used to map an initial exception.
     * 
     * @param cause
     *            Throwable
     * @param httpStatusCode
     *            the http status code
     * @param httpStatusMsg
     *            the http status message
     * @common
     */
    public EscidocException(final Throwable cause, final int httpStatusCode,
        final String httpStatusMsg) {
        // FIXME: better solution instead of empty string needed
        super("", cause);
        this.httpStatusCode = httpStatusCode;
        this.httpStatusMsg = httpStatusMsg;
    }

    /**
     * Gets the XML representation of the provided <code>Throwable</code>.
     * 
     * @param throwable
     *            The <code>Throwable</code> to get the XML representation
     *            for.
     * @return String xml-representation of this throwable
     * @common
     */
    public static String getXml(final Throwable throwable) {

        StringBuilder result = new StringBuilder("<exception>\n");

        // http status line, if any
        EscidocException escidocException = null;
        if (throwable instanceof EscidocException) {
            escidocException = (EscidocException) throwable;
            result.append("  <title><h1>");
            result.append(XmlEscaper.escapeTextContent(String.valueOf(escidocException.getHttpStatusCode())));
            result.append(' ');
            result.append(XmlEscaper.escapeTextContent(escidocException
                .getHttpStatusMsg()));
            result.append("</h1></title>\n");
        }

        // message
        final String throwableMessage = throwable.getMessage();
        if (throwableMessage != null) {
            result.append("  <message><p>");
            result.append(XmlEscaper.escapeTextContent(throwableMessage));
            result.append("</p></message>\n");
        }
        else {
            result.append("  <message/>");
        }

        // class name
        result.append("  <class><p>");
        result.append(throwable.getClass().getName());
        result.append("</p></class>\n");

        // stacktrace
        if (escidocException == null
            || hasHttpErrorCode(escidocException)) {

            result.append(getStackTraceXml(throwable));
        }

        // cause
        final Throwable throwableCause = throwable.getCause();
        if (throwableCause != null && throwableCause != throwable) {
            result.append("\n\n\n  <cause>");
            result.append(getXml(throwableCause));
            result.append("</cause>\n");
        }
        result.append("</exception>");

        return result.toString();
    }

    /**
     * Checks if the provided {@link EscidocException} holds an http error code.
     * 
     * @param e
     *            The {@link EscidocException} to check.
     * @return Returns <code>true</code> if the http status code of the
     *         exception equals or is larger than 400 (Bad Request).
     */
    private static boolean hasHttpErrorCode(final EscidocException e) {
        
        return e.getHttpStatusCode() >= HttpServletResponse.SC_BAD_REQUEST;
    }

    /**
     * Gets the stack trace of the provided Exception in an XML structure.
     * 
     * @param e
     *            The exception to get the stack trace from.
     * @return Returns the stack trace in the XML structure.
     * 
     * @common
     */
    public static String getStackTraceXml(final Throwable e) {

        StringBuilder result = new StringBuilder("  <stack-trace><p><![CDATA[\n");
        StackTraceElement[] elements = e.getStackTrace();
        for (StackTraceElement element : elements) {
            result.append("    ");
            result.append(element);
            result.append('\n');
        }
        result.append("]]></p></stack-trace>\n");
        return result.toString();
    }

}
