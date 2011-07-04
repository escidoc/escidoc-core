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
package de.escidoc.core.aa.business.stax.handler;

import java.util.HashMap;
import java.util.Map;

import com.sun.xacml.EvaluationCtx;

import de.escidoc.core.common.exceptions.application.missing.MissingAttributeValueException;
import de.escidoc.core.common.util.xml.XmlUtility;
import de.escidoc.core.common.util.xml.stax.events.EndElement;
import de.escidoc.core.common.util.xml.stax.events.StartElement;
import de.escidoc.core.common.util.xml.stax.handler.DefaultHandler;

/**
 * Abstract stax handler that handles the attributes that have to be fetched from a resource Xml representation.<br>
 *
 * @author Torsten Tetteroo
 */
public class AbstractResourceAttributeStaxHandler extends DefaultHandler {

    private final EvaluationCtx ctx;

    private final String resourceId;

    private final String urnModifiedBy;

    private final String urnStatus;

    private final String urnVersionStatus;

    private boolean statusFound;

    private boolean inMetadata;

    /**
     * contains the by this class extracted Attributes that have value type String
     */
    private final Map<String, String> superAttributes = new HashMap<String, String>();

    /**
     * The constructor.
     *
     * @param ctx              The {@code EvaluationCtx} for that the item xml representation shall be parsed.
     * @param resourceId       The id of the item resource
     * @param urnModifiedBy    The urn of the modified-by attribute.
     * @param urnStatus        The urn of the status attribute.
     * @param urnVersionStatus The urn of the version-status attribute.
     */
    public AbstractResourceAttributeStaxHandler(final EvaluationCtx ctx, final String resourceId,
        final String urnModifiedBy, final String urnStatus, final String urnVersionStatus) {

        this.ctx = ctx;
        this.resourceId = resourceId;
        this.urnModifiedBy = urnModifiedBy;
        this.urnStatus = urnStatus;
        this.urnVersionStatus = urnVersionStatus;
    }

    /**
     * See Interface for functional description.
     *
     * @see DefaultHandler #startElement (de.escidoc.core.common.util.xml.stax.events.StartElement)
     */
    @Override
    public StartElement startElement(final StartElement element) throws MissingAttributeValueException {

        //Only parse elements that are not in metadata-section
        if (XmlUtility.NAME_MDRECORDS.equals(element.getLocalName())) {
            setInMetadata(true);
        }

        if (isNotReady() && !isInMetadata()) {
            final String localName = element.getLocalName();
            if (XmlUtility.NAME_MODIFIED_BY.equals(localName)) {
                superAttributes.put(this.urnModifiedBy, XmlUtility.getIdFromStartElement(element));
            }
        }

        return element;
    }

    /**
     * This method handles an end element.<br> Check if this element is the mdrecords end element.
     *
     * @param element The {@link StartElement} to handle.
     * @return Returns an end element that shall be handled by further handlers in the chain, or {@code null} to
     *         stop the chain.
     * @throws Exception Thrown if anything fails. This depends on the implementation of the concrete class.
     */
    @Override
    public EndElement endElement(final EndElement element) throws Exception {

        //Only parse elements that are not in metadata-section
        if (XmlUtility.NAME_MDRECORDS.equals(element.getLocalName())) {
            setInMetadata(false);
        }

        return element;
    }

    /**
     * See Interface for functional description.
     *
     * @see DefaultHandler #characters(java.lang.String, de.escidoc.core.common.util.xml.stax.events.StartElement)
     */
    @Override
    public String characters(final String data, final StartElement element) throws Exception {

        if (isNotReady() && !isInMetadata()) {
            final String localName = element.getLocalName();
            if (!this.statusFound && XmlUtility.NAME_PUBLIC_STATUS.equals(localName)) {
                superAttributes.put(this.urnStatus, data);
                // in an item representation, status is contained in item
                // properties and in the component properties. We have to mark
                // the found item status to prevent overriding with component
                // status.
                this.statusFound = true;
            }
            else if (XmlUtility.NAME_STATUS.equals(localName)) {
                superAttributes.put(this.urnVersionStatus, data);
            }
        }

        return data;
    }

    /**
     * @return the inMetadata
     */
    protected boolean isInMetadata() {
        return this.inMetadata;
    }

    /**
     * @param inMetadata the inMetadata to set
     */
    protected void setInMetadata(final boolean inMetadata) {
        this.inMetadata = inMetadata;
    }

    /**
     * @return the superAttributes
     */
    public Map<String, String> getSuperAttributes() {
        return superAttributes;
    }

    /**
     * @return the ctx
     */
    public EvaluationCtx getCtx() {
        return ctx;
    }

    /**
     * @return the resourceId
     */
    public String getResourceId() {
        return resourceId;
    }

}
