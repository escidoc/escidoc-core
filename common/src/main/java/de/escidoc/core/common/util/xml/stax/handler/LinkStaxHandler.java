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
package de.escidoc.core.common.util.xml.stax.handler;

import java.lang.reflect.Constructor;

import de.escidoc.core.common.business.Constants;
import de.escidoc.core.common.exceptions.EscidocException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.util.service.UserContext;
import de.escidoc.core.common.util.string.StringUtility;
import de.escidoc.core.common.util.xml.XmlUtility;
import de.escidoc.core.common.util.xml.stax.events.EndElement;
import de.escidoc.core.common.util.xml.stax.events.StartElement;

/**
 * Stax handler that manages a link element that refers to another eSciDoc
 * resource object.<br>
 * In case of a REST access, a link consists of the xlink attributes referencing
 * the resource object. The xlink:type attribute is "simple". In common, the
 * xlink:href consists of a base uri (e.g. /ir/item/) followed by an id (e.g.
 * escidoc:12345). The expected base uri can be specified during creation of the
 * {@link LinkStaxHandler} instance. This value will be used to validate the
 * provided href.<br>
 * In case of a SOAP access, a link consists of the objid referencing the
 * resource object.
 * 
 * @author TTE
 * @common
 */
public class LinkStaxHandler extends DefaultHandler {

    /**
     * Error message in case of base uri mismatch of href provided in REST
     * request.
     */
    private static final String MSG_WRONG_BASE_URI =
        StringUtility.concatenateToString(
            "Reference does not point to a resource of the expected type,",
            " base uri mismatch");

    private String href;

    private String objid;

    private final String elementPath;

    private final String parentPath;

    private String hrefBaseUri;

    private Class exceptionClass;

    /**
     * The constructor.
     * 
     * @param elementPath
     *            The path to the link element that shall be handled by this
     *            handler.
     * @common
     */
    public LinkStaxHandler(final String elementPath) {

        super();
        this.elementPath = elementPath;
        this.parentPath =
            elementPath.substring(0, elementPath.lastIndexOf('/'));
        this.hrefBaseUri = null;
    }

    /**
     * Constructs a {@link LinkStaxHandler} that checks the base uri of the
     * link.
     * 
     * @param elementPath
     *            The path to the link element that shall be handled by this
     *            handler.
     * @param hrefBaseUri
     *            The base uri of the href pointing to the object that is
     *            referenced by the link that shall be parsed by this handler.
     *            In case of REST, this value is used to check the provided
     *            href. In case of SOAP it is ignored.
     * @param exceptionClass
     *            The type of the exception to throw if href base uri is not
     *            matched in case of REST. This parameter must not be
     *            <code>null</code> and must be an instance of
     *            {@link EscidocException}, but this is not checked!.
     * @common
     */
    public LinkStaxHandler(final String elementPath, final String hrefBaseUri,
        final Class exceptionClass) {

        this(elementPath);
        this.hrefBaseUri = hrefBaseUri;
        this.exceptionClass = exceptionClass;
    }

    // CHECKSTYLE:JAVADOC-OFF

    /**
     * See Interface for functional description.<br>
     * If the current element is the link element, the xlink attributes and the
     * objid attribute is fetched and stored. After that,
     * <code>startLinkElement is called.</code>
     * 
     * @param element
     * @return
     * @throws EscidocException
     *             Thrown exceptions depend on sub class implementations.
     * @see de.escidoc.core.common.util.xml.stax.handler.DefaultHandler
     *      #startElement
     *      (de.escidoc.core.common.util.xml.stax.events.StartElement)
     * @common
     */
    @Override
    public StartElement startElement(final StartElement element)
        throws EscidocException {

        final String currentPath = element.getPath();
        if (isNotReady() && currentPath.equals(elementPath)) {
            href = null;
            objid = null;

            if (UserContext.isRestAccess()) {
                int index =
                    element.indexOfAttribute(Constants.XLINK_NS_URI, "href");
                if (index != -1) {
                    href = element.getAttribute(index).getValue();
                    objid = XmlUtility.getIdFromURI(href);

                    // check if href refers to the correct object type, if this
                    // has been specified
                    if (hrefBaseUri != null) {
                        final String expectedHref =
                            StringUtility.concatenateToString(hrefBaseUri,
                                objid);
                        if (!expectedHref.equals(href)) {
                            final String errorMsg =
                                StringUtility.concatenateWithBracketsToString(
                                    MSG_WRONG_BASE_URI, hrefBaseUri, href,
                                    element.getLocationString());
                            Constructor constructor;
                            try {
                                constructor =
                                    exceptionClass
                                        .getConstructor(new Class[] { String.class });
                                throw (EscidocException) constructor
                                    .newInstance(new Object[] { errorMsg });
                            }
                            catch (EscidocException e) {
                                throw e;
                            }
                            catch (Exception e) {
                                throw new SystemException(
                                    "Initializing exception failed.", e);
                            }
                        }
                    }
                }

                // the type is allways "simple" and discarded, the title is
                // discarded, too.
            }
            else {
                int index = element.indexOfAttribute(null, "objid");
                if (index != -1) {
                    objid = element.getAttribute(index).getValue();
                }
            }

            startLinkElement(element);
        }

        return element;
    }

