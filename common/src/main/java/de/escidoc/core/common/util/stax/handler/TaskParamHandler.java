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

import de.escidoc.core.common.exceptions.application.invalid.InvalidXmlException;
import de.escidoc.core.common.exceptions.application.invalid.XmlCorruptedException;
import de.escidoc.core.common.util.stax.StaxParser;
import de.escidoc.core.common.util.xml.Elements;
import de.escidoc.core.common.util.xml.stax.events.Attribute;
import de.escidoc.core.common.util.xml.stax.events.EndElement;
import de.escidoc.core.common.util.xml.stax.events.StartElement;
import de.escidoc.core.common.util.xml.stax.handler.DefaultHandler;
import org.joda.time.DateTime;

import javax.naming.directory.NoSuchAttributeException;
import java.util.LinkedList;
import java.util.List;

/**
 * Handle the parameters for a task oriented method.
 * 
 * @author MSC
 * 
 */
public class TaskParamHandler extends DefaultHandler {

    private final StaxParser parser;

    private String lastModificationDate = null;

    private String withdrawComment = null;

    private String revokationRemark = null;

    private String comment = null;

    private String password = null;

    private String objectType = null;

    private String format = null;

    private List<String> ids = new LinkedList<String>();

    private String pid = null;

    private boolean keepInSync = true;

    private static final String PARAM_PATH = "/param";

    private static final String PARAM_ID_PATH = PARAM_PATH + "/id";

    private static final String PARAM_PASSWORD_PATH = PARAM_PATH + "/password";

    private static final String PARAM_OBJECTTYPE_PATH = 
                                    PARAM_PATH + "/object-type";

    private static final String PARAM_PID_PATH = PARAM_PATH + "/pid";

    private static final String PARAM_SYNC_PATH = PARAM_PATH + "/sync";

    private static final String LAST_MODIFICATION_DATE_ATT =
        "last-modification-date";

    // private static final String WITHDRAW_COMMENT_ATT = "withdraw-comment";

    private boolean checkLastModificationDate = true;

    /**
     * Instantiate a TaskParamHandler.
     * 
     * @param parser
     *            The parser.
     */
    public TaskParamHandler(final StaxParser parser) {

        this.parser = parser;
    }

    /**
     * Handle the start of an element.
     * 
     * @param element
     *            The element.
     * @return The element.
     * @throws InvalidXmlException
     *             Thrown if the XML has an invalid structure.
     * @om
     */
    @Override
    public StartElement startElement(final StartElement element)
        throws InvalidXmlException {

        String currentPath = parser.getCurPath();

        if (PARAM_PATH.equals(currentPath)) {
            Attribute date = null;
            try {
                date = element.getAttribute(null, LAST_MODIFICATION_DATE_ATT);
                lastModificationDate = date.getValue();
            }
            catch (NoSuchAttributeException e1) {
                // date is still 'null'
            }
            // If we would have a schema for taskParam, then is the
            // last-modifiaction-date timestamp already checked by schema
            // validation.
            try {
                if (checkLastModificationDate) {
                    if (lastModificationDate == null) {
                        throw new XmlCorruptedException("");
                    }
                    new DateTime(lastModificationDate);
                }
            }
            catch (Exception e) {
                throw new XmlCorruptedException(
                    "Task param: last-modification-date '"
                        + lastModificationDate + "' is no valid timestamp!", e);
            }
        } else if (!currentPath.startsWith(PARAM_PATH)) {
            throw new XmlCorruptedException("Task param has wrong root element '"
                + currentPath + "'!");
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
    @Override
    public EndElement endElement(final EndElement element) {

        return element;
    }

    /**
     * See Interface for functional description.
     * 
     * @param data
     *            The data.
     * @param element
     *            The element.
     * @return The character set of the element.
     * @see de.escidoc.core.common.util.xml.stax.handler.DefaultHandler#characters(java.lang.String,
     *      de.escidoc.core.common.util.xml.stax.events.StartElement)
     */
    @Override
    public String characters(final String data, final StartElement element) {
        String curPath = parser.getCurPath();

        if (curPath.equals(PARAM_PATH + "/"
            + Elements.ELEMENT_PARAM_WITHDRAW_COMMENT)) {
            this.withdrawComment = data;
        }
        else if (curPath.equals(PARAM_PATH + "/"
            + Elements.ELEMENT_PARAM_REVOKATION_REMARK)) {
            this.revokationRemark = data;
        }
        else if (curPath.equals(PARAM_PATH + "/"
            + Elements.ELEMENT_PARAM_COMMENT)) {
            this.comment = data;
        }
        else if (curPath.equals(PARAM_PATH + "/"
            + Elements.ELEMENT_PARAM_FORMAT)) {
            this.format = data;
        }
        else if (curPath.equals(PARAM_PASSWORD_PATH)) {
            this.password = data;
        }
        else if (curPath.equals(PARAM_OBJECTTYPE_PATH)) {
            this.objectType = data;
        }
        else if (curPath.equals(PARAM_PID_PATH)) {
            this.pid = data;
        }
        else if (curPath.equals(PARAM_ID_PATH)) {
            this.ids.add(data);
        }
        else if (curPath.equals(PARAM_SYNC_PATH)) {
            this.keepInSync = Boolean.valueOf(data);
        }
        return data;
    }

    /**
     * 
     * @return The keep in sync value used for AdminHandler.deleteObjects()
     */
    public boolean getKeepInSync() {
        return keepInSync;
    }

    /**
     * 
     * @return The latest modification date.
     */
    public String getLastModificationDate() {
        return lastModificationDate;
    }

    /**
     * Get the withdraw comment.
     * 
     * @return withdraw comment
     */
    public String getWithdrawComment() {
        return withdrawComment;
    }

    /**
     * Get the comment.
     * 
     * @return comment.
     */
    public String getComment() {
        return comment;
    }

    /**
     * @return the format
     */
    public String getFormat() {
        return format;
    }

    /**
     * 
     * @return the id list
     */
    public List<String> getIds() {
        return ids;
    }

    /**
     * Set the last-modification-date.
     * 
     * @param checkLastModificationDate
     *            the checkLastModificationDate to set
     */
    public void setCheckLastModificationDate(
        final boolean checkLastModificationDate) {
        this.checkLastModificationDate = checkLastModificationDate;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @return the objectType
     */
    public String getObjectType() {
        return objectType;
    }

    /**
     * Get the Pid.
     * 
     * @return Value of pid element or null if not provided.
     */
    public String getPid() {
        return this.pid;
    }

    /**
     * @return the revokationRemark
     */
    public String getRevokationRemark() {
        return revokationRemark;
    }
}
