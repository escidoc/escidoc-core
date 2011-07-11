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

import de.escidoc.core.aa.business.persistence.EscidocPolicy;
import de.escidoc.core.aa.business.persistence.EscidocRole;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.util.IOUtils;
import de.escidoc.core.common.util.xml.XmlUtility;
import de.escidoc.core.common.util.xml.stax.events.Attribute;
import de.escidoc.core.common.util.xml.stax.events.EndElement;
import de.escidoc.core.common.util.xml.stax.events.StartElement;
import de.escidoc.core.common.util.xml.stax.handler.DefaultHandler;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;

/**
 * StaxHandler that manages the Xacml policy (set) of a role.<br> This handler extracts the policy (set) from the xml
 * data, creates an {@code EscidocPolicy} object from this data and sets it in the {@code EscidocRole}object.
 *
 * @author Torsten Tetteroo
 */
public class XacmlStaxHandler extends DefaultHandler {

    private String policyXml;

    private final EscidocRole role;

    private boolean insidePolicy;

    private int policyLevel;

    private XMLStreamWriter policyWriter;

    private StringWriter writer;

    /**
     * The constructor.
     *
     * @param role The role to handle
     */
    public XacmlStaxHandler(final EscidocRole role) {

        this.role = role;
    }

    /**
     * See Interface for functional description.<br> If the current element is the link element, the xlink attributes
     * and the objid attribute is fetched and stored. After that, {@code startLinkElement is called.}
     *
     * @see DefaultHandler #startElement (de.escidoc.core.common.util.xml.stax.events.StartElement)
     */
    @Override
    public StartElement startElement(final StartElement element) throws SystemException {

        if (isNotReady()) {
            final String elementName = element.getLocalName();
            if (!this.insidePolicy && elementName.startsWith("Policy")) {

                this.insidePolicy = true;
                this.writer = new StringWriter();
                try {
                    this.policyWriter = XmlUtility.createXmlStreamWriter(this.writer);
                }
                catch (final XMLStreamException e) {
                    throw new SystemException("Writer creation failed.", e);
                }
            }

            if (this.insidePolicy) {

                if (elementName.startsWith("Policy")) {
                    this.policyLevel++;
                }

                try {
                    policyWriter.writeStartElement(elementName);
                    for (int i = 0; i < element.getAttributeCount(); i++) {
                        final Attribute attribute = element.getAttribute(i);
                        policyWriter.writeAttribute(attribute.getLocalName(), attribute.getValue());
                    }
                }
                catch (final Exception e) {
                    throw new SystemException("Extracting policy failed.", e);
                }
            }
        }

        return element;
    }

    /**
     * See Interface for functional description.
     */
    @Override
    public String characters(final String data, final StartElement element) throws XMLStreamException {

        if (isNotReady() && this.insidePolicy) {
            policyWriter.writeCharacters(data);
        }
        return data;
    }

    /**
     * See Interface for functional description.
     *
     * @see DefaultHandler #endElement(de.escidoc.core.common.util.xml.stax.events.EndElement)
     */
    @Override
    public EndElement endElement(final EndElement element) throws XMLStreamException {

        if (isNotReady() && this.insidePolicy) {

            policyWriter.writeEndElement();

            final String elementName = element.getLocalName();
            if (elementName.startsWith("Policy")) {
                this.policyLevel--;
                if (this.policyLevel == 0) {
                    this.insidePolicy = false;
                    policyWriter.close();
                    IOUtils.closeWriter(this.writer);
                    this.policyXml = writer.toString();

                    Collection<EscidocPolicy> escidocPolicies = role.getEscidocPolicies();
                    if (escidocPolicies == null) {
                        escidocPolicies = new ArrayList<EscidocPolicy>(1);
                        role.setEscidocPolicies(escidocPolicies);
                    }

                    if (escidocPolicies.isEmpty()) {
                        final EscidocPolicy policy = new EscidocPolicy(this.policyXml, this.role);
                        escidocPolicies.add(policy);
                    }
                    else {
                        final EscidocPolicy policy = escidocPolicies.iterator().next();
                        policy.setXml(this.policyXml);
                    }

                    setReady();
                }
            }
        }
        return element;
    }

    /**
     * Gets the xml representation of the XACML policy (set).
     *
     * @return Returns the xml representation of the XACML policy (set) or {@code null} if no policy has been
     *         found.
     */
    public String getPolicyXml() {
        return this.policyXml;
    }
}