    /**
     * See Interface for functional description.<br>
     * If the current element is the parent element of the link element, it is
     * checked if the link element, if it is defined as mandatoy, has been
     * provided in the XML data. After that, <code>endParentElement</code> and
     * <code>checkReady</code> are called.<br>
     * If the current element is the link element, <code>endLinkElement</code>
     * is called.
     * 
     * @param element
     * @return
     * @throws EscidocException
     *             Thrown exceptions depend on sub class implementation.
     * @see de.escidoc.core.common.util.xml.stax.handler.DefaultHandler
     *      #endElement(de.escidoc.core.common.util.xml.stax.events.EndElement)
     * @common
     */
    @Override
    public EndElement endElement(final EndElement element)
        throws EscidocException {

        if (isNotReady()) {
            if (element.getPath().equals(parentPath)) {
                endParentElement(element);
                checkReady(element);
                return element;
            }
            else if (element.getPath().equals(elementPath)) {
                endLinkElement(element);
            }
        }

        return element;
    }

    // CHECKSTYLE:JAVADOC-ON

    /**
     * Processes the end of the parent element.<br>
     * 
     * Sub classes may override this method, but they should call
     * <code>return super.endParentElement(element)</code> and keep in mind
     * that this could change the ready flag.
     * 
     * @param parentElement
     *            The parent element of the link element.
     * @throws EscidocException
     *             Thrown exceptions depend on sub class implementation.
     * @common
     */
    protected void endParentElement(final EndElement parentElement)
        throws EscidocException {

        return;
    }

    /**
     * Processes the start of a link element.<br>
     * This can be used by sub classes to perform additional operations on the
     * start element by overriding this default implementation.
     * 
     * @param linkElement
     *            The link element.
     * @throws EscidocException
     *             Thrown exceptions depend on sub class implementation.
     * @common
     */
    protected void startLinkElement(final StartElement linkElement)
        throws EscidocException {

        return;
    }

    /**
     * Processes the end of a link element.<br>
     * This can be used by sub classes to perform additional operations on the
     * end element by overriding this default implementation that just returns
     * the provided element.
     * 
     * @param linkElement
     *            The link element to process.
     * @return Returns the provided link element.
     * @throws EscidocException
     *             Thrown exceptions depend on sub class implementation.
     * @common
     */
    protected EndElement endLinkElement(final EndElement linkElement)
        throws EscidocException {

        return linkElement;
    }

    /**
     * Checks if this stax handler is ready. If this is true,
     * <code>setReady</code> is called to prevent further processing of this
     * handler. This method is called as the last operation of endElement after
     * processing the parent element.<br>
     * This default operation allways calls <code>setReady</code>. Sub
     * classes may override this to define the end of processing by their own.
     * 
     * @param endElement
     *            The currently processed <code>EndElement</code>.
     * @common
     */
    protected void checkReady(final EndElement endElement) {

        setReady();
    }

    /**
     * @return the href
     * @common
     */
    public String getHref() {
        return href;
    }

    /**
     * @return the objid
     * @common
     */
    public String getObjid() {
        return objid;
    }

}
