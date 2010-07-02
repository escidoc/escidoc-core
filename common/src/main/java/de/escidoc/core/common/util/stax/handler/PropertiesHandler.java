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
package de.escidoc.core.common.util.stax.handler;

import java.util.HashMap;

import de.escidoc.core.common.business.Constants;
import de.escidoc.core.common.business.fedora.TripleStoreUtility;
import de.escidoc.core.common.exceptions.application.missing.MissingAttributeValueException;
import de.escidoc.core.common.exceptions.application.notfound.ContentModelNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ContextNotFoundException;
import de.escidoc.core.common.exceptions.application.violated.ReadonlyAttributeViolationException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.util.logger.AppLogger;
import de.escidoc.core.common.util.stax.StaxParser;
import de.escidoc.core.common.util.xml.XmlUtility;
import de.escidoc.core.common.util.xml.stax.events.Attribute;
import de.escidoc.core.common.util.xml.stax.events.EndElement;
import de.escidoc.core.common.util.xml.stax.events.StartElement;
import de.escidoc.core.common.util.xml.stax.handler.DefaultHandler;

/**
 * The PropertiesHandler.
 * 
 * @author MSC
 * 
 * @om
 */
public class PropertiesHandler extends DefaultHandler {

    public static final int CREATE_ACTION = 0;

    public static final int UPDATE_ACTION = 1;

    public static final String PROPERTIES_PATH = "/container/properties";

    public static final String ADMIN_DESCRIPTOR = "admin-descriptor";

    public static final String CONTEXT = "context";

    public static final String CONTENT_MODEL = "content-model";

    /*
     * @deprecated
     */
    public static final String CONTENT_TYPE = CONTENT_MODEL;

    public static final String CREATOR = "creator";

    public static final String CURRENT_VERSION = "current-version";

    public static final String LATEST_REVISION = "latest-revision";

    public static final String LATEST_VERSION = "latest-version";

    public static final String LOCK_STATUS = "lock-status";

    public static final String LOCK_OWNER = "lock-owner";

    public static final String DATE = "date";

    public static final String NUMBER = "number";

    public static final String PID = "pid";

    public static final String PUBLIC_STATUS = "public-status";

    /**
     * @deprecated
     */
    public static final String STATUS = PUBLIC_STATUS; // "status";

    public static final String TITLE = "title";

    public static final String VALID_STATUS = "valid-status";

    public static final String HREF = "href";

    public static final String INITIAL_VERSION = "1";

    public static final String INITIAL_REVISION = "";

    private StaxParser staxParser = null;

    private String id = null;

    private String creator = null;

    private int action = -1;

    private boolean parsing = false;

    private HashMap<String, String> properties = new HashMap<String, String>();

    private static AppLogger logger =
        new AppLogger(PropertiesHandler.class.getName());

    /**
     * Instantiate a PropertiesHandler.
     * 
     * @param id
     *            The id of the parsed object.
     * @param creator
     *            The creator of the parsed object.
     * @param action
     *            The action performed on the parsed object (create or update).
     * @param parser
     *            The parser.
     * 
     * @om
     */
    public PropertiesHandler(final String id, final String creator,
        final int action, final StaxParser parser) {
        this.id = id;
        this.creator = creator;
        this.action = action;
        this.staxParser = parser;

        if (action == CREATE_ACTION) {
            properties.put(CURRENT_VERSION + "." + NUMBER, INITIAL_VERSION);
            properties.put(CURRENT_VERSION + "." + PUBLIC_STATUS,
                Constants.STATUS_PENDING);
            properties.put(CURRENT_VERSION + "." + VALID_STATUS,
                Constants.STATUS_INVALID);

            properties.put(LATEST_VERSION + "." + NUMBER, INITIAL_VERSION);

            properties.put(LATEST_REVISION + "." + NUMBER, INITIAL_REVISION);
            properties.put(LATEST_REVISION + "." + PID, INITIAL_REVISION);
            properties.put(LATEST_REVISION + "." + PID + "." + TITLE,
                INITIAL_REVISION);
            properties.put(PUBLIC_STATUS, Constants.STATUS_PENDING);
            properties.put(LOCK_STATUS, Constants.STATUS_UNLOCKED);
        }
    }

