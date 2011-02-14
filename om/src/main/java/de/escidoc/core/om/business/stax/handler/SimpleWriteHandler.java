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
package de.escidoc.core.om.business.stax.handler;

import de.escidoc.core.common.util.stax.handler.WriteHandler;
import de.escidoc.core.common.util.xml.XmlUtility;
import de.escidoc.core.common.util.xml.stax.events.Attribute;
import de.escidoc.core.common.util.xml.stax.events.EndElement;
import de.escidoc.core.common.util.xml.stax.events.StartElement;

import javax.xml.stream.XMLStreamException;
import java.io.ByteArrayOutputStream;

/**
 * SimpleWriteHandler is designed to write every XML element (StartElement,
 * EndElement, characters) to a OutputStream.
 * 
 * @author SWA
 * 
 */
public class SimpleWriteHandler extends WriteHandler {

    private ByteArrayOutputStream out = null;

    /**
     * SimpleWriteHandler.
     * 
     * @throws XMLStreamException
     *             Thrown if creating stream writer failed.
     */
    public SimpleWriteHandler() throws XMLStreamException {
        this.out = new ByteArrayOutputStream();
        this.setWriter(XmlUtility.createXmlStreamWriter(out));
    }

    /**
     * SimpleWriteHandler.
     * 
     * @param out
     *            ByteArrayOutputStream
     * @throws XMLStreamException
     *             Thrown if creating stream writer failed.
     */
    public SimpleWriteHandler(final ByteArrayOutputStream out)
        throws XMLStreamException {
        this.out = out;
        this.setWriter(XmlUtility.createXmlStreamWriter(out));
    }

    /**
     * @param element
     *            StAX StartElement
     * @return StAX StartElement
     * @throws XMLStreamException
     *             Thrown if writing to stream writer failed.
     */
    @Override
    public StartElement startElement(final StartElement element)
        throws XMLStreamException {

        writeElement(element);
        int c = element.getAttributeCount();
        for (int i = 0; i < c; i++) {
            Attribute att = element.getAttribute(i);
            writeAttribute(att.getNamespace(), element.getLocalName(), att
                .getLocalName(), att.getValue(), att.getPrefix(), element
                .getNamespaceContext());
        }

        return element;
    }

    /**
     * 
     * @param s
     *            StAX Character String Content
     * @param element
     *            StartElement StAX StartElement
     * @return StAX Character
     * @throws XMLStreamException
     *             Thrown if writing to stream writer failed.
     */
    @Override
    public String characters(final String s, final StartElement element)
        throws XMLStreamException {
        this.getWriter().writeCharacters(s);
        return s;
    }

    /**
     * @param element
     *            StAX EndElement
     * @return StAX EndElement
     * @throws XMLStreamException
     *             Thrown if writing to stream writer failed.
     */
    @Override
    public EndElement endElement(final EndElement element) throws XMLStreamException {
        this.getWriter().writeEndElement();
        return element;
    }

    /**
     * Get OutputStream.
     * 
     * @return the OutputStream with the whole content of the XML element.
     * @throws XMLStreamException
     *             Thrown if writing to stream writer failed.
     */
    public ByteArrayOutputStream getOutputStream() throws XMLStreamException {
        this.getWriter().flush();
        this.getWriter().close();
        return out;
    }

}
