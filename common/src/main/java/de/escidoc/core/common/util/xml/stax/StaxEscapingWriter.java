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
package de.escidoc.core.common.util.xml.stax;

import de.escidoc.core.common.util.xml.XmlUtility;

import java.io.IOException;
import java.io.Writer;

/**
 * <code>Writer</code> implementation that is used to escape special XML
 * characters.<br/> For escaping, the method escapeForbiddenXmlCharacters of
 * {@link XmlUtility} is used.
 * 
 * @author TTE
 * 
 */
public class StaxEscapingWriter extends Writer {

    private final Writer writer;

    /**
     * Constructs a <code>StaxEscapingWriter</code>.
     * 
     * @param writer
     *            The <code>Writer</code> used for output.
     * @param attributeWriter
     *            A Flag indicating if this writer is used for writing
     *            attributes (<code>true</code>) or if it is used for
     *            writing text content (<code>false)</code>).
     */
    public StaxEscapingWriter(final Writer writer, final boolean attributeWriter) {

        this.writer = writer;
    }

    // CHECKSTYLE:JAVADOC-OFF

    /**
     * See Interface for functional description.
     * 
     * @throws IOException
     * @see java.io.Writer#close()
     */
    @Override
    public void close() throws IOException {

        writer.close();
    }

    /**
     * See Interface for functional description.
     * 
     * @throws IOException
     * @see java.io.Writer#flush()
     */
    @Override
    public void flush() throws IOException {

        writer.flush();
    }

    /**
     * See Interface for functional description.
     * 
     * @param cbuf
     * @param off
     * @param len
     * @throws IOException
     * @see java.io.Writer#write(char[], int, int)
     */
    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {

        writer.write(XmlUtility.escapeForbiddenXmlCharacters(new String(cbuf,
            off, len)));
    }

    // CHECKSTYLE:JAVADOC-ON

}