    /**
     * Get the properties of the handler.
     * 
     * @return The properties.
     */
    public HashMap<String, String> getProperties() {
        return this.properties;
    }

    /**
     * Handle the start of an element.
     * 
     * @param element
     *            The element.
     * @return The element.
     * 
     * @throws ContentModelNotFoundException
     *             If the content-type is not available.
     * @throws ContextNotFoundException
     *             If the context is not available.
     * @throws MissingAttributeValueException
     *             If a required attribute is missing.
     * @throws SystemException
     *             Thrown in case of an internal error.
     * @see de.escidoc.core.om.business.stax.handler.DefaultHandler#startElement
     *      (de.escidoc.core.om.business.stax.events.StartElement)
     * 
     * @om
     */
    public StartElement startElement(final StartElement element)
        throws ContentModelNotFoundException, ContextNotFoundException,
        MissingAttributeValueException, ReadonlyAttributeViolationException,
        SystemException {

        if (getLogger().isInfoEnabled()) {
            getLogger().info(
                "[Container] PropertiesHandler.startElement("
                    + element.getLocalName() + ")");
        }

        String current = element.getLocalName();
        if (parsing) {
            if ((current.equals(CONTENT_MODEL)) || (current.equals(CONTEXT))) {
                Attribute href =
                    element.getAttribute(element.indexOfAttribute(
                        Constants.XLINK_URI, HREF));
                Attribute title =
                    element.getAttribute(element.indexOfAttribute(
                        Constants.XLINK_URI, TITLE));
                if ("".equals(href.getValue())) {
                    String message =
                        "The value of" + " href attribute of the element "
                            + element.getLocalName() + " is missing.";
                    getLogger().error(message);
                    throw new MissingAttributeValueException(message);
                }
                String id = XmlUtility.getIdFromURI(href.getValue());
                if (!TripleStoreUtility.getInstance().exists(id)
                    || (id == null)) {
                    if (current.equals("context")) {
                        getLogger().error(
                            "context with id " + id + " does not exist");

                        throw new ContextNotFoundException("context with id "
                            + id + " does not exist");
                    }
                    if (current.equals("content-model")) {
                        getLogger().error(
                            "context with id " + id + " does not exist");

                        throw new ContentModelNotFoundException(
                            "content type with id " + id + " does not exist");
                    }
                }
                properties.put(element.getLocalName(), id);
                properties.put(element.getLocalName() + "." + TITLE, title
                    .getValue());
            }
            else if (current.equals(ADMIN_DESCRIPTOR)) {
                Attribute href =
                    element.getAttribute(element.indexOfAttribute(
                        Constants.XLINK_URI, HREF));
                Attribute title =
                    element.getAttribute(element.indexOfAttribute(
                        Constants.XLINK_URI, TITLE));
                if (!"".equals(href.getValue())) {
                    String message =
                        "The value of"
                            + " the read only attribute href of the element "
                            + " admin-descriptor may not be set.";
                    getLogger().error(message);
                    throw new ReadonlyAttributeViolationException(message);
                }
                properties.put(element.getLocalName() + "." + TITLE, title
                    .getValue());
            }
            // else if (action == UPDATE_ACTION) {
            // }

        }
        else if (PROPERTIES_PATH.equals(staxParser.getCurPath())) {
            parsing = true;
        }

        return element;
    }

    /**
     * Handle the end of an element.
     * 
     * @param element
     *            The element.
     * @return The element.
     * @see de.escidoc.core.common.util.xml.stax.handler.DefaultHandler#endElement
     *      (de.escidoc.core.common.util.xml.stax.events.EndElement)
     * @om
     */
    public EndElement endElement(final EndElement element) {
        getLogger().info(
            "[Container] PropertiesHandler.endElement("
                + element.getLocalName() + ")");
        if (!staxParser.getCurPath().startsWith(PROPERTIES_PATH)) {
            parsing = false;
        }
        return element;
    }

    /**
     * @return Returns the logger.
     */
    public static AppLogger getLogger() {
        return logger;
    }

}
