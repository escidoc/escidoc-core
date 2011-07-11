/*
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the Common Development and Distribution License, Version 1.0
 * only (the "License"). You may not use this file except in compliance with the License.
 *
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE or http://www.escidoc.de/license. See the License for
 * the specific language governing permissions and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each file and include the License file at
 * license/ESCIDOC.LICENSE. If applicable, add the following below this CDDL HEADER, with the fields enclosed by
 * brackets "[]" replaced with your own identifying information: Portions Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
 *
 * Copyright 2006-2011 Fachinformationszentrum Karlsruhe Gesellschaft fuer wissenschaftlich-technische Information mbH
 * and Max-Planck-Gesellschaft zur Foerderung der Wissenschaft e.V. All rights reserved. Use is subject to license
 * terms.
 */

package de.escidoc.core.common.util.xml.stax.handler;

import de.escidoc.core.common.business.Constants;
import de.escidoc.core.common.exceptions.EscidocException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.util.string.StringUtility;
import de.escidoc.core.common.util.xml.XmlUtility;
import de.escidoc.core.common.util.xml.stax.events.EndElement;
import de.escidoc.core.common.util.xml.stax.events.StartElement;

import java.lang.reflect.Constructor;

/**
 * Stax handler that manages a link element that refers to another eSciDoc resource object.<br> In case of a REST
 * access, a link consists of the xlink attributes referencing the resource object. The xlink:type attribute is
 * "simple". In common, the xlink:href consists of a base uri (e.g. /ir/item/) followed by an id (e.g. escidoc:12345).
 * The expected base uri can be specified during creation of the {@link LinkStaxHandler} instance. This value will be
 * used to validate the provided href.
 *
 * @author Torsten Tetteroo
 */
public class LinkStaxHandler extends DefaultHandler {

    /**
     * Error message in case of base uri mismatch of href provided in REST request.
     */
    private static final String MSG_WRONG_BASE_URI =
        "Reference does not point to a resource of the expected type," + " base uri mismatch";

    private String href;

    private String objid;

    private final String elementPath;

    private final String parentPath;

    private final String hrefBaseUri;

    private Class exceptionClass;

    /**
     * The constructor.
     *
     * @param elementPath The path to the link element that shall be handled by this handler.
     */
    public LinkStaxHandler(final String elementPath) {

        this.elementPath = elementPath;
        this.parentPath = elementPath.substring(0, elementPath.lastIndexOf('/'));
        this.hrefBaseUri = null;
    }

    /**
     * Constructs a LinkStaxHandler that checks the base uri of the link.
     *
     * @param elementPath    The path to the link element that shall be handled by this handler.
     * @param hrefBaseUri    The base uri of the href pointing to the object that is referenced by the link that shall
     *                       be parsed by this handler.
     * @param exceptionClass The type of the exception to throw if href base uri is not matched. This parameter must not
     *                       be {@code null} and must be an instance of {@link EscidocException}, but this is not
     *                       checked!.
     */
    public LinkStaxHandler(final String elementPath, final String hrefBaseUri, final Class exceptionClass) {

        this.elementPath = elementPath;
        this.parentPath = elementPath.substring(0, elementPath.lastIndexOf('/'));
        this.hrefBaseUri = hrefBaseUri;
        this.exceptionClass = exceptionClass;
    }

    /**
     * See Interface for functional description.<br> If the current element is the link element, the xlink attributes
     * and the objid attribute is fetched and stored. After that, {@code startLinkElement is called.}
     *
     * @throws EscidocException Thrown exceptions depend on sub class implementations.
     * @see DefaultHandler #startElement (de.escidoc.core.common.util.xml.stax.events.StartElement)
     */
    @Override
    public StartElement startElement(final StartElement element) throws EscidocException {
        final String currentPath = element.getPath();
        if (isNotReady() && currentPath.equals(this.elementPath)) {
            this.href = null;
            this.objid = null;
            final int index = element.indexOfAttribute(Constants.XLINK_NS_URI, "href");
            if (index != -1) {
                this.href = element.getAttribute(index).getValue();
                this.objid = XmlUtility.getIdFromURI(this.href);
                // check if href refers to the correct object type, if this has been specified
                if (this.hrefBaseUri != null) {
                    final String expectedHref = this.hrefBaseUri + this.objid;
                    if (!expectedHref.equals(this.href)) {
                        final String errorMsg =
                            StringUtility.format(MSG_WRONG_BASE_URI, this.hrefBaseUri, this.href, element
                                .getLocationString());
                        try {
                            final Constructor constructor = exceptionClass.getConstructor(new Class[] { String.class });
                            throw (EscidocException) constructor.newInstance(errorMsg);
                        }
                        catch (final EscidocException e) {
                            throw e;
                        }
                        catch (final Exception e) {
                            throw new SystemException("Initializing exception failed.", e);
                        }
                    }
                }
            }
            startLinkElement(element);
        }
        return element;
    }

    /**
     * See Interface for functional description.<br> If the current element is the parent element of the link element,
     * it is checked if the link element, if it is defined as mandatoy, has been provided in the XML data. After that,
     * {@code endParentElement} and {@code checkReady} are called.<br> If the current element is the link
     * element, {@code endLinkElement} is called.
     *
     * @throws EscidocException Thrown exceptions depend on sub class implementation.
     * @see DefaultHandler #endElement(de.escidoc.core.common.util.xml.stax.events.EndElement)
     */
    @Override
    public EndElement endElement(final EndElement element) throws EscidocException {

        if (isNotReady()) {
            if (element.getPath().equals(this.parentPath)) {
                endParentElement(element);
                checkReady(element);
                return element;
            }
            else if (element.getPath().equals(this.elementPath)) {
                endLinkElement(element);
            }
        }

        return element;
    }

    /**
     * Processes the end of the parent element.<br>
     * <p/>
     * Sub classes may override this method, but they should call {@code return super.endParentElement(element)}
     * and keep in mind that this could change the ready flag.
     *
     * @param parentElement The parent element of the link element.
     * @throws EscidocException Thrown exceptions depend on sub class implementation.
     */
    protected void endParentElement(final EndElement parentElement) {
    }

    /**
     * Processes the start of a link element.<br> This can be used by sub classes to perform additional operations on
     * the start element by overriding this default implementation.
     *
     * @param linkElement The link element.
     * @throws EscidocException Thrown exceptions depend on sub class implementation.
     */
    protected void startLinkElement(final StartElement linkElement) {
    }

    /**
     * Processes the end of a link element.<br> This can be used by sub classes to perform additional operations on the
     * end element by overriding this default implementation that just returns the provided element.
     *
     * @param linkElement The link element to process.
     * @return Returns the provided link element.
     * @throws EscidocException Thrown exceptions depend on sub class implementation.
     */
    protected EndElement endLinkElement(final EndElement linkElement) throws EscidocException {

        return linkElement;
    }

    /**
     * Checks if this stax handler is ready. If this is true, {@code setReady} is called to prevent further
     * processing of this handler. This method is called as the last operation of endElement after processing the parent
     * element.<br> This default operation allways calls {@code setReady}. Sub classes may override this to define
     * the end of processing by their own.
     *
     * @param endElement The currently processed {@code EndElement}.
     */
    protected void checkReady(final EndElement endElement) {

        setReady();
    }

    /**
     * @return the href
     */
    public String getHref() {
        return this.href;
    }

    /**
     * @return the objid
     */
    public String getObjid() {
        return this.objid;
    }

}
